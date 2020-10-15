package com.weiwei.salemall.bean;

import com.weiwei.home.entity.CustomCarouselEntity;

import java.io.Serializable;
import java.util.List;

public class DuduSeckillBanner implements Serializable, CustomCarouselEntity {

    public long now;
    public long startTime;
    public long endTime;
    public String status;
    public List<List<Prdoucts>> prdoucts;

    public static class Prdoucts {
        public Object seckillProductId;
        public String productName;
        public String productNo;
        public int stock;
        public String picture;
        public String price;
        public String appId;
        public Object jdUrl;
        public Object startTime;
        public Object endTime;
        public int totalStock;
        public String percent;
        public int contrastSource;
        public String seckillPrice;
    }

    @Override
    public Object getUrl() {
        return null;
    }
}
