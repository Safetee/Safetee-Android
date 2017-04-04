package com.safeteeapp.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.safeteeapp.MainActivity2;
import com.safeteeapp.safetee.R;

public class PhoneVerification extends BroadcastReceiver{


    public static final String SMS_EXTRA_NAME = "safetee";


    public SmsMessage messages[] = null;

    private void showNotification(Context context, String sms) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity2.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Safetee")
                        .setContentText(sms);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        try {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                messages = null;
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    String sms = "";
                    String mobile = "Trulo NG";

                    for (int i = 0; i < pdus.length; i++) {
                        SmsMessage tmp = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String senderMobile = tmp.getOriginatingAddress();

                        if (senderMobile.equals(mobile)) {
                            sms = tmp.getMessageBody();
                            String getcode = sms.replaceAll("[^0-9]", "");
                            showNotification(context, sms);

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Exception caught", e.getMessage());
        }


    }
}
