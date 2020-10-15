package com.weiwei.rider.utils;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hwtx.dududh.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyDialog extends DialogFragment {

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_dialog,container);
        ButterKnife.bind(this,view);
        this.mContext = getActivity();
        return view;
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
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = R.style.DialogAnimation;
        params.width = getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(params);
    }

    @OnClick({R.id.tv_cancel,R.id.tv_online,R.id.tv_unline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_online:
                if(mOnClickekListener != null) {
                    mOnClickekListener.itemOnline();
                }
                this.dismiss();
                break;
            case R.id.tv_unline:
                if(mOnClickekListener != null) {
                    mOnClickekListener.itemUnline();
                }
                this.dismiss();
                break;
        }
    }
    private OnClickListener mOnClickekListener;

    public interface OnClickListener {

        void itemOnline();

        void itemUnline();
    }

    public void setOnClickekListener(OnClickListener mOnClickekListener) {
        this.mOnClickekListener = mOnClickekListener;
    }
}
