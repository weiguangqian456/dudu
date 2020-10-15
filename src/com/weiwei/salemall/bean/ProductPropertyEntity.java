package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/8/15
 */

public class ProductPropertyEntity {
    private List<PropertyBean> property;
    private List<InventoryBean> inventory;

    public List<PropertyBean> getProperty() {
        return property;
    }

    public void setProperty(List<PropertyBean> property) {
        this.property = property;
    }

    public List<InventoryBean> getInventory() {
        return inventory;
    }

    public void setInventory(List<InventoryBean> inventory) {
        this.inventory = inventory;
    }
}
