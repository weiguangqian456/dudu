package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;
import com.weiwei.home.utils.TextDisposeUtils;

/**
 * @author: Created by EDZ on 2018/7/19.
 */

public class ProductBean {
    private String productId;
    private String productName;
    private String productNo;
    private int spu;
    private int type;
    private String picture;
    private String jdPrice;
    private String price;
    private String appId;
    private int contrastSource;

    public String getConversionPrice() {
        return conversionPrice;
    }

    public void setConversionPrice(String conversionPrice) {
        this.conversionPrice = conversionPrice;
    }

    public String getCoupon() {
        return coupon;
    }

    public String getCouponGoods() {
        return coupon + "加油余额";
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    private String conversionPrice;  //兑换价格
    private String coupon;           //优惠券

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
//        return  Constant.compareFactory[contrastSource] + "：￥" + jdPrice;
        return TextDisposeUtils.dispseMoneyText(jdPrice);
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

    public int getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(int contrastSource) {
        this.contrastSource = contrastSource;
    }
}
