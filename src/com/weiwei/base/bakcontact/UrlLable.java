package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class UrlLable {
	public String lable;
	public String url;

	public UrlLable() {

	}

	public UrlLable(String lable, String url) {
		this.lable = lable;
		this.url = url;
	}

	public static UrlLable valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String url = jsonObject.has("url") ? jsonObject.getString("url") : null;
			return new UrlLable(lable, url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
