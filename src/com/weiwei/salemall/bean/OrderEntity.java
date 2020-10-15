package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;

import java.util.List;

/**
 * @author Created by EDZ on 2018/6/21.
 *         订单实体
 */

public class OrderEntity {
    private String orderId;
    private String orderNo;
    private String storeName;
    private String productNo;
    private String productPrice;
    private int productNum;
    private String productName;
    private String orderPicture;
    private String jdPrice;
    private String economicalMoney;
    private String appId;
    private String uid;
    private int totalNum;
    private List<OrderDetailBean> orderDetails;
    private ExpressInfoBean expressInfo;
    private String totalAmount;    //小计
    private String orderTime;
    private String orderStatus;
    private String isFreePostage;
    private String postage;
    private String productModeDesc;
    private String productAmount;  //商品金额
    private String orderType;
    private String localBy;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public String getOrderPicture() {
        return orderPicture;
    }

    public void setOrderPicture(String orderPicture) {
        this.orderPicture = orderPicture;
    }

    public String getJdPrice() {
        return Constant.compareFactory[0] + "￥：" + jdPrice;
//        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getEconomicalMoney() {
        return economicalMoney;
    }

    public void setEconomicalMoney(String economicalMoney) {
        this.economicalMoney = economicalMoney;
    }

    public String getProductModeDesc() {
        return productModeDesc;
    }

    public void setProductModeDesc(String productModeDesc) {
        this.productModeDesc = productModeDesc;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }


    public List<OrderDetailBean> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailBean> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ExpressInfoBean getExpressInfo() {
        return expressInfo;
    }

    public void setExpressInfo(ExpressInfoBean expressInfo) {
        this.expressInfo = expressInfo;
    }

    public String getLocalBy() {
        return localBy;
    }

    public void setLocalBy(String localBy) {
        this.localBy = localBy;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "orderId='" + orderId + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ",storeName='" + storeName + '\'' +
                ", productNo='" + productNo + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productNum=" + productNum +
                ", productName='" + productName + '\'' +
                ", orderPicture='" + orderPicture + '\'' +
                ", jdPrice='" + jdPrice + '\'' +
                ", economicalMoney='" + economicalMoney + '\'' +
                ", appId='" + appId + '\'' +
                ", uid='" + uid + '\'' +
                ", totalNum=" + totalNum +
                ", orderDetails=" + orderDetails +
                ", expressInfo=" + expressInfo +
                ", totalAmount='" + totalAmount + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", isFreePostage='" + isFreePostage + '\'' +
                ", postage='" + postage + '\'' +
                ", productModeDesc='" + productModeDesc + '\'' +
                ", productAmount='" + productAmount + '\'' +
                ", orderType='" + orderType + '\'' +
                ",localBy='" + localBy + '\'' +
                '}';
    }
}


