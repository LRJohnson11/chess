package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
private TeamColor teamTurn;
private ChessBoard gameBoard;

    public ChessGame() {
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(this.gameBoard.getPiece(startPosition) == null){
            return null;
        }
        TeamColor pieceColor = this.gameBoard.getPiece(startPosition).getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>(this.gameBoard.getPiece(startPosition).pieceMoves(getBoard(), startPosition));

        moves.removeIf(move -> moveInvalid(move, pieceColor));//consider removing teamturn from this funciton too

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!moveInvalid(move, this.teamTurn)){
            this.gameBoard.addPiece(move.getEndPosition(), this.gameBoard.getPiece(move.getStartPosition()));
            this.gameBoard.addPiece(move.getStartPosition(), null);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //check if King is being attacked

        //find position of this team's king
        //check all legal moves of enemy team
        //if king is in a position of any legal moves of enemy team, king is in check
        ChessBoard board = this.getBoard();
        ChessPosition kingPos = this.gameBoard.findKingForColor(teamColor);
        ChessPosition positionToCheck;
        ArrayList<ChessMove> enemyMoves = new ArrayList<>();
        for(int i = 1; i < 9; i++){
            for(int j = 1; j<9; j++){
                positionToCheck = new ChessPosition(i,j);
                if(board.getPiece(positionToCheck) != null){
                    if(board.getPiece(positionToCheck).getTeamColor() != board.getPiece(kingPos).getTeamColor()){
                        enemyMoves.addAll(board.getPiece(positionToCheck).pieceMoves(board, positionToCheck));
                    }
                }
            }
        }
        for(ChessMove enemyMove : enemyMoves){
            if(enemyMove.getEndPosition() == kingPos){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
        //collect all valid moves for this team, also grab king position.
        //collect all enemy moves and check king valid moves.
        //if king's position is safe, but no valid moves available for team, return true.
//        ArrayList<ChessMove> teamMoves = new ArrayList<>();
//        ChessPosition kingPosition = null;
//        ChessPosition positionToCheck;
//        for(int i = 1; i < 9; i++){
//            for( int j = 1; j < 9; j++){
//                positionToCheck = new ChessPosition(i,j);
//                if(gameBoard.getPiece(positionToCheck) != null){
//                    if(gameBoard.getPiece(positionToCheck).getTeamColor() == teamColor){
//                        teamMoves.addAll(gameBoard.getPiece(positionToCheck).pieceMoves(this.getBoard(),positionToCheck));
//                        if(gameBoard.getPiece(positionToCheck).getPieceType() == ChessPiece.PieceType.KING){
//                            kingPosition = new ChessPosition(i,j);
//                        }
//                    }
//                }
//            }
//        }
//        ChessPosition finalKingPosition = kingPosition;
//        teamMoves.removeIf(teamMove -> moveInvalid(teamMove, this.getBoard(), finalKingPosition));
//        return teamMoves.isEmpty();
//        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
        //check if not in check  and no valid moves
        //collect all valid moves for this team, also grab king position.
        //collect all enemy moves and check king valid moves.
        //if king's position is safe, but no valid moves available for team, return true.
//        ArrayList<ChessMove> teamMoves = new ArrayList<>();
//        ChessPosition kingPosition = null;
//        ChessPosition positionToCheck;
//        for(int i = 1; i < 9; i++){
//            for( int j = 1; j < 9; j++){
//                positionToCheck = new ChessPosition(i,j);
//                if(gameBoard.getPiece(positionToCheck) != null){
//                    if(gameBoard.getPiece(positionToCheck).getTeamColor() == teamColor){
//                        teamMoves.addAll(gameBoard.getPiece(positionToCheck).pieceMoves(this.getBoard(),positionToCheck));
//                        if(gameBoard.getPiece(positionToCheck).getPieceType() == ChessPiece.PieceType.KING){
//                            kingPosition = new ChessPosition(i,j);
//                        }
//                    }
//                }
//            }
//        }
//        ChessPosition finalKingPosition = kingPosition;
//        teamMoves.removeIf(teamMove -> moveInvalid(teamMove, this.getBoard(), this.teamTurn));
//        return teamMoves.isEmpty();
        //for each team move, if it would put the king in danger, remove it.
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }

    private boolean moveInvalid(ChessMove move, TeamColor color){

        //create copy of the board
        ChessBoard board = this.gameBoard.copy();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        ChessPosition positionToCheck;
        ArrayList<ChessMove> enemyMoves = new ArrayList<>();
        for(int i = 1; i < 9; i++){
            for(int j = 1; j<9; j++){
                positionToCheck = new ChessPosition(i,j);
                if(board.getPiece(positionToCheck) != null){
                    if(board.getPiece(positionToCheck).getTeamColor() != color){
                        enemyMoves.addAll(board.getPiece(positionToCheck).pieceMoves(board, positionToCheck));
                    }
                }
            }
        }
        ChessPosition kingPos = board.findKingForColor(color);
        //if any move endangers the king, move is invalid
        for(ChessMove enemyMove : enemyMoves){
           if(enemyMove.getEndPosition().equals(kingPos)){
               return true;
           }
        }
        return false;
    }

    private boolean hasValidMoves(TeamColor color){
        ChessPosition kingPos = this.gameBoard.findKingForColor(color);
        //find king
        //get all moves for this team
        //get all moves for enemy
        //for each move this
        return true;
    }
}
