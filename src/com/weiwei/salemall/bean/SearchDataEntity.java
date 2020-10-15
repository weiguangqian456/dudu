package com.weiwei.salemall.bean;

import java.io.Serializable;

public class SearchDataEntity implements Serializable {
    private String productName;
    private String productNo;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }
}
