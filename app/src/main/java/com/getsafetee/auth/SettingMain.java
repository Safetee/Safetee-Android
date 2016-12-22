package com.getsafetee.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getsafetee.DonateToNGO;
import com.getsafetee.MainActivityAdapter;
import com.getsafetee.SessionManager;
import com.getsafetee.audiorecorder.activities.VoiceRecorderMainActivity;
import com.getsafetee.circleoffriends.FriendsList;
import com.getsafetee.incidencereport.ReportActivity;
import com.getsafetee.safetee.R;
import com.getsafetee.safetytips.SafetyTips;
import com.getsafetee.util.Constants;
import com.getsafetee.util.ShowMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingMain extends AppCompatActivity {
    ListView list;
    SessionManager session;
    ShowMessage message;
    LayoutInflater lf;
    View renameprompt;
    private RadioGroup uploadoption;
    private RadioButton noautoupload;
    private RadioButton autoupload;
    private String getAutoupload;
    private String getPin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(this);

        if (session.getUPin().length() != 4 || session.getUName().isEmpty()) {
            message.message("Account", "You are required to complete settings.", "Dismiss");
        }

        final String[] itemname ={
                "Full Name",
                "Phone Number",
                "Pin Code",
                "Auto Upload",
                "About",
                "Terms of Service",
                "Privacy Policy",
                "Log Out"
        };

        if(session.getAutoupload()){
            getAutoupload = "On";
        }else{
            getAutoupload = "Off";
        }

        if(session.getUPin().length() == 4 && !session.getUPin().isEmpty()){
            getPin = "****";
        }else{
            getPin = "not set";
        }

        final String[] itemabout = {
                session.getUName(),
                session.getUPhone(),
                getPin,
                getAutoupload,
                "",
                "",
                "",
                ""
        };

        final Integer[] imgid = {
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1
        };

        SettingsAdapter adapter = new SettingsAdapter(this, itemname, itemabout, imgid);
        list = (ListView) findViewById(R.id.list);
        //
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String menu = itemname[+position].toString();
                //
                switch (menu) {
                    case "Auto Upload":
                        autoUpload();
                        break;
                    case "Full Name":
                        renameItem("name");
                        break;
                    case "Phone Number":
                        renameItem("phone");
                        break;
                    case "Pin Code":
                        renameItem("pin");
                        break;
                    case "Log Out":
                        logoutUser();
                        break;


                }

            }
        });
    }

    public void autoUpload() {
        lf = LayoutInflater.from(getApplicationContext());
        View autouploadprompt = lf.inflate(R.layout.record_user_settings, null);
        uploadoption = (RadioGroup) autouploadprompt.findViewById(R.id.uploadoption);
        autoupload = (RadioButton) autouploadprompt.findViewById(R.id.autoupload);
        noautoupload = (RadioButton) autouploadprompt.findViewById(R.id.noautoupload);

        // check if auto upload is set already
        if(session.getAutoupload()){
            autoupload.setChecked(true);
        } else {
            noautoupload.setChecked(true);
        }
        // if auto upload is clicked
        autoupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                session.setAutoupload(true);
                Toast.makeText(getApplicationContext(), "Your recordings will now be uploaded to safetee immediately after recording stops.", Toast.LENGTH_LONG).show();
            }
        });
        // if not auto upload is clicked
        noautoupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setAutoupload(false);
                Toast.makeText(getApplicationContext(), "Your recordings will not be uploaded to safetee immediately after recording stops.", Toast.LENGTH_LONG).show();
            }
        });
        final AlertDialog.Builder autouploadbuilder = new AlertDialog.Builder(this);
        autouploadbuilder.setView(autouploadprompt);
        autouploadbuilder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        });
        //
        AlertDialog autouploaddialog = autouploadbuilder.create();
        autouploaddialog.show();
    }

    // reset fullname, phone number, pin
    public void renameItem(final String type) {
        lf = LayoutInflater.from(getApplicationContext());
        renameprompt = lf.inflate(R.layout.input_for_dialog, null);
        final AlertDialog.Builder renamebuilder = new AlertDialog.Builder(this);
        renamebuilder.setView(renameprompt);
        final EditText input = (EditText) renameprompt.findViewById(R.id.input);
        final TextView heading = (TextView) renameprompt.findViewById(R.id.heading);
        if(type.equals("name")){
            heading.setText("FULL NAME");
            input.setText(session.getUName());
        } else if(type.equals("phone")){
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setMaxLines(11);
            heading.setText("PHONE NUMBER");
            input.setText(session.getUPhone());
        } else if(type.equals("pin")){
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD );
            input.setMaxLines(4);
            heading.setText("PIN CODE");
        }
        renamebuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
                final String inputVal = input.getText().toString();
                if (inputVal.length() > 0) {
                    if(type.equals("name")){
                        if(inputVal.contains(" ")){

                            saveSetting(inputVal, "");
                        }else{
                            Toast.makeText(getApplicationContext(), "Please set a proper full name", Toast.LENGTH_LONG).show();
                        }
                    } else if(type.equals("phone")){
                        if(inputVal.length() == 11) {
                            saveSetting("", inputVal);
                        }else{
                            Toast.makeText(getApplicationContext(), "Phone number should not be less or more than 11 digits", Toast.LENGTH_LONG).show();
                        }
                    } else if(type.equals("pin")){
                        if(inputVal.length() == 4) {
                            session.setUPin(inputVal);
                            Toast.makeText(getApplicationContext(), "Pin code successfully set", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Pin code should not be less or more than 4 digits", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });
        //
        AlertDialog renamedialog = renamebuilder.create();
        renamedialog.show();
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

    // save name or number
    private void saveSetting(final String fullname, final String phone){
        final ProgressBar loading = (ProgressBar) renameprompt.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SETTING_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SAFETEE", "Request Response: " + response);
                loading.setVisibility(View.INVISIBLE);


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
                        // user fullname
                        String name = jObj.getString("fullname");
                        // user number
                        String phone = jObj.getString("phone_no");
                        // set user name
                        session.setUName(name);
                        // set phone
                        session.setUPhone(phone);


                        // Prompt user
                        Toast.makeText(getApplicationContext(), successMsg, Toast.LENGTH_LONG).show();


                    } else {

                        // Error occurred in setting. Get the error
                        // get message
                        String errorMsg = jObj.getString("message");
                        // show error message to user
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Network connection failed", Toast.LENGTH_LONG).show();
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

    //
    private void logoutUser() {
        session.setLogin(false);
        session.freeUser();
        session.freeCircleFriends();
        // Launching the login activity
        Intent intent = new Intent(SettingMain.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

}


