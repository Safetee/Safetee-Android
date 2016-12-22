package com.getsafetee;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getsafetee.audiorecorder.activities.VoiceRecorderMainActivity;
import com.getsafetee.auth.LoginActivity;
import com.getsafetee.auth.SettingActivity;
import com.getsafetee.auth.SettingMain;
import com.getsafetee.circleoffriends.FriendsList;
import com.getsafetee.circleoffriends.MessageDialogBox;
import com.getsafetee.circleoffriends.fragments.CustomAlertDialogFragment;
import com.getsafetee.circleoffriends.helpers.LocationHelper;
import com.getsafetee.incidencereport.ReportActivity;
import com.getsafetee.safetee.R;
import com.getsafetee.safetytips.SafetyTips;
import com.getsafetee.safetytips.TipsLocal;
import com.getsafetee.safetytips.TipsUpdate;
import com.getsafetee.safetytips.db.DBAdapter;
import com.getsafetee.util.Constants;
import com.getsafetee.util.LocationManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.getsafetee.circleoffriends.FriendsList.NUMBER_OF_COMRADES;


public class MainActivity2 extends AppCompatActivity{

    SessionManager session;
    LocationManager location;

    public static final String TAG = MainActivity2.class.getSimpleName();
    private int SAFETEE_VOICE_RECORDER_PERMISSION = 100;
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
    Activity activity = this;
    Context context = this;
    DBAdapter db;
    private Vibrator vibrator;
    private static int REQUEST_CODE_TRUSTEES = 1001;
    private long VIBRATION_TIME = 300; // Length of vibration in milliseconds
    private long VIBRATION_PAUSE = 200;

    ListView list;
    String[] itemname ={
            "Add Contacts",
            "Get Help",
            "Record",
            "Report",
            "Tips",
            "Donate"
    };

    String[] itemabout = {
            "add your circle of friends",
            "get help from your circle of friends",
            "record scene in real time",
            "report a case with evidence",
            "get safety tips to keep you safe",
            "donate to support ngos around"
    };

    Integer[] imgid={
            R.drawable.add_contact,
            R.drawable.cf,
            R.drawable.mic,
            R.drawable.circle_of_friends,
            R.drawable.tips,
            R.drawable.donate
    };

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
        setContentView(R.layout.activity_main_2);


        // session manager
        session = new SessionManager(getApplicationContext());
        // location manager
        location = new LocationManager(this);

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {

        }
        // tips update service
        Intent tipsUpdateService = new Intent(Intent.ACTION_SYNC, null, this, TipsUpdate.class);
        //startService(tipsUpdateService);
        //
        // check if pin code and full name is set before allowing access to menu
        if (session.isLoggedIn()){
            if (session.getUName().isEmpty()) {
                gotoSettings();
            }
    }
        if (session.isLoggedIn()){
            if (session.getUPin().length() != 4) {
                gotoSettings();
            }
        }

