package com.startapps.activityrec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.startapps.activityrec.activities.SettingsActivity;
import com.startapps.activityrec.activities.ShowRegistryActivity;
import com.startapps.activityrec.content.SharedPreferenceCompat;
import com.startapps.activityrec.dialogs.EnterPasswordDialog;
import com.startapps.activityrec.dialogs.EnterPasswordDialog.EnterPasswordAction;
import com.startapps.activityrec.dialogs.EnterPasswordDialog.EnterPasswordListener;
import com.startapps.activityrec.dialogs.SetPasswordDialog;
import com.startapps.activityrec.dialogs.SetPasswordDialog.SetPasswordListener;
import com.startapps.activityrec.utils.MainUtils;
import com.startapps.activityrec.utils.PreferenceUtils;

public class MainActivity extends ActionBarActivity /*FragmentActivity*/
	implements SetPasswordListener, EnterPasswordListener/*, SharedPreferences.OnSharedPreferenceChangeListener*/	
{
	
	public final static String EXTRA_MESSAGE = "com.startapps.activityrec.MESSAGE";
	
	private boolean statusActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		statusActive = prefs.getBoolean(getString(R.string.key_monitoring_active), false);
		/*SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(R.string., value)*/
		
		// Creamos la instancia de MainUtils y PreferenceUtils
		MainUtils.getInstance().setActivity(this);
		PreferenceUtils.getInstance().setActivity(this);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getSupportActionBar();
	        actionBar.setHomeButtonEnabled(false);
		}
	}
	
	//@Override
	public void onResume()
	{
		super.onResume();
		changeMainAppearance();
		//getPrefs().registerOnSharedPreferenceChangeListener(this);
	}
	
	/*@Override
    protected void onPause() {
        getPrefs().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
	
	private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {	    
	        /*case R.id.action_search:
	            openSearch();
	            return true;*/
	       case R.id.menu_overflow:
	            showDropDown(R.id.menu_overflow);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/*private void openSearch()
	{
		Context context = getApplicationContext();
		String text = getResources().getString(R.string.not_implemented);
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}*/
	
	private void showDropDown(int dropDownId)
	{
		View view = findViewById(dropDownId);
        PopupMenu popupMenu = new PopupMenu(getSupportActionBar().getThemedContext(), view);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener()
        {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId())
				{
				case R.id.menu_main_settings:
					openSettingsDialog();
				    break;
				case R.id.menu_main_about:
					showAboutDialog();
					break;
				}
				//MainUtils.getInstance().showToast(R.string.not_implemented);
				return false;
			}        	
        });
        popupMenu.getMenuInflater().inflate(R.menu.drop_down_menu, popupMenu.getMenu());
        popupMenu.show();
	}
	
	public void openActivityAction(View view)
	{
		if (statusActive)
		{
			android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
			EnterPasswordDialog epd = new EnterPasswordDialog();
			epd.setAction(EnterPasswordAction.ACCESS_REGISTRY);
			epd.show(fm, "fragment_enter_password");
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.no_reg_access);
		}
	}
	
	public void openSettingsDialog()
	{
		if (statusActive)
		{
			// Pedir la clave (si está establecida)
			if (PreferenceUtils.getInstance().checkPrefsPasswordEnabled())
			{
				android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
				EnterPasswordDialog epd = new EnterPasswordDialog();
				epd.setAction(EnterPasswordAction.ACCESS_SETTINGS);
				epd.show(fm, "fragment_enter_password");				
			}
			else
			{
				MainUtils.getInstance().showErrorDialog(getText(R.string.error), getText(R.string.no_pwd_active));
			}			
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.no_reg_access);
		}
	}
	
	public void showDebugInfo(View view)
	{
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		//Set<String> apps = prefs.getStringSet(getString(R.string.key_applist), new HashSet<String>());
		
		final Set<String> defValues = new HashSet<String>();
        defValues.addAll(Arrays.asList(getResources().getStringArray(R.array.pref_applist_defaults)));
		
		final Set<String> values = SharedPreferenceCompat.getStringSet(prefs, getString(R.string.key_applist), defValues);
		String[] allValues = getResources().getStringArray(R.array.pref_applist_values);
        String[] allNames = getResources().getStringArray(R.array.pref_applist_titles);
		String msg = MainUtils.getInstance().makeSummaryText("", values, allValues, allNames);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setTitle("Debug Info");
		AlertDialog errDiag = builder.create();
		errDiag.show();
	}
	
	public void showAboutDialog()
	{
		MainUtils.getInstance().showToast(R.string.not_implemented);
	}
	
	public void activateMonitoring(View view)
	{
		if (!statusActive)
		{
			android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
	        SetPasswordDialog spd = new SetPasswordDialog();
	        spd.show(fm, "fragment_set_password");
		}
	}
	
	public void deactivateMonitoring(View view)
	{
		if (statusActive)
		{			
			android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
			EnterPasswordDialog epd = new EnterPasswordDialog();
			epd.setAction(EnterPasswordAction.DEACTIVATE);
			epd.show(fm, "fragment_enter_password");
		}
	}
	
	private void changeMainAppearance()
	{
		Button act_btn = (Button) findViewById(R.id.activate_btn);
		Button dea_btn = (Button) findViewById(R.id.deactivate_btn);
		Button via_btn = (Button) findViewById(R.id.activity_btn);
		
		ImageView img = (ImageView)findViewById(R.id.mon_status_icon);
		if (statusActive)
		{
			img.setImageResource(R.drawable.monit_active);
			act_btn.setVisibility(View.GONE);
			dea_btn.setVisibility(View.VISIBLE);
			via_btn.setVisibility(View.VISIBLE);
		}
		else
		{
			img.setImageResource(R.drawable.monit_inactive);
			act_btn.setVisibility(View.VISIBLE);
			dea_btn.setVisibility(View.GONE);
			via_btn.setVisibility(View.GONE);
		}
	}
	
	public void setPasswordClick(String pwd)
	{
		PreferenceUtils.getInstance().enablePrefsActivityRegistry(pwd);
		statusActive = true;
		
		// Refresh view
		changeMainAppearance();
		MainUtils.getInstance().showToast(R.string.mon_activated);
	}
	
	public void enterPasswordDisable(String pwd)
	{
		boolean deact = false;
		if (PreferenceUtils.getInstance().checkPrefsPasswordEnabled())
		{
			String m_pwd = PreferenceUtils.getInstance().getPrefsMasterPassword();
			deact = m_pwd.equalsIgnoreCase(pwd);
		}
		else
		{
			deact = true;
		}
		
		if (deact)
		{
			PreferenceUtils.getInstance().disablePrefsActivityRegistry();
			statusActive = false;
			
			// Refresh view
			changeMainAppearance();
			MainUtils.getInstance().showToast(R.string.mon_deactivated);
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.wrong_pwd);
		}
	}
	
	public void enterPasswordAccessSettings(String pwd)
	{
		boolean access = false;
		if (PreferenceUtils.getInstance().checkPrefsPasswordEnabled())
		{
			String m_pwd = PreferenceUtils.getInstance().getPrefsMasterPassword();
			access = m_pwd.equalsIgnoreCase(pwd);
		}
		else
		{
			access = true;
		}
		
		if (access)
		{
			Intent intent = new Intent(this, SettingsActivity.class);
		    startActivity(intent);
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.wrong_pwd);
		}
	}
	
	public void enterPasswordAccessRegistry(String pwd)
	{
		boolean access = false;
		if (PreferenceUtils.getInstance().checkPrefsPasswordEnabled())
		{
			String m_pwd = PreferenceUtils.getInstance().getPrefsMasterPassword();
			access = m_pwd.equalsIgnoreCase(pwd);
		}
		else
		{
			access = true;
		}
		
		if (access)
		{
			Intent intent = new Intent(this, ShowRegistryActivity.class);
		    startActivity(intent);
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.wrong_pwd);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

}
