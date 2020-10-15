package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/8.
 * @description:
 */

public class DefaultGoodsAdapter extends BaseRecycleAdapter {

    public DefaultGoodsAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_default_goods, parent,false);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder) holder1;
        boolean isLeft = position % 2 == 0;
        holder.mViewLeft .getLayoutParams().width = isLeft ? DensityUtils.dp2px(mContext,10) : DensityUtils.dp2px(mContext,3);
        holder.mViewRight.getLayoutParams().width = isLeft ? DensityUtils.dp2px(mContext,3) : DensityUtils.dp2px(mContext,10);
        final ProductBean choicesBean = JSONObject.parseObject(getItem(position).toString(),ProductBean.class);
        String imageUrl = JudgeImageUrlUtils.isAvailable(choicesBean.getPicture());
        Glide.with(mContext).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(holder.iv_image);
        holder.tv_name.setText(choicesBean.getProductName());
        holder.tv_price.setText(TextDisposeUtils.dispseMoneyText(choicesBean.getJdPrice()));
        holder.tv_price_order.setText("VIP"+TextDisposeUtils.dispseMoneyText(choicesBean.getPrice()));
//        holder.tv_price_order.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                String[] value = new String[]{choicesBean.getProductNo(), JudgeImageUrlUtils.isAvailable(choicesBean.getPicture()), choicesBean.getProductName(),choicesBean.getPrice()};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.mLlGoods)
        LinearLayout mLlGoods;
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_price_order)
        TextView tv_price_order;

        @BindView(R.id.mViewRight)
        View mViewRight;
        @BindView(R.id.mViewLeft)
        View mViewLeft;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
