package com.safeteeapp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.safeteeapp.audiorecorder.activities.VoiceRecorderMainActivity;
import com.safeteeapp.audiorecorder.services.RecordingService;
import com.safeteeapp.auth.SettingActivity;
import com.safeteeapp.auth.SettingMain;
import com.safeteeapp.circleoffriends.FriendsList;
import com.safeteeapp.circleoffriends.MessageDialogBox;
import com.safeteeapp.circleoffriends.fragments.CustomAlertDialogFragment;
import com.safeteeapp.circleoffriends.helpers.LocationHelper;
import com.safeteeapp.cof2.CofDatabase;
import com.safeteeapp.cof2.CofLocal;
import com.safeteeapp.incidencereport.ReportActivity;
import com.safeteeapp.safetee.R;
import com.safeteeapp.safetytips.TipsLocal;
import com.safeteeapp.safetytips.TipsUpdate;
import com.safeteeapp.safetytips.db.DBAdapter;
import com.safeteeapp.util.Constants;
import com.safeteeapp.util.LocationManager;
import com.safeteeapp.util.ShowMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.sephiroth.android.library.tooltip.Tooltip;

import static com.safeteeapp.circleoffriends.FriendsList.NUMBER_OF_COMRADES;


public class MainActivity2 extends AppCompatActivity{

    SessionManager session;
    LocationManager location;
    private ShowMessage messager;
    private String smsbody;
    private RecordingService mRecordingService;

