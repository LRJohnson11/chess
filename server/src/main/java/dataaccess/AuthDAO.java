package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username);

    boolean deleteAuth(String username);

    AuthData getAuth(String authToken);

    boolean clear();
}
