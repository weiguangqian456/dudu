package com.weiwei.base.activity.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsRc4;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.TreeMap;

/**
 * @Title:Android客户端
 * @Description: 忘记密码、手机号码确认界面
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsSetPhoneActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 手机号码输入框
     */
    private EditText vs_set_phone_edit;
    /**
     * 一键删除
     */
    private ImageView vs_set_phone_eidt_del;
    /**
     * 下一步按钮
     */
    private RoundTextView vs_set_phone_next_btn;
    /**
     * 输入内容长度
     */
    private int oldLength = 0;
    /**
     * 是否为删除
     */
    private boolean flag = false;
    /**
     * 电话号码
     */
    private String phoneNumber = null;
    private String endphoneNumber = null;
    /**
     * 获取验证码成功
     */
    private final char MSG_ID_GET_VERIFY_SUCC = 301;
    /**
     * 修改密码成功
     */
    private final char MSG_ID_SET_PASSWORD_SUCC = 303;
    /**
     * 获取验证码失败
     */
    private final char MSG_ID_Show_Fail_Message = 302;
    /**
     * 验证码
     */
    private EditText vs_edit_set_code;
    /**
     * 显示密码
     */
    private ImageView set_password_show_btn;
    /**
     * 显示隐藏密码
     */
    private boolean pwd_show_hide = false;
    /**
     * 密码输入框
     */
    private EditText vs_set_passeord_edit;
    /**
     * 获取验证码
     */
    private Button vs_btn_set_code;
    private String set_code = null;
    private String setpassword = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_set_phone_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(R.string.vs_set_phone_title_hint1);
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
        vs_set_phone_edit = (EditText) findViewById(R.id.vs_set_phone_edit);
        vs_set_phone_eidt_del = (ImageView) findViewById(R.id.vs_set_phone_eidt_del);
        vs_edit_set_code = (EditText) findViewById(R.id.vs_edit_set_code);
        vs_btn_set_code = (Button) findViewById(R.id.vs_btn_set_code);
        vs_set_passeord_edit = (EditText) findViewById(R.id.vs_set_passeord_edit);
        set_password_show_btn = (ImageView) findViewById(R.id.set_password_show_btn);
        vs_set_phone_next_btn = (RoundTextView) findViewById(R.id.vs_set_phone_next_btn);
        // 设置监听事件
        vs_set_phone_eidt_del.setOnClickListener(this);
        vs_btn_set_code.setOnClickListener(this);
        set_password_show_btn.setOnClickListener(this);
        vs_set_phone_next_btn.setOnClickListener(this);
