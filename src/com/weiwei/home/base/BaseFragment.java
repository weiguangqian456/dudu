package com.weiwei.home.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwtx.dududh.R;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * @author hc
 * @date 2019/2/27.
 * @description:
 */

public abstract class BaseFragment extends Fragment {

    private Bundle arguments;
    protected Context mContext;
    protected boolean isInitView = false;
    private View rootView;
    protected CustomProgressDialog loadingDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(rootView == null){ rootView = inflater.inflate(getLayoutId(),null); }
        mContext = getActivity();
        ButterKnife.bind(this,rootView);
        if(isBindEvent()){ EventBus.getDefault().register(this); }
        isInitView = true;
        loadingDialog = new CustomProgressDialog(getContext(), "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        initView();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isBindEvent()){EventBus.getDefault().unregister(this);}
    }

    protected String getArgumentsInfo(String key){
        if(arguments == null){
            arguments = getArguments();
        }
        return arguments.getString(key);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected Boolean isBindEvent(){
        return false;
    }
}
