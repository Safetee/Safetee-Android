package com.getsafeteeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getsafeteeapp.safetee.R;
import com.getsafeteeapp.util.Constants;
import com.getsafeteeapp.util.ShowMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrganizationView extends AppCompatActivity {

    private ShowMessage message;

    private Intent getIntent;
    private TextView title;
    private TextView body;
    private ProgressBar getprogress;

    private String title_f;
    private  String body_f;
    private String type_f;
    private String url_f;

    private int HTML_FL = 3433;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_view);

        message = new ShowMessage(OrganizationView.this);

        getIntent = getIntent();
        setTitle("");
        type_f = getIntent.getStringExtra("title");
        if(type_f.equals("tou")){
            title_f = "Terms of Use";
            url_f = Constants.TERMS_OF_USE_URL;
        }else if (type_f.equals("faq")){
            title_f = "Frequently Asked Questions";
            url_f = Constants.FAQ_URL;
        }else if (type_f.equals("pp")){
            title_f = "Privacy Policy";
            url_f = Constants.PRIVACY_POLICY_URL;
        }

        getBody(url_f);

        getprogress = (ProgressBar) findViewById(R.id.getbody);
        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);

        title.setText(title_f);
    }

    private void getBody(final String url){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SAFETEE", "Request Response: " + response);
                getprogress.setVisibility(View.GONE);

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        // we declare strings to get user info
                        // success message
                        String content = jObj.getString("message");
                        getprogress.setVisibility(View.GONE);
                        body.setText(Html.fromHtml(content));
                    } else {
                        message.message("Error", "Something went wrong, please try again later.", "Dismiss");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    Log.i("SAFETEE", "Request Response: " + "Network connection failed");
                    message.message("Error", "Network connection failed", "Dismiss");
                    getprogress.setVisibility(View.GONE);
                }else {
                    if (!error.toString().isEmpty() && error.toString().length() > 3 && error.toString() != null) {
                        message.message("Error", "Our servers may be experiencing some downtime, please try again few minutes.", "Dismiss");
                        getprogress.setVisibility(View.GONE);
                    }
                }
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        //Add the request to the request queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }
}
