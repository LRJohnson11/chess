package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.request.RegisterUserRequest;

import java.util.Objects;

public class AuthDAOTests {
    private final MySqlAuthDAO authDAO = new MySqlAuthDAO();

    @BeforeEach
    public void resetDb(){
        authDAO.clear();
    }

    @Test
    @DisplayName("create auth in database")
    public void createUserInDB(){
        try {

            AuthData auth = authDAO.createAuth(new AuthData("username", "abcde"));
            assert true;
        } catch (Exception e) {
            assert false;
        }


    }

    @Test
    @DisplayName("fail to make auth in database")
    public void failToMakeAuthInDb(){
        try{
            AuthData auth = authDAO.createAuth(new AuthData("username", "abcde"));
            AuthData newAuth = authDAO.createAuth(new AuthData("username", "abcde"));

            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    @DisplayName("delete auth in db")
    public void deleteAuthInDb(){
        try {
            var auth = authDAO.createAuth(new AuthData("username", "abcde"));
            authDAO.deleteAuth(auth);
            assert true;
        } catch (Exception e){
            assert false;
        }

    }

    @Test
    @DisplayName("fail to delte auth in db")
    public  void failToDeleteAuthInDb(){
        assert !authDAO.deleteAuth(new AuthData("user", "abcde"));
    }

    @Test
    @DisplayName("get auth test")
    public void getAuthTest(){
        var auth = authDAO.createAuth(new AuthData("user", "abcdef"));
        var result = authDAO.getAuth(auth.authToken());

        assert Objects.equals(result.username(), auth.username());

    }

    @Test
    @DisplayName("fail to get authTest")
    public void failToGetAuthTest(){
        var result = authDAO.getAuth("abcdef");
        assert result == null;

    }

    @Test
    @DisplayName("clear auth test")
    public void clearAuthTest(){
            var auth = authDAO.createAuth(new AuthData("clearme", "abcdefg"));
            authDAO.clear();
            var newAuth = authDAO.getAuth(auth.authToken());
            if(newAuth == null){
                assert true;
            } else {
                assert false;
            }

    }
}
