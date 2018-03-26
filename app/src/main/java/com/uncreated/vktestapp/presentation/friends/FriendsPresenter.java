package com.uncreated.vktestapp.presentation.friends;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.uncreated.vktestapp.model.image.ImageCache;
import com.uncreated.vktestapp.model.image.ImageLoadedCallback;
import com.uncreated.vktestapp.model.vk.Vk;
import com.uncreated.vktestapp.model.vk.VkImage;
import com.uncreated.vktestapp.mvp.PresenterBase;
import com.uncreated.vktestapp.ui.friends.FriendsView;

import java.util.ArrayList;

public class FriendsPresenter extends PresenterBase<FriendsView> {
    private static final int SET_FRIENDS = 1;

    private static FriendsPresenter ourInstance = new FriendsPresenter();

    public static FriendsPresenter getInstance() {
        return ourInstance;
    }

    private ImageCache mImageCache = ImageCache.getInstance();

    private FriendsPresenter() {
        runCommand(new UniqueCommand(SET_FRIENDS, () -> mView.setFriends(
                new ArrayList<>(Vk.getInstance().getVkUser().getFriends().values()))));
    }

    @Override
    public void onFirstAttachView() {
    }

    /**
     * Запрашивает фото по vkImage в размере (width, height)
     * Если view был откреплён, вызов imageLoadedCallback не происходит
     */
    public Bitmap loadImage(@NonNull VkImage vkImage, int width, int height,
                            @NonNull ImageLoadedCallback imageLoadedCallback) {
        final FriendsView currentView = mView;
        return mImageCache.loadImage(vkImage, width, height, bitmap -> {
            if (mView != null && currentView == mView) {
                imageLoadedCallback.onImageLoaded(bitmap);
            }
        });
    }

    @Override
    public void onDetachView(FriendsView view) {
        super.onDetachView(view);
    }
}
