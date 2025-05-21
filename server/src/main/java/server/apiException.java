package server;

public class apiException extends RuntimeException{
    private final int status;

    public  apiException(int status, String message){
        super(message);
        this.status = status;

    }

    public int getStatus() {
        return status;
    }
}
