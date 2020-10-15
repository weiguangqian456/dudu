package com.weiwei.salemall.inter;

/**
 * @author Created by EDZ on 2018/8/10.
 *         支付返回结果
 */

public interface PayResultListener {
    void onPaySuccess();

    void onPayError();

    void onPayCancel();
}
