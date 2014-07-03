package com.tech37c.ebpmeter.contorller;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.utils.TypeTrans;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddRecordActivity extends Activity {
	protected EditText userName;
	
	protected EditText date;
	protected EditText time;
	
	protected EditText high;
	protected EditText low;
	protected EditText beat;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_record);
		
		userName = (EditText)findViewById(R.id.user_name);
		
		date = (EditText)findViewById(R.id.date);
		time = (EditText)findViewById(R.id.time);
		
		high = (EditText)findViewById(R.id.high);
		low = (EditText)findViewById(R.id.low);
		beat = (EditText)findViewById(R.id.beat);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_record, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	      case R.id.save_record:
	        if(saveRecord()) {
	          Toast.makeText(this, getString(R.string.save_record_success, userName.getText().toString()), Toast.LENGTH_SHORT).show();
	        }
	          
	      default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public boolean saveRecord() {
		String userNameText = userName.getText().toString();
		
		String dateText = date.getText().toString();
		String timeText = time.getText().toString();
		
		String highText = high.getText().toString();
		String lowText = low.getText().toString();
		String beatText = beat.getText().toString();
		
		
		if(TextUtils.isEmpty(userNameText.trim()) || TextUtils.isEmpty(dateText.trim()) 
				|| TextUtils.isEmpty(timeText.trim()) || TextUtils.isEmpty(highText.trim())
				|| TextUtils.isEmpty(lowText.trim()) || TextUtils.isEmpty(beatText.trim())) {
			
			Toast.makeText(this, getString(R.string.save_record_fail, userName.getText().toString()), Toast.LENGTH_SHORT).show();
			return false;
		} else {
			BaseDAO dao = new BaseDAO(this);
			long lastId = dao.insert("1", "32768", userNameText, dateText+" "+timeText, highText, lowText, beatText);
			dao.close();
			
//			SharedPreferences.Editor editor = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
//		    editor.putInt(MainActivity.lAST_RECORD_ID_SHARED_PREF, TypeTrans.safeLongToInt(lastId));
			return true;
		}
	}
}
