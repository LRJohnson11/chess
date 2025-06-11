package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.util.*;

public class LocalGameDAO implements GameDAO{
    private final Map<Integer,GameData> games;

    public LocalGameDAO(){
        this.games = new HashMap<>();
    }

    @Override
    public CreateGameResponse createGame(String name) {
        int gameId = games.size() + 1;
        games.put(gameId, new GameData(gameId, null, null, name, new ChessGame()));
        return new CreateGameResponse(gameId);
    }

    @Override
    public GetGamesResponse listGames() {
        return new GetGamesResponse(new ArrayList<>(games.values()));
    }

    @Override
    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    @Override
    public boolean updateGame(int gameId, ChessGame.TeamColor color, String username) {
        GameData game = games.get(gameId);
        if (color == ChessGame.TeamColor.WHITE){
            games.put(gameId, new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game() ));
        }
        else{
            games.put(gameId, new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game()));
        }
        return true;
    }

    @Override
    public boolean clear() {
        games.clear();
        return true;
    }

    @Override
    public void updateGameData(int gameID, String gameJson) {
    }
}
