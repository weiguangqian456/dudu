package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/12/6.
 *         Describe  秒杀商品Data层
 */

public class SeckillDataEntity<T> {
    private String status;
    private String startTime;
    private String endTime;
    private String now;
    private List<T> prdoucts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<T> getPrdoucts() {
        return prdoucts;
    }

    public void setPrdoucts(List<T> prdoucts) {
        this.prdoucts = prdoucts;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }
}
