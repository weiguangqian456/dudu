package com.weiwei.rider.bean;

import java.util.List;

public class Order{

    /**
     *"id": "6d7c499f3ca94a6b9118ae77281ec69f",
     "orderNo": "13301330202006101524371000",
     "uid": null,
     "appId": null,
     "productNo": null,
     "merchantId": "8537bdc9d4af4360b4a6653e2b2283fa",
     "createTime": 1591774046000,
     "updateTime": null,
     "status": null,
     "postage": "0",
     "orderMerchantNo": "13301330202006101527251002",
     "riderId": null,
     "riderOrderStatus": "0",
     "address": "交通银行(深圳高新园支行)6666",
     "storeAddress": "广东省深圳市南山区粤海街道高新园(地铁站)",
     "storePhone": "13106021540",
     "phone": "6666",
     "distance": "460",
     "image": null,
     "orderDetails": null,
     "orderMerchantImages": null,
     "deliveryType": null,
     "totalMoney": null,
     "longitude": null,
     "latitude": null,
     "longitudeUser": null,
     "latitudeUser": null,
     "longitudeLatitude": "113.957639,22.538060999999999",
     "storLongitudeLatitude": "113.95387,22.540282",
     "number": null,
     "orderDetailStatu": "1",
     "delivery": "30分钟",
     "refundStatus": null,
     "merchantsStatus": null,
     "name": null
     */

    private String id;

    private String orderNo;

    private String delivery;

    private String phone;

    private String storePhone;

    private String address;

    private String totalMoney;

    private String storeAddress;

    private String createTime;

    private String riderOrderStatus;

    private String distance;

    private String longitudeLatitude;

    private String storLongitudeLatitude;

    private String orderMerchantNo;

    private String name;

    private List<OrderDetail> orderDetails;

    private String image;

    private List<GoodsImage> orderMerchantImages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getRiderOrderStatus() {
        return riderOrderStatus;
    }

    public void setRiderOrderStatus(String riderOrderStatus) {
        this.riderOrderStatus = riderOrderStatus;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLongitudeLatitude() {
        return longitudeLatitude;
    }

    public void setLongitudeLatitude(String longitudeLatitude) {
        this.longitudeLatitude = longitudeLatitude;
    }

    public String getStorLongitudeLatitude() {
        return storLongitudeLatitude;
    }

    public void setStorLongitudeLatitude(String storLongitudeLatitude) {
        this.storLongitudeLatitude = storLongitudeLatitude;
    }

    public String getOrderMerchantNo() {
        return orderMerchantNo;
    }

    public void setOrderMerchantNo(String orderMerchantNo) {
        this.orderMerchantNo = orderMerchantNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<GoodsImage> getOrderMerchantImages() {
        return orderMerchantImages;
    }

    public void setOrderMerchantImages(List<GoodsImage> orderMerchantImages) {
        this.orderMerchantImages = orderMerchantImages;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ",delivery='" + delivery + '\'' +
                ", phone='" + phone + '\'' +
                ", storePhone='" + storePhone + '\'' +
                ",createTime='" + createTime +'\'' +
                ", address='" + address + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ",totalMoney='" + totalMoney + '\'' +
                ", riderOrderStatus='" + riderOrderStatus + '\'' +
                ", distance='" + distance + '\'' +
                ", longitudeLatitude='" + longitudeLatitude + '\'' +
                ", storLongitudeLatitude='" + storLongitudeLatitude + '\'' +
                ", orderMerchantNo='" + orderMerchantNo + '\'' +
                ", name='" + name + '\'' +
                ",orderDetails=" + orderDetails + '\'' +
                ",image=" + image + '\'' +
                ",orderMerchantImages=" + orderMerchantImages + '\'' +
                '}';
    }
}
