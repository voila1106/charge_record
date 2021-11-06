package com.voila.charge;

import java.io.*;
import java.util.*;

public class Charge implements Serializable
{
	private final long startTime;
	private long endTime;
	private long fullTime = -1;
	private final int startBattery;
	private int endBattery;
	private final File dataDir;
	private final Map<Long, Integer> process;

	public Charge(int level, File file)
	{
		startTime = System.currentTimeMillis();
		startBattery = level;
		dataDir = file;
		process = new TreeMap<>();
	}


	public long getStartTime()
	{
		return startTime;
	}

	public long getEndTime()
	{
		return endTime;
	}

	public long getFullTime()
	{
		return fullTime;
	}

	public int getStartBattery()
	{
		return startBattery;
	}

	public int getEndBattery()
	{
		return endBattery;
	}

	public Map<Long, Integer> getProcess()
	{
		return process;
	}

	public void finish(int level)
	{
		endTime = System.currentTimeMillis();
		endBattery = level;

		File f = new File(dataDir.getAbsolutePath() + "/" + endTime);
		try
		{
			f.createNewFile();
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
			os.writeObject(this);
			os.flush();
			os.close();

			f = new File(dataDir.getAbsolutePath() + "/unfinished");
			f.delete();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setFullTime(Long time)
	{
		fullTime = time;
	}

	public void mark(long timestamp, int level)
	{
		process.put(timestamp, level);
		File f = new File(dataDir.getAbsolutePath() + "/unfinished");
		try
		{
			f.createNewFile();
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
			os.writeObject(this);
			os.flush();
			os.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}

	}

}
