package com.tech37c.ebpmeter.contorller;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 程序入口
 * 
 * @author ShawnLi
 * 
 */
public class MainActivity extends FragmentActivity  implements  CompatActionBarNavListener, OnClickListener,
											       OnItemSelectedListener {
	protected TextView meterUser;
	protected TextView checkingTime;
	protected TextView highValue;
	protected TextView lowValue;
	protected TextView heartBeat;
	protected Button giveCall;
	protected Button giveVideo;
	// 分页下相关参数
	private static int NUM_PAGES;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private BusinessHandler handler;
	final String CATEGORIES[] = { "Top Stories", "Politics", "Economy", "Technology" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_main_title);
		final TextView txtView  = (TextView)findViewById(R.id.current_user);
		
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		String userFlag = pref.getString(UserEditActivity.CURRENT_USER_ID, "");
		String name = "";
		if (userFlag.equals("1")) {
			name = pref.getString(UserEditActivity.DAD, "");
		} else {
			name = pref.getString(UserEditActivity.MOM, "");
		}
		txtView.setText(name+"的血压记录");
		
		handler = new BusinessHandler(this);//初始化业务处理对象
		RecordPOJO record = handler.initMainView();
		// NUM_PAGES = handler.getRecordNum();
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

		
		 setUpActionBar();
		// 连接界面元素和属性
		// meterUser = (TextView) findViewById(R.id.meter_user);
		// checkingTime = (TextView) findViewById(R.id.checking_time);
		// highValue = (TextView) findViewById(R.id.high_value);
		// lowValue = (TextView) findViewById(R.id.low_value);
		// heartBeat = (TextView) findViewById(R.id.heart_beat);
		// giveCall = (Button) findViewById(R.id.give_call);
		// giveVideo = (Button) findViewById(R.id.give_video);
		//
		// //设置监听事件
		// giveCall.setOnClickListener(this);
		// giveVideo.setOnClickListener(this);
		// BusinessHandler handler = new BusinessHandler(this);//初始化业务处理对象
		// RecordPOJO record = handler.initMainView();
		// meterUser.setText(record.getUser_ID());
		// checkingTime.setText(record.getMeasure_Time());
		// highValue.setText(record.getHBP());
		// lowValue.setText(record.getLBP());
		// heartBeat.setText(record.getBeat());
	}

	
	/** Sets up Action Bar (if present).
    *
    * @param showTabs whether to show tabs (if false, will show list).
    * @param selTab the selected tab or list item.
    */
   public void setUpActionBar() {
       if (Build.VERSION.SDK_INT < 11) {
           // No action bar for you!
           // But do not despair. In this case the layout includes a bar across the
           // top that looks and feels like an action bar, but is made up of regular views.
           return;
       }
       ActionBar actionBar = getActionBar();
//       actionBar.setDisplayShowTitleEnabled(false);
       // Set up a CompatActionBarNavHandler to deliver us the Action Bar nagivation events
       CompatActionBarNavHandler handler = new CompatActionBarNavHandler(this);
       actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
       SpinnerAdapter adap = new ArrayAdapter<String>(this, R.layout.activity_main_bar_list, CATEGORIES);
       actionBar.setListNavigationCallbacks(adap, handler);
       // Show logo instead of icon+title.
       actionBar.setDisplayUseLogoEnabled(true);
   }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);

//		menu.findItem(R.id.action_previous).setEnabled(
//				mPager.getCurrentItem() > 0);
//
//		// Add either a "next" or "finish" button to the action bar, depending
//		// on which page
//		// is currently selected.
//		MenuItem item = menu
//				.add(Menu.NONE,
//						R.id.action_next,
//						Menu.NONE,
//						(mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) ? R.string.action_finish
//								: R.string.action_next);
//		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
//				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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
		
//		 switch(item.getItemId()) {
//		 case R.id.add_record:
//		 Intent intent = new Intent(this, AddRecordActivity.class);
//		 startActivity(intent);
//		 return true;
//		 case R.id.show_chart:
//		 Intent chartIntent = new Intent(this, ChartActivity.class);
//		 startActivity(chartIntent);
//		 return true;
//		 case R.id.show_all_data:
//		 Intent sdIntent = new Intent(this, ShowAllDataActivity.class);
//		 startActivity(sdIntent);
//		 return true;
//		 default:
//		 return super.onOptionsItemSelected(item);
//		 }
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
