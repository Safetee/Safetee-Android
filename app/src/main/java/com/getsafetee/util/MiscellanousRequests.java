package com.getsafetee.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getsafetee.MainActivity2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscellanousRequests {
    Context _context;
    ProgressDialog pDialog;
    private String return_msg;

    public MiscellanousRequests(Context context){
        this._context = context;
    }

    public String makeRequest(final String url, final List<String> keys, final List<String> values, final int paramcount){
        // Progress dialog
        pDialog = new ProgressDialog(_context);
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SAFETEE", "Request Response: " + response);
                pDialog.hide();

                //get response from api

                try {

                    // request response
                    JSONObject jObj = new JSONObject(response);
                    // get response value
                    String return_msg = jObj.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    Log.i("SAFETEE", "Request Response: " + "Network connection failed");
                    return_msg = "Network connection failed";
                }
            }
        }
        ){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                for(int i = 0; i < paramcount; i++) {
                    params.put(keys.get(i), values.get(i));
                }
                return params;
            }
        };
        //Add the request to the request queue
        Volley.newRequestQueue(_context).add(stringRequest);

        return return_msg;
    }
}
