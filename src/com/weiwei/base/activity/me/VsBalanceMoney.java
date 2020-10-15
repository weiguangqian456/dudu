package com.weiwei.base.activity.me;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsCallLogTextAdapter;
import com.weiwei.base.adapter.VsCallMoneyTextAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;


public class VsBalanceMoney extends VsBaseActivity implements OnRefreshListener2<ListView>{
	
		private PullToRefreshListView mList;
		private ListView lv_cur_list;
		private VsCallMoneyTextAdapter adapter;
		private String startTime,endTime;
		private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		private String uid ;
		private SimpleDateFormat sDateFormat;
		private int flag = 0;
		private Boolean upFlag = false;
		private TextView redlistback;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.vs_list_call);
	
		init();
		 uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId,"");
		
		IntentFilter mycalllogFilter = new IntentFilter();
		mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_CALL_MONEY);
		
		mContext.registerReceiver(mycallmoneyReceiver, mycalllogFilter);
		
		//计算日期
		 sDateFormat = new SimpleDateFormat("yyyy-MM-dd");       
		endTime = sDateFormat.format(new java.util.Date());
		Calendar c = Calendar.getInstance();
		int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ; 
		c.set(Calendar.DAY_OF_MONTH,startMonth);    
        startTime = sDateFormat.format(c.getTime());
		
		getMyCallMoney(uid,startTime,endTime);
		
		
		VsApplication.getInstance().addActivity(this);
	}

	private void init() {
		
		mList = (PullToRefreshListView)findViewById(R.id.mList);
		mList.setMode(Mode.PULL_FROM_START);
    	mList.setOnRefreshListener(this);
//    	mList.setRefreshing(true);
    	 redlistback = (TextView)findViewById(R.id.redlistback);
 		lv_cur_list = mList.getRefreshableView();
 		redlistback.setVisibility(View.VISIBLE);
 		adapter = new VsCallMoneyTextAdapter(VsBalanceMoney.this, list);
 		lv_cur_list.setAdapter(adapter);
 		lv_cur_list.setOnItemClickListener(new OnItemClickListener() {

 			@Override
 			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
 				
 			}
 		});
 		lv_cur_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
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
		String label = DateUtils.formatDateTime(  
                getApplicationContext(),  
                System.currentTimeMillis(),  
                DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_ALL);  
        // 显示最后更新的时间  
		mList.getLoadingLayoutProxy().setLastUpdatedLabel(label); 
		
		flag = 0;   
		endTime = sDateFormat.format(new java.util.Date());
		Calendar c = Calendar.getInstance();
		int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ; 
		c.set(Calendar.DAY_OF_MONTH,startMonth);    
        startTime = sDateFormat.format(c.getTime());
//        long i = c.getTimeInMillis();
        getMyCallMoney(uid,startTime,endTime);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (VsUtil.isFastDoubleClick()) {
			Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
			mList.onRefreshComplete(); 
			return;
		}
		upFlag = true;
		String label = DateUtils.formatDateTime(  
                getApplicationContext(),  
                System.currentTimeMillis(),  
                DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_ALL);  
        // 显示最后更新的时间  
		mList.getLoadingLayoutProxy().setLastUpdatedLabel(label);  
		flag++;
		endTime = startTime;
		
		Calendar c = Calendar.getInstance();
		int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30*(flag + 1) ; 
		c.set(Calendar.DAY_OF_MONTH,startMonth);    
        startTime = sDateFormat.format(c.getTime());
        getMyCallMoney(uid, startTime, endTime);
	}
	
	//拉取话费列表信息
			private void getMyCallMoney(String uid,String startTime,String endTime){
//				{"sdate": "2016-02-01", "uid": "1000000", "app_id": "wind", "sign": "103d8bacb293e5f9ae3604fdc21d90fb", 
//				"agent_id": "", "rows": 5, "pv": "iphone", "ts": 1456815762, "page": 1,
//					 "token": "9OW-1DRZKX47-B", "v": "1.0.0", "edate": "2016-03-01"}
				TreeMap<String, String> treeMap = new TreeMap<String, String>();
				treeMap.put("uid", uid);
				treeMap.put("sdate", startTime);
				treeMap.put("edate", endTime);
				treeMap.put("rows", "5");
				treeMap.put("page", "1");

				
				
			CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyCallMoney, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyCallMoney);


		   }
		
			
			private BroadcastReceiver mycallmoneyReceiver = new BroadcastReceiver() {
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
						if (!upFlag&&list.size()!=0) {
							list.clear();
						}
						
						for (int i = 0; i < ja.length(); i++) {
							Map<String,Object> map = new TreeMap<String, Object>();
							map.put("amount", ja.getJSONObject(i).get("amount"));
							map.put("balance", ja.getJSONObject(i).get("balance"));
							map.put("action", ja.getJSONObject(i).get("action"));
							map.put("id", ja.getJSONObject(i).get("id"));
							map.put("remark", ja.getJSONObject(i).get("remark"));
							map.put("app_id", ja.getJSONObject(i).get("app_id"));
							map.put("uid", ja.getJSONObject(i).get("uid"));
							map.put("ctime", ja.getJSONObject(i).get("ctime"));
							map.put("reason", ja.getJSONObject(i).get("reason"));
							map.put("order_no", ja.getJSONObject(i).get("order_no"));
							map.put("gift_balance", ja.getJSONObject(i).get("gift_balance"));
							if (ja.getJSONObject(i).get("action").equals("buy_vip")) {
								
							}else {
								list.add(map);
							}
							
						}
						
						adapter.notifyDataSetChanged();
						mList.onRefreshComplete(); 
						if (list.size() == 0) {
							redlistback.setVisibility(View.VISIBLE);
						}else {
							redlistback.setVisibility(View.INVISIBLE);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}

			};
	
			
			@Override
			public void onStop(){
				if (mycallmoneyReceiver != null) {
					 unregisterReceiver(mycallmoneyReceiver);
					 mycallmoneyReceiver = null;
				}
		   
		    super.onStop();
			}
			
			
	}
	

