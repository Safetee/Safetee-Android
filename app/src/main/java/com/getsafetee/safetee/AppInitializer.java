package com.getsafetee.safetee;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

/**
 * Created by i.Sec on 10/29/2016.
 */

public class AppInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = "com.getsafetee.safetee";

    }
}
