package com.weiwei.base.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwtx.dududh.R;
import com.weiwei.salemall.widget.CustomProgressDialog;

import butterknife.ButterKnife;

public abstract class LazyBaseFragment extends Fragment {

    private boolean isViewCreated;
    private boolean isLoadDataComplete;
    protected CustomProgressDialog loadingDialog;
    protected Handler mHandler;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoadDataComplete && isViewCreated) {
            isLoadDataComplete = true;
            loadData();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        ButterKnife.bind(this,view);
        isViewCreated = true;
        return view;
    }

    protected abstract void initView(View view);

    protected abstract int getLayoutId();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingDialog = new CustomProgressDialog(getContext(), "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        mHandler = new Handler();
        if (getUserVisibleHint() && !isLoadDataComplete) {
            isLoadDataComplete = true;
            loadData();
        }
    }

    protected abstract void loadData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
