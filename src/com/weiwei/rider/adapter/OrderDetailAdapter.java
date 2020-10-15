package com.weiwei.rider.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.rider.bean.OrderDetail;
import com.weiwei.salemall.base.Const;

import java.util.List;

public class OrderDetailAdapter extends BaseQuickAdapter<OrderDetail,BaseViewHolder> {
    public OrderDetailAdapter(@Nullable List<OrderDetail> data) {
        super(R.layout.order_detail_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetail item) {
        helper.setText(R.id.tv_name,item.getProductName())
                .setText(R.id.tv_intro,item.getRemark())
                .setText(R.id.tv_num,"x " + item.getNumber());
        Glide.with(mContext).load(Const.BASE_IMAGE_URL + item.getPicture())
                .into((ImageView) helper.getView(R.id.iv_image));
    }
}
