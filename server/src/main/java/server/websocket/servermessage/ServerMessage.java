package server.websocket.servermessage;

public class ServerMessage {
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    ServerMessageType serverMessageType;
}
