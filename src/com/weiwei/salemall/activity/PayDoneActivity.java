package com.weiwei.salemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.utils.FitStateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Created by EDZ on 2018/6/12.
 *         订单支付完成
 */

public class PayDoneActivity extends VsBaseActivity {
    @BindView(R.id.sys_title_txt)
    TextView titleTv;
    @BindView(R.id.tv_pay_money)
    TextView payMoneyTv;
    @BindView(R.id.btn_check_order)
    TextView checkOrderBtn;
    @BindView(R.id.btn_back_mall)
    TextView backMallBtn;

    private String discountAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_done);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleTv.setText("付款成功");
        Intent intent = getIntent();
        discountAmount = intent.getStringExtra("discountAmount");
        if (discountAmount != null) {
            payMoneyTv.setText(discountAmount);
        }
    }

    @OnClick({R.id.btn_check_order})
    void gotoOrderDetailActivity(View view) {
        skipPageAndFinish(VsMainActivity.class, "indicator", 1);
    }

    @OnClick({R.id.btn_back_mall})
    void gotoSaleMallFragment(View view) {
        skipPageAndFinish(VsMainActivity.class, "indicator", 0);
    }
}
