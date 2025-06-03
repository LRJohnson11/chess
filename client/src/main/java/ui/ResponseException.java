package ui;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception{
    private final int statusCode;

    public ResponseException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson(){
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream) throws ParseException {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);

        int status = 500;
        var maybeStatus = map.get("status");
        if(maybeStatus instanceof Number){
            status = ((Number) maybeStatus).intValue();
        }
        String message = "unknown error";
        var maybeMessage = map.get("message");
        if(maybeMessage instanceof String) {
            message = maybeMessage.toString();
        }
        return new ResponseException(message,status);
    }

}
