package com.getsafetee.util;


import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONObject;

public class SingleUploadBroadcastReceiver extends UploadServiceBroadcastReceiver {

    public interface Delegate {
        void onProgress(int progress);
        void onProgress(long uploadedBytes, long totalBytes);
        void onError(Exception exception);
        void onCompleted(Exception e, JSONObject result);
        void onCancelled();
    }

    private String mUploadID;
    private Delegate mDelegate;

    public void setUploadID(String uploadID) {
        mUploadID = uploadID;
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    //@Override
    public void onProgress(String uploadId, int progress) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(progress);
        }
    }

    //@Override
    public void onProgress(String uploadId, long uploadedBytes, long totalBytes) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(uploadedBytes, totalBytes);
        }
    }

    //@Override
    public void onError(String uploadId, Exception exception) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onError(exception);
        }
    }

    //@Override
    public void onCompleted(Exception e, JSONObject result) {
            mDelegate.onCompleted(e, result);
    }

    //@Override
    public void onCancelled(String uploadId) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onCancelled();
        }
    }
}
