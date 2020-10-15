package com.weiwei.account;

/**
 * @author Created by EDZ on 2018/10/12.
 *         Describe  提现记录实体
 */

public class ConsumptionRecordsEntity {
    private String time;  //时间
    private String remark;  //内容
    private String status; //状态
    private String integralType;//类型
    private String amount; //金额
    private String phone;  //电话
    private String type;
    private String ctime;

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIntegralType() {
        return integralType;
    }

    public void setIntegralType(String integralType) {
        this.integralType = integralType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConsumptionRecordsEntity{" +
                "time='" + time + '\'' +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", integralType='" + integralType + '\'' +
                ", amount='" + amount + '\'' +
                ", phone='" + phone + '\'' +
                ", type='" + type + '\'' +
                ", ctime='" + ctime + '\'' +
                '}';
    }
}
