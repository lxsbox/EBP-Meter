package com.tech37c.ebpmeter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtil {

	public static final int WIFI_AP_STATE_DISABLING = 0;
	public static final int WIFI_AP_STATE_DISABLED = 1;
	public static final int WIFI_AP_STATE_ENABLING = 2;
	public static final int WIFI_AP_STATE_ENABLED = 3;
	public static final int WIFI_AP_STATE_FAILED = 4;
	
	/* Why there are two different constant                       ??? ??? ??? */
	public static final int WIFI_AP_STATE_DISABLING_HIGH_SDK = 10;  
	public static final int WIFI_AP_STATE_DISABLED_HIGH_SDK = 11;  
	public static final int WIFI_AP_STATE_ENABLING_HIGH_SDK = 12;  
	public static final int WIFI_AP_STATE_ENABLED_HIGH_SDK = 13;  
	public static final int WIFI_AP_STATE_FAILED_HIGH_SDK = 14;

	public void stratWifiAp(WifiManager wifiManager) {
		// WifiManager wifi = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// wifiManager.setWifiEnabled(false);
		Method method1 = null;
		try {
			method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();
			// wifi热点名字
			netConfig.SSID = "yzy00002";
			netConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			// 密码
			netConfig.preSharedKey = "12345678";

			method1.invoke(wifiManager, netConfig, true);
			// Method method2 =
			// wifiManager.getClass().getMethod("getWifiApState");
			// int state = (Integer) method2.invoke(wifiManager);
			// Log.i("wifi state" + state);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void closeWifiAp(WifiManager wifiManager, boolean isOpen) {
		// WifiManager wifi = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// wifiManager.setWifiEnabled(false);
		Method method1 = null;
		try {
			method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();
			// wifi热点名字
			netConfig.SSID = "yzy00002";
			netConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			// 密码
			netConfig.preSharedKey = "12345678";

			method1.invoke(wifiManager, netConfig, isOpen);
			// Method method2 =
			// wifiManager.getClass().getMethod("getWifiApState");
			// int state = (Integer) method2.invoke(wifiManager);
			// Log.i("wifi state" + state);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getWifiApState(WifiManager wifiManager) {
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			int i = (Integer) method.invoke(wifiManager);
//			Log.i("wifi state:  " + i);
			return i;
		} catch (Exception e) {
//			Log.i("Cannot get WiFi AP state" + e);
			return WIFI_AP_STATE_FAILED;
		}
	}
}
