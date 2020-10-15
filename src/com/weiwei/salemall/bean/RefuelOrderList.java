package com.weiwei.salemall.bean;

import java.util.List;

public class RefuelOrderList<T> {
    public int total;
    public int size;
    public int pages;
    public int current;
    public String amountPaySum;
    public String litreSum;
    public List<T> orderList;
}
