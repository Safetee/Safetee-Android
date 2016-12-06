package com.getsafetee.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

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
import com.getsafetee.IntroActivity;
import com.getsafetee.MainActivity;
import com.getsafetee.SessionManager;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    public static final String TAG = SettingActivity.class.getSimpleName();

    private AutoCompleteTextView emailed;
    private EditText passworded;
    private EditText phoned;
    private EditText pined;
    private Button save;



    private SessionManager session;

    private String email;
    private String password;
    private String phone;
    private String pin;

    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Session manager
        session = new SessionManager(this);



        emailed = (AutoCompleteTextView) findViewById(R.id.email);
        passworded = (EditText) findViewById(R.id.password);
        phoned = (EditText) findViewById(R.id.phone);
        pined = (EditText) findViewById(R.id.pin);
        save = (Button) findViewById(R.id.save_in_button);

        // set texts
        emailed.setText(session.getUEmail());
        phoned.setText(session.getUPhone());
        pined.setText(session.getUPin());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //
        // Login button Click Event
        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                email = emailed.getText().toString().trim();
                password = passworded.getText().toString().trim();
                phone = phoned.getText().toString().trim();
                pin = pined.getText().toString().trim();
                // save user pin
                session.setUPin(pin);

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty() && !phone.isEmpty() && !pin.isEmpty()) {
                    // save user setting
                    saveSetting(email, password, phone);
                } else {
                    // Prompt user to enter credentials
                    showMessage("Oops", "All fields are required", "Ok");
                }
            }
        });

    }
    private void saveSetting(final String email, final String password, final String phone){

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
                        // user email
                        String email = jObj.getString("email");
                        // user email
                        String phone = jObj.getString("phone_no");

                        // set user id
                        session.setUid(uid);
                        // set user name
                        session.setUName(name);
                        // set user email
                        session.setUEmail(email);
                        // set phone
                        session.setUPhone(phone);

                        // Prompt user
                        showMessage("Settings", successMsg, "Ok");


                    } else {

                        // Error occurred in setting. Get the error
                        // get message
                        String errorMsg = jObj.getString("message");
                        // show error message to user

                        showMessage("Oops", errorMsg, "Try Again");
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
                    showMessage("Oops", "Network connection failed", "Try again");
                }
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("uid", session.getUid());
                params.put("email", email);
                params.put("password", password);
                params.put("phone_no", phone);
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