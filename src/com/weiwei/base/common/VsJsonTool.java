package com.weiwei.base.common;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

/**
 * JSON工具类
 * 
 * @author del1
 * 
 */
public class VsJsonTool {

	/**
	 * 返回字符串
	 * 
	 * @param getObj
	 * @param item
	 * @return
	 */
	public static String GetStringFromJSON(JSONObject getObj, String item) {
		String getStr = "";
		try {
			getStr = String.valueOf(getObj.get(item));
			if (getStr.endsWith("null")) {
				getStr = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getStr;
	}

	/**
	 * 返回JSON对象
	 * 
	 * @param getObj
	 * @param item
	 * @return
	 */
	public static JSONObject GetJsonFromJSON(JSONObject getObj, String item) {
		JSONObject getStr = null;
		try {
			getStr = getObj.getJSONObject(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getStr;
	}

	/**
	 * 返回数字
	 * 
	 * @param getObj
	 * @param item
	 * @return
	 */
	public static int GetIntegerFromJSON(JSONObject getObj, String item) {
		int getInt = -1000;
		try {
			getInt = Integer.valueOf(String.valueOf(getObj.get(item)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getInt;
	}
}
