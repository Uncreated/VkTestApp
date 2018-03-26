package com.uncreated.vktestapp.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.uncreated.vktestapp.R;
import com.uncreated.vktestapp.ui.photos.PhotoActivity;
import com.uncreated.vktestapp.ui.photos.PhotoFragment;

/**
 * По задумке должен был быть слой для больших экранов с парой фрагментов
 * FriendsFragment и PhotoFragment
 */
public class FriendsActivity extends AppCompatActivity implements
        FriendsFragment.OnFriendClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
    }

    @Override
    public void onFriendPhotoClick(ImageView imageView, long userId) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoFragment.USER_ID_KEY, Long.toString(userId));
        intent.putExtra(PhotoFragment.IMAGE_INDEX_KEY, 0);
        startActivity(intent);
    }
}
