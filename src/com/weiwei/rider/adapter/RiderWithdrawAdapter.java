package com.weiwei.rider.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.rider.bean.Income;
import com.weiwei.salemall.utils.DateUtil;

import java.util.List;

public class RiderWithdrawAdapter extends BaseQuickAdapter<Income,BaseViewHolder> {
    public RiderWithdrawAdapter(@Nullable List<Income> data) {
        super(R.layout.rider_withdraw_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Income item) {
        helper.setText(R.id.tv_money,"￥" + item.getMoney())
                .setText(R.id.tv_time, DateUtil.stampToDate(item.getCreateTime()+""));
    }
}
