package com.uncreated.vktestapp.model.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import com.uncreated.vktestapp.model.vk.VkImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Класс хранит масштабированные изображения в памяти,
 * оригинальные изображения на диске,
 * а отсутствующие скачивает с сервера
 */
public class ImageCache {
    private static final int MEMORY_SIZE = 1024 * 1024 * 100;//100mb

    private static ImageCache ourInstance = new ImageCache();

    public static ImageCache getInstance() {
        return ourInstance;
    }

    private ImageCache() {
    }

    public static void init(Context context) {
        ourInstance.mCacheDir = context.getCacheDir().getPath() + File.separator + "images";
    }

    /**
     * Масштабированные изображения
     */
    //TODO: заменить размер
    private LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(MEMORY_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private String mCacheDir;

    /**
     * Возвращает bitmap, если он хранится в памяти
     * в ином случае, возвращает null и асинхронно запрашивает изображение с диска,
     * если изображения там нет, то асинхронно запрашивает изображение с сервера,
     * сохраняет на диск оригинальное и кладёт в память масштабированное изображение,
     * после чего вызывает onPhotoLoadedListener
     *
     * @param vkImage ссылка на изображение на сервере, он же ключ для поиска в хеше
     */
    @Nullable
    public Bitmap loadImage(@NonNull VkImage vkImage, int maxWidth, int maxHeight,
                            @NonNull ImageLoadedCallback imageLoadedCallback) {
        Log.d("PhotoCache", vkImage.getImageId());
        String memoryKey = makeMemoryKey(vkImage, maxWidth, maxHeight);
        Bitmap bitmap = mMemoryCache.get(memoryKey);
        if (bitmap == null) {
            new ImageLoader(mMemoryCache, vkImage, maxWidth, maxHeight, imageLoadedCallback).execute();
        }
        return bitmap;
    }

    /**
     * Ключ для масштабированного изображения в памяти
     */
    static String makeMemoryKey(@NonNull VkImage vkImage, int maxWidth, int maxHeight) {
        return maxWidth + "x" + maxHeight + "_" + vkImage.getImageId();
    }

    /**
     * Имя файла на дисковом хеше
     */
    private String makeImagePath(VkImage vkImage) {
        return mCacheDir + vkImage.getImageId();
    }

    synchronized void saveToDiskCache(VkImage vkImage, Bitmap bitmap) {
        String imagePath = makeImagePath(vkImage);
        try (FileOutputStream outputStream = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Считывает и возвращает изображение из дискового кеша,
     * если изображения нет, возвращает null
     *
     * @param vkImage
     * @return
     */
    synchronized Bitmap loadFromDiskCache(VkImage vkImage) {
        String imagePath = makeImagePath(vkImage);
        return BitmapFactory.decodeFile(imagePath);
    }
}
