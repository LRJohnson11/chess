package server;

public abstract class ServiceLayer {

    public boolean isAuthenticated(String authToken){
        return true;
    }
}
