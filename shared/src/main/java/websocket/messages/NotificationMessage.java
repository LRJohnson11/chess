package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String notification;

    public NotificationMessage(ServerMessageType type,String notification) {
        super(type);
        this.notification = notification;
    }
}
