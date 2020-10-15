package com.weiwei.base.item;

public class VsRichMsgItem {
	//自增长ID
	private int _Id;
	//消息Id
	private int msgId;
	//消息类型9000：活动 9001：广告 9002：游戏
	private String msgType;
	//消息类型名称
	private String msgTypeName;
	//消息样式1：纯文本   2：单文本 3：单图文   4：多图文
	private int msgStyle;
	//消息生效时间
	private String effectTime;
	//置顶标识1：置顶   0：不置顶
	private int topFlag;
	//入库时间
	private String createTime;
	
	public int get_Id() {
		return _Id;
	}
	public void set_Id(int _Id) {
		this._Id = _Id;
	}
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getMsgTypeName() {
		return msgTypeName;
	}
	public void setMsgTypeName(String msgTypeName) {
		this.msgTypeName = msgTypeName;
	}
	public int getMsgStyle() {
		return msgStyle;
	}
	public void setMsgStyle(int msgStyle) {
		this.msgStyle = msgStyle;
	}
	public String getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(String effectTime) {
		this.effectTime = effectTime;
	}
	public int getTopFlag() {
		return topFlag;
	}
	public void setTopFlag(int topFlag) {
		this.topFlag = topFlag;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
