package com.weiwei.salemall.activity;

import android.os.Bundle;

import com.hwtx.dududh.R;
import com.weiwei.salemall.base.TempAppCompatActivity;

import butterknife.ButterKnife;

/**
 * @author Created by EDZ on 2018/08/06
 *         测试 智齿客服
 */
public class CustomerServiceActivity extends TempAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        ButterKnife.bind(this);
    }

    @Override
    protected void init() {

    }
}
