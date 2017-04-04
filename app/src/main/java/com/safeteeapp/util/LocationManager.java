package com.safeteeapp.util;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class LocationManager{
    private static String TAG = LocationManager.class.getSimpleName();
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    GPSTracker gps;
    Context _context;

    public LocationManager(Context context) {
        this._context = context;
    }

    public double getLong(){
        double lng = 0.0;
        // create class object
        gps = new GPSTracker(_context);
        if(gps.canGetLocation()){
            lng = gps.getLongitude();
        }else{
            noGPS();
        }
        return lng;
    }

    public double getLat(){
        double lat = 0.0;
        // create class object
        gps = new GPSTracker(_context);
        if(gps.canGetLocation()){
            lat = gps.getLatitude();
        }else{
            noGPS();
        }
        return lat;
    }

    public boolean getIfLocation(){
        // create class object
        gps = new GPSTracker(_context);
        return gps.getIfLocationOn();
    }

    private void noGPS(){
        //Toast.makeText(_context, "Please activate location", Toast.LENGTH_LONG).show();

    }

    public void showLocationSettingsAlert(String mMessage){
        String mFMessage = mMessage;
        if(mFMessage.isEmpty() && mFMessage.length() < 0){
            mFMessage = "Location is not enabled. Goto settings to enable it";
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

        // Setting Dialog Title
        //alertDialog.setTitle("Location Setting");

        // Setting Dialog Message
        alertDialog.setMessage(mFMessage);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                _context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}

