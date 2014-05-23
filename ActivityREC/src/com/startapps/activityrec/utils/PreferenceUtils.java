package com.startapps.activityrec.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.startapps.activityrec.R;

public class PreferenceUtils
{
	private static PreferenceUtils instance;
	private Activity mainActivity;
	
	private PreferenceUtils()
	{
		// NTD
	}
	
	public static PreferenceUtils getInstance()
	{
		if (instance == null)
		{
			instance = new PreferenceUtils();
		}
		return instance;
	}
	
	public void setActivity(final Activity activity)
	{
		mainActivity = activity;
	}
	
	public final boolean checkPrefsMonitoringActive()
	{
		SharedPreferences prefs = mainActivity.getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean(mainActivity.getString(R.string.key_monitoring_active), false);
	}
	
	public final boolean checkPrefsPasswordEnabled()
	{
		SharedPreferences prefs = mainActivity.getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean(mainActivity.getString(R.string.key_password_set), false);
	}
	
	public final void disablePrefsActivityRegistry()
	{
		SharedPreferences prefs = mainActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(mainActivity.getString(R.string.key_monitoring_active), false);
		editor.putBoolean(mainActivity.getString(R.string.key_password_set), false);
		editor.remove(mainActivity.getString(R.string.key_password));
		editor.commit();
	}
	
	public final void enablePrefsActivityRegistry(final String pwd)
	{
		SharedPreferences prefs = mainActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(mainActivity.getString(R.string.key_monitoring_active), true);
		editor.putBoolean(mainActivity.getString(R.string.key_password_set), true);
		editor.putString(mainActivity.getString(R.string.key_password), pwd);
		editor.commit();
	}
	
	public final String getPrefsMasterPassword()
	{
		SharedPreferences prefs = mainActivity.getPreferences(Context.MODE_PRIVATE);
		return prefs.getString(mainActivity.getString(R.string.key_password), "-1");
	}
}
