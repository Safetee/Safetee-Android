package com.safeteeapp.util;

/**
 * Stores all the constants used in the project.
 */
public class Constants {

    // API URLs
    public static final String API_DNS = "http://www.getsafetee.com/";
    public static final String API_V1 = "api/v1/";
    public static final String UPLOAD_SERVICE_URL = API_DNS + API_V1 + "record/add";
    public static final String LOGIN_URL = API_DNS + API_V1 + "user/login";
    public static final String SIGN_UP_URL = API_DNS + API_V1 + "user/signup";
    public static final String SETTING_URL = API_DNS + API_V1 + "user/setting";
    public static final String USER_RECORDS_URL = API_DNS + API_V1 + "records/get/";
    public static final String REPORT_URL = API_DNS + API_V1 + "report/add";
    public static final String GET_HELP_URL = API_DNS + API_V1 + "user/gethelp";
    public static final String GET_AGENCIES_NGO = API_DNS + API_V1 + "agencies/get/NGO";
    public static final String DONATE_URL = API_DNS + API_V1 + "donate";
    public static final String TIPS_URL = API_DNS + API_V1 + "tips/get";
    public static final String FAQ_URL =  API_DNS + API_V1 + "user/faq";
    public static final String TERMS_OF_USE_URL =  API_DNS + API_V1 + "user/tou";
    public static final String PRIVACY_POLICY_URL =  API_DNS + API_V1 + "user/pp";

    // INTERSWITCH IDS
    public static final String ISW_CLIENT_ID = "IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218";
    public static final String ISW_CLIENT_SECRET = "XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=";




    public static final class SmsConstants {
        public static final String COME_GET_ME = "I Need Help";
        public static final String CALL_NEED_INTERRUPTION = "Call I Need an Interruption";
        public static final String NEED_TO_TALK = "I Need to Talk";
    }

    public static final String TAG_LOCATION = "#LOC#";
    public static final String TAG_LOCATION_URL = "#LOC_URL#";
    public static final String LOCATION_URL = "http://maps.google.com/?q=LAT,LON";
}
