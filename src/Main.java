import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.pack();
            gui.setVisible(true);
        });
    }
}
