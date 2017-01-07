package com.getsafeteeapp.audiorecorder.activities;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getsafeteeapp.FragmentHolderActivity;
import com.getsafeteeapp.MainActivity2;
import com.getsafeteeapp.RecordsLocal;
import com.getsafeteeapp.audiorecorder.fragments.AboutFragment;
import com.getsafeteeapp.audiorecorder.fragments.RecordingControlsFragment;
import com.getsafeteeapp.audiorecorder.fragments.RecordingStatusFragment;
import com.getsafeteeapp.audiorecorder.fragments.RecordingsListFragment;
import com.getsafeteeapp.audiorecorder.fragments.SettingsFragment;
import com.getsafeteeapp.audiorecorder.models.RecordingMode;
import com.getsafeteeapp.audiorecorder.services.RecordingService;
import com.getsafeteeapp.safetee.R;


public class VoiceRecorderMainActivity extends AppCompatActivity implements RecordingService.OnAudioLevelChangedListener {
    @SuppressWarnings("unused")
    private static final String TAG = VoiceRecorderMainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SETTINGS = 0;

    private RecordingService mRecordingService;

    private RecordingStatusFragment mRecordingStatusFragment;
    private RecordingControlsFragment mRecordingControlsFragment;
    private RecordingsListFragment mRecordingsListFragment;
    private AboutFragment mAboutFragment;
    private SettingsFragment mSettingsFragment;

    private Intent getIntent;

    //start recording once button clicked
    private boolean mRecordingQueued = true;
    private boolean mIsKitKatTranslucencyEnabled = false;
    private boolean mBackButtonAlwaysQuits = false;
    private boolean mIsBound = false;

