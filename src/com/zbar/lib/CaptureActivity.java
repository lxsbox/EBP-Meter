package com.zbar.lib;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.RegisterActivity;
import com.tech37c.ebpmeter.contorller.SearchingIntroductionActivity;
import com.tech37c.ebpmeter.model.EmptyByte;
import com.tech37c.ebpmeter.service.BackgroundService;
import com.tech37c.ebpmeter.utils.ProtoUtil;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:25:31
 * 
 * 版本: V_1.0.0
 * 
 * 描述: 扫描界面
 */
public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private ImageView mQrLineView = null;
	private TextView mTextView = null;
	private boolean isNeedCapture = false;
	private static final long VIBRATE_DURATION = 200L;
	private boolean flag = true;
	
	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
		mTextView = (TextView) findViewById(R.id.capture_txt);

		mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		handler.sendEmptyMessage(R.id.restart_preview);
		
		//检测是否在连上互联网 ！！！ 虚拟机下验证不成功
		if(ProtoUtil.isConnected2Internet(getApplicationContext())) {
			new RegisterTask(getApplicationContext()).execute(ProtoUtil.parseQR(result));
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.net_unavailable, ""), Toast.LENGTH_SHORT).show();
		};
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(true);
			

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
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
	    	xh_ProgressBar = (ProgressBar)findViewById(R.id.welcomeProgressBar);
			xh_ProgressBar.setVisibility(View.VISIBLE);
			mCropLayout.setVisibility(View.INVISIBLE);
			mTextView.setText(R.string.scan_tips_registing);
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
    	    	edit.putString(RegisterActivity.DEVICE_TYPE, this.devType);
    	    	edit.putString(RegisterActivity.DEVICE_ID, this.devId);
    	    	edit.commit();
    	    	
    	    	Toast.makeText(mContext, getString(R.string.register_sucess, ""), Toast.LENGTH_SHORT).show();
    	    	Intent intent = new Intent(mContext, SearchingIntroductionActivity.class);
    			startActivity(intent);
    			finish();// Preventing going back
    			startService(new Intent(BackgroundService.ACTION));//Start the heart beating service
			}else {
				xh_ProgressBar.setVisibility(View.GONE);
				Toast.makeText(mContext, getString(R.string.register_failed, ""), Toast.LENGTH_SHORT).show();
				mCropLayout.setVisibility(View.VISIBLE);
				mTextView.setText(R.string.scan_tips);
			}
		}
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
					System.out.println("Timed out," + (RegisterActivity.MAXTRIES - tries) + " more tries ...");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (!receivedResponse && (tries < RegisterActivity.MAXTRIES));
			if (receivedResponse) {
				System.out.println("Receive record: " + in[0]);
			} else {
				System.out.println("No response -- register giving up");
				return null;
			}
			socket.close();
			return in;
	}
}