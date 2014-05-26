package com.startapps.activityrec.types;

import java.io.Serializable;
import java.util.Date;

public class ActivityRecord implements Serializable, Comparable<ActivityRecord>
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4447536507771591352L;
	
	private Date timestamp;
	private int messageIdx;
	private RecordType type;
	
	public ActivityRecord(Date timestamp, int messageIdx, RecordType type) {
		super();
		this.timestamp = timestamp;
		this.messageIdx = messageIdx;
		this.type = type;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public int getMessageIdx() {
		return messageIdx;
	}
	public void setMessageIdx(int messageIdx) {
		this.messageIdx = messageIdx;
	}
	public RecordType getType() {
		return type;
	}
	public void setType(RecordType type) {
		this.type = type;
	}
	public int compareTo(ActivityRecord o)
	{
		return o.getTimestamp().compareTo(getTimestamp());
	}
	
}
