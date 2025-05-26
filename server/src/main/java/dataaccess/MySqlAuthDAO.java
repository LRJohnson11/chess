package dataaccess;

import model.AuthData;
import server.ApiException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{
    @Override
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT into auth (username, authToken) values (?,?)";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, "username");
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
            var statement = "DELETE from auth, where username = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, "username");
                ps.executeUpdate();

            }
        } catch (Exception e) {
            throw new ApiException(500, "We had a DB error");
        }
        return true;
    }

    @Override
    public AuthData getAuth(String authToken) {

        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from auth, where authToken = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try(var rs = ps.executeQuery()){
                    return readAuth(rs);
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "We had a DB error");
        }
        return null;
    }

    @Override
    public boolean clear() {
        return false;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String authToken = rs.getString("authToken");
        AuthData auth = new AuthData(username,authToken);
        if(auth.valid())
    }
}
