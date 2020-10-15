package com.weiwei.home.test;

import com.weiwei.salemall.bean.ResultEntity;

/**
 * @author : hc
 * @date : 2019/4/10.
 * @description: 复数的网络请求
 */

public abstract class RetrofitCallBacks<T extends ResultEntity> extends RetrofitCallback<T> {

    private RequestsListener listener;

    public RetrofitCallBacks(RequestsListener listener){
        this.listener = listener;
        if(listener != null) listener.addHttpCount();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if(listener != null) listener.httpFinish();
    }

}
