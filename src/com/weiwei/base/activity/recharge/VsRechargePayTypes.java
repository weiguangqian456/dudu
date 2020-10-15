package com.weiwei.base.activity.recharge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.hwtx.dududh.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.aplpay.PayResult;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.Resource;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsAction;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.NoticeDialog;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PayUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.zbar.lib.CaptureActivity;
import com.zte.common.MD5;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * @author Sundy
 * @ClassName: VsRechargePayTypes
 * @Description: 充值类型
 * @date 2015年1月16日 下午1:22:13
 */
public class VsRechargePayTypes extends VsBaseActivity {
    private static final String TAG = VsRechargePayTypes.class.getSimpleName();
    private Context mContext = this;
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 9;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    /**
     * 充值类型适配器
     */
    RechargeAdapter adapter = null;
    /**
     * 手机号码(用户编号)
     */
    private TextView vs_recharge_phoneNumber;
    /**
     * 充值套餐内容
     */
    private TextView vs_recharge_type_tc;
    /**
     * 充值类型链表
     */
    private ListView charge_package_listview;

    /**
     * 套餐描述
     */
    private String pure_name;
    /**
     * 新版本读取的赠送商品列表
     */
    private String present;
    /**
     * 是否为赠送
     */
    private String recommend_flag;

    private String[][] payTypeString = null;
    private ArrayList<String[]> rcpData = null;

    private String mAcount = "";
    private String goodsid = "";
    private String mPrice = "";
    private String mPakDesc = "";
    private String mPromotion = "";

    private String AlipayOrderid, notify_url = "";
    private String msgString = "";
    // private Long AlipayStartTime, OnlineStartTime;

    private String OnlineOrderid = "";
    private String OnlineOrderDesc;

    private final int MSG_SHOW_HANDLE1 = 0;
    private final int MSG_SHOW_HANDLE2 = 6;
    private final int MSG_ID_RechargeAlipay_Success_Message = 4;

    /************** 获取类对象 ***********************/
    // private XmlHttpConnection httpConnection;
    // private GetValue values;
    // private Xmlpar xmlpar;
    /**
     * *******************************************
     */
    // private InputStream PrivateSign;

    private final int MSG_ID_RECHARGEWAPALIPAY_SUCCESS_MESSAGE = 8;
    private final int MSG_ID_RECHARGEWAPALIPAY_FAILURE_MESSAGE = 10;
    private final int MSG_ID_RECHARGE_WEIXIN_SUCCESS = 11;
    private final int MSG_ID_RECHARGE_WEIXIN_FAILED = 12;

