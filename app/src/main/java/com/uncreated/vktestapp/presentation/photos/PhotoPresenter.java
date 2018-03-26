package com.uncreated.vktestapp.presentation.photos;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.uncreated.vktestapp.model.image.ImageCache;
import com.uncreated.vktestapp.model.image.ImageLoadedCallback;
import com.uncreated.vktestapp.model.vk.Vk;
import com.uncreated.vktestapp.model.vk.VkHttpClient;
import com.uncreated.vktestapp.model.vk.VkImage;
import com.uncreated.vktestapp.model.vk.VkUser;
import com.uncreated.vktestapp.mvp.PresenterBase;
import com.uncreated.vktestapp.ui.photos.PhotoView;

import java.util.List;

public class PhotoPresenter extends PresenterBase<PhotoView> {

    private static PhotoPresenter ourInstance = new PhotoPresenter();

    public static PhotoPresenter getInstance() {
        return ourInstance;
    }

    private ImageCache mImageCache = ImageCache.getInstance();

    private PhotoPresenter() {
    }

    public List<VkImage> getAllImages(String userId) {
        VkUser vkUser = Vk.getInstance().getVkUser().getFriends().get(userId);
        VkHttpClient.getInstance().getAllImages(vkUser,
                response -> mView.updateImages(vkUser.getAllImages()),
                errorMessage -> mView.updateFailed());
        return vkUser.getAllImages();
    }

    public Bitmap loadImage(@NonNull VkImage vkImage, int width, int height,
                            @NonNull ImageLoadedCallback imageLoadedCallback) {
        final PhotoView currentView = mView;
        return mImageCache.loadImage(vkImage, width, height, bitmap -> {
            if (mView != null && currentView == mView) {
                imageLoadedCallback.onImageLoaded(bitmap);
            }
        });
    }
}
