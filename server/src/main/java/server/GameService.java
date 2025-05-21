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

    public int createGame(String gameName){
        return gameDAO.createGame(gameName);
    }

    public void joinGame(JoinGameRequest request){

    }

    public void clearGames(){
        gameDAO.clear();
    }
}
