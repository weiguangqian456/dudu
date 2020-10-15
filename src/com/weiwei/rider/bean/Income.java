package com.weiwei.rider.bean;

public class Income {

    private Long id;

    private String money;

    private String status;

    private String serialNumber;

    private Long createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + id +
                ", money='" + money + '\'' +
                ", status='" + status + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ",createTime=" +
                '}';
    }
}
