package dataaccess;

import model.AuthData;
import server.ApiException;

import java.util.*;

public class LocalAuthDAO implements AuthDAO{
    Map<String, AuthData> usernameMap;
    Map<String, AuthData> tokenMap;

    public LocalAuthDAO() {
        this.usernameMap = new HashMap<>();
        this.tokenMap = new HashMap<>();
    }

    @Override
    public AuthData createAuth(AuthData auth) {
        usernameMap.put(auth.username(), new AuthData(auth.username(), auth.authToken()));
        tokenMap.put(auth.authToken(), new AuthData(auth.username(),auth.authToken()));
        return auth;
    }

    @Override
    public boolean deleteAuth(AuthData auth) {

        if(auth == null){
            throw new ApiException(400, "bad request");
        }
        usernameMap.remove(auth.username());
        tokenMap.remove(auth.authToken());
        return true;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return tokenMap.get(authToken);
    }

    public boolean clear(){
        usernameMap.clear();
        tokenMap.clear();
        return true;
    }
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
