package com.weiwei.merchant.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.merchant.bean.OrderDetail;
import com.weiwei.salemall.base.Const;

import java.util.List;

public class OrderDetailAdapter extends BaseQuickAdapter<OrderDetail,BaseViewHolder> {

    public OrderDetailAdapter(@Nullable List<OrderDetail> data) {
        super(R.layout.merchant_order_detail_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetail item) {

        helper.setText(R.id.tv_name,item.getProductName())
                .setText(R.id.tv_num,"x" + item.getNumber())
                .setText(R.id.tv_price,"￥" + item.getProductPrice());

        Glide.with(mContext).load(Const.BASE_IMAGE_URL + item.getPicture()).into((ImageView) helper.getView(R.id.iv_image));
    }
}
