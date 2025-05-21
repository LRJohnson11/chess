package server.request;

public record LoginRequest(String username, String password) {
    public boolean validateRequest(){
        if(username != null && password != null) {
            return !password.isBlank() && !username.isBlank();
        }
        return false;
    }
}
