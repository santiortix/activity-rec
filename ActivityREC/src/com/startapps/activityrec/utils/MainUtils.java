package com.startapps.activityrec.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.startapps.activityrec.R;

public class MainUtils
{
	
	private static MainUtils instance;
	private Activity mainActivity;
	
	private MainUtils()
	{
		// NTD
	}
	
	public static MainUtils getInstance()
	{
		if (instance == null)
		{
			instance = new MainUtils();
		}
		return instance;
	}
	
	public void setActivity(final Activity activity)
	{
		mainActivity = activity;
	}
	
	public final void showToast(int messageId)
	{
		if (mainActivity != null)
		{
			Context context = mainActivity.getApplicationContext();
			String text = mainActivity.getResources().getString(messageId);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}
	
	public final void showToastLong(int messageId)
	{
		if (mainActivity != null)
		{
			Context context = mainActivity.getApplicationContext();
			String text = mainActivity.getResources().getString(messageId);
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		}
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
	
	public final void showErrorDialog(final CharSequence errTitle, final CharSequence errMsg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setMessage(errMsg).setTitle(errTitle);
		AlertDialog errDiag = builder.create();
		errDiag.show();
	}
	
	public boolean appInstalledOrNot(final String uri)
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
}
