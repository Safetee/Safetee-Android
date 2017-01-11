package com.safeteeapp.audiorecorder.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.safeteeapp.safetee.R;


public class AboutFragment extends Fragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_about, container, false);

		v.findViewById(R.id.gplus).setOnClickListener(this);
		v.findViewById(R.id.github_button).setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = null;
		switch (v.getId()) {
		case R.id.github_button:
			uri = Uri.parse("https://github.com/Safetee/Safetee-Android.git");
			break;
		case R.id.gplus:
			uri = Uri.parse("https://github.com/Safetee/Safetee-Android.git");
			break;
		}

		if (uri == null)
			return;

		intent.setData(uri);
		startActivity(intent);
	}
}
