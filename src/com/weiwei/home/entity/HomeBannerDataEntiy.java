package com.weiwei.home.entity;

import com.weiwei.salemall.bean.BannerImageEntity;

import java.util.List;

public class HomeBannerDataEntiy {

    private List<BannerImageEntity> list;

    public List<BannerImageEntity> getList() {
        return list;
    }

    public void setList(List<BannerImageEntity> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "HomeBannerDataEntiy{" +
                "list=" + list +
                '}';
    }
}
