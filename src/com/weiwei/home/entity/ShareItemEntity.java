package com.weiwei.home.entity;

/**
 * @author Created by EDZ on 2018/9/28.
 *         Describe
 */

public class ShareItemEntity {
    private String key;
    private int imgId;
    private String name;

    public ShareItemEntity(String key, int imgId, String name) {
        this.key = key;
        this.imgId = imgId;
        this.name = name;
    }

    public int getImgId() {

        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
