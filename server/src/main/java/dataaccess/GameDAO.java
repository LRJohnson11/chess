package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.util.Collection;

public interface GameDAO {

    public CreateGameResponse createGame(String name);

    public GetGamesResponse listGames();

    public GameData getGame(int gameId);

    public boolean updateGame(int gameId, ChessGame.TeamColor color, String username);

    public boolean clear();
}
