package com.startapps.activityrec.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import android.content.Context;

import com.startapps.activityrec.R;

public class PhonecallsLog
{
	private static PhonecallsLog inst;
	private StorageUtils su;
	
	
	private PhonecallsLog()
	{
		su = new StorageUtils();
	}
	
	public static PhonecallsLog getInstance()
	{
		if (inst == null)
		{
			inst = new PhonecallsLog();
		}
		return inst;
	}
	
	public void registerIncomingCall(final Context ctx, final String contact, final Date start, final Date end) throws IOException
	{
		long duration = (end.getTime() - start.getTime()) / 1000;
		
		String message = "[" + MainUtils.getInstance().formatDate(start) + "] - " + ctx.getString(R.string.log_act_incoming_call);
		message += " " + contact + " - (" + duration + " sec.)"; 
		
		File f = su.getPhonecallsLogFile(ctx);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f,true)));		
		pw.println(message);
		pw.close();
	}
	
	public void registerOutgoingCall(final Context ctx, final String contact, final Date start, final Date end) throws IOException
	{
		long duration = (end.getTime() - start.getTime()) / 1000;
		
		String message = "[" + MainUtils.getInstance().formatDate(start) + "] - " + ctx.getString(R.string.log_act_outgoing_call);
		message += " " + contact + " - (" + duration + " sec.)"; 
		
		File f = su.getPhonecallsLogFile(ctx);
		PrintWriter pw = new PrintWriter(f);		
		pw.println(message);
		pw.close();
	}
	
	public void registerMissedCall(final Context ctx, final String contact, final Date start) throws IOException
	{
		String message = "[" + MainUtils.getInstance().formatDate(start) + "] - " + ctx.getString(R.string.log_act_missed_call);
		message += " " + contact; 
		
		File f = su.getPhonecallsLogFile(ctx);
		PrintWriter pw = new PrintWriter(f);		
		pw.println(message);
		pw.close();
	}
}
