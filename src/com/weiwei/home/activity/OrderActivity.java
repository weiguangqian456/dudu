package com.weiwei.home.activity;

import android.app.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends Activity {

    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;

    private HolderView mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }

    public void initView() {

        mTvTitle.setText("加油订单");
        View headerView = LayoutInflater.from(this).inflate(R.layout.header_refuel_order, null, false);
        mHeaderView = new HolderView(headerView);

    }


    class HolderView {

        @BindView(R.id.tv_accumulate_consumption)
        TextView mTvAccumulateConsumption;
        @BindView(R.id.tv_tv_accumulate_refuel)
        TextView mTvTvAccumulateRefuel;



        HolderView(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}