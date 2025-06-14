package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ApiException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO(){
        try{
            configureUserDB();
        } catch (DataAccessException e) {
            throw new ApiException(500, "Error: failed to make user table");
        }
    }

    @Override
    public boolean createUser(String username, String hashPassword, String email) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT into users (username, password, email) VALUES (?,?,?)";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, hashPassword);
                ps.setString(3, email);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }

        }catch(Exception e){
            throw new ApiException(500, "Error: there was an error creating the user in the DB");
        }
    }

    @Override
    public UserData getUserByUsername(String username) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from users where username = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(var rs = ps.executeQuery()){
                    if(rs.next()) {
                        return readUser(rs);
                    }
                    else{
                        return null;
                    }
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: There was an error getting a user by username");
        }
    }

    @Override
    public boolean clear() {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE TABLE users";
            try( var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "Error: failed to clear users");
        }
        return true;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email  = rs.getString("email");
        UserData user = new UserData(username,password,email);
        if(user.valid()){
            return user;
        }
        return null;
    }

    private void configureUserDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        var statement = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
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
}
