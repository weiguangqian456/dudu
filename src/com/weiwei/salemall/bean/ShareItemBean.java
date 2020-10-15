package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/9/28.
 *         Describe
 */

public class ShareItemBean {
    private int imgId;
    private String name;

    public ShareItemBean(int imgId, String name) {
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
}
