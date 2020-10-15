package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/6/21.
 *         处理价格 data 层
 */

public class DealPriceDataEntity {
    private String postage;
    private String isFreePostage;
    private String originalAmount;
    private String discountAmount;
    private String discount;
    private String productMoney;
    private String remark;
    private String coupon;  //加油余额
    private String logisticsPostage;   //预估物流费
    private String logisticsExplain;  //物流说明字段

    public String getLogisticsPostage() {
        return logisticsPostage;
    }

    public void setLogisticsPostage(String logisticsPostage) {
        this.logisticsPostage = logisticsPostage;
    }

    public String getLogisticsExplain() {
        return logisticsExplain;
    }

    public void setLogisticsExplain(String logisticsExplain) {
        this.logisticsExplain = logisticsExplain;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getProductMoney() {
        return productMoney;
    }

    public void setProductMoney(String productMoney) {
        this.productMoney = productMoney;
    }

    public String getIsFreePostage() {
        return isFreePostage;
    }

    public void setIsFreePostage(String isFreePostage) {
        this.isFreePostage = isFreePostage;
    }

    public String getPostage() {
        return postage;
    }

    public void setPostage(String postage) {
        this.postage = postage;
    }

    public String getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }
}
