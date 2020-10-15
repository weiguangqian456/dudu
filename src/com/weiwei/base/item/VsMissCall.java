package com.weiwei.base.item;

import java.io.Serializable;

import android.text.TextUtils;

import com.weiwei.base.application.VsApplication;
import com.hwtx.dududh.R;

/**
 * 未接来电信息
 * 
 * @author Jiangxuewu
 *
 */
public class VsMissCall implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1393523580704471447L;
	/***
	 * <p>
	 * Call log type for incoming calls. INCOMING_TYPE = 1
	 * </p>
	 * <p>
	 * Call log type for outgoing calls. OUTGOING_TYPE = 2
	 * </p>
	 * <p>
	 * Call log type for missed calls. MISSED_TYPE = 3
	 * </p>
	 * <p>
	 * Call log type for voicemails.VOICEMAIL_TYPE = 4
	 * </p>
	 */
	private int type; // 类型

	private boolean isRead;// 0 表示未读， 1表示已读
	private boolean isNew;// 1表示来电是新来的，即未读， 0表示已读
	private String number; // 号码
	private String name; // 名字
	private long date; // 日期

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		if (!TextUtils.isEmpty(number) && number.length() <= 3) {
			return VsApplication.getContext().getResources().getString(R.string.vs_private_number);
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}
