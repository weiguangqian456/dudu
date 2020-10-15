package com.weiwei.base.activity.me;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.recharge.VsRechargePayTypes;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TwitterEquitiesActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_become_twitter)
    ImageView mTvBecomeTwitter;

    private String brandid;
    private String goodsid;
    private String goodsvalue;
    private String goodsname;
    private String goodsdes;
    private String recommend_flag;
    private String convert_price;
    private String pure_name;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_twitter_equities;
    }

    @Override
    protected int getStateBarColor() {
        return getResources().getColor(R.color.public_color_white);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        brandid = getIntent().getStringExtra("brandid");
        goodsid = getIntent().getStringExtra("goodsid");
        goodsvalue = getIntent().getStringExtra("goodsvalue");
        goodsname = getIntent().getStringExtra("goodsname");
        goodsdes = getIntent().getStringExtra("goodsdes");
        recommend_flag = getIntent().getStringExtra("recommend_flag");
        convert_price = getIntent().getStringExtra("convert_price");
        pure_name = getIntent().getStringExtra("pure_name");
        // 修改
        mTvTitle.setText(goodsname);
        ImageView iv_bg = findViewById(R.id.iv_bg);
        ImageView tv_become_twitter = findViewById(R.id.tv_become_twitter);
        ImageView iv_text = findViewById(R.id.iv_text);
        switch (goodsid){
            case "200008":

                break;
            case "200031":
                iv_bg.setImageResource(R.mipmap.icon_vip2_bg);

                Glide.with(this).load(R.mipmap.icon_vip2_text).into(iv_text);
              //  Glide.with(this).load(R.mipmap.icon_vip2_bg).into(iv_bg);
                Glide.with(this).load(R.mipmap.icon_vip2_btn).into(tv_become_twitter);
                break;

            default:{ indea();
            finish();}

        }
        if(goodsname.equals("二级代理商")){

        }
    }

    @OnClick({R.id.fl_back, R.id.tv_become_twitter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_become_twitter:
                indea();
                break;
        }
    }

    public void indea(){
        Intent intent = new Intent();
        intent.putExtra("brandid", brandid);
        intent.putExtra("goodsid", goodsid);
        intent.putExtra("goodsvalue", goodsvalue);
        intent.putExtra("goodsname", goodsname);
        intent.putExtra("goodsdes", goodsdes);
        intent.putExtra("recommend_flag", recommend_flag);
        intent.putExtra("convert_price", convert_price);
        intent.putExtra("present", "没有数据");
        intent.putExtra("pure_name", pure_name);
        intent.setClass(mContext, VsRechargePayTypes.class);
        mContext.startActivity(intent);
    }
}
