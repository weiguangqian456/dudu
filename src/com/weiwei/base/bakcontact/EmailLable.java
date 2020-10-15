package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class EmailLable {
	public String lable;
	public String email;

	public EmailLable() {

	}

	public EmailLable(String lable, String email) {
		this.lable = lable;
		this.email = email;
	}

	public static EmailLable valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String email = jsonObject.has("email") ? jsonObject.getString("email") : null;
			return new EmailLable(lable, email);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
