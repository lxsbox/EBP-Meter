package com.tech37c.ebpmeter.contorller;

import com.amour.ebpmeter.R;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.BusinessHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class ShowAllDataActivity extends Activity {
	protected TextView allData;
	protected TextView lastID;
	protected BaseDAO dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_all_data);
		
		allData = (TextView) findViewById(R.id.all_data);
		lastID = (TextView) findViewById(R.id.last_id);
		dao = new BaseDAO(this);
		
		Cursor cursor = dao.all(this);
		StringBuffer sb = new StringBuffer();
		while(cursor.moveToNext()) {
			sb.append(cursor.getString(0) + "  "+ cursor.getString(1) + "  " + cursor.getString(2)  + "  " + cursor.getInt(3) + "  " + cursor.getInt(4) + "  " + cursor.getInt(5) + "\r\n");
		}
		sb.toString();
		allData.setText(sb);
		
		SharedPreferences sharedPreferences = getSharedPreferences(BusinessHandler.SHARED_PREFS_NAME, MODE_PRIVATE);
	    int idInPref = sharedPreferences.getInt(BusinessHandler.lAST_RECORD_ID_SHARED_PREF, 0);
	    lastID.setText(idInPref+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_all_data, menu);
		return true;
	}

}
