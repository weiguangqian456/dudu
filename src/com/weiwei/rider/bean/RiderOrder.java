package com.weiwei.rider.bean;

import java.util.List;

public class RiderOrder {

    private int total;

    private int pages;

    private List<Order> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Order> getRecords() {
        return records;
    }

    public void setRecords(List<Order> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "RiderOrder{" +
                "total=" + total +
                ", pages=" + pages +
                ", records=" + records +
                '}';
    }
}
