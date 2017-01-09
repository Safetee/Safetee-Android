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
import android.view.ViewGroup;
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
import com.getsafetee.AboutActivity;
import com.getsafetee.OrganizationView;
import com.getsafetee.SessionManager;
import com.getsafetee.safetee.R;
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
    private RadioGroup uploadoption, discreetoption;
    private RadioButton noautoupload, discreet;
    private RadioButton autoupload, nodiscreet;
    private String getAutoupload, getDiscreet;
    private String getPin;
    private String oldpin;
    private int settingPosition;
    private String[] itemabout;
    private SettingsAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(this);

        if (session.getUPin().length() != 4 || session.getUName().isEmpty()) {
            message.message("Error", "You are required to complete settings.", "Dismiss");
        }

        final String[] itemname ={
                "Full Name",
                "Phone Number",
                "Pin Code",
                "Auto Upload",
                "Safetee Mode",
                "About",
                "F.A.Q",
                "Terms of Use",
                "Privacy Policy",
                "Log Out"
        };

        if(session.getAutoupload()){
            getAutoupload = "On";
        }else{
            getAutoupload = "Off";
        }

        if(session.isDiscreet()){
            getDiscreet = "Discreet";
        }else{
            getDiscreet = "Normal";
        }

        if(session.getUPin().length() == 4 && !session.getUPin().isEmpty()){
            getPin = "****";
        }else{
            getPin = "not set";
        }

         itemabout = new String[] {
                session.getUName(),
                session.getUPhone(),
                getPin,
                getAutoupload,
                getDiscreet,
                "Learn about safetee",
                "Frequently asked questions",
                "Terms and conditions governing the use of safetee",
                "Everything regarding privacy",
                "log out of your account"
        };

        final Integer[] imgid = {
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.ic_action_edit_low,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1,
                R.drawable.chevron_right_1
        };

        adapter = new SettingsAdapter(this, itemname, itemabout, imgid);
        list = (ListView) findViewById(R.id.list);
        //
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String menu = itemname[+position];
                settingPosition = position;
                //
                switch (menu) {
                    case "Auto Upload":
                        autoUpload();
                        break;
                    case "Safetee Mode":
                        discreet();

                        break;
                    case "Full Name":
                        renameItem("name", "Save");
                        break;
                    case "Phone Number":
                        renameItem("phone", "Save");
                        break;
                    case "Pin Code":
                        renameItem("pin", "Continue");
                        break;
                    case "About":
                        startActivity(new Intent(SettingMain.this, AboutActivity.class));
                        break;
                    case "F.A.Q":
                        organizationView("faq");
                        break;
                    case "Privacy Policy":
                        organizationView("pp");
                        break;
                    case "Terms of Use":
                        organizationView("tou");
                        break;
                    case "Log Out":
                        logoutUser();
                        break;


                }

            }
        });
    }

    public void discreet() {
        lf = LayoutInflater.from(getApplicationContext());
        View discreetprompt = lf.inflate(R.layout.discreet_mode, null);
        discreetoption = (RadioGroup) discreetprompt.findViewById(R.id.discreetoption);
        discreet = (RadioButton) discreetprompt.findViewById(R.id.discreet);
        nodiscreet = (RadioButton) discreetprompt.findViewById(R.id.nodiscreet);

        // check if discreet is set already
        if(session.isDiscreet()){
            discreet.setChecked(true);
        } else {
            nodiscreet.setChecked(true);
        }
        // if discreet is clicked
        discreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                session.setMode(true);
                Toast.makeText(getApplicationContext(), "Safetee will go to discreet mode.", Toast.LENGTH_LONG).show();
                updateSetting(settingPosition, "Discreet");

            }
        });
        // if no discreet is clicked
        nodiscreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setMode(false);
                Toast.makeText(getApplicationContext(), "Safetee will go back to normal mode", Toast.LENGTH_LONG).show();
                updateSetting(settingPosition, "Normal");
            }
        });
        final AlertDialog.Builder discreetbuilder = new AlertDialog.Builder(this);
        discreetbuilder.setView(discreetprompt);
        discreetbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        });
        //
        AlertDialog discreetdialog = discreetbuilder.create();
        discreetdialog.show();
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
                updateSetting(settingPosition, "On");
            }
        });
        // if not auto upload is clicked
        noautoupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setAutoupload(false);
                Toast.makeText(getApplicationContext(), "Your recordings will not be uploaded to safetee immediately after recording stops.", Toast.LENGTH_LONG).show();
                updateSetting(settingPosition, "Off");
            }
        });
        final AlertDialog.Builder autouploadbuilder = new AlertDialog.Builder(this);
        autouploadbuilder.setView(autouploadprompt);
        autouploadbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        });
        //
        AlertDialog autouploaddialog = autouploadbuilder.create();
        autouploaddialog.show();
    }

    // reset fullname, phone number, pin
    public void renameItem(final String type, final String btn) {
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
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setMaxLines(11);
            heading.setText("PHONE NUMBER");
            input.setText(session.getUPhone());
        } else if(type.equals("pin")){
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            input.setMaxLines(4);
            heading.setText("ENTER PIN CODE");
        } else if(type.equals("rpin")){
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            input.setMaxLines(4);
            heading.setText("NEW PIN CODE");
        }
        renamebuilder.setPositiveButton(btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
                final String inputVal = input.getText().toString();
                if (inputVal.length() > 0) {
                    if (type.equals("name")) {
                        if (inputVal.contains(" ")) {
                            saveSetting(inputVal, "");
                            updateSetting(settingPosition, inputVal);
                        } else {
                            message.message("Error", "Please set a proper full name", "Dismiss");
                            //Toast.makeText(getApplicationContext(), "Please set a proper full name", Toast.LENGTH_LONG).show();
                        }
                    } else if (type.equals("phone")) {
                        if (inputVal.length() >= 11) {
                            saveSetting("", inputVal);
                            updateSetting(settingPosition, inputVal);
                        } else {
                            message.message("Error", "Phone number should not be less or more than 14 digits, eg +2348012345678", "Dismiss");
                            //Toast.makeText(getApplicationContext(), "Phone number should not be less or more than 14 digits, eg +2348012345678", Toast.LENGTH_LONG).show();
                        }
                    } else if (type.equals("pin")) {
                        if (inputVal.length() == 4) {
                            oldpin = inputVal;
                            renameItem("rpin", "Save");
                        } else {
                            message.message("Error", "Pin code should not be less or more than 4 digits", "Dismiss");
                            //Toast.makeText(getApplicationContext(), "Pin code should not be less or more than 4 digits", Toast.LENGTH_LONG).show();
                        }
                    } else if (type.equals("rpin")) {
                        if (inputVal.length() != 4) {
                            message.message("Error", "Pin code should not be less or more than 4 digits", "Dismiss");
                            //Toast.makeText(getApplicationContext(), "Pin code should not be less or more than 4 digits", Toast.LENGTH_LONG).show();
                        } else if (session.getUPin().equals(oldpin)) {
                            session.setUPin(inputVal);
                            Toast.makeText(getApplicationContext(), "Pin code successfully changed", Toast.LENGTH_LONG).show();
                        } else {
                            message.message("Error", "Incorrect pin entered", "Dismiss");
                        }

                    } else {
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
                    message.message("Error", "Network connection failed", "Dismiss");
                    //Toast.makeText(getApplicationContext(), "Network connection failed", Toast.LENGTH_LONG).show();
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

    private void organizationView(final String type){
        Intent i = new Intent(SettingMain.this, OrganizationView.class);
        i.putExtra("title", type);
        startActivity(i);
    }

    // update setting item

    public View updateSettingItem(int position,View view,ViewGroup parent) {
        LayoutInflater inflater= getLayoutInflater();
        View rowView=inflater.inflate(R.layout.settings_main_list, null,true);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        extratxt.setText(itemabout[position]);
        return rowView;
    }


    private void updateSetting(int index, String update){
        View v = list.getChildAt(index - list.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView body = (TextView) v.findViewById(R.id.textView1);
        body.setText(update);
    }

}


