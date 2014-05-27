package com.startapps.activityrec;

import java.io.IOException;
import java.util.Date;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.TextView;

import com.startapps.activityrec.activities.SettingsActivity;
import com.startapps.activityrec.activities.ShowRegistryActivity;
import com.startapps.activityrec.dialogs.EnterPasswordDialog;
import com.startapps.activityrec.dialogs.EnterPasswordDialog.EnterPasswordAction;
import com.startapps.activityrec.dialogs.EnterPasswordDialog.EnterPasswordListener;
import com.startapps.activityrec.dialogs.SetPasswordDialog;
import com.startapps.activityrec.dialogs.SetPasswordDialog.SetPasswordListener;
import com.startapps.activityrec.services.SvcCallsService;
import com.startapps.activityrec.types.AbstractRecord;
import com.startapps.activityrec.types.ActivityRecord;
import com.startapps.activityrec.types.RecordType;
import com.startapps.activityrec.types.SortedList;
import com.startapps.activityrec.utils.ActivityLog;
import com.startapps.activityrec.utils.MainUtils;
import com.startapps.activityrec.utils.PreferenceUtils;

public class MainActivity extends ActionBarActivity /*FragmentActivity*/
	implements SetPasswordListener, EnterPasswordListener/*, SharedPreferences.OnSharedPreferenceChangeListener*/	
{
	
	public final static String EXTRA_MESSAGE = "com.startapps.activityrec.MESSAGE";
	
	private boolean statusActive = false;
	private final ActivityLog LOG = ActivityLog.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences prefs = getPrefs();
		statusActive = prefs.getBoolean(getString(R.string.key_monitoring_active), false);
		
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
		if (statusActive)
		{
			if (getApplicationContext().startService(new Intent(this, SvcCallsService.class)) == null)
			{
				// El estado es activo pero el servicio no estaba arrancado...
				// pero no lo acabamos de arrancar :(
				statusActive = false;				
				changeMainAppearance();
				MainUtils.getInstance().showToastLong(R.string.err_service_start);
			}
		}
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		changeMainAppearance();
		if (statusActive)
		{
			if (getApplicationContext().startService(new Intent(this, SvcCallsService.class)) == null)
			{
				// El estado es activo pero el servicio no estaba arrancado...
				// pero no lo acabamos de arrancar :(
				statusActive = false;				
				changeMainAppearance();
				MainUtils.getInstance().showToastLong(R.string.err_service_start);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
		case R.id.menu_overflow:
			showDropDown(R.id.menu_overflow);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
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
		/*
		//SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences prefs = getPrefs();
		//Set<String> apps = prefs.getStringSet(getString(R.string.key_applist), new HashSet<String>());
		
		final Set<String> defValues = new HashSet<String>();
		String[] sDefValues = getResources().getStringArray(R.array.pref_applist_defaults);
        defValues.addAll(Arrays.asList(sDefValues));
		
		final Set<String> values = SharedPreferenceCompat.getStringSet(prefs, getString(R.string.key_applist), defValues);
		String[] allValues = getResources().getStringArray(R.array.pref_applist_values);
        String[] allNames = getResources().getStringArray(R.array.pref_applist_titles);
		String msg = MainUtils.getInstance().makeSummaryText("", values, allValues, allNames);
		*/
		String msg = "";
		try
		{
			/*StorageUtils su = new StorageUtils();
			File f = su.getStatusLogFile(this);
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ActivityRecord ar = (ActivityRecord)ois.readObject();
			ois.close();*/
			
			SortedList<AbstractRecord> logs = LOG.getActivityLog(this);
			for (AbstractRecord record : logs)
			{
				//msg += "[" + MainUtils.getInstance().formatDate(record.getTimestamp()) + "] - " + getString(record.getMessageIdx()) + "\n";
				msg += record.getString(this) + "\n";
			} 
			
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_read);
		}
		catch (ClassNotFoundException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_read);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setTitle("Debug Info");
		AlertDialog errDiag = builder.create();
		errDiag.show();
		
	}
	
	public void clearLogFiles(View view)
	{
		try
		{
			LOG.resetActivityLog(this);
			MainUtils.getInstance().showToastLong(R.string.clearlog_ok);
		}
		catch (IOException e)
		{
			MainUtils.getInstance().showToastLong(R.string.err_logfile_read);
		}
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
		
		//ImageView img = (ImageView)findViewById(R.id.mon_status_icon);
		TextView status = (TextView)findViewById(R.id.mon_status_text);
		if (statusActive)
		{
			//img.setImageResource(R.drawable.monit_active);
			status.setText(getString(R.string.mon_status_txt_active).toUpperCase());
			status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.monit_active_icon,0,0,0);
			act_btn.setVisibility(View.GONE);
			dea_btn.setVisibility(View.VISIBLE);
			via_btn.setVisibility(View.VISIBLE);
		}
		else
		{
			//img.setImageResource(R.drawable.monit_inactive);
			status.setText(getString(R.string.mon_status_txt_inactive).toUpperCase());
			status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.monit_inactive_icon,0,0,0);
			act_btn.setVisibility(View.VISIBLE);
			dea_btn.setVisibility(View.GONE);
			via_btn.setVisibility(View.GONE);
		}
	}
	
	public void setPasswordClick(String pwd)
	{		
		// Iniciamos el servicio
		ComponentName name = getApplicationContext().startService(new Intent(this, SvcCallsService.class));
		
		if (name != null)
		{		
			// Establecemos el registro de actividad a activo en las preferencias
			PreferenceUtils.getInstance().enablePrefsActivityRegistry(pwd);
			statusActive = true;
			
			// Guardamos en el fichero de LOG la activación de la monitorización
			try
			{
				LOG.appendActivityRecord(this, new ActivityRecord(new Date(), R.string.log_act_activation,RecordType.ACTIVATION));
			}
			catch (IOException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			catch (ClassNotFoundException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			
			// Refresh view
			changeMainAppearance();
			MainUtils.getInstance().showToast(R.string.mon_activated);
		}
		else
		{
			MainUtils.getInstance().showToastLong(R.string.err_service_start);
		}
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
			// Detenemos el servicio de monitorización
			boolean bStop = getApplicationContext().stopService(new Intent(this, SvcCallsService.class)); 
						
			// Establecemos el registro de actividad a desactivado en las preferencias
			PreferenceUtils.getInstance().disablePrefsActivityRegistry();
			statusActive = false;
				
			// Guardamos en el fichero de LOG la desactivación de la monitorización
			try
			{
				LOG.appendActivityRecord(this, new ActivityRecord(new Date(), R.string.log_act_deactivation,RecordType.DEACTIVATION));
			}
			catch (IOException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			catch (ClassNotFoundException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			
			// Refresh view
			changeMainAppearance();
			MainUtils.getInstance().showToast(R.string.mon_deactivated);
			
			if (!bStop)
			{
				MainUtils.getInstance().showToastLong(R.string.err_service_stop);
			}
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
			try
			{
				LOG.appendActivityRecord(this, new ActivityRecord(new Date(),R.string.log_act_view_settings,RecordType.VIEW_SETTINGS));
			}
			catch (IOException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			catch (ClassNotFoundException e)
			{
				MainUtils.getInstance().showToastLong(R.string.err_logfile_write);
			}
			
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
	
	private SharedPreferences getPrefs()
	{
        return PreferenceManager.getDefaultSharedPreferences(this);
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
