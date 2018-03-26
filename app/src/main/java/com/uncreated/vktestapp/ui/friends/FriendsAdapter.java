package com.uncreated.vktestapp.ui.friends;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncreated.vktestapp.R;
import com.uncreated.vktestapp.model.vk.VkUser;
import com.uncreated.vktestapp.presentation.friends.FriendsPresenter;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClick(int friendIndex);

        void onFriendPhotoClick(ImageView imageView, int friendIndex);
    }

    private List<VkUser> mFriends;
    private RecyclerView mRecyclerView;
    private LayoutInflater mLayoutInflater;
    private OnFriendClickListener mOnFriendClickListener;
    private FriendsFragment mFriendsFragment;
    private FriendsPresenter mFriendsPresenter = FriendsPresenter.getInstance();

    private final int mThumbSize;

    private int mSelected = -1;

    public FriendsAdapter(@NonNull List<VkUser> friends, @NonNull RecyclerView recyclerView,
                          @NonNull Context context, @NonNull FriendsFragment friendsFragment,
                          @NonNull OnFriendClickListener onFriendClickListener) {
        mFriends = friends;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(context);
        mFriendsFragment = friendsFragment;
        mOnFriendClickListener = onFriendClickListener;
        mThumbSize = (int) context.getResources().getDimension(R.dimen.thumb_size);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VkUser vkUser = mFriends.get(position);

        if (mSelected == position) {
            holder.mCardView.setBackgroundResource(R.color.selectedCard);
        }
        holder.mFirstNameTextView.setText(vkUser.getFirstName());
        holder.mLastNameTextView.setText(vkUser.getLastName());
        if (vkUser.getImage() != null) {
            Bitmap image = mFriendsPresenter.loadImage(vkUser.getImage(), mThumbSize, mThumbSize,
                    bitmap -> {
                        if (!mRecyclerView.isComputingLayout()) {
                            notifyItemChanged(position);
                        }
                    });
            if (image != null) {
                holder.mAvatarImageView.setImageBitmap(image);
            }
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final CardView mCardView;
        final ImageView mAvatarImageView;
        final TextView mFirstNameTextView;
        final TextView mLastNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_view);
            mAvatarImageView = itemView.findViewById(R.id.iv_photo);
            mFirstNameTextView = itemView.findViewById(R.id.tv_first_name);
            mLastNameTextView = itemView.findViewById(R.id.tv_last_name);
        }
    }


}
