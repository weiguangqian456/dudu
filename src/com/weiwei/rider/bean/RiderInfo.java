package com.weiwei.rider.bean;

public class RiderInfo {
    /**
     *   "id": 13,
     "workCity": "深圳市",
     "lave": "y",
     "workArea": null,
     "uid": "923721",
     "appId": "dudu",
     "workingPlace": "南山区科技园",
     "name": "郭浩然",
     "phone": "17585722933",
     "userPhone": "17585722933",
     "ctime": 1591779207000,
     "utime": 1591779239000,
     "auditTime": 1591779239000,
     "status": "y"
     */

    private Long id;

    private String workCity;

    private String lave;

    private String uid;

    private String workingPlace;

    private String name;

    private String phone;

    private String userPhone;

    private String status;

    private Long ctime;

    private Long utime;

    private Long auditTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getLave() {
        return lave;
    }

    public void setLave(String lave) {
        this.lave = lave;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Long auditTime) {
        this.auditTime = auditTime;
    }

    @Override
    public String toString() {
        return "RiderInfo{" +
                "id=" + id +
                ", workCity='" + workCity + '\'' +
                ", lave='" + lave + '\'' +
                ", uid='" + uid + '\'' +
                ", workingPlace='" + workingPlace + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", status='" + status + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                ", auditTime=" + auditTime +
                '}';
    }
}
