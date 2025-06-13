package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.GameService;
import server.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private final ConnectionManager connections = new ConnectionManager();
    private UserDAO userDao = new MySqlUserDAO();
    private GameDAO gameDao  = new MySqlGameDAO();
    private AuthDAO authDAO = new MySqlAuthDAO();
    private UserService userService = new UserService(authDAO,userDao);
    private GameService gameService = new GameService(gameDao);

    private String[] columns = {"a","b","c","d","e","f","g","h"};

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

            if (base.getCommandType() == null) {
                System.out.println("ws: command was null");
                return;
            }

            switch (base.getCommandType()) {
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
                    handleMakeMove(command, session);
                    break;
                default:
                    session.getRemote().sendString("Unknown commandType: " + base.getCommandType());
            }

        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(gson.toJson(error, ErrorMessage.class));
        }
    }

    private void handleResign(UserGameCommand command) {
        AuthData auth = userService.getAuth(command.getAuthToken());
        GameData gameData = gameService.getGameByID(command.getGameID());
        ChessGame game = gameData.game();
        game.setTeamTurn(null);
        String jsonGame = gson.toJson(game, ChessGame.class);
        gameService.updateGame(command.getGameID(), jsonGame);
        String msg = auth.username() + " has resigned.";
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        String jsonNotification = gson.toJson(notification, NotificationMessage.class);

        try {
            connections.notifyGame(command.getGameID(), jsonNotification, null);
        } catch (IOException e) {
            System.out.println("handleResign failed");
        }
        //end the current game if user is one of the players, the other team wins
    }

    private void handleLeave(UserGameCommand command) {
        AuthData auth = userService.getAuth(command.getAuthToken());
        GameData game = gameService.getGameByID(command.getGameID());
        String msg = auth.username() + " has left the game";
        if(game.whiteUsername()!=null) {
            if (game.whiteUsername().equalsIgnoreCase(auth.username())) {
                msg = msg + " as white";
                gameService.removePlayerFromGame(command.getGameID(), ChessGame.TeamColor.WHITE);
            }
        }
        else if(game.blackUsername() != null){
            if(game.blackUsername().equalsIgnoreCase(auth.username())){
                msg = msg + " as black";
                gameService.removePlayerFromGame(command.getGameID(), ChessGame.TeamColor.BLACK);
            }

        } else{
            msg = msg + " as observer";
        }
        //if applicable, update game to no longer have the user present
        connections.remove(command.getAuthToken(), command.getGameID());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        String jsonNotification = gson.toJson(notification, NotificationMessage.class);
        try {
            connections.notifyGame(command.getGameID(), jsonNotification, auth.authToken());
        }
        catch (Exception e){
            System.out.println("handle leave failed");
        }
        //notify all other workers that root user has left
    }

    private void handleConnect(UserGameCommand command, Session session) {
        AuthData auth = userService.getAuth(command.getAuthToken());
        GameData game = gameService.getGameByID(command.getGameID());
        String msg = auth.username() + " has joined the game";
        if(game.whiteUsername()!=null) {
            if (game.whiteUsername().equalsIgnoreCase(auth.username())) {
                msg = msg + " as white";
            }
        }
        else if(game.blackUsername() != null){
            if(game.blackUsername().equalsIgnoreCase(auth.username())){
                msg = msg + " as black";
            }

        } else{
            msg = msg + " as observer";
        }
        connections.add(auth,command.getGameID(), session);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        String jsonNotification = gson.toJson(notification, NotificationMessage.class);
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String jsonLoadGameMessage = gson.toJson(loadGameMessage, LoadGameMessage.class);

        try {
        connections.notifyGame(command.getGameID(),jsonNotification, command.getAuthToken());
        session.getRemote().sendString(jsonLoadGameMessage);
        } catch (Exception e){
            System.out.println("Server: failed to connect");
        }
        //broadcast rest of game that user has joined the game
        //send load game to root client
    }
    private void handleMakeMove(MakeMoveCommand command,Session session) throws IOException {
        AuthData auth = userService.getAuth(command.getAuthToken());
        GameData gameData = gameService.getGameByID(command.getGameID());
        ChessGame game = gameData.game();

        try{
            game.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            String errorMessage = e.getMessage();
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            String errorJson = gson.toJson(error, ErrorMessage.class);
            session.getRemote().sendString(errorJson);
            return;
        }
        //update the server
        String gameJson = gson.toJson(game, ChessGame.class);
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        String loadingJson = gson.toJson(loadGameMessage, LoadGameMessage.class);
        String movemsg = auth.username() + " moved " + game.getBoard().getPiece(command.getMove().getEndPosition()).getPieceType().name() + " to " + columns[command.getMove().getEndPosition().getColumn() - 1] + command.getMove().getEndPosition().getRow();
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, movemsg);
        String moveJson = gson.toJson(notificationMessage, NotificationMessage.class);
        //check for check,checkmate, and stalemate on enemy player
        ChessGame.TeamColor enemyTeam = Objects.equals(gameData.whiteUsername(), auth.username()) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String gameStateMsg = "";
        if(game.isInCheck(enemyTeam)){
            gameStateMsg = enemyTeam.name() + " is in check";
            if(game.isInCheckmate(enemyTeam)){
                game.setTeamTurn(null);
                gameStateMsg = enemyTeam.name() + " is in checkmate! game over.";
            }
        }
        else if(game.isInStalemate(enemyTeam)){
            gameStateMsg = enemyTeam.name() + " is in Stalemate";
            game.setTeamTurn(null);
        }
        NotificationMessage gameStateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, gameStateMsg);
        String gameStateJson = gson.toJson(gameStateNotification, NotificationMessage.class);

        gameService.updateGame(command.getGameID(), gameJson);

        try{
            connections.notifyGame(command.getGameID(), loadingJson, null);
            connections.notifyGame(command.getGameID(),moveJson, null);
            if(!gameStateMsg.isEmpty()){
                connections.notifyGame(command.getGameID(), gameStateJson, null);
            }

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
