package com.weiwei.salemall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

/**
 * @author Created by EDZ on
 *         商品详情中图片轮播适配器
 */
public class NetworkImageHolderView implements Holder<String> {
    private View rootview;
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        rootview = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.goods_item_head_img, null);
        imageView = (ImageView) rootview.findViewById(R.id.sdv_item_head_img);
        return rootview;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        if (data != null) {
            String imageUrl = JudgeImageUrlUtils.isAvailable(data);
            Glide.with(context).load(imageUrl).into(imageView);
        }
    }
}
