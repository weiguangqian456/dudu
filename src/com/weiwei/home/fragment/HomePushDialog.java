package com.weiwei.home.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseDefaultDialog;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/4/9.
 * @description: 首页的推送 - 懒得做成一个了
 */

public class HomePushDialog extends BaseDefaultDialog{

    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.iv_ad)
    ImageView iv_ad;

    private BannerImageEntity mEntity;

    public static HomePushDialog getInstance(){
        return new HomePushDialog();
    }

    public HomePushDialog initPushDialog(BannerImageEntity mEntity){
        this.mEntity = mEntity;
        return this;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pop_ad_push;
    }

    @Override
    protected int getDAnimation() {
        return R.style.adpop_anim;
    }

    @Override
    protected void initView() {
        String imageRes = JudgeImageUrlUtils.isAvailable(mEntity.getImageUrl());
        Glide.with(mContext).load(imageRes).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(iv_ad);
        iv_close.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                dismiss();
            }
        });
        iv_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomSkipUtils.toSkip(mContext,mEntity);
            }
        });
    }
}
