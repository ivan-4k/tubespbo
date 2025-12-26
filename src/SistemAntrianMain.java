import javax.swing.*;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import controller.AntrianController;
import view.AntrianKlinikFrame;

public class SistemAntrianMain {
    public static void main(String[] args) {
        // Inisialisasi controller terlebih dahulu
        AntrianController controller = new AntrianController();
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            System.out.println("FlatLaf Look and Feel applied successfully!");
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Falling back to system look and feel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Run GUI
        SwingUtilities.invokeLater(() -> {
            try {
                AntrianKlinikFrame frame = new AntrianKlinikFrame(controller);
                frame.setVisible(true);
                System.out.println("Sistem Antrian Klinik berjalan...");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}