package com.weiwei.base.activity.setting;

import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsRc4;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @instruction 修改密码
 * @author 黄发兴
 * @version 创建时间：2014-12-9上午10:34:20
 */
public class VsChangePwdActivity extends VsBaseActivity implements OnClickListener {
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
    /**
     * 登录
     */
    private Button vs_login_btn;
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
    private final char MSG_ID_FAIL = 200;
    /**
     * 成功
     */
    private final char MSG_ID_SUCC = 201;
    /**
     * 保存的密码
     */
    private String savePwd = null;
    private TextView vs_register_reset_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_login_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(R.string.vs_change_pwd_title);
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
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
        vs_login_btn = (Button) findViewById(R.id.vs_login_btn);
        vs_login_reset_password.setVisibility(View.GONE);
        vs_register_reset_password.setVisibility(View.GONE);
        vs_login_btn.setText(R.string.vs_ok);
        vs_login_phone_edit.setHint(R.string.vs_change_pwd_edit_hint1);
        vs_login_phone_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        vs_login_phone_edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
        vs_longin_password_edit.setHint(R.string.vs_change_pwd_edit_hint2);

        // 设置监听事件
        vs_login_phone_eidt_del.setOnClickListener(this);
        set_password_show_btn.setOnClickListener(this);
        vs_login_reset_password.setOnClickListener(this);
        vs_login_btn.setOnClickListener(this);
        vs_login_phone_edit.addTextChangedListener(new MyTextWatcher(vs_login_phone_edit));
        vs_longin_password_edit.addTextChangedListener(new MyTextWatcher(vs_longin_password_edit));

    }

    /**
     * 文本输入框内容状态变化监听事件
     * 
     * @author 9lz3r12
     * 
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
                }
                else {
                    vs_login_phone_eidt_del.setVisibility(View.GONE);
                }
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.vs_login_btn:
            String oldpwd = vs_login_phone_edit.getText().toString().trim();
            String newpwd = vs_longin_password_edit.getText().toString().trim();
            if (oldpwd != null && !"".equals(oldpwd) && oldpwd.length() > 5) {
                if (newpwd != null && !"".equals(newpwd) && oldpwd.length() > 5) {
                    if (!oldpwd.equals(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Password))) {
                        mToast.show("原密码错误");
                    }
                    else {
                        confirmPwd(oldpwd, newpwd);// 提交修改密码  
                    }

                }
                else if (newpwd == null || "".equals(newpwd)) {
                    mToast.show("新" + getResources().getString(R.string.vs_pwd_isnull_str));
                }
                else if (newpwd.length() < 6) {
                    mToast.show("新" + getResources().getString(R.string.vs_pwd_xy6_str));
                }

            }
            else if (oldpwd == null || "".equals(oldpwd)) {
                mToast.show("原" + getResources().getString(R.string.vs_pwd_isnull_str));
            }
            else if (oldpwd.length() < 6) {
                mToast.show("原" + getResources().getString(R.string.vs_pwd_xy6_str));
            }
            break;
        case R.id.vs_login_phone_eidt_del:// 一键删除电话号码
            vs_login_phone_edit.setText("");
            break;
        case R.id.set_password_show_btn:// 显示密码
            if (!pwd_show_hide) {
                set_password_show_btn.setImageResource(R.drawable.vs_checked_yes);
                vs_longin_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                vs_longin_password_edit.setSelection(vs_longin_password_edit.getText().toString().length());
                vs_login_phone_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                vs_login_phone_edit.setSelection(vs_login_phone_edit.getText().toString().length());
                pwd_show_hide = true;
            }
            else {
                set_password_show_btn.setImageResource(R.drawable.vs_checked_no);
                vs_longin_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                vs_longin_password_edit.setSelection(vs_longin_password_edit.getText().toString().length());
                vs_login_phone_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                vs_login_phone_edit.setSelection(vs_login_phone_edit.getText().toString().length());
                pwd_show_hide = false;
            }

        default:
            break;
        }
    }

    /**
     * 提交请求修改密码
     */
    private void confirmPwd(String oldPwd, String newPwd) {
        loadProgressDialog("请求提交中...");
        savePwd = newPwd;
        // 绑定广播接收器
        unregisterKcBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_CHANGE_PWD);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送请求
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("old_passwd", VsRc4.encry_RC4_string(oldPwd, DfineAction.passwad_key));
        treeMap.put("new_passwd", VsRc4.encry_RC4_string(newPwd, DfineAction.passwad_key));
        // 发送修改密码请求
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INRFACE_CHANGE_PWD, DfineAction.authType_UID, treeMap, VsUserConfig.VS_ACTION_CHANGE_PWD);
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.handleKcBroadcast(context, intent);
        dismissProgressDialog();
        String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        JSONObject jData;
        String action = intent.getAction();
        if (VsUserConfig.VS_ACTION_CHANGE_PWD.equals(action)) {
            try {
                jData = new JSONObject(jStr);
                String retStr = jData.getString("result");
                if (retStr.equals("0")) {
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, savePwd);
                    message.what = MSG_ID_SUCC;
                }
                else {
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_FAIL;
                }
            }
            catch (Exception e) {
                bundle.putString("msg", "登录失败，请稍后再试！");
                message.what = MSG_ID_FAIL;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        }
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
        case MSG_ID_SUCC:
            mToast.show(getResources().getString(R.string.vs_change_pwd_succ_str));
            this.finish();
            break;
        case MSG_ID_FAIL:
            mToast.show(msg.getData().getString("msg"));
            break;

        default:
            break;
        }
    }

}
