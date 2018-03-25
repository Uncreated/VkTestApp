package com.uncreated.vktestapp;

import android.app.Application;

import com.uncreated.vktestapp.model.vk.Vk;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Vk.init(this);
    }
}
