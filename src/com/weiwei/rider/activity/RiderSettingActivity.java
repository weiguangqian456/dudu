package com.weiwei.rider.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.rider.base.RiderBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class RiderSettingActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    @BindView(R.id.iv_light_switch)
    ImageView ivLightSwitch;

    private boolean switchOn = true;
    private boolean switchLightOn = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_setting;
    }

    @Override
    protected void initView() {
        tvTitle.setText("设置");

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.rl_back,R.id.iv_switch,R.id.iv_light_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_switch:
                if(switchOn) {
                    ivSwitch.setImageResource(R.drawable.switch_off);
                }else {
                    ivSwitch.setImageResource(R.drawable.switch_on);
                }
                switchOn = !switchOn;
                break;
            case R.id.iv_light_switch:
                if(switchLightOn) {
                    ivLightSwitch.setImageResource(R.drawable.switch_off);
                }else {
                    ivLightSwitch.setImageResource(R.drawable.switch_on);
                }
                switchLightOn = !switchLightOn;
                break;
        }
    }
}
