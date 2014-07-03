package com.tech37c.ebpmeter.contorller;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.BusinessHandler;
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
		
		BusinessHandler handler = new BusinessHandler(this);//初始化业务处理对象
		List list = handler.query10Day();
		ChartData highData = new ChartData(ChartData.LINE_COLOR_RED);
		Map highMap = (HashMap)list.get(0);
		int highIndex = 0;
		for (Iterator it = highMap.keySet().iterator(); it.hasNext();) {
			highIndex ++;
			Object obj = (String)it.next();
			String dateTime = (String)obj;
			String highX = dateTime.substring(5, 8);
			highData.addPoint(highIndex, (Integer)highMap.get(obj));
			highData.addXValue(highIndex, highX+"日");
		}
		
		
		ChartData lowData = new ChartData(ChartData.LINE_COLOR_BLUE);
		Map lowMap = (HashMap)list.get(1);
		int lowIndex = 0;
		for (Iterator it = lowMap.keySet().iterator(); it.hasNext();) {
			lowIndex ++;
			Object obj = (String)it.next();
			String dateTime = (String)obj;
			String lowX = dateTime.substring(5, 8);
			lowData.addPoint(lowIndex, (Integer)lowMap.get(obj));
			lowData.addXValue(lowIndex, lowX +"日");
		}
//		int[] high = new int[]{80, 90, 85, 93, 79, 82, 88, 91, 93, 84, 98, 90};
//		for(int i = 1; i <= 10; i++) {
//			highData.addPoint(i , high[i-1]);
//			highData.addXValue(i, i + "日");
//		}
//		chart.addData(highData);
//		
//		ChartData lowData = new ChartData(ChartData.LINE_COLOR_BLUE);
//		int[] low = new int[]{149, 152, 158, 154, 149, 135, 140, 138, 141, 138, 136, 135};
//		for(int i = 1; i <= 10; i++) {
//			lowData.addPoint(i, low[i-1]);
//			lowData.addXValue(i, i+ "日");
//		}
		chart.addData(highData);
		chart.addData(lowData);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart, menu);
		return true;
	}
	
}
