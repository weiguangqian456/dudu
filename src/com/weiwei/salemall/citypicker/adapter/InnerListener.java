package com.weiwei.salemall.citypicker.adapter;

import com.weiwei.salemall.citypicker.model.City;

public interface InnerListener {
    void dismiss(int position, City data);
    void locate();
}
