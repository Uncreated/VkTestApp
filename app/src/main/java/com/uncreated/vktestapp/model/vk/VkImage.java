package com.uncreated.vktestapp.model.vk;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class VkImage {
    private static final Integer[] IMAGE_SIZES = {1280, 807, 604, 130, 75};

    private String mImageId;
    private String mImageUrl;
    private long mLikes;
    private long mReposts;

    public VkImage(String imageId) {
        mImageId = imageId;
    }

    static VkImage getFromJson(@NonNull JSONObject imageJson) throws JSONException {
        if (imageJson.isNull("id") || imageJson.isNull("owner_id")) {
            throw new JSONException("Incorrect json");
        }

        String imageId = imageJson.getString("owner_id") + "_" + imageJson.getString("id");
        for (Integer size : IMAGE_SIZES) {
            String photoSize = "photo_" + size;
            if (imageJson.has(photoSize)) {
                VkImage vkImage = new VkImage(imageId);
                vkImage.setImageUrl(imageJson.getString(photoSize));
                if (!imageJson.isNull("likes")) {
                    JSONObject likes = imageJson.getJSONObject("likes");
                    if (!likes.isNull("count")) {
                        vkImage.setLikes(likes.getInt("count"));
                    }
                }
                if (!imageJson.isNull("reposts")) {
                    JSONObject reposts = imageJson.getJSONObject("reposts");
                    if (!reposts.isNull("count")) {
                        vkImage.setReposts(reposts.getInt("count"));
                    }
                }
                return vkImage;
            }
        }

        throw new JSONException("Incorrect json");
    }

    public String getImageId() {
        return mImageId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getUserId() {
        return mImageId.substring(0, mImageId.indexOf('_'));
    }

    public long getLikes() {
        return mLikes;
    }

    public void setLikes(long likes) {
        mLikes = likes;
    }

    public long getReposts() {
        return mReposts;
    }

    public void setReposts(long reposts) {
        mReposts = reposts;
    }

    @Override
    public String toString() {
        return "VkImage{" +
                "mImageId='" + mImageId + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                '}';
    }
}
