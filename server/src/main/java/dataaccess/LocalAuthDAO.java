package dataaccess;

import model.AuthData;

import java.util.*;

public class LocalAuthDAO implements AuthDAO{
    Map<String, AuthData> usernameMap;
    Map<String, AuthData> tokenMap;

    public LocalAuthDAO() {
        this.usernameMap = new HashMap<>();
        this.tokenMap = new HashMap<>();
    }

    @Override
    public String createAuth(String username) {
        String authToken = generateToken();
        usernameMap.put(username, new AuthData(username, authToken));
        tokenMap.put(authToken, new AuthData(username,authToken));
        return authToken;
    }

    @Override
    public boolean deleteAuth(String authToken) {
        String username = tokenMap.get(authToken).username();
        usernameMap.remove(username);
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
