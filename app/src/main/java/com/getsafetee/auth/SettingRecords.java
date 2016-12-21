package com.getsafetee.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.getsafetee.SessionManager;
import com.getsafetee.safetee.R;
import com.getsafetee.util.ShowMessage;

public class SettingRecords extends AppCompatActivity {

    SessionManager session;
    ShowMessage message;

    private RadioGroup uploadoption;
    private RadioButton noautoupload;
    private RadioButton autoupload;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_user_settings);
        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(getApplicationContext());

        uploadoption = (RadioGroup) findViewById(R.id.uploadoption);
        autoupload = (RadioButton) findViewById(R.id.autoupload);
        noautoupload = (RadioButton) findViewById(R.id.noautoupload);

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
                showMessage("Settings", "Your recordings will now be uploaded to safetee immediately after recording stops.", "Dismiss");
            }
        });
        // if not auto upload is clicked
        noautoupload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                session.setAutoupload(false);
                showMessage("Settings", "Your recordings will not be uploaded to safetee immediately after recording stops.", "Dismiss");
            }
        });


    }

    public void viewAutouploadWhy(View v){
        showMessage("Auto Upload", "Auto upload is a feature that uploads your recording to safetee immediately after recording completes for safe keeping of record in case your phone gets damaged or lost.","Dismiss");
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
