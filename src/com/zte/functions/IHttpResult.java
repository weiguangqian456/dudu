package com.zte.functions;

import android.content.Context;

/**
 * Created by Jiangxuewu on 2015/2/4.
 */
public interface IHttpResult {
    /**
     *  run in sub thread.
     * @param context
     * @param result
     */
    public void handleResult(Context context, String result);
}
