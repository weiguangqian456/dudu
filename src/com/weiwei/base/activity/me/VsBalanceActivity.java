package com.weiwei.base.activity.me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @Title:Android客户端
 * @Description: 查询余额
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsBalanceActivity extends VsBaseActivity implements OnClickListener {
    private ListView mBytcOtherInfoListview = null;
    private RechargeMealAdapter adapter = null;
    /**
     * 帐号余额
     */
    private TextView balance_money;
    /**
     * 余额有效期
     */
    private TextView balance_time;
    /**
     * 可拨打分钟数
     */
    private TextView vs_balance_money_minute;
    /**
     * 查询话单
     */
    private LinearLayout vs_balance_expend;
    /**
     * 收支明细
     */
    private LinearLayout vs_balance_income;
    /**
     * 套餐信息
     */
    private TextView vs_balance_taocan;
    /**
     * 查询余额成功
     */
    public final char MSG_ID_CheckBalanceSucceed = 90;

    /**
     * 查询余额失败
     */
    private final char MSG_ID_CheckBalanceFail = 91;
    /**
     * 查询通话时间成功
     */
    public final char MSG_ID_CheckCallTimeSucceed = 92;
    /**
     * 查询通话时间失败
     */
    public final char MSG_ID_CheckCallTimeFail = 93;
    /**
     * 套餐布局
     */
    public RelativeLayout balance_combo_layout;

    private RoundTextView btn_recharge;
    //基本
    private TextView balance_money_base;

    //赠送
    private TextView balance_money_gif;

    /*
     * (non-Javadoc)
     * 
     * @see com.weiwei.base.activity.KcBaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_account_balance);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(R.string.my_tv1);
        showLeftNavaBtn(R.drawable.icon_back);
        // showLeftTxtBtn(getResources().getString(R.string.account_title));
        showRightTxtBtn(getResources().getString(R.string.balance_combo_Tariff));
        initView();
        initData();
        VsApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBytcOtherInfoListview = (ListView) findViewById(R.id.bytc_another_info);
        // 获取控件对象
        balance_money = (TextView) findViewById(R.id.balance_money);
        balance_time = (TextView) findViewById(R.id.balance_time);
        vs_balance_money_minute = (TextView) findViewById(R.id.vs_balance_money_minute);
        vs_balance_expend = (LinearLayout) findViewById(R.id.vs_balance_expend);
        vs_balance_income = (LinearLayout) findViewById(R.id.vs_balance_income);
        vs_balance_taocan = (TextView) findViewById(R.id.vs_balance_taocan);
        btn_recharge = (RoundTextView) findViewById(R.id.btn_recharge);
        balance_money_base = (TextView) findViewById(R.id.balance_money_base);
        balance_money_gif = (TextView) findViewById(R.id.balance_money_gif);
        // 设置监听事件
        vs_balance_expend.setOnClickListener(this);
        vs_balance_income.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        balance_money.setText("查询中");
        balance_time.setText("");
        // 查余额
        searchBalance();
        // 查月通话时间、结余、套餐
        searchCallTime();
    }

    @Override
    protected void HandleRightNavBtn() {
        // TODO Auto-generated method stub
        super.HandleRightNavBtn();
        VsUtil.startActivity("3015", mContext, null);
    }

    /**
     * 查询余额
     */
    private void searchBalance() {
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.actionSeaRchbalance);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", VsUserConfig.getDataString(mContext,
                VsUserConfig.JKey_KcId));
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_BALANCE, DfineAction.authType_UID, treeMap, GlobalVariables.actionSeaRchbalance);
    }

    /**
     * 查询通话时间与结余广播
     */
    private BroadcastReceiver searchCallTimeBroad = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
            Message message = mBaseHandler.obtainMessage();
            Bundle bundle = new Bundle();
            try {
                JSONObject jData = new JSONObject(jStr);
                String retStr = jData.getString("result");
                if ("0".equals(retStr)) {

                    bundle.putString("minutes", jData.getString("minutes") + "分钟");
                    if (jData.has("save")) {
                        bundle.putString("save", (Double.valueOf(jData.getString("save"))) + "元");
                    } else {
                        bundle.putString("save", "0元");
                    }

                    message.what = MSG_ID_CheckCallTimeSucceed;
                } else {
                    if ("-99".equals(retStr)) {
                        if (!VsUtil.isCurrentNetworkAvailable(mContext))
                            return;
                    }
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_CheckCallTimeFail;
                }
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString("msg", getResources().getString(R.string.servicer_busying));
                message.what = MSG_ID_CheckCallTimeFail;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        }
    };

    /**
     * 查询通话时长与结余
     */
    private void searchCallTime() {
        IntentFilter filter = new IntentFilter(GlobalVariables.actionSearchCallTime);

        registerReceiver(searchCallTimeBroad, filter);
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_SEACHE_CALLTIME, DfineAction.authType_UID, null, GlobalVariables.actionSearchCallTime);
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_balance_expend://话单
                VsUtil.startActivity("3024", mContext, null);
                break;
            case R.id.vs_balance_income://话费
                if (!VsUtil.isNoNetWork(mContext)) {
                    Intent intent = new Intent();
                    String urlTo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ACCOUNT_DETAILS);
                    String[] aboutBusiness = new String[3];
                    aboutBusiness[0] = getResources().getString(R.string.vs_balance_income);
                    aboutBusiness[1] = "false";
                    aboutBusiness[2] = urlTo;
                    intent.putExtra("AboutBusiness", aboutBusiness);
                    intent.setClass(mContext, WeiboShareWebViewActivity.class);
                    CustomLog.i("url", "url=" + urlTo);
                    startActivity(intent);
                } else {
                    mToast.show(getResources().getString(R.string.not_network_connon_msg), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.btn_recharge:
                startActivity(mContext, VsRechargeActivity.class);
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.weiwei.base.activity.KcBaseActivity#handleBaseMessage(android.os.Message)
     */
    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_CheckBalanceSucceed:

                Bundle bbundle = msg.getData();
                balance_money.setText(Html.fromHtml("总账户：" + "<font color='#1fa709'>" + bbundle.getString("balance") + "</font>" + "元"));
                balance_money_base.setText(Html.fromHtml("账户余额：" + "<font color='#1fa709'>" + bbundle.getString("basicbalance") + "</font>" + "元"));
                balance_money_gif.setText(Html.fromHtml("赠送账户：" + "<font color='#1fa709'>" + bbundle.getString("giftbalance") + "</font>" + "元"));

                //修改字体颜色
//            SpannableStringBuilder builder = new SpannableStringBuilder(balance_money.getText().toString());
//            SpannableStringBuilder builderbase = new SpannableStringBuilder(balance_money_base.getText().toString());
//            SpannableStringBuilder buildergif = new SpannableStringBuilder(balance_money_gif.getText().toString());
//            
//            builder.setSpan(new ForegroundColorSpan(getResources().getColor(color.money_text_color)), 4, 9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builderbase.setSpan(new ForegroundColorSpan(getResources().getColor(color.money_text_color)), 5, 10,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            buildergif.setSpan(new ForegroundColorSpan(getResources().getColor(color.money_text_color)), 5, 9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            
//            balance_money.setText(builder);
//            balance_money_base.setText(builderbase);
//            balance_money_gif.setText(buildergif);

                //vs_balance_money_minute.setText("约" + bbundle.getString("calltime") + "分钟");
                balance_time.setText(bbundle.getString("valid_date"));
                int moneybase = Integer.parseInt(bbundle.getString("calltime"));
                int moneybao = 0;
                int moneyToal = 0;
                try {
                    // 包邮套餐处理
                    int i = 0;
                    int len = bbundle.getInt("packageNumber");
                    ArrayList<String> mNameData = new ArrayList<String>();
                    ArrayList<String> mInfoData = new ArrayList<String>();
                    while (i < len) {
                        if (i == 0) {
                            vs_balance_taocan.setVisibility(View.GONE);
                            vs_balance_taocan.setText(bbundle.getString("packagename" + i));
                        }
                        mNameData.add(bbundle.getString("packagename" + i));
                        if (bbundle.getString("call_time" + i).equals("-1")) {
                            mInfoData.add("开始时间:" + bbundle.getString("eff_time" + i) + "\n到期时间:" + bbundle.getString("exp_time" + i));
                        } else {
                            mInfoData.add("开始时间:" + bbundle.getString("eff_time" + i) + "\n到期时间:" + bbundle.getString("exp_time" + i) + "\n剩余时长:" + bbundle.getString("call_time" + i) + "分钟");
                        }
                        moneybao = moneybao + Integer.parseInt(bbundle.getString("call_time" + i));
                        i++;
                        // mNameData.add("测试一下");
                        // mInfoData.add("开始时间:" + "xxxx:xxx:xxx" + "\n到期时间:" + "xxxx:xxxx:xxxx" + "\n剩余时长:" + "1000" + "分钟");
                        // i++;
                    }
                    if (i > 0)

                        adapter = new RechargeMealAdapter(mContext);
                    adapter.setData(mNameData, mInfoData);
                    mBytcOtherInfoListview.setAdapter(adapter);
                    vs_balance_taocan.setOnClickListener(new mOnClickListener("包月信息", mNameData, mInfoData));

                    // listiew适配器
                    // ArrayList<String> taocan = new ArrayList<String>();
                    // taocan = bbundle.getStringArrayList("taocan");
                    // if (taocan != null && taocan.size() > 0) {
                    // balance_combo_layout.setVisibility(View.VISIBLE);
                    // ComboAdapter adapter = new ComboAdapter(mContext, taocan);
                    // balance_combo_listview.setAdapter(adapter);
                    // }
                    moneyToal = moneybao + moneybase;
                } catch (Exception e) {
                    // TODO: handle exception
                }
                vs_balance_money_minute.setText("(约" + moneyToal + "分钟)");
                break;
            case MSG_ID_CheckBalanceFail:
                balance_money.setText("0");
                balance_time.setText("");
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
                break;
            case MSG_ID_CheckCallTimeSucceed:
                break;
            case MSG_ID_CheckCallTimeFail:
                mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }

    private class mOnClickListener implements View.OnClickListener {
        private String title = null;
        private ArrayList<String> nameData = null;
        private ArrayList<String> infoData = null;

        public mOnClickListener(String title, ArrayList<String> nameData, ArrayList<String> infoData) {
            this.title = title;
            this.nameData = nameData;
            this.infoData = infoData;
        }

        @Override
        public void onClick(View v) {
            if (VsNetWorkTools.isNetworkAvailable(mContext)) {
                Intent intent = new Intent(mContext, VsRechargeMealActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("mNameData", nameData);
                intent.putExtra("mInfoData", infoData);
                startActivity(intent);
            } else {
                mToast.show(getResources().getString(R.string.not_network_connon_msg), Toast.LENGTH_SHORT);
            }
        }
    }

    ;

    /*
     * (non-Javadoc)
     * 
     * @see com.weiwei.base.activity.KcBaseActivity#handleKcBroadcast(android.content.Context, android.content.Intent)
     */
    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.handleKcBroadcast(context, intent);

        String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
        Message message = mBaseHandler.obtainMessage();
        Bundle bundle = new Bundle();
        try {
            JSONObject getjson = new JSONObject(jStr);
            String retStr = getjson.getString("code");
            JSONObject jData = getjson.getJSONObject("data");

            if (retStr.equals("0")) {
                // 基本账户余额
                String basicbalance = jData.getString("basic_balance");
                bundle.putString("basicbalance", basicbalance);
                // 账户总余额
                String balance = jData.getString("balance");
                bundle.putString("balance", balance);
                // 有效期
                String valid_date = jData.getString("expire_time");
                bundle.putString("valid_date", valid_date);

                // 赠送余额
                String giftbalance = jData.getString("gift_balance");
                bundle.putString("giftbalance", giftbalance);

                VsUserConfig.setData(mContext, VsUserConfig.JKey_Balance, balance);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_BasicBalance, basicbalance);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_GiftBalance, giftbalance);

                // 包月套餐信息列表
                JSONArray mJSONArray = jData.getJSONArray("packagelist");
                int i = 0;
                int len = mJSONArray.length();
                JSONObject jObj = null;
                while (i < len && (jObj = (JSONObject) mJSONArray.get(i)) != null) {
                    retStr = jObj.getString("packagename");// 包月名称
                    if (retStr.equals(""))
                        retStr.replace(getResources().getString(R.string.call_back), "");
                    bundle.putString("packagename" + i, retStr);
                    bundle.putString("call_time" + i, jObj.getString("call_time"));// 包月剩余时间
                    bundle.putString("eff_time" + i, jObj.getString("eff_time"));// 包月开始时间
                    bundle.putString("exp_time" + i, jObj.getString("exp_time"));// 包月到期时间
                    i++;
                }
                bundle.putInt("packageNumber", i);
                message.what = MSG_ID_CheckBalanceSucceed;
            } else {
                if (retStr.equals("-99")) {
                    dismissProgressDialog();
                    if (!VsUtil.isCurrentNetworkAvailable(mContext))
                        return;
                }
                bundle.putString("msg", jData.getString("reason"));
                message.what = MSG_ID_CheckBalanceFail;
            }

        } catch (Exception e) {
            e.printStackTrace();
            bundle.putString("msg", getResources().getString(R.string.servicer_busying));
            message.what = MSG_ID_CheckBalanceFail;
        }
        message.setData(bundle);
        mBaseHandler.sendMessage(message);
    }

    /**
     * 套餐列表适配器
     *
     * @author 李志
     */
    // class ComboAdapter extends BaseAdapter {
    // private LayoutInflater mInflater;
    // private ArrayList<String> list = new ArrayList<String>();
    //
    // public ComboAdapter(Context context, ArrayList<String> arraylist) {
    // mInflater = LayoutInflater.from(context);
    // this.list = arraylist;
    // CustomLog.i("kcdebug", "---" + list.size() / 4);
    // }
    //
    // @Override
    // public int getCount() {
    // // TODO Auto-generated method stub
    // return (list.size() / 4);
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // // TODO Auto-generated method stub
    // return list.get(position) + list.get(position + 1) + list.get(position + 2) + list.get(position + 3);
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // // TODO Auto-generated method stub
    // return position;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // // TODO Auto-generated method stub
    // ViewHolder holder = null;
    // CustomLog.i("kcdebug", "getview-----------runing---" + position);
    // if (convertView == null) {
    // holder = new ViewHolder();
    // convertView = mInflater.inflate(R.layout.kc_account_balance_item, null);
    // holder.balance_combo_name = (TextView) convertView.findViewById(R.id.balance_combo_name);
    // holder.balance_combo_calltime = (TextView) convertView.findViewById(R.id.balance_combo_calltime);
    // holder.balance_combo_starttime = (TextView) convertView.findViewById(R.id.balance_combo_starttime);
    // holder.balance_combo_endtime = (TextView) convertView.findViewById(R.id.balance_combo_endtime);
    // convertView.setTag(holder);
    // } else {
    // holder = (ViewHolder) convertView.getTag();
    // }
    // if (list.size() > 3) {
    // if (position != 0) {
    // position = position * 4;
    // }
    // CustomLog.i("kcdebug", list.get(position) + "---" + position);
    // holder.balance_combo_name.setText(list.get(position));
    // holder.balance_combo_calltime.setText(list.get(position + 1) + "分钟");
    // holder.balance_combo_starttime.setText(list.get(position + 2));
    // holder.balance_combo_endtime.setText(list.get(position + 3));
    // }
    // return convertView;
    // }
    //
    // private class ViewHolder {
    // /**
    // * 套餐名称
    // */
    // private TextView balance_combo_name;
    // /**
    // * 剩余时长
    // */
    // private TextView balance_combo_calltime;
    // /**
    // * 生效时间
    // */
    // private TextView balance_combo_starttime;
    // /**
    // * 到期时间
    // */
    // private TextView balance_combo_endtime;
    // }
    // }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            unregisterReceiver(searchCallTimeBroad);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private class RechargeMealAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<String> nameData = null;
        private ArrayList<String> infoData = null;

        public RechargeMealAdapter(Context ctx) {
            mInflater = LayoutInflater.from(ctx);
        }

        public void setData(ArrayList<String> nameData, ArrayList<String> infoData) {
            this.nameData = nameData;
            this.infoData = infoData;
        }

        @Override
        public int getCount() {
            return (nameData == null ? 0 : nameData.size());
        }

        @Override
        public Object getItem(int position) {
            return (nameData == null ? null : nameData.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MealViewHolder holder = null;
            if (convertView == null) {
                holder = new MealViewHolder();
                convertView = mInflater.inflate(R.layout.vs_charge_meal_item, null);
                holder.mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.mInfoTv = (TextView) convertView.findViewById(R.id.info_tv);
                convertView.setTag(holder);
            } else {
                holder = (MealViewHolder) convertView.getTag();
            }
            holder.mNameTv.setText(nameData.get(position));
            holder.mInfoTv.setText(infoData.get(position));
            return convertView;
        }
    }

    private class MealViewHolder {
        private TextView mNameTv, mInfoTv;
    }
}
