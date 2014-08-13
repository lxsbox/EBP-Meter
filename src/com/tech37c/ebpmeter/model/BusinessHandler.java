package com.tech37c.ebpmeter.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;

public class BusinessHandler extends Activity {
	private static String DATA_CENTER_IP = "54.200.144.55";
	private static int DATA_CENTER_UDP_PORT = 18899;
	public static final String SHARED_PREFS_NAME = "tech37c_ebpmeter_preferences";
	private BaseDAO dao;
	
	public BusinessHandler(Activity activity) {
		this.dao = new BaseDAO(activity);
	}
	
	/**
	 * 初始化主界面
	 * @return
	 */
	public RecordPOJO initMainView() {
		//插入测试数据
		int count = (int)dao.count();
		if (count == 0) {
			for (int i=0; i<5; i++) {
				dao.insert("1", "11", "1", "2014-07-"+ i +" 20:00:"+ i, "135", "85", "75");
			}
		}
		
		Cursor cursor = dao.all();
		RecordPOJO record = new RecordPOJO();
		if (cursor.moveToLast()) {
			record.setUser_ID(cursor.getInt(3)+"");
			record.setMeasure_Time(cursor.getString(4));
			record.setHBP(cursor.getInt(5) + "");
			record.setLBP(cursor.getInt(6) + "");
			record.setBeat(cursor.getInt(7) + "");
		}
		System.out.println(cursor.getCount());
		return record;
	}
	
	public Cursor getRecordCursor() {
		return dao.all();
	}
	
	/**
	 * 查询10天的血压曲线
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List queryByDay(int num) {
		List list = new ArrayList<LinkedHashMap>();
		Map high = new LinkedHashMap<String, Integer>();
		Map low = new LinkedHashMap<String, Integer>();
		Cursor cursor = dao.all("ASC");
		int idx = 0;
		while(cursor.moveToNext()) {
			idx ++;
			if(idx>num)  break;
			high.put(cursor.getString(4), cursor.getInt(5));
			low.put(cursor.getString(4), cursor.getInt(6));
		}
		list.add(high);
		list.add(low);
		return list;
	}
	
}
