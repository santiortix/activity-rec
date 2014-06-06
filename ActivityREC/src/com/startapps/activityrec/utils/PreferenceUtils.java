package com.startapps.activityrec.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.startapps.activityrec.R;
import com.startapps.activityrec.types.PreferencesAppInformation;

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
		SharedPreferences prefs = getPrefs();
		return prefs.getBoolean(mainActivity.getString(R.string.key_monitoring_active), false);
	}
	
	public final boolean checkPrefsPasswordEnabled()
	{
		SharedPreferences prefs = getPrefs();
		return prefs.getBoolean(mainActivity.getString(R.string.key_password_set), false);
	}
	
	public final void disablePrefsActivityRegistry()
	{
		SharedPreferences prefs = getPrefs();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(mainActivity.getString(R.string.key_monitoring_active), false);
		editor.putBoolean(mainActivity.getString(R.string.key_password_set), false);
		editor.remove(mainActivity.getString(R.string.key_password));
		editor.commit();
	}
	
	public final void enablePrefsActivityRegistry(final String pwd)
	{
		SharedPreferences prefs = getPrefs();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(mainActivity.getString(R.string.key_monitoring_active), true);
		editor.putBoolean(mainActivity.getString(R.string.key_password_set), true);
		editor.putString(mainActivity.getString(R.string.key_password), pwd);
		editor.commit();
	}
	
	public final String getPrefsMasterPassword()
	{
		SharedPreferences prefs = getPrefs();
		return prefs.getString(mainActivity.getString(R.string.key_password), "-1");
	}
	
	public final List<PreferencesAppInformation> filterInstalledApps(final String[] appsNames, final String[] appsValues)
	{
		List<PreferencesAppInformation> res = new ArrayList<PreferencesAppInformation>();
		for (int i=0; i<appsValues.length; i++)
		{
			if (appInstalledOrNot(appsValues[i]))
			{
				res.add(new PreferencesAppInformation(appsNames[i], appsValues[i]));
			}
		}
		return res;
	}
	
	private boolean appInstalledOrNot(String uri)
	{
        PackageManager pm = mainActivity.getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }
	
	private SharedPreferences getPrefs()
	{
		return PreferenceManager.getDefaultSharedPreferences(mainActivity);
	}
}
