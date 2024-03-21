package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import Logic.PhysicsEngine.TrajectoryPoint;

public class DrawingPanel extends JPanel {
    private List<TrajectoryPoint> trajectoryPoints;
    private double scaleFactor = 15.0;
    private String hoverText = "";
    private Point dragStartPoint = null;
    private final Point2D.Double viewOffset = new Point2D.Double();
    private Integer selectedPointIndex = null;

    public DrawingPanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buttonIncreaseScale = new JButton("+");
        Border matteBorder = new EmptyBorder(10, 20, 10, 20);
        buttonIncreaseScale.setBackground(new Color(156, 198, 240));
        buttonIncreaseScale.setOpaque(true);
        buttonIncreaseScale.setBorderPainted(false);
        buttonIncreaseScale.setBorder(matteBorder);
        JButton buttonDecreaseScale = new JButton("-");
        buttonDecreaseScale.setBackground(new Color(156, 198, 240));
        buttonDecreaseScale.setOpaque(true);
        buttonDecreaseScale.setBorderPainted(false);
        buttonDecreaseScale.setBorder(matteBorder);
        buttonIncreaseScale.addActionListener(e -> setScaleFactor(scaleFactor + 1));
        buttonDecreaseScale.addActionListener(e -> setScaleFactor(Math.max(1, (scaleFactor >= 4) ? scaleFactor - 1 : scaleFactor)));
        buttonPanel.add(buttonDecreaseScale);
        buttonPanel.add(buttonIncreaseScale);
        this.add(buttonPanel, BorderLayout.NORTH);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (selectedPointIndex == null) {
                    updateHoverText(e.getX(), e.getY());
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartPoint != null) {
                    int dx = e.getX() - dragStartPoint.x;
                    int dy = e.getY() - dragStartPoint.y;
                    viewOffset.x += dx;
                    double potentialYOffset = viewOffset.y - dy;
                    if (getHeight() / 2 + potentialYOffset <= getHeight() / 2) {
                        viewOffset.y -= dy;
                    }
                    dragStartPoint = e.getPoint();
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStartPoint = e.getPoint();
                if (e.getClickCount() == 1) {
                    selectPoint(e.getX(), e.getY());
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStartPoint = null;
            }
        });

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                setScaleFactor(scaleFactor + 1);
            } else {
                setScaleFactor(Math.max(1, (scaleFactor >= 4) ? scaleFactor - 1 : scaleFactor));
            }
        });
    }

    private void selectPoint(int mouseX, int mouseY) {
        if (Math.abs(mouseX - dragStartPoint.x) > 5 || Math.abs(mouseY - dragStartPoint.y) > 5) {
            return;
        }

        boolean pointFound = false;
        for (int i = 0; i < trajectoryPoints.size(); i++) {
            if (isPointUnderMouse(trajectoryPoints.get(i), mouseX, mouseY)) {
                selectedPointIndex = (selectedPointIndex != null && selectedPointIndex == i) ? null : i;
                pointFound = true;
                updateHoverTextWithPoint(trajectoryPoints.get(i));
                break;
            }
        }
        if (!pointFound) {
            selectedPointIndex = null;
        }
    }

    private boolean isPointUnderMouse(TrajectoryPoint point, int mouseX, int mouseY) {
        int centerX = (int) (getWidth() / 2 + viewOffset.x);
        int centerY = (int) (getHeight() / 2 - viewOffset.y);
        int x = (int) ((point.x * scaleFactor) + centerX);
        int y = (int) (centerY - (point.y * scaleFactor));
        return mouseX >= x - 3 && mouseX <= x + 3 && mouseY >= y - 3 && mouseY <= y + 3;
    }

    private void updateHoverTextWithPoint(TrajectoryPoint point) {
        hoverText = String.format("X: %.2f, Y: %.2f, VelX: %.2f m/s, VelY: %.2f m/s, Time: %.2f s",
                point.x, point.y, point.velocityX, point.velocityY, point.time);
    }

    private void updateHoverText(int mouseX, int mouseY) {
        hoverText = "";
        int centerX = (int) (getWidth() / 2 + viewOffset.x);
        int centerY = (int) (getHeight() / 2 - viewOffset.y);
        for (TrajectoryPoint point : trajectoryPoints) {
            int x = (int) ((point.x * scaleFactor) + centerX);
            int y = (int) (centerY - (point.y * scaleFactor));
            if (mouseX >= x - 3 && mouseX <= x + 3 && mouseY >= y - 3 && mouseY <= y + 3) {
                hoverText = String.format("X: %.2f, Y: %.2f, VelX: %.2f m/s, VelY: %.2f m/s, Time: %.2f s",
                        point.x, point.y, point.velocityX, point.velocityY, point.time);
                break;
            }
        }
    }

    public void setTrajectoryPoints(List<TrajectoryPoint> trajectoryPoints) {
        this.trajectoryPoints = trajectoryPoints;
        repaint();
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int yZeroCoordinate = (int) (height / 2 - viewOffset.y);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.lightGray);
        g.fillRect(0, yZeroCoordinate, width, height - yZeroCoordinate);
        int centerX = (int) (width / 2 + viewOffset.x);
        int centerY = (int) (height / 2 - viewOffset.y);
        g.setColor(Color.BLACK);
        g.drawLine(0, centerY, width, centerY);
        g.drawLine(centerX, 0, centerX, height);
        double interval = scaleFactor >= 100 ? 0.25 : scaleFactor >= 50 ? 0.5 : scaleFactor >= 20 ? 1.0 : scaleFactor >= 15 ? 2.0 : scaleFactor >= 10 ? 4.0 : scaleFactor >= 8 ? 5.0 : scaleFactor >= 5 ? 8.0 : 10.0;
        g.drawString("(m)", centerX - 22, centerY + 15);
        for (double i = interval; i * scaleFactor + centerX < 10 * width; i += interval) {
            int markXPos = (int) (i * scaleFactor + centerX);
            g.drawLine(markXPos, centerY - 5, markXPos, centerY + 5);
            g.drawString(String.format((scaleFactor >= 100) ? "%.2f" : (scaleFactor >= 50) ? "%.1f" : "%.0f", i), markXPos - 15, centerY + 20);

            int markXNeg = (int) (-i * scaleFactor + centerX);
            g.drawLine(markXNeg, centerY - 5, markXNeg, centerY + 5);
            g.drawString(String.format((scaleFactor >= 100) ? "%.2f" : (scaleFactor >= 50) ? "%.1f" : "%.0f", -i), markXNeg - 15, centerY + 20);
        }

        for (double i = interval; i * scaleFactor + centerY < 10 * height; i += interval) {
            int markYNeg = (int) (centerY - i * scaleFactor);
            g.drawLine(centerX - 5, markYNeg, centerX + 5, markYNeg);
            g.drawString(String.format((scaleFactor >= 100) ? "%.2f" : (scaleFactor >= 50) ? "%.1f" : "%.0f", i), (i >= 100) ? centerX - 40 : (i >= 10 && scaleFactor >= 50) ? centerX - 30 : centerX - 20, markYNeg + 5);
        }

        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < trajectoryPoints.size() - 1; i++) {
            TrajectoryPoint start = trajectoryPoints.get(i);
            TrajectoryPoint end = trajectoryPoints.get(i + 1);
            int x1 = (int) ((start.x * scaleFactor) + centerX);
            int y1 = (int) (centerY - (start.y * scaleFactor));
            int x2 = (int) ((end.x * scaleFactor) + centerX);
            int y2 = (int) (centerY - (end.y * scaleFactor));
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < trajectoryPoints.size(); i++) {
            TrajectoryPoint point = trajectoryPoints.get(i);
            int x = (int) ((point.x * scaleFactor) + centerX);
            int y = (int) (centerY - (point.y * scaleFactor));
            if (selectedPointIndex != null && selectedPointIndex == i) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillOval(x - 3, y - 3, 6, 6);
        }


        if (!hoverText.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString(hoverText, 10, getHeight() - 10);
        }
    }
}