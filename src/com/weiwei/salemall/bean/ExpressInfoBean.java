package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/7/9.
 *         物流实体
 */

public class ExpressInfoBean {
    private String logisticInfo;
    private String expressName;
    private String expressNo;
    private String address;
    private String consignee;
    private String shipperCode;
    private String phone;

    public String getLogisticInfo() {
        return logisticInfo;
    }

    public void setLogisticInfo(String logisticInfo) {
        this.logisticInfo = logisticInfo;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
