package model;

public record UserData (String username, String password, String email){
    public boolean valid() {
        if(username != null && password != null && email != null){
            return !username.isBlank() && !password.isBlank() && !email.isBlank();
        }
        return false;
    }
}
