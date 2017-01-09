package com.getsafetee.safetytips;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getsafetee.SessionManager;
import com.getsafetee.app.AppController;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TipsUpdate extends IntentService {
    Handler mHandler;
    final  int INTERVAL = 1000 * 60 * 4; // 4 minutes
    Runnable mHandlerTask;
    private TipsDatabase mDatabase;
    private SessionManager session;
    private static final int NOTIFY_ID = 1337;
    private int update = 0;

    public TipsUpdate() {
        super(TipsUpdate.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent){
        mDatabase = new TipsDatabase(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        checkUpdate();

    }

    //
    public void checkUpdate(){
        // Creating volley request obj
        JsonArrayRequest stringRequest = new JsonArrayRequest(Constants.TIPS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Safetee", "RESPONSE RECS: " + response.toString());


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {

                            //
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                //Toast.makeText(getApplicationContext(), String.valueOf(mDatabase.findTip(obj.getString("_id"))), Toast.LENGTH_LONG).show();
                                if(mDatabase.findTip(obj.getString("_id")) == 0) {
                                    update = update + mDatabase.findTip(obj.getString("_id"));
                                    mDatabase.addTip(obj.getString("title"), obj.getString("body"), obj.getString("_id"), obj.getString("sender"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if(update > 0){
                            //Toast.makeText(getApplicationContext(), "New safety tips downloaded", Toast.LENGTH_LONG).show();
                             String updateCount;
                            if(update == 1){
                                updateCount = "a new safety tip is available";
                            }else{
                                updateCount = "new safety tips are  available";
                            }
                            alertUser("Hello "+ session.getUName() + ", " + updateCount);
                        }else{
                            //Toast.makeText(getApplicationContext(), "No new safety tips downloaded", Toast.LENGTH_LONG).show();
                            //alertUser("No new safety tips");
                        }
                        //Toast.makeText(getApplicationContext(), String.valueOf(update), Toast.LENGTH_LONG).show();
                        // reset update to 0 back
                        update = 0;
                    }




                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
                if (error instanceof NoConnectionError){

                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    //
    public void alertUser(final String msg){
        Intent intent = new Intent(this, TipsLocal.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
                .setContentTitle("Safetee")
                .setContentText(msg)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent);

        noti.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.setAutoCancel(true);

        notificationManager.notify(NOTIFY_ID, noti.build());
    }

}
