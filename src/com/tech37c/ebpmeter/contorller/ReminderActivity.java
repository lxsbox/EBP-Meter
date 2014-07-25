package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ReminderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//自定义标题栏
		setContentView(R.layout.activity_reminder);
		final TextView txtView = (TextView) findViewById(R.id.current_user);
		txtView.setText("提醒");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reminder, menu);
		return true;
	}

}
