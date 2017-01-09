package com.getsafetee.util;


import android.Manifest;
import android.content.Context;

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

     private void noGPS(){
         //Toast.makeText(_context, "Please activate location", Toast.LENGTH_LONG).show();

    }

}
