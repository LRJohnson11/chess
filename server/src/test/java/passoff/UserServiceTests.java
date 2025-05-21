package passoff;

import dataaccess.AuthDAO;
import dataaccess.LocalAuthDAO;
import dataaccess.LocalUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.UserService;
import server.apiException;
import server.request.LoginRequest;
import server.request.RegisterUserRequest;

import java.util.Objects;

public class UserServiceTests {
    UserDAO userDao = new LocalUserDAO();
    AuthDAO authDao = new LocalAuthDAO();
    UserService userService = new UserService(authDao,userDao);

    @Test
    @DisplayName("Register user test")
    public void registerUserTestSuccessful() {
        RegisterUserRequest request = new RegisterUserRequest("user","pass", "email@email.com");
        AuthData auth = userService.registerUser(request);

        assert Objects.equals(request.username(), auth.username());
    }

    @Test
    @DisplayName("Register duplicate user")
    public void registerDuplicateUserTest() {
        try {
            RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
            AuthData auth = userService.registerUser(request);
            AuthData duplicateAuth = userService.registerUser(request);

            assert false;
        } catch (apiException e){
            assert true;
        }

    }

    @Test
    @DisplayName("loginUserTest")
    public void loginUserTest() {
        RegisterUserRequest request = new RegisterUserRequest("user","pass", "email@email.com");
        AuthData auth = userService.registerUser(request);

        AuthData loginData = userService.loginUser(new LoginRequest("user", "pass"));

        if(!Objects.equals(loginData.authToken(), auth.authToken())){
            assert true;
        } else assert false;

    }

    @Test
    @DisplayName("login user bad request test")
    public void loginUserBadRequestTest() {
        try {
            RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
            AuthData auth = userService.registerUser(request);

            AuthData loginData = userService.loginUser(new LoginRequest("user", "password"));
            assert false;
        }
        catch (apiException e) {
            assert true;
        }

    }

    @Test
    @DisplayName("logout user test")
    public void logoutUserTest() {
        try {
            RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
            AuthData auth = userService.registerUser(request);
            userService.logoutUser(auth.authToken());
            assert true;
        } catch (apiException e) {
            assert false;
        }

    }

    @Test
    @DisplayName("logout user unauthorized test")
    public void logoutUserUnauthorizedTest(){
        try {
            userService.logoutUser("abcde");
            assert false;
        } catch (apiException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("get auth test")
    public void getAuthTest() {
        RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
        AuthData auth = userService.registerUser(request);
        AuthData retrievedAuthData = userService.getAuth(auth.authToken());

        assert Objects.equals(auth.authToken(),retrievedAuthData.authToken());
    }

    @Test
    @DisplayName("get auth fail test")
    public void getAuthFailTest(){
        AuthData auth = userService.getAuth("abcdef");
        if(auth == null){
            assert true;
        }else assert false;
    }

    @Test
    @DisplayName("clear auth test")
    public void deleteAuthTest() {
        RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
        AuthData auth = userService.registerUser(request);
        userService.clearAuth();
        AuthData retrievedAuthData = userService.getAuth(auth.authToken());

        if(retrievedAuthData == null){
            assert true;
        }else assert false;

    }

    @Test
    @DisplayName("clear users test")
    public void clearUsersTest(){
        RegisterUserRequest request = new RegisterUserRequest("user", "pass", "email@email.com");
        AuthData auth = userService.registerUser(request);
        userService.clearUsers();
        AuthData newAuth = userService.registerUser(request);

        assert Objects.equals(newAuth.username(), auth.username());
    }
}
