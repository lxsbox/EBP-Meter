package com.tech37c.ebpmeter.contorller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.view.ChartData;
import com.tech37c.ebpmeter.view.FancyChart;
import com.tech37c.ebpmeter.view.FancyChartPointListener;
import com.tech37c.ebpmeter.view.Point;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ChartActivity extends Activity {
	private ValueLineChart chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.activity_chart);
		chart = (ValueLineChart) findViewById(R.id.cubiclinechart);
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart, menu);
		return true;
	}

	private void loadData() {
		BusinessHandler handler = new BusinessHandler(this);// 初始化业务处理对象
		List list = handler.query10Day();
		
//		ChartData highData = new ChartData(ChartData.LINE_COLOR_RED);
		ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
		
		Map highMap = (HashMap) list.get(0);
//		int highIndex = 0;
		for (Iterator it = highMap.keySet().iterator(); it.hasNext();) {
//			highIndex++;
			Object obj = (String) it.next();
			String dateTime = (String) obj;
			String highX = dateTime.substring(5, 10);
//			highData.addPoint(highIndex, (Integer) highMap.get(obj));
//			highData.addXValue(highIndex, highX + "日");
			series.addPoint(new ValueLinePoint(highX + "日", (Integer)highMap.get(obj)));
		}
		chart.addSeries(series);
	    chart.addStandardValue(135);
	    chart.setOnPointFocusedListener(new IOnPointFocusedListener() {
            public void onPointFocused(int _PointPos) {
                Log.d("Test", "Pos: " + _PointPos);
            }
        });
	    
//
//		ChartData lowData = new ChartData(ChartData.LINE_COLOR_BLUE);
//		Map lowMap = (HashMap) list.get(1);
//		int lowIndex = 0;
//		for (Iterator it = lowMap.keySet().iterator(); it.hasNext();) {
//			lowIndex++;
//			Object obj = (String) it.next();
//			String dateTime = (String) obj;
//			String lowX = dateTime.substring(5, 8);
//			lowData.addPoint(lowIndex, (Integer) lowMap.get(obj));
//			lowData.addXValue(lowIndex, lowX + "日");
//		}
	}
}
