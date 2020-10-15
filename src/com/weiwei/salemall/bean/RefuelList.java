package com.weiwei.salemall.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RefuelList implements Serializable {
    public String gasId;
    public String gasName;
    public String gasAddress;
    public Double gasAddressLongitude;
    public Double gasAddressLatitude;
    public Double priceYfq;
    public Double priceOfficial;
    public String distance;
    public String gasLogoSmall;
    public String gasLogoBig;
}
