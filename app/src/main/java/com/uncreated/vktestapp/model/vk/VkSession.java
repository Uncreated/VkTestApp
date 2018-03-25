package com.uncreated.vktestapp.model.vk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;

import static com.uncreated.vktestapp.model.vk.VkHttpClient.ACCESS_TOKEN;
import static com.uncreated.vktestapp.model.vk.VkHttpClient.EXPIRES_IN;
import static com.uncreated.vktestapp.model.vk.VkHttpClient.USER_ID;

/**
 * Информация о сессии пользователя
 */
public class VkSession {
    private static final long HOUR_IN_SECONDS = 3600;

    private static final String SHARED_PREF = "sessionSharedPref";

    private String mAccessToken;
    private Long mExpiresIn;
    private Long mUserId;

    VkSession(@NonNull String accessToken, @NonNull Long lifetime, @NonNull Long userId) {
        mAccessToken = accessToken;
        mExpiresIn = getCurrentSeconds() + lifetime;
        mUserId = userId;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public Long getExpiresIn() {
        return mExpiresIn;
    }

    public Long getUserId() {
        return mUserId;
    }

    /**
     * Сохраняет {@link this} в {@link SharedPreferences}
     *
     * @param context для получения {@link SharedPreferences}
     */
    void save(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF,
                Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(ACCESS_TOKEN, mAccessToken)
                .putLong(EXPIRES_IN, mExpiresIn)
                .putLong(USER_ID, mUserId)
                .apply();
    }

    /**
     * Загружает {@link VkSession} из {@link SharedPreferences}
     *
     * @param context для получения {@link SharedPreferences}
     * @return null если сессия не найдена, либо не актуальна
     */
    @Nullable
    static VkSession load(@NonNull Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF,
                Context.MODE_PRIVATE);

        String accessToken = sharedPref.getString(ACCESS_TOKEN, null);
        Long expiresIn = sharedPref.getLong(EXPIRES_IN, -1);
        Long userId = sharedPref.getLong(USER_ID, -1);

        if (accessToken != null && expiresIn != -1 && userId != -1 &&
                getCurrentSeconds() < expiresIn + HOUR_IN_SECONDS) {
            return new VkSession(accessToken, expiresIn, sharedPref.getLong(USER_ID, -1));
        }
        return null;
    }

    /**
     * Возвращает текущее время в секундах
     */
    private static long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
