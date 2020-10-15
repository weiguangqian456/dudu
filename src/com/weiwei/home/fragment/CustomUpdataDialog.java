package com.weiwei.home.fragment;

import android.content.Intent;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.account.VipMemberActivity;
import com.weiwei.base.activity.me.VsRechargeActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.base.BaseDefaultDialog;
import com.weiwei.home.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author : hc
 * @date : 2019/5/21.
 * @description: 自定义的升级弹窗(你以为是更新 其实是会员升级)
 */

public class CustomUpdataDialog extends BaseDefaultDialog {

    @BindView(R.id.iv_image)
    ImageView iv_image;

    private int mDrawableId = R.drawable.imgae_up_member;

    public static CustomUpdataDialog getInstance(){
        return new CustomUpdataDialog();
    }

    public CustomUpdataDialog setDrawableId(int mDrawableId){
        this.mDrawableId = mDrawableId;
        return this;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_updata_member;
    }

    @Override
    protected void initView() {
        Glide.with(mContext).load(mDrawableId).into(iv_image);
    }

    @OnClick(R.id.iv_up)
    public void toSkipMember(){
        if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt3), mContext)) {
//            startActivity(new Intent(mContext, VsRechargeActivity.class));
            SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_REGION, null);
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

}
