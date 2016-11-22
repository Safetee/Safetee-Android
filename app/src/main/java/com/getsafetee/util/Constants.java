package com.getsafetee.util;

/**
 * Stores all the constants used in the project.
 */
public class Constants {
    public static final java.lang.String UPLOAD_SERVICE_URL = "https://safetee2.herokuapp.com/api/v1/record/add";

    public static final class SmsConstants {
        public static final String COME_GET_ME = "Come get me";
        public static final String CALL_NEED_INTERRUPTION = "Call I need an interruption";
        public static final String NEED_TO_TALK = "I need to talk";
    }

    public static final String TAG_LOCATION = "#LOC#";
    public static final String TAG_LOCATION_URL = "#LOC_URL#";
    public static final String LOCATION_URL = "http://maps.google.com/?q=LAT,LON";
}
