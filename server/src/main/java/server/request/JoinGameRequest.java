package server.request;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
    public boolean valid(){
        return playerColor() != null && gameID != 0;
    }
}
