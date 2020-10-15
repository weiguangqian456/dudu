package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/26.
 * @description: 精选 用于精选模块以及首页
 */

public class ChoicesAdapter extends BaseRecycleAdapter {

//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    public ChoicesAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .rv_dudu_special_item, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder) holder1;
        String string = getItem(position).toString();
        final ProductBean choicesBean = JSONObject.parseObject(getItem(position).toString(), ProductBean.class);
        final String imageUrl = JudgeImageUrlUtils.isAvailable(choicesBean.getPicture());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.mall_logits_default);
        Glide.with(mContext).load(imageUrl).apply(options).into(holder.imageView);
        holder.productEconomicalPrice.setText("立省¥" + DealEcnomicalMoneyUtils.get(choicesBean.getJdPrice(), choicesBean.getPrice(), 1));
        int contrastSource = choicesBean.getContrastSource();
        holder.productJdPrice.setText(choicesBean.getJdPrice2());
//        holder.productJdPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.productMallPrice.setText("VIP" + TextDisposeUtils.dispseMoneyText(choicesBean.getPrice()));
        holder.productName.setText(choicesBean.getProductName());
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice"};
                String[] value = new String[]{choicesBean.getProductNo(), imageUrl, choicesBean.getProductName(), choicesBean.getPrice()};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_shop)
        ImageView imageView;
        @BindView(R.id.tv_introduction)
        TextView productName;
        @BindView(R.id.tv_mall_price)
        TextView productMallPrice;
        @BindView(R.id.tv_jd_price)
        TextView productJdPrice;
        @BindView(R.id.tv_ecoprice)
        TextView productEconomicalPrice;
        @BindView(R.id.ll_content)
        RelativeLayout contentLl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
