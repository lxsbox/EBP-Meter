package com.tech37c.ebpmeter.contorller;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;
import com.tech37c.ebpmeter.contorller.SearchingActivity.CloseWifiThread;
import com.tech37c.ebpmeter.model.EmptyByte;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.ProtoUtil;
import com.tech37c.ebpmeter.utils.TypeTrans;
import com.tech37c.ebpmeter.utils.WifiUtil;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WifiInputActivity extends Activity {
	private EditText wifiName;
	private EditText wifiPassword;
	private WifiManager wifiManager;
	private CloseWifiThread cwt;
	private Handler closeWifiHandler;
	public static final int m_nWTSearchTimeOut = 0;// 搜索超时
	private WTSearchProcess m_wtSearchProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifi_input);
		wifiName = (EditText) findViewById(R.id.wifi_name);
		wifiPassword = (EditText) findViewById(R.id.wifi_password);
		ImageButton okWifi = (ImageButton) findViewById(R.id.ok_wifi);
		Intent myIntent = getIntent();
		
		if (myIntent != null) {
			String sName = (String)myIntent.getExtras().get("sName");
			String sPassword = (String)myIntent.getExtras().get("sPassword");
			wifiName.setText(sName);
			wifiPassword.setText(sPassword);
		}
		
		okWifi.setOnClickListener(new Button.OnClickListener(){
    		public void onClick (View v){
    			String name = wifiName.getText().toString();
    	    	String password = wifiPassword.getText().toString();
    			String[] namePassword = {name, password};
    			new WriteWifiTask(v.getContext()).execute(namePassword);
    		}
    	});
		
		m_wtSearchProcess = new WTSearchProcess();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_input, menu);
		return true;
	}

	
	/**
	 * Write wifi name and password to device
	 *
	 */
	private class WriteWifiTask extends AsyncTask<String, String, byte[]> {
		private Context mContext;//外面类的上下文
		private Exception exception;
	    private ProgressBar xh_ProgressBar;
	    private String name;
	    private String password;
	    
	    
	    public WriteWifiTask (Context context){
	         mContext = context;
	    }
	    
	    @Override
		protected void onPreExecute() {
	    	xh_ProgressBar = (ProgressBar)findViewById(R.id.write_wifi_progressBar);
			xh_ProgressBar.setVisibility(View.VISIBLE);
		}
	    
		@Override
		protected byte[] doInBackground(String... typeId) {
			this.name = typeId[0]; 
		    this.password = typeId[1];
			return writeDevice(typeId[0], typeId[1]);
		}
		
		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			if (result[10]==0) {
				/*Resume previous WIFI*/
				WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				WifiUtil wifiUtil = new WifiUtil();
				wifiUtil.createWiFiAP(wifiManager, wifiUtil.createWifiInfo(),false);
				wifiUtil.OpenWifi(wifiManager);
				m_wtSearchProcess.start();
				wifiUtil.startScan(wifiManager);
				/*Set device is match flag*/
				SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
    	    	Editor edit = pref.edit();
    	    	edit.putString(RegisterActivity.DEVICE_IS_MATCH, "true");
				
				xh_ProgressBar.setVisibility(View.GONE);
    	    	Intent intent = new Intent(mContext, MainActivity.class);
    			startActivity(intent);
			}else {
				xh_ProgressBar.setVisibility(View.GONE);
				Toast.makeText(mContext, getString(R.string.set_wifi_failed, ""), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	public byte[] writeDevice(String name, String password) {
			DatagramSocket socket = SearchingActivity.waitDeviceSocket;
			try {
				byte[] out = new byte[81];
				out[0] = 0x25;
				out[1] = 0x43;
				out[2] = 0x1A;
				out[5] = 0x0C;
				
				byte[] bName = name.getBytes();
				int j = 10;
				for(int i=0; i<bName.length; i++) {
						out[j] = bName[i];
						j++;
				}
				
				byte[] bPassword = password.getBytes();
				int k = 42;
				for(int i=0; i<bPassword.length; i++) {
						out[k] = bPassword[i];
						k++;
				}
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				byte[] bDateTime = ProtoUtil.time2Byte(df.format(new Date()));
				int L = 74;
				for(int i=0; i<bDateTime.length; i++) {
						out[L] = bDateTime[i];
						L++;
				}
				
				DatagramPacket outPacket = new DatagramPacket(out,81,SearchingActivity.outIP, SearchingActivity.outPort);// 包裹
				socket.send(outPacket);// 发送报文
			} catch (SocketException e1) {
				e1.printStackTrace();
			}catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] in = new byte[12];
			DatagramPacket inPacket = new DatagramPacket(in, 12);// 构造用来接收长度为length的数据包
				try {
					while(true) {
						socket.receive(inPacket);
						if(null != in
								&& in[0] == 0x25 
								&& in[1] == 0x43 
								&& in[2] == 0x1B)
							break;
					}
				} catch (InterruptedIOException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			return in;
	}
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case m_nWTSearchTimeOut:
				m_wtSearchProcess.stop();
				break;
			default:
				break;
			}
		}
	};
	
	
	class WTSearchProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		WTSearchProcess() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime >= 30000L) {
					Message msg = handler.obtainMessage(m_nWTSearchTimeOut);
					handler.sendMessage(msg);
				}
				try {
					Thread.sleep(10L);
				} catch (Exception localException) {
				}
			}
		}

		public void start() {
			try {
				this.thread = new Thread(this);
				this.running = true;
				this.startTime = System.currentTimeMillis();
				this.thread.start();
			} finally {
			}
		}

		public void stop() {
			try {
				this.running = false;
				this.thread = null;
				this.startTime = 0L;
			} finally {
			}
		}
	}
}
