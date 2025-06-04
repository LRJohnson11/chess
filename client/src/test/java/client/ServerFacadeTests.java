package client;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String seedToken;
    private static String logoutToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        try {
            facade.clearDB();
            seedToken = facade.registerUser(new UserData("gamesUser", "password", "email")).authToken();
            logoutToken = facade.registerUser(new UserData("logoutUser","password", "email")).authToken();
            var loggedOutAuth = facade.registerUser(new UserData("loggedOutUser", "password", "email")).authToken();
            facade.logoutUser(loggedOutAuth);
        } catch (Exception e){
            System.out.print(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("positive: register user")
    public void registerUserTest() {
        try {
            facade.registerUser(new UserData("testUser","password","email"));
            assert true;
        }catch (Exception e){
            assert false;
        }
    }

    @Test
    @DisplayName("negative: register user")
    public void registerUserFail(){
        try {
            facade.registerUser(new UserData("gamesUser","password","email"));
            assert false;
        }catch (Exception e){
            assert true;
        }
    }

    @Test
    @DisplayName("positive login user")
    public void loginUserTest(){
        try{
            facade.loginUser(new LoginRequest("loggedOutUser", "password"));
            assert true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assert false;
        }

    }

    @Test
    @DisplayName("negative login user")
    public void loginUserFailTest(){
        try{
            facade.loginUser(new LoginRequest("fakeUser", "password"));
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    @Test
    @DisplayName("positive logout test")
    public void logoutUserTest(){
        try{
            facade.logoutUser(logoutToken);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    @DisplayName("negative logout test")
    public  void logoutUserFailTest(){
        try{
            facade.logoutUser(null);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    @DisplayName("create game test")
    public void createGameTest(){
        try{
            facade.createGame(seedToken, "game");
            assert true;
        } catch (Exception e) {
            assert false;
        }

    }

    @Test
    @DisplayName("failCreateGameTest")
    public void failCreateGameTest(){
        try {
            facade.createGame(seedToken, null);
                    assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    @DisplayName("list games test")
    public void listGamesTest(){
        try{
            var games = facade.listGames(seedToken);
            if(!games.games().isEmpty()){
                assert true;
            } else {
                assert false;
            };
        } catch (Exception e) {
            assert false;
        }

    }

    @Test
    @DisplayName("fail list games test")
    public void failListGamesTest(){
        try{
            facade.listGames(null);
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    @Test
    @DisplayName("join game test")
    public void joinGameTest(){
        try{
            facade.joinGame(seedToken, 1, ChessGame.TeamColor.WHITE);
            assert true;
        } catch (Exception e) {
            assert false;
        }

    }

    @Test
    @DisplayName("fail join game test")
    public void failJoinGameTest(){
        try {
            facade.joinGame(seedToken, 1, ChessGame.TeamColor.WHITE);
            assert false;
        } catch (Exception e){
            assert true;
        }
    }
}
