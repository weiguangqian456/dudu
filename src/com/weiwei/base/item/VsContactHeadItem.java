package com.weiwei.base.item;

import java.io.Serializable;

/**
 * 联系人信息<好友邀请列表使用>
 * 
 * @author Jiangxuewu
 *
 */
public class VsContactHeadItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5400603874243735665L;
	private String id;//联系人id，可能相同
    private String uid;//用户唯一ID, 也是融云id
	private String name;// 姓名
	private String number;// 号码
	private String local;// 号码归属地
	private String headPath;// 头像
	private boolean isShowHead;//是否显示头像
	private boolean isSelected;// 是否选中, 好友邀请列表中选中联系人
    private boolean isMembers;// 是否是群组中成员了，是则不可点击
	private boolean isFirstLetter;// 是否该字母的第一位
	private String firstLetter;// 首字母
	
	public String input = "";//搜索


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String id) {
		this.uid = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public boolean isMembers() {
		return isMembers;
	}

	public void setMembers(boolean isMembers) {
		this.isMembers = isMembers;
	}

	public String getHeadPath() {
		return headPath;
	}

	public void setHeadPath(String headPath) {
		this.headPath = headPath;
	}
	

	public boolean isShowHead() {
		return isShowHead;
	}

	public void setShowHead(boolean isShowHead) {
		this.isShowHead = isShowHead;
	}

	public boolean isFirstLetter() {
		return isFirstLetter;
	}

	public void setFirstLetter(boolean isFirstLetter) {
		this.isFirstLetter = isFirstLetter;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

}
