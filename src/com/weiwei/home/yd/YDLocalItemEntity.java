package com.weiwei.home.yd;

import android.text.TextUtils;

import com.weiwei.home.Constant;
import com.weiwei.home.utils.TextDisposeUtils;

/**
 * Created by hc on 2019/7/18.
 * Description:
 */

public class YDLocalItemEntity {

    /**
     * productId : e2fe821975964f4c90a36d3ea8368d5c
     * productName : 海信/Hisense 43英寸 全高清智能液晶平板电视 HZ43H35A
     * productNo : 201904161511539629
     * spu : 6446
     * type : 1
     * picture : shop/product/1555398664864.jpg
     * jdPrice : 3099
     * price : 1669.9
     * appId : dudu
     * jdUrl : null
     * contrastSource : 1
     * conversionPrice : null
     * coupon : null
     */

    private String productId;
    private String productName;
    private String productNo;
    private int spu;
    private int type;
    private String picture;
    private String jdPrice;
    private String price;
    private String appId;
    private Object jdUrl;
    private int contrastSource;
    private Object conversionPrice;
    private Object coupon;
    private String subTitle;

    public String getSubTitle() {
        return TextUtils.isEmpty(subTitle) ? "" : subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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
        return  Constant.compareFactory[contrastSource] + "￥：" + jdPrice;
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

    public Object getJdUrl() {
        return jdUrl;
    }

    public void setJdUrl(Object jdUrl) {
        this.jdUrl = jdUrl;
    }

    public int getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(int contrastSource) {
        this.contrastSource = contrastSource;
    }

    public Object getConversionPrice() {
        return conversionPrice;
    }

    public void setConversionPrice(Object conversionPrice) {
        this.conversionPrice = conversionPrice;
    }

    public Object getCoupon() {
        return coupon;
    }

    public void setCoupon(Object coupon) {
        this.coupon = coupon;
    }
}
