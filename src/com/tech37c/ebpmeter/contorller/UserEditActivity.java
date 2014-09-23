package com.tech37c.ebpmeter.contorller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.utils.ProtoUtil;
import com.tech37c.ebpmeter.utils.ViewUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 编辑用户
 * 
 * @author Shawn Li
 * 
 */
public class UserEditActivity extends Activity {
	private CheckBox dadCheckBox;
	private CheckBox momCheckBox;
	private EditText dadText;
	private EditText momText;

	public static final String DAD = "dad";
	public static final String MOM = "mom";
	public static final String CURRENT_USER_ID = "current_user_id";
	public static final String USER_1 = "1";
	public static final String USER_1_AGE = "0";
	public static final String USER_2 = "2";
	public static final String USER_2_AGE = "0";
	private static final int SELECT_PHOTO = 100;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	public static final String DAD_IMAGE_FILE_NAME = "dadFaceImage.jpg";
	public static final String MOM_IMAGE_FILE_NAME = "momFaceImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private ImageView dadImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏
		setContentView(R.layout.activity_user_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_user_edit_title);

		final ImageButton backBtn = (ImageButton) findViewById(R.id.back_setting);
		backBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						SettingActivity.class);
				startActivity(intent);
			}
		});

		final ImageButton changeDadImageBtn = (ImageButton) findViewById(R.id.dad_image_arrow);
		changeDadImageBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		dadImage = (ImageView) findViewById(R.id.dad_image);
		setDrawableFace4UerEidt(dadImage, DAD);
	}

	
	/**
	 * Fill main page's face picture
	 * @return
	 */
	public void setDrawableFace4UerEidt(ImageView face, String imageFlag) {
		Bitmap bitmap = null;
		String path = "";
		if(imageFlag.equals(DAD)) {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.DAD_IMAGE_FILE_NAME;
		} else {
			path = Environment.getExternalStorageDirectory() + "/" + UserEditActivity.MOM_IMAGE_FILE_NAME;
		}
		
		if (!path.equals("")) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path);
				bitmap  = BitmapFactory.decodeStream(fis);
				face.setImageBitmap(ViewUtil.getRoundedCornerBitmap(bitmap, 100, bitmap.getWidth(), bitmap.getHeight()));
//				Drawable drawable = new BitmapDrawable(bitmap);
//				face.setImageBitmap(ViewUtil.createStarPhoto(120, 120, bitmap));
//				face.setImageDrawable(drawable);
			} catch (FileNotFoundException e) {
				System.out.println("file not found");
			} finally {
				try {
					if(fis != null) fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (ProtoUtil.hasSdcard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												DAD_IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_edit, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				saveBitmap(data.getData());
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (ProtoUtil.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ DAD_IMAGE_FILE_NAME);

					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(UserEditActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void saveBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			// 读取uri所在的图片
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = null;
		if (ProtoUtil.hasSdcard()) {
			f = new File(
					Environment.getExternalStorageDirectory() + "/"
							+ DAD_IMAGE_FILE_NAME);
		} else {
			Toast.makeText(UserEditActivity.this, "未找到存储卡，无法存储照片！",
					Toast.LENGTH_LONG).show();
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveBitmap(Bitmap bitmap) {
		File f = null;
		if (ProtoUtil.hasSdcard()) {
			f = new File(
					Environment.getExternalStorageDirectory() + "/"
							+ DAD_IMAGE_FILE_NAME);
		} else {
			Toast.makeText(UserEditActivity.this, "未找到存储卡，无法存储照片！",
					Toast.LENGTH_LONG).show();
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			saveBitmap(photo);//haha! save afer crop pic yeah~
			dadImage.setImageBitmap(ViewUtil.getRoundedCornerBitmap(photo, 100, photo.getWidth(), photo.getHeight()));
//			dadImage.setImageDrawable(drawable);
		}
	}

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = toRoundCorner(bitmap, 10);// 调用圆角处理方法
				// headImageView.setImageBitmap(bitmap);
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	/**
	 * 将图片变为圆角
	 * 
	 * @param bitmap
	 *            原Bitmap图片
	 * @param pixels
	 *            图片圆角的弧度(单位:像素(px))
	 * @return 带有圆角的图片(Bitmap 类型)
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

}
