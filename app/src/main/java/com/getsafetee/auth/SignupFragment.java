package com.getsafetee.auth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    public static final String TAG = SignupFragment.class.getSimpleName();

    private AutoCompleteTextView emailAutoComplete;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumber;
    private EditText passwordEditText;
    private Button signupButton;
    private RadioButton radioButton;
    RadioGroup radioGroup;
    private RadioButton bbb;
    private View mProgressView;
    private View mLoginFormView;
    View rootView;


    private String sex;
    private String password;

    private ProgressDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        firstNameEditText = (EditText) rootView.findViewById(R.id.first_name);
        lastNameEditText = (EditText) rootView.findViewById(R.id.last_name);
        emailAutoComplete = (AutoCompleteTextView) rootView.findViewById(R.id.sign_up_email);
        phoneNumber = (EditText) rootView.findViewById(R.id.sign_up_phone_number);
        passwordEditText = (EditText) rootView.findViewById(R.id.sign_up_password);
        signupButton = (Button) rootView.findViewById(R.id.sign_up_submit);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioSex);

        // Progress dialog
       /* pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(true);
*/

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("SAFETEE","Name: "+firstNameEditText.getText().toString()+" "+lastNameEditText.getText().toString());
                Log.i("SAFETEE","Email: "+ emailAutoComplete.getText().toString());
                Log.i("SAFETEE", "Phone Number: "+ phoneNumber.getText().toString() );
                Log.i("SAFETEE", "Sex: "+ getSex());
                Log.i("SAFETEE", "Password: "+passwordEditText.getText().toString());

                /*pDialog.setMessage("Hold on, we are creating your account...");
                pDialog.show();
*/
                StringRequest stringRequest = new StringRequest(Method.POST, Constants.SIGN_UP_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("SAFETEE", "Request Response: " + response.toString());
                       // pDialog.dismiss();

                        //get response from api

                        try {

                            // request response
                            JSONObject jObj = new JSONObject(response);
                            // get response value
                            int success = jObj.getInt("success");
                            //if success is 1 then signup went well
                            if (success == 1) {
                                // we declare strings to get user info
                                // success message
                                String successMsg = jObj.getString("message");
                                // user id
                                String uid = jObj.getString("uid");
                                // user fullname
                                String name = jObj.getString("fullname");
                                // user email
                                String email = jObj.getString("email");
                                // user phone number
                                String phone = jObj.getString("phone_no");

                                // use the strings that has user info tied to them to do whatever
                                // store them to sharedpreference or to sqlite
                                // continue to login activity or mainactivity

                            } else {

                                // Error occurred in registration. Get the error
                                // get message
                                String errorMsg = jObj.getString("message");
                                // show error message to user
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
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                ){
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("name", "Ilo"+" Calistus");
                        params.put("phone_no", "0815678883");
                        params.put("email", "juanna3@test.com");
                        params.put("sex", "female");
                        params.put("Location", "");
                        params.put("password", "juanna2");
                        return params;
                    }
                };
                //Add the request to the request queue
                Volley.newRequestQueue(getActivity()).add(stringRequest);
            }
        });

        return rootView;
    }

    public String getSex() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) rootView.findViewById(selectedId);
        return radioButton.getText().toString();
    }

}