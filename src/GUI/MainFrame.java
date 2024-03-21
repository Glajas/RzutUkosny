package GUI;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import Logic.PhysicsEngine;
import Logic.PhysicsEngine.TrajectoryPoint;

public class MainFrame extends JFrame {
    private PhysicsEngine physicsEngine;
    private JTextField textFieldDeltaT, textFieldVelocityX, textFieldVelocityY, textFieldAirResistance, textFieldX, textFieldY;
    private JCheckBox checkboxUpgradedEuler;
    private JButton buttonSimulate;

    public MainFrame() {
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
        panelSettings.add(new JLabel("  Velocity X:"));
        panelSettings.add(textFieldVelocityX);
        panelSettings.add(new JLabel("  Velocity Y:"));
        panelSettings.add(textFieldVelocityY);
        panelSettings.add(new JLabel("  Air Resistance:"));
        panelSettings.add(textFieldAirResistance);
        panelSettings.add(new JLabel("  Delta T:"));
        panelSettings.add(textFieldDeltaT);
        panelSettings.add(new JLabel("  X Position:"));
        panelSettings.add(textFieldX);
        panelSettings.add(new JLabel("  Y Position:"));
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
                    JOptionPane.showMessageDialog(MainFrame.this, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

            List<TrajectoryPoint> trajectoryPoints = physicsEngine.simulateProjectile(physicsEngine.options);
            showTrajectoryInNewWindow(trajectoryPoints);
        });
    }

    private void showTrajectoryInNewWindow(List<TrajectoryPoint> trajectoryPoints) {
        JFrame trajectoryFrame = new JFrame("Trajectory Visualization");
        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanel.setTrajectoryPoints(trajectoryPoints);
        trajectoryFrame.add(drawingPanel);
        trajectoryFrame.setSize(800, 600);
        trajectoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trajectoryFrame.setLocationRelativeTo(null);
        trajectoryFrame.setVisible(true);
    }
}

