package com.weiwei.base.activity.me;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hwtx.dududh.R;
import com.weiwei.account.ConsumptionRecordsEntity;
import com.weiwei.account.RedBagEntity;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsMyRedAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.CircleImageView;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.SpUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/10/9.
 *         Describe 我的红白界面
 */

public class VsMyRedPagActivity extends VsBaseActivity implements OnClickListener, OnRefreshListener2<ListView> {
    private TextView myBalancRedpag, red_meth, redlistback;
    private Button myBalancChargeBtn;
    private PullToRefreshListView mList;
    private String uid;
    private String money, status;

    private ListView lv_cur_list;
    private VsMyRedAdapter adapter;
    private String endTime;
    private String startTime = "2015-01-01";
    private SimpleDateFormat sDateFormat;
    private int flag = 0;
    private Boolean upFlag = false;
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private Boolean weixinFlag = false;
    private CircleImageView header_img;
    private int page = 1;
    boolean isLoading = false;

    /**
     * 界面标志
     */
    private String uiFlag;
    private TextView detailTv;
    private TextView callmoney;
    private String invitation;

    private boolean isRegister = false;

    private static final String TAG = "VsMyRedPagActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_myredpag);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
//		showRightTxtBtn("充值卡");
        init();
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
        money = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Money, "0.0");
        status = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Status, "n");
        uiFlag = getIntent().getStringExtra("uiFlag");
        if (!StringUtils.isEmpty(uiFlag)) {
            SpUtils.putStringValue(this, "uiFlag", uiFlag);
            switch (uiFlag) {
                case "redbag":
                    mTitleTextView.setText("我的红包");
                    detailTv.setText("待提现成长金");
                    myBalancRedpag.setText(money);
                    callmoney.setText("成长金记录");
                    IntentFilter mycalllogFilter = new IntentFilter();
                    mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_RED_LOG);
                    mContext.registerReceiver(mycalllogReceiver, mycalllogFilter);
                    isRegister = true;
                    break;
                case "shareRedbag":
                    mTitleTextView.setText("我的红包");
                    detailTv.setText("待提现红包");
                    callmoney.setText("成长金记录");
                    getShareRedBag();
                    getShareRedbagRecords();
                    break;
                default:
                    break;
            }
        }

        //status表示红包冻结状态   红包账户状态(y：正常   n：冻结)
        redlistback.setVisibility(View.VISIBLE);

        IntentFilter mybankFilter = new IntentFilter();
        mybankFilter.addAction(VsUserConfig.JKey_GET_MY_BANK);
        mContext.registerReceiver(mybankReceiver, mybankFilter);

        //计算日期
        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        endTime = sDateFormat.format(new java.util.Date());
