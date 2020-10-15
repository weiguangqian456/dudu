package com.weiwei.base.common;

import com.weiwei.base.item.ImageData;

public class TagInfo {
	String url;
	int position;
	int msgid;
	ImageData imgData;

	public int getMsgid() {
		return msgid;
	}

	public void setMsgid(int msgid) {
		this.msgid = msgid;
	}

	public ImageData getImgData() {
		return imgData;
	}

	public void setImgData(ImageData imgData) {
		this.imgData = imgData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
