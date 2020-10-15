package com.weiwei.merchant.adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.merchant.activity.MerchantOrderDetailActivity;
import com.weiwei.merchant.bean.Order;
import com.weiwei.salemall.utils.DateUtil;

import java.util.List;

public class MerchantOrderAdapter extends BaseQuickAdapter<Order,BaseViewHolder> {

    public MerchantOrderAdapter(@Nullable List<Order> data) {
        super(R.layout.merchant_order_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        helper.setText(R.id.tv_orderNo,"订单号: " + item.getOrderNo())
                .setText(R.id.tv_time, DateUtil.stampToDate(item.getCreateTime()+""))
                .setText(R.id.tv_num,"共" + item.getOrderDetails().size() + "件商品")
                .setText(R.id.tv_money,"￥" + item.getTotalAmount())
                .setText(R.id.tv_total,"合计: ￥" + item.getCombined());
        RecyclerView recyclerView = helper.getView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new OrderDetailAdapter(item.getOrderDetails()));

        helper.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MerchantOrderDetailActivity.class);
            mContext.startActivity(intent);

        });
    }
}
