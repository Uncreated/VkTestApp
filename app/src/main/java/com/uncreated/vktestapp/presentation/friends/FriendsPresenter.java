package com.uncreated.vktestapp.presentation.friends;

import android.graphics.Bitmap;

import com.uncreated.vktestapp.model.PhotoCache;
import com.uncreated.vktestapp.model.vk.Vk;
import com.uncreated.vktestapp.mvp.PresenterBase;
import com.uncreated.vktestapp.ui.friends.FriendsView;

public class FriendsPresenter extends PresenterBase<FriendsView> {
    private static final int SET_FRIENDS = 1;

    private static FriendsPresenter ourInstance = new FriendsPresenter();

    public static FriendsPresenter getInstance() {
        return ourInstance;
    }

    private PhotoCache mPhotoCache = PhotoCache.getInstance();

    private FriendsPresenter() {
        runCommand(new UniqueCommand(SET_FRIENDS,
                () -> mView.setFriends(Vk.getInstance().getVkUser().getFriends())));
    }

    @Override
    public void onFirstAttachView() {
    }

    /**
     * Запрашивает фото по photoUrl в размере (width, height)
     * Если view был откреплён, вызов onPhotoLoadedListener не происходит
     */
    public void loadPhoto(String photoUrl, int width, int height,
                          FriendsView.OnPhotoLoadedListener onPhotoLoadedListener) {
        mPhotoCache.loadPhoto(photoUrl, width, height, new FriendsView.OnPhotoLoadedListener(mView) {
            @Override
            public void onPhotoLoaded(Bitmap bitmap) {
                if (getFriendsView() == mView) {
                    onPhotoLoadedListener.onPhotoLoaded(bitmap);
                }
            }
        });
    }

    @Override
    public void onDetachView(FriendsView view) {
        super.onDetachView(view);
    }
}
