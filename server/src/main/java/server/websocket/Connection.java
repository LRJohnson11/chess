package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    private String username;
    private Session session;

    public Connection(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void sendNotification(String message) throws IOException {

        session.getRemote().sendString(message);
    }
}
