package com.getsafetee.audiorecorder.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getsafetee.safetee.R;
import com.getsafetee.audiorecorder.models.RecordingItem;
import com.getsafetee.audiorecorder.models.RecordingsDatabase;
import com.nhaarman.listviewanimations.ArrayAdapter;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordingsAdapter extends ArrayAdapter<RecordingItem> implements RecordingsDatabase.OnDatabaseChangedListener {
	private Context mContext;
	private RecordingsDatabase mDatabase;
	private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.getDefault());
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

	public RecordingsAdapter(Context context, RecordingsDatabase database) {
		super();
		mContext = context;
		mDatabase = database;
		mDatabase.setOnDatabaseChangedListener(this);
	}

	public RecordingsAdapter(Context context) {
		super();
		mContext = context;
		mDatabase = new RecordingsDatabase(context);
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
			v = LayoutInflater.from(mContext).inflate(R.layout.recording_list_item, arg2, false);
		}

		TextView title = (TextView) v.findViewById(android.R.id.text1);
		TextView date = (TextView) v.findViewById(android.R.id.summary);
		TextView lengthView = (TextView) v.findViewById(android.R.id.text2);

		RecordingItem item = getItem(position);

		title.setText(item.getName());
		date.setText(getTime(item.getTime()));
		lengthView.setText(getLengthString(item.getLength()));

		return v;
	}

	@Override
	public RecordingItem getItem(int position) {
		return mDatabase.getItemAt(position);
	}

	public static String getTime(long milliSeconds) {
		Date date = new Date(milliSeconds);
		return mDateAddedFormatter.format(date);
	}

	public static String getLengthString(int length) {
		return mDateFormat.format(length);
	}

	public RecordingsDatabase getDatabase() {
		return mDatabase;
	}


	@Override
	public boolean remove(@NonNull Object object) {
		RecordingItem item = (RecordingItem)object;
		mDatabase.removeItemWithId(item.getId());
		return super.remove(object);
	}

	@Override
	public void onDatabaseEntryUpdated() {
		notifyDataSetChanged();
	}
}
