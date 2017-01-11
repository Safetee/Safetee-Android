package com.safeteeapp.audiorecorder.listeners;

import com.safeteeapp.audiorecorder.models.RecordingMode;

public interface OnRecordingStateChangedListener {
	public void onRecordingStateChanged(RecordingMode mode);
}
