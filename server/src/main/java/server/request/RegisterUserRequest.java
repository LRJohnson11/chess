package server.request;

public record RegisterUserRequest(String username, String password, String email) {
}
