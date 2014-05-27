package com.startapps.activityrec.types;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;

public abstract class AbstractRecord implements Serializable, Comparable<AbstractRecord>
{
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 5971678156404276956L;
	
	private Date timestamp;
	private int messageIdx;
	private RecordType type;
	
	public AbstractRecord(Date timestamp, int messageIdx, RecordType type) {
		super();
		this.timestamp = timestamp;
		this.messageIdx = messageIdx;
		this.type = type;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public int getMessageIdx() {
		return messageIdx;
	}

	public RecordType getType() {
		return type;
	}

	public int compareTo(AbstractRecord o)
	{
		return o.getTimestamp().compareTo(getTimestamp());
	}
	
	public abstract String getString(Context ctx);

}
