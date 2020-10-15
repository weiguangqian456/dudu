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
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.PercentProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/11.
 * @description: 新会员专区 - （复制于SeckillProductAdapter）
 */

public class MemberRegionAdapter extends BaseRecycleAdapter {

    public MemberRegionAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_seckill_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder) holder1;
        final SeckillProductEntity entity = JSONObject.parseObject(getItem(position).toString(),SeckillProductEntity.class);
        holder.tv_name.setText(entity.getProductName());
        holder.tv_price.setText(TextDisposeUtils.dispseMoneyText(entity.getSeckillPrice()));
        String com = entity.getShopSource() + TextDisposeUtils.dispseMoneyText(entity.getPrice());
        holder.tv_com_price.setText(com);
//        holder.tv_com_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //设置图片
        final String image = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        Glide.with(mContext).load(image).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(holder.iv_image);
        //剩余数量 - 进度
        int haveSold = Integer.parseInt(entity.getTotalStock()) - Integer.parseInt(entity.getStock());
        int schedule = (int)(((float)haveSold / (float) Integer.parseInt(entity.getTotalStock())) * 100);
        holder.pb_seckill.setProgress(schedule);
        //商品状态
        boolean isSellOut = "0".equals(entity.getStock());
        holder.btn_buy.setTextColor(mContext.getResources().getColor(isSellOut ? R.color.noclick : R.color.vs_white));
        holder.btn_buy.setText(isSellOut ? "已售竭":"立即抢购");
        holder.btn_buy.setBackgroundResource(isSellOut ? R.drawable.btn_shape_v3 : R.drawable.btn_shape_v2);
        holder.iv_soldout.setVisibility(isSellOut ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice","seckillProductId", "seckill","type"};
                String[] value = new String[]{entity.getProductNo(), image, entity.getProductName(),entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill",mContext.getString(R.string.member_region)};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.iv_soldout)
        ImageView iv_soldout;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_com_price)
        TextView tv_com_price;
        @BindView(R.id.pb_seckill)
        PercentProgressBar pb_seckill;
        @BindView(R.id.btn_buy)
        TextView btn_buy;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
