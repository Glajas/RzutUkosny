import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    private final PhysicsEngine physicsEngine;
    private JTextField textFieldDeltaT, textFieldVelocityX, textFieldVelocityY, textFieldAirResistance, textFieldX, textFieldY;
    private JCheckBox checkboxUpgradedEuler;
    private JButton buttonSimulate;

    public GUI() {
        this.physicsEngine = new PhysicsEngine();
        setTitle("Projectile Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeComponents();
        setupListeners();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        textFieldVelocityX = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getVelocityX()));
        textFieldDeltaT = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getDeltaT()));
        textFieldVelocityY = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getVelocityY()));
        textFieldAirResistance = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getResistance()));
        textFieldX = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getX()));
        textFieldY = createTextFieldWithValidation(String.valueOf(physicsEngine.options.getY()));

        checkboxUpgradedEuler = new JCheckBox("Use Upgraded Euler's Method", physicsEngine.options.isUpgradedEulersMethod());
        buttonSimulate = new JButton("Simulate");

        JPanel panelSettings = new JPanel(new GridLayout(0, 2, 10, 10));
        panelSettings.add(new JLabel("Velocity X:"));
        panelSettings.add(textFieldVelocityX);
        panelSettings.add(new JLabel("Velocity Y:"));
        panelSettings.add(textFieldVelocityY);
        panelSettings.add(new JLabel("Air Resistance:"));
        panelSettings.add(textFieldAirResistance);
        panelSettings.add(new JLabel("Delta T:"));
        panelSettings.add(textFieldDeltaT);
        panelSettings.add(new JLabel("X Position:"));
        panelSettings.add(textFieldX);
        panelSettings.add(new JLabel("Y Position:"));
        panelSettings.add(textFieldY);
        panelSettings.add(checkboxUpgradedEuler);
        panelSettings.add(buttonSimulate);

        add(panelSettings, BorderLayout.CENTER);
    }

    private JTextField createTextFieldWithValidation(String defaultValue) {
        JTextField textField = new JTextField(defaultValue, 5);
        textField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                try {
                    double value = Double.parseDouble(textField.getText());
                    if (input == textFieldY && value < 0) {
                        throw new NumberFormatException("Value must be greater than or equal to 0");
                    }
                    return true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });
        return textField;
    }

    private void setupListeners() {
        buttonSimulate.addActionListener(e -> {
            physicsEngine.options.setX(Double.parseDouble(textFieldX.getText()));
            physicsEngine.options.setY(Double.parseDouble(textFieldY.getText()));
            physicsEngine.options.setVelocityX(Double.parseDouble(textFieldVelocityX.getText()));
            physicsEngine.options.setVelocityY(Double.parseDouble(textFieldVelocityY.getText()));
            physicsEngine.options.setAirResistance(Double.parseDouble(textFieldAirResistance.getText()));
            physicsEngine.options.setUpgradedEulersMethod(checkboxUpgradedEuler.isSelected());
            physicsEngine.options.setDeltaT(Double.parseDouble(textFieldDeltaT.getText()));

            physicsEngine.options.printChangesLog();
            physicsEngine.options.clearChangesLog();

            List<Point2D.Double> trajectoryPoints = physicsEngine.simulateProjectile(physicsEngine.options);
            showTrajectoryInNewWindow(trajectoryPoints);
        });
    }

    private void showTrajectoryInNewWindow(List<Point2D.Double> trajectoryPoints) {
        JFrame trajectoryFrame = new JFrame("Trajectory Visualization");
        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanel.setTrajectoryPoints(trajectoryPoints);
        trajectoryFrame.add(drawingPanel);
        trajectoryFrame.setSize(800, 600);
        trajectoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trajectoryFrame.setLocationRelativeTo(null);
        trajectoryFrame.setVisible(true);
    }

    static class DrawingPanel extends JPanel {
        private List<Point2D.Double> trajectoryPoints = new ArrayList<>();
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
                        viewOffset.x += scaleFactor*dx / scaleFactor;
                        viewOffset.y -= scaleFactor*dy / scaleFactor;
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

            for (Point2D.Double point : trajectoryPoints) {
                int x = (int) ((point.x * scaleFactor) + centerX);
                int y = (int) (centerY - (point.y * scaleFactor));
                if (mouseX >= x - 3 && mouseX <= x + 3 && mouseY >= y - 3 && mouseY <= y + 3) {
                    hoverText = String.format("X: %.1f, Y: %.1f", point.x, point.y);
                    break;
                }
            }
        }

        public void setTrajectoryPoints(List<Point2D.Double> trajectoryPoints) {
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

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int centerX = (int) (width / 2 + viewOffset.x);
            int centerY = (int) (height / 2 - viewOffset.y);
            g.setColor(Color.BLACK);
            g.drawLine(0, centerY, width, centerY);
            g.drawLine(centerX, 0, centerX, height);

            double interval = scaleFactor >= 100 ? 0.25 : scaleFactor >= 50 ? 0.5 : scaleFactor >= 20 ? 1.0 : scaleFactor >= 15 ? 2.0 : scaleFactor >= 10 ? 2.5 : scaleFactor >= 8 ? 4.0 : scaleFactor >= 5 ? 5.0 : 10.0;
            for (double i = interval; i * scaleFactor + centerX < 5*width; i += interval) {
                int markXPos = (int) (i * scaleFactor + centerX);
                g.drawLine(markXPos, centerY - 5, markXPos, centerY + 5);
                g.drawString(String.format(((scaleFactor >=100) ? "%.2f" : "%.1f"), i), markXPos - 15, centerY - 10);

                int markXNeg = (int) (-i * scaleFactor + centerX);
                g.drawLine(markXNeg, centerY - 5, markXNeg, centerY + 5);
                g.drawString(String.format(((scaleFactor >=100) ? "%.2f" : "%.1f"), -i), markXNeg - 15, centerY - 10);
            }

            for (double i = interval; i * scaleFactor + centerY < 5*height; i += interval) {
                int markYNeg = (int) (centerY - i * scaleFactor);
                g.drawLine(centerX - 5, markYNeg, centerX + 5, markYNeg);
                g.drawString(String.format(((scaleFactor >=100) ? "%.2f" : "%.1f"), i), centerX + 10, markYNeg + 5);
            }

            g.setColor(Color.RED);
            for (Point2D.Double point : trajectoryPoints) {
                int x = (int) ((point.x * scaleFactor) + centerX);
                int y = (int) (centerY - (point.y * scaleFactor));
                g.fillOval(x - 3, y - 3, 6, 6);
            }

            if (!hoverText.isEmpty()) {
                g.setColor(Color.BLACK);
                g.drawString(hoverText, 10, height - 20);
            }
        }
    }
}

