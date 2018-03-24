package com.uncreated.vktestapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Информация о сессии пользователя
 */
public class Session {
    private static final long HOUR_IN_SECONDS = 3600;

    private static final String SHARED_PREF = "sessionSharedPref";
    private static final String ACCESS_TOKEN_KEY = "accessTokenKey";
    private static final String EXPIRES_IN_KEY = "expiresInKey";
    private static final String USER_ID_KEY = "userIdKey";

    private static Session current;

    public static Session getCurrent() {
        return current;
    }

    public static void setCurrent(Session mCurrent) {
        Session.current = mCurrent;
    }

    private String mAccessToken;
    private Long mExpiresIn;
    private Long mUserId;

    public Session(@NonNull String accessToken, @NonNull Long lifetime, @NonNull Long userId) {
        mAccessToken = accessToken;
        mExpiresIn = getCurrentSeconds() + lifetime;
        mUserId = userId;

        setCurrent(this);
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

    public void save(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF,
                Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(ACCESS_TOKEN_KEY, mAccessToken)
                .putLong(EXPIRES_IN_KEY, mExpiresIn)
                .putLong(USER_ID_KEY, mUserId)
                .apply();
    }

    public static Session load(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF,
                Context.MODE_PRIVATE);

        String accessToken = sharedPref.getString(ACCESS_TOKEN_KEY, null);
        if (accessToken != null &&
                sharedPref.contains(EXPIRES_IN_KEY) &&
                sharedPref.contains(USER_ID_KEY)) {
            Long expiresIn = sharedPref.getLong(EXPIRES_IN_KEY, -1);

            if (getCurrentSeconds() < expiresIn + HOUR_IN_SECONDS) {
                return new Session(accessToken, expiresIn, sharedPref.getLong(USER_ID_KEY, -1));
            }
        }

        return null;
    }

    private static long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
