package com.getsafetee;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getsafetee.FragmentHolderActivity;
import com.getsafetee.audiorecorder.activities.VoiceRecorderMainActivity;
import com.getsafetee.audiorecorder.fragments.AboutFragment;
import com.getsafetee.audiorecorder.fragments.RecordingControlsFragment;
import com.getsafetee.audiorecorder.fragments.RecordingStatusFragment;
import com.getsafetee.audiorecorder.fragments.RecordingsListFragment;
import com.getsafetee.audiorecorder.fragments.SettingsFragment;
import com.getsafetee.audiorecorder.models.RecordingMode;
import com.getsafetee.audiorecorder.models.RecordingsDatabase;
import com.getsafetee.audiorecorder.services.RecordingService;
import com.getsafetee.auth.SettingActivity;
import com.getsafetee.auth.SettingRecords;
import com.getsafetee.circleoffriends.FriendsList;
import com.getsafetee.safetee.R;

import org.w3c.dom.Text;


public class RecordsLocal extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = RecordsLocal.class.getSimpleName();

    private RecordingStatusFragment mRecordingStatusFragment;
    private RecordingControlsFragment mRecordingControlsFragment;
    private RecordingsListFragment mRecordingsListFragment;
    private Toolbar toolbar;
    private RecordingsDatabase recordingsDatabase;
    private TextView recordsInfo;
    private RelativeLayout records_info_container;
    private FloatingActionButton record;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder_main);



       toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recordingsDatabase = new RecordingsDatabase(this);
        recordsInfo = (TextView) findViewById(R.id.records_info);
        records_info_container = (RelativeLayout) findViewById(R.id.records_info_container);
        if (recordingsDatabase.getCount() < 1){
            recordsInfo.setText("You have no records");
            records_info_container.setVisibility(View.VISIBLE);
            recordsInfo.setVisibility(View.VISIBLE);
        }

        record = (FloatingActionButton) findViewById(R.id.launchrecord);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordsLocal.this, VoiceRecorderMainActivity.class));
            }
        });



        if (savedInstanceState == null) {
            mRecordingStatusFragment = new RecordingStatusFragment();
            mRecordingStatusFragment.setRetainInstance(true);

            mRecordingControlsFragment = new RecordingControlsFragment();
            mRecordingControlsFragment.setRetainInstance(true);

            switchToRecordings();

        }


    }




    private void switchToRecordings() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, getRecordingsListFragment());
        transaction.commit();
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




}