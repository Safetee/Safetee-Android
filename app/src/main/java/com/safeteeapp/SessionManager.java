package com.safeteeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

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
    private static final String PREF_NAME = "Safetee_rock_eagle_2";
    private static final String KEY_IS_LOGGEDIN = "isLoggedin";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PIN = "pin";
    private static final String KEY_TOUR = "isToured";
    private static final String KEY_AUTO_UPLOAD = "isAutoupload";
    private static final String KEY_DISCREET_MODE = "isDiscreet";
    private static final String KEY_DISCREET_TUTORIAL = "isDiscreetTutorial";
    private static final String KEY_HELP_MESSAGE = "helpMessage";
    //
    public static final int NUMBER_OF_COMRADES = 6;
    public static final List<String> COMRADE_KEY = Arrays.asList("comrade1Key", "comrade2Key", "comrade3Key", "comrade4Key", "comrade5Key", "comrade6Key");

    //
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

    public void setMode(boolean isDiscreet) {

        editor.putBoolean(KEY_DISCREET_MODE, isDiscreet);
        editor.commit();
    }

    public void setDiscreetTutorial(boolean isDiscreetTutorial) {

        editor.putBoolean(KEY_DISCREET_TUTORIAL, isDiscreetTutorial);
        editor.commit();
    }

    public boolean isLoggedIn(){

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isDiscreet(){

        return pref.getBoolean(KEY_DISCREET_MODE, false);
    }

    public boolean isDiscreetTutorial(){

        return pref.getBoolean(KEY_DISCREET_TUTORIAL, false);
    }

    public void setUid(final String uid){
        editor.putString(KEY_UID, uid);
        editor.commit();
    }

    public void setUName(final String name){
        editor.putString(KEY_NAME, name);
        editor.commit();
    }
    public void setUHelpMessage(final String helpMessage){
        editor.putString(KEY_HELP_MESSAGE, helpMessage);
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
    public void setUPin(final String pin){
        editor.putString(KEY_PIN, pin);
        editor.commit();
    }
    public void setTour(boolean hasTour) {
        editor.putBoolean(KEY_TOUR, hasTour);
        editor.commit();
        Log.d(TAG, "User toured");
    }
    public void setAutoupload(boolean upload) {
        editor.putBoolean(KEY_AUTO_UPLOAD, upload);
        editor.commit();
    }
    public boolean getToured(){
        return pref.getBoolean(KEY_TOUR, false);
    }
    public boolean getAutoupload(){
        return pref.getBoolean(KEY_AUTO_UPLOAD, false);
    }
    public String getUid(){
        return pref.getString(KEY_UID, "");
    }
    public String getUName(){
        return pref.getString(KEY_NAME, "");
    }
    public String getUHelpMessage(){
        return pref.getString(KEY_HELP_MESSAGE, "");
    }
    public String getUEmail(){
        return pref.getString(KEY_EMAIL, "");
    }
    public String getUPhone(){
        return pref.getString(KEY_PHONE, "");
    }
    public String getUPin(){
        return pref.getString(KEY_PIN, "");
    }
    //
    public void freeUser(){
        editor.putString(KEY_UID, "");
        editor.putString(KEY_PIN, "");
        editor.putString(KEY_PHONE, "");
        editor.putString(KEY_EMAIL, "");
        editor.putString(KEY_NAME, "");
        editor.commit();
        editor.clear();
    }
    public void freeCircleFriends(){
        for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
            editor.putString(COMRADE_KEY.get(i), "");
            editor.commit();
            editor.clear();
        }
    }
}
