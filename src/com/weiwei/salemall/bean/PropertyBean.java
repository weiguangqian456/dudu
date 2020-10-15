package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/8/15.
 */

public class PropertyBean {
    private String propertyNameId;
    private String propertyName;
    private List<PropertyValuesBean> propertyValues;

    public String getPropertyNameId() {
        return propertyNameId;
    }

    public void setPropertyNameId(String propertyNameId) {
        this.propertyNameId = propertyNameId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public List<PropertyValuesBean> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<PropertyValuesBean> propertyValues) {
        this.propertyValues = propertyValues;
    }
}
