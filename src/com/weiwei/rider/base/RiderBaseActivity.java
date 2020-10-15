package com.weiwei.rider.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import butterknife.ButterKnife;

public abstract class RiderBaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected CustomProgressDialog loadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
