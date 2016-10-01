package com.getsafetee.safetee.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import com.getsafetee.safetee.R;
import com.getsafetee.safetee.fragments.AboutFragment;
import com.getsafetee.safetee.fragments.SettingsFragment;

import java.util.HashMap;

/* 
 * This class is a generic activity that holds fragments that need
 * no additional coding from the Activity, examples of these are the
 * settings fragments and about fragments.
 */

public class FragmentHolderActivity extends AppCompatActivity {
	private static final HashMap<ActivityType, Class<?>> mActivityMap = new HashMap<ActivityType, Class<?>>();
	private static final HashMap<Class<?>, Integer> mTitleMap = new HashMap<Class<?>, Integer>();
	public enum ActivityType {
		SETTINGS, ABOUT
	}
	static {
		mActivityMap.put(ActivityType.SETTINGS, SettingsFragment.class);
		mActivityMap.put(ActivityType.ABOUT, AboutFragment.class);
		mTitleMap.put(SettingsFragment.class, R.string.settings);
		mTitleMap.put(AboutFragment.class, R.string.about);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_holder);
		
		int color = getIntent().getIntExtra("color", -999);
		if (color != -999) {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
		}

		ActivityType type = (ActivityType) getIntent().getSerializableExtra("activity_type");
		if (type == null) {
			throw new RuntimeException("You must pass an activity_to_launch extra");
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Class<?> fragmentClass = mActivityMap.get(type);
		Fragment fragment;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		getSupportActionBar().setTitle(mTitleMap.get(fragmentClass));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public static void startActivity(Activity parentActivity, ActivityType type, Bundle extras) {
		Intent intent = new Intent(parentActivity, FragmentHolderActivity.class);
		intent.putExtra("activity_type", type);
		if (extras != null)
			intent.putExtras(extras);
		parentActivity.startActivity(intent);
	}

	public static void startActivityForResult(Activity parentActivity, ActivityType type, int requestCode, Bundle extras) {
		Intent intent = new Intent(parentActivity, FragmentHolderActivity.class);
		intent.putExtra("activity_type", type);
		if (extras != null)
			intent.putExtras(extras);
		parentActivity.startActivityForResult(intent, requestCode);
	}
	
	public static final Bundle getBundleOfColor(int color) {
		Bundle bundle = new Bundle();
		bundle.putInt("color", color);
		return bundle;
	}
}
