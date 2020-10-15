package com.weiwei.salemall.bean;

/**
 * @author : hc
 * @date : 2019/3/12.
 * @description: 获取签到信息
 */

public class SignInfoBean {
    private String amount;
    private String rule;
    private String isSign;
    private String isCanSign;
//    private String authority;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getIsSign() {
        return isSign;
    }

    public void setIsSign(String isSign) {
        this.isSign = isSign;
    }

    public String getIsCanSign() {
        return isCanSign;
    }

    public void setIsCanSign(String isCanSign) {
        this.isCanSign = isCanSign;
    }

}
