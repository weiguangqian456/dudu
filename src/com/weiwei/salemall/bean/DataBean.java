package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/11/20.
 *         Describe
 */

public class DataBean<T> {
    private List<T> other;
    private List<T> jd;

    public List<T> getOther() {
        return other;
    }

    public void setOther(List<T> other) {
        this.other = other;
    }





    public List<T> getJd() {
        return jd;
    }

    public void setJd(List<T> jd) {
        this.jd = jd;
    }

}
