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
            throw new ApiException(500, "failed to create tables");
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
                throw new ApiException(500, "failed to create a game");
            }


        } catch (Exception e) {
            throw new ApiException(500, "error creating game");
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
            throw new RuntimeException(e);
        }
        return new GetGamesResponse(responses);
    }

    @Override
    public GameData getGame(int gameId) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from game, where id = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameId);
                try(var rs = ps.executeQuery()){
                    return readGame(rs);
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "error getting game by id");
        }
    }

    @Override
    public boolean updateGame(int gameId, ChessGame.TeamColor color, String username) {
        return false;
    }

    @Override
    public boolean clear() {
        var statement = "TRUNCATE game";
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "error clearing games");
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
            throw new DataAccessException("failed to create tables");
        }


    }
}
