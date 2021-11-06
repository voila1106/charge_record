package com.voila.charge;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.LinearLayout.*;

import java.text.*;
import java.util.*;

public class ChartActivity extends Activity
{

	Map<Long, Integer> process;

	@Override
	public void onPointerCaptureChanged(boolean hasCapture)
	{
		// TODO: Implement this method
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Charge charge = (Charge)getIntent().getSerializableExtra("charge");
		setContentView(R.layout.activity_chart);
		process = charge.getProcess();

		LayoutInflater inf = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout)inf.inflate(R.layout.activity_chart, null);
		Chart chart = new Chart(this);
		LayoutParams param = new LayoutParams(-2, px(400));
		param.setMargins(px(20), 0, px(20), 0);
		chart.setLayoutParams(param);

		layout.addView(chart);
		setContentView(layout);
	}

	class Chart extends View
	{
		public Chart(Context cxt)
		{
			super(cxt);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1);
			paint.setTextSize(px(10));
			canvas.drawLine(px(26), 0, px(26), px(200), paint);
			canvas.drawLine(px(26), px(200), px(350), px(200), paint);
			canvas.drawText("100%", 0, px(10), paint);
			canvas.drawText("0%", px(5), px(200), paint);
			canvas.drawText("50%", 0, px(105), paint);
			
			/*
			Map<Long,Integer> trimed=new TreeMap<>();
			int diff=process.size()-324;
			*/
			if(process.size() <= 1)
			{
				//Toast.makeText(ChartActivity.this,"0",1).show();
				return;
			}
			//paint.setColor(Color.rgb(0x46,0xff,0x5d));
			paint.setStrokeWidth(px(1) * 2);
			double scale = (double)px(322) / (process.size() - 1);
			double x = px(28);
			ArrayList<Long> keys = new ArrayList<>();
			ArrayList<Integer> values = new ArrayList<>();
			keys.addAll(process.keySet());
			values.addAll(process.values());
			for(int i = 0; i < process.size() - 1; i++)
			{
				if(values.get(i) < 20)
					paint.setColor(Color.rgb(0xff, 0xcb, 0x2e));
				else if(values.get(i) < 80)
					paint.setColor(Color.rgb(0x2e, 0x97, 0xff));
				else
					paint.setColor(Color.rgb(0x46, 0xff, 0x5d));
				canvas.drawLine((float)x, px(202) - px(values.get(i) * 2), (float)(x + scale), px(202) - px(values.get(i + 1) * 2), paint);
				canvas.drawCircle((float)x, px(202) - px(values.get(i) * 2), px(1), paint);
				x += scale;
			}
			canvas.drawCircle((float)x, px(202) - px(values.get(process.size() - 1) * 2), px(1), paint);

			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			paint.setColor(Color.BLACK);
			canvas.drawText(format.format(keys.get(0)), px(26), px(210), paint);
			canvas.drawText(format.format(keys.get(keys.size() - 1)), px(310), px(210), paint);
		}


	}

	private int px(int dp)
	{
		double scale = getResources().getDisplayMetrics().density;
		return (int)(dp * scale + 0.5);
	}
}
