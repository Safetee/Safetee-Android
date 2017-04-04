package com.safeteeapp.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.safeteeapp.IntroActivity;
import com.safeteeapp.MainActivity2;
import com.safeteeapp.SessionManager;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.Constants;
import com.safeteeapp.util.ShowMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private BroadcastReceiver mBroadcastReceiver;
    public SmsMessage messages[] = null;

    private EditText phoned;
    private EditText passworded;
    private Button signin;



    private SessionManager session;
    private ShowMessage message;

    private String phone;
    private String password;

    private int ccode;

    private ProgressDialog pDialog;
    private TextView confirming;
    private TextView verifying;
    private String mPhoneNumber = "";
    // user id
    private String useruid = "";
    // user fullname
    private String username = "";
    // user phone
    private String userphone = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Session manager
        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(this);
        // generate confirmation code
        ccode = (int)(Math.random()*9000)+1000;
        // get device phone number
//        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        mPhoneNumber = tMgr.getLine1Number();

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
//
        phoned = (EditText) findViewById(R.id.phone);
        passworded = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.sign_in_button);
        confirming = (TextView) findViewById(R.id.confirming);
        verifying = (TextView) findViewById(R.id.verifying);

        //
        phoned.setInputType(InputType.TYPE_CLASS_PHONE);
        phoned.setText(mPhoneNumber);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //
        mBroadcastReceiver = new BroadcastReceiver() {
            //
            // get phone verification
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
                            String mobile = "Safetee VF";

                            for (int i = 0; i < pdus.length; i++) {
                                SmsMessage tmp = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                String senderMobile = tmp.getOriginatingAddress();
                                sms = tmp.getMessageBody();
                                String getcode = sms.replaceAll("[^0-9]", "");

                                if (senderMobile.equals(mobile) && getcode.equals(String.valueOf(ccode))) {
                                    LinearLayout verify;
                                    LinearLayout verified;
                                    verify = (LinearLayout) findViewById(R.id.verification);
                                    verified = (LinearLayout) findViewById(R.id.verified);
                                    verify.setVisibility(View.GONE);
                                    verify.setVisibility(View.INVISIBLE);
                                    verified.setVisibility(View.VISIBLE);
                                    pDialog.dismiss();
                                    pDialog.hide();
                                    verifying.setText("Your phone number was successfully verified, you may now continue");
                                    // set user login to true
                                    session.setLogin(true);
                                    session.setAutoupload(true);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }

            }
        };
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //registerReceiver(mBroadcastReceiver, mIntentFilter); // do not forget to activate back



        //
        // Login button Click Event
        signin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                phone = phoned.getText().toString().trim();
                password = passworded.getText().toString().trim();

                // Check for empty data in the form
                if (phone.isEmpty()) {
                    // Prompt user to enter credentials
                    message.message("Error", "Please enter your phone number to continue", "Dismiss");
                }else if(mPhoneNumber.length() > 0 && !phone.equals(mPhoneNumber)){
                    // incorrect number
                    message.message("Error", "Incorrect phone number", "Dismiss");
                } else {
                    // login user
                    checkLogin(phone);
                }
            }
        });

        }
   private void checkLogin(final String phone){

        pDialog.setMessage("Please wait...");
       pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SAFETEE", "Request Response: " + response);
                pDialog.hide();
                pDialog.dismiss();
                LinearLayout verify;
                LinearLayout signinform;
                signinform = (LinearLayout) findViewById(R.id.signin);
                verify = (LinearLayout) findViewById(R.id.verification);
                signinform.setVisibility(View.GONE);
                verify.setVisibility(View.VISIBLE);

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
                        useruid = jObj.getString("uid");
                        // user fullname
                        username = jObj.getString("fullname");
                        // user phone
                        userphone = jObj.getString("phone_no");

                        // set user id
                        session.setUid(useruid);
                        // set user name
                        session.setUName(username);
                        // set user phone
                        session.setUPhone(userphone);

                        //
                        pDialog.hide();
                        pDialog.dismiss();
                        signinform.setVisibility(View.GONE);
                        signinform.setVisibility(View.INVISIBLE);
                        verify.setVisibility(View.VISIBLE);
                        confirming.setText("Please wait while we verify your phone number " +phone);

                        // skip sms for now
                        gotoHome2();

                        // now wait for sms confirmation

                    } else {

                        // Error occurred in registration. Get the error
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
                message.message("Error", "Check your network connection", "Dismiss");
                pDialog.dismiss();
                pDialog.hide();
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("phone_no", phone);
                params.put("ccode", String.valueOf(ccode));
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


    public void showMessage(final String title, final String msg, final String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (btn.equals("Continue")) {
                            unregisterReceiver(mBroadcastReceiver);
                            Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                            startActivity(intent);
                            finish();
                        }
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

    // go to settings completion instead
    public void gotoHome(View view){
        session.setLogin(true);
        session.setAutoupload(true);
        Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    // go to settings completion instead
    public void gotoHome2(){
        session.setLogin(true);
        session.setAutoupload(true);
        Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }


}