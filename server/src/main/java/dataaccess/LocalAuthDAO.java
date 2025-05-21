package dataaccess;

import model.AuthData;
import server.apiException;

import java.util.*;

public class LocalAuthDAO implements AuthDAO{
    Map<String, AuthData> usernameMap;
    Map<String, AuthData> tokenMap;

    public LocalAuthDAO() {
        this.usernameMap = new HashMap<>();
        this.tokenMap = new HashMap<>();
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        usernameMap.put(username, new AuthData(username, authToken));
        tokenMap.put(authToken, new AuthData(username,authToken));
        return new AuthData(username, authToken);
    }

    @Override
    public boolean deleteAuth(String authToken) {
        AuthData auth = tokenMap.get(authToken);
        if(auth == null){
            throw new apiException(400, "bad request");
        }
        usernameMap.remove(auth.username());
        tokenMap.remove(authToken);
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
