package com.getsafetee.safetytips;


import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getsafetee.app.AppController;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TipsLocal extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = TipsLocal.class.getSimpleName();


    int update = 0;

    TipsListFragment mTipsListFragment;
    Toolbar toolbar;
    TipsDatabase mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_local_main);

        mDatabase = new TipsDatabase(getApplicationContext());



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            checkUpdate();
            switchToTips();


    }

    private void switchToTips() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, getTipsListFragment());
        transaction.commit();
    }


    private TipsListFragment getTipsListFragment() {
        if (mTipsListFragment == null)
            mTipsListFragment = new TipsListFragment();

        return mTipsListFragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void checkUpdate(){
        // Creating volley request obj
        JsonArrayRequest stringRequest = new JsonArrayRequest(Constants.TIPS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "RESPONSE RECS: " + response.toString());


                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {

                                //
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    //Toast.makeText(getApplicationContext(), String.valueOf(mDatabase.findTip(obj.getString("_id"))), Toast.LENGTH_LONG).show();
                                    if(mDatabase.findTip(obj.getString("_id")) == 0) {
                                        update += mDatabase.findTip(obj.getString("_id"));
                                        mDatabase.addTip(obj.getString("title"), obj.getString("body"), obj.getString("_id"), obj.getString("sender"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        //
                        if(update > 0){
                            //Toast.makeText(getApplicationContext(), "New safety tips downloaded", Toast.LENGTH_LONG).show();
                        }else{
                           // Toast.makeText(getApplicationContext(), "No new safety tips downloaded", Toast.LENGTH_LONG).show();
                        }
                        //Toast.makeText(getApplicationContext(), String.valueOf(update), Toast.LENGTH_LONG).show();

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

}


