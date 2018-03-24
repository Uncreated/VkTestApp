package com.uncreated.vktestapp.presentation.login;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.uncreated.vktestapp.model.Session;

import static com.uncreated.vktestapp.model.VkClient.API_VERSION;
import static com.uncreated.vktestapp.model.VkClient.CLIENT_ID;

public class LogInHandler {

    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String USER_ID = "user_id";

    private static final String REDIRECT_HOST = "http://uncreated.com";
    public static final String AUTH_URI = "https://oauth.vk.com/authorize" +
            "?display=mobile" +
            "&response_type=token" +
            "&scope=6" +//friends(2) + photos(4)
            "&v=" + API_VERSION +
            "&client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_HOST;

    /**
     * Парсит {@param url} и извлекает из него токен, если перехвачена нужная страница
     *
     * @param url ссылка переадресации
     * @return true если извлечен токен
     */
    public Session getToken(String url) throws AuthException {
        if (url.startsWith(REDIRECT_HOST)) {
            Uri uri = Uri.parse(url.replaceFirst("#", "?"));

            String error = uri.getQueryParameter("error");
            if (error != null)
                throw new AuthException(error);

            String accessToken = uri.getQueryParameter(ACCESS_TOKEN_KEY);
            String expiresIn = uri.getQueryParameter(EXPIRES_IN);
            String userId = uri.getQueryParameter(USER_ID);
            if (accessToken != null && expiresIn != null && userId != null) {
                return new Session(accessToken, Long.valueOf(expiresIn), Long.valueOf(userId));
            }
        }
        return null;
    }

    public class AuthException extends Exception {
        public AuthException(@NonNull String message) {
            super(message);
        }
    }
}
