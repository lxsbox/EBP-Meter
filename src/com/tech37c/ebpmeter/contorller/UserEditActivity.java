package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;;

/**
 * 编辑用户
 * @author Shawn Li
 *
 */
public class UserEditActivity extends Activity {
	private CheckBox dadCheckBox;
	private CheckBox momCheckBox;
	private EditText dadText;
	private EditText momText;
	
	public static final String DAD = "dad";
	public static final String MOM = "mom";
	public static final String CURRENT_USER_ID = "current_user_id";
	public static final String USER_1 = "1";
	public static final String USER_2 = "2";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
		setContentView(R.layout.activity_user_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_user_edit_title);
		
		dadText = (EditText) findViewById(R.id.dadText);
		momText = (EditText) findViewById(R.id.momText);
		
		dadCheckBox = (CheckBox) findViewById(R.id.dadCheckBox);
		momCheckBox = (CheckBox) findViewById(R.id.momCheckBox);
		momCheckBox.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				dadCheckBox.setChecked(false);
			}
		});
		dadCheckBox.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				momCheckBox.setChecked(false);
			}
		});
		
		final Button saveBtn  = (Button)findViewById(R.id.saveUser);
		saveBtn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Editor edit = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
				String name1 = dadText.getText().toString();
				String name2 = momText.getText().toString();
				edit.putString(DAD, name1);
				edit.putString(MOM, name2);
				if (dadCheckBox.isChecked()) {
					edit.putString(CURRENT_USER_ID, USER_1);
				} else {
					edit.putString(CURRENT_USER_ID, USER_2);
				}
				edit.commit();
				
				Intent intent = new Intent(v.getContext(), TabsActivity.class);
    			startActivity(intent);
    			startService(new Intent(BackgroundService.ACTION));//开始心跳服务
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_edit, menu);
		return true;
	}

}
