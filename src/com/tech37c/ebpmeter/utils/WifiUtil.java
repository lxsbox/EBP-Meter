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
	public static final String YZY_SSID = "yzy00002";
	public static final String YZY_SSID_KEY = "12345678";

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
			netConfig.SSID = YZY_SSID;
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
			netConfig.preSharedKey = YZY_SSID_KEY;

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
	
	/**
	 * Close the Wifi AP
	 * @param wifiManager
	 * @param paramWifiConfiguration
	 * @param paramBoolean
	 */
	public void createWiFiAP(WifiManager wifiManager, WifiConfiguration paramWifiConfiguration,
			boolean paramBoolean) {
		try {
			Class localClass = wifiManager.getClass();
			Class[] arrayOfClass = new Class[2];
			arrayOfClass[0] = WifiConfiguration.class;
			arrayOfClass[1] = Boolean.TYPE;
			Method localMethod = localClass.getMethod("setWifiApEnabled",
					arrayOfClass);
			WifiManager localWifiManager = wifiManager;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = paramWifiConfiguration;
			arrayOfObject[1] = Boolean.valueOf(paramBoolean);
			localMethod.invoke(localWifiManager, arrayOfObject);
			return;
		} catch (Exception localException) {
		}
	}
	
	
	public WifiConfiguration createWifiInfo() {
		WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
		localWifiConfiguration1.allowedAuthAlgorithms.clear();
		localWifiConfiguration1.allowedGroupCiphers.clear();
		localWifiConfiguration1.allowedKeyManagement.clear();
		localWifiConfiguration1.allowedPairwiseCiphers.clear();
		localWifiConfiguration1.allowedProtocols.clear();
		localWifiConfiguration1.SSID = YZY_SSID;
		localWifiConfiguration1.allowedAuthAlgorithms.set(1);
		localWifiConfiguration1.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.CCMP);
		localWifiConfiguration1.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.TKIP);
		localWifiConfiguration1.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.WEP40);
		localWifiConfiguration1.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.WEP104);
		localWifiConfiguration1.allowedKeyManagement.set(0);
		localWifiConfiguration1.wepTxKeyIndex = 0;
		localWifiConfiguration1.preSharedKey = YZY_SSID_KEY;
		localWifiConfiguration1.allowedAuthAlgorithms.set(0);
		localWifiConfiguration1.allowedProtocols.set(1);
		localWifiConfiguration1.allowedProtocols.set(0);
		localWifiConfiguration1.allowedKeyManagement.set(1);
		localWifiConfiguration1.allowedPairwiseCiphers.set(2);
		localWifiConfiguration1.allowedPairwiseCiphers.set(1);
		return localWifiConfiguration1;
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
	
	public void OpenWifi(WifiManager wifiManager) {
		if (!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);
	}
	
	public void startScan(WifiManager wifiManager) {
		wifiManager.startScan();
	}
}
