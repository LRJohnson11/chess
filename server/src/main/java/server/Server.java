package server;

import com.google.gson.Gson;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            var message = req.body();
            var authorization = req.headers("Authorization");
            if(authorization.isEmpty()){
                System.out.println("user is not authenticated");
                return "Unauthorized";
            }else{
                System.out.print("user is authenticated with: ");
                System.out.println(authorization);
            }
            System.out.println("/user endpoint has been called");
            res.type("application/json");
            return "success";
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
