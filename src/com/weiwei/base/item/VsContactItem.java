package com.weiwei.base.item;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 联系人对象
 * 
 * @author Administrator
 * 
 */
public class VsContactItem implements Parcelable {
	public byte isSelect = -1;
	/**
	 * 联系人唯一标识
	 */
	public String mContactId = "";  
	/**
	 * 联系人名字
	 */
	public String mContactName = ""; // 
	/**
	 * 联系人号码
	 */
	public String mContactPhoneNumber = ""; 
	/**
	 * 街道
	 */
	public String mContactStreet = ""; 
	/**
	 * 联系人第一个字的第一个字母
	 */
	public String mContactFirstLetter = ""; 
	/**
	 * 住宅/手机/单位
	 */
	public String mContactType = "";  
	/**
	 * 归属地
	 */
	public String mContactBelongTo = ""; 
	/**
	 * 是否是飞音用户 1:是 0：不是
	 */
	public String mContactIsYxUser = "";  
	/**
	 * 联系人头像id
	 */
	public String mContactPhotoId = "";  
	public String mSign = ""; // 个性签名
	public int isShow = 0;
	/**
	 * 匹配索引
	 */
	public int mMatchIndex = 40; 
	/**
	 * T9搜索上色索引 0 1:首字母 、2：全拼 、3：号码 、4:拼音小写、5：联系人名、6 7:拼音首字母
	 */
	public int mIndex = 5; 
	/**
	 * T9搜索输入的数字
	 */
	public String mInput = "";  
	/**
	 * T9首字母大写
	 */
	public String mContactFirstUpper = "";
	/**
	 * T9联系人首字母拼音转换成数字
	 */
	public String mContactFirstUpperToNumber = ""; 
	/**
	 *  T9联系人全拼
	 */
	public String mContactPY = ""; 
	/**
	 * T9联系人全拼转换成数字
	 */
	public String mContactPYToNumber = ""; 
	public ArrayList<String> phoneNumList = new ArrayList<String>();
	public ArrayList<String> localNameList = new ArrayList<String>();
	public ArrayList<Boolean> ISVsPhone = new ArrayList<Boolean>();
	public ArrayList<String> mContactCliendid = new ArrayList<String>();// 联系人cliendid用户拨打免费电话
	public ArrayList<String> mContactUid= new ArrayList<String>();// 联系人uid发信息
	/**
	 * 是否是VS用户
	 */
	public boolean isVsUser = false;

    public VsContactItem(){

    }
    public VsContactItem(VsContactItem item){
        this.isSelect = item.isSelect;
        this.mContactId = item.mContactId;
        this.mContactName = item.mContactName ;
        this.mContactPhoneNumber = item.mContactPhoneNumber;
        this.mContactStreet = item.mContactStreet;
        this.mContactFirstLetter = item.mContactFirstLetter;
        this.mContactType = item.mContactType;
        this.mContactBelongTo = item.mContactBelongTo;
        this.mContactIsYxUser = item.mContactIsYxUser;
        this.mContactPhotoId = item.mContactPhotoId;
        this.mSign = item.mSign;
        this.isShow = item.isShow;
        this.mMatchIndex = item.mMatchIndex;
        this.mIndex = item.mIndex;
        this.mInput = item.mInput;
        this.mContactFirstUpper = item.mContactFirstUpper;
        this.mContactFirstUpperToNumber = item.mContactFirstUpperToNumber;
        this.mContactPY = item.mContactPY;
        this.mContactPYToNumber = item.mContactPYToNumber;
        this.isVsUser = item.isVsUser;
        this.mContactCliendid = new ArrayList<String>(item.mContactCliendid);
        this.mContactUid= new ArrayList<String>(item.mContactUid);
        this.ISVsPhone = new ArrayList<Boolean>(item.ISVsPhone);
        this.phoneNumList = new ArrayList<String>(item.phoneNumList);
        this.localNameList = new ArrayList<String>(item.localNameList);
    }

	public static final Parcelable.Creator<VsContactItem> CREATOR = new Parcelable.Creator<VsContactItem>() {
		@SuppressWarnings("unchecked")
		public VsContactItem createFromParcel(Parcel in) {
			VsContactItem item = new VsContactItem();
			item.isSelect = in.readByte();
			item.mContactId = in.readString();
			item.mContactName = in.readString();
			item.mContactPhoneNumber = in.readString();
			item.mContactStreet = in.readString();
			item.mContactFirstLetter = in.readString();
			item.mContactType = in.readString();
			item.mContactBelongTo = in.readString();
			item.mContactIsYxUser = in.readString();
			item.mContactPhotoId = in.readString();
			item.mSign = in.readString();
			item.isShow = in.readInt();
			item.mMatchIndex = in.readInt();
			item.mIndex = in.readInt();
			item.mInput = in.readString();
			item.mContactFirstUpper = in.readString();
			item.mContactFirstUpperToNumber = in.readString();
			item.mContactPY = in.readString();
			item.mContactPYToNumber = in.readString();
			item.isVsUser = in.readByte() != 0;
			item.mContactCliendid = in.readArrayList(String.class.getClassLoader());
			item.mContactUid=in.readArrayList(String.class.getClassLoader());
			item.ISVsPhone = in.readArrayList(Boolean.class.getClassLoader());
			item.phoneNumList = in.readArrayList(String.class.getClassLoader());
			item.localNameList = in.readArrayList(String.class.getClassLoader());
			return item;
		}

		public VsContactItem[] newArray(int size) {
			return new VsContactItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeByte(isSelect);
		dest.writeString(mContactId);
		dest.writeString(mContactName);
		dest.writeString(mContactPhoneNumber);
		dest.writeString(mContactStreet);
		dest.writeString(mContactFirstLetter);
		dest.writeString(mContactType);
		dest.writeString(mContactBelongTo);
		dest.writeString(mContactIsYxUser);
		dest.writeString(mContactPhotoId);
		dest.writeString(mSign);
		dest.writeInt(isShow);
		dest.writeInt(mMatchIndex);
		dest.writeInt(mIndex);
		dest.writeString(mInput);
		dest.writeString(mContactFirstUpper);
		dest.writeString(mContactFirstUpperToNumber);
		dest.writeString(mContactPY);
		dest.writeString(mContactPYToNumber);
		dest.writeByte((byte) (isVsUser ? 1 : 0));
		dest.writeList(mContactCliendid);
		dest.writeList(mContactUid);
		dest.writeList(ISVsPhone);
		dest.writeList(phoneNumList);
		dest.writeList(localNameList);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
