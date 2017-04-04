package com.safeteeapp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.sephiroth.android.library.tooltip.Tooltip;

import static com.safeteeapp.circleoffriends.FriendsList.NUMBER_OF_COMRADES;


public class MainActivity2 extends AppCompatActivity{

    SessionManager session;
    LocationManager mLocationManager;
    private ShowMessage messager;
    private String smsbody;
    private RecordingService mRecordingService;

    public static final String TAG = MainActivity2.class.getSimpleName();
    private int SAFETEE_VOICE_RECORDER_PERMISSION = 100;
    private String numbers[];
    private String[] numbersCof;
    LocationHelper mLocationManagerHelper;
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
    Geocoder geocoder;
    List<Address> address;
    private String mLocationText = "";
    private String mCurrentDateTime = "";
    private Handler pHandler = new Handler();

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


        mCurrentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        // session manager
        session = new SessionManager(getApplicationContext());
        mCofDatabase = new CofDatabase(getApplicationContext());
        // mLocationManager manager
        mLocationManager = new LocationManager(MainActivity2.this);
        geocoder = new Geocoder(this, Locale.getDefault());
        messager = new ShowMessage(this);
        updateLocation();

        //
        getLocation = String.valueOf(mLocationManager.getLong()) + "," + String.valueOf(mLocationManager.getLat());
        if (!session.isDiscreet()) {
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
        }else if(id == R.id.nav_discreet){
            session.setMode(true);
            startActivity(new Intent(MainActivity2.this, MainActivity2.class));
            finish();
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

        if(mLocationManager.getIfLocation()) {
            if (checkMobileNetworkAvailable(MainActivity2.this)) {

                getUserLocationAddress();
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
        }else{
            mLocationManager.showLocationSettingsAlert("You are required to turn on location to use the Get Help feature");
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
        String mLocationManagerUrl = Constants.LOCATION_URL.replace("LAT", String.valueOf(mLocationManager.getLat()))
                .replace("LON", String.valueOf(mLocationManager.getLong()));
        switch (optionSelected) {
            case Constants.SmsConstants.COME_GET_ME:
                String getMessage;
                if(session.getUHelpMessage().length() > 1 && !session.getUHelpMessage().isEmpty()){
                    getMessage = session.getUHelpMessage();
                }else{
                    getMessage = getString(R.string.come_get_me_message);
                }
                    smsbody = getMessage + "\n" + "My location ( #LOC_URL# ). " + getString(R.string.message_with_footer);
                    smsbody = smsbody.replace(Constants.TAG_LOCATION, mLocationManager.getLat() + "," + mLocationManager.getLong());
                    smsbody = smsbody.replace(Constants.TAG_LOCATION_URL, mLocationManagerUrl);
                break;
            case Constants.SmsConstants.CALL_NEED_INTERRUPTION:
                smsbody = getString(R.string.interruption_message);
                break;
            case Constants.SmsConstants.CHECK_IN:
                smsbody = getString(R.string.check_in_message);
                smsbody = smsbody.replace(Constants.TAG_LOCATION, mLocationManager.getLat() + "," + mLocationManager.getLong());
                smsbody = smsbody.replace(Constants.TAG_LOCATION_URL, mLocationManagerUrl);
                smsbody = smsbody.replace(Constants.TAG_LOCATION_TEXT, mLocationText);
                break;
        }
        //
        int counter = 0;

        // add timestamp
        smsbody += " on "+mCurrentDateTime;

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

    public void updateLocation() {
        pHandler.postDelayed(mUpdateLocationTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateLocationTask = new Runnable() {
        int runLocationAlert = 0;
        public void run() {
            mCurrentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            if(mLocationManager.getIfLocation()) {
                if (mLocationManager.getLat() == 0.0 && mLocationManager.getLong() == 0.0) {
                } else {
                    //getUserLocationAddress();
                }
            }else {
                if(runLocationAlert == 0) {
                    mLocationManager.showLocationSettingsAlert("Location is not enabled. Goto settings to enable it");
                }
                runLocationAlert += 1;
            }
            // Running this thread after 10000 milliseconds
            pHandler.postDelayed(this, 10000);
        }
    };

public void getUserLocationAddress() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                pHandler.postDelayed(mUserLocation, 100);
                return null;
            }
        }.execute();
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUserLocation = new Runnable() {
        public void run() {

            try {
                address = geocoder.getFromLocation(mLocationManager.getLat(), mLocationManager.getLong(), 1);
            }catch (IOException e){
            }
            //
            if(address.size() > 0) {
                mLocationText = address.get(0).getAddressLine(0) + ", " + address.get(0).getLocality();
            }
        }
    };
    public void launchrecord(View view){
        Intent openrecord = new Intent(this, VoiceRecorderMainActivity.class);
        startActivity(openrecord);
    }
}