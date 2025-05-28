package server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.request.LoginRequest;
import server.request.RegisterUserRequest;

public class UserService  {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData registerUser(RegisterUserRequest request){
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new ApiException(400, "Error: bad request");
        }
        if(userDAO.getUserByUsername(request.username())!= null){
            throw new ApiException(403, "Error: already taken");
        }
        userDAO.createUser(request.username(),request.password(), request.email());
        return authDAO.createAuth(request.username());
    }

    public AuthData loginUser(LoginRequest request){
        if(!request.validateRequest()){
            throw new ApiException(400, "Error: bad request");
        }
        UserData user = userDAO.getUserByUsername(request.username());
        if(user == null){
            throw new ApiException(401, "Error: unauthorized");
        }
        var hashedPasswordFromDb = user.password();
        if(!BCrypt.checkpw(request.password(), hashedPasswordFromDb)){
            throw new ApiException(401, "Error: unauthorized");
        }
        return authDAO.createAuth(request.username());
    }

    public boolean logoutUser(String authToken){
        AuthData user = authDAO.getAuth(authToken);
        if(user == null){
            throw new ApiException(401, "Error: unauthorized");
        }
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
