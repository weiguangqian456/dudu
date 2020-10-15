package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.bean.DuduSeckillBanner;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/21.
 * @description: Adapter
 */

public class DuduSeckillBannerAdapter extends RecyclerView.Adapter<DuduSeckillBannerAdapter.ItemViewHolder> {

    private Context mContext;
    private List<DuduSeckillBanner.Prdoucts> mList = new ArrayList<>();
    private RecycleItemClick mRecycleItemClick;

    public List<DuduSeckillBanner.Prdoucts> getList() {
        return mList;
    }

    public void setList(List<DuduSeckillBanner.Prdoucts> list) {
        this.mList = list;
    }


    public DuduSeckillBannerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dudu_seckill_banner, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        final DuduSeckillBanner.Prdoucts item = mList.get(position);

        Glide.with(mContext).load(JudgeImageUrlUtils.isAvailable(item.picture)).into(holder.mIvGoodsImage);
        holder.mTvGoodsDiscountsPrice.setText(TextDisposeUtils.dispseMoneyText(item.seckillPrice));
        holder.mTvGoodsPrice.setText(TextDisposeUtils.dispseMoneyText(item.price));
        holder.mTvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvGoodsPrice.getPaint().setAntiAlias(true);//抗锯齿

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            layoutParams.leftMargin = DensityUtils.dp2px(mContext, 10);
        } else {
            layoutParams.leftMargin = DensityUtils.dp2px(mContext, 0);
        }
        if (position == mList.size() - 1) {
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 10);
        } else {
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 0);
        }
        holder.itemView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecycleItemClick.onItemClick(position);
            }
        });

    }

    public void setRecycleClickListener(RecycleItemClick mRecycleItemClick) {
        this.mRecycleItemClick = mRecycleItemClick;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_goods_image)
        ImageView mIvGoodsImage;
        @BindView(R.id.tv_goods_discounts_price)
        TextView mTvGoodsDiscountsPrice;
        @BindView(R.id.tv_goods_price)
        TextView mTvGoodsPrice;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
