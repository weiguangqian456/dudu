package com.weiwei.home.entity;

public class IncomeEntity {

    private String red;
    private String amount;
    private String coupon;
    private String invitation;
    private String exchangePoint;

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public String getExchangePoint() {
        return exchangePoint;
    }

    public void setExchangePoint(String exchangePoint) {
        this.exchangePoint = exchangePoint;
    }

    @Override
    public String toString() {
        return "IncomeEntity{" +
                "red='" + red + '\'' +
                ", amount='" + amount + '\'' +
                ", coupon='" + coupon + '\'' +
                ", invitation='" + invitation + '\'' +
                ", exchangePoint='" + exchangePoint + '\'' +
                '}';
    }
}
