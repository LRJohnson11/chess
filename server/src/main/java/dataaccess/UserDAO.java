package dataaccess;

import model.UserData;

public interface UserDAO {

    public boolean createUser(String username, String password, String email);

    public UserData getUserByUsername(String username);

    public boolean clear();
}
