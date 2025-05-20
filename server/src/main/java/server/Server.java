package server;

import com.google.gson.Gson;
import spark.*;

public class Server {
    private final userService

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            var message = req.body();


            System.out.println(message);
            res.type("application/json");
            return "success";
        });

        Spark.post("/session", (req,res) -> {
            System.out.println("login");
            return "login";
        });

        Spark.delete("/session", (req,res) -> {
            System.out.println("log out");
            return "logout";
        });

        Spark.get("/game", (req,res) -> {
            System.out.println("list games");
            return "List games";
        });

        Spark.post( "/game", (req, res) -> {
            System.out.println("create game");
            return "create game";
        });

        Spark.put("/game", (req,res) -> {
            System.out.println("join game");
            return "join game";
        });
        Spark.delete("/db", (req,res) -> {
            var message = req.body();
            var authorization = req.headers("Authorization");
            if(authorization.isEmpty()){
                System.out.print("user is not authenticated");
                return "Unauthorized";
            } else {
                System.out.print("user is authenticated with: ");
                System.out.println(authorization);
            }
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
