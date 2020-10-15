package com.weiwei.base.activity.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Title:Android客户端
 * @Description: 短信验证码验证
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsMsgVerifyActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 提示
     */
    private TextView vs_msg_verify_tv;
    /**
     * 验证码输入框
     */
    private EditText vs_msg_verify_edit;
    /**
     * 再次获取按钮
     */
    private LinearLayout vs_msg_verify_agin_layout;
    /**
     * 再次获取文字
     */
    private TextView vs_msg_verify_agin_tv;
    /**
     * 读秒
     */
    private TextView vs_msg_verify_agin_time;
    /**
     * 下一步
     */
    private RoundTextView vs_msg_verify_next_btn;
    /**
     * 一键删除
     */
    private ImageView vs_set_msg_verify_eidt_del;
    /**
     * 倒计时标志
     */
    private final char MSG_ID_UI_COUNT_MESSAGE = 330;
    /**
     * 请求失败
     */
    private final char MSG_ID_Show_Fail_Message = 331;
    /**
     * 获取验证码成功
     */
    private final char MSG_ID_GET_VERIFY_SUCC = 332;

    /**
     * 验证码校验成功
     */
    private final char MSG_ID_CHECK_SUCCESS = 333;
    /**
     * 获取验证码
     */
    private final static char MSG_ID_GET_VERIFY = 334;

    /**
     * 倒计数时间
     */
    private int countPercent = 0;
    /**
     * 电话号码
     */
    private String phoneNumber = null;
    /**
     * 验证码
     */
    private String code = null;
    /**
     * 请求类型：0注册、1忘记密码
     */
    private int type;
    /**
     * 提示内容
     */
    private String hintStr = null;
    private SmsObserver smsObserver;
    private Uri SMS_IN = Uri.parse("content://sms/"); // 短信地址uri

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_msg_verify_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        initView();
        VsApplication.getInstance().addActivity(this);
        /**
         * 1.注册短信观察者
         */
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_IN, true, smsObserver);
    }

    /**
     * 3.handler执行读取短信验证码方法
     */
    public Handler smsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 0000) {
                getSmsFromPhone();
            }
        }

    };

    /**
     * 2.监听短信类
     *
     * @author user-5.28
     */
    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            smsHandler.sendEmptyMessageDelayed(0000, 1000);

        }
    }

    /**
     * 4.从收信箱数据库中提取短信读出验证码
     */
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        String where = "date >" + (System.currentTimeMillis() - 1 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur) return;
        StringBuffer str = new StringBuffer();
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            str.append(body);
        }
        String bodystr = str.toString();
        Pattern pattern = Pattern.compile("[0-9]{4}");
        Matcher matcher = pattern.matcher(bodystr);
        if (matcher.find()) {
            String res = matcher.group();
            vs_msg_verify_edit.setText(res);
        }
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
     * 初始化控件
     */
    private void initView() {
        // 获取数据
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        type = intent.getIntExtra("type", 0);
        // 判断是注册还是重置密码
        if (type == 0) {
            mTitleTextView.setText(R.string.vs_reghist_title_hint1);
        } else if (type == 1) {
            mTitleTextView.setText(R.string.vs_set_phone_title_hint2);
        }
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        vs_msg_verify_tv = (TextView) findViewById(R.id.vs_msg_verify_tv);
        vs_msg_verify_edit = (EditText) findViewById(R.id.vs_msg_verify_edit);
        vs_msg_verify_agin_layout = (LinearLayout) findViewById(R.id.vs_msg_verify_agin_layout);
        vs_msg_verify_agin_tv = (TextView) findViewById(R.id.vs_msg_verify_agin_tv);
        vs_msg_verify_agin_time = (TextView) findViewById(R.id.vs_msg_verify_agin_time);
        vs_msg_verify_next_btn = (RoundTextView) findViewById(R.id.vs_msg_verify_next_btn);
        vs_set_msg_verify_eidt_del = (ImageView) findViewById(R.id.vs_set_msg_verify_eidt_del);
        // 设置监听事件
        vs_msg_verify_next_btn.setOnClickListener(this);
        vs_msg_verify_agin_layout.setOnClickListener(this);
        vs_set_msg_verify_eidt_del.setOnClickListener(this);
        vs_msg_verify_edit.addTextChangedListener(new VsMsgWhatch());

        if (phoneNumber != null && !"".equals(phoneNumber) && phoneNumber.length() == 11) {
            setHintText(getResources().getString(R.string.vs_msg_verify_hint1), getResources().getString(R.string.vs_msg_verify_hint2));
        }
        countPercent = 30;
        mBaseHandler.sendEmptyMessage(MSG_ID_UI_COUNT_MESSAGE);
    }

    /**
     * 设置提示内容
     *
     * @param startHint
     * @param endHint
     */
    public void setHintText(String startHint, String endHint) {
        vs_msg_verify_tv.setText(startHint);
        vs_msg_verify_tv.append(Html.fromHtml("<B>" + phoneNumber + "</B>"));
        vs_msg_verify_tv.append(endHint);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_msg_verify_agin_layout:// 再次获取

//			signature_setting_dialog.dismiss();
                Message msg = new Message();
                msg.what = MSG_ID_GET_VERIFY;
                Bundle bundle = new Bundle();
                bundle.putString("type", "0");
                msg.setData(bundle);
                mBaseHandler.sendMessage(msg);
                // 弹出验证码选择栏
//			showDialog(mContext, mBaseHandler);
                break;
            case R.id.vs_msg_verify_next_btn:// 下一步
                code = vs_msg_verify_edit.getText().toString().trim();
                if (code != null && !"".equals(code)) {
                    if (code.length() == 4) {
                        confirm(code);// 校验验证码
                        /**
                         * 5.注销监听
                         */
                        getContentResolver().unregisterContentObserver(smsObserver);
                    } else {
                        mToast.show("请输入4位验证码！");
                    }
                } else {
                    mToast.show("验证码不能为空！");
                }

                break;
            case R.id.vs_set_msg_verify_eidt_del:
                vs_msg_verify_edit.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    protected void HandleLeftNavBtn() {
        // TODO Auto-generated method stub
        showExitDialog();
    }

    /**
     * @Title:输入框状态监听
     * @Description:
     * @Copyright: Copyright (c) 2014
     * @author: Sundy
     * @version: 1.0.0.0
     * @Date: 2014-8-29
     */
    class VsMsgWhatch implements TextWatcher {

        /*
         * (non-Javadoc)
         *
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         *
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         *
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String vsStr = vs_msg_verify_edit.getText().toString().trim();
            if (vsStr.length() > 0) {
                vs_set_msg_verify_eidt_del.setVisibility(View.VISIBLE);
            } else {
                vs_set_msg_verify_eidt_del.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 获取验证码
     *
     * @param phoneNumber
     * @param type        1为语音 0或空为短信验证码
     */
    private void getVerificationCode(String phoneNumber, int codetype) {
        loadProgressDialog("请求提交中");
        if (codetype == 1) {
            hintStr = getResources().getString(R.string.vs_msg_verify_hint_voice);
        } else if (codetype == 0) {
            hintStr = getResources().getString(R.string.vs_msg_verify_hint_sms);
        }
        if (phoneNumber == null || phoneNumber.length() < 11) {
            mToast.show("请输入手机号", Toast.LENGTH_SHORT);
            return;
        }
        // 绑定广播接收器
        unregisterKcBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_RESET_PWD_APPLY);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        // 发送请求
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("account", phoneNumber);
        // 发送获取验证码请求
        String intrface = null;
        if (type == 0) {
            if (codetype == 1) {
                treeMap.put("sendtype", "1");
            } else if (codetype == 0) {
                treeMap.put("sendtype", "2");
            }
            treeMap.put("accounttype", "mobile");
            treeMap.put("deviceid", VsUtil.getMacAddress(mContext));
            intrface = GlobalVariables.INTERFACE_REG_GET_VERIFY;
        } else if (type == 1) {
            if (codetype == 1) {
                treeMap.put("issue_typt", "1");
            }
            intrface = GlobalVariables.INTRFACE_VERIFY_NUMBER;
        }
        CoreBusiness.getInstance().startThread(mContext, intrface, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_RESET_PWD_APPLY);
        countPercent = 30;
        mBaseHandler.sendEmptyMessage(MSG_ID_UI_COUNT_MESSAGE);
    }

    /**
     * 校验验证码
     *
     * @param code 验证码
     */
    private void confirm(String code) {
        loadProgressDialog("正在校验验证码....");
        // 绑定广播接收器
        unregisterKcBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.VS_ACTION_RESET_PWD_CHECK);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);

        // 发送请求
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("account", phoneNumber.replaceAll("-", ""));
        String intrface = null;
        if (type == 0) {
            treeMap.put("vcode", code);
            intrface = GlobalVariables.INTRFACE_VERIFY_MSG_REG;
        } else if (type == 1) {
            treeMap.put("code", code);
            intrface = GlobalVariables.INTRFACE_VERIFY_MSG;
        }
        // 发送校验验证码请求
        CoreBusiness.getInstance().startThread(mContext, intrface, DfineAction.authType_Key, treeMap, VsUserConfig.VS_ACTION_RESET_PWD_CHECK);
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
        // 关闭进度栏
        dismissProgressDialog();
        try {
            jData = new JSONObject(jStr);
            String retStr = jData.getString("result");
            if (VsUserConfig.VS_ACTION_RESET_PWD_APPLY.equals(action)) {
                if ("0".equals(retStr)) {
                    bundle.putString("msg", hintStr);
                    message.what = MSG_ID_GET_VERIFY_SUCC;
                } else {
                    countPercent = 0;
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_Show_Fail_Message;
                }

            } else if (VsUserConfig.VS_ACTION_RESET_PWD_CHECK.equals(action)) {
                if ("0".equals(retStr)) {
                    message.what = MSG_ID_CHECK_SUCCESS;
                } else {
                    message.what = MSG_ID_Show_Fail_Message;
                    bundle.putString("msg", jData.getString("reason"));
                }
            }
        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
            bundle.putString("msg", getString(R.string.vs_set_phone_title_hint2) + "失败，请稍后再试！");
            message.what = MSG_ID_Show_Fail_Message;
        }
        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_Show_Fail_Message:
                mToast.show(msg.getData().getString("msg"));
                break;
            case MSG_ID_GET_VERIFY_SUCC:
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_LONG);
                break;
            case MSG_ID_CHECK_SUCCESS:
                // 进入设置密码界面-传递验证码
                Intent intent = new Intent(mContext, VsSetPasswordActivity.class);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("code", code);
                intent.putExtra("type", type);
                startActivity(intent);
                this.finish();
                break;
            case VsUserConfig.MSG_ID_GET_MSG_SUCCESS:
                String code = msg.getData().getString("code");
                if (code != null && !"".equals(code) && code.length() == 4) {
                    // 自动填充验证码
                    vs_msg_verify_edit.setText(code);
                    vs_msg_verify_edit.setSelection(code.length());
                }
                break;
            case MSG_ID_UI_COUNT_MESSAGE:
                if (countPercent > 1) {
                    // 修改计数
                    countPercent--;
                    // 设置再次按钮状态
                    vs_msg_verify_agin_layout.setEnabled(false);
                    vs_msg_verify_agin_time.setText(countPercent + "s");
                    vs_msg_verify_agin_time.setVisibility(View.VISIBLE);
                    vs_msg_verify_agin_layout.setBackgroundColor(getResources().getColor(R.color.vs_gray_simaple));
                    vs_msg_verify_agin_tv.setTextColor(getResources().getColor(R.color.vs_gray));
                    vs_msg_verify_agin_time.setTextColor(getResources().getColor(R.color.vs_gree));
                    mBaseHandler.sendEmptyMessageDelayed(MSG_ID_UI_COUNT_MESSAGE, 1 * 1000);
                } else {
                    vs_msg_verify_agin_layout.setEnabled(true);
                    vs_msg_verify_agin_time.setVisibility(View.GONE);
                    vs_msg_verify_agin_layout.setBackgroundResource(R.drawable.vs_whilte_btn_selecter);
                    vs_msg_verify_agin_tv.setTextColor(getResources().getColor(R.color.vs_gree));
                }
                break;
            case MSG_ID_GET_VERIFY:// 选择获取验证码
                if ("0".equals(msg.getData().getString("type"))) {

                    getVerificationCode(phoneNumber, 0);
                    setHintText(getResources().getString(R.string.vs_msg_verify_hint1), getResources().getString(R.string.vs_msg_verify_hint2));
                } else if ("1".equals(msg.getData().getString("type"))) {

                    getVerificationCode(phoneNumber, 1);
                    setHintText(getResources().getString(R.string.vs_msg_verify_voice_hint1), getResources().getString(R.string.vs_msg_verify_voice_hint2));
                }
                break;
        }
    }

    /**
     * 滑出显示手动选择验证码烦死
     */
    public static void showDialog(final Activity mContext, final Handler mHandler) {
        final Dialog signature_setting_dialog = new Dialog(mContext, R.style.CommonDialogStyle);
        final View v = View.inflate(mContext, R.layout.vs_verify_type_layout, null);
        Button vs_msg_verify_btn = (Button) v.findViewById(R.id.vs_msg_verify_btn);
        Button vs_voice_verify_btn = (Button) v.findViewById(R.id.vs_voice_verify_btn);
        Button vs_cannel_btn = (Button) v.findViewById(R.id.vs_cannel_btn);

        vs_msg_verify_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signature_setting_dialog.dismiss();
                Message msg = new Message();
                msg.what = MSG_ID_GET_VERIFY;
                Bundle bundle = new Bundle();
                bundle.putString("type", "1");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
        vs_voice_verify_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signature_setting_dialog.dismiss();
                Message msg = new Message();
                msg.what = MSG_ID_GET_VERIFY;
                Bundle bundle = new Bundle();
                bundle.putString("type", "0");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
        vs_cannel_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signature_setting_dialog.dismiss();
                signature_setting_dialog.cancel();
            }
        });

        signature_setting_dialog.setContentView(v);
        signature_setting_dialog.setCanceledOnTouchOutside(true);
        signature_setting_dialog.setCancelable(true);
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = signature_setting_dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移. 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略. 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略. 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动. 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动. gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        if (GlobalVariables.width == 0) {
            // 获取屏幕宽高与密度
            VsUtil.setDensityWH(mContext);
        }
        lp.x = 0; // 新位置X坐标
        lp.y = (int) (GlobalVariables.height - mContext.getResources().getDimension(R.dimen.signature_height)); // 新位置Y坐标
        lp.width = GlobalVariables.width; // 宽度
        lp.height = (int) (mContext.getResources().getDimension(R.dimen.signature_height)); // 高度
        lp.alpha = 1f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(0, 0, GlobalVariables.height, 0);// 移动
        mTranslateAnimation.setDuration(500);
        v.startAnimation(mTranslateAnimation);
        signature_setting_dialog.show();
        signature_setting_dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                signature_setting_dialog.dismiss();
                signature_setting_dialog.cancel();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示退出对话框
     */
    private void showExitDialog() {
        String msg = null;
        String btnMsg = null;
        if (type == 0) {
            msg = getResources().getString(R.string.vs_reg_dialog_msg2);
            btnMsg = getResources().getString(R.string.vs_reg_dialog_goon);
        } else if (type == 1) {
            msg = getResources().getString(R.string.vs_pwd_dialog_msg2);
            btnMsg = getResources().getString(R.string.vs_pwd_dialog_goon);
        }
        VsUtil.showYesNoDialog(mContext, null, msg, btnMsg, getResources().getString(R.string.vs_exit), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                VsMsgVerifyActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
