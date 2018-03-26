package com.uncreated.vktestapp.model.vk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class VkUser {
    private static final String USER_ID_KEY = "id";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String IMAGE_URL_KEY = "photo_id";

    private Long mUserId;
    private String mFirstName;
    private String mLastName;
    private VkImage mImage;
    private HashMap<String, VkUser> mFriends;
    private List<VkImage> mAllImages;

    private VkUser(@NonNull Long userId, @NonNull String firstName, @NonNull String lastName) {
        mUserId = userId;
        mFirstName = firstName;
        mLastName = lastName;
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

        VkUser vkUser = new VkUser(jsonObject.getLong(USER_ID_KEY),
                jsonObject.getString(FIRST_NAME_KEY),
                jsonObject.getString(LAST_NAME_KEY));
        if (!jsonObject.isNull(IMAGE_URL_KEY)) {
            vkUser.setImage(new VkImage(jsonObject.getString(IMAGE_URL_KEY)));
        }
        return vkUser;
    }

    void setFriendsByJsonArray(@NonNull JSONArray jsonArray) throws JSONException {
        mFriends = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            VkUser vkUser = fromJson(jsonArray.getJSONObject(i));
            mFriends.put(vkUser.getUserId().toString(), vkUser);
        }
    }

    void setFriendsImagesByJson(@NonNull JSONArray imageArray) throws JSONException {
        for (int i = 0; i < imageArray.length(); i++) {
            VkImage vkImage = VkImage.getFromJson(imageArray.getJSONObject(i));
            VkUser vkUser = mFriends.get(vkImage.getUserId());
            vkUser.setImage(vkImage);
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

    @Nullable
    public VkImage getImage() {
        return mImage;
    }

    public void setImage(VkImage image) {
        mImage = image;
    }

    public HashMap<String, VkUser> getFriends() {
        return mFriends;
    }

    public List<VkImage> getAllImages() {
        return mAllImages;
    }

    @Override
    public String toString() {
        return "VkUser{" +
                "mUserId=" + mUserId +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mImage=" + mImage +
                '}';
    }
}
