package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class RelationLable {
	public String lable;
	public String name;

	public RelationLable() {

	}

	public RelationLable(String lable, String name) {
		this.lable = lable;
		this.name = name;
	}

	public static RelationLable valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String name = jsonObject.has("name") ? jsonObject.getString("name") : null;
			return new RelationLable(lable, name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
