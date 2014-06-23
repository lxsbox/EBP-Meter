package com.tech37c.ebpmeter.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.amour.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.MainActivity;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.EmptyByte;
import com.tech37c.ebpmeter.utils.ProtoUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

/**
 * 后台服务类
 * 
 * @author lixiang
 * 
 */
public class BackgroundService extends Service {
//	private static String DATA_CENTER_IP = "54.200.144.55";// 数据中心IP
//	private static int DATA_CENTER_UDP_PORT = 18899;// 数据中心端口
//	private static String DATA_CENTER_IP = "192.168.199.101";//数据中心IP
	private static int DATA_CENTER_UDP_PORT = 6006;// 数据中心端口
	private static String DATA_CENTER_IP = "192.168.199.190";//数据中心IP
//	private static int DATA_CENTER_UDP_PORT = 6001; //数据中心端口
	private static int TIME_OUT = 3000;
	private static int MAXTRIES = 3;
	
	public static final String SHARED_PREFS_NAME = "tech37c_ebpmeter_preferences";// 本地共享文件名
	public static final String POP_UP_STRING = "pop_up_string";// 气泡信息内容
	public static final String LATEST_RECORD_TIME = "latest_record_time";//最近插入的一条记录时间
	public static final String DEV_TYPE = "dev_type";//设备类型（血压计）
	public static final String DEV_ID = "dev_id";//设备ID（血压计）
	public static final String APP_VERSION = "app_version";//当前app版本
	
	private BackgroundThread heartBeatThread;
	private BackgroundThread popUpThread;
	public static final String ACTION = "com.tech37c.ebpmeter.service.BackgroundService";
	private Intent messageIntent;
	private PendingIntent messagePendingIntent;
	private int messageNotificationID = 1000;
	private Notification notification;
	private NotificationManager notificatioManager;
	private BaseDAO dao;

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
		notificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		messageIntent = new Intent(this, MainActivity.class);
		messagePendingIntent = PendingIntent.getActivity(this, 0,
				messageIntent, 0);
		// 心跳线程
		heartBeatThread = new BackgroundThread();
		heartBeatThread.type = 0;
		heartBeatThread.start();
		// 提示线程
		popUpThread = new BackgroundThread();
		popUpThread.type = 1;
		popUpThread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 后台服务线程类
	 * 
	 * @author lixiang
	 * 
	 */
	class BackgroundThread extends Thread {
		public boolean isRunning = true;
		public int type;

		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(5000);// 5秒一次心跳
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				switch (type) {
				case 0:
					reportHeartBeat();
				case 1:
					checkPopUpString();
				default:
				}
			}
		}
	}

	/**
	 * 上报心跳 获取结果并插入本地库 提示信息写在本地文件
	 */
	public void reportHeartBeat()  {
		byte[] response = getAnRecord();
		if (response[2] == 0x11) {//回应的是心跳
			SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString(APP_VERSION, response[10]+"");//版本号为啥两个字节？
			editor.commit();
		} else if (response[2] == 0x12) {//回应的是记录
			// 缺少本地库记录时间验证验证， 以保证数据不重复！！！
			String dateTime = ProtoUtil.byte2Time(response[13], response[14],response[15],//测量时间
									    response[16],response[17],response[18]);
			dao.insert(response[10]+"", response[11]+"", response[19]+"", dateTime,
					(response[20]&0xFF)+"",response[21]+"",response[22]+"");//高压值可能溢出，转为无符号
			dao.close();
			SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString(POP_UP_STRING, dateTime + " " + response[19] + "新血压值 ："+ (response[20]&0xFF));
			editor.putString(LATEST_RECORD_TIME, dateTime);
			editor.commit();
		}
	}

	/**
	 * 检查本地文件是否有提示信息
	 * 提示完后置空
	 */
	@SuppressWarnings("deprecation")
	public void checkPopUpString() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		String popUp = sharedPreferences.getString(POP_UP_STRING, "");
        
		if (!popUp.equals("")) {
			notification.setLatestEventInfo(BackgroundService.this,"新消息", popUp, messagePendingIntent);
			notificatioManager.notify(messageNotificationID, notification);
			messageNotificationID++;
			SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString(POP_UP_STRING, "");
			editor.commit();
		}
	}

	/**
	 * 从数据中心获取一条记录,如果有的话
	 * 
	 * @return
	 */
	public byte[] getAnRecord()  {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		String time = sharedPreferences.getString(LATEST_RECORD_TIME, "2010-1-1 0:0:0");
		byte[] bDateTime = ProtoUtil.time2Byte(time);
		
		DatagramSocket socket = null;
		int tries = 0;
		boolean receivedResponse = false;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIME_OUT);
			EmptyByte eb = new EmptyByte(20);
			byte[] out = eb.getSbyte();
			out[0] = 0x25;
			out[1] = 0x43;
			out[2] = 0x11;//报文类型
			out[3] = 22;//有效数据区长度2
			out[5] = 0x0C;//源类型（安卓）
			out[6] = 0x0A;//宿类型
			
		    out[10] = 1; //设备型号
//		    short sId = (short)32760;
		    short sId = (short)65534;
		    byte[] bId = ProtoUtil.shortToByte(sId);
		    out[11] = bId[1]; //设备ID
		    out[12] = bId[0]; //设备ID
			out[13]	= bDateTime[0];//时间6B
			out[14] = bDateTime[1];
			out[15] = bDateTime[2];
			out[16] = bDateTime[3];
			out[17] = bDateTime[4];
			out[18] = bDateTime[5];
			DatagramPacket outPacket = new DatagramPacket(out,20,
					InetAddress.getByName(DATA_CENTER_IP), DATA_CENTER_UDP_PORT);// 包裹
			socket.send(outPacket);// 发送报文
		} catch (SocketException e1) {
			e1.printStackTrace();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EmptyByte eb = new EmptyByte(24);
		byte[] in = eb.getSbyte();
		DatagramPacket inPacket = new DatagramPacket(in, 24);// 构造用来接收长度为length的数据包
		do {
			try {
				socket.receive(inPacket);
				//为什么得到得值还有一个／？
//				if (!inPacket.getAddress().equals(DATA_CENTER_IP)) {
//					throw new IOException("Received packet from an unknown source");
//				}
				receivedResponse = true;
			} catch (InterruptedIOException e) {
				tries += 1;
				System.out.println("Timed out," + (MAXTRIES - tries) + " more tries ...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!receivedResponse && (tries < MAXTRIES));
		if (receivedResponse) {
			System.out.println("Receive record: " + in[0]);
		} else {
			System.out.println("No response -- giving up");
		}
		socket.close();
		return in;
	}
}