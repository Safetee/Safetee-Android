package com.safeteeapp.cof2;

import android.os.Parcel;
import android.os.Parcelable;

public class CofItem implements Parcelable {
    private int mId;
    private long mTime;
    private String mName;
    private String mUniqueid;
    private String mBody;
    private String mBy;

    public CofItem() {

    }

    public CofItem(Parcel in) {
        mId = in.readInt();
        mTime = in.readLong();
        mName = in.readString();
        mUniqueid = in.readString();
        mBody = in.readString();
        mBy = in.readString();
    }


    public String getUniqueid() {
        return mUniqueid;
    }

    public void setBy(String by) {
        mBy = by;
    }

    public String getBy() {
        return mBy;
    }

    public void setUniqueid(String uniqueid) {
        mUniqueid = uniqueid;
    }


    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public static final Creator<CofItem> CREATOR = new Creator<CofItem>() {
        public CofItem createFromParcel(Parcel in) {
            return new CofItem(in);
        }

        public CofItem[] newArray(int size) {
            return new CofItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeLong(mTime);
        dest.writeString(mName);
        dest.writeString(mUniqueid);
        dest.writeString(mBody);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}