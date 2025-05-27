package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public boolean valid(){
        if(gameID != 0 && whiteUsername != null && blackUsername != null && gameName != null && game != null){
            return !gameName.isBlank();
        }
        return false;
    }
}
