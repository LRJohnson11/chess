package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameUI {
    ChessGame game;
    ChessGame.TeamColor clientColor;
    private String[] columnLabels = {"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
    private String[] rowLabels =    {"", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", ""};

    private boolean running;
    private Scanner scanner = new Scanner(System.in);
    private String response = "";
    public GameUI(ChessGame game, ChessGame.TeamColor color){
        this.game = game;
        this.clientColor = color;
        this.running = true;
    }


    public void run(){
        drawBoard(new ArrayList<>());
        while(running){

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
    private void handeInput() {
        //parse the response value.
        var args = response.toLowerCase().split("\\s+");
        var command = args[0];
    }

    private void highlightLegalMoves(String position) {
        System.out.println(game.getBoard().getPiece(parseStringChessPosition(position)).getPieceType() + ", " + game.getBoard().getPiece(parseStringChessPosition(position)).getTeamColor());
        var moves = game.validMoves(parseStringChessPosition(position));
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

    }

    private void resignGame() {
    }


    private void leaveGame() {
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

    private void printBoardPiece(int i, int j) {
        if(game.getBoard().getPiece(new ChessPosition(i, 9 -j)) == null){
            System.out.print("   ");
            return;
        }
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i,9 - j));
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if(pieceColor == ChessGame.TeamColor.BLACK){
            System.out.print(SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(SET_TEXT_COLOR_YELLOW);
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
}
