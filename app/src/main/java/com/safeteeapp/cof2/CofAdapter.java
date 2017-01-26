package com.safeteeapp.cof2;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.safeteeapp.audiorecorder.models.RecordingItem;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.CircleTransform;
import com.safeteeapp.util.ContactUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CofAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<CofItem> implements CofDatabase.OnDatabaseChangedListener {
    private Context mContext;
    private CofDatabase mDatabase;
    private Activity activity;
    private ContactUtil mContactUtil;
    private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public CofAdapter(Context context, CofDatabase database) {
        super();
        mContext = context;
        mDatabase = database;
        mDatabase.setOnDatabaseChangedListener(this);
    }

    public CofAdapter(Context context) {
        super();
        mContext = context;
        mDatabase = new CofDatabase(context);
        mDatabase.setOnDatabaseChangedListener(this);
        mContactUtil = new ContactUtil(context);
    }

    @Override
    public int getCount() {
        return mDatabase.getCount();
    }

    @Override
    public View getView(int position, View covertView, ViewGroup arg2) {
        View v = covertView;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.cof_list_item, arg2, false);
        }

        ImageView dp = (ImageView) v.findViewById(R.id.dp);
        TextView title = (TextView) v.findViewById(android.R.id.text1);
        TextView body = (TextView) v.findViewById(android.R.id.summary);
        TextView date = (TextView) v.findViewById(android.R.id.text2);

        CofItem item = getItem(position);

        title.setText(item.getName());
        date.setText(getTime(item.getTime()));
        body.setText(item.getBody());

        Uri u = mContactUtil.getPhotoUri(mContactUtil.contactIdByPhoneNumber(mContext, item.getBody()));
        //Bitmap bDp = mContactUtil.retrieveContactPhoto(mContactUtil.contactIdByPhoneNumber(mContext, item.getBody()));
        if (u != null) {
            Glide.with(mContext).load(u)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dp);
        }else {
            Glide.with(mContext).load(R.drawable.user2)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dp);
        }

        return v;
    }

    @Override
    public CofItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    public static String getTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        return mDateAddedFormatter.format(date);
    }


    public CofDatabase getDatabase() {
        return mDatabase;
    }

    @Override
    public boolean remove(@NonNull Object object) {
        CofItem item = (CofItem)object;
        mDatabase.removeItemWithId(item.getId());
        return super.remove(object);
    }


    @Override
    public void onDatabaseEntryUpdated() {
        notifyDataSetChanged();
    }
}
