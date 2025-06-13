package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch(notification.getServerMessageType()){
                        case LOAD_GAME:
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.loadGame(loadGameMessage);
                            break;
                        case NOTIFICATION:
                            notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                            break;
                        case ERROR:
                            notificationHandler.showError(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void resign(String authToken, int gameID){
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, UserGameCommand.class));
        } catch (Exception e) {
            throw new RuntimeException("failed to resign");
        }

    }

    public void connect(String authToken, int gameID){
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, UserGameCommand.class));
        } catch (Exception e) {
            throw new RuntimeException("failed to connect");
        }
    }

    public void leave(String authToken, int gameID) {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command,UserGameCommand.class));
        } catch (Exception e){
            throw new RuntimeException("failed to leave the game");
        }
    }
    public void makeMove(String authtoken, int gameID, ChessMove move){
        try{
            MakeMoveCommand moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authtoken,gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand, MakeMoveCommand.class));
        } catch (Exception e) {
            throw new RuntimeException("failed to make move");
        }
    }
}
