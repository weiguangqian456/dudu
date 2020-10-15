package com.weiwei.base.item;

public class VsRichMsgContentItem {
	//主键ID 自增
	private int id;
	//消息ID 外键
	private int msgId;
	//消息标题
	private String msgTitle;
	//内容概要
	private String summary;
	//图片地址
	private String imgUrl;
	//跳转方式(in：内容界面、sdk：第三方sdk、wap：内嵌wap页、web：外部界面、html：自定义详情页)
	private String jumpType;
	//跳转按钮标题
	private String jumpBtnTitle;
	//跳转的url地址
	private String jumpUrl;
	//消息内容索引
	private int imgIndex;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getMsgTitle() {
		return msgTitle;
	}
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getJumpType() {
		return jumpType;
	}
	public void setJumpType(String jumpType) {
		this.jumpType = jumpType;
	}
	public String getJumpBtnTitle() {
		return jumpBtnTitle;
	}
	public void setJumpBtnTitle(String jumpBtnTitle) {
		this.jumpBtnTitle = jumpBtnTitle;
	}
	public String getJumpUrl() {
		return jumpUrl;
	}
	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}
	public int getImgIndex() {
		return imgIndex;
	}
	public void setImgIndex(int imgIndex) {
		this.imgIndex = imgIndex;
	}
}
