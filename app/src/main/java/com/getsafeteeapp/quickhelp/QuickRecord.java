package com.getsafeteeapp.quickhelp;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.view.KeyEvent;

import com.getsafeteeapp.audiorecorder.activities.VoiceRecorderMainActivity;

public class QuickRecord  extends Service{

    private static final String TAG = QuickRecord.class.getSimpleName();
    private final IBinder mBinder = new ServiceBinder();
    private BroadcastReceiver mBroadcastReceiver;

    public class ServiceBinder extends Binder {
        public QuickRecord getService() {
            return QuickRecord.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        sendBroadcast(new Intent("com.safetee.quickhelp.SERVICE_QUICK_RECORD_STARTED"));
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.safetee.quickhelp.SERVICE_QUICK_RECORD_STARTED")) {
                    //

                }
            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.safetee.quickhelp.SERVICE_QUICK_RECORD_STARTED");
        registerReceiver(mBroadcastReceiver, mIntentFilter);



    }

    public boolean onKeyDown(KeyEvent event){
        if((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)){
            startActivity(new Intent(QuickRecord.this, VoiceRecorderMainActivity.class));
        }
        return true;
    }
}
