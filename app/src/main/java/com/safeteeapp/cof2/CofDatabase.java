package com.safeteeapp.cof2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.safeteeapp.SessionManager;

import java.util.Comparator;


public class CofDatabase extends SQLiteOpenHelper {
    private Context mContext;
    private SessionManager session;

    public static final String DATABASE_NAME = "safeteecof.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class CofDatabaseItem implements BaseColumns {
        public static final String TABLE_NAME = "coffinal";

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
            "CREATE TABLE " + CofDatabaseItem.TABLE_NAME + " (" +
                    CofDatabaseItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    CofDatabaseItem.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    CofDatabaseItem.COLUMN_UNIQUE_ID + TEXT_TYPE + COMMA_SEP +
                    CofDatabaseItem.COLUMN_BY + TEXT_TYPE + COMMA_SEP +
                    CofDatabaseItem.COLUMN_BODY + TEXT_TYPE + COMMA_SEP +
                    CofDatabaseItem.COLUMN_TIME_ADDED + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CofDatabaseItem.TABLE_NAME;

    public CofDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        session = new SessionManager(context);
    }

    public long addCof(String title, String body, String uniqueid, String by) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CofDatabaseItem.COLUMN_TITLE, title);
        values.put(CofDatabaseItem.COLUMN_UNIQUE_ID, uniqueid);
        values.put(CofDatabaseItem.COLUMN_BY, by);
        values.put(CofDatabaseItem.COLUMN_BODY, body);
        values.put(CofDatabaseItem.COLUMN_TIME_ADDED, System.currentTimeMillis());

        long rowId = db.insert(CofDatabaseItem.TABLE_NAME, null, values);

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryUpdated();

        return rowId;
    }

    public int findCof(String uniqueid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { CofDatabaseItem.COLUMN_UNIQUE_ID };
        Cursor c = db.query(CofDatabaseItem.TABLE_NAME, projection, CofDatabaseItem.COLUMN_UNIQUE_ID + "=" + '"' + uniqueid + '"', null, null, null, null, "1");
        int count = c.getCount();
        c.close();
        return count;
    }

    public CofItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                CofDatabaseItem._ID,
                CofDatabaseItem.COLUMN_TITLE,
                CofDatabaseItem.COLUMN_UNIQUE_ID,
                CofDatabaseItem.COLUMN_BY,
                CofDatabaseItem.COLUMN_BODY,
                CofDatabaseItem.COLUMN_TIME_ADDED
        };

        Cursor c = db.query(CofDatabaseItem.TABLE_NAME, projection, CofDatabaseItem.COLUMN_BY + "=" + '"' + session.getUid() + '"', null, null, null, CofDatabaseItem._ID + " DESC", null);
        if (c.moveToPosition(position)) {
            CofItem item = new CofItem();
            item.setId(c.getInt(c.getColumnIndex(CofDatabaseItem._ID)));
            item.setName(c.getString(c.getColumnIndex(CofDatabaseItem.COLUMN_TITLE)));
            item.setUniqueid(c.getString(c.getColumnIndex(CofDatabaseItem.COLUMN_UNIQUE_ID)));
            item.setBy(c.getString(c.getColumnIndex(CofDatabaseItem.COLUMN_BY)));
            item.setBody(c.getString(c.getColumnIndex(CofDatabaseItem.COLUMN_BODY)));
            item.setTime(c.getLong(c.getColumnIndex(CofDatabaseItem.COLUMN_TIME_ADDED)));
            c.close();
            return item;
        }

        return null;
    }

    public String getCofs(int position) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                CofDatabaseItem._ID,
                CofDatabaseItem.COLUMN_TITLE,
                CofDatabaseItem.COLUMN_UNIQUE_ID,
                CofDatabaseItem.COLUMN_BY,
                CofDatabaseItem.COLUMN_BODY,
                CofDatabaseItem.COLUMN_TIME_ADDED
        };

        Cursor c = db.query(CofDatabaseItem.TABLE_NAME, projection, CofDatabaseItem.COLUMN_BY + "=" + '"' + session.getUid() + '"', null, null, null, CofDatabaseItem._ID + " DESC", null);
        if (c.moveToPosition(position)) {
            String cof = c.getString(c.getColumnIndex(CofDatabaseItem.COLUMN_BODY));
            c.close();
            return cof;
        }

        return null;
    }


    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(CofDatabaseItem.TABLE_NAME, "_id=?", whereArgs);

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryUpdated();
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { CofDatabaseItem._ID };
        Cursor c = db.query(CofDatabaseItem.TABLE_NAME, projection, CofDatabaseItem.COLUMN_BY + "=" + '"' + session.getUid() + '"', null, null, null, null, null);
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

    public class CofComparator implements Comparator<CofItem> {
        public int compare(CofItem item1, CofItem item2) {
            Long o1 = item1.getTime();
            Long o2 = item2.getTime();
            return o2.compareTo(o1);
        }
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CofDatabaseItem.TABLE_NAME);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.close();
    }



    public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

}

