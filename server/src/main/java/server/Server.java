package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.*;
import model.AuthData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterUserRequest;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;
import spark.*;

import java.util.Map;

public class Server {
    private final Gson gson = new GsonBuilder().serializeNulls().create();
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
        Spark.post("/user", (req, res) -> {
            try {
                RegisterUserRequest request = gson.fromJson(req.body(), RegisterUserRequest.class);
                AuthData user = userService.registerUser(request);
                res.type("application/json");
                return gson.toJson(user);
            } catch (ApiException e) {
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.post("/session", (req,res) -> {
            try {
                LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
                AuthData user = userService.loginUser(request);
                res.type("application/json");
                return gson.toJson(user);
            }catch (ApiException e){
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.delete("/session", (req,res) -> {
            try {
                String authToken = req.headers("authorization");
                if(userService.getAuth(authToken) == null){
                    throw new ApiException(401, "Error: unauthorized");
                }
                var result = userService.logoutUser(authToken);
                res.type("application/json");
                return "";
            } catch (ApiException e) {
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.get("/game", (req,res) -> {
            try{
            String authToken = req.headers("authorization");
            if(userService.getAuth(authToken) == null){
                throw new ApiException(401, "Error: user not logged in");
            }
            GetGamesResponse games = gameService.listGames();
            res.type("application/json");
            return gson.toJson(games);
            } catch (ApiException e) {
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.post( "/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                AuthData user = userService.getAuth(authToken);
                if (user == null) {
                    throw new ApiException(401, "Error: unauthenticated");
                }
                CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
                CreateGameResponse response = gameService.createGame(request);
                res.type("application/json");
                return gson.toJson(response);
            }catch (ApiException e) {
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.put("/game", (req,res) -> {
            try{
            String authToken = req.headers("authorization");
            AuthData user = userService.getAuth(authToken);
            if(user == null){
                throw new ApiException(401, "Error: unauthenticated");
            }
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            gameService.joinGame(request, user.username());
            res.status(200);
            res.type("application/json");
            return "";
            } catch (ApiException e) {
                res.status(e.getStatus());
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        });
        Spark.delete("/db", (req,res) -> {
            userService.clearUsers();
            userService.clearAuth();
            gameService.clearGames();
            res.type("application/json");
            return "";
        });
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
