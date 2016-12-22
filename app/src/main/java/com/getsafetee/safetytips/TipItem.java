package com.getsafetee.safetytips;

import android.os.Parcel;
import android.os.Parcelable;

public class TipItem implements Parcelable {
    private int mId;
    private long mTime;
    private String mName;
    private String mUniqueid;
    private String mBody;

    public TipItem() {

    }

    public TipItem(Parcel in) {
        mId = in.readInt();
        mTime = in.readLong();
        mName = in.readString();
        mUniqueid = in.readString();
        mBody = in.readString();
    }


    public String getUniqueid() {
        return mUniqueid;
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

    public static final Creator<TipItem> CREATOR = new Creator<TipItem>() {
        public TipItem createFromParcel(Parcel in) {
            return new TipItem(in);
        }

        public TipItem[] newArray(int size) {
            return new TipItem[size];
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
