package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String message;

    public NotificationMessage(ServerMessageType type,String notification) {
        super(type);
        this.message = notification;
    }
}
