package com.weiwei.merchant.bean;

public class OrderDetail {

    private int number;

    private String productName;

    private String remark;

    private String picture;

    private String productPrice;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "number=" + number +
                ", productName='" + productName + '\'' +
                ", remark='" + remark + '\'' +
                ", picture='" + picture + '\'' +
                ", productPrice='" + productPrice + '\'' +
                '}';
    }
}
