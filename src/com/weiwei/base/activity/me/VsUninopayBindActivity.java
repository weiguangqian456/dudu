package com.weiwei.base.activity.me;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * @Title:Android客户端
 * @Description:关于界面
 * @Copyright: Copyright (c) 2014
 * @author: Sundy
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsUninopayBindActivity extends VsBaseActivity implements
        OnClickListener {
    public final char MSG_ID_CheckCardSucceed = 85;
    /**
     * 查询通话时间失败
     */
    public final char MSG_ID_CheckCardFail = 86;
    private TextView unionpay_bind_info;
    private Button bind_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_unionpay_bind_layout);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        unionpay_bind_info = (TextView) findViewById(R.id.unionpay_bind_info);
        bind_button = (Button) findViewById(R.id.unionpay_bind);
        bind_button.setOnClickListener(this);
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        mTitleTextView.setText("银行卡信息");
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.unionpay_bind:
                startActivity(mContext, VsUninopayGetActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        searchcard();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /**
     * 查询余额
     */
    private void searchcard() {
        loadProgressDialog("查询中...");
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.actionSeaRchcard);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        CoreBusiness.getInstance().startThread(mContext,
                GlobalVariables.INTERFACE_SEACHE_CARD,
                DfineAction.authType_UID, null,
                GlobalVariables.actionSeaRchcard);
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.handleKcBroadcast(context, intent);

        String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        try {
            JSONObject jData = new JSONObject(jStr);
            String retStr = jData.getString("result");

            if (retStr.equals("0")) {

                // 卡信息
                JSONObject cardinfo = null;
                cardinfo = jData.getJSONObject("data");
                String bank_account = cardinfo.getString("bank_account");
                bundle.putString("bank_account", bank_account);
                message.what = MSG_ID_CheckCardSucceed;
            } else {
                if (retStr.equals("-99")) {
                    dismissProgressDialog();
                    if (!VsUtil.isCurrentNetworkAvailable(mContext))
                        return;
                }
                bundle.putString("msg", jData.getString("msg"));
                message.what = MSG_ID_CheckCardFail;
            }

        } catch (Exception e) {
            e.printStackTrace();
            bundle.putString("msg",
                    getResources().getString(R.string.servicer_busying));
            message.what = MSG_ID_CheckCardFail;
        }
        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_CheckCardSucceed:
                dismissProgressDialog();
                String bankaccount = msg.getData().getString("bank_account");
                if (bankaccount.length() > 0 && bankaccount != null) {
                    unionpay_bind_info.setText(bankaccount);
                    bind_button.setText("去换绑");
                } else {
                    unionpay_bind_info.setText("尚未绑定银行卡");
                    bind_button.setText("去绑定");
                }
                break;
            case MSG_ID_CheckCardFail:
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }
}
