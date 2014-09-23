package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//自定义标题栏
		setContentView(R.layout.activity_setting);
//		final TextView txtView = (TextView)findViewById(R.id.myTitle);
//		txtView.setText("设置");
		
		final Switch openBuble = (Switch)findViewById(R.id.openBuble);
		final Switch openNeverUse = (Switch) findViewById(R.id.openNeverUse);
		final ImageButton updateBtn = (ImageButton) findViewById(R.id.check_update_Btn);
		final ImageButton back2MainBtn = (ImageButton) findViewById(R.id.back_2_main);
		final ImageButton userSetting = (ImageButton) findViewById(R.id.user_setting);
		final ImageButton rgstDevBtn = (ImageButton) findViewById(R.id.register_dev);
		
		
		openBuble.setChecked(true);
		openNeverUse.setChecked(true);
		
		back2MainBtn.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
				Intent intent = new Intent(v.getContext(), MainActivity.class);
    			startActivity(intent);
    		}
    	});
		
		openBuble.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)  {
					BackgroundService.isON4Buble = true;
				}else{
					BackgroundService.isON4Buble = false;
				}
			}
		});
		
		updateBtn.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
    			UpdateManager manager = new UpdateManager(SettingActivity.this);
				// 检查软件更新
				manager.checkUpdate();
    		}
    	});
		
		userSetting.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
				Intent intent = new Intent(v.getContext(), UserEditActivity.class);
    			startActivity(intent);
    		}
    	});
		
		rgstDevBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick (View v){
				Intent intent = new Intent(v.getContext(), WelcomeActivity.class);
    			startActivity(intent);
    		}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
