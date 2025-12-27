package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class AntrianWebSocketClient extends WebSocketClient {

    public AntrianWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("CLIENT TERHUBUNG KE SERVER");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("DATA REALTIME: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("KONEKSI DITUTUP");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
