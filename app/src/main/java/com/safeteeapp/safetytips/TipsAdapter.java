package com.safeteeapp.safetytips;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.safeteeapp.safetee.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TipsAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<TipItem> implements TipsDatabase.OnDatabaseChangedListener {
    private Context mContext;
    private TipsDatabase mDatabase;
    private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public TipsAdapter(Context context, TipsDatabase database) {
        super();
        mContext = context;
        mDatabase = database;
        mDatabase.setOnDatabaseChangedListener(this);
    }

    public TipsAdapter(Context context) {
        super();
        mContext = context;
        mDatabase = new TipsDatabase(context);
        mDatabase.setOnDatabaseChangedListener(this);
    }

    @Override
    public int getCount() {
        return mDatabase.getCount();
    }

    @Override
    public View getView(int position, View covertView, ViewGroup arg2) {
        View v = covertView;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.tip_list_item, arg2, false);
        }

        TextView title = (TextView) v.findViewById(android.R.id.text1);
        TextView body = (TextView) v.findViewById(android.R.id.summary);
        TextView date = (TextView) v.findViewById(android.R.id.text2);

        TipItem item = getItem(position);

        title.setText(item.getName());
        date.setText(getTime(item.getTime()));
        body.setText(item.getBody());

        return v;
    }

    @Override
    public TipItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    public static String getTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        return mDateAddedFormatter.format(date);
    }


    public TipsDatabase getDatabase() {
        return mDatabase;
    }


    @Override
    public void onDatabaseEntryUpdated() {
        notifyDataSetChanged();
    }
}
