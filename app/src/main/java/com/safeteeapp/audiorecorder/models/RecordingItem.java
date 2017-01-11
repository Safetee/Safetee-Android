package com.safeteeapp.audiorecorder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingItem implements Parcelable {
	private int mId;
	private int mLength;
	private long mTime;
	private String mFilePath;
	private String mName;
	private String mUniqueid;
	private String mShared;
	private String mUser;
	private String mLocation;

	public RecordingItem() {

	}

	public RecordingItem(Parcel in) {
		mId = in.readInt();
		mLength = in.readInt();
		mTime = in.readLong();
		mFilePath = in.readString();
		mName = in.readString();
		mUniqueid = in.readString();
		mShared = in.readString();
		mUser = in.readString();
		mLocation = in.readString();
	}

	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String filePath) {
		mFilePath = filePath;
	}

	public String getUniqueid() {
		return mUniqueid;
	}

	public void setUniqueid(String uniqueid) {
		mUniqueid = uniqueid;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String location) {
		mLocation = location;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public String getShared() {
		return mShared;
	}

	public void setShared(String shared) {
		mShared = shared;
	}

	public int getLength() {
		return mLength;
	}

	public void setLength(int length) {
		mLength = length;
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

	public static final Creator<RecordingItem> CREATOR = new Creator<RecordingItem>() {
		public RecordingItem createFromParcel(Parcel in) {
			return new RecordingItem(in); 
		}

		public RecordingItem[] newArray(int size) {
			return new RecordingItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeInt(mLength);
		dest.writeLong(mTime);
		dest.writeString(mFilePath);
		dest.writeString(mName);
		dest.writeString(mUniqueid);
		dest.writeString(mShared);
		dest.writeString(mLocation);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
