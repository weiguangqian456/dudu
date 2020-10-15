package com.weiwei.base.item;

import java.util.ArrayList;

import android.text.Html;
import android.text.Spanned;

public class FilterItem {
	/**
	 * 匹配成功方式,0--号码，1--拼音
	 */
	public byte type;
	public ArrayList<FilterColorItem> colorItemList;

	public FilterItem() {
		colorItemList = new ArrayList<FilterColorItem>();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colorItemList.size(); i++) {
			sb.append(colorItemList.get(i).content + ",");
		}
		return sb.toString();
	}

	public Spanned toHtml() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colorItemList.size(); i++) {
			if (colorItemList.get(i).hightlight) {
				sb.append("<font color=#5DB43B>" + colorItemList.get(i).content + "</font>");
			} else {
				sb.append(colorItemList.get(i).content);
			}
		}
		Spanned htm = Html.fromHtml(sb.toString());
		return htm;
	}

}
