package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.util.Collection;

public interface GameDAO {

    CreateGameResponse createGame(String name);

    GetGamesResponse listGames();

    GameData getGame(int gameId);

    boolean updateGame(int gameId, ChessGame.TeamColor color, String username);

    boolean clear();
}
