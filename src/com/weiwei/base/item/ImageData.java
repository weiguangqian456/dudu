package com.weiwei.base.item;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageData {
	Uri uri;// 图片存放地址
	int imgType;// 图片格式
	Bitmap bitmap;
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public int getImgType() {
		return imgType;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	// Drawable drawable;// 保存PNG/JPG图片
	// byte[] imgByte;// 保存GIF图片

	// public byte[] getImgByte() {
	// return imgByte;
	// }
	// public void setImgByte(byte[] imgByte) {
	// this.imgByte = imgByte;
	// }

	// public Drawable getDrawable() {
	// return drawable;
	// }
	//
	// public void setDrawable(Drawable drawable) {
	// this.drawable = drawable;
	// }
}
