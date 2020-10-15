package com.weiwei.salemall.bean;

/**
 * @author Created by EDZ on 2018/6/21.
 *
 */

public class ShopConfigRecordsEntity {
    private String productNo;
    private String specName;
    private String specValue;

    public ShopConfigRecordsEntity(){
        super();
    }

    public ShopConfigRecordsEntity(String specName, String specValue) {
        this.specName = specName;
        this.specValue = specValue;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }
}
