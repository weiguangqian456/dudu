package com.weiwei.home.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/27.
 * @description: 更新弹窗
 */

public class UpDataNotV4Dialog extends DialogFragment {

    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.fl_close)
    FrameLayout fl_close;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;

    protected Context mContext;
    private String version = "1.0.0";
    private String content = "APP优化";
    private UpDataListener listener;
    private boolean isMandatory = false;

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

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getLayoutId() {
        return R.layout.dialog_updata;
    }

    public static UpDataNotV4Dialog getInstance() {
        return new UpDataNotV4Dialog();
    }

    public UpDataNotV4Dialog setVersion(String version){
        if(!TextUtils.isEmpty(version)){
            this.version = version;
        }
        return this;
    }

    public UpDataNotV4Dialog isUpMandatory(boolean isMandatory){
        this.isMandatory =isMandatory;
        return this;
    }

    public UpDataNotV4Dialog setContent(String content){
        this.content = content;
        return this;
    }

    protected void initView() {
        setCancelable(false);
        String v = "更新版本: " + version;
        tv_version.setText(v);
        tv_content.setText(content);
        if(isMandatory) fl_close.setVisibility(View.GONE);
        fl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){  listener.onUpDataClick(); }
                dismiss();
            }
        });
    }

    public UpDataNotV4Dialog setUpDataListener(UpDataListener listener){
        this.listener = listener;
        return this;
    }

    public interface UpDataListener{
        void onUpDataClick();
    }
}
