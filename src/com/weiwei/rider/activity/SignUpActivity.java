package com.weiwei.rider.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.salemall.widget.CustomProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class SignUpActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    private Handler handler;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }


    @Override
    protected void initView() {
        tvTitle.setText("加入骑手");

        handler = new Handler();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.rl_back,R.id.btn_sign_up,R.id.tv_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_sign_up:
                startActivity(new Intent(this,JoinRiderActivity.class));
                finish();
                break;
            case R.id.tv_refresh:
                refreshData();
                break;

        }
    }

    private void refreshData() {
        loadingDialog.setLoadingDialogShow();
        handler.postDelayed(() -> loadingDialog.setLoadingDialogDismiss(),1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
