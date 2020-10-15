package com.weiwei.base.item;

import android.os.Parcel;
import android.os.Parcelable;

public class VsInviteItem implements Parcelable {
	/**
	 * 字界面标题
	 */
	private String title;
	/**
	 * 一级名称
	 */
	private String name;
	/**
	 * 商品编号
	 */
	private String goods_id;
	/**
	 * 一级跳转链接
	 */
	private String url;
	/**
	 * 按钮名称、子界面
	 */
	private String btn_name;
	/**
	 * 按钮跳转链接、子界面
	 */
	private String jump_url;
	/**
	 * 任务类型
	 */
	private String tasktype;
	/**
	 * 提示信息、子界面
	 */
	private String tips;
	/**
	 * 金额、子界面
	 */
	private String total_money;
	/**
	 * 一级名称 子标题
	 */
	private String second_name;
	/**
	 * 分钟数、子界面
	 */
	private String total_min;

	public static final Parcelable.Creator<VsInviteItem> CREATOR = new Parcelable.Creator<VsInviteItem>() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public VsInviteItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			VsInviteItem item = new VsInviteItem();
			item.title = source.readString();
			item.name = source.readString();
			item.goods_id = source.readString();
			item.url = source.readString();
			item.btn_name = source.readString();
			item.jump_url = source.readString();
			item.tasktype = source.readString();
			item.tips = source.readString();
			item.total_money = source.readString();
			item.second_name = source.readString();
			item.total_min = source.readString();
			return item;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public VsInviteItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new VsInviteItem[size];
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(title);
		dest.writeString(name);
		dest.writeString(goods_id);
		dest.writeString(url);
		dest.writeString(btn_name);
		dest.writeString(jump_url);
		dest.writeString(tasktype);
		dest.writeString(tips);
		dest.writeString(total_money);
		dest.writeString(second_name);
		dest.writeString(total_min);
	};

	/**
	 * 获取name
	 * 
	 * @return name
	 */

	public String getName() {
		return name;
	}

	/**
	 * 设置name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取goods_id
	 * 
	 * @return goods_id
	 */

	public String getGoods_id() {
		return goods_id;
	}

	/**
	 * 设置goods_id
	 * 
	 * @param goods_id
	 */
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	/**
	 * 获取url
	 * 
	 * @return url
	 */

	public String getUrl() {
		return url;
	}

	/**
	 * 设置url
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取btn_name
	 * 
	 * @return btn_name
	 */

	public String getBtn_name() {
		return btn_name;
	}

	/**
	 * 设置btn_name
	 * 
	 * @param btn_name
	 */
	public void setBtn_name(String btn_name) {
		this.btn_name = btn_name;
	}

	/**
	 * 获取jump_url
	 * 
	 * @return jump_url
	 */

	public String getJump_url() {
		return jump_url;
	}

	/**
	 * 设置jump_url
	 * 
	 * @param jump_url
	 */
	public void setJump_url(String jump_url) {
		this.jump_url = jump_url;
	}

	/**
	 * 获取tasktype
	 * 
	 * @return tasktype
	 */

	public String getTasktype() {
		return tasktype;
	}

	/**
	 * 设置tasktype
	 * 
	 * @param tasktype
	 */
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	/**
	 * 获取tips
	 * 
	 * @return tips
	 */

	public String getTips() {
		return tips;
	}

	/**
	 * 设置tips
	 * 
	 * @param tips
	 */
	public void setTips(String tips) {
		this.tips = tips;
	}

	/**
	 * 获取total_money
	 * 
	 * @return total_money
	 */

	public String getTotal_money() {
		return total_money;
	}

	/**
	 * 设置total_money
	 * 
	 * @param total_money
	 */
	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}

	/**
	 * 获取second_name
	 * 
	 * @return second_name
	 */

	public String getSecond_name() {
		return second_name;
	}

	/**
	 * 设置second_name
	 * 
	 * @param second_name
	 */
	public void setSecond_name(String second_name) {
		this.second_name = second_name;
	}

	/**
	 * 获取total_min
	 * 
	 * @return total_min
	 */

	public String getTotal_min() {
		return total_min;
	}

	/**
	 * 设置total_min
	 * 
	 * @param total_min
	 */
	public void setTotal_min(String total_min) {
		this.total_min = total_min;
	}

	/**
	 * 获取title
	 * 
	 * @return title
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * 设置title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
