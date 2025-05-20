import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var port = 2000;
        var server = new Server().run(port);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}