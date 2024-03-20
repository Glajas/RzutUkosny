import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

public class GUI extends JFrame {
    private PhysicsEngine physicsEngine;
    private JSlider sliderVelocityX, sliderVelocityY, sliderAirResistance;
    private JTextField textFieldDeltaT, textFieldVelocityX, textFieldVelocityY, textFieldAirResistance, textFieldX, textFieldY;
    private JCheckBox checkboxUpgradedEuler;
    private JButton buttonSimulate;
    private DrawingPanel drawingPanel;
    private TrajectoryFrame trajectoryFrame;


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
        sliderVelocityX = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) physicsEngine.options.getVelocityX());
        sliderVelocityY = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) physicsEngine.options.getVelocityY());
        sliderAirResistance = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (physicsEngine.options.getResistance() * 100));

        textFieldVelocityX = new JTextField(String.valueOf(physicsEngine.options.getVelocityX()), 5);
        textFieldDeltaT = new JTextField(String.valueOf(physicsEngine.options.getDeltaT()), 5);
        textFieldVelocityY = new JTextField(String.valueOf(physicsEngine.options.getVelocityY()), 5);
        textFieldAirResistance = new JTextField(String.valueOf(physicsEngine.options.getResistance()), 5);
        textFieldX = new JTextField(String.valueOf(physicsEngine.options.getX()), 5);
        textFieldY = new JTextField(String.valueOf(physicsEngine.options.getY()), 5);

        checkboxUpgradedEuler = new JCheckBox("Use Upgraded Euler's Method", physicsEngine.options.isUpgradedEulersMethod());
        buttonSimulate = new JButton("Simulate");

        JPanel panelVelocityX = new JPanel();
        panelVelocityX.add(new JLabel("Velocity X:"));
        panelVelocityX.add(textFieldVelocityX);
        panelVelocityX.add(sliderVelocityX);

        JPanel panelVelocityY = new JPanel();
        panelVelocityY.add(new JLabel("Velocity Y:"));
        panelVelocityY.add(textFieldVelocityY);
        panelVelocityY.add(sliderVelocityY);

        JPanel panelAirResistance = new JPanel();
        panelAirResistance.add(new JLabel("Air Resistance:"));
        panelAirResistance.add(textFieldAirResistance);
        panelAirResistance.add(sliderAirResistance);

        JPanel panelDeltaT = new JPanel();
        panelDeltaT.add(new JLabel("Delta t:"));
        panelDeltaT.add(textFieldDeltaT);

        JPanel panelXPosition = new JPanel();
        panelXPosition.add(new JLabel("X Position:"));
        panelXPosition.add(textFieldX);

        JPanel panelYPosition = new JPanel();
        panelYPosition.add(new JLabel("Y Position:"));
        panelYPosition.add(textFieldY);

        JPanel panelUpgradedEuler = new JPanel();
        panelUpgradedEuler.add(checkboxUpgradedEuler);

        JPanel panelSimulateButton = new JPanel();
        panelSimulateButton.add(buttonSimulate);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(panelVelocityX);
        controlPanel.add(panelVelocityY);
        controlPanel.add(panelAirResistance);
        controlPanel.add(panelDeltaT);
        controlPanel.add(panelXPosition);
        controlPanel.add(panelYPosition);
        controlPanel.add(panelUpgradedEuler);
        controlPanel.add(panelSimulateButton);

        add(controlPanel, BorderLayout.NORTH);

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        sliderVelocityX.addChangeListener(e -> {
            double value = sliderVelocityX.getValue();
            textFieldVelocityX.setText(Double.toString(value));
            physicsEngine.options.setVelocityX(value);
        });

        sliderVelocityY.addChangeListener(e -> {
            double value = sliderVelocityY.getValue();
            textFieldVelocityY.setText(Double.toString(value));
            physicsEngine.options.setVelocityY(value);
        });

        sliderAirResistance.addChangeListener(e -> {
            double value = sliderAirResistance.getValue() / 100.0;
            textFieldAirResistance.setText(Double.toString(value));
            physicsEngine.options.setAirResistance(value);
        });

        textFieldVelocityX.addActionListener(e -> {
            double value = Double.parseDouble(textFieldVelocityX.getText());
            physicsEngine.options.setVelocityX(value);
            sliderVelocityX.setValue((int) value);
        });

        textFieldVelocityY.addActionListener(e -> {
            double value = Double.parseDouble(textFieldVelocityY.getText());
            physicsEngine.options.setVelocityY(value);
            sliderVelocityY.setValue((int) value);
        });

        textFieldAirResistance.addActionListener(e -> {
            double value = Double.parseDouble(textFieldAirResistance.getText());
            physicsEngine.options.setAirResistance(value);
            sliderAirResistance.setValue((int) (value * 100));
        });

        textFieldDeltaT.addActionListener(e -> {
            double value = Double.parseDouble(textFieldDeltaT.getText());
            physicsEngine.options.setDeltaT(value);
        });

        textFieldX.addActionListener(e -> {
            double value = Double.parseDouble(textFieldX.getText());
            physicsEngine.options.setX(value);
        });

        textFieldY.addActionListener(e -> {
            double value = Double.parseDouble(textFieldY.getText());
            physicsEngine.options.setY(value);
        });

        checkboxUpgradedEuler.addActionListener(e -> {
            physicsEngine.options.setUpgradedEulersMethod(checkboxUpgradedEuler.isSelected());
        });

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

            DrawingPanel drawingPanel = new DrawingPanel(trajectoryFrame);
            trajectoryFrame = new TrajectoryFrame("Trajektoria pocisku", drawingPanel);
            trajectoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            drawingPanel.setTrajectoryPoints(trajectoryPoints);
            drawingPanel.repaint();

            trajectoryFrame.setVisible(true);
        });
    }

    class TrajectoryFrame extends JFrame {
        private DrawingPanel drawingPanel;

        public TrajectoryFrame(String title, DrawingPanel drawingPanel) {
            super(title);
            this.drawingPanel = drawingPanel;
            this.setLayout(new BorderLayout());
            this.add(drawingPanel, BorderLayout.CENTER);
            this.setSize(800, 600);
            this.setLocationRelativeTo(null);
        }
    }

    class DrawingPanel extends JPanel {
        private TrajectoryFrame trajectoryFrame;

        public DrawingPanel(TrajectoryFrame trajectoryFrame) {
            this.trajectoryFrame = trajectoryFrame;
        }
        public DrawingPanel() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        private List<Point2D.Double> trajectoryPoints = new ArrayList<>();

        public void setTrajectoryPoints(List<Point2D.Double> trajectoryPoints) {
            this.trajectoryPoints = trajectoryPoints;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int width = getWidth();
            int height = getHeight();

            super.paintComponent(g);

            // Draw axis
            g.setColor(Color.BLACK);
            g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

            double maxX = Math.abs(trajectoryPoints.stream()
                    .max(Comparator.comparingDouble(p -> p.x))
                    .map(p -> p.x)
                    .orElse(0.0));

            double maxY = Math.abs(trajectoryPoints.stream()
                    .max(Comparator.comparingDouble(p -> p.y))
                    .map(p -> p.y)
                    .orElse(0.0));

            double scaleFactor = Math.min(width / (2 * maxX), height / (2 * maxY));

            g.setColor(Color.RED);
            for (Point2D.Double point : trajectoryPoints) {
                int x = (int) (point.x * scaleFactor) + width / 2;
                int y = (int) (height - point.y * scaleFactor) - height / 2;
                g.fillOval(x - 3, y - 3, 6, 6);
            }
        }
    }
}
