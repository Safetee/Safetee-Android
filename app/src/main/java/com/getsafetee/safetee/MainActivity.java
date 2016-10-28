package com.getsafetee.safetee;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.getsafetee.safetee.activities.VoiceRecorderMainActivity;
import com.getsafetee.safetee.circle_of_friends.CustomAlertDialogFragment;
import com.getsafetee.safetee.circle_of_friends.FriendsList;
import com.getsafetee.safetee.circle_of_friends.LocationHelper;
import com.getsafetee.safetee.circle_of_friends.MessageDialogBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.getsafetee.safetee.circle_of_friends.FriendsList.NUMBER_OF_COMRADES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private String numbers[];
    LocationHelper locationHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String[] phoneNumbers;
    ArrayList<PendingIntent> sentIntents = new ArrayList<>();
    public final static String SENT = "300";
    private static int msgParts;
    private static boolean firstTime = false;
    TextView[] allTextViews;
    static Map allNames = new HashMap();
    private static final String NAME_KEY = "Friend's Name";

    private Vibrator vibrator;
    private static int REQUEST_CODE_TRUSTEES = 1001;
    private long VIBRATION_TIME = 300; // Length of vibration in milliseconds
    private long VIBRATION_PAUSE = 200;
    /**
     * TODO : Add info about vibration pattern in intro activity
     */
    private long[] patternSuccess = {0, // Start immediately
            VIBRATION_TIME
    };

    private long[] patternFailure = {0, // Start immediately
            VIBRATION_TIME, VIBRATION_PAUSE, VIBRATION_TIME, // Each element then alternates between vibrate, sleep, vibrate, sleep...
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CircleImageView donateButton = (CircleImageView) findViewById(R.id.image_donate);
        CircleImageView circleOfFriends = (CircleImageView) findViewById(R.id.image_cf);
        CircleImageView recordButton = (CircleImageView) findViewById(R.id.image_record);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Hello", Toast.LENGTH_SHORT).show();
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, VoiceRecorderMainActivity.class));
            }
        });
        circleOfFriends.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(checkMobileNetworkAvailable(MainActivity.this))
                {
/*
                    if (vibrator.hasVibrator()) {
                        // Only perform success pattern one time (-1 means "do not repeat")
                        vibrator.vibrate(patternSuccess, -1);
                    }
*/
                    MessageDialogBox messageDialogBox = MessageDialogBox.newInstance(MainActivity.this,MainActivity.this);
                    messageDialogBox.show(MainActivity.this.getSupportFragmentManager(),getString(R.string.message_options));
                }
                else
                {
                    if (vibrator.hasVibrator()) {
                        // Only perform failure pattern one time (-1 means "do not repeat")
                        vibrator.vibrate(patternFailure, -1);
                    }
                    Toast.makeText(MainActivity.this,R.string.network_unavailable,Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        circleOfFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }


    public void showToast(View view){
        Toast.makeText(this,"You Clicked me",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
//        locationHelper.startAcquiringLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
//        locationHelper.stopAcquiringLocation();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this, FriendsList.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static boolean checkMobileNetworkAvailable(Context appcontext) {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return (tel.getNetworkOperator() != null && tel.getNetworkOperator().equals("") ? false : true);
    }
    /**
     * Sends a message to the Friend's' phone numbers
     * @param optionSelected selected option
     */
    public void sendMessage(String optionSelected)
    {
        SmsManager sms = SmsManager.getDefault();
        String message = "";
        switch(optionSelected)
        {
            case Constants.SmsConstants.COME_GET_ME:
//                Location location = locationHelper.retrieveLocation(false);
                Location location = null;
                if(location == null) {
                    message = getString(R.string.come_get_me_message);
                }else{
                    message = getString(R.string.come_get_me_message_with_location);
                    message = message.replace(Constants.TAG_LOCATION,location.getLatitude() +"," + location.getLongitude());
                    String locationUrl = Constants.LOCATION_URL.replace("LAT" , String.valueOf(location.getLatitude()))
                            .replace("LON" , String.valueOf(location.getLongitude()));
                    message = message.replace(Constants.TAG_LOCATION_URL,locationUrl);
                }
                break;
            case Constants.SmsConstants.CALL_NEED_INTERRUPTION:
                message = getString(R.string.interruption_message);
                break;
            case Constants.SmsConstants.NEED_TO_TALK:
                message = getString(R.string.need_to_talk_message);
                break;
        }

        sharedPreferences = this.getSharedPreferences(FriendsList.MY_PREFERENCES, Context.MODE_PRIVATE);

        if(phoneNumbers == null)
        {
            loadPhoneNumbers();
        }
        // The numbers variable holds the Comrades numbers
        numbers = phoneNumbers;

        int counter=0;

        //Fix sending messages if the length is more than single sms limit
        ArrayList<String> parts = sms.divideMessage(message);
        int numParts = parts.size();
        for (int i = 0; i < numParts; i++) {
            sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent(
                    SENT), 0));
        }
        int numRegisteredComrades = 0;
        for(String number : numbers) {
            if (!number.isEmpty()) {
                numRegisteredComrades++;
            }
        }
        msgParts = numParts * numRegisteredComrades;
        firstTime = true;
        for(String number : numbers) {
            if (!number.isEmpty()) {
                try{
                    sms.sendMultipartTextMessage(number, null, parts, sentIntents, null);
                }
                catch(Exception e){
                    Toast.makeText(this, R.string.message_failed + (counter+1), Toast.LENGTH_LONG).show();
                }
                counter++;
            }
        }
        if(counter!=0)
        {
            String contentToPost;

            //For 1 comrade
            if(counter == 1)
                contentToPost = getString(R.string.confirmation_message1)+ " " + counter + " "+ getString(R.string.confirmation_message3) +" " + getString(R.string.receive_log);
            else
                contentToPost = getString(R.string.confirmation_message1)+ " " + counter + " "+ getString(R.string.confirmation_message2)+ " " + getString(R.string.receive_log);
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.msg_sent),contentToPost);
            customAlertDialogFragment.show(this.getSupportFragmentManager(),getString(R.string.dialog_tag));
        }
        else
        {
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.no_comrade_title),getString(R.string.no_comrade_msg));
            customAlertDialogFragment.show(this.getSupportFragmentManager(),getString(R.string.dialog_tag));
        }
    }

    private boolean loadPhoneNumbers() {
        sharedPreferences = this.getSharedPreferences(FriendsList.MY_PREFERENCES, Context.MODE_PRIVATE);
        try {

            phoneNumbers = new String[NUMBER_OF_COMRADES];
            for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
                phoneNumbers[i] = sharedPreferences.getString( FriendsList.COMRADE_KEY.get( i ), "" );
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Unable to load comrades numbers from shared preferences", e);
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TRUSTEES) {
            refreshPhotos();
            Iterator it = allNames.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                allTextViews[(Integer)pair.getKey() - 1].setText(pair.getValue().toString());
                editor.putString(NAME_KEY + ((Integer)pair.getKey()-1),pair.getValue().toString());
            }

            for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
                if(!allNames.containsKey(i+1) && !(phoneNumbers[i].isEmpty())){
                    allTextViews[i].setText(phoneNumbers[i]);
                    editor.putString(NAME_KEY + i,phoneNumbers[i]);
                }
                if(phoneNumbers[i].isEmpty()) {
                    allTextViews[i].setText(getString(R.string.unregistered));
                    editor.putString(NAME_KEY + i,getString(R.string.unregistered));
                }
            }
            editor.commit();
        }
    }

    private void refreshPhotos() {
        phoneNumbers = null;
     //   loadContactPhotos();
    }
}
