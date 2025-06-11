package server.websocket;

import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<String, Connection> connections = new ConcurrentHashMap<>(); //authToken to session
    public final Map<Integer, Set<String>> connectionsByGame = new ConcurrentHashMap<>(); //gameId to the authTokens related

    public void add(AuthData auth, int gameID, Session session){
        connections.put(auth.authToken(), new Connection(auth.username(),session));
        connectionsByGame.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet())
                .add(auth.authToken());
    }

    public void remove(String authToken, int gameID){
        connections.remove(authToken);

        Set<String> tokenSet = connectionsByGame.get(gameID);
        if (tokenSet != null) {
            tokenSet.remove(authToken);
            if (tokenSet.isEmpty()) {
                connectionsByGame.remove(gameID); // cleanup if no more users
            }
        }
    }

    public void notifyGame(int gameID, String jsonMessage, String excludePlayer) throws IOException {
        Set<String> tokens = connectionsByGame.get(gameID);
        if (tokens == null) return;

        for (String token : tokens) {
            if (token.equals(excludePlayer)) continue;

            Connection conn = connections.get(token);
            if (conn != null) {
                conn.sendNotification(jsonMessage); // calls the private notify method internally
            }
        }
    }

    //broadcast to player

}
