package com.weiwei.salemall.bean;

import java.util.List;

/**
 * ClassName:      OrderRefurlBean
 * <p>
 * Author:
 * <p>
 * CreateDate:      2020/9/21 14:14
 * <p>
 * Description:
 */
public class OrderRefurlBean {
    OrderRefurlBeanData data;
    int code;
    String msg;

    public OrderRefurlBeanData getData() {
        return data;
    }

    public void setData(OrderRefurlBeanData data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    public static class OrderRefurlBeanData {
        List<OrderRefurlOrderList> orderList;
        int total;
        int pages;
        int size;
        String amountPaySum;
        String litreSum;

        public List<OrderRefurlOrderList> getOrderList() {
            return orderList;
        }

        public void setOrderList(List<OrderRefurlOrderList> orderList) {
            this.orderList = orderList;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getAmountPaySum() {
            return amountPaySum;
        }

        public void setAmountPaySum(String amountPaySum) {
            this.amountPaySum = amountPaySum;
        }

        public String getLitreSum() {
            return litreSum;
        }

        public void setLitreSum(String litreSum) {
            this.litreSum = litreSum;
        }
    }



    public static class OrderRefurlOrderList {
       String id;
       String orderId;
       String userId;
       String username;
       String agentName;
       String gasStationId;
       String paySn;
       String phone;
       String orderTime;
       String payTime;
       String gasName;
       String province;
       String city;
       String county;
       String gunNo;
       String oilNo;
       String amountPay;
       String amountGas;
       String amountGun;
       String amountDiscounts;
       String orderStatusName;
       String couponMoney;
       String couponCode;
       String couponId;
       String litre;
       String payType;
       String payChannel;
       String priceUnit;
       String priceOfficial;
       String priceGun;
       String priceGas;
       String orderSource;
       String duduPhone;
       String qrCode4PetroChina;
       String source;
       String paymentType;
       String orderState;
       String gasUserId;
       String gasUsername;
       String gasAgentName;
       String preDepositState;
       String totalAmount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getGasStationId() {
            return gasStationId;
        }

        public void setGasStationId(String gasStationId) {
            this.gasStationId = gasStationId;
        }

        public String getPaySn() {
            return paySn;
        }

        public void setPaySn(String paySn) {
            this.paySn = paySn;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String payTime) {
            this.payTime = payTime;
        }

        public String getGasName() {
            return gasName;
        }

        public void setGasName(String gasName) {
            this.gasName = gasName;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getGunNo() {
            return gunNo;
        }

        public void setGunNo(String gunNo) {
            this.gunNo = gunNo;
        }

        public String getOilNo() {
            return oilNo;
        }

        public void setOilNo(String oilNo) {
            this.oilNo = oilNo;
        }

        public String getAmountPay() {
            return amountPay;
        }

        public void setAmountPay(String amountPay) {
            this.amountPay = amountPay;
        }

        public String getAmountGas() {
            return amountGas;
        }

        public void setAmountGas(String amountGas) {
            this.amountGas = amountGas;
        }

        public String getAmountGun() {
            return amountGun;
        }

        public void setAmountGun(String amountGun) {
            this.amountGun = amountGun;
        }

        public String getAmountDiscounts() {
            return amountDiscounts;
        }

        public void setAmountDiscounts(String amountDiscounts) {
            this.amountDiscounts = amountDiscounts;
        }

        public String getOrderStatusName() {
            return orderStatusName;
        }

        public void setOrderStatusName(String orderStatusName) {
            this.orderStatusName = orderStatusName;
        }

        public String getCouponMoney() {
            return couponMoney;
        }

        public void setCouponMoney(String couponMoney) {
            this.couponMoney = couponMoney;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        public String getCouponId() {
            return couponId;
        }

        public void setCouponId(String couponId) {
            this.couponId = couponId;
        }

        public String getLitre() {
            return litre;
        }

        public void setLitre(String litre) {
            this.litre = litre;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPayChannel() {
            return payChannel;
        }

        public void setPayChannel(String payChannel) {
            this.payChannel = payChannel;
        }

        public String getPriceUnit() {
            return priceUnit;
        }

        public void setPriceUnit(String priceUnit) {
            this.priceUnit = priceUnit;
        }

        public String getPriceOfficial() {
            return priceOfficial;
        }

        public void setPriceOfficial(String priceOfficial) {
            this.priceOfficial = priceOfficial;
        }

        public String getPriceGun() {
            return priceGun;
        }

        public void setPriceGun(String priceGun) {
            this.priceGun = priceGun;
        }

        public String getPriceGas() {
            return priceGas;
        }

        public void setPriceGas(String priceGas) {
            this.priceGas = priceGas;
        }

        public String getOrderSource() {
            return orderSource;
        }

        public void setOrderSource(String orderSource) {
            this.orderSource = orderSource;
        }

        public String getDuduPhone() {
            return duduPhone;
        }

        public void setDuduPhone(String duduPhone) {
            this.duduPhone = duduPhone;
        }

        public String getQrCode4PetroChina() {
            return qrCode4PetroChina;
        }

        public void setQrCode4PetroChina(String qrCode4PetroChina) {
            this.qrCode4PetroChina = qrCode4PetroChina;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getOrderState() {
            return orderState;
        }

        public void setOrderState(String orderState) {
            this.orderState = orderState;
        }

        public String getGasUserId() {
            return gasUserId;
        }

        public void setGasUserId(String gasUserId) {
            this.gasUserId = gasUserId;
        }

        public String getGasUsername() {
            return gasUsername;
        }

        public void setGasUsername(String gasUsername) {
            this.gasUsername = gasUsername;
        }

        public String getGasAgentName() {
            return gasAgentName;
        }

        public void setGasAgentName(String gasAgentName) {
            this.gasAgentName = gasAgentName;
        }

        public String getPreDepositState() {
            return preDepositState;
        }

        public void setPreDepositState(String preDepositState) {
            this.preDepositState = preDepositState;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
