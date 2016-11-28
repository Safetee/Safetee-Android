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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;

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
    private RadioButton bbb;
    private View mProgressView;
    private View mLoginFormView;


    private String email;
    private String password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        firstNameEditText = (EditText) rootView.findViewById(R.id.first_name);
        lastNameEditText = (EditText) rootView.findViewById(R.id.last_name);
        emailAutoComplete = (AutoCompleteTextView) rootView.findViewById(R.id.sign_up_email);
        phoneNumber = (EditText) rootView.findViewById(R.id.sign_up_phone_number);
        passwordEditText = (EditText) rootView.findViewById(R.id.password);
        signupButton = (Button) rootView.findViewById(R.id.sign_up_submit);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST, Constants.SIGN_UP_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("SAFETEE", "Request Response: " + response);
                        Toast.makeText(getActivity(), "Signup Successful", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("SAFETEE", "Request Response: " + "Signup failed");
                        Toast.makeText(getActivity(), "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("name", firstNameEditText.getText().toString());
                        params.put("phone_number", phoneNumber.getText().toString());
                        params.put("email", emailAutoComplete.getText().toString());
                        params.put("sex", getSex(rootView));
                        params.put("Location", "Weti you dey usam do");
                        params.put("password", passwordEditText.getText().toString());
                        return params;
                    }
                };
            }
        });

        return rootView;
    }

    public String getSex(View view) {
        String sex = null;
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_female:
                if (checked)
                    sex = "female";
                    break;
            case R.id.radio_male:
                if (checked)
                    sex = "male";
                    break;
        }
        return sex;
    }

}
