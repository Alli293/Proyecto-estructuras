import service.RedSocial;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RedSocial redSocial = new RedSocial();
            MainFrame frame = new MainFrame(redSocial);
            frame.setVisible(true);
        });
    }
}