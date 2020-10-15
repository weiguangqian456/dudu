package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/7/19.
 *         主页
 */

public class HomePageDataBean {
    private List<ProductBean> welfares;
    private List<ProductBean> recommends;
    private List<ProductBean> choices;

    public List<ProductBean> getWelfares() {
        return welfares;
    }

    public void setWelfares(List<ProductBean> welfares) {
        this.welfares = welfares;
    }

    public List<ProductBean> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<ProductBean> recommends) {
        this.recommends = recommends;
    }

    public List<ProductBean> getChoices() {
        return choices;
    }

    public void setChoices(List<ProductBean> choices) {
        this.choices = choices;
    }
}
