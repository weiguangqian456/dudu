package com.weiwei.qrcode.encoding;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.weiwei.base.common.CustomLog;

/**
 * 
 * @Title:Android客户端
 * @Description: 二维码生成类
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-10-27
 */
public final class EncodingHandler {

	private static final int BLACK = 0xff000000;

	public static Bitmap createQRCode(String str, int widthAndHeight, Bitmap foreground) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
		CustomLog.i("vsdebug", "生成图片大小:"+width+"--"+height);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return combineBitmap(bitmap, foreground, width, height);
	}

	/**
	 * 
	 * 拼接二维码和Logo
	 * 
	 * @param background
	 *            二维码
	 * @param foreground
	 *            logo
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return
	 */
	public static Bitmap combineBitmap(Bitmap background, Bitmap foreground, int width, int height) {
		Matrix matrix = new Matrix();
		float sfb = (float) 0.16;
		float sfx = (float) (sfb * background.getWidth()) / (float) foreground.getWidth();
		float sfy = (float) (sfb * background.getHeight()) / (float) foreground.getHeight();
		matrix.postScale(sfx, sfy);

		Bitmap newForeground = Bitmap.createBitmap(foreground, 0, 0, foreground.getWidth(), foreground.getHeight(),
				matrix, true);

		System.out.println("宽度比值：" + sfx + "-----" + "高度比值：" + sfy);

		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(background, new Matrix(), null);

		int logoX = background.getWidth() / 2 - newForeground.getWidth() / 2; // 设置logo图片的位置,这里令其居中
		int logoY = background.getHeight() / 2 - newForeground.getHeight() / 2; // 设置logo图片的位置,这里令其居中

		canvas.drawBitmap(newForeground, logoX, logoY, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		canvas.restore();// 存储
		
		return newBitmap;
	}

}
