package com.tech37c.ebpmeter.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;

public class BusinessHandler extends Activity{
	private static String DATA_CENTER_IP = "54.200.144.55";
	private static int DATA_CENTER_UDP_PORT = 18899;
	public static final String SHARED_PREFS_NAME = "tech37c_ebpmeter_preferences";
	public static final String lAST_RECORD_ID_SHARED_PREF = "last_record_id";
	public static final String IS_FIRST_HEART_BEAT = "is_first_heart_beat";//是否是第一次心跳
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
		if (dao.count() == 0) {
			dao.insert("1", "11", "周瑜", "2014-01-05 20:00:00", "135", "85", "75");
			dao.insert("1", "11", "豆豆 ", "2014-02-08 20:00:00", "135"+"", "85", "75");
			dao.insert("1", "11", "贾某某", "2014-03-10 20:00:00", "135", "85", "75");
		}
		
		Cursor cursor = dao.all();
		RecordPOJO record = new RecordPOJO();
		if (cursor.moveToLast()) {
			record.setUserId(cursor.getString(1));
			record.setCheckingTime(cursor.getString(2));
			record.setHighValue(cursor.getInt(3) + "");
			record.setLowValue(cursor.getInt(4) + "");
			record.setHeartBeat(cursor.getInt(5) + "");
		}
		
		return record;
	}
	
	/**
	 * 初始化首次心跳标志
	 */
	public void initFirstBeatFlag() {
		SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
	    editor.putBoolean(IS_FIRST_HEART_BEAT, true);
	    editor.commit();
	}
	
	
	/**
	 * 是否是第一次心跳
	 * @return
	 */
	public boolean isFirstBeat() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		return sharedPreferences.getBoolean(IS_FIRST_HEART_BEAT, true);
	}
	
	
	/**
	 * 恢复历史数据
	 */
	public void recoverHistory() {
		
		
		SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
	    editor.putBoolean(IS_FIRST_HEART_BEAT, false);//恢复完数据后，重置标志位
	    editor.commit();
	}
	
	/***
	 * 设置最后同步过的最后一个ID
	 * @param id
	 */
	public void setLastRecord(int id) {
		int lastRecordId = id;
	    // Update the brewCount and write the value to the shared preferences.
	    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
	    editor.putInt(lAST_RECORD_ID_SHARED_PREF, lastRecordId);
	    editor.commit();
    }
	
	/**
	 * 
	 * @return
	 */
//	public String getLatestReocord() {
//		addNewRecord2DB();//将新的数据插入本地
//	    String msg = "";
//		Cursor cursor = dao.all();
//		return msg;
//	}
	
	
	/**
	 * 获取本地保存的已提示ID
	 * @return
	 */
	public int getLocalId() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
	    int idInPref = sharedPreferences.getInt(lAST_RECORD_ID_SHARED_PREF, 0);
	    return idInPref;
	}
	
	
//	public String getDataFromDataCenter() {
//		String result = "";
//		Socket client = null;
//		BufferedReader br = null;
//		StringBuffer sb = null;
//		try {
//			client = new Socket(DATA_CENTER_IP, DATA_CENTER_TCP_PORT);
//			/* Write */
//			Writer writer = null;
//			try {
//				writer = new OutputStreamWriter(client.getOutputStream());
//				System.out.println("*** *** getLocalId: "+getLocalId());
//				writer.write("find," +getLocalId());
//				writer.write("eof\n");
//				writer.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//	        
//			/* Read */
//			try {
//				br = new BufferedReader(new InputStreamReader(client.getInputStream()));
//				sb = new StringBuffer();
//				String temp;
//				int index;
//				while ((temp = br.readLine()) != null) {
//					if ((index = temp.indexOf("eof")) != -1) {
//						sb.append(temp.substring(0, index));
//						break;
//					}
//					sb.append(temp);
//				}
//				System.out.println("*** *** From server: " + sb);
//				result = sb.toString();
//				writer.close();
//				br.close();
//				client.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		} catch (UnknownHostException e) {
//			System.out.println("UnknownHostException");
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.out.println("IOException");
//		}
//		return result;
//	}
	
	
	public String getDataFromDataCenterByUdp() {
		String result = "";
        
		try {
			/*  DatagramSocket此类表示用来发送和接收数据报包的套接字。*/
			DatagramSocket socket = new DatagramSocket();// 管道
			String str = "find,4,eof";
			/* DatagramPacket此类表示数据报包 */
			DatagramPacket outPacket = new DatagramPacket(str.getBytes(), str.length(), InetAddress.getByName(DATA_CENTER_IP), DATA_CENTER_UDP_PORT);// 包裹
			/* 发送数据报 */ 
			socket.send(outPacket);
			byte[] by = new byte[1000*1000];
			/* 构造 DatagramPacket，用来接收长度为 length 的数据包。*/
			DatagramPacket inPacket = new DatagramPacket(by, 1000*1000);
			socket.receive(inPacket);
			result = new String(by, 0, inPacket.getLength());
			socket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 取数据中心最近的一条记录，有则加入
	 */
//	public String addNewRecord2DB() {
//		String strObj = getDataFromDataCenterByUdp();
//		
//		if(strObj != null && !strObj.equals("")) {
//			String[] arrObj =  strObj.split("#");
//			
//			for(int i=0; i<arrObj.length; i++) {
//				String obj = arrObj[i];
//				String[] arrFiled = obj.split(",");
//				
//				/* 如果分割后的数组长度不合法，跳出本次*/
//				if(arrFiled.length != 6) {
//					System.out.println("数据格式错误！" + obj);
//					continue;
//				}
//				String tempId = arrFiled[0];
//				String tempUserId = arrFiled[1];
//				String tempCheckingTime = arrFiled[2];
//				String tempHighValue = arrFiled[3];
//				String tempLowValue = arrFiled[4];
//				String tempHeartBeat = arrFiled[5];
//				String[] arrId = tempId.split(":");
//				String[] arrUserId = tempUserId.split(":");
//				String[] arrCheckingTime = tempCheckingTime.split(":");
//				String[] arrHighValue = tempHighValue.split(":");
//				String[] arrLowValue = tempLowValue.split(":");
//				String[] arrHeartBeat = tempHeartBeat.split(":");
//				
//				dao.insert(arrUserId[1], arrCheckingTime[1], (int)Integer.valueOf(arrHighValue[1]), (int)Integer.valueOf(arrLowValue[1]), (int)Integer.valueOf(arrHeartBeat[1]));
//			}
//			dao.close();
//		}
//		
//		return strObj;
//	}
}
