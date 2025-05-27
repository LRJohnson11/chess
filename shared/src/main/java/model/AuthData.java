package model;

public record AuthData(String username, String authToken) {
    public boolean valid() {
        if(username != null && authToken != null){
            return !username.isBlank() && !authToken.isBlank();
        }
        return false;
    }
}
