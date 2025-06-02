package ui;

import model.AuthData;
import model.UserData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Cli {
    private boolean running = true;
    private boolean loggedIn = false;
    private String authToken;
    private String response = "";
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:2001");




    public void run(){
        System.out.println("Welcome to 240 chess! login or register to play, or type help for command information");
        while(running){
            if(loggedIn){
                System.out.print("LOGGED IN");
            }
            else{
                System.out.print("LOGGED OUT");
            }
            System.out.print(" >>>");
            response = scanner.nextLine().trim();

            if(!response.isEmpty()){
                try {
                    handeInput();
                }catch (Throwable e){
                    System.out.println(e.getMessage());
                }
            }
        }
        //runs command line interface
    }

    private void registerUser(String[] args){
        System.out.print("register user!");
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

    private void loginUser(){
        System.out.println("login user!");
    }

    private void logoutUser() {
        System.out.println("logout");
    }

    private void createGame(){
        System.out.println("create");
    }
    private void help(){
        //fixme this has backwards login logic
        if(loggedIn){
            System.out.print(SET_TEXT_COLOR_BLUE + "register <username> <password> <email> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- to create an account");
            System.out.print(SET_TEXT_COLOR_BLUE + "login <username> <password> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- to log in to your account");

        } else{
            System.out.print(SET_TEXT_COLOR_BLUE + "logout ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- log out of account");
            System.out.print(SET_TEXT_COLOR_BLUE + "create <name> ");
            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- creates a new chess game");
        }
        System.out.print(SET_TEXT_COLOR_BLUE + "quit ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- close the application");
        System.out.print(SET_TEXT_COLOR_BLUE + "help ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- display available commands");
    }
    private void handeInput(){
        //parse the response value.
        var args = response.split("\\s+");
        var command = args[0];
        switch (command){
            case "register": registerUser(args);
                break;

            case "login" : loginUser();
                break;

            case "logout": logoutUser();
            break;

            case "create" : createGame();

            case "quit" : running = false;
                break;

            case "help" : help();
                break;

            default:
                throw new RuntimeException("invalid command");

        }
    }
}
