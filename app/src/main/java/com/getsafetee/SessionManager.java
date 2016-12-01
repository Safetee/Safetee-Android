package com.getsafetee;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "Safetee";
    private static final String KEY_IS_LOGGEDIN = "isLoggedin";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setUid(final String uid){
        editor.putString(KEY_UID, uid);
        editor.commit();
    }

    public void setUName(final String name){
        editor.putString(KEY_NAME, name);
        editor.commit();
    }
    public void setUEmail(final String email){
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }
    public void setUPhone(final String phone){
        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }
    public String getUid(){
        return pref.getString(KEY_UID, "");
    }
    public String getUName(){
        return pref.getString(KEY_NAME, "");
    }
    public String getUEmail(){
        return pref.getString(KEY_EMAIL, "");
    }
    public String getUPhone(){
        return pref.getString(KEY_PHONE, "");
    }
}
