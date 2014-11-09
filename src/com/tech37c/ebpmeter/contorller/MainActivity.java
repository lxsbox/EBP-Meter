package com.tech37c.ebpmeter.contorller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.ViewUtil;
import com.tech37c.ebpmeter.view.HexagonMaskView;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
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
//	private HexagonMaskView face;
	
	// 分页下相关参数
	private static int NUM_PAGES;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private BusinessHandler handler;
	private SharedPreferences pref;
	private String currentUserId;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏
		setContentView(R.layout.activity_home);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_home_title);
		
		pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		currentUserId = pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1_KEY);
		showInfoByUserId(currentUserId);

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
		
		face.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = getSharedPreferences(
						BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
				String currentUserId = pref.getString(UserEditActivity.CURRENT_USER_ID, "");
				if (currentUserId.equals(UserEditActivity.USER_1_KEY)) {
					editor.putString(UserEditActivity.CURRENT_USER_ID,"2");
					editor.commit();
				} else {
					editor.putString(UserEditActivity.CURRENT_USER_ID,"1");
					editor.commit();
				}
				
				String currentUserIdNow = pref.getString(UserEditActivity.CURRENT_USER_ID, "");
				setDrawableFace4Main(face);
				showInfoByUserId(currentUserIdNow);
			}
		});
	}
	
	/**
	 * 根据用户id显示当前一条信息们
	 */
	public void showInfoByUserId(String userId) {
		face = (ImageView) findViewById(R.id.head_pic);
		final TextView txtView = (TextView) findViewById(R.id.current_user_name);
		final TextView ageView = (TextView) findViewById(R.id.main_age);
		
		setDrawableFace4Main(face);
		String name = "";
		String age = "";
		
		if(userId.equals(UserEditActivity.USER_1_KEY)) {
			name = pref.getString(UserEditActivity.USER_1_NAME_VALUE, getString(R.string.main_dad));
			age = pref.getString(UserEditActivity.USER_1_AGE_VALUE, "0");
			txtView.setText(name);
			ageView.setText(age);
		}else {
			name = pref.getString(UserEditActivity.USER_2_NAME_VALUE, getString(R.string.main_mom));
			age = pref.getString(UserEditActivity.USER_2_AGE_VALUE, "0");
			txtView.setText(name);
			ageView.setText(age);
		}
		
		handler = new BusinessHandler(this);// 初始化业务处理对象
		Cursor cursor = handler.getRecordCursor();
		final ImageView imgView = (ImageView) findViewById(R.id.home_status);
		final TextView highTxt = (TextView) findViewById(R.id.main_high);
		final TextView lowTxt = (TextView) findViewById(R.id.main_low);
		final TextView beatTxt = (TextView) findViewById(R.id.main_beat);
		if(cursor.getCount()>0) {
			cursor.moveToPosition(0);
			highTxt.setText(cursor.getInt(5) + "");
			lowTxt.setText(cursor.getInt(6) + "");
			beatTxt.setText(cursor.getInt(7) + "");
			imgView.setVisibility(0);
			if(cursor.getInt(5)<135) {
				imgView.setImageResource(R.drawable.health_good);
			}else if(cursor.getInt(5)>135 && cursor.getInt(5)<161) {
				imgView.setImageResource(R.drawable.health_light);
			}else if(cursor.getInt(5)>160 && cursor.getInt(5)<180) {
				imgView.setImageResource(R.drawable.health_middle);
			}else {
				imgView.setImageResource(R.drawable.health_heavy);
			}
		} else {
			highTxt.setText("0");
			lowTxt.setText("0");
			beatTxt.setText("0");
			imgView.setVisibility(4);
		}
	}
	
	/**
	 * Fill main page's face picture
	 * @return
	 */
	public void setDrawableFace4Main(ImageView face) {
		
		String path = "";
		if(pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1_KEY).equals(UserEditActivity.USER_1_KEY)) {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.DAD_IMAGE_FILE_NAME;
			File f = new File(path);
			if (f.exists()) {
				getPicFromSDCard(path);
			}else {
				face.setImageDrawable(getResources().getDrawable(R.drawable.pic_dad));
			}
		} else if(pref.getString(UserEditActivity.CURRENT_USER_ID, UserEditActivity.USER_1_KEY).equals(UserEditActivity.USER_2_KEY)) {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.MOM_IMAGE_FILE_NAME;
			File f = new File(path);
			if (f.exists()) {
				getPicFromSDCard(path);
			}else {
				face.setImageDrawable(getResources().getDrawable(R.drawable.pic_mom));
			}
		}
		
	}
	
	public void getPicFromSDCard(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			bitmap  = BitmapFactory.decodeStream(fis);
			face.setImageBitmap(ViewUtil.getRoundedCornerBitmap(bitmap, 100, bitmap.getWidth(), bitmap.getHeight()));
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 // TODO Auto-generated method stub  
	    if(keyCode == KeyEvent.KEYCODE_BACK)  
	       {    
	           exitBy2Click();      //调用双击退出函数  
	       }  
	    return false;  
	}
	
	
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	        finish();  
	        System.exit(0);  
	    }
	}  
}