        MainActivityAdapter adapter=new MainActivityAdapter(this, itemname, itemabout, imgid);
        list=(ListView)findViewById(R.id.list);
        //
        list.setAdapter(adapter);


        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String menu = itemname[+position].toString();
                //
                switch (menu){
                    case "Get Help":
                        initializehelp();
                        break;
                    case "Record":
                        startActivity(new Intent(MainActivity2.this, VoiceRecorderMainActivity.class));
                        break;
                    case "Report":
                        startActivity(new Intent(MainActivity2.this, ReportActivity.class));
                        break;
                    case "Tips":
                        startActivity(new Intent(MainActivity2.this, TipsLocal.class));
                        break;
                    case "Donate":
                        startActivity(new Intent(MainActivity2.this, DonateToNGO.class));
                        break;
                    case "Add Contacts":
                        startActivity(new Intent(MainActivity2.this, FriendsList.class));
                        break;


                }

            }
        });
        //
        String destDir;
        destDir = "/data/data/" + getPackageName() + "/databases/";
        String destPath = destDir + "safety_tips";
        File f = new File(destPath);
        if (!f.exists()) {
            //---make sure directory exists---
            File directory = new File(destDir);
            directory.mkdirs();
            //---copy the db from the assets folder into
            // the databases folder---
            try {
                CopyDB(getBaseContext().getAssets().open("safety_tips"),
                        new FileOutputStream(destPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db = new DBAdapter(this);
        requestWriteExternalStoragePermission();
    }

    private void requestWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAFETEE_VOICE_RECORDER_PERMISSION);
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
        if (id == R.id.nav_circle_friends) {
            startActivity(new Intent(MainActivity2.this, FriendsList.class));
        } else if(id == R.id.nav_settings){
            startActivity(new Intent(MainActivity2.this, SettingMain.class));
        } else if(id == R.id.nav_about){
            startActivity(new Intent(MainActivity2.this, AboutActivity.class));
        } else if(id == R.id.nav_records){
            startActivity(new Intent(MainActivity2.this, RecordsLocal.class));
        } else if (id == R.id.nav_circle) {
            startActivity(new Intent(MainActivity2.this, FriendsList.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void gotoSettings() {
        // Launching the setting activity
        Intent intent = new Intent(MainActivity2.this, SettingMain.class);
        startActivity(intent);
    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
    //
    public void initializehelp(){
        if (checkMobileNetworkAvailable(MainActivity2.this)) {


/*
                    if (vibrator.hasVibrator()) {
                        // Only perform success pattern one time (-1 means "do not repeat")
                        vibrator.vibrate(patternSuccess, -1);
                    }
*/
            MessageDialogBox messageDialogBox = MessageDialogBox.newInstance(MainActivity2.this, MainActivity2.this);
            messageDialogBox.show(MainActivity2.this.getSupportFragmentManager(), getString(R.string.message_options));
        } else {
            if (vibrator.hasVibrator()) {
                // Only perform failure pattern one time (-1 means "do not repeat")
                vibrator.vibrate(patternFailure, -1);
            }
            Toast.makeText(MainActivity2.this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }
    public static boolean checkMobileNetworkAvailable(Context appcontext) {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return (tel.getNetworkOperator() != null && tel.getNetworkOperator().equals("") ? false : true);
    }

    /**
     * Sends a message to the Friend's' phone numbers
     *
     * @param optionSelected selected option
     */
    public void sendMessage(String optionSelected) {
        SmsManager sms = SmsManager.getDefault();
        String message = "";
        switch (optionSelected) {
            case Constants.SmsConstants.COME_GET_ME:
               //Location location = locationHelper.retrieveLocation(false);
                //Location location = null;
                if (location == null) {
                    message = getString(R.string.come_get_me_message);
                } else {
                    message = getString(R.string.come_get_me_message_with_location);
                    message = message.replace(Constants.TAG_LOCATION, location.getLat() + "," + location.getLong());
                    String locationUrl = Constants.LOCATION_URL.replace("LAT", String.valueOf(location.getLat()))
                            .replace("LON", String.valueOf(location.getLong()));
                    message = message.replace(Constants.TAG_LOCATION_URL, locationUrl);
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

        if (phoneNumbers == null) {
            loadPhoneNumbers();
        }
        // The numbers variable holds the Comrades numbers
        numbers = phoneNumbers;

        int counter = 0;

        //Fix sending messages if the length is more than single sms limit
        ArrayList<String> parts = sms.divideMessage(message);
        int numParts = parts.size();
        for (int i = 0; i < numParts; i++) {
            sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent(
                    SENT), 0));
        }
        int numRegisteredComrades = 0;
        for (String number : numbers) {
            if (!number.isEmpty()) {
                numRegisteredComrades++;
            }
        }
        msgParts = numParts * numRegisteredComrades;
        firstTime = true;
        for (String number : numbers) {
            if (!number.isEmpty()) {
                try {
                    sms.sendMultipartTextMessage(number, null, parts, sentIntents, null);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.message_failed + (counter + 1), Toast.LENGTH_LONG).show();
                }
                counter++;
            }
        }
        if (counter != 0) {
            String contentToPost;

            //For 1 comrade
            if (counter == 1)
                contentToPost = getString(R.string.confirmation_message1) + " " + counter + " " + getString(R.string.confirmation_message3) + " " + getString(R.string.receive_log);
            else
                contentToPost = getString(R.string.confirmation_message1) + " " + counter + " " + getString(R.string.confirmation_message2) + " " + getString(R.string.receive_log);
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.msg_sent), contentToPost);
            customAlertDialogFragment.show(this.getSupportFragmentManager(), getString(R.string.dialog_tag));
        } else {
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.no_comrade_title), getString(R.string.no_comrade_msg));
            customAlertDialogFragment.show(this.getSupportFragmentManager(), getString(R.string.dialog_tag));
        }
    }

    private boolean loadPhoneNumbers() {
        sharedPreferences = this.getSharedPreferences(FriendsList.MY_PREFERENCES, Context.MODE_PRIVATE);
        try {

            phoneNumbers = new String[NUMBER_OF_COMRADES];
            for (int i = 0; i < NUMBER_OF_COMRADES; i++) {
                phoneNumbers[i] = sharedPreferences.getString(FriendsList.COMRADE_KEY.get(i), "");
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
        if (requestCode == REQUEST_CODE_TRUSTEES) {
            refreshPhotos();
            Iterator it = allNames.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                allTextViews[(Integer) pair.getKey() - 1].setText(pair.getValue().toString());
                editor.putString(NAME_KEY + ((Integer) pair.getKey() - 1), pair.getValue().toString());
            }

            for (int i = 0; i < NUMBER_OF_COMRADES; i++) {
                if (!allNames.containsKey(i + 1) && !(phoneNumbers[i].isEmpty())) {
                    allTextViews[i].setText(phoneNumbers[i]);
                    editor.putString(NAME_KEY + i, phoneNumbers[i]);
                }
                if (phoneNumbers[i].isEmpty()) {
                    allTextViews[i].setText(getString(R.string.unregistered));
                    editor.putString(NAME_KEY + i, getString(R.string.unregistered));
                }
            }
            editor.commit();
        }
    }

    private void refreshPhotos() {
        phoneNumbers = null;
        //   loadContactPhotos();
    }

    public void launchrecord(View view){
        Intent openrecord = new Intent(this, VoiceRecorderMainActivity.class);
        startActivity(openrecord);
    }
}