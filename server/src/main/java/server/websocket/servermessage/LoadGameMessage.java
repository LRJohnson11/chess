package server.websocket.servermessage;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        this.game = game;
    }
}
