package com.startapps.activityrec.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.startapps.activityrec.R;

public class EnterPasswordDialog extends DialogFragment 
{
	EnterPasswordListener mListener;
	private EnterPasswordAction passwordAction;
	
	public enum EnterPasswordAction
	{
		/** Grant access to settings page. */
		ACCESS_SETTINGS,
		/** Grant access to activity regstry page. */
		ACCESS_REGISTRY,
		/** Deactivate monitoring. */
		DEACTIVATE
	}
	
	public void setAction(final EnterPasswordAction action)
	{
		passwordAction = action;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_enter_password, null))
	    // Add action buttons
	           .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // check if passwords are equals...
	            	   EditText pwd_enter = (EditText) EnterPasswordDialog.this.getDialog().findViewById(R.id.pwd_enter);
	            	   String s_pwd_enter = pwd_enter.getText().toString();
	            	   
	            	   switch (passwordAction)
	            	   {
	            	   case DEACTIVATE:
	            		   mListener.enterPasswordDisable(s_pwd_enter);
	            		   break;
	            	   case ACCESS_SETTINGS:
	            		   mListener.enterPasswordAccessSettings(s_pwd_enter);
	            		   break;
	            	   case ACCESS_REGISTRY:
	            		   mListener.enterPasswordAccessRegistry(s_pwd_enter);
	            		   break;
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   EnterPasswordDialog.this.getDialog().cancel();
	               }
	           });
	    
	    return builder.create();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EnterPasswordListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement EnterPasswordListener");
        }
    }
	
	public interface EnterPasswordListener
	{
        public void enterPasswordDisable(String pwd);
        public void enterPasswordAccessSettings(String pwd);
        public void enterPasswordAccessRegistry(String pwd);
    }
}
