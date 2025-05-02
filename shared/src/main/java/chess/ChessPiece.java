package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (this.type) {
            case PAWN -> getPawnMoves(board, myPosition);
            case KING -> getKingMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case BISHOP -> getBishopMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
        };
    }


    private boolean checkPawnInitialMove(ChessPosition pos){
        if(this.type != PieceType.PAWN){
            return false;
        }
        if((pos.getRow() == 2 && this.pieceColor == ChessGame.TeamColor.WHITE) || (pos.getRow() == 7 && this.pieceColor == ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }

    private boolean checkPawnPromotion(ChessPosition pos){
        if(this.type != PieceType.PAWN){
            return false;
        }
        if((pos.getRow() == 8 && this.pieceColor == ChessGame.TeamColor.WHITE) || (pos.getRow() == 1 && this.pieceColor == ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }
    private boolean checkValidPosition(ChessPosition pos){
        return pos.getColumn() >= 1 && pos.getColumn() <= 8 && pos.getRow() >=1 && pos.getRow() <= 8;
    }

    private Collection<ChessMove>getPawnMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int directionSign = 1;
        if(this.pieceColor == ChessGame.TeamColor.BLACK){
            directionSign = -1;
        }

        ChessPosition positionToCheck = new ChessPosition(pos.getRow() + (directionSign), pos.getColumn());
        //straight moves logic
        if(board.getPiece(positionToCheck) == null){
            if(checkPawnPromotion(positionToCheck)) {
                moves.add(new ChessMove(pos, positionToCheck, null));//figure out how to do promotion
            }else {
                moves.add(new ChessMove(pos, positionToCheck, null));
            }
            if(checkPawnInitialMove(pos)){
                positionToCheck = new ChessPosition(pos.getRow() + (directionSign * 2), pos.getColumn());
                if(board.getPiece(positionToCheck) == null){
                    moves.add(new ChessMove(pos, positionToCheck, null));
                }
            }
        }
        //capture logic
        positionToCheck = new ChessPosition(pos.getRow()+directionSign, pos.getColumn() + 1);
        if(checkValidPosition(positionToCheck)) {
            if (board.getPiece(positionToCheck) != null) {
                if (board.getPiece(positionToCheck).pieceColor != this.pieceColor) {
                    if (checkPawnPromotion(positionToCheck)) {
                        moves.add(new ChessMove(pos, positionToCheck, null));//figure out how to do promotion
                    } else {
                        moves.add(new ChessMove(pos, positionToCheck, null));
                    }
                }
            }
        }

        positionToCheck = new ChessPosition(pos.getRow()+directionSign, pos.getColumn() - 1);
        if(checkValidPosition(positionToCheck)) {
            if (board.getPiece(positionToCheck) != null) {
                if (board.getPiece(positionToCheck).pieceColor != this.pieceColor) {
                    if (checkPawnPromotion(positionToCheck)) {
                        moves.add(new ChessMove(pos, positionToCheck, null));//figure out how to do promotion
                    } else {
                        moves.add(new ChessMove(pos, positionToCheck, null));
                    }
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove>getKingMoves(ChessBoard board, ChessPosition pos){
        System.out.println("get king moves");
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    private Collection<ChessMove>getRookMoves(ChessBoard board, ChessPosition pos){
        System.out.println("get rook moves");
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    private Collection<ChessMove>getQueenMoves(ChessBoard board, ChessPosition pos){
        System.out.println("get queen moves");
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    private Collection<ChessMove>getBishopMoves(ChessBoard board, ChessPosition pos){
        System.out.println("get bishop moves");
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    private Collection<ChessMove>getKnightMoves(ChessBoard board, ChessPosition pos){
        System.out.println("get knight moves");
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }



}