    /**
     * 外部传入的订单号
     */
    private String operparam;
    private String callback;
    private String callbacktype;
    private String phoneNum;
    private NoticeDialog.Builder builder = new NoticeDialog.Builder(mContext);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_recharge_paytypes);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        // 注册广播接收查询余额的银联充值确定按钮信息
        // registerReceiver(upmopbutton, new IntentFilter(DfineAction.ACTION_UPMOP));
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
        showRightTxtBtn("为他人充值");
        mTitleTextView.setText(R.string.title_bar_recharge);
        Intent intent = getIntent();
        mPrice = intent.getStringExtra("goodsvalue");
        goodsid = intent.getStringExtra("goodsid");
        mPakDesc = intent.getStringExtra("goodsname");
        mPromotion = intent.getStringExtra("goodsdes");
        // convert_price = intent.getStringExtra("convert_price");
        recommend_flag = intent.getStringExtra("recommend_flag");
        pure_name = intent.getStringExtra("pure_name");
        present = intent.getStringExtra("present");
        operparam = intent.getStringExtra("operparam");
        callback = intent.getStringExtra("callback");
        callbacktype = intent.getStringExtra("callbacktype");
        // 套餐类型数据需要获取---------------------包括套餐类型、金额、套餐详情、了解链接等
        mAcount = intent.getStringExtra("Accounts");
        initRegInfoData();
        initView();// 初始化充值主界面
        VsApplication.getInstance().addActivity(this);
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
                    VsUtil.showDialog("温馨提醒", "您将为:" + phoneNum + "充值", "我知道了", mContext.getResources().getString(R.string.cancel), null, null, mContext);
                } else {
                    mToast.show("请输入正确的手机号码", Toast.LENGTH_SHORT);
                }

            }
        });
        super.HandleRightNavBtn();
    }

    @Override
    protected void HandleLeftNavBtn() {
        // TODO Auto-generated method stub
        setResult();
        super.HandleLeftNavBtn();
    }

    /**
     * 初始化充值类型数据
     */
    private void initRegInfoData() {
        String regInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PayTypes);
        try {
            JSONArray jsonArray = new JSONArray(regInfo);
            int count = jsonArray.length();
            Object obj = null;
            JSONObject jobj = null;
            for (int n = 0; n < count; n++) {
                String str = ((JSONObject) (jsonArray.get(n))).getString("pay_name");
                if (str.contains("充值卡")) {
                    count--;
                }
            }
            payTypeString = new String[count][3];
            for (int i = 0; i < count && (obj = jsonArray.get(i)) != null; i++) {
                jobj = (JSONObject) obj;
                if (!jobj.getString("pay_name").contains("充值卡")) {
                    payTypeString[i][0] = jobj.getString("pay_desc");
                    payTypeString[i][1] = jobj.getString("pay_name");
                    payTypeString[i][2] = jobj.getString("pay_type");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // if (payTypeString == null || payTypeString.length == 0) {
            // payTypeString = new String[DfineAction.defaultRegType.length][3];
            // int len = DfineAction.defaultRegType.length;
            // for (int i = 0; i < len; i++) {
            // payTypeString[i][0] = DfineAction.defaultRegType[i][0];
            // payTypeString[i][1] = DfineAction.defaultRegType[i][1];
            // payTypeString[i][2] = DfineAction.defaultRegType[i][2];
            // }
            // }
        }
    }

    private void initView() {
        vs_recharge_phoneNumber = (TextView) findViewById(R.id.vs_recharge_phoneNumber);
        vs_recharge_type_tc = (TextView) findViewById(R.id.vs_recharge_type_tc);
        charge_package_listview = (ListView) findViewById(R.id.charge_package_listview);
        // 初始化数据
        vs_recharge_phoneNumber.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        if (mPakDesc != null && !"".equals(mPakDesc)) {
            vs_recharge_type_tc.setText(mPakDesc);
        }

        // 获取套餐数
        int len = payTypeString.length;
        rcpData = new ArrayList<String[]>();
        for (int i = 0; i < len; i++) {
            rcpData.add(payTypeString[i]);
        }
        charge_package_listview = (ListView) findViewById(R.id.charge_package_listview);
        adapter = new RechargeAdapter(mContext, rcpData);
        charge_package_listview.setAdapter(adapter);

    }

    /**
     * 充值类型适配器
     * listView item适配器
     *
     * @author
     */
    private class RechargeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<String[]> data = null;

        public RechargeAdapter(Context ctx, ArrayList<String[]> data) {
            mInflater = LayoutInflater.from(ctx);
            this.data = data;
        }

        @Override
        public int getCount() {
            return (data == null ? 0 : data.size());
        }

        @Override
        public Object getItem(int position) {
            return (data == null ? null : data.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomLog.i("kcdebug", "getview---" + data.get(position)[0]);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.vs_recharge_item, null);
                holder.charge_package_layout = (RelativeLayout) convertView.findViewById(R.id.charge_package_layout);
                holder.charge_package_name = (TextView) convertView.findViewById(R.id.charge_package_name);
                holder.charge_paytypes_img = (ImageView) convertView.findViewById(R.id.charge_paytypes_img);
                holder.charge_package_hint = (TextView) convertView.findViewById(R.id.charge_package_hint);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取数据
            final String[] cpName = data.get(position);
            holder.charge_package_name.setText(cpName[1]);
            holder.charge_paytypes_img.setVisibility(View.VISIBLE);
            if ("701".equals(cpName[2])) {// 移动卡充值
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_mobile));
                holder.charge_package_hint.setVisibility(View.GONE);
            } else if ("702".equals(cpName[2])) {// 联通卡充值
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_unicom));
                holder.charge_package_hint.setVisibility(View.GONE);
            } else if ("alipay_app".equals(cpName[2])) {// 支付宝客户端
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_alipay));
            } else if ("705".equals(cpName[2])) {// 银联充值
                holder.charge_package_hint.setVisibility(View.GONE);
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_unionpay));
            } else if ("706".equals(cpName[2])) {// 银联充值
                holder.charge_package_hint.setVisibility(View.GONE);
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_unionpay));
            } else if ("707".equals(cpName[2])) {// 支付宝网页版
                holder.charge_package_hint.setText(R.string.recharge_paytype_hinit2);
                holder.charge_paytypes_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_recharge_alipay_net));
            } else if ("weixin_app".equals(cpName[2])) {// 微信支付
                holder.charge_package_hint.setText(R.string.recharge_weixin_pay_tips);
                holder.charge_paytypes_img.setImageResource(R.drawable.vs_recharge_alipay_weixin);
            }

            // 设置监听事件
            holder.charge_package_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (VsUtil.isFastDoubleClick()) {
                        return;
                    }
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
                        VsUtil.showDialog(DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.prompt), mContext.getResources().getString(R
                                .string.login_prompt7), mContext.getResources().getString(R.string.login_prompt9), mContext.getResources().getString(R.string.login_prompt8), new
                                OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(VsRechargePayTypes.this, CaptureActivity.class);
                                intent.putExtra("code", "2");
                                startActivity(intent);

                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VsAction.insertAction(Resource.activity_2000, Resource.activity_action_005, String.valueOf(System.currentTimeMillis() / 1000), "0", mContext);
                                CustomLog.i("DGK", "mPayKind=" + cpName[2]);
                                if ("alipay_app".equals(cpName[2])) {// 支付宝充值
                                    AlipayRecharge(cpName[1], GlobalVariables.actionRechargeAlipay);
                                } else if ("707".equals(cpName[2])) {// 支付宝网页充值
                                    AlipayRecharge(cpName[1], GlobalVariables.actionRechargeWapAlipay);
                                } else if ("705".equals(cpName[2])) {// 银联充值
                                    if ("19".equals(cpName[1])) {
                                        OnlineRecharge(cpName[1], GlobalVariables.actionRechargeSDKPay);
                                    } else {
                                        OnlineRecharge(cpName[1], GlobalVariables.actionRechargeOnline);
                                    }
                                } else if ("706".equals(cpName[2])) {// 银联充值
                                    OnlineRecharge(cpName[1], GlobalVariables.actionRechargeOnline);
                                } else if ("weixin_app".equals(cpName[2])) {// 微信支付
                                    weixinPay(cpName[1], GlobalVariables.actionRechargeWeiXin);
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("mAcount", mAcount);
                                    intent.putExtra("goodsid", goodsid);
                                    intent.putExtra("mPakDesc", mPakDesc);
                                    intent.putExtra("mPromotion", mPromotion);
                                    intent.putExtra("mPrice", mPrice);
                                    intent.putExtra("mPayType", cpName[1]);
                                    intent.putExtra("mPayKind", cpName[2]);
                                    intent.putExtra("mPayTypeDesc", cpName[0]);
                                    if (operparam != null) {
                                        intent.putExtra("operparam", operparam);
                                    }
                                    VsUserConfig.cardList.clear();// 清空充值卡列表
                                    intent.setClass(mContext, VsRechargeMobileCard.class);
                                    mContext.startActivity(intent);
                                }

                            }
                        }, mContext);

                    } else {

                        VsAction.insertAction(Resource.activity_2000, Resource.activity_action_005, String.valueOf(System.currentTimeMillis() / 1000), "0", mContext);
                        CustomLog.i("DGK", "mPayKind=" + cpName[2]);
                        if ("alipay_app".equals(cpName[2])) {// 支付宝充值
                            AlipayRecharge(cpName[1], GlobalVariables.actionRechargeAlipay);
                        } else if ("707".equals(cpName[2])) {// 支付宝网页充值
                            AlipayRecharge(cpName[1], GlobalVariables.actionRechargeWapAlipay);
                        } else if ("705".equals(cpName[2])) {// 银联充值
                            if ("19".equals(cpName[1])) {
                                OnlineRecharge(cpName[1], GlobalVariables.actionRechargeSDKPay);
                            } else {
                                OnlineRecharge(cpName[1], GlobalVariables.actionRechargeOnline);
                            }
                        } else if ("706".equals(cpName[2])) {// 银联充值
                            OnlineRecharge(cpName[1], GlobalVariables.actionRechargeOnline);
                        } else if ("weixin_app".equals(cpName[2])) {// 微信支付
                            weixinPay(cpName[1], GlobalVariables.actionRechargeWeiXin);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("mAcount", mAcount);
                            intent.putExtra("goodsid", goodsid);
                            intent.putExtra("mPakDesc", mPakDesc);
                            intent.putExtra("mPromotion", mPromotion);
                            intent.putExtra("mPrice", mPrice);
                            intent.putExtra("mPayType", cpName[1]);
                            intent.putExtra("mPayKind", cpName[2]);
                            intent.putExtra("mPayTypeDesc", cpName[0]);
                            if (operparam != null) {
                                intent.putExtra("operparam", operparam);
                            }
                            VsUserConfig.cardList.clear();// 清空充值卡列表
                            intent.setClass(mContext, VsRechargeMobileCard.class);
                            mContext.startActivity(intent);
                        }
                    }

                }

            });
            return convertView;
        }
    }

    private void weixinPay(String paytype, String action) {
        // 1，去服务器生成订单号, 广播中接收结果
        OnlineRecharge(paytype, action);
    }

    private class ViewHolder {
        /**
         * item布局
         */
        private RelativeLayout charge_package_layout;
        /**
         * 充值类型提示
         */
        private TextView charge_package_hint;
        /**
         * 充值类型
         */
        private TextView charge_package_name;
        /**
         * 充值类型图标
         */
        private ImageView charge_paytypes_img;
    }

    /**
     * 支付宝请求
     *
     * @param paytype
     */
    public void AlipayRecharge(String paytype, String action) {
        loadProgressDialog("正在提交请求,请稍候...");

        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        if (null == phoneNum) {

            treeMap.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
            treeMap.put("account", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        } else {
            treeMap.put("phone", phoneNum);
            treeMap.put("account", phoneNum);
        }
        treeMap.put("pay_type", "alipay_app");
        treeMap.put("goods_id", goodsid);
        treeMap.put("amount", "1");
        treeMap.put("src", "app");
        treeMap.put("type", "money");
        treeMap.put("card_no", "");
        treeMap.put("card_pwd", "");
        treeMap.put("isNew", "y");
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_ORDER_RECHARGE, DfineAction.authType_UID, treeMap, action);
        // AlipayStartTime = System.currentTimeMillis();
    }

    /**
     * 微信支付请求
     */
    public void OnlineRecharge(String paytype, String action) {
        loadProgressDialog("正在提交请求,请稍候...");

        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        if (null == phoneNum) {

            treeMap.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
            treeMap.put("account", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        } else {
            treeMap.put("phone", phoneNum);
            treeMap.put("account", phoneNum);
        }
        treeMap.put("pay_type", "weixin_app");
        treeMap.put("goods_id", goodsid);
        treeMap.put("amount", "1");
        treeMap.put("src", "app");
        treeMap.put("type", "money");
        treeMap.put("card_no", "");
        treeMap.put("card_pwd", "");
        if (operparam != null) {
            treeMap.put("operparam", operparam);
        }
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_ORDER_RECHARGE, DfineAction.authType_UID, treeMap, action);
        // OnlineStartTime = System.currentTimeMillis();
    }

    /**
     * 得到线上充值结果
     *
     * @author: 项韬
     * @version: 2012-7-25 下午16:47:38
     */
    protected void handleKcBroadcast(Context context, Intent intent) {
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        String kcStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
        JSONObject vsData;
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        try {
            if (action.equals(GlobalVariables.actionRechargeWapAlipay)) {
                vsData = new JSONObject(kcStr);
                if (vsData.getInt("result") == 0) {
                    bundle.putString("kcStr", kcStr);
                    message.what = MSG_ID_RECHARGEWAPALIPAY_SUCCESS_MESSAGE;
                } else {
                    msgString = vsData.getString("reason");
                    message.what = MSG_ID_RECHARGEWAPALIPAY_FAILURE_MESSAGE;
                }
            } else if (action.equals(GlobalVariables.actionRechargeWeiXin)) {

                vsData = new JSONObject(kcStr);
                String kcResult = vsData.getString("code");
                JSONObject data = vsData.getJSONObject("data");
                CustomLog.i(TAG, "weixin pay result  = " + kcStr);
                if (kcResult.equals("0") && data.has("prepay_id")) {
                    String prepay_id = data.getString("prepay_id");
                    message.obj = prepay_id;
                    message.what = MSG_ID_RECHARGE_WEIXIN_SUCCESS;
                } else {
                    String msg = "服务器生成订单失败.";
                    if (vsData.has("return_msg")) {
                        msg = vsData.getString("msg");
                    }
                    message.obj = msg;
                    message.what = MSG_ID_RECHARGE_WEIXIN_FAILED;
                }

            } else {
                vsData = new JSONObject(kcStr);
                // CustomLog.i("lte", "vsData---" + vsData);
                // CustomLog.i("lte", "intent.getAction()---" + intent.getAction());
                String kcResult = vsData.getString("code");
                if (action.equals(GlobalVariables.actionRechargeAlipay)) {
//						JSONObject alipaydata = vsData.getJSONObject("data");
                    String orderInfo = vsData.getString("data");
                    dismissProgressDialog();
                    try {
                        PayUtils.getInstance(mContext).toAliPay(orderInfo, 1);
                    } catch (Exception e) {
                        String msgString = getResources().getString(R.string.request_failinfo);
                        Toast.makeText(VsRechargePayTypes.this, msgString, Toast.LENGTH_SHORT).show();
                    }

//						AlipayOrderid = alipaydata.getString("orderNo");
//						notify_url = alipaydata.getString("notifyUrl");
//						if (notify_url == null || notify_url.length() == 0) {
//							msgString = getResources().getString(R.string.request_failinfo);
//							message.what = MSG_SHOW_HANDLE2;
//						} else {
//							message.what = MSG_ID_RechargeAlipay_Success_Message;
//						}
                } else {
                    if (kcResult.equals("-99")) {
                        dismissProgressDialog();
                        if (!VsUtil.isCurrentNetworkAvailable(mContext)) return;
                    }
                    String wrongResult = vsData.getString("msg");
                    if (wrongResult != null) {
                        msgString = wrongResult.toString();
                    }
                    message.what = MSG_SHOW_HANDLE2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msgString = getResources().getString(R.string.request_failinfo);
            message.what = MSG_SHOW_HANDLE2;
        }

        bundle.putString("msgString", msgString);
        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    private static final Pattern LINE_PATTERN = Pattern.compile("^(\\w+)=(\\w*)$");

    @SuppressWarnings("deprecation")
    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        dismissProgressDialog();
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        msgString = msg.getData().getString("msgString");
        switch (msg.what) {
            case SDK_PAY_FLAG: {
//			PayResult payResult = new PayResult((String) msg.obj);
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    Toast.makeText(VsRechargePayTypes.this, "支付成功", Toast.LENGTH_SHORT).show();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(VsRechargePayTypes.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(VsRechargePayTypes.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case SDK_CHECK_FLAG: {
                break;
            }
            case MSG_SHOW_HANDLE1:
                dismissProgressDialog();
                break;
            case MSG_SHOW_HANDLE2:
                dismissProgressDialog();
                // showMessageDialog(R.string.lb_alter, msgString, 0, null, mContext, "确定");
                showMessageDialog(getResources().getString(R.string.lb_alter), msgString, true);
                break;
//            case MSG_ID_RechargeAlipay_Success_Message:
//                dismissProgressDialog();
//                try {
//                    String body = "充值";
//                    String orderInfo = getOrderInfo(AlipayOrderid, body, mPrice);
//                    // 对订单做RSA 签名
//                    String sign = sign(orderInfo);
//                    try {
//                        // 仅需对sign 做URL编码
//                        sign = URLEncoder.encode(sign, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//                    // 完整的符合支付宝参数规范的订单信息
//                    final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//
//                    check();
//
//                    // 调用pay方法进行支付
//                    Runnable payRunnable = new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // 构造PayTask 对象
//                            PayTask alipay = new PayTask(VsRechargePayTypes.this);
//
//                            Map<String, String> result = alipay.payV2(payInfo, true);
//                            // 调用支付接口，获取支付结果
////						String result = alipay.pay(payInfo);
//
//                            Message msg = new Message();
//                            msg.what = SDK_PAY_FLAG;
//                            msg.obj = result;
//                            mBaseHandler.sendMessage(msg);
//                        }
//                    };
//
//                    // 必须异步调用
//                    Thread payThread = new Thread(payRunnable);
//                    payThread.start();
//
//                    VsUserConfig.isChangeBalance = true;
//                    VsUserConfig.changeBalanceTime = System.currentTimeMillis();
//                } catch (Exception e) {
//                    msgString = getResources().getString(R.string.request_failinfo);
//                    bundle.putString("msgString", msgString);
//                    message.what = MSG_SHOW_HANDLE2;
//                }
//                message.setData(bundle);
//                mBaseHandler.sendMessage(message);
//                break;
            case MSG_ID_RECHARGEWAPALIPAY_SUCCESS_MESSAGE:
                try {
                    String kcStr = msg.getData().getString("kcStr");
                    JSONObject jsonData = (new JSONObject(kcStr)).getJSONObject("epayresult");
                    String url = jsonData.getString("url");
                    JSONArray jsonArray = jsonData.getJSONArray("tags");
                    TreeMap<String, String> treeMap = new TreeMap<String, String>();
                    Object gObjtemp, name, value;
                    if (jsonArray != null) {
                        int a = 0;
                        int len = jsonArray.length();
                        while (a < len && (gObjtemp = jsonArray.get(a)) != null) {
                            a++;
                            JSONObject mJSONObject = (JSONObject) gObjtemp;
                            if (mJSONObject != null) {
                                name = mJSONObject.get("name");
                                value = mJSONObject.get("value");
                                if (name != null && value != null) {
                                    treeMap.put(name.toString(), value.toString());
                                }
                            }
                        }
                    }
                    url = url + CoreBusiness.getInstance().enmurParse(treeMap);
                    CustomLog.i("GDK", "url=" + url);
                    Intent intent = new Intent();
                    intent.putExtra("AboutBusiness", new String[]{"", "aplpay", url});
                    intent.setClass(mContext, WeiboShareWebViewActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_ID_RECHARGEWAPALIPAY_FAILURE_MESSAGE:
                mToast.show(msgString, Toast.LENGTH_SHORT);
                break;
            case MSG_ID_RECHARGE_WEIXIN_FAILED:
                String errorStr = (String) msg.obj;
                Intent intent = new Intent(mContext, WXPayEntryActivity.class);
                intent.putExtra("message", TextUtils.isEmpty(errorStr) ? "" : errorStr);
                mContext.startActivity(intent);
                break;
            case MSG_ID_RECHARGE_WEIXIN_SUCCESS:
                String prepayId = (String) msg.obj;
                IWXAPI api = WXAPIFactory.createWXAPI(mContext, DfineAction.WEIXIN_APPID, true);
                PayReq payReq = new PayReq();
                payReq.appId = DfineAction.WEIXIN_APPID;
                // payReq.extData = DfineAction.brandid;//不能添加该字段， 添加后调转不到微信客户端
                payReq.nonceStr = genNonceStr();
                payReq.packageValue = "Sign=WXPay";// "prepay_id=" + prepayId;//拓展字段， 官网说是暂填写固定值Sign=WXPay，
                // 但是Demo中是这样写的"prepay_id=" + prepayId;。[Demo中的不对]
                payReq.partnerId = DfineAction.WEIXIN_MCH_ID;// 微信支付分配的商户号
                payReq.prepayId = prepayId;// 微信返回的支付交易会话ID
                payReq.timeStamp = "" + (System.currentTimeMillis() / 1000);// 需转换为秒
                payReq.sign = getWeiXinPaySign(payReq);
                api.registerApp(DfineAction.WEIXIN_APPID);
                boolean b = api.sendReq(payReq);
                if (!b) {
                    Intent i = new Intent(mContext, WXPayEntryActivity.class);
                    i.putExtra("message", "未安装微信客户端");
                    mContext.startActivity(i);
                }
                break;
            default:
                break;
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String getWeiXinPaySign(PayReq payReq) {
        if (null == payReq) {
            return null;
        }

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();

        if (!TextUtils.isEmpty(payReq.appId)) {
            signParams.add(new BasicNameValuePair("appid", payReq.appId));
        }
        if (!TextUtils.isEmpty(payReq.extData)) {
            signParams.add(new BasicNameValuePair("extdata", payReq.extData));
        }
        if (!TextUtils.isEmpty(payReq.nonceStr)) {
            signParams.add(new BasicNameValuePair("noncestr", payReq.nonceStr));
        }
        if (!TextUtils.isEmpty(payReq.packageValue)) {
            signParams.add(new BasicNameValuePair("package", payReq.packageValue));
        }
        if (!TextUtils.isEmpty(payReq.partnerId)) {
            signParams.add(new BasicNameValuePair("partnerid", payReq.partnerId));
        }
        if (!TextUtils.isEmpty(payReq.prepayId)) {
            signParams.add(new BasicNameValuePair("prepayid", payReq.prepayId));
        }
        if (!TextUtils.isEmpty(payReq.timeStamp)) {
            signParams.add(new BasicNameValuePair("timestamp", payReq.timeStamp));
        }
//        if (!TextUtils.isEmpty(payReq.sign)) {
//            signParams.add(new BasicNameValuePair("sign", payReq.sign));
//        }

        return genAppSign(signParams);
    }

    /**
     * 生成微信支付签名
     *
     * @param params
     * @return
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(DfineAction.WEIXIN_API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }

    /**
     * create the order info. 创建订单信息
     */
    public String orderSubject = "充值";

//    public String getOrderInfo(String OutTradeNo, String body, String price) {
//        float payMoney = Float.parseFloat(mPrice) / 10000;
//
//        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + DfineAction.RES.getString(R.string.PARTNER) + "\"";
//
//        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + DfineAction.RES.getString(R.string.orderSeller) + "@" + DfineAction.RES.getString(R.string.endSeller) + "\"";
//
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" + OutTradeNo + "\"";
//
//        // 商品名称
//        orderInfo += "&subject=" + "\"" + orderSubject + "\"";
//
//        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";
//
//        // 商品金额
//        orderInfo += "&total_fee=" + "\"" + payMoney + "\"";
//
//        // 服务器异步通知页面路径
//        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";
//
//        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
//
//        // 参数编码， 固定值
//        orderInfo += "&_input_charset=\"utf-8\"";
//
//        // 设置未付款交易的超时时间
//        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//        // 取值范围：1m～15d。
//        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//        orderInfo += "&it_b_pay=\"30m\"";
//
//        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
//        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
//
//        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";
//
//        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//        // orderInfo += "&paymethod=\"expressGateway\"";
//
//        return orderInfo;
//    }

    /**
     * check whether the device has authentication alipay account. 查询终端设备是否存在支付宝认证账户
     */
    public void check() {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(VsRechargePayTypes.this);
//				// 调用查询接口，获取查询结果
//				boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mBaseHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();

    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
//    public String sign(String content) {
//        return SignUtils.sign(content, DfineAction.RES.getString(R.string.RSA_PRIVATE));
//    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    @Override
    protected void onDestroy() {
        // if (upmopbutton != null) {
        // unregisterReceiver(upmopbutton);
        // }
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        CustomLog.i(TAG, "onActivityResult(), ...requestCode = " + requestCode + ", resultCode = " + resultCode + ", data is null ? " + (data == null));

        if (data != null) {

            handlerAPKPayResult(data);

            Bundle bundle = data.getExtras();
            byte[] xml = bundle.getByteArray("xml");
            // 自行解析并输出
            String Sxml;
            try {
                Sxml = new String(xml, "utf-8");

                CustomLog.i(TAG, "onActivityResult(), " + Sxml);

                ByteArrayInputStream tInputStringStream = null;

                try {
                    if (Sxml != null && !Sxml.trim().equals("")) {
                        tInputStringStream = new ByteArrayInputStream(Sxml.getBytes());
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    return;
                }
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(tInputStringStream, "UTF-8");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                                // persons = new ArrayList<Person>();
                                break;
                            case XmlPullParser.START_TAG:// 开始元素事件
                                String name = parser.getName();
                                if (name.equalsIgnoreCase("respCode")) {
                                    String respCode = parser.nextText();
                                    if (respCode.equals("0000")) {
                                        // 首次充值成功标识
                                        VsUserConfig.setData(mContext, VsUserConfig.JKey_FirstRechargeState, true);
                                        // 首次银联充值成功标识
                                        VsUserConfig.setData(mContext, VsUserConfig.JKey_FirstUpompRechargeState, true);
                                    }
                                }
                                break;
                            case XmlPullParser.END_TAG:// 结束元素事件
                                break;
                        }
                        eventType = parser.next();
                    }
                    tInputStringStream.close();
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }
        }
    }

    private void handlerAPKPayResult(Intent data) {
        String msg = "";
        /*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");

        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！请稍后查看话费余额。";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        // builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void setResult() {
        if (operparam != null) {
            Intent intent = new Intent();
            intent.putExtra("callback", callback);
            intent.putExtra("callbacktype", callbacktype);
            setResult(1, intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult();
        }
        return super.onKeyDown(keyCode, event);
    }
}