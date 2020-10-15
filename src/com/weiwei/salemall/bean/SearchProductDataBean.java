package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/7/30.
 *         搜索商品结果Data层
 */

public class SearchProductDataBean {
    private int orderRule;
    private Object data;
    private String columnId;
    private String appId;
    private String keyword;

    public int getOrderRule() {
        return orderRule;
    }

    public void setOrderRule(int orderRule) {
        this.orderRule = orderRule;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
