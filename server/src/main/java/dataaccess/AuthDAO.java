package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(AuthData auth);

    boolean deleteAuth(AuthData auth);

    AuthData getAuth(String authToken);

    boolean clear();
}
