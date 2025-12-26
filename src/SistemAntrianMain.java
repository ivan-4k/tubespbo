import controller.AntrianController;
import view.AntrianKlinikFrame;
import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

public class SistemAntrianMain {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Controller tanpa koneksi database langsung
            AntrianController controller = new AntrianController();
            AntrianKlinikFrame frame = new AntrianKlinikFrame(controller);
            frame.setVisible(true);
        });
    }
}