    private BroadcastReceiver mStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RecordingService.INTENT_RECORDING_STARTED.equals(intent.getAction())) {
                String filename = intent.getStringExtra("filename");
                mRecordingStatusFragment.setRecordingMode(RecordingMode.RECORDING);
                mRecordingStatusFragment.setFileName(filename.replace(".pcm", ""));
                mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.RECORDING);
                getSupportActionBar().setTitle(R.string.state_recording);
            } else if (RecordingService.INTENT_RECORDING_STOPPED.equals(intent.getAction())) {
                mRecordingStatusFragment.setRecordingMode(RecordingMode.IDLE);
                mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.IDLE);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mRecordingService = ((RecordingService.ServiceBinder) service).getService();
            mRecordingService.setOnTimerChangedListener(mRecordingStatusFragment);
            mRecordingService.setOnAudioLevelChanged(VoiceRecorderMainActivity.this);
            if (mRecordingQueued) {
                mRecordingService.startRecording();
                mRecordingQueued = false;
                ifDescreet();
            }

            if (mRecordingService.isRecording()) {
                mRecordingStatusFragment.setRecordingMode(RecordingMode.RECORDING);
                mRecordingStatusFragment.setFileName(mRecordingService.getFilename().replace(".pcm", ""));
                mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.RECORDING);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mRecordingService = null;
        }
    };
    private Toolbar toolbar;

    private void doBindService() {
        bindService(new Intent(VoiceRecorderMainActivity.this,
                RecordingService.class), mConnection, Context.BIND_AUTO_CREATE);

        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder_main);
        getIntent = getIntent();

        findViewById(R.id.launchrecord).setVisibility(View.GONE);

        if (getIntent.hasExtra("discreet")){
            findViewById(R.id.main).setVisibility(View.INVISIBLE);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mBackButtonAlwaysQuits = prefs.getBoolean(SettingsFragment.BACK_BUTTON_ALWAYS_QUITS, false);
        mIsKitKatTranslucencyEnabled = prefs.getBoolean(SettingsFragment.KITKAT_TRANSLUCENCY_KEY, false);
        prefs = null;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startService(new Intent(this, RecordingService.class));
        doBindService();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            mRecordingStatusFragment = new RecordingStatusFragment();
            mRecordingStatusFragment.setRetainInstance(true);

            mRecordingControlsFragment = new RecordingControlsFragment();
            mRecordingControlsFragment.setRetainInstance(true);

            mRecordingControlsFragment.setRecordButtonCallback(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordingMode mode;
                    if (mRecordingService == null)
                        mode = mRecordingStatusFragment.getRecordingMode();
                    else
                        mode = mRecordingService.getRecordingMode();
                    switch (mode) {
                        case IDLE:
                            if (mRecordingStatusFragment != null) {
                                mRecordingStatusFragment.setTimeFromSeconds(0);
                                mRecordingStatusFragment.clearAudioBars();
                            }
                            startRecording();
                            break;
                        case RECORDING:
                        default:
                            mRecordingService.stopRecording();
                            //switchToRecordings();
                            // goto RecordsLocal, the activity is more arranged but stil uses the same recordlistfragment
                            recordsLocal();
                            break;
                    }
                }
            });
            transaction.add(R.id.container, mRecordingControlsFragment);
            transaction.add(R.id.status_container, mRecordingStatusFragment);
            transaction.commit();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void switchToNewRecording() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        removeAllFragments(transaction);
        transaction.add(R.id.container, mRecordingControlsFragment);
        transaction.add(R.id.status_container, mRecordingStatusFragment);
        transaction.commit();
    }

    private void switchToRecordings() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        removeAllFragments(transaction);
        transaction.add(R.id.container, getRecordingsListFragment());
        transaction.commit();
    }

    private void removeAllFragments(FragmentTransaction transaction) {
        transaction.remove(mRecordingStatusFragment);
        transaction.remove(mRecordingControlsFragment);
        transaction.remove(getRecordingsListFragment());

        if (mAboutFragment != null) {
            transaction.remove(mAboutFragment);
            mAboutFragment = null;
        }

        if (mSettingsFragment != null) {
            transaction.remove(mSettingsFragment);
        }
    }

    private RecordingsListFragment getRecordingsListFragment() {
        if (mRecordingsListFragment == null)
            mRecordingsListFragment = new RecordingsListFragment();

        return mRecordingsListFragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    protected void startRecording() {
        if (mRecordingService != null) {
            mRecordingService.startRecording();
        }else {
            mRecordingQueued = true;
        }
    }

    public void ifDescreet(){
        //Toast.makeText(getApplicationContext(), ".......", Toast.LENGTH_SHORT).show();
        // if coming from discreet mode
        if (getIntent.hasExtra("discreet")){
            // then return back to main activity
            Intent iM = new Intent(VoiceRecorderMainActivity.this, MainActivity2.class);
            iM.putExtra("discreetRecord", "success");
            startActivity(iM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SETTINGS) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mBackButtonAlwaysQuits = prefs.getBoolean(SettingsFragment.BACK_BUTTON_ALWAYS_QUITS, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mStateChangedReceiver);
        doUnbindService();
        if (mRecordingService.getRecordingMode() == RecordingMode.IDLE) {
            mRecordingService.stopSelf();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iF = new IntentFilter();
        iF.addAction(RecordingService.INTENT_RECORDING_STARTED);
        iF.addAction(RecordingService.INTENT_RECORDING_STOPPED);
        registerReceiver(mStateChangedReceiver, iF);

        if (mRecordingService == null)
            return;

        if (mRecordingStatusFragment != null)
            mRecordingStatusFragment.setRecordingMode(mRecordingService.getRecordingMode());

        if (mRecordingControlsFragment != null)
            mRecordingControlsFragment.onRecordingStateChanged(mRecordingService.getRecordingMode());

        if (mRecordingService.getRecordingMode() == RecordingMode.IDLE)
            getSupportActionBar().setTitle(R.string.my_recordings);
        else if (mRecordingService.getRecordingMode() == RecordingMode.RECORDING)
            getSupportActionBar().setTitle(R.string.state_recording);
    }

    @Override
    public void onAudioLevelChanged(final int percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRecordingControlsFragment != null)
                    mRecordingControlsFragment.onAudioLevelChanged(percentage);

                if (mRecordingStatusFragment != null)
                    mRecordingStatusFragment.onAudioLevelChanged(percentage);
            }
        });
    }

    private void showAbout() {
        FragmentHolderActivity.startActivity(this, FragmentHolderActivity.ActivityType.ABOUT, null);
    }

    private void showSettings() {
        Bundle bundle = FragmentHolderActivity.getBundleOfColor(Color.parseColor("#666666"));
        FragmentHolderActivity.startActivityForResult(this,
                FragmentHolderActivity.ActivityType.SETTINGS, REQUEST_CODE_SETTINGS, bundle);
    }



    public void setPrettyName(String string) {
        mRecordingService.setNextPrettyRecordingName(string);
        mRecordingStatusFragment.setFileName(string);
    }

    public void recordsLocal(){
        Intent i = new Intent(VoiceRecorderMainActivity.this, RecordsLocal.class);
        startActivity(i);
        finish();

    }
}