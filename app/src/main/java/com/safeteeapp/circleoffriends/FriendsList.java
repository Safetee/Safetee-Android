package com.safeteeapp.circleoffriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.safeteeapp.MainActivity2;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.ShowMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsList extends AppCompatActivity {
    public static final int REQUEST_SELECT_CONTACT = 100;
    public static final int NUMBER_OF_COMRADES = 6;
    List<EditText> comradeEditText = new ArrayList<>(NUMBER_OF_COMRADES);
    List<TextView> comradeEditTextName = new ArrayList<>(NUMBER_OF_COMRADES);
    List<Integer> indexesUpdated = new ArrayList<>();
    Toolbar toolbar;
    ShowMessage messager;
    private View selectedButton;
    private Button okButton;

    private EditText mEd1, mEd2, mEd3, mEd4, mEd5, mEd6;

    public static final String MY_PREFERENCES = "Safetee_rock_eagle_2"; // don't change, it controls all app prefs
    public static final List<String> COMRADE_KEY = Arrays.asList("comrade1Key", "comrade2Key", "comrade3Key", "comrade4Key", "comrade5Key", "comrade6Key");
    public static final List<String> COMRADE_NAME = Arrays.asList("comrade1Name", "comrade2Name", "comrade3Name", "comrade4Name", "comrade5Name", "comrade6Name");

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messager = new ShowMessage(this);

        mEd1 = (EditText) findViewById(R.id.comrade1EditText);
        mEd1.setKeyListener(null);
        comradeEditText.add(mEd1);
        mEd2 = (EditText) findViewById(R.id.comrade2EditText);
        mEd2.setKeyListener(null);
        comradeEditText.add(mEd2);
        mEd3 = (EditText) findViewById(R.id.comrade3EditText);
        mEd3.setKeyListener(null);
        comradeEditText.add(mEd3);
        mEd4 = (EditText) findViewById(R.id.comrade4EditText);
        mEd4.setKeyListener(null);
        comradeEditText.add(mEd4);
        mEd5 = (EditText) findViewById(R.id.comrade5EditText);
        mEd5.setKeyListener(null);
        comradeEditText.add(mEd5);
        mEd6 = (EditText) findViewById(R.id.comrade6EditText);
        mEd6.setKeyListener(null);
        comradeEditText.add(mEd6);

        /* methods are useless, bin redefined above
        comradeEditText.add((EditText) findViewById(R.id.comrade2EditText));
        comradeEditText.add((EditText) findViewById(R.id.comrade3EditText));
        comradeEditText.add((EditText) findViewById(R.id.comrade4EditText));
        comradeEditText.add((EditText) findViewById(R.id.comrade5EditText));
        comradeEditText.add((EditText) findViewById(R.id.comrade6EditText));
        */

        comradeEditTextName.add((TextView) findViewById(R.id.comrade1EditTextName));
        comradeEditTextName.add((TextView) findViewById(R.id.comrade2EditTextName));
        comradeEditTextName.add((TextView) findViewById(R.id.comrade3EditTextName));
        comradeEditTextName.add((TextView) findViewById(R.id.comrade4EditTextName));
        comradeEditTextName.add((TextView) findViewById(R.id.comrade5EditTextName));
        comradeEditTextName.add((TextView) findViewById(R.id.comrade6EditTextName));



        okButton = (Button) findViewById(R.id.okButton);
        okButton.setFocusable(true);

        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
            comradeEditTextName.get(i).setText(sharedpreferences.getString(COMRADE_NAME.get(i), ""));

            comradeEditText.get(i).setText(sharedpreferences.getString(COMRADE_KEY.get(i), ""));
        }
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean noDuplicateNumber = noDuplicateNumber();

                //To store previous values (numbers) of comrades
                List<String> old_comrade = new ArrayList<String>(NUMBER_OF_COMRADES);
                //To store names
                List<String> old_comrade_name = new ArrayList<String>(NUMBER_OF_COMRADES);

                //To store newly entered values (numbers) of comrades, if any
                List<String> new_comrade = new ArrayList<String>(NUMBER_OF_COMRADES);
                //To store names
                List<String> new_comrade_name = new ArrayList<String>(NUMBER_OF_COMRADES);

                //Retrieving stored values
                for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
                    old_comrade.add(sharedpreferences.getString(COMRADE_KEY.get(i), ""));
                    old_comrade_name.add(sharedpreferences.getString(COMRADE_NAME.get(i), ""));
                }

                //Retrieving new values
                for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
                    final String comradeInfo = comradeEditText.get(i).getText().toString();
                    /*
                    if(comradeInfo.isEmpty() || comradeInfo.length() < 11){
                        new_comrade.add("");
                        new_comrade_name.add("");
                    }else {
                        final String[] comradeInfo_f = comradeInfo.split(" ");
                        */
                        new_comrade.add(comradeEditText.get(i).getText().toString());
                        new_comrade_name.add(comradeEditTextName.get(i).getText().toString());

                }


                if (noDuplicateNumber) {

                    for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
                        editor.putString(COMRADE_KEY.get(i), new_comrade.get(i));
                        editor.putString(COMRADE_NAME.get(i), new_comrade_name.get(i));
                    }

                    boolean status = editor.commit();
                    if (status) {

                        //Check if any updation is required
                        boolean needToUpdate = false;
                        for(int i = 0; i < NUMBER_OF_COMRADES; i++)
                            if(!old_comrade.get(i).equals(new_comrade.get(i))){
                                needToUpdate = true;
                                /*if(CircleOfTrustFragment.allNames.containsKey(i+1) && !indexesUpdated.contains(i))
                                {
                                    CircleOfTrustFragment.allNames.remove(i+1);
                                }*/

                            }

                        //Nothing to update
                        if (!needToUpdate) {
                            //messager.message("Error", getString(R.string.not_updated_phone_numbers), "Dismiss");
                            //Toast.makeText(getApplicationContext(), getString(R.string.not_updated_phone_numbers), Toast.LENGTH_LONG).show();
                        }

                        //Need to update
                        else {
                            messager.message("Success", getString(R.string.updated_phone_numbers), "Dismiss");
                            //Toast.makeText(getApplicationContext(), getString(R.string.updated_phone_numbers), Toast.LENGTH_LONG).show();
                        }

                        //go back to main activiyty
                        startActivity(new Intent(FriendsList.this, MainActivity2.class));
                    } else {
                        messager.message("Error",  getString(R.string.updated_phone_numbers_fail), "Dismiss");
                        //Toast.makeText(getApplicationContext(), getString(R.string.updated_phone_numbers_fail), Toast.LENGTH_LONG).show();
                    }

                } else {
                    messager.message("Error", getString(R.string.duplicate_number_errormessage), "Dismiss");
                    //Toast.makeText(getApplicationContext(), getString(R.string.duplicate_number_errormessage), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Function to show cursor on being clicked
        for(int i = 0; i < NUMBER_OF_COMRADES; i++) {
            final EditText comradeText = comradeEditText.get(i);
            comradeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comradeText.setCursorVisible(true);
                }
            });
        }
    }

    /**
     * Finds the appropriate edit text for the given contact pick button
     *
     * @param view Contact pick button
     * @return
     */
    private EditText findInput(View view) {
        if (view != null) {
            int index = -1;
            String tag = (String) view.getTag();
            try{
                index = Integer.parseInt( tag ) - 1 ;
            }
            catch ( ClassCastException | NumberFormatException e ){
                e.printStackTrace();
            }

            if(index != -1)
                return comradeEditText.get(index);
            else
                return null;
        }
        return null;
    }

    private TextView findInputName(View view) {
        if (view != null) {
            int index = -1;
            String tag = (String) view.getTag();
            try{
                index = Integer.parseInt( tag ) - 1 ;
            }
            catch ( ClassCastException | NumberFormatException e ){
                e.printStackTrace();
            }

            if(index != -1)
                return comradeEditTextName.get(index);
            else
                return null;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_CONTACT) {
            final EditText phoneInput = findInput(selectedButton);
            final TextView nameInput = findInputName(selectedButton);
            int tag = Integer.parseInt(selectedButton.getTag().toString());
            if(phoneInput == null){
                return;
            }
            if(nameInput == null){
                return;
            }
            Cursor cursor = null;
            String phoneNumber = "";
            String phoneName = "No Name";
            Set<String> allNumbers = new HashSet<>();
            Set<String> allNames = new HashSet<>();
            int phoneIdx;
            String contactDisplayName = "No Name";
            try {
                Uri result = data.getData();
                String id = result.getLastPathSegment();
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                if (cursor.moveToFirst()) {
                    contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //CircleOfTrustFragment.allNames.put(tag,contactDisplayName);
                    indexesUpdated.add(tag-1);
                    while (!cursor.isAfterLast()) {
                        phoneNumber = cursor.getString(phoneIdx);
                        phoneName = contactDisplayName;
                        allNumbers.add(phoneNumber);
                        allNames.add(contactDisplayName);
                        cursor.moveToNext();
                    }
                } else {
                    //no results actions
                    showNoPhoneNumberToast();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

                if (allNumbers.size() > 1) {
                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    final CharSequence[] itemsname = allNames.toArray(new String[allNames.size()]);
                    new Handler().post(new Runnable() {
                        public void run() {

                            final ContactListDialog dialog = ContactListDialog.newInstance(FriendsList.this, getString(R.string.choose_number), items);
                            dialog.setListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedNumber = items[position - 1].toString();
                                    String selectedName = itemsname[position - 1].toString();
                                    selectedNumber = selectedNumber.replace("-", "");
                                    if(selectedName.equals(selectedNumber)){
                                        //selectedName = "No Name";
                                    }
                                    if (noDuplicateContactNumber(selectedNumber)) {
                                        phoneInput.setText(selectedNumber);
                                        nameInput.setText(selectedName);
                                        setFocusOnNextView();
                                    } else {
                                        messager.message("Error", getString(R.string.duplicate_number_errormessage), "Dismiss");
                                        //Toast.makeText(getApplicationContext(), getString(R.string.duplicate_number_errormessage), Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            dialog.show(getSupportFragmentManager(), getString(R.string.choose_number));
                        }
                    });

                } else {
                    String selectedNumber = phoneNumber;
                    String selectedName = phoneName;
                    selectedNumber = selectedNumber.replace("-", "");
                    if(selectedName.equals(selectedNumber)){
                        //selectedName = "No Name";
                    }
                    if(noDuplicateContactNumber(selectedNumber)) {
                        phoneInput.setText(selectedNumber);
                        nameInput.setText(selectedName);
                        setFocusOnNextView();
                    }
                    else {
                        messager.message("Error", getString(R.string.duplicate_number_errormessage), "Dismiss");
                       // Toast.makeText(getApplicationContext(), getString(R.string.duplicate_number_errormessage), Toast.LENGTH_LONG).show();
                    }
                }

                if (phoneNumber.length() == 0) {
                    //no numbers found actions
                    //phoneInput.setText("");
                    //nameInput.setText("");
                    showNoPhoneNumberToast();
                }
            }
        }

    }

    private void setFocusOnNextView() {
        int index = -1;
        View currentFocus = selectedButton;
        String tag = (String)currentFocus.getTag();
        try
        {
            index = Integer.parseInt(tag);
            Log.d("index", String.valueOf(index));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        if(index >=0 && index <= comradeEditText.size()) {
            boolean isEmpty = false;
            for (int i = 0; i < comradeEditText.size() && !isEmpty; ++i) {
                if (comradeEditText.get(i).getText().toString().equals("")) {
                    isEmpty = true;
                    comradeEditText.get(i).requestFocus();
                }
            }
            //if (!isEmpty)
                //okButton.requestFocus();
        }
        //
        if(index >=0 && index <= comradeEditTextName.size()) {
            boolean isEmpty = false;
            for (int i = 0; i < comradeEditTextName.size() && !isEmpty; ++i) {
                if (comradeEditTextName.get(i).getText().toString().equals("")) {
                    isEmpty = true;
                    comradeEditTextName.get(i).requestFocus();
                }
            }
            if (!isEmpty)
                okButton.requestFocus();
        }
    }

    private void showNoPhoneNumberToast() {
        messager.message("Error", getString(R.string.no_phone_number), "Dismiss");
        //Toast.makeText(FriendsList.this, R.string.no_phone_number, Toast.LENGTH_LONG).show();
    }

    /**
     * Checks for the selected number exist in other contacts
     * @param selectedNumber
     * @return true if duplicate exist
     */
    private boolean noDuplicateContactNumber(String selectedNumber) {

        boolean result = true;

        for(int i = 0; i < NUMBER_OF_COMRADES; i++){
            if ( comradeEditText.get( i ).getText().toString().equals( selectedNumber ) ){
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Lists the comrades numbers which are not empty
     * @return List of numbers which are not empty
     */
    private List<String> nonEmptyComradeNumbers() {

        List<String> nonEmptyComradeNumbers = new ArrayList<String>();
        for(EditText number : comradeEditText) {
            if(number.getText().toString().length() != 0)
                nonEmptyComradeNumbers.add(number.getText().toString());
        }

        return nonEmptyComradeNumbers;
    }

    /**
     * Check for duplicate numbers
     * @return true if no duplicate number else returns false
     */
    private boolean noDuplicateNumber() {
        boolean noDuplicate = true;
        List<String> comradeNumbers = nonEmptyComradeNumbers();
        Set<String> uniqueNumbersSet = new HashSet<>();

        for (String str : comradeNumbers) {
            if (uniqueNumbersSet.add(str) == false) {
                noDuplicate = false;
            }
        }
        return noDuplicate;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start for selecting contacts from standard contract picker
     * @param v
     */
    public void addContact(View v) {
        try {
            selectedButton = v;
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}