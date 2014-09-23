package com.tech37c.ebpmeter.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.tech37c.ebpmeter.service.BackgroundService;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;

/**
 * View Tools
 * 
 * @author lixiang
 * 
 */
public class ViewUtil {
	public static Bitmap createStarPhoto(int x, int y, Bitmap image) {
		// 根据源文件新建一个darwable对象
		Drawable imageDrawable = new BitmapDrawable(image);

		// 新建一个新的输出图片
		Bitmap output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// 新建一个矩形
		RectF outerRect = new RectF(0, 0, x, y);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);

		Path path = new Path();

		// 绘制三角形
		// path.moveTo(0, 0);
		// path.lineTo(320, 250);
		// path.lineTo(400, 0);

		// 绘制正无边形
		long tmpX, tmpY;
		path.moveTo(200, 200);// 此点为多边形的起点
		for (int i = 0; i <= 5; i++) {
			tmpX = (long) (200 + 200 * Math.sin((i * 72 + 36) * 2 * Math.PI
					/ 360));
			tmpY = (long) (200 + 200 * Math.cos((i * 72 + 36) * 2 * Math.PI
					/ 360));
			path.lineTo(tmpX, tmpY);
		}
		path.close(); // 使这些点构成封闭的多边形
		canvas.drawPath(path, paint);
		// canvas.drawCircle(100, 100, 100, paint);

		// 将源图片绘制到这个圆角矩形上
		// 产生一个红色的圆角矩形

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		imageDrawable.setBounds(0, 0, x, y);
		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
		imageDrawable.draw(canvas);
		canvas.restore();
		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
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
