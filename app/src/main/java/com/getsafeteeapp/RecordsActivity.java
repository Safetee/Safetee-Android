package com.getsafeteeapp;

import com.getsafeteeapp.adater.CustomListAdapter;
import com.getsafeteeapp.app.AppController;
import com.getsafeteeapp.model.GetRecords;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getsafeteeapp.safetee.R;
import com.getsafeteeapp.util.Constants;

public class RecordsActivity extends AppCompatActivity {
    // Log tag
    private static final String TAG = RecordsActivity.class.getSimpleName();


    private ProgressDialog pDialog;
    private List<GetRecords> recordsList = new ArrayList<GetRecords>();
    private ListView listView;
    private CustomListAdapter adapter;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_main);

        //
        session = new SessionManager(getApplicationContext());



        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(RecordsActivity.this, recordsList);
        listView.setAdapter(adapter);



        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please wait...");
        //pDialog.setCancelable(false);
        pDialog.show();


        // Creating volley request obj
        JsonArrayRequest stringRequest = new JsonArrayRequest(Constants.USER_RECORDS_URL+session.getUid(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "RESPONSE RECS: " + response.toString());
                        hidePDialog();

                        //
                        if(response.length() < 1){
                            //

                            showMessage("Oops", "You currently do not have any records.", "Ok");

                        } else {

                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {

                                //
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    final String shared = obj.getString("share");
                                    GetRecords getrecords = new GetRecords();
                                    getrecords.setTitle(obj.getString("recordname"));
                                    getrecords.setThumbnailUrl("https://cdn2.iconfinder.com/data/icons/music-sound-2/512/Music_13-512.png");
                                    getrecords.setRemark(shared);
                                    getrecords.setCreated(obj.getString("created"));
                                    getrecords.setAudio(obj.getString("record"));
                                    getrecords.setRid(obj.getString("_id"));

                                    // adding record to records array
                                    recordsList.add(getrecords);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        // notifying list adapter about data changes
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
                if (error instanceof NoConnectionError){
                   hidePDialog();
                    showMessage("Aww! snap", "There's no active internet connection.", "Try again");
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
        //
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String gettitle = ((TextView) view.findViewById(R.id.title)).getText().toString();
                String getaudio = ((TextView) view.findViewById(R.id.audio)).getText().toString();
                String getrid = ((TextView) view.findViewById(R.id.rid)).getText().toString();
                String getremark = ((TextView) view.findViewById(R.id.remark)).getText().toString();
                //
                //
                Intent i = new Intent(RecordsActivity.this, RecordView.class);
                i.putExtra("title", gettitle);
                i.putExtra("audio", getaudio);
                i.putExtra("rid", getrid);
                i.putExtra("remark", getremark);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    //
    public void backgo(View view){
        //
        Intent i = new Intent(this, MainActivity2.class);
        startActivity(i);
        finish();
    }
    //
    public void showMessage(String title, String msg, final String btn){
        //
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                        if (btn == "Try again") {
                            //
                            Intent i = new Intent(getApplicationContext(), RecordsActivity.class);
                            startActivity(i);
                            //
                        }
                    }
                });
        //
        AlertDialog alert = builder.create();
        alert.show();
    }

}
