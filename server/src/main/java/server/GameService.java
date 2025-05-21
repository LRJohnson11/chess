package server;

import dataaccess.GameDAO;
import model.GameData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService( GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GetGamesResponse listGames(){
        return gameDAO.listGames();
    }

    public CreateGameResponse createGame(CreateGameRequest request){
        if(!request.validRequest()) {
            throw new apiException(400, "Error: bad request");
        }
            return gameDAO.createGame(request.gameName());

    }

    public void joinGame(JoinGameRequest request, String username){
        gameDAO.updateGame(request.gameID(),request.playerColor(), username);
    }

    public void clearGames(){
        gameDAO.clear();
    }
}
