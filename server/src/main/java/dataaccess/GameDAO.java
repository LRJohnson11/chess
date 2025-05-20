package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface GameDAO {

    public boolean createGame(String name);

    public Collection<GameData> listGames();

    public GameData getGame(int gameId);

    public boolean updateGame(int gameId, ChessGame.TeamColor color, String username);

    public boolean clear();
}
