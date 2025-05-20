package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class LocalGameDAO implements GameDAO{
    private Map<Integer,GameData> games;

    public LocalGameDAO(){
        this.games = new HashMap<>();
    }
    @Override
    public boolean createGame(String name) {
        int gameId = games.size() + 1;
        games.put(gameId, new GameData(gameId, null, null, name, new ChessGame()));
        return true;
    }

    @Override
    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
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
    }

    @Override
    public boolean clear() {
        games.clear();
        return true;
    }
}
