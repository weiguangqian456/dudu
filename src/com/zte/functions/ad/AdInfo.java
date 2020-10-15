package com.zte.functions.ad;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

/**
 * Created by Jiangxuewu on 2015/3/19.
 * <p>
 * 每条广告信息，一个adno
 * </p>
 */
public class AdInfo {


    public static final String CID = "cid";
    public static final String ADNO = "adno";
    public static final String AD_CACHE_TIME = "ad_cache_time";
    public static final String AD_CONFIG = "ad_config";

    private String cid;
    private String adNo;
    private long adCacheTime;//服务器单位分钟，客户端保存毫秒单位
    private List<AdConfig> adConfigs;

    public AdInfo(JSONObject jsonObject) {
        if (null == jsonObject)
            return;
        ;

        if (jsonObject.has(CID)) {
            try {
                cid = jsonObject.getString(CID);
            } catch (JSONException e) {

            }
        }
        if (jsonObject.has(ADNO)) {
            try {
                adNo = jsonObject.getString(ADNO);
            } catch (JSONException e) {

            }
        }
        if (jsonObject.has(AD_CACHE_TIME)) {
            try {
                adCacheTime = jsonObject.getLong(AD_CACHE_TIME) * 60 * 1000;
            } catch (JSONException e) {
            }
        }
        if (jsonObject.has(AD_CONFIG)) {
            try {
                JSONObject json = jsonObject.getJSONObject(AD_CONFIG);
                if (null != json && json.length() > 0) {
                    adConfigs = new ArrayList<AdConfig>();
                    int size = AdConfig.mPages.length;
                    if (size > 0) {
                        for (String pageId : AdConfig.mPages) {
                            if (json.has(pageId)) {
                                try {
                                    JSONObject jsonjson = json.getJSONObject(pageId);
                                    if (null != json) {
                                        adConfigs.add(new AdConfig(jsonjson));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
            }
        }

    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getAdNo() {
        return adNo;
    }

    public void setAdNo(String adNo) {
        this.adNo = adNo;
    }

    public long getAdCacheTime() {
        return adCacheTime;
    }

    public void setAdCacheTime(long adCacheTime) {
        this.adCacheTime = adCacheTime;
    }

    public List<AdConfig> getAdConfig() {
        return adConfigs;
    }

    public void setAdConfig(List<AdConfig> adConfigs) {
        this.adConfigs = adConfigs;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public boolean save(Context context, String json) {

        VsUserConfig.setData(context, AdManager.KEY_SP_FILE, json);

        if (null != adConfigs) {
            for (AdConfig c : adConfigs) {
                List<AdData> datas = c.getAdData();
                if (null != datas) {
                    for (AdData d : datas) {
                        d.downloadImg();
                    }
                }
            }
        }

        return true;
    }

}
