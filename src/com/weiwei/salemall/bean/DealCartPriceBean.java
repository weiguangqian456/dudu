package com.weiwei.salemall.bean;

import java.io.Serializable;

/**
 * @author Created by EDZ on 2018/7/10.
 *         处理购物车价格
 */

public class DealCartPriceBean implements Serializable {
    private String productNo;
    private int productNum;
    private String property;
    private String columnId;
    private String deliveryType;

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }
}
