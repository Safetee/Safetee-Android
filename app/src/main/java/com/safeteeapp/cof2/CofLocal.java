package com.safeteeapp.cof2;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.safeteeapp.SessionManager;
import com.safeteeapp.app.AppController;
import com.safeteeapp.audiorecorder.adapters.RecordingsAdapter;
import com.safeteeapp.auth.SettingRecords;
import com.safeteeapp.circleoffriends.ContactListDialog;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.Constants;
import com.safeteeapp.util.LocationManager;
import com.safeteeapp.util.ShowMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import it.sephiroth.android.library.tooltip.Tooltip;

public class CofLocal extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = CofLocal.class.getSimpleName();
    public static final int REQUEST_SELECT_CONTACT = 100;
    int update = 0;
    CofListFragment mCofListFragment;
    CofDatabase mDatabase;
    SessionManager session;
    private ShowMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cof_local_main);

        mDatabase = new CofDatabase(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        message = new ShowMessage(this);
        FloatingActionButton addcontact = (FloatingActionButton) findViewById(R.id.addcontact);
        Tooltip.make(this, new Tooltip.Builder(101)
                        .anchor(addcontact, Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 4000)
                        .activateDelay(500)
                        .showDelay(200)
                        .text("Click icon to add contact")
                        .maxWidth(450)
                        .withArrow(true)
                        .withStyleId(R.style.ToolTipLayout)
                        .withOverlay(true).build()
        ).show();

            switchToCof();

    }

    private void switchToCof() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, getCofListFragment());
        transaction.commit();
    }


    private CofListFragment getCofListFragment() {
        if (mCofListFragment == null)
            mCofListFragment = new CofListFragment();

        return mCofListFragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_CONTACT) {
            Cursor cursor = null;
            String phoneNumber = "";
            String phoneName = "No Name";
            String phoneType = "";
            Set<String> allNumbers = new HashSet<>();
            Set<String> allNames = new HashSet<>();
            Set<String> allTypes = new HashSet<>();
            int phoneIdx;
            final String contactDisplayName;
            String contactType;
            try {
                Uri result = data.getData();
                String id = result.getLastPathSegment();
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                if (cursor.moveToFirst()) {
                    contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactType = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    //CircleOfTrustFragment.allNames.put(tag,contactDisplayName);
                    //indexesUpdated.add(tag-1);
                    while (!cursor.isAfterLast()) {
                        phoneNumber = cursor.getString(phoneIdx);
                        phoneName = contactDisplayName;
                        phoneType = contactType;
                        allNumbers.add(phoneNumber);
                        allNames.add(contactDisplayName);
                        allTypes.add(phoneType);
                        cursor.moveToNext();
                    }
                } else {
                    //no results actions
                   // showNoPhoneNumberToast();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                final String phoneNameL = phoneName;
                if (allNumbers.size() > 1) {
                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    final CharSequence[] itemsname = allNames.toArray(new String[allNames.size()]);
                    final CharSequence[] itemstype = allTypes.toArray(new String[allTypes.size()]);
                    new Handler().post(new Runnable() {
                        public void run() {

                            final ContactListDialog dialog = ContactListDialog.newInstance(CofLocal.this, getString(R.string.choose_number), items);
                            dialog.setListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedNumber = items[position - 1].toString();
                                    String selectedName = phoneNameL;
                                    //String selectedType = itemstype[position - 1].toString();
                                    selectedNumber = selectedNumber.replace("-", "");

                                    saveContact(selectedName, selectedNumber, "");
                                    dialog.dismiss();

                                }
                            });
                            dialog.show(getSupportFragmentManager(), getString(R.string.choose_number));
                        }
                    });

                } else {
                    String selectedNumber = phoneNumber;
                    String selectedName = phoneName;
                    String selectedType = phoneType;
                    selectedNumber = selectedNumber.replace("-", "");
                    if(selectedName.equals(selectedNumber)){
                        //selectedName = "No Name";
                    }
                    saveContact(selectedName, selectedNumber, phoneType);

                }

                if (phoneNumber.length() == 0) {
                    //no numbers found actions
                    //phoneInput.setText("");
                    //nameInput.setText("");
                }
            }
        }

    }

    public void addContact(View v) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveContact(final String fname, final String fphone, final String ftype){
        if(!fphone.isEmpty()) {
            if (mDatabase.findCof(fphone) == 0) {
                mDatabase.addCof(fname, fphone, fphone, session.getUid());
                message.message("Success", fname + " was successfully added to your Circle of Friends.\n\nswipe contact to right or left to delete.", "Dismiss");
                refreshCof();
            } else {
                message.message("Error", fname + " is already added to your Circle of Friends", "Dismiss");
            }
        }else{
            message.message("Error", "No phone number detected for selected contact", "Dismiss");

        }

    }

    public void refreshCof(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(mCofListFragment).attach(mCofListFragment).commit();
    }

}


