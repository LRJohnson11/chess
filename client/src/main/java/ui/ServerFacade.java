package ui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;

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

    public UserData registerUser(UserData user) throws Exception {
        //prepare this to post as a new user
        var targetAddress = address + "/user";

        return makeRequest("post", targetAddress, user, UserData.class);

    }



    private <T> T makeRequest(String method, String path, Object object, Class<T> responseObject) throws Exception{
        try{
            URL url = new URI(address).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(object, http);
            http.connect();

            return readBody(http, responseObject);

        } catch (Exception e){
            throw new RuntimeException("an unknown error occurred");
        }

    }

    private <T> T readBody(HttpURLConnection http, Class <T> responseClass) throws IOException {
        T response = null;
        if(http.getContentLength() > 0){
            try(InputStream is = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(is);
                if(responseClass != null){
                    response = gson.fromJson(reader,responseClass);
                }
            }
        }
        return response;
    }

    private static void writeBody(Object object, HttpURLConnection http) throws IOException {
        if(object != null){
            http.addRequestProperty("Content-Type", "application/json");
            String jsonBody = gson.toJson(object, object.getClass());
            try(OutputStream os = http.getOutputStream()){
                os.write(jsonBody.getBytes());
            }
        }
    }

}
