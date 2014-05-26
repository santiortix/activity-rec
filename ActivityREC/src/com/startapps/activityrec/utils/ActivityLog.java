package com.startapps.activityrec.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.startapps.activityrec.types.ActivityRecord;
import com.startapps.activityrec.types.SortedList;

public class ActivityLog
{
	private static ActivityLog instance;
	private StorageUtils su;
	
	private ActivityLog()
	{
		su = new StorageUtils();
	}
	
	public static ActivityLog getInstance()
	{
		if (instance == null)
		{
			instance = new ActivityLog();
		}
		return instance;
	}
	
	public void resetActivityLog(Context ctx) throws IOException
	{
		File log = su.getStatusLogFile(ctx);
		SortedList<ActivityRecord> logRecord = new SortedList<ActivityRecord>();
		FileOutputStream fos = new FileOutputStream(log);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(logRecord);
		oos.close();
	}
	
	@SuppressWarnings("unchecked")
	public void appendActivityRecord(final Context ctx, final ActivityRecord ar) throws IOException, ClassNotFoundException
	{
		// Leemos el contenido actual del fichero de log...
		File log = su.getStatusLogFile(ctx);
		FileInputStream fis = new FileInputStream(log);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		Object actual = ois.readObject();
		ois.close();
		
		SortedList<ActivityRecord> logRecord;
		if (actual instanceof SortedList)
		{
			// Encontramos una lista en el fichero
			logRecord = (SortedList<ActivityRecord>)actual;
			// Añadimos el elemento y volvemos a escribir
			logRecord.add(ar);
		}
		else if (actual instanceof ActivityRecord)
		{
			// Encontramos solo un record en el fichero
			ActivityRecord record = (ActivityRecord)actual;
			// Hacemos una lista con el record anterior y el nuevo
			logRecord = new SortedList<ActivityRecord>();
			logRecord.add(record);
			logRecord.add(ar);
		}
		else
		{
			// No encontramos nada en la lista
			// Creamos una nueva lista con el nuevo record
			logRecord = new SortedList<ActivityRecord>();
			logRecord.add(ar);
		}
		
		if (logRecord != null && !logRecord.isEmpty())
		{
			FileOutputStream fos = new FileOutputStream(log);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(logRecord);
			oos.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public SortedList<ActivityRecord> getActivityLog(Context ctx) throws IOException, ClassNotFoundException
	{
		File log = su.getStatusLogFile(ctx);
		FileInputStream fis = new FileInputStream(log);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		Object actual = ois.readObject();
		ois.close();
		if (actual instanceof SortedList)
		{
			return (SortedList<ActivityRecord>)actual;
		}
		else if (actual instanceof ActivityRecord)
		{
			// Encontramos solo un record en el fichero
			ActivityRecord record = (ActivityRecord)actual;
			// Hacemos una lista con el record anterior y el nuevo
			SortedList<ActivityRecord> res = new SortedList<ActivityRecord>();
			res.add(record);
			return res;
		}
		else
		{
			throw new ClassNotFoundException("Object class not expected");
		}
	}
}
