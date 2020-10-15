package com.weiwei.salemall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import com.hwtx.dududh.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weiwei.base.activity.aplpay.PayResult;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.zte.common.MD5;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Created by EDZ on 2018/8/10.
 *         三种支付方式的封装
 */

public class PayUtils {
    private String TAG = PayUtils.class.getSimpleName();

    /**
     * 支付类型
     */
    public final static int PAY_TYPE_WX = 1;
    public final static int PAY_TYPE_ALI = 2;
    public final static int PAY_TYPE_RED = 3;

    /**
     * 支付宝返回参数
     */
    public final static int SDK_PAY_FLAG = 1001;
    private static final int SDK_CHECK_FLAG = 1002;

    private static PayUtils instance;
    private static Context mContext;
    private int flag;

    private PayUtils() {
    }

    public static PayUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PayUtils();
        }
        mContext = context;
        return instance;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
//                    PayResult payResult = new PayResult((String) msg.obj);
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        switch (flag) {
                            case 0: //默认支付
//                                SkipPageUtils.getInstance(mContext).skipPageAndFinish(VsMainActivity.class);
                                Intent intent = new Intent(mContext,VsMainActivity.class);
                                intent.putExtra("indicator",1);
                                mContext.startActivity(intent);
                                ((Activity)mContext).finish();
                                break;
                            case 1:  //支付宝充值
                                sendPaySuccedMsg();
                                break;
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                            switch (flag) {
                                case 0: //默认支付
 //                                   SkipPageUtils.getInstance(mContext).skipPageAndFinish(VsMainActivity.class);
                                    Intent intent = new Intent(mContext,VsMainActivity.class);
                                    intent.putExtra("indicator",1);
                                    mContext.startActivity(intent);
                                    ((Activity)mContext).finish();
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 发送支付成功的消息
     */
    private void sendPaySuccedMsg() {
        MessageEvent bean = new MessageEvent();
        bean.setMessage("paySucced");
        EventBus.getDefault().post(bean);
    }

    /**
     * 微信支付
     *
     * @param
     * @param result
     */
    public void toWXPay(Context mContext, String result) {
        VsApplication.getInstance().payType = 0;
        //result中是重服务器请求到的签名后各个字符串，可自定义
        //result是服务器返回结果
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, DfineAction.WEIXIN_APPID, true);
        PayReq request = new PayReq();
        request.appId = DfineAction.WEIXIN_APPID;
        request.partnerId = DfineAction.WEIXIN_MCH_ID;
        request.prepayId = result;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = genNonceStr();
        request.timeStamp = "" + (System.currentTimeMillis() / 1000);
        request.sign = getWeiXinPaySign(request);
        api.registerApp(DfineAction.WEIXIN_APPID);
        boolean send = api.sendReq(request);
        if (!send) {
            Intent i = new Intent(mContext, WXPayEntryActivity.class);
            i.putExtra("message", "未安装微信客户端");
            mContext.startActivity(i);
        }
//        if (!send) {
//            SkipPageUtils.getInstance((Activity)mContext).skipPage(WXPayEntryActivity.class,
//                    "message", "未安装微信客户端");
//        }
    }

    /*==================微信支付====================*/
    private String getWeiXinPaySign(PayReq payReq) {
        if (null == payReq) {
            return null;
        }

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();

        if (!TextUtils.isEmpty(payReq.appId)) {
            signParams.add(new BasicNameValuePair("appid", payReq.appId));
        }
        if (!TextUtils.isEmpty(payReq.extData)) {
            signParams.add(new BasicNameValuePair("extdata", payReq.extData));
        }
        if (!TextUtils.isEmpty(payReq.nonceStr)) {
            signParams.add(new BasicNameValuePair("noncestr", payReq.nonceStr));
        }
        if (!TextUtils.isEmpty(payReq.packageValue)) {
            signParams.add(new BasicNameValuePair("package", payReq.packageValue));
        }
        if (!TextUtils.isEmpty(payReq.partnerId)) {
            signParams.add(new BasicNameValuePair("partnerid", payReq.partnerId));
        }
        if (!TextUtils.isEmpty(payReq.prepayId)) {
            signParams.add(new BasicNameValuePair("prepayid", payReq.prepayId));
        }
        if (!TextUtils.isEmpty(payReq.timeStamp)) {
            signParams.add(new BasicNameValuePair("timestamp", payReq.timeStamp));
        }

        return genAppSign(signParams);
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(DfineAction.WEIXIN_API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    /**
     * 支付宝
     *
     * @param orderInfo
     */
    public void toAliPay(String orderInfo, int flag) {
        this.flag = flag;
        final String payInfo = orderInfo;
        // 调用pay方法进行支付
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(payInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public static void showMessage(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}