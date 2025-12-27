package api;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.function.Consumer;

public class WebSocketAntrianClient extends WebSocketClient {
    
    private Consumer<String> messageHandler;
    private Consumer<Boolean> connectionStatusHandler;
    
    public WebSocketAntrianClient(URI serverUri, 
                                  Consumer<String> messageHandler,
                                  Consumer<Boolean> connectionStatusHandler) {
        super(serverUri);
        this.messageHandler = messageHandler;
        this.connectionStatusHandler = connectionStatusHandler;
    }
    
    // Constructor overload untuk URL string
    public WebSocketAntrianClient(String serverUrl,
                                  Consumer<String> messageHandler,
                                  Consumer<Boolean> connectionStatusHandler) {
        this(URI.create(serverUrl), messageHandler, connectionStatusHandler);
    }
    
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✓ WebSocket connected");
        if (connectionStatusHandler != null) {
            connectionStatusHandler.accept(true);
        }
    }
    
    @Override
    public void onMessage(String message) {
        System.out.println("📨 WebSocket message: " + message);
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("✗ WebSocket closed: " + reason);
        if (connectionStatusHandler != null) {
            connectionStatusHandler.accept(false);
        }
    }
    
    @Override
    public void onError(Exception ex) {
        System.err.println("⚠️ WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }
    
    public boolean isOpen() {
        return super.isOpen();
    }
}