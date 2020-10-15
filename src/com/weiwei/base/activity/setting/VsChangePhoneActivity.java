package com.weiwei.base.activity.setting;

import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsMd5;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.util.ReceiverSendNoteObserver;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


public class VsChangePhoneActivity extends VsBaseActivity {
    private TextView mOldBindPhoneTextView;// TODO 原绑定手机号
    private EditText edit_pwd, edit_new_phone, edit_vorification_code;// TODO 输入新手机号和密码
    private TextView tv_bind_forget_pwd;// TODO 忘记密码

    private RoundTextView btn_bind;//

    private Button btn_get_code;// TODO 重新获取验证码按钮
    private String newphonenumber = "";
    private String msgString = null;
    String TAG = "KcRebinActivity";

    // private final String TAG = "KcRebindActivity";
    private final char MSG_ID_Show_Succeed_Message = 0;
    private final char MSG_ID_Show_Fail_Message = 1;
    private int countPercent = 30;
    private final char MSG_ID_Show_COUNT_Message = 23;// 倒计时
    private String comeflag = "";

    public final static String ID_INPUTVERIFICATIONCODERECEIVER = "ID_INPUTVERIFICATIONCODERECEIVER";
    private ReceiverSendNoteObserver receiverNote;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_bind_phone_change);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        Intent intent = getIntent();
        if (intent.hasExtra("comeflag")) { //comeflag 有三种 登录进来绑定loginBind 拨打电话时绑定callBind 设置里绑定settingBind
            comeflag = intent.getStringExtra("comeflag");
        }

        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        init();      
        // KcApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭

     // 注册短信监听
        receiverNote = new ReceiverSendNoteObserver(mBaseHandler);
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, receiverNote);
		countPercent = 30;
    }

    private void init() {
        mOldBindPhoneTextView = (TextView) findViewById(R.id.tv_bind_phone);

        //  String PhoneNum = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        // mOldBindPhoneTextView.setText(getResources().getString(R.string.bind_old_phonenumber) + PhoneNum);

        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
        btn_bind = (RoundTextView) findViewById(R.id.btn_bind);

        btn_bind.setOnClickListener(onClickListener);
        edit_new_phone = (EditText) findViewById(R.id.edit_new_phone);
        edit_vorification_code = (EditText) findViewById(R.id.edit_vorification_code);
        // mPwdEditText.setHint(R.string.login_bind_hint_pwd);
        // setEditTextTextSize(mPwdEditText);

        tv_bind_forget_pwd = (TextView) findViewById(R.id.tv_bind_forget_pwd);
        tv_bind_forget_pwd.setText("忘记密码");
        tv_bind_forget_pwd.setVisibility(View.GONE);

        if ("loginBind".equals(comeflag)) {

            mTitleTextView.setText("绑定手机号");
        }
        if ("settingBind".equals(comeflag)) {
            btn_bind.setText("确认改绑手机号");
            mTitleTextView.setText("改绑手机号");
        }
        if ("callBind".equals(comeflag)) {

            mTitleTextView.setText("绑定手机号");
        }
        /*  tv_bind_forget_pwd.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                  MobclickAgent.onEvent(mContext, "hpsForgetPswClick");
                  Intent intent = new Intent();

                  intent.setClass(mContext, KcFindPwdActivity.class);

                  startActivity(intent);
              }
          });*/
        btn_get_code = (Button) findViewById(R.id.btn_get_code);

        btn_get_code.setOnClickListener(new mRebind1Listener());
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.btn_bind:// 绑定

                /**
                 * 绑定手机第二步
                 */
                String validatecode = edit_vorification_code.getText().toString();
                // System.out.println("pne==2"+newphonenumber);
                newphonenumber = edit_new_phone.getText().toString().replaceAll(" ", "");

                if (VsUtil.isNull(newphonenumber)) {
                    mToast.show(getResources().getString(R.string.input_wrong_phone_1), Toast.LENGTH_SHORT);
                    return;
                }
                if (newphonenumber.length() > 0 && newphonenumber.length() < 11 || newphonenumber.length() > 11) {
                    mToast.show(getResources().getString(R.string.input_wrong_phone), Toast.LENGTH_SHORT);
                    return;
                }

                if (validatecode == null || validatecode.length() == 0) {
                    mToast.show(getResources().getString(R.string.bind_input_proving), Toast.LENGTH_SHORT);
                    return;
                }
                loadProgressDialog(getResources().getString(R.string.bind_loading_ask));

                unregisterKcBroadcast();
                IntentFilter filter = new IntentFilter();
                filter.addAction(VsUserConfig.VS_ACTION_CHANGE_PHONE_TWO);
                vsBroadcastReceiver = new KcBroadcastReceiver();
                registerReceiver(vsBroadcastReceiver, filter);
                // 发送请求
                TreeMap<String, String> treeMap = new TreeMap<String, String>();
                treeMap.put("code", validatecode);
                treeMap.put("phone", newphonenumber);

                CoreBusiness.getInstance().startThread(mContext, "/user/bind_phone", DfineAction.authType_UID, treeMap, VsUserConfig.VS_ACTION_CHANGE_PHONE_TWO);                          
                break;
            default:
                break;
            }
        }
    };

    /**
     * 绑定手机第一步 得到验证码
     */
    private class mRebind1Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            newphonenumber = edit_new_phone.getText().toString().replaceAll(" ", "");
            System.out.println("pne==" + newphonenumber);

            if (VsUtil.isNull(newphonenumber)) {
                mToast.show(getResources().getString(R.string.input_wrong_phone_1), Toast.LENGTH_SHORT);
                return;
            }
            if (newphonenumber.length() > 0 && newphonenumber.length() < 11) { 
                mToast.show(getResources().getString(R.string.input_wrong_phone), Toast.LENGTH_SHORT);
                return;
            }
            loadProgressDialog(getResources().getString(R.string.bind_loading));

            String pswmd5 = VsMd5.md5(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Password));

            unregisterKcBroadcast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(VsUserConfig.VS_ACTION_CHANGE_PHONE);
            vsBroadcastReceiver = new KcBroadcastReceiver();
            registerReceiver(vsBroadcastReceiver, filter);
            // 发送请求
            TreeMap<String, String> treeMap = new TreeMap<String, String>();
            treeMap.put("passwd", pswmd5);
            treeMap.put("new_phone", newphonenumber);

            CoreBusiness.getInstance().startThread(mContext, "/user/bind_req", DfineAction.authType_UID, treeMap, VsUserConfig.VS_ACTION_CHANGE_PHONE);
        }
    };

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        super.handleKcBroadcast(context, intent);
        String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        JSONObject jData;
        try {
            jData = new JSONObject(jStr);
            String retStr = jData.getString("result");
            if (retStr.equals("0")) {
                if (intent.getAction().equals(VsUserConfig.VS_ACTION_CHANGE_PHONE_TWO)) {// 判断是否是绑定返回信息
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, newphonenumber);
                    mOldBindPhoneTextView.setText(getResources().getString(R.string.bind_old_phonenumber) + newphonenumber);
                    bundle.putString("msg", getResources().getString(R.string.bind_success));
                    bundle.putBoolean("bindsuc", true);
                    mContext.sendBroadcast(new Intent(DfineAction.ACTION_REGSENDMONEY));
                }
                else if (intent.getAction().equals(VsUserConfig.VS_ACTION_CHANGE_PHONE)) {
                    bundle.putString("msg", getResources().getString(R.string.bind_get_success));
                    bundle.putBoolean("refresh", true);
                }
                countPercent = 30;
                btn_get_code.setClickable(false);
                btn_get_code.setBackgroundResource(R.drawable.vs_green_btn_selecter);
                // btn_get_code.setPadding(8, 0, 8, 0);
                mBaseHandler.sendEmptyMessageDelayed(MSG_ID_Show_COUNT_Message, 1 * 1000);
            }
            /*else if (retStr.equals("33")) {//该手机号已经绑定过
                mToast.show(mContext.getString(R.string.bind_input_has_bind), Toast.LENGTH_LONG);
                return;
            }*/
            else {
                if (retStr.equals("-99")) {
                    dismissProgressDialog();
                    if (!VsUtil.isCurrentNetworkAvailable(mContext))
                        return;
                }
                String wrongResult = jData.getString("reason");
                if (wrongResult != null) {
                    msgString = wrongResult.toString();
                }
                bundle.putString("msg", msgString);
                message.what = MSG_ID_Show_Fail_Message;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            if (intent.getAction().equals(VsUserConfig.VS_ACTION_CHANGE_PHONE)) {// 判断是否是绑定返回信息
                bundle.putString("msg", getResources().getString(R.string.bind_fail));
            }
            else if (intent.getAction().equals(VsUserConfig.VS_ACTION_CHANGE_PHONE_TWO)) {
                bundle.putString("msg", getResources().getString(R.string.bind_fail));
            }
            message.what = MSG_ID_Show_Fail_Message;
        }
        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
        case MSG_ID_Show_Succeed_Message:
            dismissProgressDialog();
            if (msg.getData().getBoolean("refresh")) {
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
            }
            else if (msg.getData().getBoolean("bindsuc")) {

                if ("loginBind".equals(comeflag)) {
                    mToast.show("绑定成功，您现在可以拨打电话了！", Toast.LENGTH_SHORT);
                    Intent intent3 = new Intent(mContext, VsMainActivity.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                    VsUserConfig.setData(this, VsUserConfig.JKEY_TOKEN, null);
                    VsBizUtil.getInstance().getToken(this);

                    finish();

                }
                else if ("settingBind".equals(comeflag)) {
                    mToast.show("改绑手机号成功，您现在可以拨打电话了！", Toast.LENGTH_SHORT);
                    VsUserConfig.setData(this, VsUserConfig.JKEY_TOKEN, null);
                    VsBizUtil.getInstance().getToken(this);
                    //  GlobalVariables.RE_CONNECT_FLAG = true;

                    // VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, newphonenumber);
                    finish();
                }
                else if ("callBind".equals(comeflag)) {
                    mToast.show("绑定成功，您现在可以拨打电话了！", Toast.LENGTH_SHORT);
                    VsUserConfig.setData(this, VsUserConfig.JKEY_TOKEN, null);
                    VsBizUtil.getInstance().getToken(this);
                    //  GlobalVariables.RE_CONNECT_FLAG = true;

                    finish();

                }

            }
            break;
        case MSG_ID_Show_Fail_Message:
            dismissProgressDialog();
            mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
            break;
        case MSG_ID_Show_COUNT_Message:
            btn_get_code.setText(Html.fromHtml("<font color='##FFFFFF'>" + "重新获取" + "</font>" + "<font color='#F6FF00'>" + countPercent + "s" + "</font>"));

            countPercent--;
            if (countPercent >= 0) {
                mBaseHandler.sendEmptyMessageDelayed(MSG_ID_Show_COUNT_Message, 1 * 1000);
            }
            else {
                btn_get_code.setClickable(true);
                btn_get_code.setText("重新获取");
                //  btn_get_code.setBackgroundResource(R.drawable.kc_button_com_bg);
                // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                // LinearLayout.LayoutParams.WRAP_CONTENT);
                // btn_get_code.setPadding(8, 0, 8, 0);

                mBaseHandler.removeMessages(MSG_ID_Show_COUNT_Message);
            }
        case VsUserConfig.MSG_ID_GET_MSG_SUCCESS://自动填充验证码
        	String code = msg.getData().getString("code");
			if (code != null && !"".equals(code) && code.length() == 4) {
        	edit_vorification_code.setText(code);
			}
        	break;
        default:
            break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vsBroadcastReceiver != null) {
            unregisterReceiver(vsBroadcastReceiver);
            vsBroadcastReceiver = null;
        }
        getContentResolver().unregisterContentObserver(receiverNote); 
    }

    protected void HandleLeftNavBtn() {
        // TODO 处理左导航按钮事件

        if ("loginBind".equals(comeflag)) {
            clear(VsChangePhoneActivity.this);
            this.finish();// 默认为后退  

        }
        else {
            this.finish();// 默认为后退  

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if ("loginBind".equals(comeflag)) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        clear(VsChangePhoneActivity.this);
                    }
                }).start();
                ;

            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
    * @Title: clear 
    * @Description: 清除信息
    * @param @param mContext    设定文件 
    * @return void    返回类型 
    * @throws
     */
    public void clear(Context mContext) {
        VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, true);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_RegAwardSwitch, true);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_VipValidtime, "");
        // 清空Token
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETVSUSERINFO, "");
     
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN_RESULT, "");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_CALLSERVER_FLAG, false);
        //注销
//        UCSService.uninit();       
        // 关闭通话服务
        //   stopService(new Intent(mContext, ConnectionService.class));  

    }
}
