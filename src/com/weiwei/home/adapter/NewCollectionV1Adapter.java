package com.weiwei.home.adapter;

import android.content.Context;
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
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hc
 * @date by hc on 2019/3/1.
 * @description: 复制于CollectionV1Adapter
 */

public class NewCollectionV1Adapter extends BaseRecycleAdapter{

    public NewCollectionV1Adapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_collection_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        MyViewHolder holder = (MyViewHolder)holder1;
        final CollectionEntity bean = JSONObject.parseObject(getItem(position).toString(),CollectionEntity.class);
        String name = bean.getStoreName();
        if (!StringUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        String distance = bean.getDistance();
        if (!StringUtils.isEmpty(distance)) {
            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText(distance);
        } else {
            holder.distance.setVisibility(View.GONE);
        }

        String address = bean.getStoreAddress();
        if (!StringUtils.isEmpty(address)) {
            holder.address.setText(address);
        }

        String perPrice = bean.getAverageConsumption();
        if (!StringUtils.isEmpty(perPrice)) {
            holder.perPrice.setText("人均消费：¥" + perPrice);
        }

        String discount = bean.getPropaganda();
        if (!StringUtils.isEmpty(discount)) {
            holder.discount.setText(discount);
        }

        final String storeNo = bean.getStoreNo();
        holder.contenRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipPageUtils.getInstance(mContext).skipPage(StoreDetailActivity.class,"storeNo", storeNo);
            }
        });

        String image = JudgeImageUrlUtils.isAvailable(bean.getIconUrl());
        if (!StringUtils.isEmpty(image)) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.mall_logits_default);
            Glide.with(mContext).load(image).apply(options).into(holder.image);
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout contenRl;
        private TextView name;
        private TextView distance;
        private TextView perPrice;
        private TextView address;
        private TextView discount;
        private ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            contenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            distance = (TextView) itemView.findViewById(R.id.tv_distance);
            perPrice = (TextView) itemView.findViewById(R.id.tv_perprice);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            discount = (TextView) itemView.findViewById(R.id.tv_discount);
        }
    }
}
