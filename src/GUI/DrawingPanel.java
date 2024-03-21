package GUI;

import javax.swing.*;
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

    public DrawingPanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buttonIncreaseScale = new JButton("+");
        JButton buttonDecreaseScale = new JButton("-");
        buttonIncreaseScale.addActionListener(e -> setScaleFactor(scaleFactor + 1));
        buttonDecreaseScale.addActionListener(e -> setScaleFactor(Math.max(1, (scaleFactor >= 4) ? scaleFactor - 1 : scaleFactor)));
        buttonPanel.add(buttonDecreaseScale);
        buttonPanel.add(buttonIncreaseScale);
        this.add(buttonPanel, BorderLayout.NORTH);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoverText(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartPoint != null) {
                    int dx = e.getX() - dragStartPoint.x;
                    int dy = e.getY() - dragStartPoint.y;
                    double potentialYOffset = viewOffset.y - dy;
                    viewOffset.x += dx;
                    if (getHeight() / 2 + potentialYOffset <= getHeight() / 2) {
                        viewOffset.y -= dy;
                    }
                    dragStartPoint = e.getPoint();
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartPoint = e.getPoint();
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
        for (double i = interval; i * scaleFactor + centerX < width; i += interval) {
            int markXPos = (int) (i * scaleFactor + centerX);
            g.drawLine(markXPos, centerY - 5, markXPos, centerY + 5);
            g.drawString(String.format("%.0f", i), markXPos - 15, centerY + 20);

            int markXNeg = (int) (-i * scaleFactor + centerX);
            g.drawLine(markXNeg, centerY - 5, markXNeg, centerY + 5);
            g.drawString(String.format("%.0f", -i), markXNeg - 15, centerY + 20);
        }

        for (double i = interval; i * scaleFactor + centerY < height; i += interval) {
            int markYNeg = (int) (centerY - i * scaleFactor);
            g.drawLine(centerX - 5, markYNeg, centerX + 5, markYNeg);
            g.drawString(String.format("%.0f", i), centerX - 20, markYNeg + 5);
        }

        g.setColor(Color.RED);
        for (TrajectoryPoint point : trajectoryPoints) {
            int x = (int) ((point.x * scaleFactor) + centerX);
            int y = (int) (centerY - (point.y * scaleFactor));
            g.fillOval(x - 3, y - 3, 6, 6);
        }

        if (!hoverText.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString(hoverText, 10, getHeight() - 20);
        }
    }
}