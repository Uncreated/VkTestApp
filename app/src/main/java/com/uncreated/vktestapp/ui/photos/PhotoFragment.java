package com.uncreated.vktestapp.ui.photos;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uncreated.vktestapp.R;
import com.uncreated.vktestapp.model.vk.VkImage;
import com.uncreated.vktestapp.presentation.photos.PhotoPresenter;

import java.util.List;

public class PhotoFragment extends Fragment implements PhotoView {

    public static final String USER_ID_KEY = "userIdKey";
    public static final String IMAGE_INDEX_KEY = "keyPosition";

    private ViewPager mViewPager;
    private LayoutInflater mLayoutInflater;

    private static final PhotoPresenter mPhotoPresenter = PhotoPresenter.getInstance();

    private List<VkImage> mPhotoList;
    private int mDisplayWidth = 1;
    private int mDisplayHeight = 1;

    private String mErrorTitle;
    private String mErrorMessage;

    public PhotoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mErrorTitle = getResources().getString(R.string.error_title);
        mErrorMessage = getResources().getString(R.string.images_update_error);
        mPhotoPresenter.onAttachView(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mPhotoPresenter.onDetachView(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setPageTransformer(true, new ZoomPageTransformer());

        Activity activity = getActivity();
        if (activity != null) {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            mDisplayWidth = size.x;
            mDisplayHeight = size.y;

            Intent intent = activity.getIntent();
            String userId = intent.getStringExtra(USER_ID_KEY);
            int imageIndex = intent.getIntExtra(IMAGE_INDEX_KEY, 0);
            mPhotoList = mPhotoPresenter.getAllImages(userId);
            mViewPager.setAdapter(new CustomPagerAdapter());
            mViewPager.setCurrentItem(imageIndex);
        }
        return view;
    }

    @Override
    public void updateImages(List<VkImage> images) {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFailed() {
        Activity activity = getActivity();
        if (activity != null) {
            new AlertDialog.Builder(activity)
                    .setTitle(mErrorTitle)
                    .setMessage(mErrorMessage)
                    .show();
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.page_item, container, false);

            ImageView imageView = itemView.findViewById(R.id.image_view);
            Bitmap image = mPhotoPresenter.loadImage(mPhotoList.get(position), mDisplayWidth,
                    mDisplayHeight, imageView::setImageBitmap);

            if (image != null) {
                imageView.setImageBitmap(image);
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class ZoomPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else {
                view.setAlpha(0);
            }
        }
    }
}
