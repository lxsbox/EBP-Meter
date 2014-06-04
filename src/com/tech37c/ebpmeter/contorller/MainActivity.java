package com.tech37c.ebpmeter.contorller;



import com.amour.ebpmeter.R;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	protected TextView meterUser;
	protected TextView checkingTime;
	protected TextView highValue;
	protected TextView lowValue;
	protected TextView heartBeat;
	protected Button giveCall;
	protected Button giveVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//连接界面元素和属性
		meterUser = (TextView) findViewById(R.id.meter_user);
		checkingTime = (TextView) findViewById(R.id.checking_time);
		highValue = (TextView) findViewById(R.id.high_value);
		lowValue = (TextView) findViewById(R.id.low_value);
		heartBeat = (TextView) findViewById(R.id.heart_beat);
		giveCall = (Button) findViewById(R.id.give_call);
		giveVideo = (Button) findViewById(R.id.give_video);
		
		//设置监听事件
		giveCall.setOnClickListener(this);
		giveVideo.setOnClickListener(this);
		BusinessHandler handler = new BusinessHandler(this);//初始化业务处理对象
		RecordPOJO record = handler.initMainView();
		meterUser.setText(record.getUser_ID());
		checkingTime.setText(record.getMeasure_Time());
		highValue.setText(record.getHBP());
		lowValue.setText(record.getLBP());
		heartBeat.setText(record.getBeat());
		startService(new Intent(BackgroundService.ACTION));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	      case R.id.add_record:
	        Intent intent = new Intent(this, AddRecordActivity.class);
	        startActivity(intent);
	        return true;
	      case R.id.show_chart:
	    	Intent chartIntent = new Intent(this, ChartActivity.class);
		    startActivity(chartIntent);
	        return true;
	      case R.id.show_all_data:
	    	  Intent sdIntent = new Intent(this, ShowAllDataActivity.class);
		      startActivity(sdIntent);
		      return true;
	      default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}
