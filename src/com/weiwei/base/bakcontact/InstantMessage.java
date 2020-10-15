package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class InstantMessage {
	public String lable;
	public String service;
	public String userName;

	public InstantMessage() {

	}

	public InstantMessage(String lable, String service, String userName) {
		this.lable = lable;
		this.service = service;
		this.userName = userName;
	}

	public static InstantMessage valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String service = jsonObject.has("service") ? jsonObject.getString("service") : null;
			final String userName = jsonObject.has("userName") ? jsonObject.getString("userName") : null;
			return new InstantMessage(lable, service, userName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
