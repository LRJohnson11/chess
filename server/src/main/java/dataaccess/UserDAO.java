package dataaccess;

import model.UserData;

public interface UserDAO {

    boolean createUser(String username, String password, String email);

    UserData getUserByUsername(String username);

    boolean clear();
}
