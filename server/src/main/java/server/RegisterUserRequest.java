package server;

public record RegisterUserRequest(String username, String password, String email) {
}
