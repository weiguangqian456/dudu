package com.weiwei.rider.bean;

public class RiderCity {

    /**
     *   "id":418,
     "cityCode":"532300",
     "cityName":"楚雄彝族自治州",
     "appId":"dudu",
     "provinceCode":null
     */

    private String id;

    private String cityCode;

    private String cityName;

    private String appId;

    private String provinceCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String toString() {
        return "RiderCity{" +
                "id='" + id + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", appId='" + appId + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                '}';
    }
}
