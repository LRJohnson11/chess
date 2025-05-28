package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class UserDAOTests {
    private final UserData user = new UserData("user", "pass", "email");

    private final UserDAO userDAO = new MySqlUserDAO();

    @Test
    @DisplayName("create user test")
    public void createUsertest(){
        assert userDAO.createUser(user.username(),user.password(),user.email());

    }

    @Test
    @DisplayName("fail to create user test")
    public void  failCreateUserTest(){
        try {
            userDAO.createUser(user.username(), user.password(), user.email());
            userDAO.createUser(user.username(), user.password(), user.email());
            assert false;
        } catch (Exception e){
            assert true;
        }


    }

    @Test
    @DisplayName("get user test")
    public void getUserTest(){
        userDAO.createUser(user.username(),user.password(),user.email());
        var result = userDAO.getUserByUsername(user.username());
        assert user.password().equals(result.password());

    }

    @Test
    @DisplayName("fail to get user test")
    public void failGetUserTest(){
        var result = userDAO.getUserByUsername(user.username());
        assert result == null;

    }

    @Test
    @DisplayName("clear users test")
    public void clearUsersTest(){
        userDAO.createUser(user.username(),user.password(),user.email());
        userDAO.clear();
        assert userDAO.createUser(user.username(),user.password(),user.email());

    }

    @BeforeEach
    public void resetusers(){
        userDAO.clear();
    }
}
