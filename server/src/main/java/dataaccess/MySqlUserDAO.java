package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ApiException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{

    @Override
    public boolean createUser(String username, String password, String email) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT into USERS, (username, password, email) VALUES (1,2,3)";
            var hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "username");
                ps.setString(2, "password");
                ps.setString(3, "email");
                ps.executeUpdate();
            }

        }catch(Exception e){
            throw new ApiException(500, "internal errror");
        }
        return true;
    }

    @Override
    public UserData getUserByUsername(String username) {
        UserData user;
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * from users where username=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, "username");
                try(var rs = ps.executeQuery()){
                    return readUser(rs);
                }

            }
        } catch (Exception e) {
            throw new ApiException(500, "We had a DB error");
        }
    }

    @Override
    public boolean clear() {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE users";
            try( var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ApiException(500, "failed to update");
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
}
