import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JPanel;

public class GUI extends JFrame {
    private PhysicsEngine physicsEngine;
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
        textFieldVelocityX = new JTextField(String.valueOf(physicsEngine.options.getVelocityX()), 5);
        textFieldDeltaT = new JTextField(String.valueOf(physicsEngine.options.getDeltaT()), 5);
        textFieldVelocityY = new JTextField(String.valueOf(physicsEngine.options.getVelocityY()), 5);
        textFieldAirResistance = new JTextField(String.valueOf(physicsEngine.options.getResistance()), 5);
        textFieldX = new JTextField(String.valueOf(physicsEngine.options.getX()), 5);
        textFieldY = new JTextField(String.valueOf(physicsEngine.options.getY()), 5);

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

    class DrawingPanel extends JPanel {
        private List<Point2D.Double> trajectoryPoints = new ArrayList<>();
        private double scaleFactor = 10.0;
        private String hoverText = "";

        public DrawingPanel() {
            setLayout(new BorderLayout());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JButton buttonIncreaseScale = new JButton("+");
            JButton buttonDecreaseScale = new JButton("-");

            buttonIncreaseScale.addActionListener(e -> setScaleFactor(scaleFactor + 1));
            buttonDecreaseScale.addActionListener(e -> setScaleFactor(Math.max(1, scaleFactor - 1)));

            buttonPanel.add(buttonDecreaseScale);
            buttonPanel.add(buttonIncreaseScale);

            this.add(buttonPanel, BorderLayout.NORTH);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    updateHoverText(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        private void updateHoverText(int mouseX, int mouseY) {
            hoverText = "";
            for (Point2D.Double point : trajectoryPoints) {
                int x = (int) ((point.x * scaleFactor) + getWidth() / 2);
                int y = getHeight() - (int) ((point.y * scaleFactor) + getHeight() / 2);
                if (mouseX >= x - 3 && mouseX <= x + 3 && mouseY >= y - 3 && mouseY <= y + 3) {
                    hoverText = String.format("X: %.2f, Y: %.2f", point.x, point.y);
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

            g.setColor(Color.BLACK);
            g.drawLine(0, height / 2, width, height / 2);
            g.drawLine(width / 2, 0, width / 2, height);

            final int markSize = 5;
            final int interval = (int) scaleFactor;
            java.util.function.IntPredicate isMultipleOfFive = value -> value % 5 == 0;

            for (int x = 0; x <= width; x += interval) {
                int scaledX = (x - width / 2) / interval;
                if (isMultipleOfFive.test(scaledX) && scaledX != 0) {
                    g.drawLine(x, height / 2 - markSize, x, height / 2 + markSize);
                    g.drawString(String.valueOf(scaledX), x - markSize, height / 2 + 2 * markSize);
                }
            }

            for (int y = 0; y <= height; y += interval) {
                int scaledY = (height / 2 - y) / interval;
                if (isMultipleOfFive.test(scaledY) && scaledY != 0) {
                    g.drawLine(width / 2 - markSize, y, width / 2 + markSize, y);
                    g.drawString(String.valueOf(scaledY), width / 2 + 2 * markSize, y + markSize);
                }
            }

            g.setColor(Color.RED);
            for (Point2D.Double point : trajectoryPoints) {
                int x = (int) (point.x * scaleFactor) + width / 2;
                int y = height - (int) (point.y * scaleFactor) - height / 2;
                g.fillOval(x - 3, y - 3, 6, 6);
            }
            if (!hoverText.isEmpty()) {
                g.setColor(Color.BLACK);
                g.drawString(hoverText, 10, height - 10);
            }
        }
    }
}
