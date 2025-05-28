package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import server.ApiException;
import server.response.CreateGameResponse;
import server.response.GetGamesResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO{

    private Gson gson = new GsonBuilder().serializeNulls().create();

    public MySqlGameDAO()  {
        try {
            configureGamesDB();
        } catch (DataAccessException e) {
            throw new ApiException(500, "Error: failed to create tables");
        }
    }
    @Override
    public CreateGameResponse createGame(String name) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT into game (name, chess_game) values (?,?)";
            var game = gson.toJson(new ChessGame());
            try(var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,name);
                ps.setString(2,game);
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    return new CreateGameResponse(rs.getInt(1));
                }
                throw new ApiException(500, "Error: failed to create a game");
            }


        } catch (Exception e) {
            throw new ApiException(500, "Error: error creating game");
        }
    }

    @Override
    public GetGamesResponse listGames() {
        ArrayList<GameData> responses = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM game";
            try(var ps = conn.prepareStatement(statement)){
                try(var rs = ps.executeQuery()){
                    while(rs.next()) {
                        responses.add(readGame(rs));
                    }
                }
            }

        } catch (Exception e) {
            throw new ApiException(500, "Error: failed to list games");
        }
        return new GetGamesResponse(responses);
    }

    @Override
    public GameData getGame(int gameId) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from game where id = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameId);
                try(var rs = ps.executeQuery()){
                    if(rs.next()) {
                        return readGame(rs);
                    }
                    else {
                        return null;
                    }
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: error getting game by id");
        }
    }

    @Override
    public boolean updateGame(int gameId, ChessGame.TeamColor color, String username) {
        GameData game = getGame(gameId);
        if(color == ChessGame.TeamColor.BLACK && game.blackUsername() == null){
            var statement = "UPDATE game set black_username = ? where id = ?";
            joinGameForColor(statement, username, gameId);
        }
        if(color == ChessGame.TeamColor.WHITE && game.whiteUsername() == null){
            var statement = "UPDATE game set white_username = ? where id = ?";
            joinGameForColor(statement, username, gameId);
        }
        return false;
    }

    @Override
    public boolean clear() {
        var statement = "TRUNCATE TABLE game";
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: error clearing games");
        }

        return false;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        String whiteUsername = rs.getString("white_username");
        String blackUsername = rs.getString("black_username");
        String gameName = rs.getString("name");
        ChessGame gameBoard = gson.fromJson(rs.getString("chess_game"), ChessGame.class);
        return new GameData(id,whiteUsername,blackUsername,gameName,gameBoard);
    }

    private void configureGamesDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        var statement = """
                CREATE TABLE IF NOT EXISTS game (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    white_username VARCHAR(255),
                    black_username VARCHAR(255),
                    name VARCHAR(255) NOT NULL,
                    chess_game JSON NOT NULL
                );
                """;
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: failed to create tables");
        }
    }

    private void joinGameForColor(String statement, String username, int id){
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1,username);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: failed to join game for color");
        }
    }


}
