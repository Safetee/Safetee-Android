package com.getsafeteeapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.getsafeteeapp.MainActivity2;
import com.getsafeteeapp.SessionManager;
import com.getsafeteeapp.safetee.R;
import com.getsafeteeapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = SignupActivity.class.getSimpleName();

    private EditText fullnameEd;
    private EditText phoneNumber;
    private EditText passwordEd;
    private Button signup;
    private RadioButton bbb;


    private SessionManager session;

    private String fullname;
    private String phone;
    private String password;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
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

        fullnameEd = (EditText) findViewById(R.id.fullname);
        phoneNumber = (EditText) findViewById(R.id.phone);
        passwordEd = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.sign_up_button);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                fullname = fullnameEd.getText().toString().trim();
                phone = phoneNumber.getText().toString().trim();
                password = passwordEd.getText().toString().trim();

                // Check for empty data in the form
                if (!fullname.isEmpty()  && !phone.isEmpty() &&  !password.isEmpty()) {
                    // sign up user
                    signupUser(fullname, phone, password);
                } else {
                    // Prompt user to enter credentials
                    showMessage("Oops", "Please all fields are required", "Ok");
                }
            }
        });

    }

    private void signupUser(final String fullname, final String phone, final String password){

        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Method.POST, Constants.SIGN_UP_URL, new Response.Listener<String>() {
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
                        // set user id
                        session.setUid(uid);
                        // set user name
                        session.setUName(name);
                        // set user pin
                        session.setUPin(password);
                        // set user phone
                        session.setUPhone(phone);

                        // welcome message and got main activity
                        welcomeMessage("Welcome onboard", successMsg, "Continue");

                    } else {

                        // Error occurred in registration. Get the error
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
                params.put("name", fullname);
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

    public void welcomeMessage(String title, String msg, String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void gosignin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

}