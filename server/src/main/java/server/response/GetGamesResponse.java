package server.response;

import model.GameData;

import java.util.Collection;

public record GetGamesResponse(Collection<GameData> games) {
}
