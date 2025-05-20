package model;

import chess.ChessGame;

public class GameData {
    private final int gameId;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public GameData(int gameId, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameId = gameId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameId() {
        return gameId;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame() {
        return game;
    }
}
