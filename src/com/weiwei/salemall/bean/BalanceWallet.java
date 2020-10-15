package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/11/30.
 *         Describe   红包余额
 */

public class BalanceWallet<T> {
    /**
     * {"code":0,"data":{"vip_info":"","gift_flag":"n","gift_balance":0,"expire_time":"2019-07-14 00:46:06","balance":0,"status":"y","basic_balance":0,"uid":"898108",
     * "package_list":[{"package_name":"银卡会员（普通会员）","id":105222,"is_time":"n","expire_time":"2019-07-14 00:46:06","uid":"898108","package_id":200011,"app_id":"dudu",
     * "ctime":"2018-07-12 00:46:05","left_call_time":10000,"effect_time":"2018-07-12 00:46:06","package_type":0,"is_nature":"n"}],"app_id":"dudu","ctime":"2018-05-22 15:58:34",
     * "gift_expire_time":"2018-05-29 15:58:34"},"msg":"success"}
     */
    private String vip_info;
    private String gift_flag;
    private String gift_balance;
    private String expire_time;
    private String balance;
    private String status;
    private String basic_balance;
    private String uid;
    private List<T> package_list;   //充值记录
    private String ctime;
    private String gift_expire_time;

    public String getVip_info() {
        return vip_info;
    }

    public void setVip_info(String vip_info) {
        this.vip_info = vip_info;
    }

    public String getGift_flag() {
        return gift_flag;
    }

    public void setGift_flag(String gift_flag) {
        this.gift_flag = gift_flag;
    }

    public String getGift_balance() {
        return gift_balance;
    }

    public void setGift_balance(String gift_balance) {
        this.gift_balance = gift_balance;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBasic_balance() {
        return basic_balance;
    }

    public void setBasic_balance(String basic_balance) {
        this.basic_balance = basic_balance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<T> getPackage_list() {
        return package_list;
    }

    public void setPackage_list(List<T> package_list) {
        this.package_list = package_list;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getGift_expire_time() {
        return gift_expire_time;
    }

    public void setGift_expire_time(String gift_expire_time) {
        this.gift_expire_time = gift_expire_time;
    }
}
