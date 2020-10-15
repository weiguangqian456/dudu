package com.hwtx.dududh.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.home.activity.RefuelCouponMoneyActivity;
import com.weiwei.home.activity.RefuelOrderActivity;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.VsMainActivity;

/**
 * Created by Jiangxuewu on 2015/4/8.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXPayEntryActivity.class.getSimpleName();
    private static final int MSG_UPDATE_TIP_TEXT_ID = 10;

    private IWXAPI api;

    private TextView mMsgTV;

    private String mTipMsg;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TIP_TEXT_ID:
                    mMsgTV.setText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weixin_pay_result);
        mMsgTV = (TextView) findViewById(R.id.message);

        api = WXAPIFactory.createWXAPI(this, DfineAction.WEIXIN_APPID);

        api.handleIntent(getIntent(), this);

        mTipMsg = getIntent().getStringExtra("message");

        if (null == mTipMsg) {
            mTipMsg = "";
        }
        mMsgTV.setText(mTipMsg);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    public void onClick_Sure(View view) {
        finish();
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        CustomLog.i(TAG, "winxin_ 5 errCode = " + resp.errCode + ", type = " + resp.getType());

        Message msg = new Message();
        msg.what = MSG_UPDATE_TIP_TEXT_ID;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK://展示成功页面
                if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                    msg.obj = getString(R.string.pay_result_success);
                    Intent intent;
                    if(VsApplication.getInstance().payType == 100) {
                        intent = new Intent(this,RefuelOrderActivity.class);
                        if(RefuelCouponMoneyActivity.instance != null) {
                            RefuelCouponMoneyActivity.instance.finish();
                        }
                    }else {
                        intent = new Intent(this,VsMainActivity.class);
                        intent.putExtra("indicator",1);
                    }
                    startActivity(intent);
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://无需处理。发生场景：用户不支付了，点击取消，返回APP。
                msg.obj = getString(R.string.pay_result_user_cancel);
                break;
            case BaseResp.ErrCode.ERR_COMM://可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_SENT_FAILED:
            case BaseResp.ErrCode.ERR_UNSUPPORT:
            default:
                if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                    msg.obj = getString(R.string.pay_result_callback_msg, resp.errStr + ";code=" + String.valueOf(resp.errCode));
                } else {
                    msg.obj = getString(R.string.pay_result_callback_msg, resp.errStr + ";code=" + String.valueOf(resp.errCode) + ";type=" + resp.getType());
                }

                break;
        }
        finish();
//        mHandler.sendMessage(msg);
    }
}