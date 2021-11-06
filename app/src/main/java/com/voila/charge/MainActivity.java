package com.voila.charge;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import androidx.appcompat.app.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity
{

	ListView recordListView;
	iAdapter adapter;
	List<Charge> recordList;
	BroadcastReceiver onDisconnected;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		ChargeService.activity = this;

		recordListView = findViewById(R.id.chargeList);
		recordList = new ArrayList<>();

		adapter = new iAdapter(this, R.layout.entry, recordList);
		recordListView.setAdapter(adapter);

		read();

//		ChargeService service=ChargeService.getInstance();
//		if(service!=null)
//		{
//			service.setCallback(this::read);
//		}

		onDisconnected = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				Log.d(" fuck", "received");
				runOnUiThread(MainActivity.this::read);
			}
		};

		registerReceiver(onDisconnected, new IntentFilter("voila.disconnected"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inf = getMenuInflater();
		inf.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.refresh:
				read();
				break;
			case R.id.clear:
				File[] record = getFilesDir().listFiles();
				for(File t : record)
				{
					t.delete();
				}
				read();
				break;
		}
		return true;
	}

	void read()
	{
		//Toast.makeText(this, "read", Toast.LENGTH_SHORT).show();
		File[] record = getFilesDir().listFiles();
		if(record.length <= 0)
		{
			recordList.clear();
			adapter.notifyDataSetChanged();
			return;
		}

		recordList.clear();
		for(File t : record)
		{
			ObjectInputStream is = null;
			try
			{
				is = new ObjectInputStream(new FileInputStream(t));
				Charge c = (Charge)is.readObject();
				is.close();
				recordList.add(c);
			}catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}finally
			{
				if(is != null)
				{
					try
					{
						is.close();
					}catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		adapter.notifyDataSetChanged();

	}

	class iAdapter extends ArrayAdapter<Charge>
	{
		public iAdapter(Context cxt, int res, List<Charge> list)
		{
			super(cxt, res, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inf = LayoutInflater.from(getContext());
			View v = inf.inflate(R.layout.entry, parent, false);
			TextView st = v.findViewById(R.id.startText);
			TextView ed = v.findViewById(R.id.endText);
			TextView ct = v.findViewById(R.id.costTime);
			TextView ft = v.findViewById(R.id.fullText);

			final Charge c = recordList.get(position);
			v.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent(getContext(), ChartActivity.class);
					intent.putExtra("charge", c);
					startActivity(intent);
				}
			});

			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			st.setText(format.format(c.getStartTime()) + "\n" + c.getStartBattery() + "%");
			ed.setText(format.format(c.getEndTime()) + "\n" + c.getEndBattery() + "%");
			ct.setText(format.format(c.getEndTime() - c.getStartTime() + 57600000));
			long fullTime = c.getFullTime();

			if(fullTime == -1)
			{
				ft.setText("未充满");
				return v;
			}

			ft.setText(format.format(fullTime) + "\n耗时" + format.format(fullTime - c.getStartTime() + 57600000));


			return v;
		}


	}

//	public void setCallback()
//	{
//		ChargeService.getInstance().setCallback(this::read);
//	}

	@Override
	protected void onDestroy()
	{
//		ChargeService service=ChargeService.getInstance();
//		if(service!=null)
//		{
//			service.setCallback(null);
//		}
//		ChargeService.activity = null;
		unregisterReceiver(onDisconnected);
		super.onDestroy();
	}

}

