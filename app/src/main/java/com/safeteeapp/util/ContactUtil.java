package com.safeteeapp.util;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.IOException;
import java.io.InputStream;

public class ContactUtil {

    Context _context;

    public ContactUtil(Context context){
        this._context = context;
    }

    public Uri getPhotoUri(final String getId) {
        try {
            Cursor cur = this._context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + getId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(getId));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public Bitmap retrieveContactPhoto(String getId) {

        Bitmap photo;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this._context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(getId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                return photo;
            }

            if(inputStream != null) {
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;

    }

    public String contactIdByPhoneNumber(Context ctx, String phoneNumber) {
        String contactId = null;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            ContentResolver contentResolver = ctx.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[] { ContactsContract.PhoneLookup._ID };

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }
        }
        return contactId;
    }

}
