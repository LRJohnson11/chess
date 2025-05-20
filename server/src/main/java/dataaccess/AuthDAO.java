package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public String createAuth(String username);

    public boolean deleteAuth(String username);

    public AuthData getAuth(String authToken);

    public boolean clear();
}
