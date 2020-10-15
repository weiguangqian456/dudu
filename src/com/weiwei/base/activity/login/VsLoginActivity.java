package com.weiwei.base.activity.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.setting.VsChangePhoneActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsRc4;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PreferencesUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.TreeMap;

import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_DEFAULT_PASSWORD;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TAGNAME;

/**
 * @Title:Android客户端
 * @Description: 登录
 */
public class VsLoginActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 电话号码输入框
     */
    private EditText vs_login_phone_edit;
    /**
     * 电话号码一键删除
     */
    private ImageView vs_login_phone_eidt_del;
    /**
     * 密码输入框
     */
    private EditText vs_longin_password_edit;
    /**
     * 显示密码
     */
    private ImageView set_password_show_btn;
    /**
     * 忘记密码
     */
    private TextView vs_login_reset_password;
    private TextView vs_register_reset_password;
    /**
     * 登录
     */
    private RoundTextView vs_login_btn;
    /**
     * 显示隐藏密码
     */
    private boolean pwd_show_hide = false;
    /**
     * 输入内容长度
     */
    private int oldLength = 0;
    /**
     * 是否为删除
     */
    private boolean flag = false;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 失败
     */
    private final char MSG_ID_Show_Fail_Message = 200;
    /**
     * 成功
     */
    private final char MSG_ID_Show_Succeed_Message = 201;

    private String phoneNuber = "";

    private static final String TAG = "VsLoginActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("phoneNuber")) {
            phoneNuber = getIntent().getStringExtra("phoneNuber");
        }
        setContentView(R.layout.vs_login_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(R.string.vs_login_btn_hint);
        showLeftNavaBtn(R.drawable.icon_back);
        initView();
        VsApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        vs_login_phone_edit = (EditText) findViewById(R.id.vs_login_phone_edit);
        vs_login_phone_eidt_del = (ImageView) findViewById(R.id.vs_login_phone_eidt_del);
        vs_longin_password_edit = (EditText) findViewById(R.id.vs_longin_password_edit);
        set_password_show_btn = (ImageView) findViewById(R.id.set_password_show_btn);
        vs_login_reset_password = (TextView) findViewById(R.id.vs_login_reset_password);
        vs_register_reset_password = (TextView) findViewById(R.id.vs_register_reset_password);
        vs_login_btn = (RoundTextView) findViewById(R.id.vs_login_btn);
        // 设置监听事件
        vs_login_phone_eidt_del.setOnClickListener(this);
        set_password_show_btn.setOnClickListener(this);
        vs_login_reset_password.setOnClickListener(this);
        vs_register_reset_password.setOnClickListener(this);
        vs_login_btn.setOnClickListener(this);
        vs_login_phone_edit.addTextChangedListener(new MyTextWatcher(vs_login_phone_edit));
        vs_longin_password_edit.addTextChangedListener(new MyTextWatcher(vs_longin_password_edit));
        // 初始化数据
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String savePhone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        try {
            if (phone != null && !"".equals(phone)) {
                vs_login_phone_edit.setText(phone);
                vs_login_phone_edit.setSelection(phone.length());
                vs_longin_password_edit.requestFocus();
            } else if (savePhone != null && !"".equals(savePhone)) {// 填充用户手机号
                String formatPhone = VsUtil.formatPhoneNumber(savePhone);
                vs_login_phone_edit.setText(formatPhone);
                vs_login_phone_edit.setSelection(formatPhone.length());
                vs_longin_password_edit.requestFocus();
            } else {
                // 获取手机号码
                String tempPhoneNumber = VsUtil.getPhoneNumber(mContext);
                if (tempPhoneNumber != null && !"".equals(tempPhoneNumber)) {
                    tempPhoneNumber = VsUtil.formatPhoneNumber(tempPhoneNumber);
                    vs_login_phone_edit.setText(tempPhoneNumber);
                    vs_login_phone_edit.setSelection(tempPhoneNumber.length());
                    vs_longin_password_edit.requestFocus();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
        if (!VsUtil.isNull(phoneNuber)) {
            vs_login_phone_edit.setText(phoneNuber);
            vs_longin_password_edit.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_login_phone_eidt_del:// 一键删除电话号码
                vs_login_phone_edit.setText("");
                vs_login_phone_edit.requestFocus();
                break;
            case R.id.set_password_show_btn:// 显示密码
                if (!pwd_show_hide) {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_yes);
                    vs_longin_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    vs_longin_password_edit.setSelection(vs_longin_password_edit.getText().toString().length());
                    pwd_show_hide = true;
                } else {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_no);
                    vs_longin_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    vs_longin_password_edit.setSelection(vs_longin_password_edit.getText().toString().length());
                    pwd_show_hide = false;
                }
                break;
            case R.id.vs_login_reset_password:// 忘记密码
                String phone = vs_login_phone_edit.getText().toString().trim();
                Intent intent = new Intent(mContext, VsSetPhoneActivity.class);
                if (phone != null && !"".equals(phone)) {
                    intent.putExtra("phone", phone);
                }
                // 进入重置密码界面
                startActivity(intent);
                break;
            case R.id.vs_register_reset_password:// 免费注册
                Intent intent2 = new Intent(mContext, VsRegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.vs_login_btn:// 登录
                // 判断网络连接是否正常
                if (!VsNetWorkTools.isNetworkAvailable(this)) {
                    showYesNoDialog("提示", "网络连接异常，请检查您的网络是否连接！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }, null);
                } else {
                    String mPhoneNumber = vs_login_phone_edit.getText().toString().trim().replaceAll(" ", "").replaceAll("-", "");
                    pwd = vs_longin_password_edit.getText().toString();
                    //if (mPhoneNumber.length() == 11) {
                    //if (VsUtil.checkPhone(mPhoneNumber)) {
                    if (mPhoneNumber != null && !"null".equalsIgnoreCase(mPhoneNumber) && !"".equalsIgnoreCase(mPhoneNumber)) {
                        if (pwd != null && !"".equals(pwd) && pwd.length() >= 6) {
                            // vs_login_phone_edit.setEnabled(false);
                            // vs_longin_password_edit.setEnabled(false);
                            //  vs_login_btn.setEnabled(false);
                            // 登录
                            login(mPhoneNumber, pwd);
                        } else if (pwd == null || "".equals(pwd)) {
                            mToast.show(getResources().getString(R.string.vs_pwd_isnull_str));
                        } else if (pwd.length() < 6) {
                            mToast.show(getResources().getString(R.string.vs_pwd_xy6_str));
                        }
                    } else {
                        //mToast.show(getResources().getString(R.string.vs_phone_erro), Toast.LENGTH_SHORT);
                        mToast.show(getResources().getString(R.string.vs_phone_erro_isnull), Toast.LENGTH_SHORT);
                    }
                    //} else {
                    //  mToast.show(getResources().getString(R.string.vs_phone_erro), Toast.LENGTH_SHORT);
                    //}
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     *
     * @param acount
     * @param pwd
     */
    private void login(String acount, String pwd) {
        loadProgressDialog("登录中，请稍候......");
        loginAccount();
        String netType = VsUtil.getNetTypeString();
        String deciceid = VsUtil.getMacAddress(mContext);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        if (acount.indexOf("+86") == 0) {
            acount = acount.substring(3, acount.length());
        }
        treeMap.put("account", acount);
        treeMap.put("password", VsRc4.encry_RC4_string(pwd, DfineAction.passwad_key));
        treeMap.put("device_id", deciceid.toUpperCase());
        treeMap.put("net_mode", netType);
        treeMap.put("device_type", android.os.Build.MODEL);
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INRFACE_LOGIN, DfineAction.authType_Key, treeMap, VsCoreService.VS_ACTION_LOGIN);
        // KcHttpsClient.GetHttps(mContext, bizparams);
    }

    /**
     * 发送登录账号广播
     *
     * @param
     */
    private void loginAccount() {
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsCoreService.VS_ACTION_LOGIN);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
    }

    /**
     * 文本输入框内容状态变化监听事件
     *
     * @author 9lz3r12
     */
    class MyTextWatcher implements TextWatcher {
        /**
         * 输入框
         */
        private EditText view;

        public MyTextWatcher(EditText view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.vs_login_phone_edit:// 手机号
                    String textString = vs_login_phone_edit.getText().toString().trim();
                    if (textString.length() > 0) {// 判断是否输入内容
                        vs_login_phone_eidt_del.setVisibility(View.VISIBLE);
                    } else {
                        vs_login_phone_eidt_del.setVisibility(View.GONE);
                    }
                    if (oldLength == 0) {
                        oldLength = textString.length();
                    } else {
                        if ((oldLength > textString.length())) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                        oldLength = textString.length();
                    }
                    //              if (!flag) {
                    //                  if (textString.length() == 3 || textString.length() == 8) {
                    //                      vs_login_phone_edit.append("-");
                    //                  }
                    //              } else if (textString.length() == 3 || textString.length() == 8) {
                    //                  vs_login_phone_edit.setText(textString.subSequence(0, textString.length() - 1));
                    //                  vs_login_phone_edit.setSelection(textString.length() - 1);
                    //              }
                    break;
                default:
                    break;
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.handleKcBroadcast(context, intent);
        String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        JSONObject jData;
        String action = intent.getAction();
        if (VsCoreService.VS_ACTION_LOGIN.equals(action)) {
            //CustomLog.i("lte", "直接登录返回结果是:" + jStr);
            // 关闭进度栏
            try {
                jData = new JSONObject(jStr);
                CustomLog.i("logintesst", "json===" + jData + "");
                String code = jData.getString("code");
                if (code.equals("0")) {
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, false);
                    JSONObject getjDat = jData.getJSONObject("data");
                    String kcId = getjDat.getString("uid");

                    registerToEase(kcId);   //注册到环信
                    int level = getjDat.getInt("level");
                    String levelName = getjDat.getString("levelName");
                    Log.e(TAG, "当前用户等级 ==" + level + "====" + levelName);

                    PreferencesUtils.putInt(VsApplication.getContext(), VsUserConfig.JKey_MyInfo_Level, level);
                    if (!StringUtils.isEmpty(levelName)) PreferencesUtils.putString(VsApplication.getContext(), VsUserConfig.JKey_MyInfo_LevelName, levelName);

                    VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, kcId);
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, pwd);
                    VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, getjDat.getString("phone"));
                    String token = getjDat.getString("token");
                    VsUserConfig.setData(context, VsUserConfig.JKey_LoginToken, getjDat.getString("token"));
                    Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
                    intent2.putExtra("packname", mContext.getPackageName());
                    intent2.setPackage(mContext.getPackageName());
                    sendBroadcast(intent2);

                    //bundle.putString("msg", "登录成功！您现在就可以拨打电话啦！");
                  /*  message.what = MSG_ID_Show_Succeed_Message;
                    message.setData(bundle);*/
                    mBaseHandler.sendEmptyMessageDelayed(MSG_ID_Show_Succeed_Message, 1);
                    return;
                } else {
                    dismissProgressDialog();
                    bundle.putString("msg", jData.getString("msg"));
                    message.what = MSG_ID_Show_Fail_Message;
                    message.setData(bundle);
                    mBaseHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                dismissProgressDialog();
                bundle.putString("msg", "登录失败，请稍后再试！");
                message.what = MSG_ID_Show_Fail_Message;
                message.setData(bundle);
                mBaseHandler.sendMessage(message);
            } finally {
            }
        }
    }

    private void registerToEase(String uid) {
        Log.e(TAG, "uid===》" + uid);
        ChatClient.getInstance().register(uid, EASEMOB_DEFAULT_PASSWORD, new Callback() {
            @Override
            public void onSuccess() {
                Log.e(EASEMOB_TAGNAME, "demo register success");
            }

            @Override
            public void onError(int i, String s) {
                Log.e(EASEMOB_TAGNAME, "demo register fail===>" + s + "errorCode===>" + i);
            }

            @Override
            public void onProgress(int i, String s) {
                Log.e(EASEMOB_TAGNAME, "demo register progress===>" + s + "errorCode===>" + i);
            }
        });
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_Show_Succeed_Message:
                if (!VsUtil.isNull(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber))) {// 有绑定手机
                    dismissProgressDialog();
                    mToast.show("登录成功！您现在就可以拨打电话啦！", Toast.LENGTH_LONG);
 //                   Intent intent3 = new Intent(mContext, VsMainActivity.class);
               /*  intent3.putExtra("msg1", jData.getString("msg1"));
                   intent3.putExtra("firstreg", jData.getString("firstreg"));
                   intent3.putExtra("check", jData.getString("check"));*/
 //                   intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 //                   startActivity(intent3);
                    finish();
                    break;
                } else {
                    dismissProgressDialog();
                    Intent intent = new Intent(mContext, VsChangePhoneActivity.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("comeflag", "loginBind");
                    startActivity(intent);
                }
                // mBaseHandler.sendEmptyMessageDelayed(MSG_ID_Show_Succeed_Message_go, 2000);
                break;
            case MSG_ID_Show_Fail_Message:// 登录失败
                // 设置控件可用
                vs_login_phone_edit.setEnabled(true);
                vs_longin_password_edit.setEnabled(true);
                vs_login_btn.setEnabled(true);
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
