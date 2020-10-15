package com.weiwei.base.item;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class VsCallLogListItem implements Serializable {
	private ArrayList<VsCallLogItem> childs;
	   private ArrayList<VsCallLogItem> missChilds;
	public ArrayList<VsCallLogItem> getMissChilds() {
     return missChilds;
 }

 public void setMissChilds(ArrayList<VsCallLogItem> missChilds) {
     this.missChilds = missChilds;
 }

 /**
	 * 是否已经查找了归属地数据
	 */
	private boolean foundLocal;
	/**
	 * 归属地
	 */
	private String localName;

	public VsCallLogListItem() {
		childs = new ArrayList<VsCallLogItem>();
		missChilds = new ArrayList<VsCallLogItem>();
	}

	/**
	 * 得到最近的通话记录
	 * 
	 * @return
	 */
	public VsCallLogItem getFirst() {
		if (childs.size() > 0) {
			return childs.get(0);
		}
		return null;
	}

	public int getMissChildSize() {
		return missChilds.size();
	}

	public VsCallLogItem getMissFirst() {
     if (missChilds.size() > 0) {
         return missChilds.get(0);
     }
     return null;
 }
 

 public int getChildSize() {
     return childs.size();
 }
	public boolean isFoundLocal() {
		return foundLocal;
	}

	public ArrayList<VsCallLogItem> getChilds() {
		//为了不崩溃
		if(childs == null){
			ArrayList<VsCallLogItem> childs2 = new ArrayList<>();
			childs2.add(new VsCallLogItem("","0",0,"0",0,"",false));
			return childs2;
		}
		return childs;
	}

	public void setFoundLocal(boolean foundLocal) {
		this.foundLocal = foundLocal;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	/**
	 * @param childs the childs to set
	 */
	public void setChilds(ArrayList<VsCallLogItem> childs) {
		this.childs = childs;
	}
	
}
