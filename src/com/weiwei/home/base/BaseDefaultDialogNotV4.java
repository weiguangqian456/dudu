package com.weiwei.home.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.hwtx.dududh.R;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description:
 */

public abstract class BaseDefaultDialogNotV4 extends android.app.DialogFragment {

    protected Context mContext;

    protected abstract int   getGravity ();
    protected abstract int   getLayoutId();
    protected abstract void  initView   ();

    protected Boolean isLoadAnimation(){
        return true;
    }

    protected int getDAnimation(){
        return R.style.DialogAnimation;
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

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if(window == null){return;}
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = getGravity();
        params.width = getResources().getDisplayMetrics().widthPixels;
        if(isLoadAnimation()){
            params.windowAnimations = getDAnimation();
        }
        window.setAttributes(params);
    }
}
