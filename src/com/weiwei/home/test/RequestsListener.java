package com.weiwei.home.test;

/**
 * @author : hc
 * @date : 2019/4/10.
 * @description:
 */

public class RequestsListener {

    private HttpRequestFinish mListener;
    private int mHttpCount;
    private int mHttpFinish;
    private Boolean isFinish;
    private int mMaxHttpCount;

    public RequestsListener(HttpRequestFinish mListener){
        this.mListener = mListener;
    }

    public RequestsListener(HttpRequestFinish mListener,int mMaxHttpCount){
        this.mListener = mListener;
        this.mMaxHttpCount = mMaxHttpCount;
    }

    void addHttpCount(){
        mHttpCount++;
    }

    void httpFinish(){
        mHttpFinish++;
        if(mMaxHttpCount != 0){
            if(mHttpFinish == mMaxHttpCount && mListener != null){
                isFinish = true;
                mListener.onHttpRequestFinish();
                mHttpFinish = 0;
                mHttpCount = 0;
            }
        }else{
            if(mHttpFinish == mHttpCount && mListener != null){
                isFinish = true;
                mListener.onHttpRequestFinish();
                mHttpFinish = 0;
                mHttpCount = 0;
            }
        }
    }

    public interface HttpRequestFinish{
        void onHttpRequestFinish();
    }
}
