package com.startapps.activityrec.types;

import java.util.Date;

import com.startapps.activityrec.utils.MainUtils;

import android.content.Context;

public class SmsRecord extends AbstractRecord
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -7671018975177856257L;
	
	private String contact;
	
	public SmsRecord(Date timestamp, int messageIdx, RecordType type, String contact) {
		super(timestamp, messageIdx, type);
		this.contact = contact;
	}
	
	public String getContact()
	{
		return contact;
	}

	@Override
	public String getString(Context ctx)
	{
		String msg =  "[" + MainUtils.getInstance().formatDate(getTimestamp()) + "] - " + ctx.getString(getMessageIdx());
		msg += " " + contact;
		return msg;
	}

	
}
