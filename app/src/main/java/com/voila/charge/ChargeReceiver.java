package com.voila.charge;

import android.content.*;
import android.util.*;
import android.widget.*;

public class ChargeReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
		{
			Intent srv = new Intent(context, ChargeService.class);
			srv.putExtra("ChargeState", 1);
			context.startService(srv);
		}

		if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
		{
			Intent srv = new Intent(context, ChargeService.class);
			srv.putExtra("ChargeState", 0);
			context.startService(srv);
		}

	}

}
