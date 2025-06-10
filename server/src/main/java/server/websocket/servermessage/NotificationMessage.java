package server.websocket.servermessage;

public class NotificationMessage extends ServerMessage{
    private final String notification;

    public NotificationMessage(String notification) {
        this.notification = notification;
    }
}
