package com.weiwei.merchant.bean;

import java.util.List;

public class Order {

    private String orderNo;

    private Long createTime;

    private String orderType;

    private String totalAmount;

    private String combined;

    private int orderDetailStatus;

    private String riderOrderStatus;

    private String riderId;

    private String delivery;

    private String latitude;

    private String latitudeUser;

    private String longitude;

    private String longitudeUser;

    private String merchantsStatus;

    private List<OrderDetail> orderDetails;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCombined() {
        return combined;
    }

    public void setCombined(String combined) {
        this.combined = combined;
    }

    public int getOrderDetailStatus() {
        return orderDetailStatus;
    }

    public void setOrderDetailStatus(int orderDetailStatus) {
        this.orderDetailStatus = orderDetailStatus;
    }

    public String getRiderOrderStatus() {
        return riderOrderStatus;
    }

    public void setRiderOrderStatus(String riderOrderStatus) {
        this.riderOrderStatus = riderOrderStatus;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(String latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(String longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public String getMerchantsStatus() {
        return merchantsStatus;
    }

    public void setMerchantsStatus(String merchantsStatus) {
        this.merchantsStatus = merchantsStatus;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNo='" + orderNo + '\'' +
                ", createTime=" + createTime +
                ", orderType='" + orderType + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ",combined='" + combined + '\'' +
                ", orderDetailStatus=" + orderDetailStatus +
                ", riderOrderStatus='" + riderOrderStatus + '\'' +
                ", riderId='" + riderId + '\'' +
                ", delivery='" + delivery + '\'' +
                ", latitude='" + latitude + '\'' +
                ", latitudeUser='" + latitudeUser + '\'' +
                ", longitude='" + longitude + '\'' +
                ", longitudeUser='" + longitudeUser + '\'' +
                ", merchantsStatus='" + merchantsStatus + '\'' +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
