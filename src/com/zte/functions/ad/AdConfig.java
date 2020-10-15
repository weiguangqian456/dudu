package com.zte.functions.ad;

import java.util.ArrayList;
import java.util.List;

import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

/**
 * Created by Jiangxuewu on 2015/3/19.
 * <p>
 * 广告配置信息
 * </p>
 */
public class AdConfig {
    /**
     * 页面编号, page id:
     */
    public static final String AD_START_PAGE = "5000";//启动页面
    public static final String AD_CALL_LIVE_PAGE = "5001";//直拨页面
    public static final String AD_IM_PAGE = "5002";//信息页面
    public static final String AD_CALL_PAGE = "0001";//拨号页面

    /**
     * 显示广告的所有页面
     */
    public static final String[] mPages = {AD_START_PAGE, AD_CALL_LIVE_PAGE, AD_IM_PAGE, AD_CALL_PAGE};

    /**
     * 广告位置
     */
    public static final String AD_START_PAGE_LOC = "all";
    public static final String AD_CALL_LIVE_PAGE_LOC = "top";
    public static final String AD_IM_PAGE_LOC = "top";
    public static final String AD_CALL_PAGE_LOC = "top";

    private static final String PAGE_NAME = "page_name";
    private static final String PAGE_ID = "page_id";
    private static final String AD_DATA = "ad_data";

    private String pageName;
    private String pageId;
    private List<AdData> adData;

    public AdConfig(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        if (jsonObject.has(PAGE_NAME)) {
            try {
                pageName = jsonObject.getString(PAGE_NAME);
            } catch (JSONException e) {

            }
        }
        if (jsonObject.has(PAGE_ID)) {
            try {
                pageId = jsonObject.getString(PAGE_ID);
            } catch (JSONException e) {

            }
        }
        if (jsonObject.has(AD_DATA)) {
            JSONObject json = null;
            try {
                json = jsonObject.getJSONObject(AD_DATA);
            } catch (JSONException e) {

            } finally {
                if (null != json && json.length() > 0) {
                    String key;
                    if (AD_START_PAGE.equals(pageId))
                        key = AD_START_PAGE_LOC;
                    else if (AD_CALL_LIVE_PAGE.equals(pageId))
                        key = AD_CALL_LIVE_PAGE_LOC;
                    else if (AD_IM_PAGE.equals(pageId))
                        key = AD_IM_PAGE_LOC;
                    else if (AD_CALL_PAGE.equals(pageId))
                        key = AD_CALL_PAGE_LOC;
                    else
                        key = null;

                    if (null != key && json.has(key)) {
                        try {
                            JSONArray array = json.getJSONArray(key);
                            if (null != array && array.length() > 0) {
                                adData = new ArrayList<AdData>();
                                for (int i = 0; i < array.length(); i++) {
                                    adData.add(new AdData(array.getJSONObject(i)));
                                }
                            }

                        } catch (JSONException e) {
                        }
                    }
                }
            }
        }


    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public List<AdData> getAdData() {
        return adData;
    }

    public void setAdData(List<AdData> adData) {
        this.adData = adData;
    }
}
