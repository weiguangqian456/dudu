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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.weiwei.base.util.MyButton;
import com.weiwei.base.util.MyButton.CountDownOnClickListener;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.zbar.lib.CaptureActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.TreeMap;

/**
 * @Title:Android客户端
 * @Description:注册、新老用户检测
 * @Date: 2014-9-23
 */
public class VsRegisterActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 一键删除
     */
    private ImageView vs_set_register_eidt_del;
    /**
     * 显示密码
     */
    private ImageView set_password_show_btn;
    /**
     * 一键删除
     */
    private ImageView vs_set_register_eidt_del_invide;
    /**
     * 手机号码
     */
    private EditText vs_register_edit;
    /**
     * 验证码
     */
    private EditText vs_edit_reg_code;
    /**
     * 密码
     */
    private EditText vs_passeord_edit;
    /**
     * 手机号码
     */
    private EditText vs_register_edit_invide;
    private RelativeLayout vs_set_register_eidt_invide_lay;
    private TextView vs_regit_txt;
    /**
     * 下一步
     */
    private MyButton vs_register_next_btn;
    /**
     * 获取验证码
     */
    private Button vs_btn_reg_code;
    /**
     * 服务条款
     */
    private TextView vs_register_server;
    /**
     * 输入内容长度
     */
    private int oldLength = 0;
    /**
     * 是否为删除
     */
    private boolean flag = false;
    /**
     * 登录失败
     */
    private final char MSG_ID_Show_Fail_Message = 22;
    /**
     * 手机已经注册过
     */
    private final char MSG_ID_SHOW_PHONENUMBER_HAVE_REGISTER = 23;
    /**
     * 手机号码
     */
    private String phone = null;
    /**
     * 手机号码
     */
    private String phoneNumber = null;
    private String verify_code = null;
    private String regpassword = null;
    /**
     * 获取验证码成功
     */
    private final char MSG_ID_GET_VERIFY_SUCC = 25;

    // TODO 注册成功
    private final char MSG_ID_Show_Succeed_Message = 0;
    // TODO 获取验证码成功
    private final char MSG_ID_Get_Code_Succeed = 5;

    // TODO 开始注册
    private final char MSG_ID_Show_reg_Message = 1;

    private int chose_img = 1;
    private String flag_invit = "";
    /**
     * 显示隐藏密码
     */
    private boolean pwd_show_hide = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_register_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(R.string.vs_reghist_title_hint);
        showLeftNavaBtn(R.drawable.icon_back);
        initView();

        VsApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String code_account = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Code_Account);
        if ((code_account != null) && (code_account.length() > 0)) {
            vs_register_edit_invide.setText(code_account);
        }
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
        vs_set_register_eidt_del = (ImageView) findViewById(R.id.vs_set_register_eidt_del);
        vs_set_register_eidt_del_invide = (ImageView) findViewById(R.id.vs_set_register_eidt_del_invide);
        vs_register_edit = (EditText) findViewById(R.id.vs_register_edit);
        vs_edit_reg_code = (EditText) findViewById(R.id.vs_edit_reg_code);
        vs_passeord_edit = (EditText) findViewById(R.id.vs_passeord_edit);
        set_password_show_btn = (ImageView) findViewById(R.id.set_password_show_btn);
        vs_btn_reg_code = (Button) findViewById(R.id.vs_btn_reg_code);
        vs_register_edit_invide = (EditText) findViewById(R.id.vs_register_edit_invide);
        vs_register_next_btn = (MyButton) findViewById(R.id.vs_register_next_btn);
        vs_register_server = (TextView) findViewById(R.id.vs_register_server);
        vs_set_register_eidt_invide_lay = (RelativeLayout) findViewById(R.id.vs_set_register_eidt_invide_lay);
        vs_regit_txt = (TextView) findViewById(R.id.vs_regit_txt);
        flag_invit = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Single_Init, "");
        if ("y".equals(flag_invit)) {
            vs_set_register_eidt_invide_lay.setVisibility(View.VISIBLE);
            vs_regit_txt.setVisibility(View.GONE);
        } else if ("n".equals(flag_invit)) {
            vs_set_register_eidt_invide_lay.setVisibility(View.VISIBLE);
            vs_regit_txt.setVisibility(View.GONE);
        } else {
            vs_set_register_eidt_invide_lay.setVisibility(View.GONE);
            vs_regit_txt.setVisibility(View.GONE);
        }
        // 设置监听事件
        vs_set_register_eidt_del.setOnClickListener(this);
        vs_set_register_eidt_del_invide.setOnClickListener(this);
        set_password_show_btn.setOnClickListener(this);
