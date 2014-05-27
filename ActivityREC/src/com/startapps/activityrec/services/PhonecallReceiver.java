package com.startapps.activityrec.services;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public abstract class PhonecallReceiver extends BroadcastReceiver
{

	// The receiver will be recreated whenever android feels like it. We need a
	// static variable to remember data between instantiations
	static PhonecallStartEndDetector listener;
	String outgoingSavedNumber;
	protected Context savedContext;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		savedContext = context;
		if (listener == null)
		{
			listener = new PhonecallStartEndDetector();
		}

		// We listen to two intents. The new outgoing call only tells us of an
		// outgoing call. We use it to get the number.
		if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
		{
			listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
			return;
		}
		else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
		{
			//final SmsManager smsManager = SmsManager.getDefault();
			final Bundle extras = intent.getExtras();
			if (extras != null)
			{
				final Object[] pdusObj = (Object[]) extras.get("pdus");
				for (int i=0; i<pdusObj.length; i++)
				{
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					Date recvTime = new Date();
					onSMSReceived(savedContext, phoneNumber, recvTime);
				}
			}
		}

		// The other intent tells us the phone state changed. Here we set a
		// listener to deal with it
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	// Derived classes should override these to respond to specific events of
	// interest
	protected abstract void onIncomingCallStarted(Context ctx, String number, Date start);
	protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);
	protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);
	protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);
	protected abstract void onMissedCall(Context ctx, String number, Date start);
	protected abstract void onSMSReceived(Context ctx, String number, Date recvTime);

	// Deals with actual events
	public class PhonecallStartEndDetector extends PhoneStateListener
	{
		int lastState = TelephonyManager.CALL_STATE_IDLE;
		Date callStartTime;
		boolean isIncoming;
		String savedNumber; // because the passed incoming is only valid in
							// ringing

		public PhonecallStartEndDetector()
		{}

		// The outgoing number is only sent via a separate intent, so we need to
		// store it out of band
		public void setOutgoingNumber(String number)
		{
			savedNumber = number;
		}

		// Incoming call- goes from IDLE to RINGING when it rings, to OFFHOOK
		// when it's answered, to IDLE when its hung up
		// Outgoing call- goes from IDLE to OFFHOOK when it dials out, to IDLE
		// when hung up
		@Override
		public void onCallStateChanged(int state, String incomingNumber) 
		{
			super.onCallStateChanged(state, incomingNumber);
			if (lastState == state)
			{
				// No change, debounce extras
				return;
			}
			switch (state)
			{
			case TelephonyManager.CALL_STATE_RINGING:
				isIncoming = true;
				callStartTime = new Date();
				savedNumber = incomingNumber;
				onIncomingCallStarted(savedContext, incomingNumber, callStartTime);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// Transition of ringing->offhook are pickups of incoming calls.
				// Nothing donw on them
				if (lastState != TelephonyManager.CALL_STATE_RINGING)
				{
					isIncoming = false;
					callStartTime = new Date();
					onOutgoingCallStarted(savedContext, savedNumber, callStartTime);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// Went to idle- this is the end of a call. What type depends on
				// previous state(s)
				if (lastState == TelephonyManager.CALL_STATE_RINGING)
				{
					// Ring but no pickup- a miss
					onMissedCall(savedContext, savedNumber, callStartTime);
				}
				else if (isIncoming)
				{
					onIncomingCallEnded(savedContext, savedNumber, callStartTime, new Date());
				}
				else
				{
					onOutgoingCallEnded(savedContext, savedNumber, callStartTime, new Date());
				}
				break;
			}
			lastState = state;
		}

	}
}