//		vs_set_phone_edit.addTextChangedListener(new SetPhoneWhatch());
        vs_edit_set_code.addTextChangedListener(new SetPhoneWhatch1());
        vs_set_passeord_edit.addTextChangedListener(new SetPhoneWhatch2());
        // 初始化内容
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        vs_set_phone_edit.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//		if (phone != null && !"".equals(phone)) {// 判断是否有手机号码
//			vs_set_phone_edit.setText(phone);
//			vs_set_phone_edit.setSelection(phone.length());
//		}
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_set_phone_eidt_del:// 一键删除
                vs_set_phone_edit.setText("");
                break;
            case R.id.set_password_show_btn:// 显示密码
                if (!pwd_show_hide) {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_yes);
                    vs_set_passeord_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    vs_set_passeord_edit.setSelection(vs_set_passeord_edit.getText().toString().length());
                    pwd_show_hide = true;
                } else {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_no);
                    vs_set_passeord_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    vs_set_passeord_edit.setSelection(vs_set_passeord_edit.getText().toString().length());
                    pwd_show_hide = false;
                }
                break;
            case R.id.vs_btn_set_code:
                phoneNumber = vs_set_phone_edit.getText().toString().trim();
                if (phoneNumber.trim().replaceAll("-", "").length() == 11) {
                    if (phoneNumber != null && !"".equals(phoneNumber)) {
                        endphoneNumber = phoneNumber.replaceAll("-", "");
                        if (VsUtil.checkPhone(endphoneNumber)) {// 检查手机是否符合规范
                            getRegCode(endphoneNumber);// 绑定广播并发送注册请求
                        } else {
                            mToast.show(getResources().getString(R.string.vs_phone_erro));
                            return;
                        }
                    } else {
                        mToast.show("手机号码不能为空！");
                        return;
                    }
                } else {
                    mToast.show(getResources().getString(R.string.vs_phone_erro));
                    return;
                }
                break;
            case R.id.vs_set_phone_next_btn:
                phoneNumber = vs_set_phone_edit.getText().toString().trim().replaceAll(" ", "").replaceAll("-", "");
                set_code = vs_edit_set_code.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
                setpassword = vs_set_passeord_edit.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
                if (phoneNumber != null && !"null".equalsIgnoreCase(phoneNumber) && !"".equalsIgnoreCase(phoneNumber)) {
                    if (phoneNumber.trim().replaceAll("-", "").length() == 11) {
                        if (set_code != null && !"".equals(set_code)) {
                            if (setpassword != null && !"".equals(setpassword)) {

                                setpassword(phoneNumber);// 绑定广播并发送注册请求
                            } else {
                                mToast.show(getResources().getString(R.string.vs_pwd_isnull_str));
                            }
                        } else {
                            mToast.show(getResources().getString(R.string.vs_code_isnull_str));
                        }
                    } else {
                        mToast.show(getResources().getString(R.string.vs_phone_erro));
                    }
                } else {
                    mToast.show(getResources().getString(R.string.vs_phone_toast_isnull), Toast.LENGTH_SHORT);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param phoneNumber
     */
    private void getRegCode(final String mPhoneNumber) {
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            mToast.show("网络连接失败，请检查网络");
            // return;

        }
        loadProgressDialog("请求提交中...");
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_RESETPWD_CODE);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送查询请求
        String deciceid = VsUtil.getMacAddress(mContext);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("action", "resetpwd");
        treeMap.put("way", "txt");
        treeMap.put("phone", mPhoneNumber);
        // 请求服务器
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GET_REG_CODE, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_RESETPWD_CODE);
    }

    /**
     * 重置密码
     *
     * @param phoneNumber
     */
    private void setpassword(final String mPhoneNumber) {
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            mToast.show("网络连接失败，请检查网络");
            // return;

        }
        loadProgressDialog("请求提交中...");
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_SET_PASSWORD);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送查询请求
        String deciceid = VsUtil.getMacAddress(mContext);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("password", VsRc4.encry_RC4_string(setpassword, DfineAction.passwad_key));
        treeMap.put("verify_code", set_code);
        treeMap.put("phone", mPhoneNumber);
        // 请求服务器
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.SET_NEW_PASSWORD, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_SET_PASSWORD);
    }

    /**
     * @Title:
     * @Description: 监听输入状态
     * @Copyright: Copyright (c) 2014
     * @author: 李志
     * @version: 1.0.0.0
     * @Date: 2014-8-27
     */
    class SetPhoneWhatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String textString = vs_set_phone_edit.getText().toString().trim();
            if (textString.length() > 0) {// 判断是否输入内容
                vs_set_phone_eidt_del.setVisibility(View.VISIBLE);
            } else {
                vs_set_phone_eidt_del.setVisibility(View.GONE);
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
            if (!flag) {
                if (textString.length() == 3 || textString.length() == 8) {
                    vs_set_phone_edit.append("-");
                }
            } else if (textString.length() == 3 || textString.length() == 8) {
                vs_set_phone_edit.setText(textString.subSequence(0, textString.length() - 1));
//				vs_set_phone_edit.setSelection(textString.length() - 1);
            }
        }

    }

    class SetPhoneWhatch1 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            set_code = vs_edit_set_code.getText().toString().trim();
        }
    }

    class SetPhoneWhatch2 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        super.handleKcBroadcast(context, intent);
        String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        JSONObject jData;
        String action = intent.getAction();

        if (action.equals(VsUserConfig.VS_ACTION_SET_PASSWORD)) {
            try {
                jData = new JSONObject(jStr);
                String code = jData.getString("code");
                if (code.equals("0")) {
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", getResources().getString(R.string.setpass_successsumbitmsg));
                    message.what = MSG_ID_SET_PASSWORD_SUCC;
                } else {
                    if (code.equals("-99")) {
                        dismissProgressDialog();
                        if (!VsNetWorkTools.isNetworkAvailable(mContext)) return;
                    }
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", jData.getString("msg"));
                    message.what = MSG_ID_Show_Fail_Message;
                }
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                bundle.putString("message", getResources().getString(R.string.register_fail_info));
                message.what = MSG_ID_Show_Fail_Message;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        } else if (action.equals(VsUserConfig.VS_ACTION_RESETPWD_CODE)) {
            try {
                jData = new JSONObject(jStr);
                String code = jData.getString("code");
                if (code.equals("0")) {
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", getResources().getString(R.string.register_successcode));
                    message.what = MSG_ID_GET_VERIFY_SUCC;
                } else {
                    if (code.equals("-99")) {
                        dismissProgressDialog();
                        if (!VsNetWorkTools.isNetworkAvailable(mContext)) return;
                    }
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", jData.getString("msg"));
                    message.what = MSG_ID_Show_Fail_Message;
                }
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                bundle.putString("message", getResources().getString(R.string.register_fail_code));
                message.what = MSG_ID_Show_Fail_Message;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        }
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        String title = msg.getData().getString("title");
        String message = msg.getData().getString("message");
        switch (msg.what) {
            case MSG_ID_GET_VERIFY_SUCC:
                dismissProgressDialog();
                showMessageDialog(title, message, false);
                break;
            case MSG_ID_SET_PASSWORD_SUCC:
                dismissProgressDialog();
                showMessageDialog_setpass(title, message, false);
                break;
            case MSG_ID_Show_Fail_Message:
                dismissProgressDialog();
                showMessageDialog(title, message, false);
                break;
            default:
                break;
        }
    }

    /**
     * 显示消息提示框
     *
     * @param title
     * @param message
     */

    protected void showMessageDialog_setpass(String title, String message, boolean nocanbtn) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
