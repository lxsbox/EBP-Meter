package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.id;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;
import com.tech37c.ebpmeter.utils.WifiUtil;
import com.zbar.lib.CaptureActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchingIntroductionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//自定义标题栏
		setContentView(R.layout.activity_searching_introduction);
		Button scanBtn = (Button) findViewById(R.id.start_2_scan);
		TextView skipView = (TextView) findViewById(R.id.skip_scan);
		
		scanBtn.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
    			Intent intent = new Intent(v.getContext(), SearchingActivity.class);
     			startActivity(intent);
     			finish();
    		}
    	});
		skipView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
    			Intent intent = new Intent(v.getContext(), MainActivity.class);
     			startActivity(intent);
     			finish();
    		}
    	});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_searching, menu);
		return true;
	}
}
