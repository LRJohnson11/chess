package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameDAOTests {
    GameDAO gameDAO = new MySqlGameDAO();

    @BeforeEach
    public void clearGames(){
        gameDAO.clear();
    }

    @Test
    @DisplayName("create game test")
    public void createGameTest(){
        String gameName = "hoodoo";
        var data = gameDAO.createGame(gameName);
        assert data.gameID() != 0;

    }

    @Test
    @DisplayName("fail create game test")
    public void failCreateGameTest(){
        try {
            gameDAO.createGame(null);
            assert false;
        }
        catch (Exception e){
            assert true;
        }

    }

    @Test
    @DisplayName("list games test")
    public void listGamesTest(){
        gameDAO.createGame("firstGame");
        gameDAO.createGame("secondGame");
        var result = gameDAO.listGames();
        assert result.games().size() == 2;

    }

    @Test
    @DisplayName("fail list games test")
    public void failListGamesTest(){
        assert gameDAO.listGames().games().isEmpty();

    }

    @Test
    @DisplayName("update game test")
    public void updateGameTest(){
        var gameId = gameDAO.createGame("game1");
        assert gameDAO.updateGame(gameId.gameID(), ChessGame.TeamColor.WHITE, "user");


    }

    @Test
    @DisplayName("fail update game test")
    public void failUpdateGameTest(){
        assert !gameDAO.updateGame(1, ChessGame.TeamColor.WHITE,"user");

    }

    @Test
    @DisplayName("get game test")
    public void  getGameTest(){
        String gameName = "hoodoo";
        var gameData = gameDAO.createGame(gameName);
        var result = gameDAO.getGame(gameData.gameID());
        assert result.gameName().equals(gameName);
    }

    @Test
    @DisplayName("fail get game test")
    public void failGetGameTest(){
        GameData game = gameDAO.getGame(1);
        assert game == null;

    }

    @Test
    @DisplayName("clear games test")
    public void clearGamesTest(){
        gameDAO.createGame("game");
        var games = gameDAO.listGames();
        gameDAO.clear();
        var updatedGames = gameDAO.listGames();

        assert games.games().size() != updatedGames.games().size();
    }

}
