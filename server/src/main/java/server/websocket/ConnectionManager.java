package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> conections = new ConcurrentHashMap<>();

    public void add(String username, Session session){
        conections.put(username, new Connection(username,session));
    }

    public void remove(String username){
        conections.remove(username);
    }

    //methods needed: broadcast to game

    //broadcast to rest of game

    //broadcast to player

}
