package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameUI implements NotificationHandler {
    GameData gameData;
    ChessGame.TeamColor clientColor;
    private String[] columnLabels = {"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
    private String[] rowLabels =    {"", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", ""};
    private WebsocketFacade ws;
    private String authToken;
    private boolean running;
    private Scanner scanner = new Scanner(System.in);
    private String response = "";


    public GameUI(GameData game, ChessGame.TeamColor color,String authToken) throws Exception {
        this.ws = new WebsocketFacade("http://localhost:2001", this);
        this.gameData = game;
        this.clientColor = color;
        this.running = true;
        this.authToken = authToken;

        ws.connect(authToken,game.gameID());
    }


    public void run(){
        drawBoard(new ArrayList<>());
        while(running){

            System.out.print(SET_TEXT_COLOR_WHITE + " >>>");
            response = scanner.nextLine().trim();

            if(!response.isEmpty()){
                try {
                    handleInput();
                }catch (Throwable e){
                    System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
                }
            }
        }
        //runs command line interface
    }
    private void handleInput() {
        // Parse the response value.
        var args = response.toLowerCase().split("\\s+");
        var command = args[0];

        switch (command) {
            case "help": help();
                break;

            case "redraw":
                redrawBoard();
                break;

            case "leave":
                leaveGame();
                break;

            case "move":
                if (args.length != 3) {
                    System.out.println("Usage: move <from> <to> (e.g., move e2 e4)");
                } else {
                    String from = args[1];
                    String to = args[2];
                    makeMove(from, to);
                }
                break;

            case "resign":
                System.out.println("Are you sure you want to resign? (yes/no)");
                var confirmation = scanner.nextLine().toLowerCase();
                if (confirmation.equals("yes")) {
                    resignGame();
                } else {
                    System.out.println("Resignation failed.");
                }
                break;

            case "highlight":
                if (args.length < 2) {
                    System.out.println("Usage: highlight <position> (e.g., highlight e2)");
                } else {
                    String position = args[1];
                    highlightLegalMoves(position);
                }
                break;

            default:
                System.out.println("Unknown command. Type 'help' for a list of available commands.");
                break;
        }
    }

    private void highlightLegalMoves(String position) {
        System.out.println(gameData.game().getBoard().getPiece(parseStringChessPosition(position)).getPieceType() + ", " + gameData.game().getBoard().getPiece(parseStringChessPosition(position)).getTeamColor());
        var moves = gameData.game().validMoves(parseStringChessPosition(position));
        ArrayList<ChessPosition> positions = new ArrayList<>();
        if(moves != null) {
            for (ChessMove move : moves) {
                System.out.println("move from: " + move.getStartPosition().getRow() +move.getStartPosition().getColumn());
                System.out.println("move to: " + move.getEndPosition().getRow() + move.getEndPosition().getColumn());
                positions.add(move.getEndPosition());
            }
        }
        drawBoard(positions);
    }

    private void makeMove(String from, String to){
        ChessPosition start = parseStringChessPosition(from);
        ChessPosition end = parseStringChessPosition(to);

        ChessPiece piece = gameData.game().getBoard().getPiece(start);
        if(piece == null){
            System.out.println("invalid move. move cancelled");
        }
        //if pawn can promote, query for a promotion piece.
        ChessPiece.PieceType promotionPiece = null;
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            if (end.getRow() == 1 || end.getRow() == 8) {
                System.out.println("Piece can promote! Enter one of the following:");
                System.out.println("Q - Queen");
                System.out.println("N - Knight");
                System.out.println("B - Bishop");
                System.out.println("R - Rook");

                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toUpperCase();

                switch (choice) {
                    case "Q":
                        System.out.println("You chose Queen.");
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                        break;
                    case "N":
                        System.out.println("You chose Knight.");
                        promotionPiece = ChessPiece.PieceType.KNIGHT;
                        break;
                    case "B":
                        System.out.println("You chose Bishop.");
                        promotionPiece = ChessPiece.PieceType.BISHOP;
                        break;
                    case "R":
                        System.out.println("You chose Rook.");
                        promotionPiece = ChessPiece.PieceType.ROOK;
                        break;
                    default:
                        System.out.println("Invalid choice. move cancelled.");
                        return;
                }
            }
        }

        ChessMove move = new ChessMove(start,end,promotionPiece);
        if(piece.getTeamColor() != clientColor){
            throw new RuntimeException("you can't move enemy pieces!");
        }
        if(piece.getTeamColor() != gameData.game().getTeamTurn()){
            throw new RuntimeException("you can't move a piece when it is not your turn!");
        }
        ws.makeMove(authToken, gameData.gameID(), move);

    }

    private void resignGame() {
        ws.resign(authToken, gameData.gameID());
    }


    private void leaveGame() {
        ws.leave(authToken, gameData.gameID());
        running = false;
    }

    private void redrawBoard() {
        drawBoard(new ArrayList<>());
    }

    private void drawBoard(Collection<ChessPosition> validPositions) {
        if(clientColor == ChessGame.TeamColor.WHITE) {
            drawGameBoardWhite(validPositions);
        } else {
            drawGameBoardBlack(validPositions);
        }
    }

    private void drawGameBoardWhite(Collection<ChessPosition> validPositions) {
        for(int i = 9; i >= 0; i--) {
            for(int j = 9; j >= 0; j--) {
                if(i == 0 || i == 9 || j == 0 || j == 9) {
                    System.out.print(SET_BG_COLOR_DARK_GREY);
                    System.out.print(SET_TEXT_COLOR_WHITE);
                    if(i == 0 || i == 9) {
                        System.out.print(columnLabels[j]);
                    }
                    if(j == 0 || j == 9) {
                        System.out.print(rowLabels[i] + (j == 0 ? "\n" : ""));
                    }
                } else {
                    if(validPositions.contains(new ChessPosition(i,9-j))) {
                        System.out.print(SET_BG_COLOR_YELLOW);
                    } else if(i % 2 == j % 2) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        System.out.print(SET_BG_COLOR_BLACK);
                    }
                    printBoardPiece(i, j);
                }
            }
        }
    }



    private void drawGameBoardBlack(Collection<ChessPosition> validPositions) {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(i == 0 || i == 9 || j == 0 || j == 9) {
                    System.out.print(SET_BG_COLOR_DARK_GREY);
                    System.out.print(SET_TEXT_COLOR_WHITE);
                    if(i == 0 || i == 9) {
                        System.out.print(columnLabels[j]);
                    }
                    if(j == 0 || j == 9) {
                        System.out.print(rowLabels[i] + (j == 9 ? "\n" : ""));
                    }
                } else {
                    if(validPositions.contains(new ChessPosition(i,9 - j))) {
                        System.out.print(SET_BG_COLOR_YELLOW);
                    } else if(i % 2 == j % 2) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        System.out.print(SET_BG_COLOR_BLACK);
                    }
                    printBoardPiece(i, j);
                }
            }
        }
    }

    private ChessPosition parseStringChessPosition(String position){
        if(position.length() != 2){
            throw new RuntimeException("Chess position string invalid. Expects two characters (e.g., 'e5').");
        }

        char columnChar = Character.toLowerCase(position.charAt(0)); // 'a' to 'h'
        char rowChar = position.charAt(1); // '1' to '8'

        if(columnChar < 'a' || columnChar > 'h'){
            throw new RuntimeException("Invalid columncharacter. Must be a-h.");
        }

        if(rowChar < '1' || rowChar > '8'){
            throw new RuntimeException("Invalid row character. Must be 1-8.");
        }

        int column = columnChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row, column);
    }






    private void printBoardPiece(int i, int j) {
        if(gameData.game().getBoard().getPiece(new ChessPosition(i, 9 -j)) == null){
            System.out.print("   ");
            return;
        }
        ChessPiece piece = gameData.game().getBoard().getPiece(new ChessPosition(i,9 - j));
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if(pieceColor == ChessGame.TeamColor.BLACK){
            System.out.print(SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(SET_TEXT_COLOR_WHITE);
        }
        ChessPiece.PieceType type = piece.getPieceType();

        switch (type){
            case KING -> System.out.print(" K ");
            case QUEEN -> System.out.print(" Q ");
            case BISHOP -> System.out.print(" B ");
            case KNIGHT -> System.out.print(" N ");
            case ROOK -> System.out.print(" R ");
            case PAWN -> System.out.print(" P ");
        }
    }

    private void help(){
        System.out.print(SET_TEXT_COLOR_BLUE + "help ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- display available commands.");
        System.out.print(SET_TEXT_COLOR_BLUE + "redraw ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + " - redraw the game board.");
        System.out.print(SET_TEXT_COLOR_BLUE + "leave ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- leave the game and return to the previous UI");
        System.out.print(SET_TEXT_COLOR_BLUE + "move <from> <to> ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- make a move (e.g. move a2 a4).");
        System.out.print(SET_TEXT_COLOR_BLUE + "resign ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- resign from the current game.");
        System.out.print(SET_TEXT_COLOR_BLUE + "highlight <position ");
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "- Highlight all legal moves for the piece at the given position");
    }

    @Override
    public void loadGame(LoadGameMessage loadGame) {
        gameData = loadGame.getGame();
        System.out.print("\n");
        redrawBoard();
    }

    @Override
    public void notify(NotificationMessage notification) {
        System.out.println("\n" + notification.getMessage());
        System.out.print(" >>>");

    }

    @Override
    public void showError(ErrorMessage error) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + error.getErrorMessage());
        System.out.print(SET_TEXT_COLOR_WHITE + " >>>");

    }
}
