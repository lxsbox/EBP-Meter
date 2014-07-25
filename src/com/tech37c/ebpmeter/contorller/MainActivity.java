package com.tech37c.ebpmeter.contorller;

import java.util.ArrayList;
import java.util.List;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
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
public class MainActivity extends FragmentActivity implements
		CompatActionBarNavListener, OnClickListener, OnItemSelectedListener {
	private Context mContext;
	protected TextView meterUser;
	protected TextView checkingTime;
	protected TextView highValue;
	protected TextView lowValue;
	protected TextView heartBeat;
	protected Button giveCall;
	protected Button giveVideo;
	private Spinner mActionbarSpinner;
	ImageButton button1;
	// 分页下相关参数
	private static int NUM_PAGES;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private BusinessHandler handler;
	private SharedPreferences pref;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏
		
		setContentView(R.layout.activity_main);
		
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_main_title);
//		pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
//		String name = pref.getString(UserEditActivity.CURRENT_USER_ID, "").equals(UserEditActivity.USER_1)?//获取当前用户名
//				pref.getString(UserEditActivity.DAD, ""):pref.getString(UserEditActivity.MOM, "");
//		final TextView txtView = (TextView) findViewById(R.id.user_In_content);
//		txtView.setText(name + "的血压记录");

		handler = new BusinessHandler(this);// 初始化业务处理对象
		RecordPOJO record = handler.initMainView();
//		 NUM_PAGES = handler.getRecordNum();
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When changing pages, reset the action bar actions since they
				// are dependent
				// on which page is currently active. An alternative approach is
				// to have each
				// fragment expose actions itself (rather than the activity
				// exposing actions),
				// but for simplicity, the activity provides the actions in this
				// sample.
				invalidateOptionsMenu();
			}
		});

//		button1 = (ImageButton) findViewById(R.id.button1);
//		button1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mContext = v.getContext();
//				PopupMenu popup = new PopupMenu(MainActivity.this, button1);
//				popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());
//				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//					public boolean onMenuItemClick(MenuItem item) {
////						Toast.makeText(MainActivity.this, "You Clicked : " + item.getTitle(),
////						Toast.LENGTH_SHORT).show();
//						boolean isChangeItem = true;
//						String itemId = (String)item.getTitle();
//						isChangeItem = itemId.equals(getResources().getString(R.string.change_user))?true:false;
//						if(isChangeItem) {
//							String cur = pref.getString(UserEditActivity.CURRENT_USER_ID, "");
//							
//							String perparedId = cur.equals(UserEditActivity.USER_1)?UserEditActivity.USER_2 : UserEditActivity.USER_1;
//									
//							Editor edit = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
//							edit.putString(UserEditActivity.CURRENT_USER_ID, perparedId);
//							edit.commit();
//							Intent intent = new Intent(mContext, MainActivity.class);
//							startActivity(intent);
//						}
//						return true;
//					}
//				});
//				popup.show();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Navigate "up" the demo structure to the launchpad activity.
			// See http://developer.android.com/design/patterns/navigation.html
			// for more.
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;
		case R.id.action_previous:
			// Go to the previous step in the wizard. If there is no previous
			// step,
			// setCurrentItem will do nothing.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			return true;
		case R.id.action_next:
			// Advance to the next step in the wizard. If there is no next step,
			// setCurrentItem
			// will do nothing.
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);

		case R.id.add_record:
			Intent intent = new Intent(this, AddRecordActivity.class);
			startActivity(intent);
			return true;
		case R.id.show_chart:
			Intent chartIntent = new Intent(this, ChartActivity.class);
			startActivity(chartIntent);
			return true;
		case R.id.show_all_data:
			Intent sdIntent = new Intent(this, ShowAllDataActivity.class);
			startActivity(sdIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
	 * objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		private Cursor cursor;

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			this.cursor = handler.getRecordCursor();
			RecordPOJO record = new RecordPOJO();
			if (cursor.getCount() > 0
					&& cursor.moveToPosition(cursor.getCount() - 1 - position)) {// cursor的最后一个
				record.setUser_ID(cursor.getInt(3) + "");
				record.setMeasure_Time(cursor.getString(4));
				record.setHBP(cursor.getInt(5) + "");
				record.setLBP(cursor.getInt(6) + "");
				record.setBeat(cursor.getInt(7) + "");
			}
			return ScreenSlidePageFragment.create(position, record);
		}

		@Override
		public int getCount() {
			this.cursor = handler.getRecordCursor();
			NUM_PAGES = cursor.getCount();
			return NUM_PAGES;
		}
	}

	@Override
	public void onCategorySelected(int catIndex) {

	}
}
