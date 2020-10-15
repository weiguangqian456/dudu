package com.weiwei.salemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.PreferencesUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Created by EDZ on 2018/08/06
 *         广告页
 */
public class AdActivity extends VsBaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_ad)
    ImageView adIv;
    @BindView(R.id.tv_countdown)
    TextView countDownTv;
    @BindView(R.id.countdownLl)
    LinearLayout countdownLl;

    private String className;
    private String adUrl;
    private String params;
    private String productNo;
    private String storeNo;
    private String skipUrl;

    private boolean isClicked = false;

    /**
     * 倒计时
     */
    private int recLen;
    Timer timer = new Timer();

    /**
     * Handler ui刷新常量
     */
    private static final int MSG_REFRESH_SKIP = 101;
    private static final int MSG_REFRESH_COUNTTIME = 102;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClicked) {
            skipPageAndFinish(VsMainActivity.class);
        }
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_SKIP:
                skipPageAndFinish(VsMainActivity.class);
                break;
            case MSG_REFRESH_COUNTTIME:
                countdownLl.setVisibility(View.VISIBLE);
                countDownTv.setText(recLen + "s" + " 跳过");
                recLen--;
                if (recLen < 0) {
                    timer.cancel();
                    countdownLl.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        adUrl = JudgeImageUrlUtils.isAvailable(PreferencesUtils.getString(VsApplication.getContext(), "adUrl"));
        className = PreferencesUtils.getString(VsApplication.getContext(), "className").trim();
        params = PreferencesUtils.getString(VsApplication.getContext(), "params");
        recLen = PreferencesUtils.getInt(VsApplication.getContext(), "duration");
        JSONObject jsonObject = JSONObject.parseObject(params);
        if (jsonObject != null) {
            productNo = (String) jsonObject.get("productNo");
            storeNo = (String) jsonObject.get("storeNo");
            skipUrl = (String) jsonObject.get("skipUrl");
        }
        Glide.with(this).load(adUrl).into(adIv);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isClicked) {
                    skipPageAndFinish(VsMainActivity.class);
                }
            }
        }, recLen * 1000);


        countDownTv.setOnClickListener(this);
        adIv.setOnClickListener(this);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = MSG_REFRESH_COUNTTIME;
            mBaseHandler.sendMessage(message);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ad:
                isClicked = true;
                Intent intent = new Intent();
                intent.setClassName(this, className);
                if (!StringUtils.isEmpty(productNo)) {
                    intent.putExtra("productNo", productNo);
                    intent.putExtra("productImage", adUrl);
                }
                if (!StringUtils.isEmpty(storeNo)) {
                    intent.putExtra("storeNo", storeNo);
                }
                if (!StringUtils.isEmpty(skipUrl)) {
                    intent.putExtra("skipUrl", skipUrl);
                }
                startActivity(intent);
                break;
            case R.id.tv_countdown:
                timer.cancel();
                Message message = new Message();
                message.what = MSG_REFRESH_SKIP;
                mBaseHandler.sendMessage(message);
                break;
            default:
                break;
        }
    }
}
