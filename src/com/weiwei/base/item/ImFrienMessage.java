package com.weiwei.base.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 会话
 * @author 黄发兴
 * @version 创建时间：2014-11-12下午02:53:37
 */
public class ImFrienMessage implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	//public static ArrayList<ImFrienMessage> imFrienMessageList = new ArrayList<ImFrienMessage>();// 所有会话列表
	//public static ArrayList<ImMessageItem> msgList = new ArrayList<ImMessageItem>();// 当前对话列表
	
	public HashMap<String, ArrayList<ImMessageItem>> msgArraylistMap_user = new HashMap<String, ArrayList<ImMessageItem>>();
	
	public VsContactItem vsContactItem = new VsContactItem();// 微友信息
	
	//public int id; // 会话ID
	public String fromuid;// 来自
	
	public int message_count = 0; // 会话消息数量
	public int unread_message_count = 0;// 未读消息数
	
	public String time; // 最后一条记录时间
	public int type;//最后一条记录内容的类型  消息类型  对应getExtra_mime
	public int isread;// 最后一条记录是否已经阅读：0--读, 1--未读 未听
	public int isme; // 最后一条记录是不是自己发送的：0-接收 1--发出
	public String msg;// 最后一条记录的实际内容，根据类型不同，内容可能是地址，可能是文字
}
