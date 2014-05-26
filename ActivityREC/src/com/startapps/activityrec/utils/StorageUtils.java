package com.startapps.activityrec.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class StorageUtils
{
	/** Nombre del fichero de registro de actividad */
	private static final String LOG_FILE_NAME = "activityLog.acr";
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable()
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable()
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
	    {
	        return true;
	    }
	    return false;
	}
	
	public File getExternalDataStorageDir(Context ctx) 
	{
	    // Get the directory for the app's private directory. 
	   return ctx.getExternalFilesDir(null);
	}
	
	public File getInternalDataStorageDir(Context ctx)
	{
		return ctx.getFilesDir();
	}
	
	public File getStatusLogFile(Context ctx) throws IOException
	{
		// El proceso para encontrar este fichero es:
		// A- Si el almacenamiento externo está disponible:
		//   A.1- Si existe, se devuelve
		//   A.2- Si no existe:
		//     A.2.1- Si existe en el almacenamiento interno, se devuelve
		//     A.2.2- Si no existe, se crea en el almacenamiento externo
		// B- Si no está disponible:
		//   B.1- Si existe en el almacenamiento interno, se devuelve
		//   B.2- Si no existe, se crea en el almacenamiento interno
		
		// A
		if (isExternalStorageWritable())
		{
			File f = new File(getExternalDataStorageDir(ctx),LOG_FILE_NAME);
			// A.1
			if (f.exists())
			{
				return f;
			}
			// A.2
			else
			{
				f = new File(getInternalDataStorageDir(ctx),LOG_FILE_NAME);
				// A.2.1
				if (f.exists())
				{
					return f;
				}
				// A.2.2
				else //No existe ni en almacenamiento externo ni interno
				{
					// Lo creamos en el almacenamiento externo
					f = new File(getExternalDataStorageDir(ctx),LOG_FILE_NAME);
					if (f.createNewFile())
					{
						return f;
					}
					else
					{
						// Si falla, lo creamos en el almacenamiento interno
						f = new File(getInternalDataStorageDir(ctx),LOG_FILE_NAME);
						f.createNewFile();
						return f;
					}
				}
			}
		}
		// B
		else
		{
			File f = new File(getInternalDataStorageDir(ctx),LOG_FILE_NAME);
			// B.2
			if (!f.exists())
			{
				f.createNewFile();
			}
			// B.1
			return f;
		}
	}
	
	/*public void registerActivityLog(final Context ctx, final ActivityRecord record) throws IOException
	{
		File f = getStatusLogFile(ctx);
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(record);
		oos.close();
	}*/
}
