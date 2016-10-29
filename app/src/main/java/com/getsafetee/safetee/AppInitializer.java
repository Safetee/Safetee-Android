package com.getsafetee.safetee;

import android.app.Application;

import net.gotev.uploadservice.UploadService;


public class AppInitializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = this.getPackageName();

    }
}
