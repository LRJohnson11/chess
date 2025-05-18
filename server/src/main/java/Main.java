import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var port = 2020;
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}