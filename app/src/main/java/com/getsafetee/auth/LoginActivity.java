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
import com.getsafetee.MainActivity2;
import com.getsafetee.SessionManager;
import com.getsafetee.circleoffriends.FriendsList;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText phoned;
    private EditText passworded;
    private Button signin;

    private long VIBRATION_TIME = 300; // Length of vibration in milliseconds
    private long VIBRATION_PAUSE = 200;


    private SessionManager session;

    private String phone;
    private String password;

    private ProgressDialog pDialog;

    private long[] patternSuccess = {0, // Start immediately
            VIBRATION_TIME
    };

    private long[] patternFailure = {0, // Start immediately
            VIBRATION_TIME, VIBRATION_PAUSE, VIBRATION_TIME, // Each element then alternates between vibrate, sleep, vibrate, sleep...
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Session manager
        session = new SessionManager(this);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take them to main activity
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
            this.finish();
        }

        // check if user has auto toured before
            if (!session.getToured()){
                session.setTour(true);
                //startTour();
            }

        phoned = (EditText) findViewById(R.id.phone);
        passworded = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.sign_in_button);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //
        // Login button Click Event
        signin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                phone = phoned.getText().toString().trim();
                password = passworded.getText().toString().trim();

                // Check for empty data in the form
                if (phone.isEmpty()) {
                    // Prompt user to enter credentials
                    showMessage("Error", "Please enter your phone number to continue", "Dismiss");
                }else if(phone.length() != 11){
                    // incomplete number
                    showMessage("Error", "Phone number is not complete", "Dismiss");
                } else {
                    // login user
                    checkLogin(phone);
                }
            }
        });

        }
   private void checkLogin(final String phone){

        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
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
                    //if success is 1 then signup went well
                    if (success == 1) {
                        // we declare strings to get user info
                        // success message
                        String successMsg = jObj.getString("message");
                        // user id
                        String uid = jObj.getString("uid");
                        // user fullname
                        String name = jObj.getString("fullname");
                        // user phone
                        String phone = jObj.getString("phone_no");

                        // use the strings that has user info tied to them to do whatever
                        // store them to sharedpreference or to sqlite
                        // continue to login activity or mainactivity

                        // set user login to true
                        session.setLogin(true);
                        session.setAutoupload(true);
                        // set user id
                        session.setUid(uid);
                        // set user name
                        session.setUName(name);
                        // set user phone
                        session.setUPhone(phone);

                        // redirect to main activity

                        Intent intent = new Intent(getApplicationContext(), FriendsList.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // get message
                        String errorMsg = jObj.getString("message");
                        // show error message to user
                        showMessage("Error", errorMsg, "Dismiss");
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
                    showMessage("Error", "Network connection failed", "Dismiss");
                }
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("phone_no", phone);
                return params;
            }
        };
        //Add the request to the request queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

   }

    public void tour(View v) {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
    }

    public void startTour() {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
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

    public void gosignup(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();

    }

}