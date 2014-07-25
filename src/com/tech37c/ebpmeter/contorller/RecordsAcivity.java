/**
 * 
 */
package com.tech37c.ebpmeter.contorller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.ProtoUtil;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author allin
 * 
 */
public class RecordsAcivity extends ListActivity {


	private List<Map<String, Object>> mData;
	private BusinessHandler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_records);
		
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, 0);
		String name = pref.getString(UserEditActivity.CURRENT_USER_ID, "").equals(UserEditActivity.USER_1)?//获取当前用户名
				pref.getString(UserEditActivity.DAD, ""):pref.getString(UserEditActivity.MOM, "");
		final TextView txtView = (TextView)findViewById(R.id.current_user222);
		txtView.setText(name + "的血压记录");
		
		final ImageButton chartBtn = (ImageButton)findViewById(R.id.chart_on_title);
		chartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ChartActivity.class);
				startActivity(intent);
			}
			
		});
		
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		handler = new BusinessHandler(this);// 初始化业务处理对象
		RecordPOJO record = handler.initMainView();
		Cursor cursor = handler.getRecordCursor();
		while(cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", cursor.getInt(0));
			map.put("time", cursor.getString(4));
			map.put("high", cursor.getInt(5) + "");
			map.put("low", cursor.getInt(6) + "");
			list.add(map);
		}
		return list;
	}
	
	// ListView 中某项被选中后的逻辑
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
//		Log.v("MyListView4-click", (String)mData.get(position).get("title"));
	}
	
	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo(){
		Intent callIntent = new Intent(Intent.ACTION_DIAL);
		startActivity(callIntent);
	}
	
	
	
	public final class ViewHolder{
		public TextView time;
		public TextView high;
		public TextView low;
		public ImageButton viewBtn;
		public LinearLayout layout;
	}
	
	
	public class MyAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private Context context;
		
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int lastId = (Integer)mData.get(position).get("id");
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder=new ViewHolder();
				
				convertView = mInflater.inflate(R.layout.activity_records_line, null);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				holder.high = (TextView)convertView.findViewById(R.id.high);
				holder.low = (TextView)convertView.findViewById(R.id.low);
				holder.viewBtn = (ImageButton)convertView.findViewById(R.id.view_btn);
				System.out.println(holder.viewBtn);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.layoutId);
				
				System.out.println(getCount());
				//首条换样式
				if(lastId==getCount()) {
					holder.time.setTextColor(Color.parseColor("#ffffff"));
				}else {
					
					holder.time.setTextColor(Color.parseColor("#000000"));
				}
//				holder.time.setTextColor(lastId==getCount()?Color.parseColor("#ffffff"):Color.parseColor("#000000"));
				holder.high.setTextColor(lastId==getCount()?Color.parseColor("#ffffff"):Color.parseColor("#000000"));
				holder.low.setTextColor(lastId==getCount()?Color.parseColor("#ffffff"):Color.parseColor("#000000"));
				holder.viewBtn.setVisibility(lastId==getCount()?0:4);//0:visible
				holder.layout.setBackgroundColor(lastId==getCount()?Color.parseColor("#FF8000"):Color.parseColor("#ffffff"));
				
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			String origTime = (String)mData.get(position).get("time");
			holder.time.setText(ProtoUtil.getEasyTime(origTime));
			holder.high.setText((String)mData.get(position).get("high"));
			holder.low.setText((String)mData.get(position).get("low"));
			
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showInfo();					
				}
			});
			
			
			return convertView;
		}
		
	}
	
	
	
	
}
