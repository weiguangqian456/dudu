package com.weiwei.base.item;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class VsMakeMoenyItemList implements Parcelable {
	private int id;
	private ArrayList<VsMakeMoneyAppItem> app_list;

	public VsMakeMoenyItemList() {
		app_list = new ArrayList<VsMakeMoneyAppItem>();
	}

	public static final Parcelable.Creator<VsMakeMoenyItemList> CREATOR = new Parcelable.Creator<VsMakeMoenyItemList>() {
		@SuppressWarnings("unchecked")
		public VsMakeMoenyItemList createFromParcel(Parcel in) {
			VsMakeMoenyItemList item = new VsMakeMoenyItemList();
			item.id = in.readInt();
			item.app_list = in.readArrayList(VsMakeMoneyAppItem.class.getClassLoader());
			return item;
		}

		public VsMakeMoenyItemList[] newArray(int size) {
			return new VsMakeMoenyItemList[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeList(app_list);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<VsMakeMoneyAppItem> getApp_list() {
		return app_list;
	}

	public void setApp_list(ArrayList<VsMakeMoneyAppItem> app_list) {
		this.app_list = app_list;
	}
}