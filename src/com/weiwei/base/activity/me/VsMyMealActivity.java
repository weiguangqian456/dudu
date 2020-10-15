package com.weiwei.base.activity.me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsMyTaoCanAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class VsMyMealActivity extends VsBaseActivity implements OnRefreshListener2<ListView> {
    private TextView callmoney, calllog;
    private ImageView cursor;
    private String uid = "";

    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private TextView myBalancDetail;
    private Button myBalancChargeBtn;

    private ListView lv_cur_list;
    private VsMyTaoCanAdapter adapter;
    private PullToRefreshListView mList;
    private int flag = 0;
    private Boolean upFlag = false;
    private String startTime, endTime;
    private SimpleDateFormat sDateFormat;
    private TextView redlistback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vsmymeal);
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
        init();

        IntentFilter mytaocanFilter = new IntentFilter();
        mytaocanFilter.addAction(VsUserConfig.JKey_GET_MY_TAOCAN_LOG);
        mContext.registerReceiver(mytaocanReceiver, mytaocanFilter);

//		TreeMap<String, String> treeMap = new TreeMap<String, String>();
//		treeMap.put("uid",
//				VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//		CoreBusiness.getInstance().startThread(mContext,
//				GlobalVariables.INTERFACE_BALANCE, DfineAction.authType_UID,
//				treeMap, GlobalVariables.actionSeaRchtaocan);

        //计算日期
        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        endTime = sDateFormat.format(new java.util.Date());
        Calendar c = Calendar.getInstance();
        int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30;
        c.set(Calendar.DAY_OF_MONTH, startMonth);
        startTime = sDateFormat.format(c.getTime());
        getMyTaocanLog(uid, startTime, endTime);

        VsApplication.getInstance().addActivity(this);
    }

    private void init() {
        callmoney = (TextView) findViewById(R.id.callmoney);
        calllog = (TextView) findViewById(R.id.calllog);
        redlistback = (TextView) findViewById(R.id.redlistback);
        redlistback.setVisibility(View.VISIBLE);
        myBalancDetail = (TextView) findViewById(R.id.myBalancDetail);
//        String balance = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Balance_Left,"0.00");
        myBalancDetail.setText("包年餐套");

        myBalancChargeBtn = (Button) findViewById(R.id.myBalancChargeBtn);

        mList = (PullToRefreshListView) findViewById(R.id.mList);
        mList.setMode(Mode.PULL_FROM_START);
        mList.setOnRefreshListener(this);
        //	mList.setRefreshing(true);
        lv_cur_list = mList.getRefreshableView();

        adapter = new VsMyTaoCanAdapter(VsMyMealActivity.this, list);
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
//				if (totalItemCount <= visibleItemCount) {
//					mList.setMode(Mode.PULL_FROM_START);
//				}else {
//					mList.setMode(Mode.BOTH);
//				}
            }
        });
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
        Calendar c = Calendar.getInstance();
        int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30;
        c.set(Calendar.DAY_OF_MONTH, startMonth);
        startTime = sDateFormat.format(c.getTime());
//	        long i = c.getTimeInMillis();
        getMyTaocanLog(uid, startTime, endTime);

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
        flag++;
        endTime = startTime;

        Calendar c = Calendar.getInstance();
        int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 * (flag + 1);
        c.set(Calendar.DAY_OF_MONTH, startMonth);
        startTime = sDateFormat.format(c.getTime());
        getMyTaocanLog(uid, startTime, endTime);
    }

    //拉取话单列表信息
    private void getMyTaocanLog(String uid, String startTime, String endTime) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", uid);
        treeMap.put("sdate", startTime);
        treeMap.put("edate", endTime);
        treeMap.put("rows", "5");
        treeMap.put("page", "1");
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_BALANCE, DfineAction.authType_UID, treeMap, GlobalVariables.actionSeaRchtaocan);
    }

    private BroadcastReceiver mytaocanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                JSONObject js = new JSONObject(bundle.get("list").toString());
                JSONArray mJSONArray = js.getJSONArray("package_list");
                if (!upFlag && list.size() != 0) {
                    list.clear();
                }
                for (int i = 0; i < mJSONArray.length(); i++) {
                    Map<String, Object> map = new TreeMap<String, Object>();
                    map.put("package_id", mJSONArray.getJSONObject(i).get("package_id"));
                    map.put("package_type", mJSONArray.getJSONObject(i).get("package_type"));
                    map.put("id", mJSONArray.getJSONObject(i).get("id"));
                    map.put("expire_time", mJSONArray.getJSONObject(i).get("expire_time"));
                    map.put("app_id", mJSONArray.getJSONObject(i).get("app_id"));
                    map.put("is_nature", mJSONArray.getJSONObject(i).get("is_nature"));
                    map.put("uid", mJSONArray.getJSONObject(i).get("uid"));
                    map.put("effect_time", mJSONArray.getJSONObject(i).get("effect_time"));
                    map.put("ctime", mJSONArray.getJSONObject(i).get("ctime"));
                    map.put("left_call_time", mJSONArray.getJSONObject(i).get("left_call_time"));
                    map.put("is_time", mJSONArray.getJSONObject(i).get("is_time"));
                    map.put("package_name", mJSONArray.getJSONObject(i).get("package_name"));
                    list.add(map);
//							map.clear();
                }

                adapter.notifyDataSetChanged();
                mList.onRefreshComplete();
                if (list.size() == 0) {
                    redlistback.setVisibility(View.VISIBLE);
                } else {
                    redlistback.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mToast.show("查询套餐失败", Toast.LENGTH_SHORT);
            }
        }
    };
}
