package com.safeteeapp;

import com.safeteeapp.adapter.CustomListAdapter;
import com.safeteeapp.app.AppController;
import com.safeteeapp.model.GetRecords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.Constants;

import com.safeteeapp.util.MiscellanousRequests;
import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.Pay;
import com.interswitchng.sdk.payment.model.PurchaseResponse;

public class DonateToNGO extends AppCompatActivity {
    // Log tag
    private static final String TAG = RecordsActivity.class.getSimpleName();


    private ProgressDialog pDialog;
    private List<GetRecords> recordsList = new ArrayList<GetRecords>();
    private ListView listView;
    private CustomListAdapter adapter;
    private SessionManager session;
    private MiscellanousRequests request;
    private String gettitle, getrid;
    private String  paymethod = "";
    private String amount = "1000";
    // to make requests
    private List<String> keys;
    private List<String> values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_main);

        //
        session = new SessionManager(getApplicationContext());
        request = new MiscellanousRequests(getApplicationContext());
        // interswitch
        Passport.overrideApiBase(Passport.SANDBOX_API_BASE);
        Payment.overrideApiBase(Payment.SANDBOX_API_BASE);



        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(DonateToNGO.this, recordsList);
        listView.setAdapter(adapter);



        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please wait...");
        //pDialog.setCancelable(false);
        pDialog.show();


        // Creating volley request obj
        JsonArrayRequest stringRequest = new JsonArrayRequest(Constants.GET_AGENCIES_NGO,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "RESPONSE RECS: " + response.toString());
                        hidePDialog();

                        //
                        if(response.length() < 1){
                            //

                            showMessage("Oops", "No NGOs around", "Ok");

                        } else {

                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {

                                //
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    GetRecords getrecords = new GetRecords();
                                    getrecords.setTitle(obj.getString("name"));
                                    //getrecords.setThumbnailUrl("");
                                    getrecords.setRemark("");
                                    getrecords.setCreated(obj.getString("address"));
                                    getrecords.setAudio("");
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
                gettitle = ((TextView) view.findViewById(R.id.title)).getText().toString();
                getrid = ((TextView) view.findViewById(R.id.rid)).getText().toString();
                // enter amount to donate
                amountdonate();
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

    private void popformdonate(){
        final AlertDialog.Builder donateform = new AlertDialog.Builder(DonateToNGO.this);
                donateform.setTitle("Select Payment Method")
                .setSingleChoiceItems(R.array.paymentsmethods, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Resources res = getResources();
                        paymethod = res.getStringArray(R.array.paymentsmethods)[which];

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (paymethod.equals("Interswitch")) {
                            donateWithISW(gettitle, getrid);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertform = donateform.create();
        alertform.show();
    }

    private void donateWithISW(final String ngotitle, final String ngoid) {

        RequestOptions options = RequestOptions.builder()
                .setClientId(Constants.ISW_CLIENT_ID)
                .setClientSecret(Constants.ISW_CLIENT_SECRET)
                .build();

        Pay pay = new Pay(DonateToNGO.this, "Safetee", "Donate With Interswitch to " + ngotitle, amount, "NGN", options,
                new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        showMessage("Safetee", "An error occurred, please try again later.", "OK");
                        //Util.notify(getApplicationContext(), "error", error.getMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(PurchaseResponse response) {
                        String f = "Your donation of =N=" + amount + ".00 to " + ngotitle + " was successful.";
                        keys = Arrays.asList("donor","recipient","amount");
                        values = Arrays.asList(session.getUid(), getrid, amount);
                        request.makeRequest(Constants.DONATE_URL, keys, values, 3);
                         showMessage("Safetee", f, "OK");
                        //Util.notify(getContext(), "Success", f, "Close", false);

                    }
                });
        pay.start();
    }

    //
    public void amountdonate(){
        //
        LayoutInflater lf = LayoutInflater.from(getApplicationContext());
        View amountprompt = lf.inflate(R.layout.amount_donate, null);
        final AlertDialog.Builder amountbuilder = new AlertDialog.Builder(this);
        amountbuilder.setView(amountprompt);
        final EditText amountinput = (EditText) amountprompt.findViewById(R.id.amount);
        amountinput.setText(amount);
        amountbuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                        if (amountinput.getText().length() > 0) {
                            amount = amountinput.getText().toString();
                        }
                        //popformdonate();
                        // pop up interswitch directly instead
                        donateWithISW(gettitle, getrid);
                    }
                });
        //
        AlertDialog alertamount = amountbuilder.create();
        alertamount.show();
    }


}