//		vs_register_next_btn.setOnClickListener(this);
        vs_register_server.setOnClickListener(this);
        vs_btn_reg_code.setOnClickListener(this);
        vs_register_edit.addTextChangedListener(new SetPhoneWhatch());
        vs_register_edit_invide.addTextChangedListener(new SetPhoneWhatch2());
        vs_edit_reg_code.addTextChangedListener(new SetPhoneWhatch3());
        vs_passeord_edit.addTextChangedListener(new SetPhoneWhatch4());
        vs_register_next_btn.setCountDownOnClickListener(new CountDownOnClickListener() {

            @Override
            public void onClickListener() {
                phone = vs_register_edit.getText().toString().trim().replaceAll(" ", "").replaceAll("-", "");
                verify_code = vs_edit_reg_code.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
                String phoneInvide = vs_register_edit_invide.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_INVITED_BY, phoneInvide);
                regpassword = vs_passeord_edit.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
                if (phone != null && !"null".equalsIgnoreCase(phone) && !"".equalsIgnoreCase(phone)) {
                    if (phone.trim().replaceAll("-", "").length() == 11) {
                        if (verify_code != null && !"".equals(verify_code)) {
                            if (regpassword != null && !"".equals(regpassword)) {
                                if ("y".equals(flag_invit)) {
                                    String textString = vs_register_edit_invide.getText().toString().trim().replaceAll("-", "");
                                    if (!StringUtils.isEmpty(textString)) {
                                        vs_register_next_btn.startCountDown("提交中", "确定");
                                        checkUserAccount(phone);// 绑定广播并发送注册请求
                                    } else {
                                        //mToast.show("请输入正确的邀请人手机号码");
                                        mToast.show("请输入邀请码");
                                    }
                                } else {
                                    vs_register_next_btn.startCountDown("提交中", "确定");
                                    checkUserAccount(phone);// 绑定广播并发送注册请求
                                }

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
                    //mToast.show(getResources().getString(R.string.vs_phone_erro), Toast.LENGTH_SHORT);
                    mToast.show(getResources().getString(R.string.vs_phone_toast_isnull), Toast.LENGTH_SHORT);
                }

            }
        });
        try {
            // 获取手机号码
            String tempPhoneNumber = VsUtil.getPhoneNumber(mContext);
            if (tempPhoneNumber != null && !"".equals(tempPhoneNumber)) {
                tempPhoneNumber = VsUtil.formatPhoneNumber(tempPhoneNumber);
                vs_register_edit.setText(tempPhoneNumber);
                vs_register_edit.setSelection(tempPhoneNumber.trim().length());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_register_server:// 服务条款
                final String urlTo = "file:///android_asset/shop_service_terms.html";
                Intent intent = new Intent();
                intent.setClass(mContext, WeiboShareWebViewActivity.class);
                String[] aboutBusiness = new String[]{mContext.getString(R.string.welcome_main_elecdeal), "service", urlTo};
                intent.putExtra("AboutBusiness", aboutBusiness);
                startActivity(intent);
                // VsUtil.startActivity("301997", mContext, null);
                break;
            case R.id.set_password_show_btn:// 显示密码
                if (!pwd_show_hide) {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_yes);
                    vs_passeord_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    vs_passeord_edit.setSelection(vs_passeord_edit.getText().toString().length());
                    pwd_show_hide = true;
                } else {
                    set_password_show_btn.setImageResource(R.drawable.vs_checked_no);
                    vs_passeord_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    vs_passeord_edit.setSelection(vs_passeord_edit.getText().toString().length());
                    pwd_show_hide = false;
                }
                break;
            case R.id.vs_btn_reg_code:// 获取验证码

                phone = vs_register_edit.getText().toString().trim();
                if (phone.trim().replaceAll("-", "").length() == 11) {
                    if (phone != null && !"".equals(phone)) {
                        phoneNumber = phone.replaceAll("-", "");
                        if (VsUtil.checkPhone(phoneNumber)) {// 检查手机是否符合规范
                            getRegCode(phoneNumber);// 绑定广播并发送注册请求
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
            case R.id.vs_register_next_btn:// 下一步

//			phone = vs_register_edit.getText().toString().trim().replaceAll(" ", "").replaceAll("-", "");
//			verify_code = vs_edit_reg_code.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
//			String phoneInvide = vs_register_edit_invide.getText().toString()
//					.trim().replaceAll("-", "").replaceAll(" ", "");
//			VsUserConfig.setData(mContext, VsUserConfig.JKEY_INVITED_BY,
//					phoneInvide);
//			regpassword = vs_passeord_edit.getText().toString().trim().replaceAll("-", "").replaceAll(" ", "");
//              if (phone != null && !"null".equalsIgnoreCase(phone) && !"".equalsIgnoreCase(phone)) {
//            	  if (phone.trim().replaceAll("-", "").length() == 11) {
//                  if (verify_code != null && !"".equals(verify_code)) {
//                      if (regpassword != null && !"".equals(regpassword)) {
//                    	  if ("y".equals(flag_invit)) {
//                    		  String textString = vs_register_edit_invide.getText().toString().trim().replaceAll("-", "");
//                    		  if ((textString != null&&textString.length()==11)||"888888".equals(textString)) {
//		                          checkUserAccount(phone);// 绑定广播并发送注册请求
//                    			  
//							}else {
//								mToast.show("请输入正确的邀请人手机号码");
//							}
//						}else {
//	                          checkUserAccount(phone);// 绑定广播并发送注册请求
//						}
//                          
//                      } else  {
//                          mToast.show(getResources().getString(R.string.vs_pwd_isnull_str));
//                      } 
//                  } else  {
//                      mToast.show(getResources().getString(R.string.vs_code_isnull_str));
//                  } }else{
//                	  mToast.show(getResources().getString(R.string.vs_phone_erro));
//                  }
//              } else {
//                  //mToast.show(getResources().getString(R.string.vs_phone_erro), Toast.LENGTH_SHORT);
//                  mToast.show(getResources().getString(R.string.vs_phone_toast_isnull), Toast.LENGTH_SHORT);
//              }


                break;
            case R.id.vs_set_register_eidt_del:// 一键删除
                vs_register_edit.setText("");

                break;
            case R.id.vs_set_register_eidt_del_invide:// 一键删除
                if (chose_img == 1) {
                    Intent v_intent = new Intent(mContext, CaptureActivity.class);
                    v_intent.putExtra("code", "1");
                    startActivity(v_intent);
                }
                if (chose_img == 2) {
                    vs_register_edit_invide.setText("");
                }
                break;
            default:
                break;
        }

    }

    /**
     * @param 设定文件
     * @return void 返回类型
     * @throws
     * @Title: reg
     * @Description:
     */
    public void reg() {

        phone = vs_register_edit.getText().toString().trim();
        if (phone.trim().replaceAll("-", "").length() == 11) {
            if (phone != null && !"".equals(phone)) {
                phoneNumber = phone.replaceAll("-", "");
                if (VsUtil.checkPhone(phoneNumber)) {// 检查手机是否符合规范
                    // checkUser(phoneNumber);// 检查用户是否为新注册
                } else {
                    mToast.show(getResources().getString(R.string.vs_phone_erro));
                }
            } else {
                mToast.show("手机号码不能为空！");
            }
        } else {
            mToast.show(getResources().getString(R.string.vs_phone_erro));
        }
    }

    /**
     * 获取验证码广播
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
        filter.addAction(VsUserConfig.VS_ACTION_GET_CODE);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送查询请求
        String deciceid = VsUtil.getMacAddress(mContext);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("action", "reg");
        treeMap.put("way", "txt");
        treeMap.put("phone", mPhoneNumber);
        // 请求服务器
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GET_REG_CODE, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_GET_CODE);
    }

    /**
     * 发送注册账号广播
     */
    private void checkUserAccount(final String mPhoneNumber) {
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            mToast.show("网络连接失败，请检查网络");
            // return;

        }
        loadProgressDialog("请求提交中...");
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_CHECK_USER);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送查询请求
        String deciceid = VsUtil.getMacAddress(mContext);
        String netType = VsUtil.getNetTypeString();
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("src", "mobile");
        treeMap.put("way", "mobile");
        treeMap.put("invited_by", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_INVITED_BY));
        treeMap.put("password", VsRc4.encry_RC4_string(regpassword, DfineAction.passwad_key));
        treeMap.put("phone", mPhoneNumber);
        treeMap.put("device_id", deciceid.toUpperCase());
        treeMap.put("net_mode", netType);
        treeMap.put("verify_code", verify_code);
        treeMap.put("ext_info", "");
        treeMap.put("device_type", android.os.Build.MODEL);
        // 请求服务器
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_QUERYUSER, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_CHECK_USER);
    }

    /**
     * @Title:
     * @Description: 监听输入状态
     * @Copyright: Copyright (c) 2014
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
            String textString = vs_register_edit.getText().toString().trim();
            if (textString.length() > 0) {// 判断是否输入内容
                vs_set_register_eidt_del.setVisibility(View.VISIBLE);
            } else {
                vs_set_register_eidt_del.setVisibility(View.GONE);
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
                    vs_register_edit.append("");
                }
            } else if (textString.length() == 3 || textString.length() == 8) {
                vs_register_edit.setText(textString.subSequence(0, textString.length() - 1));
                vs_register_edit.setSelection(textString.length() - 1);
            }
        }

    }

    /**
     * @Title:
     * @Description: 监听输入状态
     * @Copyright: Copyright (c) 2014
     * @version: 1.0.0.0
     * @Date: 2014-8-27
     */
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
            String textString = vs_register_edit_invide.getText().toString().trim();
            if (textString.length() > 0) {// 判断是否输入内容
                vs_set_register_eidt_del_invide.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_del));
                chose_img = 2;
            } else {
                vs_set_register_eidt_del_invide.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_reg_code));
                chose_img = 1;
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
                    vs_register_edit_invide.append("-");
                }
            } else if (textString.length() == 3 || textString.length() == 8) {
                vs_register_edit_invide.setText(textString.subSequence(0, textString.length() - 1));
                vs_register_edit_invide.setSelection(textString.length() - 1);
            }
        }

    }

    class SetPhoneWhatch3 implements TextWatcher {

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
            verify_code = vs_edit_reg_code.getText().toString().trim();
        }
    }

    class SetPhoneWhatch4 implements TextWatcher {

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
            regpassword = vs_passeord_edit.getText().toString().trim();
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

        if (action.equals(VsUserConfig.VS_ACTION_CHECK_USER)) {
            try {
                jData = new JSONObject(jStr);
                String code = jData.getString("code");
                if (code.equals("0")) {
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", getResources().getString(R.string.register_successsumbitmsg));
                    message.what = MSG_ID_Show_Succeed_Message;
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
        } else if (action.equals(VsUserConfig.VS_ACTION_GET_CODE)) {
            try {
                jData = new JSONObject(jStr);
                String code = jData.getString("code");
                if (code.equals("0")) {
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("message", getResources().getString(R.string.register_successcode));
                    message.what = MSG_ID_Get_Code_Succeed;
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
            case MSG_ID_Show_Succeed_Message:
                dismissProgressDialog();
                showMessageDialog_register(title, message, false);
                break;
            case MSG_ID_Get_Code_Succeed:
                dismissProgressDialog();
                showMessageDialog(title, message, false);
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

    protected void showMessageDialog_register(String title, String message, boolean nocanbtn) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, VsLoginActivity.class);
                intent.putExtra("phoneNuber", phone);
                mContext.startActivity(intent);
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
