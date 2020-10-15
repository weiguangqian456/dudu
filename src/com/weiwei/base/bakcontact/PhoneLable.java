package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class PhoneLable {
	public String lable;
	public String phoneNumber;

	public PhoneLable() {
	}

	public PhoneLable(String lable, String phoneNumber) {
		this.lable = lable;
		this.phoneNumber = phoneNumber;
	}

	public static PhoneLable valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String phoneNumber = jsonObject.has("phoneNumber") ? jsonObject.getString("phoneNumber") : null;
			return new PhoneLable(lable, phoneNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
