import controller.AntrianController;
import view.AntrianKlinikFrame;
import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import client.AntrianWebSocketClient;
import java.net.URI;

public class SistemAntrianMain {

    public static AntrianWebSocketClient wsClient;

    public static void main(String[] args) {

        try {
            wsClient = new AntrianWebSocketClient(
                    new URI("ws://localhost:3000/ws/antrian")

            );
            wsClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

       
        SwingUtilities.invokeLater(() -> {
            AntrianController controller = new AntrianController();
            AntrianKlinikFrame frame = new AntrianKlinikFrame(controller);
            frame.setVisible(true);
        });
    }
}
