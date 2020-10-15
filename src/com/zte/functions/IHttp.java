package com.zte.functions;

import java.util.TreeMap;

import android.content.Context;

/**
 * Created by Jiangxuewu on 2015/2/3.
 */
public interface IHttp {

    /**
     * 获取方法名
     *
     * @return
     */
    public abstract String getMethod();

    /**
     * 获取参数
     *
     * @param context
     * @return
     */
    public abstract TreeMap<String, String> getParamas(Context context);

    /**
     * 处理请求结果
     *
     * @param result
     */
    public abstract void handleResult(Context context, String result);

    /**
     * 获取签名参数
     *
     * @return
     */
    public abstract String getSignParams(Context context);

    /**
     * 获取签名方式
     * @return
     */
    public abstract String getAuthType();
}
