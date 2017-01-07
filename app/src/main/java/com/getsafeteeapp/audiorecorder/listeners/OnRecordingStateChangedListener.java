package com.getsafeteeapp.audiorecorder.listeners;

import com.getsafeteeapp.audiorecorder.models.RecordingMode;

public interface OnRecordingStateChangedListener {
	public void onRecordingStateChanged(RecordingMode mode);
}
