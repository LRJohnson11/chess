package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void loadGame(LoadGameMessage loadGame);
    void notify(NotificationMessage notification);
    void showError(ErrorMessage error);
}