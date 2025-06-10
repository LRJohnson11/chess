package server.websocket.servermessage;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
