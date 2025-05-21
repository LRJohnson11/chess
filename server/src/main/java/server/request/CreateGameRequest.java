package server.request;

public record CreateGameRequest(String gameName) {
    public boolean validRequest(){
        if(gameName != null){
            return !gameName.isBlank();
        }
        return false;
    }
}
