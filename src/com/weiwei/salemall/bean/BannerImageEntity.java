package com.weiwei.salemall.bean;

import com.weiwei.home.entity.CustomCarouselEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import java.io.Serializable;

/**
 * @author Created by EDZ on 2018/8/16.
 *         轮播图实体
 */

public class BannerImageEntity implements Serializable ,CustomCarouselEntity {
    private String id;
    private String name;
    private int skipType;
    private String imageUrl;
    private String location;
    private String iosClassName;
    private String androidClassName;
    private String iosParams;
    private String androidParams;
    private int duration;
    private String columnId;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkipType() {
        return skipType;
    }

    public void setSkipType(int skipType) {
        this.skipType = skipType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIosClassName() {
        return iosClassName;
    }

    public void setIosClassName(String iosClassName) {
        this.iosClassName = iosClassName;
    }

    public String getAndroidClassName() {
        return androidClassName;
    }

    public void setAndroidClassName(String androidClassName) {
        this.androidClassName = androidClassName;
    }

    public String getIosParams() {
        return iosParams;
    }

    public void setIosParams(String iosParams) {
        this.iosParams = iosParams;
    }

    public String getAndroidParams() {
        return androidParams;
    }

    public void setAndroidParams(String androidParams) {
        this.androidParams = androidParams;
    }

    @Override
    public Object getUrl() {
        return JudgeImageUrlUtils.isAvailable(imageUrl);
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    @Override
    public String toString() {
        return "BannerImageEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", skipType=" + skipType +
                ", imageUrl='" + imageUrl + '\'' +
                ", location='" + location + '\'' +
                ", iosClassName='" + iosClassName + '\'' +
                ", androidClassName='" + androidClassName + '\'' +
                ", iosParams='" + iosParams + '\'' +
                ", androidParams='" + androidParams + '\'' +
                ", duration=" + duration +
                ", columnId='" + columnId + '\'' +
                '}';
    }
}
