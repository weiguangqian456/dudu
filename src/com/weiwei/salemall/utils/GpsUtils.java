package com.weiwei.salemall.utils;

/**
 * @author Created by EDZ on 2018/8/4.
 *         转换百度地图导航需要的经纬度
 */

public class GpsUtils {
    private double mLatitude;
    private double mLongitude;

    public GpsUtils(double longitude, double mLatitude) {
        setLatitude(mLatitude);
        setLongitude(longitude);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    @Override
    public String toString() {
        return mLongitude + "," + mLatitude;
    }
}
