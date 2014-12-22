package com.tech37c.ebpmeter.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.MainActivity;
import com.tech37c.ebpmeter.contorller.RecordsActivity;
import com.tech37c.ebpmeter.contorller.RegisterActivity;
import com.tech37c.ebpmeter.contorller.SettingActivity;
import com.tech37c.ebpmeter.contorller.TabsActivity;
import com.tech37c.ebpmeter.contorller.UserEditActivity;
import com.tech37c.ebpmeter.contorller.WelcomeActivity;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.EmptyByte;
import com.tech37c.ebpmeter.model.RecordPOJO;
import com.tech37c.ebpmeter.utils.ProtoUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 后台服务类
 * 
 * @author Code Play
 * 
 */
public class BackgroundService extends Service {
	
	public static int DATA_CENTER_UDP_PORT = 6007;
	public static String DATA_CENTER_IP = "114.215.146.49";
	public static int TIME_OUT = 5000;
	public static int MAXTRIES = 3;
	
	public static final String SHARED_PREFS_NAME = "tech37c_ebpmeter_preferences";
	public static final String POP_UP_TIME = "pop_up_time";
	public static final String POP_UP_HIGH = "pop_up_high";
	public static final String POP_UP_LOW = "pop_up_low";
	public static final String POP_UP_BEAT = "pop_up_beat";
	public static final String LATEST_RECORD_TIME = "latest_record_time";
	public static final String DEV_TYPE = "dev_type";//设备类型（血压计）
	public static final String DEV_ID = "dev_id";//设备ID（血压计）
	public static final String APP_VERSION = "app_version";
	public static final String FORGET_THE_PILL = "forget_the_pill";
	
	private HeartBeatThread heartBeatThread;
	private PopUpThread popUpThread;
	public static final String ACTION = "com.tech37c.ebpmeter.service.BackgroundService";
	private Intent messageIntent;
	private PendingIntent messagePendingIntent;
	private int messageNotificationID = 1000;
	private Notification notification;
	private NotificationManager notificatioManager;
	private BaseDAO dao;
	public DatagramSocket backgroudSocket;//BackGround Socket, Must be open
	
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		dao = new BaseDAO(this);
		notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.tickerText = "新消息";
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动清除
		notificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		messageIntent = new Intent(this, TabsActivity.class);

		messagePendingIntent = PendingIntent.getActivity(this, 0,
												messageIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		try {
			backgroudSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		heartBeatThread = new HeartBeatThread();
		heartBeatThread.start();
		
		// 提示线程
		popUpThread = new PopUpThread();
		popUpThread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	class HeartBeatThread extends Thread {
		public void run() {
			while (true) {
//				reportHeart();
			}
		}
	}
	class PopUpThread extends Thread {
		public void run() {
			while (true) {
				checkPopUp();
			}
		}
	}
	
	/**
	 * Reporting The Heart Beat
	 */
	public void reportHeart() {
		try {
			EmptyByte eb = new EmptyByte(25);
			byte[] out = eb.getSbyte();
			out[0] = 0x25;
			out[1] = 0x43;
			out[2] = 0x07;//报文类型
			out[3] = 25;//有效数据区长度2
			out[5] = 0x0C;//源类型（安卓）
			out[6] = 0x0A;//宿类型
			TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			this.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			byte[] bIme = imei.getBytes();
			int j=10;
			for(int i=0; i<bIme.length; i++) {
				out[j] = bIme[i];
				j++;
			}
			InetAddress ip = InetAddress.getByName(DATA_CENTER_IP);
			DatagramPacket outPacket = new DatagramPacket(out, 25, ip, DATA_CENTER_UDP_PORT);
			backgroudSocket.send(outPacket);//发送报文
			Thread.sleep(10000);//10秒一次心跳
			System.out.println("--- --- --- I'm alive --- --- --- ");
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] receiveMsg() {
		byte[] in = new byte[24];
		DatagramPacket inPacket = new DatagramPacket(in, 24);
		
		try {
			backgroudSocket.setSoTimeout(3000);
			backgroudSocket.receive(inPacket);
			if (!inPacket.getAddress().toString().equals("/"+DATA_CENTER_IP)) {
				throw new IOException("Received packet from an unknown source");
			}
		}catch (SocketException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	@SuppressWarnings("deprecation")
	public void checkPopUp() {
		byte[] bMsg = receiveMsg();
		
		if (bMsg[2] == 0x08) {
			System.out.println("--- --- Get A Record:" + bMsg);
			// 缺少本地库记录时间验证验证， 以保证数据不重复！！！
			String dateTime = ProtoUtil.byte2Time(bMsg[13], bMsg[14],bMsg[15],
													bMsg[16], bMsg[17], bMsg[18]);//测量时间
			dao.insert(bMsg[10] + "", bMsg[11] + "", bMsg[19] + "",dateTime,
						(bMsg[20] & 0xFF) + "", (bMsg[21] & 0xFF) + "",bMsg[22] + "");//高压值可能溢出，转为无符号
			dao.close();
			
			SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, 0);
			boolean isON4Buble = pref.getBoolean(SettingActivity.IS_ON_4_BUBLE, true);
			if(isON4Buble) {
				if (!RecordsActivity.isOnForeground) {
					notification.setLatestEventInfo(BackgroundService.this, "新消息",dateTime+ " " + bMsg[19] +
													"测了血压：  >< "+  (bMsg[20] & 0xFF) +" <>" + (bMsg[21] & 0xFF) + 
													" -^-" + bMsg[22], messagePendingIntent);
					notificatioManager.notify(messageNotificationID, notification);
				} else {//每隔1秒发送一次广播，同时把i放进intent传出
					Intent intent = new Intent();
					intent.putExtra("popT", dateTime);
					intent.putExtra("popH", (bMsg[20] & 0xFF)+"");
					intent.putExtra("popL", (bMsg[21] & 0xFF)+"");
					intent.putExtra("popB", bMsg[22]+"");
					intent.setAction("android.intent.action.test");//action与接收器相同
					sendBroadcast(intent);
				}
			}
		}
		checkNoInUse();
	}
	
	public void checkNoInUse() {
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, 0);
		boolean isOn4InUse = pref.getBoolean(SettingActivity.IS_ON_4_IN_USE, true);
		if(isOn4InUse) {
		Cursor cursor = dao.all();
			if (cursor.moveToNext()) {
				String lastTime = cursor.getString(4);
				int days = ProtoUtil.computeDValTime(lastTime);
				if(days >14) {
					notification.setLatestEventInfo(BackgroundService.this, "新消息","亲，咱父母2周没测血压了呢！问候下呗", messagePendingIntent);
					notificatioManager.notify(messageNotificationID, notification);
				}
			}
		}
	}
}
