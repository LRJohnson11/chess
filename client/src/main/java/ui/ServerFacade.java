package ui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    static Gson gson = new GsonBuilder().serializeNulls().create();
    String address;

    public ServerFacade(String address){
        this.address = address;
    }


    public AuthData registerUser(UserData user) throws Exception {
        //prepare this to post as a new user
        var targetAddress = address + "/user";

        return makeRequest("POST", targetAddress, user, AuthData.class);

    }
    public AuthData loginUser(LoginRequest loginRequest) throws Exception {
        return makeRequest("POST", getTargetAddress("session"), loginRequest, AuthData.class);
    }

    public void logoutUser() throws Exception {
        makeRequest("DELETE", getTargetAddress("session"), null, null);
    }

    private String getTargetAddress(String endpoint){
        return address + "/" + endpoint;
    }


    private <T> T makeRequest(String method, String path, Object object, Class<T> responseObject) throws Exception{
        try{
            URL url = new URI(path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
                throw new ResponseException("other error: " + e.getMessage(), status);
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
