package com.weiwei.home.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.account.VipMemberActivity;
import com.weiwei.base.activity.me.VsRechargeActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : hc
 * @date : 2019/5/21.
 * @description: 自定义的升级弹窗(你以为是更新 其实是会员升级)
 */

public class CustomUpdataNotV4Dialog extends DialogFragment {

    @BindView(R.id.iv_image)
    ImageView iv_image;

    protected Context mContext;

    private int mDrawableId = R.drawable.imgae_up_member;

    public static CustomUpdataNotV4Dialog getInstance(){
        return new CustomUpdataNotV4Dialog();
    }

    public CustomUpdataNotV4Dialog setDrawableId(int mDrawableId){
        this.mDrawableId = mDrawableId;
        return this;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getLayoutId() {
        return R.layout.dialog_updata_member;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), container);
        this.mContext = getActivity();
        ButterKnife.bind(this,inflate);
        initView();
        return inflate;
    }

    protected void initView() {
        Glide.with(mContext).load(mDrawableId).into(iv_image);
    }

    @OnClick(R.id.iv_up)
    public void toSkipMember(){
        if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt3), mContext)) {
            startActivity(new Intent(mContext, VsRechargeActivity.class));
        }
//        SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_REGION,null);
    }

    @OnClick(R.id.tv_look_member)
    public void toSkipLook(){
        startActivity(new Intent(mContext,VipMemberActivity.class));
    }

    @OnClick(R.id.iv_close)
    public void toClose(){
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if(window == null){return;}
        //背景
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //设置在底部 以及 填充全部宽度
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = getGravity();
        params.width = getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(params);
    }
}
