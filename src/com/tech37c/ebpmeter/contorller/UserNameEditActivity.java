package com.tech37c.ebpmeter.contorller;


import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserNameEditActivity extends Activity {
	private EditText userName;
	private EditText wifiPassword;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifi_input);
		wifiPassword = (EditText) findViewById(R.id.wifi_password);
		wifiPassword.setVisibility(4);
		TextView red_top_hit = (TextView) findViewById(R.id.red_top_hit);
		red_top_hit.setVisibility(4);
		
		ImageButton back_2_parent = (ImageButton) findViewById(R.id.back_2_parent);
		back_2_parent.setVisibility(0);
		ImageButton okName = (ImageButton) findViewById(R.id.ok_wifi);
		userName = (EditText) findViewById(R.id.wifi_name);
		TextView wifi_input_title_text = (TextView)findViewById(R.id.wifi_input_title_text);
		wifi_input_title_text.setText(getString(R.string.edit_user_name));
		
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		Intent myIntent = getIntent();
		int requestFrom = 0;
		if (null != myIntent) {
			requestFrom = (Integer)myIntent.getExtras().get("requestFrom");
		}
		
		if(requestFrom == UserEditActivity.USER1_NAME_EDIT_REQUEST_CODE) {
			String user1 = pref.getString(UserEditActivity.USER_1_NAME_VALUE, getString(R.string.user1_default_name));
			userName.setHint(user1);
			okName.setOnClickListener(new Button.OnClickListener() {
	    		public void onClick (View v) {
	    			String name = userName.getText().toString();
	    			SharedPreferences.Editor editor = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
	    			editor.putString(UserEditActivity.USER_1_NAME_VALUE, name);
	    			editor.commit();
	    			
	                Intent intent = new Intent();
	                intent.putExtra("result", name);
	                UserNameEditActivity.this.setResult(RESULT_OK, intent);
	                UserNameEditActivity.this.finish();
	    		}
	    	});
		}else {
			String user2 = pref.getString(UserEditActivity.USER_2_NAME_VALUE, getString(R.string.user2_default_name));
			userName.setHint(user2);
			okName.setOnClickListener(new Button.OnClickListener() {
	    		public void onClick (View v){
	    			String name = userName.getText().toString();
	    			SharedPreferences.Editor editor = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
	    			editor.putString(UserEditActivity.USER_2_NAME_VALUE, name);
	    			editor.commit();
	    			
	                Intent intent = new Intent();
	                intent.putExtra("result", name);
	                UserNameEditActivity.this.setResult(RESULT_OK, intent);
	                UserNameEditActivity.this.finish();
	    		}
	    	});
		}
		
		back_2_parent.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
