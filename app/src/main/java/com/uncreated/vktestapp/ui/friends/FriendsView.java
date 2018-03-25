package com.uncreated.vktestapp.ui.friends;

import android.graphics.Bitmap;

import com.uncreated.vktestapp.model.vk.VkUser;
import com.uncreated.vktestapp.mvp.ViewBase;

import java.util.List;

public interface FriendsView extends ViewBase {

    void setFriends(List<VkUser> friends);

    void onPhotoChanged();

    abstract class OnPhotoLoadedListener {
        private FriendsView mFriendsView;

        public OnPhotoLoadedListener(FriendsView friendsView) {
            mFriendsView = friendsView;
        }

        public FriendsView getFriendsView() {
            return mFriendsView;
        }

        public abstract void onPhotoLoaded(Bitmap bitmap);
    }
}
