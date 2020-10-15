package com.weiwei.base.activity.me;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 签到页面
 */
public class VsSignInFirstActivity extends VsBaseActivity {
    TextView mSignInNoticeViewFirst;
    TextView SignContentViewFirst;
    TextView TitletostFirst;
    TextView FavourateContentViewFirst;
    TextView TotalGive_First;
    TextView TotalFavourate_First;
    LinearLayout ClockSetting;
    ImageButton ClockSwitch;
    TextView ClockTimeShow;
    Button SignInButton_now_first;
    ProgressDialog mProgress;
    String TAG = "sign_in_first";
    String msgString = "签到失败,请稍后再试!";
    private final int MSG_ID_Signin_Fail_Message = 1;
    private final int MSG_ID_Signin_Success_Message = 0;
    private final int DIALOG_TIME = 2; // 设置对话框id
    private boolean clockImageSwitch;
    private int gethour;
    private int getminute;
    LinearLayout slid_title;

    String sign_result = "";
    String sign_count = "";
    String sign_count_info = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_everyday_signin_first);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getResources().getString(R.string.vs_sign_title));
        showLeftNavaBtn(R.drawable.icon_back);


        SignInButton_now_first = (Button) findViewById(R.id.SignInButton_now_first);
        mSignInNoticeViewFirst = (TextView) findViewById(R.id.SigninNoticeViewFirst);

        TitletostFirst = (TextView) findViewById(R.id.Titletost_First);
        SignContentViewFirst = (TextView) findViewById(R.id.SignContentView_First);

        TotalGive_First = (TextView) findViewById(R.id.TotalGive_First);


        String sign_head = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_header);
        if (sign_head == null || ("").equals(sign_head)) {
            sign_head = getResources().getString(R.string.everyday_signin_below_prompt);
        }
        String sign_explain = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_explain);
        if (sign_explain == null || ("").equals(sign_explain)) {
            sign_explain = getResources().getString(R.string.everyday_signin_prompt_info);
        }

        if (sign_head.length() > 0) {
            mSignInNoticeViewFirst.setText(sign_head);
        }
        if (sign_explain.length() > 0) {
            SignContentViewFirst.setText(sign_explain);
        }

        SignInButton_now_first.setOnClickListener(getSignInMethod);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_TIME:
                dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        //setClockTime(hourOfDay, minute);
                        gethour = hourOfDay;
                        getminute = minute;
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),
                        get24HourMode());
                break;
        }
        return dialog;
    }


    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(this);
    }

    @SuppressLint({"SimpleDateFormat", "UseValueOf"})
    public long getTime(String dateValue) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Long l = new Long(dateValue);
        try {
            dateValue = df.format(new Date(l));
            d1 = df.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1.getTime();
    }

    private View.OnClickListener ClockSettingListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
            showDialog(DIALOG_TIME);
        }

    };

    private View.OnClickListener getSignInMethod = new View.OnClickListener() {
        public void onClick(View arg0) {
            if ("立即签到".equals(SignInButton_now_first.getText())) {
                loadProgressDialog("正在签到,请稍候...");
                sigin();
            } else {
                try {
                    String url = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_btntarget);

                    CustomLog.i(TAG, "getSignInMethod, ... url is " + url);

                    if (TextUtils.isEmpty(url)) {
                        url = "3016";
                    }

                    if (url.equals("3035")) {
                        VsUtil.startActivity(url, mContext, null);
                    } else {
                        VsUtil.startActivity(url, mContext, null);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 签到返回结果
     *
     * @param intent
     * @param context
     */
    @SuppressLint("SimpleDateFormat")
    protected void handleKcBroadcast(Context context, Intent intent) {
        super.handleKcBroadcast(context, intent);
        String signStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        CustomLog.i(TAG, "handleKcBroadcast(), signStr is " + signStr);

        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();

        try {

            JSONObject returnResult = new JSONObject(signStr);

            String signResult = "";
            JSONObject contentResult = null;
            String btntext = "";
            String btntarget = "";
            String btnresult = "";
            String btnresulttarget = "";
            String sign_sun_awards = "";
            try {
                signResult = returnResult.getString("result");
                contentResult = returnResult.getJSONObject("sign_result");
                btntext = contentResult.getString("btn_txt");
                if (TextUtils.isEmpty(btntext)) {
                    btntext = mContext.getString(R.string.vs_check_the_bill);
                }
                btntarget = contentResult.getString("btn_target");
                btnresult = contentResult.getString("sign_result");
                btnresulttarget = contentResult.getString("btn_result");

                VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_btntext, btntext);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_btntarget, btntarget);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_btnresult, btnresult);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_btnresult_target, btnresulttarget);
                sign_sun_awards = contentResult.getString("sign_sun_awards");
                VsUserConfig.setData(context, VsUserConfig.JKey_SIGNIN_SHARE, sign_sun_awards);

            } catch (Exception e1) {
                e1.printStackTrace();
            }


            if (signResult.equals("1")) {
                try {
                    signResult = contentResult.getString("sign_result");
                    if (signResult != null) {
                        sign_result = signResult.toString();
                    }

                    signResult = contentResult.getString("order");
                    if (signResult != null) {
                        sign_result = sign_result + "\n" + signResult.toString();

                    }

                    VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_success_explain, sign_result);

                    signResult = contentResult.getString("stat");
                    if (signResult != null) {
                        sign_count_info = signResult.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_success_header, sign_count_info);
                message.what = MSG_ID_Signin_Success_Message;

                Intent intentdrainage = new Intent();
                intentdrainage.setClass(mContext, KcDrainageDialog.class);
                startActivity(intentdrainage);

            } else if (signResult.equals("0")) {
                try {

                    // 获取当前时间
                    Date curDate = new Date(System.currentTimeMillis());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    String signSuccessTime = formatter.format(curDate);
                    VsUserConfig.setData(this, VsUserConfig.JKey_SigninSuccessTime, signSuccessTime);

                    signResult = contentResult.getString("sign_result");
                    if (signResult != null) {
                        sign_result = signResult.toString();
                    }
                    signResult = contentResult.getString("order");
                    if (signResult != null) {
                        sign_result = sign_result + "\n" + signResult.toString();
                    }
                    signResult = contentResult.getString("stat");
                    if (signResult != null) {
                        sign_count_info = signResult.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                message.what = MSG_ID_Signin_Success_Message;

                Intent intentdrainage = new Intent();
                intentdrainage.setClass(mContext, KcDrainageDialog.class);
                startActivity(intentdrainage);
            } else {
                if (signResult.equals("-99")) {
                    dismissProgressDialog();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bundle.putString("msg", getResources().getString(R.string.request_failinfo));
            message.what = MSG_ID_Signin_Fail_Message;
        }

        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_Signin_Success_Message:
                dismissProgressDialog();
                if (sign_result.length() > 2) {
                    mSignInNoticeViewFirst.setText(sign_result);
                    SignInButton_now_first.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_btntext));
                    if (sign_count_info.length() > 2) {
                        TotalGive_First.setText(sign_count_info);
                        TotalGive_First.setVisibility(View.VISIBLE);
                    }
                } else {
                    mSignInNoticeViewFirst.setText(msgString);
                }

                TitletostFirst.setVisibility(View.GONE);
                SignContentViewFirst.setVisibility(View.GONE);
                break;
            case MSG_ID_Signin_Fail_Message:
                dismissProgressDialog();
                if (sign_result.length() > 2) {
                    mSignInNoticeViewFirst.setText(sign_result);
                    if (sign_count_info.length() > 2) {
                        TotalGive_First.setText(sign_count_info);
                        TotalGive_First.setVisibility(View.VISIBLE);
                    }
                } else {
                    mSignInNoticeViewFirst.setText(msgString);
                }
                break;
            default:
                break;
        }
    }

    /**
     * qiandao
     *
     * @author 黄发兴
     * @version 创建时间：2014-12-7 下午08:01:12
     */
    private void sigin() {
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.VS_ACTION_SIGNIN);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_SIGIN, DfineAction.authType_UID, null, GlobalVariables.VS_ACTION_SIGNIN);
    }
}
