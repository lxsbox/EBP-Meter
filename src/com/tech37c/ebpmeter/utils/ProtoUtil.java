package com.tech37c.ebpmeter.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.tech37c.ebpmeter.service.BackgroundService;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;

/**
 * 通讯协议相关
 * 
 * @author lixiang
 * 
 */
public class ProtoUtil {
	static {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
	}
	
	
	/**
	 * Byte 转 十六进制 字符串
	 * @param b
	 * @return
	 */
	
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * Byte 转 Short
	 * @param b
	 * @return
	 */
	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	/**
	 * Short 转  Byte
	 * @param s
	 * @return
	 */
	public static byte[] shortToByte(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}
	
	/**
	 * 时间转成协议需要的byte[]
	 * @param time
	 * @return
	 */
	public static byte[] time2Byte(String dateTime) {
		byte[] b = new byte[6];
		String[] dAndt = null;
        String[] date = null;
        String[] time = null;
    	dAndt = dateTime.split(" ");
    	date = dAndt[0].split("-");
    	time = dAndt[1].split(":");
    	//年月日，时分秒
        b[0] = (byte)Integer.parseInt(date[0].substring(2));
        b[1] = (byte)Integer.parseInt(date[1]);
        b[2] = (byte)Integer.parseInt(date[2]);
        b[3] = (byte)Integer.parseInt(time[0]);
        b[4] = (byte)Integer.parseInt(time[1]);
        b[5] = (byte)Integer.parseInt(time[2]);
		return b;
	}
	
	/**
	 * byte[]转成协议需要的时间
	 * @param time
	 * @return
	 */
	public static String byte2Time(int year, int month, int day, 
									int hour, int minutes, int second) {
		String sYear = year>9? year+"":"0"+year;
		String sMonth = month>9? month+"":"0"+month;
		String sDay = day>9? day+"":"0"+day;
		
		String sHour = hour>9? hour+"":"0"+hour;
		String sMinutes = minutes>9? minutes+"":"0"+minutes;
		String sSecond = second>9? second+"":"0"+second;
		
		String strDateTime = "20" + sYear + "-" + sMonth + "-" + sDay + " "  + sHour + ":" + sMinutes  + ":"+  sSecond;
		
		return strDateTime;
	}

	
	/**
	 * 是否连接到网络
	 * @return
	 */
	public static boolean isConnected2Internet(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {  
	        return cm.getActiveNetworkInfo().isAvailable();  
	    }  
	    return false;
	}
	
	/**
	 * 解析二维码
	 * @return
	 */
	public static String[] parseQR(String result) {
		String[] typeId = new String[2];
		if (!result.isEmpty()) {
			String[] temp = result.split(",");
			String[] typeKV = temp[0].split("=");
			String type = typeKV[1];
			
			String[] idKV = temp[1].split("=");
			String id = idKV[1];
			
			typeId[0] = type;
			typeId[1] = id;
		}
		
		return typeId;
	}
	
	/**
	 * 将原始时间转位更易读的时间
	 * @return
	 */
	public static String getEasyTime(String origTime) {
		Date now = new Date();
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String strNow = df2.format(now);
		boolean isToday = true;
		boolean isYesterday = true;
		long aDayMilli = 24*60*60*1000;
		long Min30Milli = 30*60*1000;
		long diff =0;
		try {
			Date orgin = df1.parse(origTime);
			Date nowNoTime = df2.parse(strNow);
			long today0Milli = nowNoTime.getTime();
			long orginMilli = orgin.getTime();
			isToday	= (orginMilli-today0Milli)>0?true:false;
			isYesterday	= orginMilli-today0Milli<0 && Math.abs(orginMilli-today0Milli)<aDayMilli?true:false;
			diff = now.getTime() - orginMilli;
//			days = diff / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String[] dateTime = origTime.split(" ");
		String date = dateTime[0];
		String dateNoYear = date.substring(5);
		String time = dateTime[1];
		String timeNoSec = time.substring(0, 5);
		
		String easyTime = "";
		if(isToday) {
			if(diff<Min30Milli) {
				easyTime = "刚刚";
			}else {
				easyTime = "今天    "+ timeNoSec;
			}
			
		}else {
			if(isYesterday) {
				easyTime = "昨天    " + timeNoSec;
			}else {
				easyTime = dateNoYear + " " +  timeNoSec;
			}
		}
		
		return easyTime;
	}
	
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	public static int computeDValTime(String sTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begin = null;
		try {
			begin = df.parse(sTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date end = new Date();
		long between = (end.getTime()- begin.getTime())/1000;//除以1000是为了转换成秒   
		int	day = (int)between/(24*3600);
		return day;
	}
	
}
