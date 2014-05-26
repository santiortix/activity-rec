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
	CHANGE_SETTINGS
}
