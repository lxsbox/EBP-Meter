package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.zbar.lib.CaptureActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		String devId = pref.getString(RegisterActivity.DEVICE_ID, "");
		String devType = pref.getString(RegisterActivity.DEVICE_TYPE, "");
		
		
		if (devId.equals("")||devType.equals("")) {//If this App is not been registered
			setContentView(R.layout.activity_welcome);
			final Button regButton  = (Button)findViewById(R.id.welcomeButton);
			final TextView skipBtn  = (TextView)findViewById(R.id.skipBtn);
			regButton.setOnClickListener(new Button.OnClickListener(){
	    		public void onClick (View v){
	    			Intent intent = new Intent(v.getContext(), CaptureActivity.class);
	     			startActivity(intent);
	     			finish();
	    		}
	    	});
			skipBtn.setOnClickListener(new Button.OnClickListener(){
	    		public void onClick (View v){
//	    			Intent intent = new Intent(v.getContext(), SearchingIntroductionActivity.class);
	    			Intent intent = new Intent(v.getContext(), RegisterActivity.class);
	     			startActivity(intent);
	     			//startService(new Intent(BackgroundService.ACTION));//Start the heart beating service
	     			finish();
	    		}
	    	});
		} else {
			//Set the current user
			SharedPreferences.Editor editor = getSharedPreferences(
					BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString(UserEditActivity.CURRENT_USER_ID,"1");
			editor.commit();
			
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			startService(new Intent(BackgroundService.ACTION));//Start the heart beating service
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
}
