package com.startapps.activityrec.types;

import java.util.Date;

import android.content.Context;

import com.startapps.activityrec.utils.MainUtils;

public class ActivityRecord extends AbstractRecord
{
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4447536507771591352L;
	
	public ActivityRecord(Date timestamp, int messageIdx, RecordType type) {
		super(timestamp, messageIdx, type);
	}

	@Override
	public String getString(final Context ctx)
	{
		return "[" + MainUtils.getInstance().formatDate(getTimestamp()) + "] - " + ctx.getString(getMessageIdx());
	}
	
	
	
}
