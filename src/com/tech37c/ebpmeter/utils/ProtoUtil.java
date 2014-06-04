package com.tech37c.ebpmeter.utils;

import com.tech37c.ebpmeter.service.BackgroundService;

import android.content.SharedPreferences;

/**
 * 通讯协议相关
 * 
 * @author lixiang
 * 
 */
public class ProtoUtil {
	
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
		String strDateTime = "20" + year + "-" + month + "-" + day + " "  + hour + ":" + minutes  + ":"+  second;
		
		return strDateTime;
	}

}
