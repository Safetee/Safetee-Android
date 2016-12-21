package com.getsafetee.audiorecorder.listeners;

import com.getsafetee.audiorecorder.models.RecordingMode;

public interface OnRecordingStateChangedListener {
	public void onRecordingStateChanged(RecordingMode mode);
}
