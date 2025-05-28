package dataaccess;

import model.AuthData;
import server.ApiException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO(){
        try {
            configureAuthDB();
        } catch (DataAccessException e) {
            throw new ApiException(500, "failed to make auth tables");
        }
    }
    @Override
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT into auth (username, auth_token) values (?,?)";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                ps.setString(2, authToken);
                ps.executeUpdate();

            }
        } catch (Exception e) {
            throw new ApiException(500, "We had a DB error");
        }
        return new AuthData(username,authToken);
    }

    @Override
    public boolean deleteAuth(String username) {

        try(var conn = DatabaseManager.getConnection()){
            var statement = "DELETE from auth where username = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, "username");
                ps.executeUpdate();

            }
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting auth");
        }
        return true;
    }

    @Override
    public AuthData getAuth(String authToken) {

        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from auth where auth_token = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try(var rs = ps.executeQuery()){
                    return readAuth(rs);
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "Error getting Auth");
        }
    }

    @Override
    public boolean clear() {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE TABLE auth";
            try( var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: failed to update");
        }
        return true;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        if(rs.next()) {
            String username = rs.getString("username");
            String authToken = rs.getString("auth_token");
            AuthData auth = new AuthData(username, authToken);
            if (auth.valid()) {
                return auth;
            }
            return null;
        }
        return null;
    }

    private void configureAuthDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        var statement = """
                CREATE TABLE IF NOT EXISTS auth (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    auth_token VARCHAR(255) NOT NULL
                );
                """;
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: failed to create tables");
        }


    }
}
