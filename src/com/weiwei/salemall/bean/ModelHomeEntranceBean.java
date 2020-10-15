package com.weiwei.salemall.bean;

import com.weiwei.home.entity.ReadyItemClassifyTitleEntity;

/**
 * @author Created by EDZ on 2018/7/26
 *         主界面分类实体
 */
public class ModelHomeEntranceBean implements ReadyItemClassifyTitleEntity {
    private String id;
    private String appId;
    private String columnName;
    private String iconUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
    private int type;
    private String androidClassName;
    private String skipUrl;
    private String isExchange;    //是否兑换专区

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
    }

    public String getSkipUrl() {
        return skipUrl;
    }

    public void setSkipUrl(String skipUrl) {
        this.skipUrl = skipUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAndroidClassName() {
        return androidClassName;
    }

    public void setAndroidClassName(String androidClassName) {
        this.androidClassName = androidClassName;
    }

    @Override
    public String getTitle() {
        return columnName;
    }

    @Override
    public String toString() {
        return "ModelHomeEntranceBean{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", columnName='" + columnName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                ", androidClassName='" + androidClassName + '\'' +
                ", skipUrl='" + skipUrl + '\'' +
                ", isExchange='" + isExchange + '\'' +
                '}';
    }
}
