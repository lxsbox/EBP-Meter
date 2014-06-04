package com.amour.ebpmeter;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	protected TextView meterUser;
	protected TextView checkingTime;
	protected TextView highValue;
	protected TextView lowValue;
	protected TextView heartBeat;

	protected Button giveCall;
	protected Button giveVideo;

	protected MeterData meterData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Connect interface elements to properties */
		meterUser = (TextView) findViewById(R.id.meter_user);
		checkingTime = (TextView) findViewById(R.id.checking_time);
		highValue = (TextView) findViewById(R.id.high_value);
		lowValue = (TextView) findViewById(R.id.low_value);
		heartBeat = (TextView) findViewById(R.id.heart_beat);

		giveCall = (Button) findViewById(R.id.give_call);
		giveVideo = (Button) findViewById(R.id.give_video);

		/* Setup ClickListeners */
		giveCall.setOnClickListener(this);
		giveVideo.setOnClickListener(this);

		/* Set the blood pressure meter's data source */
		meterData = new MeterData(this);

		/* Add some default data! (Adjust to your preference :) */
		if (meterData.count() == 0) {
			meterData.insert("贾某某","2014-01-05 20:00:00", 135, 85, 75);
			meterData.insert("程爽", "2014-02-08 20:00:00", 135, 85, 75);
			meterData.insert("周瑜", "2014-03-10 20:00:00", 135, 85, 75);
		}
		Cursor cursor = meterData.all(this);
		if (cursor.moveToLast()) {
			meterUser.setText(cursor.getString(1));
			checkingTime.setText(cursor.getString(2));
			highValue.setText(cursor.getInt(3) + "");
			lowValue.setText(cursor.getInt(4) + "");
			heartBeat.setText(cursor.getInt(5) + "");
		}
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
}
