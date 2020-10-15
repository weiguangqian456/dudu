package com.weiwei.home.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/12.
 * @description: 新的兑换专区
 */

public class ExchangeRegionAdapter extends BaseRecycleAdapter {

    private int mType = 0;
    private String mColumnId;

    public ExchangeRegionAdapter(Context mContext, String mColumnId) {
        super(mContext);
        this.mColumnId = mColumnId;
    }

    public ExchangeRegionAdapter(Context mContext, int type, String mColumnId) {
        super(mContext);
        this.mType = type;
        this.mColumnId = mColumnId;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_sep, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder) holder1;
        final boolean isExchange = mType == 0;
        String json = getItem(position).toString();
        final ProductBean bean = new Gson().fromJson(json, ProductBean.class);
        Glide.with(mContext).load(JudgeImageUrlUtils.isAvailable(bean.getPicture())).into(holder.iv_image);
        holder.tv_title.setText(bean.getProductName().trim());
        holder.tv_price.setText(TextDisposeUtils.dispseMoneyText(bean.getJdPrice()));
        holder.tv_originally_price.setText("VIP￥" + bean.getPrice());
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "columnId", "type"};
                String[] value = new String[]{bean.getProductNo(), JudgeImageUrlUtils.isAvailable(bean.getPicture())
                        , bean.getProductName(), bean.getPrice(), mColumnId, isExchange ? mContext.getString(R.string.exchange_region) : ""};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_originally_price)
        TextView tv_originally_price;
        @BindView(R.id.iv_buy)
        ImageView iv_buy;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
