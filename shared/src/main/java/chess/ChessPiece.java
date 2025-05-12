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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (this.type){
            case KING -> {
                return getKingMoves(board, myPosition);
            }
            case PAWN -> {return getPawnMoves(board, myPosition);}
            case ROOK -> {return getRookMoves(board, myPosition);}
            case BISHOP -> {return getBishopMoves(board,myPosition);}
            case QUEEN -> {return getQueenMoves(board,myPosition);}
            case KNIGHT -> {return getKnightMoves(board,myPosition);}
        }
        return null;
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[] dx = {-1, 0, 1};
        int[] dy = {-1,0,1};
        for(int x : dx){
            for(int y: dy){
                ChessPosition positionToCheck = new ChessPosition(pos.getRow() + y, pos.getColumn() + x);
                if(checkValidPosition(positionToCheck)){
                    if(board.getPiece(positionToCheck) == null){
                        moves.add(new ChessMove(pos, positionToCheck, null));
                    } else if(board.getPiece(positionToCheck).getTeamColor() != this.pieceColor){
                        moves.add(new ChessMove(pos, positionToCheck, null));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};
        for(int i = 0; i < 4; i++){
            int dist = 1;
            while(true){
                ChessPosition positionToCheck = new ChessPosition(pos.getRow() + dy[i] * dist, pos.getColumn() + dx[i] * dist);
                if(!checkValidPosition(positionToCheck)){
                    break;
                }

                if(board.getPiece(positionToCheck) == null){
                    moves.add(new ChessMove(pos,positionToCheck,null));
                } else if(board.getPiece(positionToCheck).getTeamColor() != this.pieceColor){
                    moves.add(new ChessMove(pos, positionToCheck, null));
                    break;
                } else {
                    break;
                }
                dist +=1;
            }
        }
        return moves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[] dx = {-1,1};
        int[] dy = {-1,1};
        for(int x: dx){
            for(int y: dy){
                int dist = 1;
                while(true){
                    ChessPosition positionToCheck = new ChessPosition(pos.getRow() +y*dist, pos.getColumn() + x*dist);
                    if(!checkValidPosition(positionToCheck)){
                        break;
                    }
                    if(board.getPiece(positionToCheck) == null){
                        moves.add(new ChessMove(pos, positionToCheck, null));
                    }else if( board.getPiece(positionToCheck).getTeamColor() != this.pieceColor){
                        moves.add(new ChessMove(pos, positionToCheck, null));
                        break;
                    }else {
                        break;
                    }
                    dist+=1;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = 1;
        int[] dx = {-1,1};
        if(this.pieceColor == ChessGame.TeamColor.BLACK){
            direction = -1;
        }
        ChessPosition positionToCheck = new ChessPosition(pos.getRow() +direction, pos.getColumn());
        if(board.getPiece(positionToCheck) == null){
            if(pawnCanPromote(positionToCheck)){
                moves.add(new ChessMove(pos, positionToCheck, PieceType.QUEEN));
                moves.add(new ChessMove(pos, positionToCheck, PieceType.BISHOP));
                moves.add(new ChessMove(pos, positionToCheck, PieceType.ROOK));
                moves.add(new ChessMove(pos, positionToCheck, PieceType.KNIGHT));
            }
            else{
                moves.add(new ChessMove(pos, positionToCheck, null));
            }
            if(checkPawnFirstMove(pos)){
                positionToCheck = new ChessPosition(pos.getRow()+ 2*direction, pos.getColumn());
                if(board.getPiece(positionToCheck) == null){
                    moves.add(new ChessMove(pos, positionToCheck, null));
                }
            }
        }
        for(int x : dx){
            positionToCheck = new ChessPosition(pos.getRow() + direction, pos.getColumn() + x);
            if(checkValidPosition(positionToCheck)){
                if(board.getPiece(positionToCheck) != null){
                    if(board.getPiece(positionToCheck).getTeamColor() != this.pieceColor){
                        if(pawnCanPromote(positionToCheck)){
                            moves.add(new ChessMove(pos, positionToCheck, PieceType.QUEEN));
                            moves.add(new ChessMove(pos, positionToCheck, PieceType.BISHOP));
                            moves.add(new ChessMove(pos, positionToCheck, PieceType.ROOK));
                            moves.add(new ChessMove(pos, positionToCheck, PieceType.KNIGHT));
                        }else {
                            moves.add(new ChessMove(pos, positionToCheck, null));
                        }
                    }
                }
            }

        }

        return moves;
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(getBishopMoves(board, pos));
        moves.addAll(getRookMoves(board,pos));
        return moves;
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[] dx = {1,2, 2, 1,-1,-2,-2,-1};
        int[] dy = {2,1,-1,-2,-2,-1, 1, 2};
        for(int i = 0; i < 8; i++){
            ChessPosition positionToCheck = new ChessPosition(pos.getRow() + dy[i], pos.getColumn()+dx[i]);
            if(checkValidPosition(positionToCheck)){
                if(board.getPiece(positionToCheck) == null){
                    moves.add(new ChessMove(pos,positionToCheck, null));
                }
                else if(board.getPiece(positionToCheck).getTeamColor() != this.getTeamColor()){
                    moves.add(new ChessMove(pos, positionToCheck, null));
                }
            }
        }
        return moves;
    }

    private boolean checkValidPosition(ChessPosition pos){
        return pos.getRow() >=1 && pos.getRow() <=8 && pos.getColumn() >=1 && pos.getColumn() <=8;
    }

    private boolean checkPawnFirstMove(ChessPosition pos){
        return (pos.getRow() ==2 && this.pieceColor == ChessGame.TeamColor.WHITE) || (pos.getRow() == 7 && this.pieceColor == ChessGame.TeamColor.BLACK);
    }

    private boolean pawnCanPromote(ChessPosition pos){
        return (pos.getRow() ==8 && this.pieceColor == ChessGame.TeamColor.WHITE) || (pos.getRow() == 1 && this.pieceColor == ChessGame.TeamColor.BLACK);
    }
}
