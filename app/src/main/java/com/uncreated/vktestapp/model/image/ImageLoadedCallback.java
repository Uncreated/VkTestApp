package com.uncreated.vktestapp.model.image;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface ImageLoadedCallback {

    void onImageLoaded(@Nullable Bitmap bitmap);
}
