package com.safeteeapp.util;

import android.app.Application;

import com.safeteeapp.safetee.BuildConfig;

import net.gotev.uploadservice.UploadService;


public class AppInitializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = this.getPackageName();

    }
}