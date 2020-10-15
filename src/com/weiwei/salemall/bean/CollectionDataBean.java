package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/7/30.
 *         收藏Data层
 */

public class CollectionDataBean {
    private String uid;
    private String appId;
    private Object data;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
