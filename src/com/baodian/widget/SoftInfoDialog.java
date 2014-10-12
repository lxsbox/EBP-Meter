package com.baodian.widget;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.UserEditActivity;
import com.tech37c.ebpmeter.service.BackgroundService;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author user
 */
public class SoftInfoDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Window window = null;
	private Context context = null;
	public Button softInfo = null;
	public Button softInfoButton = null;
	public static boolean flag = true;
	private NumericWheelAdapter year_adapter = null;
	private NumericWheelAdapter month_adapter = null;
	private Button btn_sure = null;
	private Button btn_cancel = null;
	private int age;
	private TextView age_text;
	private String userFlag;
	private Calendar c;

	public SoftInfoDialog(final Context context, TextView textView, String flag) {
		super(context, R.style.dialog);
		this.context = context;
		this.age_text = textView;
		this.userFlag = flag;
		setContentView(R.layout.time_layout);
		btn_sure = (Button) findViewById(R.id.lay_left);
//		btn_cancel = (Button) findViewById(R.id.lay_right);
		c = Calendar.getInstance();
		btn_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String year = year_adapter.getValues();
				int curYear = c.get(Calendar.YEAR);
//				String month = month_adapter.getValues();
				int age = curYear>Integer.parseInt(year)?curYear-Integer.parseInt(year):55;
				SharedPreferences.Editor editor = context.getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, 0).edit();
				if(userFlag.equals(UserEditActivity.USER_1_KEY)) {
					editor.putString(UserEditActivity.USER_1_AGE_VALUE, ""+age);
				}else {
					editor.putString(UserEditActivity.USER_2_AGE_VALUE, ""+age);
				}
				editor.commit();
				
    			age_text.setText(age+"");
				dismiss();
			}
		});

//		btn_cancel.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});

		WheelView year = (WheelView) this.findViewById(R.id.year);
		year.setLabel("年");
		WheelView month = (WheelView) this.findViewById(R.id.month);
		month.setLabel("月");
		year_adapter = new NumericWheelAdapter(1, 2100);
		month_adapter = new NumericWheelAdapter(1, 12);

		int curMonth = c.get(Calendar.MONTH);
		year.setAdapter(year_adapter);
		year.setCurrentItem(1960);
		month.setAdapter(month_adapter);
		month.setCurrentItem(6);

		Display d = getWindow().getWindowManager().getDefaultDisplay();
		window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x =  0;
		wl.y = (int) (d.getHeight() * 0.7);
		wl.height = (int) (d.getHeight() * 0.3);
		wl.width = (int) (d.getWidth()* 0.7);
		wl.alpha = 1.0f;
		window.setAttributes(wl);
		show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
}
