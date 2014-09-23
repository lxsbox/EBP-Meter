package com.tech37c.ebpmeter.contorller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


import com.example.actionbaraddui.SearchDevicesView;
import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.R.layout;
import com.tech37c.ebpmeter.R.menu;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.TypeTrans;
import com.tech37c.ebpmeter.utils.WifiUtil;

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
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SearchingActivity extends Activity {
	public Handler hander;
	private Handler closeWifiHandler;
	private WifiManager wifiManager;
	public static  CloseWifiThread cwt;
	private StratWifiApThread swat;
	private static final int UDP_LISTEN_PORT = 18899;
	public static InetAddress outIP;
	public static int outPort;
	public static DatagramSocket waitDeviceSocket;
	public static boolean beConnected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_searching);
		SearchDevicesView searchDeviceview = (SearchDevicesView) findViewById(R.id.search_device_view);
		searchDeviceview.setWillNotDraw(false);
		startWifiAp();
		new SocketListenTask(getApplicationContext()).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searching, menu);
		return true;
	}

	private void startWifiAp() {
		hander = new Handler();
		wifiManager = (WifiManager) SearchingActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			closeWifiHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					startWifiApTh();
					super.handleMessage(msg);
				}
			};
			cwt = new CloseWifiThread();
			Thread thread = new Thread(cwt);
			thread.start();

		} else {
			startWifiApTh();
		}

	}

	class CloseWifiThread implements Runnable {
		public CloseWifiThread() {
			super();
		}

		@Override
		public void run() {
			int state = wifiManager.getWifiState();
			if (state == WifiManager.WIFI_STATE_ENABLED) {
				wifiManager.setWifiEnabled(false);
				closeWifiHandler.postDelayed(cwt, 1000);
			} else if (state == WifiManager.WIFI_STATE_DISABLING) {
				closeWifiHandler.postDelayed(cwt, 1000);
			} else if (state == WifiManager.WIFI_STATE_DISABLED) {
				closeWifiHandler.sendEmptyMessage(0);
			}
		}
	}

	private void startWifiApTh() {
		swat = new StratWifiApThread();
		Thread thread = new Thread(swat);
		thread.start();
	}

	
	class StratWifiApThread implements Runnable {
		Handler handler2;

		public StratWifiApThread() {
			super();
			handler2 = new Handler();
		}

		public void run() {
			WifiUtil mWifiUtil = new WifiUtil();
			int state = mWifiUtil.getWifiApState(wifiManager);
			if (state == WifiUtil.WIFI_AP_STATE_DISABLED
					||state == WifiUtil.WIFI_AP_STATE_DISABLED_HIGH_SDK//Android 4.1 Samsung galaxy note2
					) {
				mWifiUtil.stratWifiAp(wifiManager);
				handler2.postDelayed(swat, 1000);
			} else if (state == WifiUtil.WIFI_AP_STATE_ENABLING
					|| state == WifiUtil.WIFI_AP_STATE_FAILED
					||state == WifiUtil.WIFI_AP_STATE_DISABLING_HIGH_SDK//Android 4.1 Samsung galaxy note2
					||state == WifiUtil.WIFI_AP_STATE_FAILED_HIGH_SDK//Android 4.1 Samsung galaxy note2
					) {
				handler2.postDelayed(swat, 1000);
			} else if (state == WifiUtil.WIFI_AP_STATE_ENABLED
					||state == WifiUtil.WIFI_AP_STATE_ENABLED_HIGH_SDK//Android 4.1 Samsung galaxy note2
					) {
				// Toast.makeText(wifidemo.this, "已开启wlan热点", 2000).show();
			}
		}

	}

	/**
	 * Socket Listen Thread
	 * 
	 */
	private class SocketListenTask extends AsyncTask<String, String, Map> {
		private Context mContext;// 外面类的上下文
		private Exception exception;
		private ProgressBar xh_ProgressBar;
		private String devType;
		private String devId;

		public SocketListenTask(Context context) {
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Map doInBackground(String... typeId) {
			return listenDevice();
		}

		@Override
		protected void onPostExecute(Map map) {
			super.onPostExecute(map);
			if (null != map) {
				Intent intent = new Intent(mContext, WifiInputActivity.class);
				intent.putExtra("sName", (String)map.get("sName"));
				intent.putExtra("sPassword", (String)map.get("sPassword"));
    			startActivity(intent);
    			finish();
			}
		}
	}

	public Map listenDevice() {
		Map map = new HashMap();
		try {
			waitDeviceSocket = new DatagramSocket(UDP_LISTEN_PORT);// 开启监听
		} catch (SocketException se) {
			se.printStackTrace();
		}
		byte[] in = new byte[75];
		DatagramPacket inPacket = new DatagramPacket(in, 75);// 构造用来接收长度为length的数据包

		try {
			if(!beConnected) {
				waitDeviceSocket.receive(inPacket);
				beConnected = true;
			}else{
				return null;
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		char[] c = TypeTrans.getChars(in);
		if(null != in && in[0] == 0x25 && in[1] == 0x43 && in[2] == 0x19) {
			outIP = inPacket.getAddress();
			outPort = inPacket.getPort();
			byte[] bName = new byte[32];
			byte[] bPassword = new byte[32];
			bName = TypeTrans.subBytes(in, 10, 32);
			bPassword = TypeTrans.subBytes(in, 42, 32);
			String sName = "";
			String sPassword = "";
			try {
				sName = new String(bName, "UTF-8");
				sPassword = new String(bPassword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			map.put("sName", sName.trim());
			map.put("sPassword", sPassword.trim());
		}
		return map;
	}
}
