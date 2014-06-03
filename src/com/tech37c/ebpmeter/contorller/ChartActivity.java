package com.tech37c.ebpmeter.contorller;


import com.amour.ebpmeter.R;
import com.tech37c.ebpmeter.view.ChartData;
import com.tech37c.ebpmeter.view.FancyChart;
import com.tech37c.ebpmeter.view.FancyChartPointListener;
import com.tech37c.ebpmeter.view.Point;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ChartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.activity_chart);
		
		FancyChart chart = (FancyChart) findViewById(R.id.chart);
		chart.setOnPointClickListener(new FancyChartPointListener() {
			@Override
			public void onClick(Point point) {
				Toast.makeText(ChartActivity.this, "月份，血压值 " + point, Toast.LENGTH_LONG).show();
			}
		});
		
		ChartData data = new ChartData(ChartData.LINE_COLOR_BLUE);
		int[] yValues = new int[]{80, 90, 85, 93, 79, 82, 88, 91, 93, 84, 98, 90};
		for(int i = 1; i <= 12; i++) {
			data.addPoint(i , yValues[i-1]);
			data.addXValue(i, i + "月");
		}
		chart.addData(data);
		
		ChartData data2 = new ChartData(ChartData.LINE_COLOR_RED);
		int[] yValues2 = new int[]{149, 152, 158, 154, 149, 135, 140, 138, 141, 138, 136, 135};
		for(int i = 1; i <= 12; i++) {
			data2.addPoint(i, yValues2[i-1]);
			data2.addXValue(i, i+ "月");
		}
		chart.addData(data2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart, menu);
		return true;
	}
	
}