//		Calendar c = Calendar.getInstance();
//		int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ;
//		c.set(Calendar.DAY_OF_MONTH,startMonth);
//       startTime = sDateFormat.format(c.getTime());

        getMyRed(uid, startTime, endTime, page + "");
        getBank();
        VsApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!StringUtils.isEmpty(uiFlag)) {
            switch (uiFlag) {
                case "shareRedbag":
                    getShareRedBag();
                    page = 1;
                    getShareRedbagRecords();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        if (mycalllogReceiver != null && isRegister) {
            unregisterReceiver(mycalllogReceiver);
            isRegister = false;
            mycalllogReceiver = null;
        }
        if (mybankReceiver != null) {
            unregisterReceiver(mybankReceiver);
            mybankReceiver = null;
        }
        super.onStop();
    }

    /**
     * 获取分享红包提现记录
     */
    private void getShareRedbagRecords() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", page + "");
        params.put("pageSize", "10");
        retrofit2.Call<ResultEntity> call = api.getShareRedBagRecord(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    List<ConsumptionRecordsEntity> recordsEntityList = JSON.parseArray(result.getData().toString(), ConsumptionRecordsEntity.class);
                    if (!upFlag && list.size() != 0) {
                        list.clear();
                    }
                    for (int i = 0; i < recordsEntityList.size(); i++) {
                        Map<String, Object> map = new TreeMap<String, Object>();
                        map.put("amount", recordsEntityList.get(i).getAmount());
                        map.put("remark", recordsEntityList.get(i).getRemark());
                        map.put("ctime", recordsEntityList.get(i).getCtime());
                        map.put("status", recordsEntityList.get(i).getStatus());
                        list.add(map);
                    }
                    isLoading = false;
                    adapter.notifyDataSetChanged();
                    mList.onRefreshComplete();
                    if (list.size() == 0) {
                        redlistback.setVisibility(View.VISIBLE);
                    } else {
                        redlistback.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(VsMyRedPagActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    protected void HandleRightNavBtn() {
        startActivity(new Intent(this, VsChangeListActivity.class));
    }

    private void init() {
        myBalancRedpag = (TextView) findViewById(R.id.myBalancRedpag);
        red_meth = (TextView) findViewById(R.id.red_meth);
        redlistback = (TextView) findViewById(R.id.redlistback);
        myBalancChargeBtn = (Button) findViewById(R.id.myBalancChargeBtn);

        detailTv = (TextView) findViewById(R.id.mydetail);
        callmoney = (TextView) findViewById(R.id.callmoney);

        mList = (PullToRefreshListView) findViewById(R.id.mList);
        mList.setMode(Mode.BOTH);
        mList.setOnRefreshListener(this);
        //	mList.setRefreshing(true);
        lv_cur_list = mList.getRefreshableView();
        header_img = (CircleImageView) findViewById(R.id.myself_head);
        String path = "";
        if (uid != null && uid.length() > 0) {
            path = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_photo, "");
        }

        if (fileIsExists(path) && path.contains(uid)) {
            header_img.setImageBitmap(BitmapFactory.decodeFile(path));
        }

        adapter = new VsMyRedAdapter(VsMyRedPagActivity.this, list);
        lv_cur_list.setAdapter(adapter);
        lv_cur_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            }
        });

        lv_cur_list.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount < visibleItemCount) {
                    mList.setMode(Mode.PULL_FROM_START);
                } else {
                    mList.setMode(Mode.BOTH);
                }
            }
        });

        red_meth.setOnClickListener(this);
        myBalancChargeBtn.setOnClickListener(this);
    }

    private void getBank() {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", uid);
        CoreBusiness.getInstance().startThread(getApplicationContext(), GlobalVariables.GetMyBank, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyBank);
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.red_meth:
                startActivity(mContext, VsredinfoActivity.class);
                break;
            case R.id.myBalancChargeBtn:
                if (weixinFlag) {
                    String[] key = new String[]{"uiFlag", "invitation"};
                    String[] value = new String[]{uiFlag, invitation};
                    SkipPageUtils.getInstance(mContext).skipPage(VsMyRedPopActivity.class, key, value);
                } else {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        mToast.show("未安装微信", Toast.LENGTH_SHORT);
                    }
                }
                //提现
//	        	showPopWindow(mContext, v);
//	        	startActivity(mContext, VsUninopayBindActivity.class);
                break;
            case R.id.red_recharge://提现
//	        	Intent intent = new Intent(mContext, VsRedbageActivity.class);
//	        	 intent.putExtra("bind_wx", bind_wx);
//	            startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (VsUtil.isFastDoubleClick()) {
            Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
            mList.onRefreshComplete();
            return;
        }
        upFlag = false;
        String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils
                .FORMAT_ABBREV_ALL);
        // 显示最后更新的时间
        mList.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        flag = 0;
        endTime = sDateFormat.format(new java.util.Date());
