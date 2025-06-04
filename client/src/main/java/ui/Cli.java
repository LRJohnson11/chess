package ui;

import model.AuthData;
import model.GameData;
import model.UserData;
import requests.LoginRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Cli {
    private boolean running = true;
    private boolean loggedIn = false;
    private String authToken;
    private String response = "";
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:2001");
    private Map<Integer, Integer> games = new HashMap<>();




    public void run(){
        System.out.println(SET_TEXT_COLOR_WHITE + "Welcome to 240 chess! login or register to play, or type help for command information");
        while(running){
            if(loggedIn){
                System.out.print(SET_TEXT_COLOR_WHITE + "LOGGED IN");
            }
            else{
                System.out.print(SET_TEXT_COLOR_WHITE + "LOGGED OUT");
            }
            System.out.print(" >>>");
            response = scanner.nextLine().trim();

            if(!response.isEmpty()){
                try {
                    handeInput();
                }catch (Throwable e){
                    System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
                }
            }
        }
        //runs command line interface
    }

    private void registerUser(String[] args){
        //calls server endpoint to register a user, response information is propogated
        if(args.length != 4){
            throw new RuntimeException("expected 4 arguments, received " + args.length);
        }
        try {
            AuthData auth = server.registerUser(new UserData(args[1], args[2], args[3]));
            if(auth.valid()){
                authToken = auth.authToken();
                loggedIn = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void loginUser(String[] args){
        if(args.length !=3){
            throw new RuntimeException("expected 3 arguments, received " + args.length);
        }
        try{
            AuthData auth = server.loginUser(new LoginRequest(args[1], args[2]));
            if(auth.valid()){
                authToken = auth.authToken();
                loggedIn = true;
            }

        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void logoutUser() {
        authorizedOrThrow();
        try{
            server.logoutUser(authToken);
            authToken = null;
            loggedIn = false;
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    private void createGame(String[] args){
        authorizedOrThrow();
        if(args.length !=2){
            throw new RuntimeException("Expected 2 arguments, received " + args.length);
        }
        try {
            CreateGameResponse res = server.createGame(authToken, args[1]);
            System.out.println("created a new game: "  + args[1] + " id: " + res.gameID() + ".");
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    private void listGames() {
        authorizedOrThrow();

        try{
            ListGamesResponse res = server.listGames(authToken);
            System.out.println("games:");
            for(GameData game : res.games()){
                games.put(res.games().indexOf(game) + 1, game.gameID());
                System.out.print(SET_TEXT_COLOR_GREEN + (res.games().indexOf(game) + 1));
                System.out.print( "> game: " + game.gameName() + " ");
                System.out.print(" White user: " + (game.whiteUsername() != null ? game.whiteUsername(): "none"));
                System.out.println(" Black user: " + (game.whiteUsername() != null ? game.blackUsername(): "none"));
            }


        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    private void joinGame(String[] args) {
        authorizedOrThrow();
        try{
            server.joinGame(authToken, args);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void observeGame(String[] args) {
        authorizedOrThrow();

    }

    private void help(){
        if(!loggedIn){
            System.out.print(SET_TEXT_COLOR_BLUE + "register <username> <password> <email> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- to create an account");
            System.out.print(SET_TEXT_COLOR_BLUE + "login <username> <password> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- to log in to your account");

        } else{
            System.out.print(SET_TEXT_COLOR_BLUE + "create <name> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- creates a new chess game");
            System.out.print(SET_TEXT_COLOR_BLUE + "list ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- all games found on server");
            System.out.print(SET_TEXT_COLOR_BLUE + "join <game id> <White/Black>");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- a game");
            System.out.print(SET_TEXT_COLOR_BLUE + "observe <game id> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- a game");
            System.out.print(SET_TEXT_COLOR_BLUE + "logout ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- log out of account");

        }
        System.out.print(SET_TEXT_COLOR_BLUE + "quit ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- close the application");
        System.out.print(SET_TEXT_COLOR_BLUE + "help ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- display available commands");
    }
    private void handeInput(){
        //parse the response value.
        var args = response.toLowerCase().split("\\s+");
        var command = args[0];
        switch (command){
            case "register": registerUser(args);
                break;

            case "login" : loginUser(args);
                break;

            case "logout": logoutUser();
                break;

            case "create" : createGame(args);
                break;
            case "list" : listGames();
            break;

            case "join" : joinGame(args);
            break;

            case "observe": observeGame(args);
            break;

            case "quit" : running = false;
                logoutUser();
                break;

            case "help" : help();
                break;

            default:
                throw new RuntimeException("invalid command");

        }
    }

    private void authorizedOrThrow(){
        if(authToken == null){
            throw new RuntimeException("Unauthorized. Please log in");
        }
    }
}
