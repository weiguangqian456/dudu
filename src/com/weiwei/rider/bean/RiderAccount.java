package com.weiwei.rider.bean;

import java.io.Serializable;

public class RiderAccount implements Serializable{

    /**
     *   "id": 10,
     "uid": "923721",
     "phone": "17585722933",
     "totalRedPacket": "0",
     "haveWithdrawal": "0",
     "notWithdrawal": "0",
     "status": "y",
     "freezeWithdrawal": "0",
     "createTime": 1591846995000,
     "appId": "dudu",
     "isWxId": null
     */

    private Long id;

    private String uid;

    private String phone;

    private String totalRedPacket;

    private String haveWithdrawal;

    private String freezeWithdrawal;

    private String notWithdrawal;

    private String isWxId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotalRedPacket() {
        return totalRedPacket;
    }

    public void setTotalRedPacket(String totalRedPacket) {
        this.totalRedPacket = totalRedPacket;
    }

    public String getHaveWithdrawal() {
        return haveWithdrawal;
    }

    public void setHaveWithdrawal(String haveWithdrawal) {
        this.haveWithdrawal = haveWithdrawal;
    }

    public String getFreezeWithdrawal() {
        return freezeWithdrawal;
    }

    public void setFreezeWithdrawal(String freezeWithdrawal) {
        this.freezeWithdrawal = freezeWithdrawal;
    }

    public String getNotWithdrawal() {
        return notWithdrawal;
    }

    public void setNotWithdrawal(String notWithdrawal) {
        this.notWithdrawal = notWithdrawal;
    }

    public String getIsWxId() {
        return isWxId;
    }

    public void setIsWxId(String isWxId) {
        this.isWxId = isWxId;
    }

    @Override
    public String toString() {
        return "RiderAccount{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", phone='" + phone + '\'' +
                ", totalRedPacket='" + totalRedPacket + '\'' +
                ", haveWithdrawal='" + haveWithdrawal + '\'' +
                ", freezeWithdrawal='" + freezeWithdrawal + '\'' +
                ", notWithdrawal='" + notWithdrawal + '\'' +
                ", isWxId='" + isWxId + '\'' +
                '}';
    }
}
