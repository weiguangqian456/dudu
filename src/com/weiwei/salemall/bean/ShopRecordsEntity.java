package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;

/**
 * @author Created by EDZ on 2018/6/20.
 */

public class ShopRecordsEntity {
    private String productId;
    private String productName;
    private String productNo;
    private int spu;       //库存
    private int type;   //类型
    private String picture;
    private String jdPrice;
    private String price;
    private String appId;
    private int contrastSource;

    public int getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(int contrastSource) {
        this.contrastSource = contrastSource;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public int getSpu() {
        return spu;
    }

    public void setSpu(int spu) {
        this.spu = spu;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getJdPrice2() {
//        return Constant.compareFactory[0] + "￥：" + jdPrice;
        return Constant.compareFactory[contrastSource] + "￥：" + jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
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
}
