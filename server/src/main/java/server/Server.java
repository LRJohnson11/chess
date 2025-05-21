package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;

import java.util.Collection;
import java.util.Map;

public class Server {
    private final Gson gson = new Gson();
    private final AuthDAO localAuthDAO = new LocalAuthDAO();
    private final UserDAO localUserDAO = new LocalUserDAO();
    private final GameDAO localGameDAO = new LocalGameDAO();
    private final UserService userService = new UserService(localAuthDAO, localUserDAO);
    private final GameService gameService = new GameService(localGameDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //register a user
        Spark.post("/user", (req, res) -> {
            try {
                RegisterUserRequest request = gson.fromJson(req.body(), RegisterUserRequest.class);
                String authToken = userService.registerUser(request);


                System.out.println(request.toString());
                res.type("application/json");
                return gson.toJson(Map.of("authToken", authToken));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });
        //login
        Spark.post("/session", (req,res) -> {
            try {
                LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

                String authToken = userService.loginUser(request);
                res.type("application/json");
                res.status(200);
                return gson.toJson(Map.of("authToken", authToken));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });
        //logout
        Spark.delete("/session", (req,res) -> {
            try {
                String authToken = req.headers("authorization");
                if(userService.getAuth(authToken) == null){
                    throw new RuntimeException("user not logged in");
                }

                var result = userService.logoutUser(authToken);
                res.type("application/json");
                res.status(200);
                return "";
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        Spark.get("/game", (req,res) -> {
            System.out.println("list games");
            String authToken = req.headers("authorization");
            if(userService.getAuth(authToken) == null){
                throw new RuntimeException("user not logged in");
            }
            Collection<GameData> games = gameService.listGames();
            res.type("application/json");
            res.status(200);
            return gson.toJson(games);
        });

        Spark.post( "/game", (req, res) -> {
            System.out.println("create game");
            String authToken = req.headers("authorization");
            AuthData user = userService.getAuth(authToken);
            if(user == null){
                throw new RuntimeException("user not logged in");
            }
            CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
            int gameId = gameService.createGame(request);
            res.status(200);
            res.type("application/json");

            return gson.toJson(gameId);
        });

        Spark.put("/game", (req,res) -> {
            System.out.println("join game");
            String authToken = req.headers("authorization");
            AuthData user = userService.getAuth(authToken);
            if(user == null){
                throw new RuntimeException("user not logged in");
            }
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            gameService.joinGame(request, user.username());
            res.status(200);
            res.type("application/json");
            return "";
        });
        //clear db
        Spark.delete("/db", (req,res) -> {
            System.out.println("clearing the db");
            userService.clearUsers();
            userService.clearAuth();
            gameService.clearGames();


            res.type("application/json");
            res.status(200);
            return "";
        });


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
