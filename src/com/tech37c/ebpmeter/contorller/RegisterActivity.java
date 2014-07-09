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
import com.tech37c.ebpmeter.model.EmptyByte;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.ProtoUtil;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText idText;
	private EditText typeText;
	private Button regButton;
	public static final String DEVICE_ID = "devId";
	public static final String DEVICE_TYPE = "devType";
	public static final String IS_REGISTERED = "isRegistered";
	public static int MAXTRIES = 3;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
		
		SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
		String devId = pref.getString(DEVICE_ID, "");
		String devType = pref.getString(DEVICE_TYPE, "");
		
		if (devId.equals("")||devType.equals("")) {
			setContentView(R.layout.activity_register);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_register_title);//Title
			final Button regButton  = (Button)findViewById(R.id.register);
			typeText = (EditText) findViewById(R.id.devType);
			idText = (EditText) findViewById(R.id.devId);
			regButton.setOnClickListener(new Button.OnClickListener(){
	    		public void onClick (View v){
	    			String type = typeText.getText().toString();
	    	    	String id = idText.getText().toString();
	    			String[] typeId = {type, id};
	    			
	    			//检测是否在连上互联网 ！！！ 虚拟机下验证不成功
	    			if(ProtoUtil.isConnected2Internet(v.getContext())) {
	    				new RegisterTask(v.getContext()).execute(typeId);
	    			} else {
	    				Toast.makeText(v.getContext(), getString(R.string.net_unavailable, ""), Toast.LENGTH_SHORT).show();
	    			};
	    			
	    		}
	    	});
			
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	/**
	 * 向服务端注册
	 * @param devType
	 * @param devId
	 */
	public byte[] registerDevice(String devType, String devId) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			byte[] bDateTime = ProtoUtil.time2Byte(df.format(new Date()));
			DatagramSocket socket = null;
			int tries = 0;
			boolean receivedResponse = false;
			try {
				socket = new DatagramSocket();
				socket.setSoTimeout(BackgroundService.TIME_OUT);
				EmptyByte eb = new EmptyByte(20);
				byte[] out = eb.getSbyte();
				out[0] = 0x25;
				out[1] = 0x43;
				out[2] = 0x17;//报文类型
				out[3] = 22;//有效数据区长度2
				out[5] = 0x0C;//源类型（安卓）
				out[6] = 0x0A;//宿类型
				
//			    out[10] = 1; //设备型号
//			    short sId = (short)65534;
				out[10] = (byte)Integer.parseInt(devType); //设备型号
			    short sId = (short)Integer.parseInt(devId);
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
						InetAddress.getByName(BackgroundService.DATA_CENTER_IP), BackgroundService.DATA_CENTER_UDP_PORT);// 包裹
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
//					if (!inPacket.getAddress().equals(DATA_CENTER_IP)) {
//						throw new IOException("Received packet from an unknown source");
//					}
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
				System.out.println("No response -- register giving up");
				return null;
			}
			socket.close();
			return in;
	}
	
	
	/**
	 * 另启注册线程
	 * @author Thinkpad
	 *
	 */
	private class RegisterTask extends AsyncTask<String, String, byte[]> {
		private Context mContext;//外面类的上下文
		private Exception exception;
	    private ProgressBar xh_ProgressBar;
	    private String devType;
	    private String devId;
	    
	    
	    public RegisterTask (Context context){
	         mContext = context;
	    }
	    
	    @Override
		protected void onPreExecute() {
	    	xh_ProgressBar = (ProgressBar)findViewById(R.id.ProgressBar);
			xh_ProgressBar.setVisibility(View.VISIBLE);
		}
	    
		@Override
		protected byte[] doInBackground(String... typeId) {
			this.devType = typeId[0]; 
		    this.devId = typeId[1];
			return registerDevice(typeId[0], typeId[1]);
		}
		
		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			
			if (null != result && result[2] == 0x18 && (int)result[10] == 0 ) {
				xh_ProgressBar.setVisibility(View.GONE);
				
				SharedPreferences pref = getSharedPreferences(BackgroundService.SHARED_PREFS_NAME, MODE_PRIVATE);
    	    	Editor edit = pref.edit();
    	    	edit.putString(DEVICE_TYPE, this.devType);
    	    	edit.putString(DEVICE_ID, this.devId);
    	    	
    	    	Toast.makeText(mContext, getString(R.string.register_sucess, ""), Toast.LENGTH_SHORT).show();
    	    	
                Intent intent = new Intent(mContext, UserEditActivity.class);
    			startActivity(intent);
			}else {
				xh_ProgressBar.setVisibility(View.GONE);
				Toast.makeText(mContext, getString(R.string.register_failed, ""), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
