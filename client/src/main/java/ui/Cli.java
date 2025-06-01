package ui;

import java.util.Scanner;

import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Cli {
    private boolean running = true;
    private String authToken;
    private String response = "";
    private Scanner scanner = new Scanner(System.in);



    public void run(){
        System.out.println("Welcome to 240 chess! login or register to play, or type help for command information");
        while(running){
            if(authToken == null){
                System.out.print("LOGGED OUT");
            }
            else{
                System.out.print("LOGGED IN");
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

    private void registerUser(){
        System.out.print("register user!");
        //calls server endpoint to register a user, response information is propogated
    }

    private void loginUser(){
        System.out.println("login user!");
    }
    private void help(){
        if(authToken == null){
            System.out.print(SET_TEXT_COLOR_BLUE + "register <username> <password> <email> ");
            System.out.println(SET_BG_COLOR_DARK_GREY + "- to create an account");
            System.out.print(SET_TEXT_COLOR_BLUE + "login <username> <password> ");
            System.out.println(SET_BG_COLOR_DARK_GREY + "- to log in to your account");

        } else{
            System.out.print(SET_TEXT_COLOR_BLUE + "logout ");
            System.out.println(SET_BG_COLOR_DARK_GREY + "- log out of account");
            System.out.print(SET_TEXT_COLOR_BLUE + "create <name> ");
            System.out.println(SET_BG_COLOR_DARK_GREY + "- creates a new chess game");
        }
        System.out.print(SET_TEXT_COLOR_BLUE + "quit ");
        System.out.println(SET_BG_COLOR_DARK_GREY + "- close the application");
        System.out.print(SET_TEXT_COLOR_BLUE + "help ");
        System.out.println(SET_BG_COLOR_DARK_GREY + "- display available commands");
    }
    private void handeInput(){
        //parse the response value.
        var args = response.split("\\s+");
        var command = args[0];
        switch (command){
            case "register": registerUser();
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
