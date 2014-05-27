package com.startapps.activityrec.types;

import java.util.Date;

import com.startapps.activityrec.utils.MainUtils;

import android.content.Context;

public class PhonecallRecord extends AbstractRecord
{
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1860435071466337718L;
	
	/** Phonecall duration (secs) */
	private long duration;
	/** Phonecall contact */
	private String contact;

	public PhonecallRecord(Date timestamp, int messageIdx, RecordType type, long duration, String contact)
	{
		super(timestamp, messageIdx, type);
		this.duration = duration;
		this.contact = contact;
	}

	public long getDuration() {
		return duration;
	}

	public String getContact() {
		return contact;
	}
	
	public String getString(final Context ctx)
	{
		String msg =  "[" + MainUtils.getInstance().formatDate(getTimestamp()) + "] - " + ctx.getString(getMessageIdx());
		msg += " " + contact + " (" + MainUtils.getInstance().seconds2Minutes(duration) + ")";
		return msg;
	}
}
