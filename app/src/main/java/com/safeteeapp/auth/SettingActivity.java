package com.safeteeapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.safeteeapp.MainActivity2;
import com.safeteeapp.SessionManager;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.Constants;
import com.safeteeapp.util.ShowMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    public static final String TAG = SettingActivity.class.getSimpleName();

    private EditText fullnameed;
    private EditText phoned;
    private EditText pined;
    private Button save;



    private SessionManager session;
    ShowMessage message;

    private String fullname;
    private String phone;
    private String pin;

    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        // Session manager
        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(this);

        if (session.getUPin().length() != 4 || session.getUName().isEmpty()) {
            message.message("Error", "You are required to complete settings.", "Dismiss");
        }

        fullnameed = (EditText) findViewById(R.id.fullname);
        phoned = (EditText) findViewById(R.id.phone);
        pined = (EditText) findViewById(R.id.pin);
        save = (Button) findViewById(R.id.save_in_button);

        // set texts
        fullnameed.setText(session.getUName());
        phoned.setText(session.getUPhone());
        pined.setText(session.getUPin());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //
        // save changes button Click Event
        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                fullname = fullnameed.getText().toString().trim();
                phone = phoned.getText().toString().trim();
                pin = pined.getText().toString().trim();



                // Check for empty data in the form
                if (fullname.isEmpty() || pin.isEmpty()) {
                    // Prompt user to enter credentials
                    message.message("Error", "All fields are required", "Dismiss");
                } else if(pin.length() != 4){
                    message.message("Error", "Pin must not be less or more than 4 digits", "Dismiss");
                } else {
                    // if it's only pin user change then no need to go online
                    if(session.getUName().equals(fullname) && session.getUPhone().equals(phone)){
                        // save user pin
                        session.setUPin(pin);
                        pined.setText(pin);
                        message.message("Success", "Pin changed successfully", "Click Here to Get Started");
                    } else {
                        // go online and save user new setting
                        // save user pin
                        session.setUPin(pin);
                        pined.setText(pin);
                        saveSetting(fullname, phone);

                    }
                }
            }
        });

        if (session.getUPin().length() == 4 && !session.getUName().isEmpty()) {
            startActivity(new Intent(SettingActivity.this, MainActivity2.class));
            finish();
        }

    }
    private void saveSetting(final String fullname, final String phone){

        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Method.POST, Constants.SETTING_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SAFETEE", "Request Response: " + response);
                pDialog.hide();

                //get response from api

                try {

                    // request response
                    JSONObject jObj = new JSONObject(response);
                    // get response value
                    int success = jObj.getInt("success");
                    //if success is 1 then signup
                    if (success == 1) {
                        // we declare strings to get user info
                        // success message
                        String successMsg = jObj.getString("message");
                        // user id
                        String uid = jObj.getString("uid");
                        // user fullname
                        String name = jObj.getString("fullname");
                        // user number
                        String phone = jObj.getString("phone_no");

                        // set user id
                        session.setUid(uid);
                        // set user name
                        session.setUName(name);
                        // set phone
                        session.setUPhone(phone);

                        // set texts
                        fullnameed.setText(name);
                        phoned.setText(phone);


                        // Prompt user
                        message.message("Success", "Pin changed successfully", "Click Here to Get Started");


                    } else {

                        // Error occurred in setting. Get the error
                        // get message
                        String errorMsg = jObj.getString("message");
                        // show error message to user

                        message.message("Error", errorMsg, "Dismiss");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    Log.i("SAFETEE", "Request Response: " + "Network connection failed");
                    message.message("Error", "Network connection failed", "Dismiss");
                }
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                final String getfname;
                final String getfphone;
                if(fullname.isEmpty() || fullname.length() < 0){
                    getfname = session.getUName();
                }else{
                    getfname = fullname;
                }
                //
                if(phone.isEmpty() || phone.length() < 11){
                    getfphone = session.getUPhone();
                }else{
                    getfphone = phone;
                }
                params.put("uid", session.getUid());
                params.put("fullname", getfname);
                params.put("phone_no", getfphone);
                return params;
            }
        };
        //Add the request to the request queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }



    public void showMessage(String title, String msg, String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}