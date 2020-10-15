package com.weiwei.base.item;

import java.io.Serializable;

/**
 * 通话记录对象
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
public class ImMessageItem implements Serializable {
	public ImMessageItem(String id, String msg,String filePath, int type, String fromuid, String touid, String size, String name ,String time,int read,int isme,String uid,int isSendSuc) {
		super();
		this.id = id;
		this.msg=msg;
		this.filePath = filePath;
		this.type = type;
		this.fromuid = fromuid;
		this.touid = touid;
		this.size = size;
		this.name = name;
		this.time=time;
		this.read=read;
		this.isme=isme;
		this.uid=uid;
		this.isSendSuc=isSendSuc;
	
		
	}
	public ImMessageItem() {
		super();
	}
	@Override
	public String toString() {
		return "ImMessage [id=" + id + ", filePath=" + filePath + ", type=" + type + ", fromuid=" + fromuid
				+ ", touid=" + touid + ", size=" + size + ", name=" + name + ",time="+time+", msg="+msg+"]";
	}
	public String id; // 消息id
	public String filePath; // 消息路径
	public int type;// 消息类型  对应getExtra_mime
	public String fromuid;// 来自
	public String touid;// 发送
	public String size;// 大小
	public String name;// 名字
	public String time;//时间
	public int read;//是否读 0读 1没读
	public String msg;
	public int isme;  //1自己 0别人
	public String uid;//UID帐号
	public String localpicPath;
	public int isSendSuc=0;//是否发送成功 0 发送 1 成功 
 
}