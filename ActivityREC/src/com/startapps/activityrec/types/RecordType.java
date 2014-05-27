package com.startapps.activityrec.types;

import java.io.Serializable;

public enum RecordType implements Serializable
{	
	/** Activation record */
	ACTIVATION,
	/** Deactivation record */
	DEACTIVATION,
	/** Access settings page */
	VIEW_SETTINGS,
	/** Change settings page */
	CHANGE_SETTINGS,
	/** Incoming call */
	INCOMING_CALL,
	/** Outgoing call */
	OUTGOING_CALL,
	/** Missed call */
	MISSED_CALL, 
	/** Incoming SMS */
	INCOMING_SMS
}
