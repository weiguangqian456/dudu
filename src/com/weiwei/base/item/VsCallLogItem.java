package com.weiwei.base.item;

import java.io.Serializable;

/**
 * 通话记录对象
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
public class VsCallLogItem implements Serializable {
	public String local = "";// 归属地
	public String callName = "";
	public String callNumber = "";
	public long calltimestamp = -999;// 是距离现在多久
	public String calltimelength = "";// 持续多久
	public String ctype = "";// 1 get,2 send,3 miss
	public int directCall = 2;// 是否直拨 0直拨1免费 2回拨（通话记录3为免费）
	public String callmoney = "";// 通话时长
	/**
	 * false不是好友、true好友
	 */
	public boolean isVs=false; 
	

	public VsCallLogItem() {
		callName = "";
		callNumber = "";
		calltimestamp = -999;
		calltimelength = "";
		callmoney = "";
		directCall = 2;
		local = "";// 归属地
		isVs=false;
	}

	public VsCallLogItem(String mcallName, String mcallNumber, long mcalltimestamp, String mcalltimelength,
			int mdirectcall, String mlocal,boolean misVs) {
		callName = mcallName;
		callNumber = mcallNumber;
		calltimestamp = mcalltimestamp;
		calltimelength = mcalltimelength;
		directCall = mdirectcall;
		local = mlocal;
		isVs=misVs;
	}

	@Override
	public String toString() {
		return "VsCallLogItem [local=" + local + ", callName=" + callName + ", callNumber=" + callNumber
				+ ", calltimestamp=" + calltimestamp + ", calltimelength=" + calltimelength + ",  callmoney="
				+ callmoney + ",ctype=" + ctype + ", directCall=" + directCall + ",isVs="+isVs+"]";
	}
}