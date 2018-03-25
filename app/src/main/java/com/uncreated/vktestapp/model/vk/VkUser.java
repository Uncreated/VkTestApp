package com.uncreated.vktestapp.model.vk;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class VkUser {
    private static final String USER_ID_KEY = "id";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String PHOTO_URL_KEY = "photo_max";

    private Long mUserId;
    private String mFirstName;
    private String mLastName;
    private String mPhotoUrl;
    private List<VkUser> mFriends;
    private List<String> mAllPhotos;

    private VkUser(Long userId, String firstName, String lastName, String photoUrl) {
        mUserId = userId;
        mFirstName = firstName;
        mLastName = lastName;
        mPhotoUrl = photoUrl;
    }

    /**
     * Парсит {@link JSONObject} и создаёт {@link VkUser}
     *
     * @throws JSONException при некорректном {@link JSONObject}
     */
    @NonNull
    static VkUser fromJson(@NonNull JSONObject jsonObject) throws JSONException {
        if (jsonObject.isNull(USER_ID_KEY) ||
                jsonObject.isNull(FIRST_NAME_KEY) ||
                jsonObject.isNull(LAST_NAME_KEY)) {
            throw new JSONException("Incorrect JSONObject: " + jsonObject);
        }

        return new VkUser(jsonObject.getLong(USER_ID_KEY), jsonObject.getString(FIRST_NAME_KEY),
                jsonObject.getString(LAST_NAME_KEY), jsonObject.getString(PHOTO_URL_KEY));
    }

    void setFriendsByJsonArray(@NonNull JSONArray jsonArray) throws JSONException {
        mFriends = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            mFriends.add(fromJson(jsonArray.getJSONObject(i)));
        }
    }

    public Long getUserId() {
        return mUserId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public List<VkUser> getFriends() {
        return mFriends;
    }

    public List<String> getAllPhotos() {
        return mAllPhotos;
    }
}
