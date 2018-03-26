package com.uncreated.vktestapp.model.vk;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class VkImage {
    private static final Integer[] IMAGE_SIZES = {1280, 807, 604, 130, 75};

    private String mImageId;
    private String mImageUrl;

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

    @Override
    public String toString() {
        return "VkImage{" +
                "mImageId='" + mImageId + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                '}';
    }
}
