package com.startapps.activityrec.types;

public class PreferencesAppInformation
{
	private String appName;
	private String appPackage;
	
	public PreferencesAppInformation(final String name, final String pckg)
	{
		appName = name;
		appPackage = pckg;
	}

	public String getAppName()
	{
		return appName;
	}

	public String getAppPackage()
	{
		return appPackage;
	}
	
}
