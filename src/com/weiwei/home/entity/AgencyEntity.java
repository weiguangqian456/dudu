package com.weiwei.home.entity;

import java.util.List;

/**
 * 用户下级代理数据
 */
public class AgencyEntity {

    private int offset;
    private int limit;
    /**
     * 总数
     */
    private int total;
    /**
     * 当前页数据
     */
    private int size;
    /**
     * 当前页码
     */
    private int pages;
    private int current;
    /**
     * 下级数据集合
     */
    private List records;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

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

    public List getRecords() {
        return records;
    }

    public void setRecords(List records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "AgencyEntity{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", total=" + total +
                ", size=" + size +
                ", pages=" + pages +
                ", current=" + current +
                ", records=" + records +
                '}';
    }
}
