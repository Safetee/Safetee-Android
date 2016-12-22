package com.getsafetee.audiorecorder.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Comparator;
import java.util.UUID;

public class RecordingsDatabase extends SQLiteOpenHelper {
	private Context mContext;

	public static final String DATABASE_NAME = "safeteerecords.db";
	private static final int DATABASE_VERSION = 1;

	public static abstract class RecordingDatabaseItem implements BaseColumns {
		public static final String TABLE_NAME = "recordsfinal";

		public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
		public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
		public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
		public static final String COLUMN_NAME_TIME_ADDED = "time_added";
		public static final String COLUMN_NAME_UNIQUE_ID = "unique_id";
		public static final String COLUMN_SHARED = "shared";
		public static final String COLUMN_LOCATION = "location";
	}

	public interface OnDatabaseChangedListener {
		void onDatabaseEntryUpdated();
	}

	private OnDatabaseChangedListener mOnDatabaseChangedListener;

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + RecordingDatabaseItem.TABLE_NAME + " (" +
					RecordingDatabaseItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_UNIQUE_ID + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_SHARED + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";

	@SuppressWarnings("unused")
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RecordingDatabaseItem.TABLE_NAME;

	public RecordingsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	public long addRecording(String recordingName, String filePath, long length, String uniqueid, String shared, String location) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
		values.put(RecordingDatabaseItem.COLUMN_NAME_UNIQUE_ID, uniqueid);
		values.put(RecordingDatabaseItem.COLUMN_SHARED, shared);
		values.put(RecordingDatabaseItem.COLUMN_LOCATION, location);
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH, length);
		values.put(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());

		long rowId = db.insert(RecordingDatabaseItem.TABLE_NAME, null, values);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();

		return rowId;
	}

	public RecordingItem getItemAt(int position) {
		SQLiteDatabase db = getReadableDatabase();

		String[] projection = {
				RecordingDatabaseItem._ID,
				RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME,
				RecordingDatabaseItem.COLUMN_NAME_UNIQUE_ID,
				RecordingDatabaseItem.COLUMN_SHARED,
				RecordingDatabaseItem.COLUMN_LOCATION,
				RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH,
				RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH,
				RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED
		};

		Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection, null, null, null, null, null, null);
		if (c.moveToPosition(position)) {
			RecordingItem item = new RecordingItem();
			item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
			item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
			item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
			item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
			item.setUniqueid(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_UNIQUE_ID)));
			item.setShared(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_SHARED)));
			item.setLocation(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_LOCATION)));
			item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
			c.close();
			return item;
		}

		return null;
	}

	public void removeItemWithId(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { String.valueOf(id) };
		db.delete(RecordingDatabaseItem.TABLE_NAME, "_id=?", whereArgs);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
	}

	public int getCount() {
		SQLiteDatabase db = getReadableDatabase();
		String[] projection = { RecordingDatabaseItem._ID };
		Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection, null, null, null, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade(db, oldVersion, newVersion);
	}

	public Context getContext() {
		return mContext;
	}

	public class RecordingComparator implements Comparator<RecordingItem> {
		public int compare(RecordingItem item1, RecordingItem item2) {
			Long o1 = item1.getTime();
			Long o2 = item2.getTime();
			return o2.compareTo(o1);
		}
	}


	public void renameItem(RecordingItem item, String recordingName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
		db.update(RecordingDatabaseItem.TABLE_NAME, values,
				RecordingDatabaseItem._ID + "=" + item.getId(), null);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
	}

	public void renameItemByID(String id, String recordingName) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
		db.update(RecordingDatabaseItem.TABLE_NAME, values,
				RecordingDatabaseItem._ID + "=" + id, null);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
	}

	public void setItemShared(String id, String shared) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(RecordingDatabaseItem.COLUMN_SHARED, shared);
		db.update(RecordingDatabaseItem.TABLE_NAME, values,
				RecordingDatabaseItem._ID + "=" + id, null);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
	}

	public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
		mOnDatabaseChangedListener = listener;
	}

	public long restoreRecording(RecordingItem item) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, item.getName());
		values.put(RecordingDatabaseItem.COLUMN_NAME_UNIQUE_ID, item.getUniqueid());
		values.put(RecordingDatabaseItem.COLUMN_SHARED, item.getShared());
		values.put(RecordingDatabaseItem.COLUMN_LOCATION, item.getLocation());
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, item.getFilePath());
		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH, item.getLength());
		values.put(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED, item.getTime());
		values.put(RecordingDatabaseItem._ID, item.getId());

		long rowId = db.insert(RecordingDatabaseItem.TABLE_NAME, null, values);

		if (mOnDatabaseChangedListener != null)
			mOnDatabaseChangedListener.onDatabaseEntryUpdated();

		return rowId;
	}
}
