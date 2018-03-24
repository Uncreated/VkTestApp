package com.uncreated.vktestapp.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class VkClient {
    private static final VkClient ourInstance = new VkClient();

    public static VkClient getInstance() {
        return ourInstance;
    }

    public static final String API_VERSION = "5.73";
    public static final Long CLIENT_ID = 6421632L;
    private static final String CLIENT_SECRET = "eyw64AxTKYOn2Ig4FwVk";

    private static final String API_URL = "https://api.vk.com/method/";
    private static final String API_CHECK_TOKEN_URL = API_URL + "secure.checkToken?";

    /**
     * Запрашивает у VK актуальность сессии
     *
     * @return true если токен валидный
     */
    public boolean checkSession(@NonNull Session session) {
        //TODO:А может и вовсе не нужен этот вызов?
        /*try {
            URL url = new URL(getCheckTokenApiUrl(session));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            String body;
            if (200 <= code && code < 300) {
                body = getBody(urlConnection.getInputStream());
            } else {
                body = getBody(urlConnection.getErrorStream());
            }

            //В случае успеха, API возвращает success = 1
            JSONObject jsonObject = new JSONObject(body);
            return jsonObject.getInt("success") == 1;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }*/
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getCheckTokenApiUrl(@NonNull Session session) {
        return API_CHECK_TOKEN_URL + "access_token=" + session.getAccessToken()
                + "&app_id=" + 6421632
                + "&client_secret=" + CLIENT_SECRET
                + "&v=" + API_VERSION;
    }

    private static String getBody(InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new IOException("InputStream is null");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
