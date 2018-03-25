package com.uncreated.vktestapp.model.vk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Основной класс vk api, хранящий текущую сессию и пользователя
 */
public class Vk {
    private static Vk ourInstance = new Vk();

    public static Vk getInstance() {
        return ourInstance;
    }

    private Context mContext;

    private VkSession mVkSession;
    private VkUser mVkUser;

    private Vk() {
    }

    public static void init(Context context) {
        if (ourInstance.mVkSession == null) {
            ourInstance.mContext = context;
            ourInstance.mVkSession = VkSession.load(context);
        }
        VkHttpClient.init(context);
    }

    @Nullable
    VkSession getVkSession() {
        return mVkSession;
    }

    void setVkSession(@NonNull VkSession vkSession) {
        mVkSession = vkSession;
        mVkSession.save(mContext);
    }

    @Nullable
    VkUser getVkUser() {
        return mVkUser;
    }

    void setVkUser(@NonNull VkUser vkUser) {
        mVkUser = vkUser;
    }
}
