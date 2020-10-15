package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/8/14.
 *         data层（复用）
 */

public class ResultDataEntity<T> {
    private int total;
    private int size;
    private int pages;
    private int current;
    private List<T> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }


    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

}
