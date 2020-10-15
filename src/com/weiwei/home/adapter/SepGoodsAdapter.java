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
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/4/3.
 * @description:
 */

public class SepGoodsAdapter extends RecyclerView.Adapter {

    private int mMaxSize = 6;
    private Context mContext;
    private List<SeckillProductEntity> mList;

    public SepGoodsAdapter(Context mContext, List<SeckillProductEntity> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_introduce, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder)holder1;
        final SeckillProductEntity entity = mList.get(position);
        holder.tv_title.setText(entity.getProductName());
        holder.tv_price.setText(TextDisposeUtils.dispseMoneyText(entity.getSeckillPrice()));
        String com = entity.getShopSource() + TextDisposeUtils.dispseMoneyText(entity.getPrice());
        holder.tv_originally_price.setText(com);
//        holder.tv_originally_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        final String image = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        Glide.with(mContext).load(image).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(holder.iv_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice","seckillProductId", "seckill","type"};
                String[] value = new String[]{entity.getProductNo(), image, entity.getProductName(),entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill",mContext.getString(R.string.member_region)};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() > mMaxSize ? mMaxSize : mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_originally_price)
        TextView tv_originally_price;
        @BindView(R.id.iv_buy)
        TextView iv_buy;
        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
