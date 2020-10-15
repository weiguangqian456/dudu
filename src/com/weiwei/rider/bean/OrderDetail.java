package com.weiwei.rider.bean;

public class OrderDetail {

    private String picture;

    private int number;

    private String productName;

    private String remark;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

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

    @Override
    public String toString() {
        return "OrderDetail{" +
                "picture='" + picture + '\'' +
                ", number=" + number +
                ", productName='" + productName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
