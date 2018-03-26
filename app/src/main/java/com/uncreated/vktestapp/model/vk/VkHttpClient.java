package com.uncreated.vktestapp.model.vk;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VkHttpClient {
    private static final VkHttpClient ourInstance = new VkHttpClient();

    public static VkHttpClient getInstance() {
        return ourInstance;
    }

    static final String API_VERSION = "5.73";
    static final Long CLIENT_ID = 6421632L;
    //private static final String CLIENT_SECRET = "eyw64AxTKYOn2Ig4FwVk";
    static final String ACCESS_TOKEN = "access_token";
    static final String EXPIRES_IN = "expires_in";
    static final String USER_ID = "user_id";

    private static final String REDIRECT_HOST = "http://uncreated.com";
    public static final String AUTH_URL = "https://oauth.vk.com/authorize" +
            "?display=mobile" +
            "&response_type=token" +
            "&scope=6" +//friends(2) + photos(4)
            "&v=" + API_VERSION +
            "&client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_HOST;

    private static final String API_URL = "api.vk.com";

    private RequestQueue mRequestQueue;

    static void init(Context context) {
        if (ourInstance.mRequestQueue == null) {
            ourInstance.mRequestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * Парсит строку и получает {@link VkSession}
     *
     * @param url ссылка переадресации
     * @return true если сессия пользователя получена
     */
    public boolean auth(String url) throws AuthException {
        if (url.startsWith(REDIRECT_HOST)) {
            Uri uri = Uri.parse(url.replaceFirst("#", "?"));

            String error = uri.getQueryParameter("error");
            if (error != null)
                throw new AuthException(error);

            String accessToken = uri.getQueryParameter(ACCESS_TOKEN);
            String expiresIn = uri.getQueryParameter(EXPIRES_IN);
            String userId = uri.getQueryParameter(USER_ID);
            if (accessToken != null && expiresIn != null && userId != null) {
                Vk.getInstance().setVkSession(new VkSession(accessToken, Long.valueOf(expiresIn),
                        Long.valueOf(userId)));
                return true;
            }
        }
        return false;
    }

    /**
     * Запрашивает у сервера информацию о текущем пользователе
     * при удачном получении необходимой информации о пользователе,
     * вызывает {@link SuccessCallback#onSuccess(Object)}, при любой ошибке
     * вызывает {@link FailedCallback#onFailed(String)}
     */
    public void getUser(@NonNull SuccessCallback<Void> successCallback,
                        @NonNull FailedCallback failedCallback) {
        VkSession vkSession = Vk.getInstance().getVkSession();
        if (vkSession == null) {
            failedCallback.onFailed("Incorrect session");
            return;
        }

        String url = makeGetUsersUrl(vkSession);

        JSONObjectListener jsonObjectListener = new JSONObjectListener(failedCallback) {
            @Override
            void onSafeResponse(JSONObject response) throws JSONException {
                JSONArray usersArray = response.getJSONArray("response");
                JSONObject userJson = usersArray.getJSONObject(0);

                VkUser vkUser = VkUser.fromJson(userJson);
                Vk.getInstance().setVkUser(vkUser);
                getFriends(vkSession, successCallback, failedCallback, vkUser);
            }
        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, jsonObjectListener, jsonObjectListener);

        mRequestQueue.add(jsonRequest);
    }

    /**
     * Запрашивает у сервера информацию о друзьях текущего пользователя
     * при удачном получении необходимой информации о пользователе,
     * вызывает {@link SuccessCallback#onSuccess(Object)}, при любой ошибке
     * вызывает {@link FailedCallback#onFailed(String)}
     */
    private void getFriends(@NonNull VkSession vkSession,
                            @NonNull SuccessCallback<Void> successCallback,
                            @NonNull FailedCallback failedCallback,
                            @NonNull VkUser vkUser) {
        String url = makeGetFriendsUrl(vkSession);

        JSONObjectListener jsonObjectListener = new JSONObjectListener(failedCallback) {
            @Override
            void onSafeResponse(JSONObject response) throws JSONException {
                response = response.getJSONObject("response");
                JSONArray usersArray = response.getJSONArray("items");

                vkUser.setFriendsByJsonArray(usersArray);
                getImages(vkSession, successCallback, failedCallback, vkUser);
            }
        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, jsonObjectListener, jsonObjectListener);

        mRequestQueue.add(jsonRequest);
    }

    private void getImages(@NonNull VkSession vkSession,
                           @NonNull SuccessCallback<Void> successCallback,
                           @NonNull FailedCallback failedCallback,
                           @NonNull VkUser vkUser) {
        if (vkUser.getFriends().size() == 0) {
            successCallback.onSuccess(null);
        }
        String url = makeGetImagesUrl(vkSession, new ArrayList<>(vkUser.getFriends().values()));

        JSONObjectListener jsonObjectListener = new JSONObjectListener(failedCallback) {
            @Override
            void onSafeResponse(JSONObject response) throws JSONException {
                JSONArray imageArray = response.getJSONArray("response");
                vkUser.setFriendsImagesByJson(imageArray);
                successCallback.onSuccess(null);
            }
        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, jsonObjectListener, jsonObjectListener);

        mRequestQueue.add(jsonRequest);
    }

    /**
     * Синхронный запрос получения изображения
     */
    public Bitmap loadImage(@NonNull VkImage vkImage) {
        RequestFuture<Bitmap> requestFuture = RequestFuture.newFuture();
        ImageRequest imageRequest = new ImageRequest(vkImage.getImageUrl(), requestFuture,
                0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                requestFuture);

        mRequestQueue.add(imageRequest);
        mRequestQueue.start();

        try {
            return requestFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создаёт Url запроса к API "users.get"
     *
     * @return сформированный Url
     */
    private static String makeGetUsersUrl(@NonNull VkSession vkSession) {
        return makeUrl(vkSession, "users.get",
                new Pair<>("fields", "user_id, photo_id"));
    }

    /**
     * Создаёт Url запроса к API "friends.get"
     *
     * @return сформированный Url
     */
    private static String makeGetFriendsUrl(@NonNull VkSession vkSession) {
        return makeUrl(vkSession, "friends.get",
                new Pair<>("fields", "photo_id"));
    }

    private static String makeGetImagesUrl(@NonNull VkSession vkSession,
                                           @NonNull List<VkUser> vkUsers) {
        StringBuilder images = new StringBuilder();
        for (int i = 0; i < vkUsers.size(); i++) {
            VkImage vkImage = vkUsers.get(i).getImage();
            if (vkImage != null) {
                images.append(vkImage.getImageId()).append(",");
            }
        }
        if (images.length() > 0) {
            images.deleteCharAt(images.length() - 1);
        }
        String url = makeUrl(vkSession, "photos.getById", new Pair<>("extended", "1"));
        return url + "&photos=" + images;
    }

    /**
     * Создаёт Url запроса к API "users.get", формируя необходимые параметры
     *
     * @param apiMethod метод API
     * @param params    параметры, добавляемые в строку, формата "ключ", "значение"
     * @return сформированный Url
     */
    @SafeVarargs
    private static String makeUrl(@NonNull VkSession vkSession, String apiMethod,
                                  Pair<String, String>... params) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(API_URL)
                .appendPath("method")
                .appendPath(apiMethod)
                .appendQueryParameter("access_token", vkSession.getAccessToken())
                .appendQueryParameter("v", API_VERSION);

        for (Pair<String, String> param : params) {
            builder.appendQueryParameter(param.first, param.second);
        }

        return builder.build().toString();
    }

    public interface SuccessCallback<T> {
        void onSuccess(T response);
    }

    public interface FailedCallback {
        void onFailed(String errorMessage);
    }

    public class AuthException extends Exception {
        AuthException(@NonNull String message) {
            super(message);
        }
    }

    /**
     * Надстройка над Response.Listener и Response.ErrorListener
     * дополнительно проверяет входящий JSONObject
     * на отсутствие в нём поля "error" и наличия "response"
     * Так же ловит JSONException в переопределяемом методе onSafeResponse
     */
    abstract class JSONObjectListener implements Response.Listener<JSONObject>, Response.ErrorListener {
        private FailedCallback mFailedCallback;

        public JSONObjectListener(FailedCallback failedCallback) {
            mFailedCallback = failedCallback;
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.has("error")) {
                    JSONObject errorJson = response.getJSONObject("error");
                    mFailedCallback.onFailed(errorJson.getString("error_msg"));
                } else if (response.has("response")) {
                    onSafeResponse(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mFailedCallback.onFailed(e.getMessage());
            }
        }

        abstract void onSafeResponse(JSONObject response) throws JSONException;

        @Override
        public void onErrorResponse(VolleyError error) {
            mFailedCallback.onFailed(error.getMessage());
        }
    }
}