import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
// atau: import com.formdev.flatlaf.FlatDarkLaf;

import view.AntrianKlinikFrame;

public class MainApp {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AntrianKlinikFrame().setVisible(true);
        });
    }
}
