package com.uncreated.vktestapp.model;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.uncreated.vktestapp.model.vk.VkHttpClient;
import com.uncreated.vktestapp.ui.friends.FriendsView;

public class PhotoCache {
    private static PhotoCache ourInstance = new PhotoCache();

    public static PhotoCache getInstance() {
        return ourInstance;
    }

    //TODO: заменить размер
    LruCache<String, Bitmap> photos = new LruCache<String, Bitmap>(1024 * 1024 * 100) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private PhotoCache() {
    }

    public void loadPhoto(String photoUrl, int width, int height,
                          FriendsView.OnPhotoLoadedListener onPhotoLoadedListener) {
        Bitmap bitmap = photos.get(photoUrl);
        if (bitmap == null) {
            VkHttpClient.getInstance().getPhoto(photoUrl, response -> {
                photos.put(photoUrl, response);
            });
        }
        onPhotoLoadedListener.onPhotoLoaded(bitmap);
    }
}
