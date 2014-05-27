package com.startapps.activityrec.services;

import java.io.IOException;
import java.util.Date;

import android.content.Context;

import com.startapps.activityrec.R;
import com.startapps.activityrec.types.PhonecallRecord;
import com.startapps.activityrec.types.RecordType;
import com.startapps.activityrec.types.SmsRecord;
import com.startapps.activityrec.utils.ActivityLog;
import com.startapps.activityrec.utils.MainUtils;
import com.startapps.activityrec.utils.PhonecallsLog;

public class RegisterPhonecallsReceiver extends PhonecallReceiver
{
	private PhonecallsLog phonecallsLog;
	private ActivityLog activityLog;
	
	public RegisterPhonecallsReceiver()
	{
		phonecallsLog = PhonecallsLog.getInstance();
		activityLog = ActivityLog.getInstance();
	}

	@Override
	protected void onIncomingCallStarted(Context ctx, String number, Date start)
	{
		// nothing to do		
	}

	@Override
	protected void onOutgoingCallStarted(Context ctx, String number, Date start)
	{
		// nothing to do		
	}

	@Override
	protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
	{
		long duration = (end.getTime() - start.getTime()) / 1000;
		try
		{
			phonecallsLog.registerIncomingCall(ctx, number, start, end);
			activityLog.appendActivityRecord(ctx, new PhonecallRecord(start, R.string.log_act_incoming_call, RecordType.INCOMING_CALL, duration, number));
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
		catch (ClassNotFoundException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
	}

	@Override
	protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
	{
		long duration = (end.getTime() - start.getTime()) / 1000;
		try
		{
			phonecallsLog.registerOutgoingCall(ctx, number, start, end);
			activityLog.appendActivityRecord(ctx, new PhonecallRecord(start, R.string.log_act_outgoing_call, RecordType.OUTGOING_CALL, duration, number));
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
		catch (ClassNotFoundException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
	}

	@Override
	protected void onMissedCall(Context ctx, String number, Date start)
	{
		try
		{
			phonecallsLog.registerMissedCall(ctx, number, start);
			activityLog.appendActivityRecord(ctx, new PhonecallRecord(start, R.string.log_act_missed_call, RecordType.MISSED_CALL, 0, number));
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
		catch (ClassNotFoundException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
	}
	
	protected void onSMSReceived(Context ctx, String number, Date recvTime)
	{
		try
		{
			//phonecallsLog.registerMissedCall(ctx, number, start);
			activityLog.appendActivityRecord(ctx, new SmsRecord(recvTime, R.string.log_act_incoming_sms, RecordType.INCOMING_SMS, number));
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
		catch (ClassNotFoundException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
		}
	}

}
