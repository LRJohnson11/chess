package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.GameService;
import server.UserService;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private final ConnectionManager connections = new ConnectionManager();
    private UserDAO userDao = new MySqlUserDAO();
    private GameDAO gameDao  = new MySqlGameDAO();
    private AuthDAO authDAO = new MySqlAuthDAO();
    private UserService userService = new UserService(authDAO,userDao);
    private GameService gameService = new GameService(gameDao);

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session.getRemoteAddress());
    }



    @OnWebSocketMessage
    public void handleMessage(Session session, String message) throws IOException {
        System.out.println("received: " + message);
        try {
            // Peek at commandType to determine exact command class
            UserGameCommand base = gson.fromJson(message, UserGameCommand.class);

            if (base.commandType == null) {
                System.out.println("ws: command was null");
                return;
            }

            switch (base.commandType) {
                case CONNECT:
                    handleConnect(base, session);
                    break;
                case LEAVE:
                    handleLeave(base);
                    break;
                case RESIGN:
                    handleResign(base);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand command = gson.fromJson(message, MakeMoveCommand.class);
                    handleMakeMove(command);
                    break;
                default:
                    session.getRemote().sendString("Unknown commandType: " + base.commandType);
            }

        } catch (Exception e) {
            session.getRemote().sendString("Malformed JSON or invalid command");
            e.printStackTrace();
        }
    }

    private void handleResign(UserGameCommand command) {
        //end the current game if user is one of the players, the other team wins
    }

    private void handleLeave(UserGameCommand command) {
        AuthData auth = userService.getAuth(command.authToken);
        connections.remove(auth.username());
        //notify all other workers that root user has left
    }

    private void handleConnect(UserGameCommand command, Session session) {
        AuthData auth = userService.getAuth(command.authToken);
        connections.add(auth.username(), session);
        try {
            session.getRemote().sendString("connected");
        } catch (Exception e){
            System.out.println("didn't work");
        }
        //broadcast rest of game that user has joined the game
        //send load game to root client
    }
    private void handleMakeMove(MakeMoveCommand command) {
        //handle makemove
        //update game in db
        //sends load_GAME to all parties



        //self-descriptive
        System.out.println("make move");
    }

}
