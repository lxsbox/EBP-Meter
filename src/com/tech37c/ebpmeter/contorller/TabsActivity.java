package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class TabsActivity extends TabActivity {
	TabHost tabHost;
	private SharedPreferences pref;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_tabs);
		tabHost = getTabHost();
		setTabs();
	}

	private void setTabs() {
		addTab("记录", R.drawable.tab_list, RecordsActivity.class);
		addTab("提醒", R.drawable.tab_reminder, ReminderActivity.class);
		addTab("设置", R.drawable.tab_setting, SettingActivity.class);
	}

	private void addTab(String labelId, int drawableId, Class<?> c) {
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

}
