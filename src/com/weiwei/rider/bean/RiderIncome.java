package com.weiwei.rider.bean;

import java.util.List;

public class RiderIncome {

    private int total;

    private int pages;

    private List<Income> records;

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

    public List<Income> getRecords() {
        return records;
    }

    public void setRecords(List<Income> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "RiderIncome{" +
                "total=" + total +
                ", pages=" + pages +
                ", records=" + records +
                '}';
    }
}
