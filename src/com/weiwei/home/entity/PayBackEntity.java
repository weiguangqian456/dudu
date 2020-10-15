package com.weiwei.home.entity;

/**
 * @author hc.
 * Description:
 * @date 2020/7/12
 */
public class PayBackEntity {

    /**
     * wxPayAppid : wxefb0bdad32cbbb70
     * aliPay : alipay
     * wxPaykey : 8888777766665555DDDDCCCCbbbbaaaa
     * wxpay : wxpay
     * wxPaySecret : 5f88994554b58322dc3faa1ac1ee6d9b
     */

    private String wxPayAppid;
    private String aliPay;
    private String wxPaykey;
    private String wxpay;
    private String wxPaySecret;

    public String getWxPayAppid() {
        return wxPayAppid;
    }

    public void setWxPayAppid(String wxPayAppid) {
        this.wxPayAppid = wxPayAppid;
    }

    public String getAliPay() {
        return aliPay;
    }

    public void setAliPay(String aliPay) {
        this.aliPay = aliPay;
    }

    public String getWxPaykey() {
        return wxPaykey;
    }

    public void setWxPaykey(String wxPaykey) {
        this.wxPaykey = wxPaykey;
    }

    public String getWxpay() {
        return wxpay;
    }

    public void setWxpay(String wxpay) {
        this.wxpay = wxpay;
    }

    public String getWxPaySecret() {
        return wxPaySecret;
    }

    public void setWxPaySecret(String wxPaySecret) {
        this.wxPaySecret = wxPaySecret;
    }
}
