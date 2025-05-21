package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
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
            if(userService.getAuth(authToken) == null){
                throw new RuntimeException("user not logged in");
            }

            return "create game";
        });

        Spark.put("/game", (req,res) -> {
            System.out.println("join game");
            return "join game";
        });
        //clear db
        Spark.delete("/db", (req,res) -> {
            System.out.println("clearing the db");
            userService.clearUsers();
            userService.clearAuth();


            System.out.println("/user endpoint has been called");
            res.type("application/json");
            return "success";
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
