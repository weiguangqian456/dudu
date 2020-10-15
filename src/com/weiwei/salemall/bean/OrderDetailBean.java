package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/7/9.
 */

public class OrderDetailBean {
    private String productNo;
    private String productPrice;
    private int productNum;
    private String productName;
    private String picture;
    private String jdPrice;
    private String productModeDesc;
    private String deliveryType;
    private String columnId = "";
    private String coupon = "0";
    private String isExchange = "n";

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getProductModeDesc() {
        return productModeDesc;
    }

    public void setProductModeDesc(String productModeDesc) {
        this.productModeDesc = productModeDesc;
    }

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
    }

    public boolean isShowCoupon(){
        return isExchange.equals("y");
    }

    @Override
    public String toString() {
        return "OrderDetailBean{" +
                "productNo='" + productNo + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productNum=" + productNum +
                ", productName='" + productName + '\'' +
                ", picture='" + picture + '\'' +
                ", jdPrice='" + jdPrice + '\'' +
                ", productModeDesc='" + productModeDesc + '\'' +
                ", deliveryType='" + deliveryType + '\'' +
                ", columnId='" + columnId + '\'' +
                ", coupon='" + coupon + '\'' +
                ", isExchange='" + isExchange + '\'' +
                '}';
    }
}
