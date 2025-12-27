package server;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class AntrianWebSocketServer extends WebSocketServer {

    public AntrianWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client TERHUBUNG: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Pesan masuk: " + message);

        // broadcast ke semua client
        broadcast(message);
    }
    @Override
public void onStart() {
    System.out.println("WebSocket Server SIAP menerima client");
}


    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client KELUAR");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        int port = 8080;
        AntrianWebSocketServer server = new AntrianWebSocketServer(port);
        server.start();

        System.out.println("WebSocket Server JALAN di ws://localhost:" + port);
    }
}
