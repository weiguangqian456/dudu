package com.weiwei.base.item;

import java.io.Serializable;

/**
 * 通话记录对象
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
public class VsNoticeItem implements Serializable {
	public String notice_id = "";// 本地生成的id。。
	public String messageid = "";// push消息ID
	public String messagebody = "";// 消息内容
	public String messagetype = "";// 消息类型 未读是0.已读是1
	public String messagetitle = "";// 消息标题
	public String messagetime = "";// 时间信息
	public String messagelink = "";// 跳转链接
	public String messagebuttontext = "";// 按钮标题
	public String messagelinktype = "";// 跳转类别

	public VsNoticeItem() {
	}

	public VsNoticeItem(final String id, final String msgid, final String body, final String type, final String title,
			final String time, final String link, final String buttontext, final String linktype) {
		notice_id = id;
		messageid = msgid;
		messagebody = body;
		messagetype = type;
		messagetitle = title;
		messagetime = time;
		messagelink = link;
		messagebuttontext = buttontext;
		messagelinktype = linktype;
	}

	@Override
	public String toString() {
		return "VsNoticeItem [notice_id=" + notice_id + ",messageid=" + messageid + ", messagebody=" + messagebody
				+ ", messagetype=" + messagetype + ", messagetitle=" + messagetitle + ", messagetime=" + messagetime
				+ ", messagelink=" + messagelink + ", messagebuttontext=" + messagebuttontext + ", messagelinktype="
				+ messagelinktype + "]";
	}
}