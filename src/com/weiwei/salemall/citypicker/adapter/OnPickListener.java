package com.weiwei.salemall.citypicker.adapter;

import com.weiwei.salemall.citypicker.model.City;

public interface OnPickListener {
    void onPick(int position, City data);
    void onLocate();
}
