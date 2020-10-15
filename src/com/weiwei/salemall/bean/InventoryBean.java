package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;

/**
 * @author: Created by EDZ on 2018/8/15.
 */

public class InventoryBean {
    private String productNo;
    private String property;
    private String price;
    private String jdPrice;
    private String stock;    //库存
    private String conversionPrice;   //对比
    private String coupon;  //加油余额
    private String isExchange;
    private String imageUrl;   //选中图

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * =================秒杀相关===================
     **/
    private String seckillPrice;  //秒杀价

    public String getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(String seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
    }

    public String getConversionPrice() {
        return conversionPrice;
    }

    public void setConversionPrice(String conversionPrice) {
        this.conversionPrice = conversionPrice;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public String getJdPrice2() {
        return Constant.compareFactory[0] + "￥：" + jdPrice;
//        return Constant.compareFactory[contrastSource] + "￥：" + jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