//			Calendar c = Calendar.getInstance();
//			int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ; 
//			c.set(Calendar.DAY_OF_MONTH,startMonth);    
//	        startTime = sDateFormat.format(c.getTime());
////	        long i = c.getTimeInMillis();
        page = 1;
        switch (uiFlag) {
            case "redbag":
                getMyRed(uid, startTime, endTime, page + "");
                break;
            case "shareRedbag":
                getShareRedbagRecords();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (VsUtil.isFastDoubleClick()) {
            Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
            mList.onRefreshComplete();
            return;
        }
        upFlag = true;
        String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils
                .FORMAT_ABBREV_ALL);
        // 显示最后更新的时间
        mList.getLoadingLayoutProxy().setLastUpdatedLabel(label);

        switch (uiFlag) {
            case "redbag":
                flag++;
                endTime = sDateFormat.format(new java.util.Date());
                page++;
                getMyRed(uid, startTime, endTime, page + "");
                break;
            case "shareRedbag":
                flag++;
                page++;
                isLoading = true;
                getShareRedbagRecords();
                break;
            default:
                break;
        }
    }

    private void getMyRed(String uid, String startTime, String endTime, String page) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", uid);
        treeMap.put("sdate", startTime);
        treeMap.put("edate", endTime);
        treeMap.put("rows", "10");
        treeMap.put("page", page);
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyRedLog, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyRedLog);
    }

    private void getShareRedBag() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getWallet();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "我的分享红包msg===>" + result.getData().toString());
                    RedBagEntity redBagEntity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
                    invitation = redBagEntity.getInvitation();
                    if (!StringUtils.isEmpty(invitation)) {
                        myBalancRedpag.setText(invitation);
                    }
                } else {
                    Toast.makeText(VsMyRedPagActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private BroadcastReceiver mybankReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String total = bundle.get("data").toString();
            try {
                JSONObject js = new JSONObject(total);
                if (js.get("wx_id").toString().length() > 0) {
                    myBalancChargeBtn.setText("提现");
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_Weixin, js.get("wx_id").toString());
                    weixinFlag = true;
                } else {
                    myBalancChargeBtn.setText("未绑定微信号");
                    weixinFlag = false;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    };


    private BroadcastReceiver mycalllogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            JSONObject js;
            try {
                if (bundle.get("list").toString().contains("频繁")) {
                    Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
                    mList.onRefreshComplete();
                    return;
                }
                js = new JSONObject(bundle.get("list").toString());
                JSONArray ja = js.getJSONArray("items");
                if (!upFlag && list.size() != 0) {
                    list.clear();
                }
//				amount":20000,"id":5,"action":"split","remark":"18576477213用户充值10.00元",
// "app_id":"wind"," +
//				""uid":"1234567","ctime":"2016-03-08 11:38:00",
// "order_no":"10402016030720411019072343466"
                for (int i = 0; i < ja.length(); i++) {
                    Map<String, Object> map = new TreeMap<String, Object>();
                    map.put("amount", ja.getJSONObject(i).get("amount"));
                    map.put("id", ja.getJSONObject(i).get("id"));
                    map.put("action", ja.getJSONObject(i).get("action"));
                    map.put("remark", ja.getJSONObject(i).get("remark"));
                    map.put("app_id", ja.getJSONObject(i).get("app_id"));
                    map.put("uid", ja.getJSONObject(i).get("uid"));
                    map.put("ctime", ja.getJSONObject(i).get("ctime"));
                    map.put("status", ja.getJSONObject(i).get("status"));
                    map.put("order_no", ja.getJSONObject(i).get("order_no"));
                    list.add(map);
//					map.clear();
                }

                adapter.notifyDataSetChanged();
                mList.onRefreshComplete();
                if (list.size() == 0) {
                    redlistback.setVisibility(View.VISIBLE);
                } else {
                    redlistback.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    };

    private void showPopWindow(Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopWindow = inflater.inflate(R.layout.my_red_popwindow, null, false);
        EditText money_get_edit = (EditText) vPopWindow.findViewById(R.id.money_get_edit);
        Button money_btn = (Button) vPopWindow.findViewById(R.id.money_btn);

        money_get_edit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });

        //获取屏幕的大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//宽度
        int height = dm.heightPixels;//高度
        final PopupWindow popWindow = new PopupWindow(vPopWindow, width, 6 * height / 7, true);

        popWindow.setOutsideTouchable(true);
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new PaintDrawable(Color.argb(255, 255, 255, 255)));
        vPopWindow.setOnKeyListener(new OnKeyListener() {


            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popWindow.dismiss();
//							popWindow = null;  
                    return true;
                }
                return false;
            }
        });

        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mContext.getWindow().setAttributes(lp);
        popWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
                lp.alpha = 1f;
                mContext.getWindow().setAttributes(lp);
            }
        });
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    // 判断文件是否存在
    public boolean fileIsExists(String strFile) {
        if (strFile.length() != 0) {
            try {
                File f = new File(strFile);
                if (!f.exists()) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}