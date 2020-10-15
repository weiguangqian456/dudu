package com.weiwei.home.yd;

import com.weiwei.home.entity.ReadyItemClassifyTitleEntity;

/**
 * Created by hc on 2019/7/18.
 * Description:
 */

public class YDLocalClassEntity implements ReadyItemClassifyTitleEntity {

    private String id;
    private String appId;
    private String columnName;

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

    @Override
    public String getTitle() {
        return columnName;
    }

    @Override
    public String toString() {
        return "YDLocalClassEntity{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
