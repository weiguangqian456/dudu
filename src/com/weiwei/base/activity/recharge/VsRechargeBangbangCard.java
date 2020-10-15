package com.weiwei.base.activity.recharge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.util.MyButton;
import com.weiwei.base.util.MyButton.CountDownOnClickListener;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.base.widgets.NoticeDialog;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.zbar.lib.CaptureActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @Title:Android客户端
 * @Description: TD自己的充值卡充值
 * @Copyright: Copyright (c) 2014
 * @author: letianen
 * @version: 1.0.0
 * @Date: 2014-11-24
 */
public class VsRechargeBangbangCard extends VsBaseActivity implements OnClickListener {
    private TextView vs_recharge_mobilecard_sumit_number;// 输入卡的数量
    private TextView vs_recharge_mobilecard_sumit_hint;// 提交提示
    private TextView vs_recharge_mobilecard_password;// 提示充值账号
    private ImageView vs_recharge_mobilecard_hint_image;// 提交失败提示图标
    private ImageView vs_recharge_mobilecard_load;// 充值提交加载图标
    private MyButton vs_recharge_cardlist_btn;// 充值提交按钮
    private EditText vs_recharge_mobilecard_password_edit;// 密码输入框
    private RelativeLayout vs_recharge_mobilecard_hintlayout;// 查看输入充值卡张数
    private RelativeLayout vs_recharge_mobilecard_load_layout;// 加载图标布局
    // private ImageView vs_recharge_mobilecard_jt;//箭头
    // private boolean summitStateFlag = false;//输入或提交状态
    // private String mAcount;// 帐号
    private String goodsid = "40010";// 商品编号
    // private String mPakDesc;//商品名称
    // private String mPromotion;
    private String mPayType = "5";// 充值类型
    // private String mPayKind;//充值类型编号
    // private String mPrice;//充值金额
    // private String mPayTypeDesc;//充值类型名称
    public int[] cardCharSequence = {4, 9, 14, 19};
    public int[] pwdCharSequence = {4, 9, 14, 19, 24};
    // private String gCardno;//卡号
    private String gCardpwd;// 卡密
    // private int sumintNumber;//充值卡提交数
    // private int failNumber;//充值失败数
    private final char MSG_ID_Recharge_Succeed_Message = 60;// 充值成功
    private final char MSG_ID_Recharge_Fail_Message = 61;// 充值失败
    /**
     * TOKEN错误
     */
    private final char MSG_TOKEN_ERROR = 64;
    private String msgString;// 返回消息
    private View recharge_line1;// 分隔线
    // private String operparam;//商城外部订单号
    private Context mContext = this;
    private NoticeDialog.Builder builder = new NoticeDialog.Builder(mContext);
    private String phoneNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_recharge_bangbangcard);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();

        // Intent intent = getIntent();
        // mAcount = intent.getStringExtra("mAcount");
        // goodsid = "40010";
        // mPakDesc = intent.getStringExtra("mPakDesc");
        // mPromotion = intent.getStringExtra("mPromotion");
        // mPrice = intent.getStringExtra("mPrice");
        // mPayType = "5";
        // mPayKind = intent.getStringExtra("mPayKind");
        // mPayTypeDesc = intent.getStringExtra("mPayTypeDesc");
        // operparam = intent.getStringExtra("operparam");

        mTitleTextView.setText(getString(R.string.my_tv_recharge_bangbangfs));
        showLeftNavaBtn(R.drawable.icon_back);
        showRightTxtBtn("为他人充值");
        // showLeftTxtBtn(getResources().getString(R.string.recharge_type));
        // 初始化视图
        initView();
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    private void refuelRecharge() {

        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        map.put("appId", "dudu");
        if (phoneNum == null) {
            map.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        } else {
            map.put("phone", phoneNum);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(map));

        RetrofitClient.getInstance(this).Api()
                .refuelRecharge(body)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {

                    }
                });

    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 获取控件对象
        vs_recharge_mobilecard_sumit_number = (TextView) findViewById(R.id.vs_recharge_mobilecard_sumit_number);
        vs_recharge_mobilecard_sumit_hint = (TextView) findViewById(R.id.vs_recharge_mobilecard_sumit_hint);
        vs_recharge_mobilecard_password = (TextView) findViewById(R.id.vs_recharge_mobilecard_password);
        vs_recharge_mobilecard_hint_image = (ImageView) findViewById(R.id.vs_recharge_mobilecard_hint_image);
        vs_recharge_mobilecard_load = (ImageView) findViewById(R.id.vs_recharge_mobilecard_load);
        vs_recharge_cardlist_btn = (MyButton) findViewById(R.id.vs_recharge_cardlist_btn);
        vs_recharge_mobilecard_password_edit = (EditText) findViewById(R.id.vs_recharge_mobilecard_password_edit);
        vs_recharge_mobilecard_hintlayout = (RelativeLayout) findViewById(R.id.vs_recharge_mobilecard_hintlayout);
        vs_recharge_mobilecard_load_layout = (RelativeLayout) findViewById(R.id.vs_recharge_mobilecard_load_layout);
        // vs_recharge_mobilecard_jt = (ImageView)
        // findViewById(R.id.vs_recharge_mobilecard_jt);
        recharge_line1 = findViewById(R.id.recharge_line1);
        vs_recharge_mobilecard_password.setText("充值账号：" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        // 设置监听事件
        // vs_recharge_cardlist_btn.setOnClickListener(this);
        vs_recharge_mobilecard_hintlayout.setOnClickListener(this);
        vs_recharge_mobilecard_password_edit.addTextChangedListener(new KcTextWatcher(R.id.vs_recharge_mobilecard_password_edit));
        vs_recharge_cardlist_btn.setCountDownOnClickListener(new CountDownOnClickListener() {

            @Override
            public void onClickListener() {
                vs_recharge_cardlist_btn.startCountDown("提交中", "立即充值");
                String invited_by = VsUserConfig.getDataString(mContext, VsUserConfig.Jey_Invited_By);
                String cate = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_cateTypes, "");
                JSONObject json;
                String version = "";
                try {
                    json = new JSONObject(cate);
                    version = json.getString("cate");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if ("all".equals(version) && !(invited_by != null && invited_by.length() != 0)) {
                    VsUtil.showDialog(DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.prompt), mContext.getResources().getString(R.string.login_prompt7), mContext.getResources().getString(R.string.login_prompt9), mContext.getResources().getString(R.string.login_prompt8), new
                            DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(VsRechargeBangbangCard.this, CaptureActivity.class);
                                    intent.putExtra("code", "2");
                                    startActivity(intent);

                                }
                            }, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击提交初始化控件状态
                            vs_recharge_mobilecard_sumit_number.setVisibility(View.INVISIBLE);
                            vs_recharge_mobilecard_sumit_hint.setVisibility(View.INVISIBLE);
                            vs_recharge_mobilecard_hint_image.setVisibility(View.INVISIBLE);
                            gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();

                            if (checkNumber(gCardpwd)) {
                                vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
                                recharge_line1.setVisibility(View.VISIBLE);
                                vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
                                // 设置按钮不可用
                                vs_recharge_cardlist_btn.setEnabled(false);
                                // 启动动画
                                VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
                                // 充值
                                recharge(gCardpwd.replaceAll(" ", ""));
                            } else {
                                mToast.show("卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
                            }

                        }
                    }, mContext);

                } else {

                    // 点击提交初始化控件状态
                    vs_recharge_mobilecard_sumit_number.setVisibility(View.INVISIBLE);
                    vs_recharge_mobilecard_sumit_hint.setVisibility(View.INVISIBLE);
                    vs_recharge_mobilecard_hint_image.setVisibility(View.INVISIBLE);
                    gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();

                    if (checkNumber(gCardpwd)) {
                        vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
                        recharge_line1.setVisibility(View.VISIBLE);
                        vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
                        // 设置按钮不可用
                        vs_recharge_cardlist_btn.setEnabled(false);
                        // 启动动画
                        VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
                        // 充值
                        recharge(gCardpwd.replaceAll(" ", ""));
                    } else {
                        mToast.show("卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
                    }

                }

            }
        });
    }

    @Override
    protected void HandleRightNavBtn() {
        NoticeDialog dialog = builder.create();
        dialog.show();
        builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                phoneNum = builder.getPhone();
                if (phoneNum.trim().replaceAll("-", "").length() == 11) {
                    vs_recharge_mobilecard_password.setText("充值账号：" + phoneNum);
                } else {
                    mToast.show("请输入正确的手机号码", Toast.LENGTH_SHORT);
                }

            }
        });
        super.HandleRightNavBtn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vs_recharge_mobilecard_hintlayout.setVisibility(View.GONE);
        recharge_line1.setVisibility(View.GONE);
        vs_recharge_mobilecard_hintlayout.setEnabled(false);// 设置进入充值卡列表不可点击
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick(3000)) {
            return;
        }
        switch (v.getId()) {
            case R.id.vs_recharge_cardlist_btn:
                // String invited_by = VsUserConfig.getDataString(mContext,
                // VsUserConfig.Jey_Invited_By);
                // String cate = VsUserConfig.getDataString(mContext,
                // VsUserConfig.JKey_cateTypes,"");
                // JSONObject json;
                // String version = "";
                // try {
                // json = new JSONObject(cate);
                // version = json.getString("cate");
                // } catch (JSONException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                //
                // if ("all".equals(version)&&!(invited_by!= null&&invited_by.length()!=0)) {
                // VsUtil.showDialog(DfineAction.RES.getString(R.string.product) +
                // mContext.getResources().getString(R.string.prompt), mContext.getResources()
                // .getString(R.string.login_prompt7), mContext.getResources()
                // .getString(R.string.login_prompt9),
                // mContext.getResources().getString(R.string.login_prompt8), new
                // DialogInterface.OnClickListener() {
                //
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // Intent intent = new Intent(VsRechargeBangbangCard.this,
                // CaptureActivity.class);
                // intent.putExtra("code", "2");
                // startActivity(intent);
                //
                // }
                // }
                // ,new DialogInterface.OnClickListener() {
                //
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // // 点击提交初始化控件状态
                // vs_recharge_mobilecard_sumit_number.setVisibility(View.INVISIBLE);
                // vs_recharge_mobilecard_sumit_hint.setVisibility(View.INVISIBLE);
                // vs_recharge_mobilecard_hint_image.setVisibility(View.INVISIBLE);
                // gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();
                //
                // if (checkNumber(gCardpwd)) {
                // vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
                // recharge_line1.setVisibility(View.VISIBLE);
                // vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
                // // 设置按钮不可用
                // vs_recharge_cardlist_btn.setEnabled(false);
                // // 启动动画
                // VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
                // // 充值
                // recharge(gCardpwd.replaceAll(" ", ""));
                // }
                // else {
                // mToast.show("卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
                // }
                //
                //
                // }
                // }, mContext);
                //
                //
                //
                // }else {
                //
                // // 点击提交初始化控件状态
                // vs_recharge_mobilecard_sumit_number.setVisibility(View.INVISIBLE);
                // vs_recharge_mobilecard_sumit_hint.setVisibility(View.INVISIBLE);
                // vs_recharge_mobilecard_hint_image.setVisibility(View.INVISIBLE);
                // gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();
                //
                // if (checkNumber(gCardpwd)) {
                // vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
                // recharge_line1.setVisibility(View.VISIBLE);
                // vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
                // // 设置按钮不可用
                // vs_recharge_cardlist_btn.setEnabled(false);
                // // 启动动画
                // VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
                // // 充值
                // recharge(gCardpwd.replaceAll(" ", ""));
                // }
                // else {
                // mToast.show("卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
                // }
                //
                //
                // }
                break;

            default:
                break;
        }

    }

    /**
     * 检测输入内容是否符合规范
     */
    public boolean checkNumber(String gCardpwd_) {
        String gCardpwd = gCardpwd_.replaceAll(" ", "");
        if (gCardpwd != null && !"".equals(gCardpwd)) {
            // if (mPayKind.equals("701")) {
            // /*
            // * if (gCardno.length() != 16 && gCardno.length() != 10 && gCardno.length() !=
            // 17) {
            // * mToast.show(getResources().getString(R.string.charge_card_error),
            // Toast.LENGTH_SHORT); return false;
            // * } else if (gCardpwd.length() != 8 && gCardpwd.length() != 17 &&
            // gCardpwd.length() != 18 &&
            // * gCardpwd.length() != 21) {
            // mToast.show(getResources().getString(R.string.charge_pwd_error),
            // * Toast.LENGTH_SHORT); return false; }
            // */
            // return true;
            // }
            // else if (mPayKind.equals("702")) {
            // if (gCardno.length() != 15) {
            // mToast.show(getResources().getString(R.string.charge_card_error),
            // Toast.LENGTH_SHORT);
            // return false;
            // }
            // else if (gCardpwd.length() != 19) {
            // mToast.show(getResources().getString(R.string.charge_pwd_error),
            // Toast.LENGTH_SHORT);
            // return false;
            // }
            // }
            // else if (mPayKind.equals("703")) {
            // if (gCardno.length() != 19) {
            // mToast.show(getResources().getString(R.string.charge_card_error),
            // Toast.LENGTH_SHORT);
            // return false;
            // }
            // else if (gCardpwd.length() != 18) {
            // mToast.show(getResources().getString(R.string.charge_pwd_error),
            // Toast.LENGTH_SHORT);
            // return false;
            // }
            // }
            return true;
        } else {
            return false;
        }
        // recharge();充值方法
    }

    /**
     * 输入状态监听
     *
     * @author 李志
     */
    class KcTextWatcher implements TextWatcher {

        private int prevLength = 0;
        /**
         * 控件ID号
         */
        private int resourceId;

        /**
         * 构造方法
         *
         * @param resourceId
         */
        public KcTextWatcher(int resourceId) {
            this.resourceId = resourceId;
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (resourceId) {
                case R.id.vs_recharge_mobilecard_password_edit:// 密码
                    // String textPwd =
                    // vs_recharge_mobilecard_password_edit.getText().toString().replaceAll(" ",
                    // "");
                    // if (mPayKind.equals("701")) {
                    // vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font
                    // color='#41B2F7'>" + textPwd.length() + "</font>)"));
                    // }
                    // else if (mPayKind.equals("702")) {
                    // vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font
                    // color='#41B2F7'>" + textPwd.length() + "</font>/19)"));
                    // }
                    // else if (mPayKind.equals("703")) {
                    // vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font
                    // color='#41B2F7'>" + textPwd.length() + "</font>/18)"));
                    // }
                    // else if (mPayKind.equals("708")) {
                    // vs_recharge_mobilecard_password.setText("(" + textPwd.length() + ")");
                    // }

                    if (prevLength > vs_recharge_mobilecard_password_edit.getText().length()) {
                        prevLength = vs_recharge_mobilecard_password_edit.getText().length();
                        return;
                    }
                    vs_recharge_mobilecard_password_edit.removeTextChangedListener(this);

                    int length = pwdCharSequence.length;
                    for (int i = 0; i < length; i++) {
                        if (vs_recharge_mobilecard_password_edit.getText().length() == pwdCharSequence[i]) {
                            vs_recharge_mobilecard_password_edit.setText(s + " ");
                            vs_recharge_mobilecard_password_edit.setSelection(vs_recharge_mobilecard_password_edit.length());
                        }
                    }
                    prevLength = vs_recharge_mobilecard_password_edit.getText().length();
                    vs_recharge_mobilecard_password_edit.addTextChangedListener(this);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    }

    /**
     * 向服务器发送充值参数
     */
    private void recharge(String gCardpwd) {
        // 充值提交数
        // sumintNumber++;
        // 统计代码
        // startTime = System.currentTimeMillis();
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            mToast.show("网络连接失败，请检查网络");
            vs_recharge_cardlist_btn.stopCountDown();
            // vs_recharge_cardlist_btn.setClickable(true);
            return;

        }
        unregisterKcBroadcast();

        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.actionMobileRecharge);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);

        // 加载用户信息
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        if (null == phoneNum) {

            treeMap.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
            treeMap.put("account", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        } else {
            treeMap.put("phone", phoneNum);
            treeMap.put("account", phoneNum);
        }
        treeMap.put("card_pwd", gCardpwd);
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_MOBILE_RECHARGE, DfineAction.authType_UID, treeMap, GlobalVariables.actionMobileRecharge);
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        msgString = msg.getData().getString("msgString");
        switch (msg.what) {
            case MSG_ID_Recharge_Succeed_Message:
                // 显示提交张数
                vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);
                vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
                // vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font
                // color=#50C965>" + 1 + "/" + 1 + "</font>张"));
                // 显示成功
                vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
                vs_recharge_mobilecard_sumit_hint.setText("充值成功");
                // 设置按钮可用
                // vs_recharge_cardlist_btn.setEnabled(true);
                // 提交完成后设置光标在第一个输入框
                vs_recharge_mobilecard_password_edit.requestFocus();
                refuelRecharge();
                // 充值完成后提示绑定手机
                new CustomDialog.Builder(mContext).setTitle(DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt)).setMessage("充值成功").setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        finish();
                    }
                }).create().show();
                break;
            case MSG_ID_Recharge_Fail_Message:
                // 显示提交张数
                vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);
                vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
                // vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font
                // color=#41B2F7>" + 1 + "/" + 1 + "</font>张"));
                // 显示失败张数
                vs_recharge_mobilecard_hint_image.setVisibility(View.VISIBLE);
                vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
                // vs_recharge_mobilecard_sumit_hint.setText(Html.fromHtml("失败<font
                // color=#FF8237>" + 1 + "</font>张"));
                vs_recharge_mobilecard_sumit_hint.setText(Html.fromHtml("充值失败：<font color=#FF8237>" + msgString + "</font>"));
                // 设置按钮可用
                // vs_recharge_cardlist_btn.setEnabled(true);
                break;
            case MSG_TOKEN_ERROR:// 余额不足
                showMessageDialog_token("温馨提示", "您的账号已经在另外一个设备上登陆，请您重新登陆", false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        super.handleKcBroadcast(context, intent);
        String vsStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        // CustomLog.i("lte", "vsStr---" + vsStr);

        if (intent.getAction().equals(GlobalVariables.actionMobileRecharge)) {
            Message message = mBaseHandler.obtainMessage();
            Bundle bundle = new Bundle();
            try {
                JSONObject vsData = new JSONObject(vsStr);
                String vsResult = vsData.getString("code");
                // 成功
                if (vsResult.equals("0")) {
                    // 清空输入
                    vs_recharge_mobilecard_password_edit.setText("");
                    VsUserConfig.isChangeBalance = true;
                    VsUserConfig.changeBalanceTime = System.currentTimeMillis();
                    msgString = vsData.getString("msg");
                    message.what = MSG_ID_Recharge_Succeed_Message;
                } else {
                    if (vsResult.equals("-99")) {
                        if (!VsUtil.isCurrentNetworkAvailable(mContext)) {
                            return;
                        }
                    }
                    String wrongResult = vsData.getString("msg");
                    if (wrongResult != null) {
                        msgString = wrongResult.toString();
                    }
                    message.what = MSG_ID_Recharge_Fail_Message;
                }

            } catch (Exception e) {
                e.printStackTrace();
                msgString = getResources().getString(R.string.request_failinfo);
                message.what = MSG_ID_Recharge_Fail_Message;
            }
            bundle.putString("msgString", msgString);
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        }
    }

}
