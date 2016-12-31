package com.getsafetee.safetytips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


import java.util.Comparator;


public class TipsDatabase extends SQLiteOpenHelper {
    private Context mContext;

    public static final String DATABASE_NAME = "safeteetips.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class TipDatabaseItem implements BaseColumns {
        public static final String TABLE_NAME = "tipsfinal";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_TIME_ADDED = "time_added";
        public static final String COLUMN_UNIQUE_ID = "unique_id";
        public static final String COLUMN_BY = "by";
    }

    public interface OnDatabaseChangedListener {
        void onDatabaseEntryUpdated();
    }

    private OnDatabaseChangedListener mOnDatabaseChangedListener;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TipDatabaseItem.TABLE_NAME + " (" +
                    TipDatabaseItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    TipDatabaseItem.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    TipDatabaseItem.COLUMN_UNIQUE_ID + TEXT_TYPE + COMMA_SEP +
                    TipDatabaseItem.COLUMN_BY + TEXT_TYPE + COMMA_SEP +
                    TipDatabaseItem.COLUMN_BODY + TEXT_TYPE + COMMA_SEP +
                    TipDatabaseItem.COLUMN_TIME_ADDED + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TipDatabaseItem.TABLE_NAME;

    public TipsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public long addTip(String title, String body, String uniqueid, String by) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TipDatabaseItem.COLUMN_TITLE, title);
        values.put(TipDatabaseItem.COLUMN_UNIQUE_ID, uniqueid);
        values.put(TipDatabaseItem.COLUMN_BY, by);
        values.put(TipDatabaseItem.COLUMN_BODY, body);
        values.put(TipDatabaseItem.COLUMN_TIME_ADDED, System.currentTimeMillis());

        long rowId = db.insert(TipDatabaseItem.TABLE_NAME, null, values);

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryUpdated();

        return rowId;
    }

    public int findTip(String uniqueid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { TipDatabaseItem.COLUMN_UNIQUE_ID };
        Cursor c = db.query(TipDatabaseItem.TABLE_NAME, projection, TipDatabaseItem.COLUMN_UNIQUE_ID + "=" + '"' + uniqueid + '"', null, null, null, null, "1");
        int count = c.getCount();
        c.close();
        return count;
    }

    public TipItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                TipDatabaseItem._ID,
                TipDatabaseItem.COLUMN_TITLE,
                TipDatabaseItem.COLUMN_UNIQUE_ID,
                TipDatabaseItem.COLUMN_BY,
                TipDatabaseItem.COLUMN_BODY,
                TipDatabaseItem.COLUMN_TIME_ADDED
        };

        Cursor c = db.query(TipDatabaseItem.TABLE_NAME, projection, null, null, null, null, TipDatabaseItem._ID + " DESC", null);
        if (c.moveToPosition(position)) {
            TipItem item = new TipItem();
            item.setId(c.getInt(c.getColumnIndex(TipDatabaseItem._ID)));
            item.setName(c.getString(c.getColumnIndex(TipDatabaseItem.COLUMN_TITLE)));
            item.setUniqueid(c.getString(c.getColumnIndex(TipDatabaseItem.COLUMN_UNIQUE_ID)));
            item.setBy(c.getString(c.getColumnIndex(TipDatabaseItem.COLUMN_BY)));
            item.setBody(c.getString(c.getColumnIndex(TipDatabaseItem.COLUMN_BODY)));
            item.setTime(c.getLong(c.getColumnIndex(TipDatabaseItem.COLUMN_TIME_ADDED)));
            c.close();
            return item;
        }

        return null;
    }


    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { TipDatabaseItem._ID };
        Cursor c = db.query(TipDatabaseItem.TABLE_NAME, projection, null, null, null, null, null, null);
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

    public class TipComparator implements Comparator<TipItem> {
        public int compare(TipItem item1, TipItem item2) {
            Long o1 = item1.getTime();
            Long o2 = item2.getTime();
            return o2.compareTo(o1);
        }
    }



    public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

}

