package com.weiwei.home.yd;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hc on 2019/7/18.
 * Description:
 */

public class YDLocalItemAdapter extends BaseRecycleAdapter {

    YDLocalItemAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_local, parent,false);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final ItemViewHolder holder = (ItemViewHolder)holder1;
        final YDLocalItemEntity entity = JSONObject.parseObject(getItem(position).toString(),YDLocalItemEntity.class);
        String imageUrl = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        Glide.with(mContext).load(imageUrl).into(holder.mIvImage);
        holder.mTvTitle.setText(entity.getProductName());
        holder.mTvText.setText(entity.getSubTitle());
        holder.mTvPrice.setText(TextDisposeUtils.dispseMoneyText(entity.getPrice()));
        holder.mTvOldPrice.setText(entity.getJdPrice());
//        holder.mTvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                String[] value = new String[]{entity.getProductNo(),
                        JudgeImageUrlUtils.isAvailable(entity.getPicture()), entity.getProductName(),entity.getPrice()};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.mIvImage)
        ImageView mIvImage;
        @BindView(R.id.mTvTitle)
        TextView mTvTitle;
        @BindView(R.id.mTvText)
        TextView mTvText;
        @BindView(R.id.mTvPrice)
        TextView mTvPrice;
        @BindView(R.id.mTvOldPrice)
        TextView mTvOldPrice;
        @BindView(R.id.mLlAdd)
        LinearLayout mLlAdd;
        @BindView(R.id.mIvShopping)
        ImageView mIvShopping;

        @BindView(R.id.mIvSubtract)
        ImageView mIvSubtract;
        @BindView(R.id.mTvCount)
        TextView mTvCount;
        @BindView(R.id.mIvAdd)
        ImageView mIvAdd;

        @BindView(R.id.mLlInfo)
        LinearLayout mLlInfo;
        @BindView(R.id.mLlInfo2)
        LinearLayout mLlInfo2;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
