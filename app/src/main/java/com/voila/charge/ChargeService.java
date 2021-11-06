package com.voila.charge;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import java.io.*;
import java.text.*;

public class ChargeService extends Service
{


	Charge charge;
	File record;
	Runnable callback;
//	static MainActivity activity;
//	private static ChargeService instance;

	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	@Override
	@SuppressLint("NewApi")
	public int onStartCommand(Intent intent, int flags, int startId)
	{
//		instance = this;
//		if(activity != null)
//		{
//			activity.setCallback();
//		}
		record = getFilesDir();
		BatteryManager bm = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
		int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

		Handler handler = new Handler(Looper.getMainLooper());

		int state = intent.getIntExtra("ChargeState", -1);
		new Thread(() ->
		{
			Runnable task = null;
			if(state != 0) //connect
			{
				if(state == 1)
				{
					charge = new Charge(level, record);
				}else
				{
					try
					{
						File last = new File(record.getAbsolutePath() + "/unfinished");
						ObjectInputStream os = new ObjectInputStream(new FileInputStream(last));
						charge = (Charge)os.readObject();
						os.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}


				task = new Runnable()
				{

					@Override
					public void run()
					{
						if(charge == null)
						{
							handler.removeCallbacks(this);
							return;
						}
						int battery = bm.getIntProperty(4);
						charge.mark(System.currentTimeMillis(), battery);
						if(battery >= 100)
						{
							charge.setFullTime(System.currentTimeMillis());
							handler.removeCallbacks(this);
							return;
						}
						handler.postDelayed(this, 60000 * 2);
					}
				};
				handler.post(task);
			}else if(state == 0)  //disconnect
			{
				File last = new File(record.getAbsolutePath() + "/unfinished");
				if(charge == null)
				{
					if(!last.exists())
					{
						charge = new Charge(level, record);
						charge.finish(level);
					}else
					{
						try
						{
							ObjectInputStream os = new ObjectInputStream(new FileInputStream(last));
							charge = (Charge)os.readObject();
							os.close();
						}catch(Exception e)
						{
							e.printStackTrace();
						}

					}
				}else
				{
					charge.finish(level);
					handler.removeCallbacks(task);
				}
//				if(activity != null)
//				{
//					Log.d("fuck","activity != null");
//					activity.runOnUiThread(activity::read);
//				}
				sendBroadcast(new Intent().setAction("voila.disconnected"));

				SimpleDateFormat fm = new SimpleDateFormat("HH:mm:ss");
				NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				NotificationChannel nc = new NotificationChannel("1", "充电", NotificationManager.IMPORTANCE_DEFAULT);
				Notification notif = new Notification.Builder(getApplicationContext(), "1")
					.setContentTitle("充电结束")
					.setSmallIcon(android.R.drawable.sym_def_app_icon)
					.setContentText("充了" + fm.format(charge.getEndTime() - charge.getStartTime() + 57600000) + "\n" + (charge.getFullTime() == -1 ? "未充满" : "充满用时" + fm.format(charge.getFullTime() - charge.getStartTime() + 57600000)))
					.setChannelId("1")
					.build();
				nm.createNotificationChannel(nc);
				nm.notify(1, notif);

				charge = null;
				stopSelf();
			}
		}).start();
		return START_STICKY;
	}

//	public void setCallback(Runnable cb)
//	{
//		callback = cb;
//	}
//
//	public static ChargeService getInstance()
//	{
//		return instance;
//	}


}
