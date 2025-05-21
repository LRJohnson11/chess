package passoff;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.LocalGameDAO;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.GameService;
import server.apiException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class GameServiceTests {
    GameDAO gameDao = new LocalGameDAO();
    GameService gameService = new GameService(gameDao);

    @Test
    @DisplayName("getGamesListTest")
    public void getGamesTest(){
        GetGamesResponse response = gameService.listGames();

        assert response.games().isEmpty();
    }
    @Test
    @DisplayName("getGamesListTest")
    public void getGamesTestWithReturn(){
        CreateGameRequest gameRequest = new CreateGameRequest("new game");
        CreateGameResponse gameResponse = gameService.createGame(gameRequest);
        GetGamesResponse response = gameService.listGames();
        ArrayList<GameData> games = (ArrayList<GameData>) response.games();

        assert games.size() == 1;
    }

    @Test
    @DisplayName("Create game test")
    public void createGameTest(){
        CreateGameRequest gameRequest = new CreateGameRequest("new game");
        CreateGameResponse gameResponse = gameService.createGame(gameRequest);

        assert gameResponse.gameID() == 1;
    }

    @Test
    @DisplayName("Create game test should fail")
    public void createGameTestInvalidParams(){
        try {
            CreateGameRequest gameRequest = new CreateGameRequest("");
            CreateGameResponse gameResponse = gameService.createGame(gameRequest);
            assert false;
        } catch (apiException e){
            assert true;
        }

    }

    @Test
    @DisplayName("Join game test")
    public void joinGameTest() {
        CreateGameRequest gameRequest = new CreateGameRequest("new game");
        CreateGameResponse gameResponse = gameService.createGame(gameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameResponse.gameID());
        gameService.joinGame(joinGameRequest, "username");
        GetGamesResponse response = gameService.listGames();
        ArrayList<GameData> games = (ArrayList<GameData>) response.games();

        assert games.getFirst().whiteUsername().equals("username");


    }

    @Test
    @DisplayName("Join game test invalid params")
    public void joinGameTestInvalidParams() {
        try{
            CreateGameRequest gameRequest = new CreateGameRequest("new game");
            CreateGameResponse gameResponse = gameService.createGame(gameRequest);
            JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 0);
            gameService.joinGame(joinGameRequest, "username");

            assert false;
        }catch (apiException e){
            assert true;
        }

    }
    
    @Test
    @DisplayName("clear games test")
    public void clearGameTest(){
        CreateGameRequest gameRequest = new CreateGameRequest("new game");
        CreateGameResponse gameResponse = gameService.createGame(gameRequest);
        gameService.clearGames();
        GetGamesResponse emptylistResponse = gameService.listGames();
        
        assert emptylistResponse.games().isEmpty();
    }

}
