package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService( GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames(){
        return gameDAO.listGames();
    }

    public int createGame(CreateGameRequest request){
        return gameDAO.createGame(request.gameName());
    }

    public void joinGame(JoinGameRequest request, String username){
        gameDAO.updateGame(request.gameID(),request.playerColor(), username);
    }

    public void clearGames(){
        gameDAO.clear();
    }
}
