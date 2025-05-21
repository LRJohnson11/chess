package server;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

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
            throw new ApiException(400, "Error: bad request");
        }
            return gameDAO.createGame(request.gameName());

    }

    public void joinGame(JoinGameRequest request, String username){
        if(!request.valid()){
            throw new ApiException(400, "Error: bad request");
        }
        GameData game = gameDAO.getGame(request.gameID());
        if(game == null){
            throw new ApiException(401, "Error: bad request");
        }
        if(request.playerColor() == ChessGame.TeamColor.WHITE && game.whiteUsername() != null){
            throw new ApiException(403, "Error: already taken");
        }
        if(request.playerColor() == ChessGame.TeamColor.BLACK && game.blackUsername() != null){
            throw new ApiException(403, "Error: already taken");
        }
        gameDAO.updateGame(request.gameID(),request.playerColor(), username);
    }

    public void clearGames(){
        gameDAO.clear();
    }
}
