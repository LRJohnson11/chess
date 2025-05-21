package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class LocalUserDAO implements UserDAO{
    private final Map<String, UserData> users;

    public LocalUserDAO() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean createUser(String username, String password, String email) {
        users.put(username, new UserData(username,password,email));
        return true;
    }

    @Override
    public UserData getUserByUsername(String username) {
        return users.get(username);
    }

    @Override
    public boolean clear() {
        users.clear();
        return true;
    }
}
