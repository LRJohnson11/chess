package server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService  {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public String registerUser(RegisterUserRequest request){
        if(userDAO.getUserByUsername(request.username())!= null){
            throw new RuntimeException("username is not unique!");
        }
        userDAO.createUser(request.username(),request.password(), request.email());
        return authDAO.createAuth(request.username());
    }

    public String loginUser(LoginRequest request){
        UserData user = userDAO.getUserByUsername(request.username());
        if(!user.password().equals(request.password())){
            throw new RuntimeException("bad request");
        }
        return authDAO.createAuth(request.username());
    }

    public boolean logoutUser(String authToken){
        return authDAO.deleteAuth(authToken);
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void clearAuth(){
        authDAO.clear();
    }
    public void clearUsers(){
        userDAO.clear();
    }
}
