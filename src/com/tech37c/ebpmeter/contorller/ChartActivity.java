package com.tech37c.ebpmeter.contorller;


import org.eazegraph.lib.charts.ValueLineChart;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.fragment.BarChartFragment;
import com.tech37c.ebpmeter.contorller.fragment.CubicValueLineChartFragment;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ChartActivity extends FragmentActivity implements OnCheckedChangeListener {
	private ValueLineChart chart;
	private ValueLineChart chart2;
//	private CheckBox cb10;
	private CheckBox cb30;
//	private CheckBox cbAll;
	SegmentedRadioGroup segmentText;
	
	private ChartFragment mCurrentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
		setContentView(R.layout.activity_chart);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_chart_title);//Title
//		cb10 = (CheckBox)findViewById(R.id.tenDays);
//		cb30 = (CheckBox)findViewById(R.id.thirtyDays);
//		cbAll = (CheckBox)findViewById(R.id.allDays);
		segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
        segmentText.setOnCheckedChangeListener(this);
        onCheckedChanged(segmentText, R.id.button_one);
//		onCheckBoxSelected(0);
//		cb10.setOnClickListener(new Button.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				cbAll.setChecked(false);
//				onCheckBoxSelected(10);
//			}
//		});
//		cbAll.setOnClickListener(new Button.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				cb10.setChecked(false);
//				onCheckBoxSelected(365);
//			}
//		});
//		cbAll.setOnClickListener(new Button.OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				cb10.setChecked(false);
//				cb30.setChecked(false);
//			}
//		});
		
		
		ImageButton backBt = (ImageButton)findViewById(R.id.back_rcrds);
		backBt.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(), RecordsActivity.class);
//    			startActivity(intent);
				finish();
			}
		});
		
		
//		chart = (ValueLineChart) findViewById(R.id.cubiclinechart);
//		chart2 = (ValueLineChart) findViewById(R.id.cubiclinechart2);
//		loadData();
	}

	
//    public void onCheckBoxSelected(int type) {
//        // update the main content by replacing fragments
//        switch (type) {
//            case 10:
//                mCurrentFragment = new BarChartFragment();
//                break;
//            case 30:
//                mCurrentFragment = new CubicValueLineChartFragment();
//                break;
//            case 365:
//                mCurrentFragment = new CubicValueLineChartFragment();
//                break;
//            default:
//                mCurrentFragment = new BarChartFragment();
//                break;
//        }
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.chart_container, mCurrentFragment).commit();
//    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart, menu);
		return true;
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == segmentText) {
			if (checkedId == R.id.button_one) {
				mCurrentFragment = new BarChartFragment();
			} else if (checkedId == R.id.button_two) {
				mCurrentFragment = new CubicValueLineChartFragment();
			} else if (checkedId == R.id.button_three) {
				mCurrentFragment = new CubicValueLineChartFragment();
			}
			FragmentManager fragmentManager = getSupportFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.chart_container, mCurrentFragment).commit();
		}
	}
}
