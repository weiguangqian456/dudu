package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/7/28.
 *         非商企栏目下数据Data层
 */

public class NormalColumnDataBean {
    private Object data;
    private String appId;
    private String  columnId;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}