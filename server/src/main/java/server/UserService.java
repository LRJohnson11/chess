package server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;

public class UserService extends ServiceLayer {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public String registerUser(String username, String password, String email){
        if(userDAO.getUserByUsername(username)!= null){
            throw new RuntimeException("username is not unique!");
        }
        userDAO.createUser(username,password,email);
        return authDAO.createAuth(username);
    }

    public String loginUser(String username, String password){
        UserData user = userDAO.getUserByUsername(username);
        if(user.getPassword().equals(password)){
            //throw error
        }
        return authDAO.createAuth(username);
    }

    public boolean logoutUser(String authToken){
        return authDAO.deleteAuth(authToken);
    }
}