    public static final String TAG = MainActivity2.class.getSimpleName();
    private int SAFETEE_VOICE_RECORDER_PERMISSION = 100;
    private String numbers[];
    private String[] numbersCof;
    LocationHelper locationHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private CofDatabase mCofDatabase;
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
    private String getLocation;

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
            "Add Your Circle Of Friends",
            "Get Help From Your Circle Of Friends",
            "Record Scene In Real Time",
            "Report A Case With Visual Evidence",
            "Get Safety Tips To Keep You Safe",
            "Donate To Support NGOs Around"
    };

    Integer[] imgid={
            R.drawable.gethelp4,
            R.drawable.gethelp2,
            R.drawable.record1,
            R.drawable.report2,
            R.drawable.tips2,
            R.drawable.donate1
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
        mCofDatabase = new CofDatabase(getApplicationContext());
        // location manager
        location = new LocationManager(this);
        messager = new ShowMessage(this);

        //
        getLocation = String.valueOf(location.getLong()) + "," + String.valueOf(location.getLat());
        if (!session.isDiscreet()) {
            if (location == null || getLocation.equals("0.0,0.0")) {
                Toast.makeText(MainActivity2.this, "You are required to turn on location, so friends and family can find you in case of emergency", Toast.LENGTH_LONG).show();
            }
        }

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {

        }
        // tips update service
        Intent tipsUpdateService = new Intent(Intent.ACTION_SYNC, null, this, TipsUpdate.class);
        startService(tipsUpdateService);
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
        // check safetee mode
        if (session.isDiscreet()){
            list.setVisibility(View.GONE);
            getSupportActionBar().hide();
            FloatingActionButton launchrec = (FloatingActionButton) findViewById(R.id.launchrecord);
            FloatingActionButton exitdiscreet = (FloatingActionButton) findViewById(R.id.exitdiscreet);
            launchrec.setVisibility(View.GONE);
            exitdiscreet.setVisibility(View.VISIBLE);
            LinearLayout discreetmenu = (LinearLayout) findViewById(R.id.discreet);
            discreetmenu.setVisibility(View.VISIBLE);
            LinearLayout gethelp = (LinearLayout) findViewById(R.id.gethelp);
            LinearLayout startrecord = (LinearLayout) findViewById(R.id.startrecord);
            TextView ghelp = (TextView) findViewById(R.id.ghelp);
            TextView srecord = (TextView) findViewById(R.id.srecord);
            //
            if (!session.isDiscreetTutorial()) {
                Tooltip.make(this, new Tooltip.Builder(101)
                                .anchor(ghelp, Tooltip.Gravity.TOP)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 20000)
                                .activateDelay(930000)
                                .showDelay(100)
                                .text("Touch anywhere on the upper half area of screen to send help message to your circle of friends.")
                                .maxWidth(650)
                                .withArrow(true)
                                .withStyleId(R.style.ToolTipLayout)
                                .withOverlay(true).build()
                ).show();
                //
                Tooltip.make(this, new Tooltip.Builder(101)
                                .anchor(srecord, Tooltip.Gravity.TOP)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 20000)
                                .activateDelay(930000)
                                .showDelay(100)
                                .text("Touch anywhere on the lower half area of screen to start recording.")
                                .maxWidth(650)
                                .withArrow(true)
                                .withStyleId(R.style.ToolTipLayout)
                                .withOverlay(true).build()
                ).show();
                //
                session.setDiscreetTutorial(true);
            }
            //
            // listen for intents from voicemainrecorder if sent back
            if (getIntent().hasExtra("discreetRecord")){
                Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
            }
            // listen for touch for help
            gethelp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    sendMessage(Constants.SmsConstants.COME_GET_ME);
                    return false;
                }
            });
            // listen for touch for record
            startrecord.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Intent iM = new Intent(MainActivity2.this, VoiceRecorderMainActivity.class);
                    iM.putExtra("discreet", "ok");
                    startActivity(iM);
                    return false;
                }
            });
            // listen to discreet menu screen for long press so as to return to normal mode
            exitdiscreet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    session.setMode(false);
                    startActivity(new Intent(MainActivity2.this, MainActivity2.class));
                    finish();
                }
            });
        }
        //
        list.setAdapter(adapter);


        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String menu = itemname[+position];
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
                        startActivity(new Intent(MainActivity2.this, CofLocal.class));
                        break;


                }

            }
        });
        //
        /*
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
        */
        requestWriteExternalStoragePermission();
    }

    private void requestWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, SAFETEE_VOICE_RECORDER_PERMISSION);
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
            startActivity(new Intent(MainActivity2.this, CofLocal.class));
        } else if(id == R.id.nav_settings){
            startActivity(new Intent(MainActivity2.this, SettingMain.class));
        } else if(id == R.id.nav_about){
            startActivity(new Intent(MainActivity2.this, AboutActivity.class));
        } else if(id == R.id.nav_records){
            startActivity(new Intent(MainActivity2.this, RecordsLocal.class));
        } else if (id == R.id.nav_circle) {
            startActivity(new Intent(MainActivity2.this, CofLocal.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void gotoSettings() {
        // Launching the setting activity
        Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
        startActivity(intent);
        finish();
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

        if (location == null || getLocation.equals("0.0,0.0")) {
           Toast.makeText(MainActivity2.this, "Your location is turned off, you are optionally required to turn on location, so friends and family can get your exact location when you reach out to them for help.", Toast.LENGTH_LONG).show();
        }
        if (checkMobileNetworkAvailable(MainActivity2.this)) {


/*
                    if (vibrator.hasVibrator()) {
                        // Only perform success pattern one time (-1 means "do not repeat")
                        vibrator.vibrate(patternSuccess, -1);
                    }
*/
            MessageDialogBox messageDialogBox = MessageDialogBox.newInstance(MainActivity2.this, MainActivity2.this);
            messageDialogBox.show(MainActivity2.this.getSupportFragmentManager(), "");
        } else {
            if (vibrator.hasVibrator()) {
                // Only perform failure pattern one time (-1 means "do not repeat")
                vibrator.vibrate(patternFailure, -1);
            }
            Toast.makeText(MainActivity2.this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
            messager.message("Error", getString(R.string.network_unavailable), "Dismiss");
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
        switch (optionSelected) {
            case Constants.SmsConstants.COME_GET_ME:
               //Location location = locationHelper.retrieveLocation(false);
                //Location location = null;
                String getMessage;
                if(session.getUHelpMessage().length() > 1 && !session.getUHelpMessage().isEmpty()){
                    getMessage = session.getUHelpMessage();
                }else{
                    getMessage = getString(R.string.come_get_me_message);
                }

                if (location == null || getLocation.equals("0.0,0.0")) {
                    smsbody = getMessage + "\n" + getString(R.string.message_with_footer);
                } else {
                    smsbody = getMessage + "\n" + "My location ( #LOC_URL# ), " + getString(R.string.message_with_footer);
                    smsbody = smsbody.replace(Constants.TAG_LOCATION, location.getLat() + "," + location.getLong());
                    String locationUrl = Constants.LOCATION_URL.replace("LAT", String.valueOf(location.getLat()))
                            .replace("LON", String.valueOf(location.getLong()));
                    smsbody = smsbody.replace(Constants.TAG_LOCATION_URL, locationUrl);
                }
                break;
            case Constants.SmsConstants.CALL_NEED_INTERRUPTION:
                smsbody = getString(R.string.interruption_message);
                break;
            case Constants.SmsConstants.NEED_TO_TALK:
                smsbody = getString(R.string.need_to_talk_message);
                break;
        }
        //
        int counter = 0;

        //Fix sending messages if the length is more than single sms limit
        ArrayList<String> parts = sms.divideMessage(smsbody);
        int numParts = parts.size();
        for (int i = 0; i < numParts; i++) {
            sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
        }
        int countCofs = mCofDatabase.getCount();
        for(int i = 0; i < countCofs; i++){
            String number = mCofDatabase.getCofs(i);
            sms.sendMultipartTextMessage(number, null, parts, sentIntents, null);
            //sms.sendTextMessage(number, null, smsbody, null, null); // assume we keep msg to standard characters
            //Toast.makeText(getApplicationContext(), mCofDatabase.getCofs(i), Toast.LENGTH_SHORT).show();
            counter ++;
        }
        /*

        sharedPreferences = this.getSharedPreferences(FriendsList.MY_PREFERENCES, Context.MODE_PRIVATE);

        if (phoneNumbers == null) {
            loadPhoneNumbers();
        }
        // The numbers variable holds the Comrades numbers
        numbers = phoneNumbers;

        int counter = 0;

        //Fix sending messages if the length is more than single sms limit
        ArrayList<String> parts = sms.divideMessage(smsbody);
        int numParts = parts.size();
        for (int i = 0; i < numParts; i++) {
            sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
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
                    //sms.sendTextMessage(number, null, smsbody, null, null); // assume we keep msg to standard characters
                } catch (Exception e) {
                    messager.message("Error",String.valueOf(getString(R.string.message_failed) + (counter + 1) + ". " + e.getMessage()), "Dismiss");
                    Toast.makeText(this, R.string.message_failed + (counter + 1), Toast.LENGTH_LONG).show();

                }
                counter++;
            }
        }
        */
        if (counter != 0) {
            String contentToPost;

            //For 1 comrade
            if (counter == 1)
                contentToPost = getString(R.string.confirmation_message1) + " " + counter + " " + getString(R.string.confirmation_message3);
            else
                contentToPost = getString(R.string.confirmation_message1) + " " + counter + " " + getString(R.string.confirmation_message2);
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.msg_sent), contentToPost);
            //customAlertDialogFragment.show(this.getSupportFragmentManager(), getString(R.string.dialog_tag));
            if (session.isDiscreet()){
                Toast.makeText(getApplicationContext(), contentToPost, Toast.LENGTH_SHORT).show();
            }else {
                messager.message("Success", contentToPost, "Dismiss");
            }
        } else {
            CustomAlertDialogFragment customAlertDialogFragment = CustomAlertDialogFragment.newInstance(getString(R.string.no_comrade_title), getString(R.string.no_comrade_msg));
            //customAlertDialogFragment.show(this.getSupportFragmentManager(), getString(R.string.dialog_tag));
            messager.message("Error", getString(R.string.no_comrade_msg),"Click Here to Add Friends");
        }
    }
    /*

    private boolean loadPhoneNumbers() {
        sharedPreferences = this.getSharedPreferences(FriendsList.MY_PREFERENCES, Context.MODE_PRIVATE);
        try {

            phoneNumbers = new String[NUMBER_OF_COMRADES];
            for (int i = 0; i < NUMBER_OF_COMRADES; i++) {
                phoneNumbers[i] = sharedPreferences.getString(FriendsList.COMRADE_KEY.get(i), "");
                //Toast.makeText(MainActivity2.this, phoneNumbers[i], Toast.LENGTH_SHORT).show();
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
        //loadContactPhotos();
    }
*/
    public void launchrecord(View view){
        Intent openrecord = new Intent(this, VoiceRecorderMainActivity.class);
        startActivity(openrecord);
    }
}