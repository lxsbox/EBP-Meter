package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.zbar.lib.CaptureActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		String devId = pref.getString(RegisterActivity.DEVICE_ID, "");
		String devType = pref.getString(RegisterActivity.DEVICE_TYPE, "");
		
		if (devId.equals("")||devType.equals("")) {//如果已经注册过直接到主页面
			setContentView(R.layout.activity_welcome);
			final Button regButton  = (Button)findViewById(R.id.welcomeButton);
			final Button skipBtn  = (Button)findViewById(R.id.skipBtn);
			regButton.setOnClickListener(new Button.OnClickListener(){
	    		public void onClick (View v){
	    			Intent intent = new Intent(v.getContext(), CaptureActivity.class);
	     			startActivity(intent);
	    		}
	    	});
			skipBtn.setOnClickListener(new Button.OnClickListener(){
	    		public void onClick (View v){
	    			Intent intent = new Intent(v.getContext(), RegisterActivity.class);
	     			startActivity(intent);
	    		}
	    	});
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
