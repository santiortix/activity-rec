package com.startapps.activityrec.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.startapps.activityrec.R;
import com.startapps.activityrec.preference.MultiSelectListPreference;
import com.startapps.activityrec.types.PreferencesAppInformation;
import com.startapps.activityrec.utils.MainUtils;
import com.startapps.activityrec.utils.PreferenceUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener/*OnSharedPreferenceChangeListener*/
{
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private String mOrigSummaryText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		//setupSimplePreferencesScreen();
		setupPreferencesScreen();
		
		final MultiSelectListPreference appsPref = (MultiSelectListPreference) findPreference(getText(R.string.key_applist));
		appsPref.setOnPreferenceChangeListener(this);
		String[] allValues = getResources().getStringArray(R.array.pref_applist_values);
        String[] allNames = getResources().getStringArray(R.array.pref_applist_titles);
        
        // Aqui est� el diablo...
        // De momento no sabemos como deshabilitar aquellas apps que no est�n instaladas en el telefono
        // asi que lo unico que se puede hacer es no mostrarlas... :(
        /*List<PreferencesAppInformation> filtered = PreferenceUtils.getInstance().filterInstalledApps(allNames, allValues);
        appsPref.setEntries(getAppsListNames(filtered));
        appsPref.setEntryValues(getAppsListValues(filtered));*/
		
		mOrigSummaryText = appsPref.getSummary().toString();		
		appsPref.setSummary(MainUtils.getInstance().makeSummaryText(mOrigSummaryText, appsPref.getValues(), allValues, allNames));
	}
	
	private void setupPreferencesScreen()
	{
		//getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralSettingsFragment()).commit();
		addPreferencesFromResource(R.xml.pref_general);
		
		addPreferencesFromResource(R.xml.pref_applist);
		
		//Map<String,String> appsSupported = getAppsSupported();
		
		bindPrefSummaryToValue(findPreference(getText(R.string.key_email_addr)));
		CheckBoxPreference mon_act = (CheckBoxPreference) findPreference(getText(R.string.key_monitoring_active));
		mon_act.setEnabled(false);
		mon_act.setChecked(PreferenceUtils.getInstance().checkPrefsMonitoringActive());
	}
	
	private String[] getAppsListNames(List<PreferencesAppInformation> appsList)
	{
		String[] a = new String[appsList.size()];
		
		for (int i=0; i<appsList.size(); i++)
		{
			PreferencesAppInformation appInf = appsList.get(i);
			a[i] = appInf.getAppName();
		}		
		
		return a; 
	}
	
	private String[] getAppsListValues(List<PreferencesAppInformation> appsList)
	{
		String[] a = new String[appsList.size()];
		
		for (int i=0; i<appsList.size(); i++)
		{
			PreferencesAppInformation appInf = appsList.get(i);
			a[i] = appInf.getAppPackage();
		}		
		
		return a; 
	}

	/*
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	/*private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);

		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_data_sync);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("example_text"));
		bindPreferenceSummaryToValue(findPreference("example_list"));
		bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
	}*/

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener bindPrefSummaryToValueListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference pref, Object value)
		{
			String stringValue = value.toString();

			if (pref instanceof ListPreference)
			{
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) pref;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				pref.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

			}
			else
			{
				// For all other preferences, set the summary to the value's
				// simple string representation.
				pref.setSummary(stringValue);
			}
			return true;
		}
	};
	
	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPrefSummaryToValue(Preference pref)
	{
		// Set the listener to watch for value changes.
		pref.setOnPreferenceChangeListener(bindPrefSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		bindPrefSummaryToValueListener.onPreferenceChange(pref,
				PreferenceManager.getDefaultSharedPreferences(pref.getContext()).getString(pref.getKey(),""));
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		final String key = preference.getKey();
        if (key.equals(getString(R.string.key_applist)))
        {        	
            final MultiSelectListPreference multiselpref = (MultiSelectListPreference) preference;
            String[] allValues = getResources().getStringArray(R.array.pref_applist_values);
            String[] allNames = getResources().getStringArray(R.array.pref_applist_titles);

            multiselpref.setSummary(MainUtils.getInstance().makeSummaryText(mOrigSummaryText, (Set<String>) newValue, allValues, allNames));

            return true;
        }
        else
        {
            return false;
        }
	}
	
	/*private static String makeSummaryText(String baseText, Set<String> values, String[] allValues, String[] allNames)
	{
		String[] names = new String[values.size()];
		int i=0;
		for (String value : values)
		{
			names[i] = MainUtils.getInstance().getArrayTitleForValue(value, allValues, allNames);
			i++;
		}
		return baseText + " " + MainUtils.getInstance().sortedToString(names);
    }*/
	
	/*private Map<String,String> getAppsSupported()
	{
		Map<String,String> appsSupported = new HashMap<String,String>();
		Resources resources = this.getResources();		
		// Read from the /res/raw directory
		try
		{
		    InputStream rawResource = resources.openRawResource(R.raw.supported_apps);
		    Properties properties = new Properties();
		    properties.load(rawResource);
		    
		    for (Entry<Object,Object> entry : properties.entrySet())
		    {
		    	appsSupported.put(entry.getKey().toString(), entry.getValue().toString());
		    }
		}
		catch (NotFoundException e)
		{
		    System.err.println("Did not find raw resource: "+e);
		}
		catch (IOException e)
		{
		    System.err.println("Failed to open microlog property file");
		}
		
		return appsSupported;
	}*/
	
	/* FRAGMENTO DE SETTINGS GENERAL */
	/* NO PODEMOS USARLO POR COMPATIBILIDAD CON 2.3.3
	public static class GeneralSettingsFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			bindPrefSummaryToValue(findPreference(getText(R.string.key_email_addr)));
			CheckBoxPreference mon_act = (CheckBoxPreference) findPreference(getText(R.string.key_monitoring_active));
			mon_act.setEnabled(false);
			mon_act.setChecked(MainUtils.getInstance().checkPrefsMonitoringActive());
		}
	}*/
}
