package com.weiwei.rider.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.VsredinfoActivity;
import com.weiwei.base.util.ViewPageFragment;
import com.weiwei.rider.bean.RiderAccount;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.fragment.ExpandFragment;
import com.weiwei.rider.fragment.IncomeFragment;
import com.weiwei.rider.utils.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RiderCenterActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_bind)
    TextView tvBind;
    @BindView(R.id.tv_total_amount)
    TextView tvTotal;
    @BindView(R.id.tv_expand)
    TextView tvExpand;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.tabLayout)
    XTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tv_freeze)
    TextView tvFreeze;

    private List<Fragment> fragmentList;
    private String[] titles = {"收入明细","提现明细"};
    private RiderAccount account;
    public int type ;

    private AlertDialog alertDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_center;
    }

    @Override
    protected void initView() {
        tvTitle.setText("个人中心");
    }

    @Override
    protected void initData() {
        account = (RiderAccount) getIntent().getSerializableExtra("account");
        type = getIntent().getIntExtra("type",0);
        if(type == 2) {
            tvFreeze.setVisibility(View.VISIBLE);
        }
        if(account != null) {
            setData();
        }
        fragmentList = new ArrayList<>();
        fragmentList.add(new IncomeFragment());
        fragmentList.add(new ExpandFragment());

        viewPager.setAdapter(new ViewPageFragment(getSupportFragmentManager(),fragmentList,titles));
        tabLayout.setupWithViewPager(viewPager);

        alertDialog = new AlertDialog(this).builder();
    }

    private void setData() {
        tvTotal.setText("￥" + account.getTotalRedPacket());
        tvExpand.setText(account.getHaveWithdrawal());
        tvIncome.setText(account.getNotWithdrawal());
        if(account.getIsWxId() != null && account.getIsWxId().equals("y")) {
            tvBind.setText("立即提现");
        }
        tvFreeze.setText("总金额包含冻结金额" + account.getFreezeWithdrawal() + "元");
    }

    @OnClick({R.id.rl_back,R.id.tv_bind,R.id.tv_step,R.id.tv_freeze})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_step:
                startActivity(new Intent(RiderCenterActivity.this,VsredinfoActivity.class));
                break;
            case R.id.tv_freeze:
                alertDialog.setGone().setShowText().show();
                break;
        }
    }
}
