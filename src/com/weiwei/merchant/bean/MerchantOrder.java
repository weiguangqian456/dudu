package com.weiwei.merchant.bean;

import java.util.List;

public class MerchantOrder {

    private int total;

    private int page;

    private List<Order> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Order> getRecords() {
        return records;
    }

    public void setRecords(List<Order> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "MerchantOrder{" +
                "total=" + total +
                ", page=" + page +
                ", records=" + records +
                '}';
    }
}
