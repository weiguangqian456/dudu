package com.weiwei.salemall.bean;

import android.text.TextUtils;

/**
 * @author Created by EDZ on 2018/12/6.
 *         Describe  秒杀商品参数实体
 */

public class SeckillProductEntity {
    private String seckillProductId;
    private String productName;
    private String productNo;
    private String stock;
    private String picture;
    private String seckillPrice;
    private String price;
    private String appId;
    private String jdUrl;
    private String startTime;
    private String endTime;
    private String totalStock;
    private String status;
    private int contrastSource; //来源类型

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeckillProductId() {
        return seckillProductId;
    }

    public void setSeckillProductId(String seckillProductId) {
        this.seckillProductId = seckillProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(String seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getJdUrl() {
        return jdUrl;
    }

    public void setJdUrl(String jdUrl) {
        this.jdUrl = jdUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(String totalStock) {
        this.totalStock = totalStock;
    }

    public int getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(int contrastSource) {
        this.contrastSource = contrastSource;
    }
//    private String[] compareFactory = new String[]{"亚马逊", "京东价", "淘宝价", "天猫价", "苏宁价", "当当价", "国美价", "其他"};
    public String getShopSource(){
        return "";
    }
}
