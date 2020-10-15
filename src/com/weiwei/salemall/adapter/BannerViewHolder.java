package com.weiwei.salemall.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

/**
 * @author Created by EDZ on 2018/08/08
 *         店铺图片轮播适配器
 */
public class BannerViewHolder implements Holder<BannerImageEntity> {
    private View rootview;
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        rootview = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.store_top_banner, null);
        imageView = (ImageView) rootview.findViewById(R.id.sdv_item_head_img);
        return rootview;
    }

    @Override
    public void UpdateUI(final Context context, int position, BannerImageEntity data) {
        String imageUrl = JudgeImageUrlUtils.isAvailable(data.getImageUrl());
        Glide.with(context).load(imageUrl).into(imageView);
        int skipType = data.getSkipType();
        final String params = data.getAndroidParams();
        final String className = data.getAndroidClassName().trim();
        JSONObject jsonObject = JSONObject.parseObject(params);
        if (jsonObject != null) {
            final String productNo = (String) jsonObject.get("productNo");
            final String skipUrl = (String) jsonObject.get("skipUrl");
            final String storeNo = (String) jsonObject.get("storeNo");
            if (params != null && className != null && !className.equals("")) {
                switch (skipType) {
                    case 0:
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                if (className.contains("VsRechargeActivity") && !CheckLoginStatusUtils.isLogin()) {
                                    intent.setClassName(context, "com.weiwei.base.activity.login.VsLoginActivity");
                                    context.startActivity(intent);
                                } else {
                                    intent.setClassName(context, className);
                                    if (productNo != null && !productNo.equals("")) {
                                        intent.putExtra("productNo", productNo);
                                    } else {
                                        intent.putExtra("storeNo", storeNo);
                                    }
                                    context.startActivity(intent);
                                }
                            }
                        });
                        break;
                    case 1:
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClassName(context, className);
                                intent.putExtra("skipUrl", skipUrl);
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
