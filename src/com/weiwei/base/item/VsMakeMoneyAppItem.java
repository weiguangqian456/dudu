package com.weiwei.base.item;

import android.os.Parcel;
import android.os.Parcelable;

public class VsMakeMoneyAppItem implements Parcelable {
	// 标题
	private String title;
	// 图片地址
	private String imgurl;
	// 跳转方式
	private String app_type;
	// 跳转地址
	private String target;
	// 下载地址
	private String down_url;
	// 版本号
	private String pkg_version;
	// 排序
	private String des;
	// 提示
	private String tips;
	// 是否显示新图片
	private String tips_image;
	// 显示新图片的3种类型
	private String tips_imageType;
	// 显示新图片的3种类型
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VsMakeMoneyAppItem() {
	}

	public VsMakeMoneyAppItem(String title, String imgurl, String app_type, String target, String down_url,
			String pkg_version, String des, String tips, String tips_image, String tipsImageType,String description) {
		this.title = title;
		this.imgurl = imgurl;
		this.app_type = app_type;
		this.target = target;
		this.down_url = down_url;
		this.pkg_version = pkg_version;
		this.des = des;
		this.tips = tips;
		this.tips_image = tips_image;
		this.tips_imageType = tipsImageType;
		this.description = description;
		
	}

	public static final Parcelable.Creator<VsMakeMoneyAppItem> CREATOR = new Parcelable.Creator<VsMakeMoneyAppItem>() {
		public VsMakeMoneyAppItem createFromParcel(Parcel in) {
			return new VsMakeMoneyAppItem(in.readString(), in.readString(), in.readString(), in.readString(),
					in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
					in.readString(),in.readString());
		}

		public VsMakeMoneyAppItem[] newArray(int size) {
			return new VsMakeMoneyAppItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(imgurl);
		dest.writeString(app_type);
		dest.writeString(target);
		dest.writeString(down_url);
		dest.writeString(pkg_version);
		dest.writeString(des);
		dest.writeString(tips);
		dest.writeString(tips_image);
		dest.writeString(tips_imageType);
		dest.writeString(description);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getDown_url() {
		return down_url;
	}

	public void setDown_url(String down_url) {
		this.down_url = down_url;
	}

	public String getPkg_version() {
		return pkg_version;
	}

	public void setPkg_version(String pkg_version) {
		this.pkg_version = pkg_version;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getTipsImage() {
		return tips_image;
	}

	public void setTipsImage(String tipsImage) {
		this.tips_image = tipsImage;
	}

	public String getTipsImageType() {
		return tips_imageType;
	}

	public void setTipsImageType(String tipsImageType) {
		this.tips_imageType = tipsImageType;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}
}