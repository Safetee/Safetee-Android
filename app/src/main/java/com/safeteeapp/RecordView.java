package com.safeteeapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.safeteeapp.audiorecorder.adapters.RecordingsAdapter;
import com.safeteeapp.auth.SettingActivity;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.Constants;
import com.safeteeapp.util.LocationManager;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.logging.Handler;

import com.safeteeapp.util.ShowMessage;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class RecordView extends AppCompatActivity{

    private String title;
    private String rid;
    private String uniid;
    private String audio;
    private RadioGroup category;
    private Button needhelp;
    ImageView buttonPlay;
    ImageView buttonStop;
    private ProgressDialog pDialog;
    private String cat_f;
    private TextView recordtitle;
    private TextView shared;
    private TextView place;
    private String shared_f;
    private String checkprivacy;
    private String location;
    private Button validatepin;
    private EditText pin;
    private String getpin = "";
    private String lengthr;
    private String uploadId, resp;
    Intent getintent;

    static MediaPlayer mPlayer;
    SessionManager session;
    private ShowMessage message;

    LinearLayout enterpin;
    RelativeLayout player;
    RelativeLayout privacy;
    RelativeLayout locationlayout;
    LinearLayout support;

    Geocoder geocoder;
    List<Address> address;

    Handler handle;
    private LocationManager mlocation;

    private RecordingsAdapter mAdapter;
    Future<String> uploadrequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_view);

        session = new SessionManager(getApplicationContext());
        mAdapter = new RecordingsAdapter(RecordView.this);
        geocoder = new Geocoder(this, Locale.getDefault());
        mlocation = new LocationManager(this);
        message = new ShowMessage(this);
        // Enable global Ion logging
        Ion.getDefault(RecordView.this).configure().setLogging("ion-sample", Log.DEBUG);
        //
        getintent = getIntent();
        //
        title = getintent.getStringExtra("title");
        // privacy is first presented to user coz they will be required to enter pin
        setTitle("Privacy");
        //
        rid = getintent.getStringExtra("rid");
        uniid = getintent.getStringExtra("uniid");
        audio = getintent.getStringExtra("audio");
        checkprivacy = getintent.getStringExtra("privacy");
        location = getintent.getStringExtra("location");
        lengthr = getintent.getStringExtra("lengthr");
        //
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        //
        category = (RadioGroup) findViewById(R.id.category);
        needhelp = (Button) findViewById(R.id.needphelp);
        recordtitle = (TextView) findViewById(R.id.recordtitle);
        shared = (TextView) findViewById(R.id.shared);
        validatepin = (Button) findViewById(R.id.continuebtn);
        pin = (EditText) findViewById(R.id.pin);
        enterpin = (LinearLayout) findViewById(R.id.enterpin);
        player = (RelativeLayout) findViewById(R.id.player);
        locationlayout = (RelativeLayout) findViewById(R.id.location);
        privacy = (RelativeLayout) findViewById(R.id.privacy);
        support = (LinearLayout) findViewById(R.id.support);
        place = (TextView) findViewById(R.id.place);
        //
        recordtitle.setText(title);
        //
        if (checkprivacy.equals("false")) {
            shared_f = "not shared with third party";
        } else {
            shared_f = "shared with third party";
        }
        //
        shared.setText(shared_f);
        //
        needhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get selected category radio
                int selected_cat_id = category.getCheckedRadioButtonId();
                // find the category button by returned id
                RadioButton catButton = (RadioButton) findViewById(selected_cat_id);
                cat_f = catButton.getText().toString();
                // Check for empty data in the form
                if (cat_f.isEmpty()) {
                    // Prompt user to enter credentials
                    message.message("Error", "Please all fields are required", "Dismiss");
                    //Toast.makeText(RecordView.this, "Please all fields are required", Toast.LENGTH_LONG).show();
                } else {
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    // get help
                    Ion.with(RecordView.this)
                            .load(Constants.UPLOAD_SERVICE_URL)
                            .setMultipartFile("record", "audio/wav", new File(audio))
                            .setMultipartParameter("sender", session.getUName())
                            .setMultipartParameter("uid", session.getUid())
                            .setMultipartParameter("location", String.valueOf(mlocation.getLat()) + "," + String.valueOf(mlocation.getLong()))
                            .setMultipartParameter("category", cat_f)
                            .setMultipartParameter("uniqueid", uniid)
                            .setMultipartParameter("length", String.valueOf(lengthr))
                            .setMultipartParameter("recordname", title)
                            .setMultipartParameter("share", "true")
                            .asJsonObject()
                        ///*
                        .setCallback(new FutureCallback<JsonObject>() {

                            @Override
                            public void onCompleted(Exception err, JsonObject result) {
                                if (err != null) {
                                    pDialog.hide();
                                    message.message("Error", "An error occurred. " + err.getMessage().toString(), "Dismiss");
                                    //Toast.makeText(RecordView.this, "Support could not be requested, try again. " + err.getMessage().toString() + audio, Toast.LENGTH_LONG).show();
                                } else {
                                    //try {
                                    pDialog.hide();
                                    // set record as shared
                                    mAdapter.getDatabase().setItemShared(rid, "true");
                                    //reportsent();
                                    //Toast.makeText(RecordView.this, "Your request for support was successfully received, an agency will get in touch you shortly.", Toast.LENGTH_LONG).show();
                                    message.message("Success", getString(R.string.reportsent), "Dismiss");
                                    //} catch (JSONException exep) {

                                    //}

                                }

                            }

                        });
                        //*/
                    //
                }
            }
        });

        //
        buttonPlay = (ImageView) findViewById(R.id.play);
        buttonStop = (ImageView) findViewById(R.id.stop);
        //
        buttonPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                buttonPlay.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(audio);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), "Error fetching record", Toast.LENGTH_LONG).show();
                    showMessage("Safetee", "Error fetching record", "OK");
                } catch (SecurityException e) {
                    Toast.makeText(getApplicationContext(), "Error fetching record", Toast.LENGTH_LONG).show();
                    showMessage("Safetee", "Error fetching record", "OK");
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), "Error fetching record", Toast.LENGTH_LONG).show();
                    showMessage("Safetee", "Error fetching record", "OK");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), "Error fetching record", Toast.LENGTH_LONG).show();
                    showMessage("Safetee", "Error fetching record", "OK");
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error fetching record", Toast.LENGTH_LONG).show();
                    showMessage("Safetee", "Error fetching record", "OK");
                }
                mPlayer.start();

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // TODO Auto-generated method stub
                buttonPlay.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.INVISIBLE);
                if(mPlayer!=null && mPlayer.isPlaying()){
                    mPlayer.stop();
                }

            }
        });
        // decipher location here
        String decipherlocation[] = location.split(",");
        // get location where record was made
        getLocation(Double.parseDouble(decipherlocation[0]), Double.parseDouble(decipherlocation[1]));
    }



    public void showMessage(String title, String msg, final String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (btn.equals("Go to settings")) {
                            Intent i = new Intent(RecordView.this, SettingActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void validatepin(View view){
        getpin = pin.getText().toString();
        if(session.getUPin().equals("")){
            showMessage("Safetee", "Please set your pin", "Go to settings");
        }else {
            if (getpin.length() < 0) {
                showMessage("Safetee", "Pin field is required to continue", "Try Again");
            } else if (getpin.equals(session.getUPin())) {
                enterpin.setVisibility(View.GONE);
                setTitle(title);
                player.setVisibility(View.VISIBLE);
                privacy.setVisibility(View.VISIBLE);
                support.setVisibility(View.VISIBLE);
                locationlayout.setVisibility(View.VISIBLE);
            } else if (!getpin.equals(session.getUPin())) {
                showMessage("Safetee", "Entered pin is incorrect", "Try Again");
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void renameItem(View v) {
        LayoutInflater lf = LayoutInflater.from(getApplicationContext());
        View renameprompt = lf.inflate(R.layout.rename_record, null);
        final AlertDialog.Builder renamebuilder = new AlertDialog.Builder(this);
        renamebuilder.setView(renameprompt);
        final EditText input = (EditText) renameprompt.findViewById(R.id.input);
        renamebuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
                if (input.getText().length() > 0) {
                    mAdapter.getDatabase().renameItemByID(rid, input.getText().toString());
                    setTitle(input.getText().toString());
                    recordtitle.setText(input.getText().toString());
                    title = input.getText().toString();
                }
            }
        });
        //
        AlertDialog renamedialog = renamebuilder.create();
        renamedialog.show();
    }

    public  void getLocation(final double lat, final double lng) {
        Runnable runnable = new Runnable(){
            public void run() {
                RecordView.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        takeLocation(lat, lng);
                    }
                });
            }
        }; new Thread(runnable).start();
    }

    private void takeLocation(final double lat, final double lng){
        try {
            address = geocoder.getFromLocation(lat, lng, 1);
        }catch (IOException e){
            Toast.makeText(RecordView.this, "unable to fetch location", Toast.LENGTH_LONG).show();
        }
        //
        if(address != null && address.size() > 0) {
            place.setText(address.get(0).getAddressLine(0) + ", " + address.get(0).getLocality());
        }else{
            place.setText("unable to fetch location");
        }

    }

    public void reportsent() {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
    }





    public void sendHelpRequest(final String cat) {



    }


}

