package com.uncreated.vktestapp.model.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.uncreated.vktestapp.model.vk.VkHttpClient;
import com.uncreated.vktestapp.model.vk.VkImage;

class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    private final LruCache<String, Bitmap> mImageCache;
    private final VkImage mVkImage;
    private final String mMemoryKey;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final ImageLoadedCallback mImageLoadedCallback;

    public ImageLoader(LruCache<String, Bitmap> imageCache, VkImage vkImage, int maxWidth,
                       int maxHeight, ImageLoadedCallback imageLoadedCallback) {
        mImageCache = imageCache;
        mVkImage = vkImage;
        mMemoryKey = ImageCache.makeMemoryKey(vkImage, maxWidth, maxHeight);
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mImageLoadedCallback = imageLoadedCallback;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null) {
            mImageCache.put(mMemoryKey, bitmap);
        }
        mImageLoadedCallback.onImageLoaded(bitmap);
    }

    /**
     * Ищет изображение на диске, если его нет, то скачивает его и заисывает на диск,
     * если скачать не удалось, возвращает null
     */
    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = ImageCache.getInstance().loadFromDiskCache(mVkImage);
        if (bitmap == null) {
            bitmap = VkHttpClient.getInstance().loadImage(mVkImage);
            if (bitmap != null) {
                ImageCache.getInstance().saveToDiskCache(mVkImage, bitmap);
            }
        }
        if (bitmap != null)
            return scaleBitmap(bitmap, mMaxWidth, mMaxHeight);
        return null;
    }

    private static Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return bitmap;
        }
        float scaleWidth = (float) maxWidth / (float) bitmap.getWidth();
        float scaleHeight = (float) maxHeight / (float) bitmap.getHeight();
        float minScale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

        return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * minScale),
                (int) (bitmap.getHeight() * minScale), false);
    }
}
