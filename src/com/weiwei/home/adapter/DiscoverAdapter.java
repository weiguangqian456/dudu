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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.SkipPageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/16.
 * @description: 发现好货  ITEM
 */

public class DiscoverAdapter extends BaseRecycleAdapter{

    private String mColumnId;

    public DiscoverAdapter(Context mContext,String mColumnId) {
        super(mContext);
        this.mColumnId = mColumnId;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_discover,parent,false));
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final ProductBean bean = JSONObject.parseObject(getItem(position).toString(),ProductBean.class);
        ItemViewHolder holder = (ItemViewHolder) holder1;
        String imageUrl = TextDisposeUtils.isAvailable(bean.getPicture());
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(10))
                .placeholder(R.drawable.mall_logits_default);
        Glide.with(mContext).load(imageUrl).apply(options).into(holder.iv_image);
        holder.tv_name.setText(bean.getProductName());
        holder.tv_price.setText("VIP"+TextDisposeUtils.dispseMoneyText(bean.getPrice()));
        holder.tv_description.setText(bean.getJdPrice2());
//        holder.tv_description.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                String[] value = new String[]{bean.getProductNo(), TextDisposeUtils.isAvailable(bean.getPicture()), bean.getProductName(), bean.getPrice()};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_description)
        TextView tv_description;
        @BindView(R.id.tv_price)
        TextView tv_price;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
