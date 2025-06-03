package ui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.UserData;
import requests.CreateGameRequest;
import requests.LoginRequest;
import response.CreateGameResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    static Gson gson = new GsonBuilder().serializeNulls().create();
    String address;

    public ServerFacade(String address){
        this.address = address;
    }


    public AuthData registerUser(UserData user) throws Exception {
        //prepare this to post as a new user
        var targetAddress = address + "/user";

        return makeRequest("POST", targetAddress, user, AuthData.class, null);

    }
    public AuthData loginUser(LoginRequest loginRequest) throws Exception {
        return makeRequest("POST", getTargetAddress("session"), loginRequest, AuthData.class, null);
    }

    public void logoutUser(String authToken) throws Exception {
        Map<String, String> headers = Map.of("Authorization", authToken);
        makeRequest("DELETE", getTargetAddress("session"), null, null, headers);
    }
    public void createGame(String authToken, String arg) throws Exception {
        Map<String,String> headers = Map.of("Authorization", authToken);
        makeRequest("POST", getTargetAddress("game"), new CreateGameRequest(arg), CreateGameResponse.class, headers);
    }

    private String getTargetAddress(String endpoint){
        return address + "/" + endpoint;
    }


    private <T> T makeRequest(String method, String path, Object object, Class<T> responseObject, Map<String,String> headers) throws Exception{
        try{
            URL url = new URI(path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if(headers != null){
                for(var header: headers.entrySet()){
                    http.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            writeBody(object, http);
            throwIfError(http);
            http.connect();

            return readBody(http, responseObject);

        } catch (ResponseException e){
            throw new ResponseException(e.getMessage(), e.getStatusCode());
        }

    }

    private void throwIfError(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if(status != 200){
            try(InputStream errors = http.getErrorStream()){
                if(errors != null){
                    throw ResponseException.fromJson(errors);
                }
            } catch (ResponseException e) {
                throw e;
            } catch (Exception e){
                throw new ResponseException("internal server error", 500);
            }
        }

    }

    private <T> T readBody(HttpURLConnection http, Class <T> responseClass) throws IOException {
        T response = null;
            try(InputStream is = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(is);
                String json = new String(is.readAllBytes());
                System.out.println(json);
                if(responseClass != null){
                    response = gson.fromJson(json,responseClass);
                }
            }
        return response;
    }

    private void writeBody(Object object, HttpURLConnection http) throws IOException {
        if(object != null){
            http.addRequestProperty("Content-Type", "application/json");
            String jsonBody = gson.toJson(object, object.getClass());
            try(OutputStream os = http.getOutputStream()){
                os.write(jsonBody.getBytes());
            }
        }
    }


}
