package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.home.view.FlikerProgressBar;
import com.weiwei.home.view.RoundedProgressBar;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.DuduSeckill;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/12.
 * @description: 新的兑换专区
 */

public class SeckillAdapter extends BaseRecycleAdapter {

    private String mStatus = "";

    public SeckillAdapter(Context mContext) {
        super(mContext);
    }

    public void setStatus(String status) {
        if (mStatus == null) {
            this.mStatus = "";
        }
        this.mStatus = status;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seckill, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        String json = getItem(position).toString();
        final DuduSeckill item = new Gson().fromJson(json, DuduSeckill.class);

        Glide.with(mContext).load(JudgeImageUrlUtils.isAvailable(item.picture)).into(holder.mIvGoodsImage);

        holder.mTvGoodsName.setText(item.productName);
        holder.mTvGoodsDiscountsPrice.setText(TextDisposeUtils.dispseMoneyText(item.seckillPrice));
        holder.mTvGoodsPrice.setText(TextDisposeUtils.dispseMoneyText(item.price));
        holder.mTvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvProgress.setText("已售" + item.percent);
        int progerss = Integer.parseInt(item.percent.replace("%", ""));
//        holder.mFpbTaskProgress.setProgress(progerss);
        holder.mRpbTaskProgress.setProgress(progerss);

        switch (mStatus) {
            case "underway":
                holder.mLlProgress.setVisibility(View.VISIBLE);
                holder.mRtvImmediatelyPurchase.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.color_red));
                break;
            default:
                holder.mLlProgress.setVisibility(View.GONE);
                holder.mRtvImmediatelyPurchase.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.public_color_999999));
                break;
        }

        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "seckillProductId", "seckill"};
                String[] value = new String[]{item.productNo, JudgeImageUrlUtils.isAvailable(item.picture), item.productName, item.seckillPrice, item.seckillProductId, "duduSeckill"};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);

            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_goods_image)
        ImageView mIvGoodsImage;
        @BindView(R.id.tv_goods_name)
        TextView mTvGoodsName;
        @BindView(R.id.tv_goods_discounts_price)
        TextView mTvGoodsDiscountsPrice;
        @BindView(R.id.tv_goods_price)
        TextView mTvGoodsPrice;
        @BindView(R.id.tv_progress)
        TextView mTvProgress;
        //        @BindView(R.id.fpb_task_progress)
//        FlikerProgressBar mFpbTaskProgress;
        @BindView(R.id.rtv_immediately_purchase)
        RoundTextView mRtvImmediatelyPurchase;
        @BindView(R.id.ll_progress)
        LinearLayout mLlProgress;
        @BindView(R.id.rpb_task_progress)
        RoundedProgressBar mRpbTaskProgress;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
