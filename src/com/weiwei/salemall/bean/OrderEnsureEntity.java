package com.weiwei.salemall.bean;

import java.io.Serializable;

/**
 * @author Created by EDZ on 2018/7/6.
 *         确定订单
 */

public class OrderEnsureEntity implements Serializable {
    private Long Id;
    private String productName;
    private String productNo;
    private String productPropery;
    private String productDesc;
    private String productMallPrice;
    private String productJdPrice;
    private int productNum;
    private String productEcnomicalMoney;
    private String productImage;
    private String columnId;
    private String conversionPrice;
    private String coupon;
    private String isExchange;
    private String deliveryType;
    private String deliveryMsg;

    public String getDeliveryMsg() {
        return deliveryMsg;
    }

    public void setDeliveryMsg(String deliveryMsg) {
        this.deliveryMsg = deliveryMsg;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
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

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
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

    public String getProductPropery() {
        return productPropery;
    }

    public void setProductPropery(String productPropery) {
        this.productPropery = productPropery;
    }

    public String getProductMallPrice() {
        return productMallPrice;
    }

    public void setProductMallPrice(String productMallPrice) {
        this.productMallPrice = productMallPrice;
    }

    public String getProductJdPrice() {
        return productJdPrice;
    }

    public void setProductJdPrice(String productJdPrice) {
        this.productJdPrice = productJdPrice;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public String getProductEcnomicalMoney() {
        return productEcnomicalMoney;
    }

    public void setProductEcnomicalMoney(String productEcnomicalMoney) {
        this.productEcnomicalMoney = productEcnomicalMoney;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

}
