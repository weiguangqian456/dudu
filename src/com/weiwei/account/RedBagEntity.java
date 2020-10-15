package com.weiwei.account;

import android.text.TextUtils;

/**
 * @author Created by EDZ on 2018/10/30.
 *         Describe
 */

public class RedBagEntity {
    private String coupon;  //商品兑换成长金
    private String red;  //红包成长金
    private String invitation = "0"; //邀请红包
    private String level;
    private String levelName;
    private String exchangePoint;
    private String rider;
    /**
     * 签到成长金 - 默认0
     */
    private String signIntegral = "0";

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getSignIntegral() {
        return signIntegral;
    }

    public void setSignIntegral(String signIntegral) {
        this.signIntegral = signIntegral;
    }

    public String getExchangePoint() {
        return TextUtils.isEmpty(exchangePoint) ? "0" : exchangePoint;
    }

    public void setExchangePoint(String exchangePoint) {
        this.exchangePoint = exchangePoint;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    @Override
    public String toString() {
        return "RedBagEntity{" +
                "coupon='" + coupon + '\'' +
                ", red='" + red + '\'' +
                ", invitation='" + invitation + '\'' +
                ", level='" + level + '\'' +
                ", levelName='" + levelName + '\'' +
                ", exchangePoint='" + exchangePoint + '\'' +
                ", rider='" + rider + '\'' +
                ", signIntegral='" + signIntegral + '\'' +
                '}';
    }
}
