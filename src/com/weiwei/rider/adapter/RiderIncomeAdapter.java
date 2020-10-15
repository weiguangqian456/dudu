package com.weiwei.rider.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.rider.bean.Income;
import com.weiwei.salemall.utils.DateUtil;

import java.util.List;

public class RiderIncomeAdapter extends BaseQuickAdapter<Income,BaseViewHolder>{

    public RiderIncomeAdapter(@Nullable List<Income> data) {
        super(R.layout.income_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Income item) {
        helper.setText(R.id.tv_order,item.getSerialNumber())
                .setText(R.id.tv_time, DateUtil.stampToDate(item.getCreateTime().toString()))
                .setText(R.id.tv_money,"￥" + item.getMoney());
    }
}
