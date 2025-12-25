

import javax.swing.SwingUtilities;
import view.AntrianKlinikFrame;

public class MainApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new AntrianKlinikFrame().setVisible(true);
        });

    }
}
