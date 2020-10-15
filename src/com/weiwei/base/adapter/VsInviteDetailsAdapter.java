package com.weiwei.base.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.base.util.image.StringUtils;
import com.weiwei.home.entity.SeckillTab;

import java.util.List;

public class VsInviteDetailsAdapter extends BaseQuickAdapter<SeckillTab.Records,BaseViewHolder> {

    public VsInviteDetailsAdapter(@Nullable List<SeckillTab.Records> data) {
        super(R.layout.invite_detail_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SeckillTab.Records item) {
        helper.setText(R.id.tv_money,item.amountGun);
        if(!StringUtils.isEmpty(item.payTime)) {
            helper.setText(R.id.tv_time,item.payTime.substring(0,10))
            .setText(R.id.tv_hour,item.payTime.substring(10,item.payTime.length()));
        }
    }
}
