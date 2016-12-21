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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getsafetee.FragmentHolderActivity;
import com.getsafetee.audiorecorder.fragments.AboutFragment;
import com.getsafetee.audiorecorder.fragments.RecordingControlsFragment;
import com.getsafetee.audiorecorder.fragments.RecordingStatusFragment;
import com.getsafetee.audiorecorder.fragments.RecordingsListFragment;
import com.getsafetee.audiorecorder.fragments.SettingsFragment;
import com.getsafetee.audiorecorder.models.RecordingMode;
import com.getsafetee.audiorecorder.services.RecordingService;
import com.getsafetee.auth.SettingActivity;
import com.getsafetee.auth.SettingRecords;
import com.getsafetee.circleoffriends.FriendsList;
import com.getsafetee.safetee.R;


public class RecordsLocal extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = RecordsLocal.class.getSimpleName();

    private RecordingStatusFragment mRecordingStatusFragment;
    private RecordingControlsFragment mRecordingControlsFragment;
    private RecordingsListFragment mRecordingsListFragment;
    private Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder_main);



       toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.records_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(RecordsLocal.this, SettingRecords.class));
        }

        return super.onOptionsItemSelected(item);
    }


}