package com.uncreated.vktestapp.ui.friends;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uncreated.vktestapp.R;
import com.uncreated.vktestapp.model.vk.VkImage;
import com.uncreated.vktestapp.model.vk.VkUser;
import com.uncreated.vktestapp.presentation.friends.FriendsPresenter;

import java.util.List;

/**
 * Фрагмент для отображения списка друзей
 */
public class FriendsFragment extends Fragment implements FriendsView {

    public interface OnFriendClickListener {
        void onFriendPhotoClick(ImageView imageView, long userId);
    }

    private FriendsPresenter mFriendsPresenter = FriendsPresenter.getInstance();

    private RecyclerView mRecyclerView;
    private FriendsAdapter mFriendsAdapter;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecyclerView = view.findViewById(R.id.friends_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFriendsPresenter.onAttachView(this);
    }

    @Override
    public void setFriends(List<VkUser> friends) {
        if (getActivity() != null) {
            mFriendsAdapter = new FriendsAdapter(friends, mRecyclerView, getActivity(),
                    this, (OnFriendClickListener) getActivity());
            mRecyclerView.setAdapter(mFriendsAdapter);
        }
    }
}