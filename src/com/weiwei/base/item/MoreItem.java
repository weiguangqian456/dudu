package com.weiwei.base.item;

public class MoreItem {
	private int imgId;
	private String moreName;
	private String url;
		
	public MoreItem(int imgId, String moreName, String url) {
		super();
		this.imgId = imgId;
		this.moreName = moreName;
		this.url = url;
	}
	
	public MoreItem() {
		super();
	}

	public int getImgId() {
		return imgId;
	}
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	public String getMoreName() {
		return moreName;
	}
	public void setMoreName(String moreName) {
		this.moreName = moreName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
