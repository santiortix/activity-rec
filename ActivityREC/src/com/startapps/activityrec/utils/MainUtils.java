package com.startapps.activityrec.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class MainUtils
{
	
	private static MainUtils instance;
	private Activity mainActivity;
	private SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
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
	
	public final void showErrorDialog(final CharSequence errTitle, final CharSequence errMsg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setMessage(errMsg).setTitle(errTitle);
		AlertDialog errDiag = builder.create();
		errDiag.show();
	}
	
	public String makeSummaryText(String baseText, Set<String> values, String[] allValues, String[] allNames)
	{
		String[] names = new String[values.size()];
		int i=0;
		for (String value : values)
		{
			names[i] = getArrayTitleForValue(value, allValues, allNames);
			i++;
		}
		return baseText + " " + sortedToString(names);
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
	
	//public  String sortedToString(Set<String> values)
	private String sortedToString(String[] sorted)
	{
        // sort items
        /*String[] sorted = new String[values.size()];
        values.toArray(sorted);*/
        Arrays.sort(sorted);

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < sorted.length; i++)
        {
            if (i > 0)
            {
                builder.append(", ");
            }

            builder.append(sorted[i]);
        }
        builder.append("]");

        return builder.toString();
    }
	
	public String getArrayTitleForValue(final String value, final String[] allValues, final String[] allNames)
	{
		try
		{
			for (int i=0; i<allValues.length; i++)
			{
				if (value.equals(allValues[i]))
				{
					return allNames[i];
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("ArraiIndexOutOfBoundsException: " + e.getMessage());
		}
		return value;
	}
	
	public String formatDate(final Date d)
	{
		return SDF.format(d);
	}
	
	public String seconds2Minutes(final long seconds)
	{
		String res = "";
		final String zf = "%02d";
		if (seconds >= 60)
		{
			long minutes = seconds / 60;
			long restSeconds = seconds % 60;
			if (minutes >= 60)
			{
				long hours = minutes / 60;
				long restMinutes = minutes % 60;
				res = String.format(zf, hours) + ":" + String.format(zf, restMinutes);
			}
			else
			{
				res = String.format(zf, minutes);
			}
			res += ":" + String.format(zf, restSeconds);
		}
		else
		{
			res = "00:" + String.format(zf, seconds);
		}
			
		return res;
	}
	
}
