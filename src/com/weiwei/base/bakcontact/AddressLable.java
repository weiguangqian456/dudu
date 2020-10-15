package com.weiwei.base.bakcontact;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class AddressLable {
	public String lable;
	public String zip;
	public String state;
	public String street;
	public String city;
	public String countrykey;
	public String neighborhood;
	public String pobox;

	public AddressLable() {

	}

	public AddressLable(String lable, String zip, String state, String street, String city, String countrykey,
			String neighborhood, String pobox) {
		this.lable = lable;
		this.zip = zip;
		this.state = state;
		this.street = street;
		this.city = city;
		this.countrykey = countrykey;
		this.neighborhood = neighborhood;
		this.pobox = pobox;
	}

	public static AddressLable valueOf(JSONObject jsonObject) {
		try {
			final String lable = jsonObject.has("lable") ? jsonObject.getString("lable") : null;
			final String zip = jsonObject.has("zip") ? jsonObject.getString("zip") : null;
			final String state = jsonObject.has("state") ? jsonObject.getString("state") : null;
			final String street = jsonObject.has("street") ? jsonObject.getString("street") : null;
			final String city = jsonObject.has("city") ? jsonObject.getString("city") : null;
			final String countrykey = jsonObject.has("countrykey") ? jsonObject.getString("countrykey") : null;
			final String neighborhood = jsonObject.has("neighborhood") ? jsonObject.getString("neighborhood") : null;
			final String pobox = jsonObject.has("pobox") ? jsonObject.getString("pobox") : null;
			return new AddressLable(lable, zip, state, street, city, countrykey, neighborhood, pobox);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
