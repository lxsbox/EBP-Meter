package com.tech37c.ebpmeter.contorller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * 程序入口
 * 
 * @author ShawnLi
 * 
 */
public class MainActivity extends FragmentActivity  {
	private Context mContext;
	protected TextView meterUser;
	protected TextView checkingTime;
	protected TextView highValue;
	protected TextView lowValue;
	protected TextView heartBeat;
	protected Button giveCall;
	protected Button giveVideo;
	private Spinner mActionbarSpinner;
	private ImageButton settingBtn;
	private ImageView lstBtn;
	private ImageView reminderBtn;
	private ImageView face;
	
	// 分页下相关参数
	private static int NUM_PAGES;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private BusinessHandler handler;
	private SharedPreferences pref;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏
		setContentView(R.layout.activity_home);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_home_title);
		pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		String name = pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1).equals(UserEditActivity.USER_1)?//获取当前用户名
				pref.getString(UserEditActivity.DAD, getString(R.string.main_dad)):pref.getString(UserEditActivity.MOM, getString(R.string.main_mom));
		String age = pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1).equals(UserEditActivity.USER_1)?//获取当前用户名
				pref.getString(UserEditActivity.USER_1_AGE, "0"):pref.getString(UserEditActivity.USER_2_AGE, "0");
				
		face = (ImageView) findViewById(R.id.head_pic);
		setDrawableFace4Main(face);
		
		final TextView txtView = (TextView) findViewById(R.id.current_user_name);
		txtView.setText(name);
		final TextView ageView = (TextView) findViewById(R.id.main_age);
		ageView.setText(age);
		handler = new BusinessHandler(this);// 初始化业务处理对象
		Cursor cursor = handler.getRecordCursor();
		if(cursor.getCount()>0) {
			cursor.moveToPosition(0);
			final TextView highTxt = (TextView) findViewById(R.id.main_high);
			highTxt.setText(cursor.getInt(5) + "");
			final TextView lowTxt = (TextView) findViewById(R.id.main_low);
			lowTxt.setText(cursor.getInt(6) + "");
			final TextView beatTxt = (TextView) findViewById(R.id.main_beat);
			beatTxt.setText(cursor.getInt(7) + "");
			
			final ImageView imgView = (ImageView) findViewById(R.id.home_status);
			if(cursor.getInt(5)<135) {
				imgView.setImageResource(R.drawable.health_good);
			}else if(cursor.getInt(5)>135 && cursor.getInt(5)<161) {
				imgView.setImageResource(R.drawable.health_light);
			}else if(cursor.getInt(5)>160 && cursor.getInt(5)<180) {
				imgView.setImageResource(R.drawable.health_middle);
			}else {
				imgView.setImageResource(R.drawable.health_heavy);
			}
		}

		settingBtn = (ImageButton) findViewById(R.id.main_title_setting);
		settingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, SettingActivity.class);
    			startActivity(intent);
			}
		});
		
		lstBtn = (ImageView) findViewById(R.id.list_btn);
		lstBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, RecordsActivity.class);
    			startActivity(intent);
			}
		});
		
		reminderBtn = (ImageView) findViewById(R.id.reminder_btn);
		reminderBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, ReminderActivity.class);
    			startActivity(intent);
			}
		});
	}

	/**
	 * Fill main page's face picture
	 * @return
	 */
	public void setDrawableFace4Main(ImageView face) {
		Bitmap bitmap = null;
		String path = "";
		if(pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1).equals(UserEditActivity.USER_1)) {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.DAD_IMAGE_FILE_NAME;
		} else if(pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1).equals(UserEditActivity.USER_2)) {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.MOM_IMAGE_FILE_NAME;
		} 
		if (!path.equals("")) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path);
				bitmap  = BitmapFactory.decodeStream(fis);
				Drawable drawable = new BitmapDrawable(bitmap);
				face.setImageDrawable(drawable);
			} catch (FileNotFoundException e) {
				System.out.println("file not found");
			} finally {
				try {
					if(fis != null) fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
