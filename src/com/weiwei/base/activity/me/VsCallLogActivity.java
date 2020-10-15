package com.weiwei.base.activity.me;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsCallLogTextAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;

public class VsCallLogActivity extends VsBaseActivity implements OnRefreshListener2<ListView>{
	private PullToRefreshListView mList;
	private ListView lv_cur_list;
	private VsCallLogTextAdapter adapter;
	private String uid ;
	private String endTime;
	private SimpleDateFormat sDateFormat;
	private String startTime = "2015-01-01";
	private int flag = 0;
	private Boolean upFlag = false;
	private TextView redlistback;
	private int page = 1;
	private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		@Override
			public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);		
			setContentView(R.layout.vs_list_calllog);
			init();
			 uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
			
			IntentFilter mycalllogFilter = new IntentFilter();
			mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_CALL_LOG);
			mContext.registerReceiver(mycalllogReceiver, mycalllogFilter);
			
			//计算日期
			 sDateFormat = new SimpleDateFormat("yyyy-MM-dd");       
			endTime = sDateFormat.format(new java.util.Date());
//			Calendar c = Calendar.getInstance();
//			int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ; 
//			c.set(Calendar.DAY_OF_MONTH,startMonth);    
//	        startTime = sDateFormat.format(c.getTime());
	        
			getMyCallLog(uid,startTime,endTime,page+"");
			VsApplication.getInstance().addActivity(this);
		}

		private void init() {
	
			mList = (PullToRefreshListView)findViewById(R.id.mList);
			mList.setMode(Mode.BOTH);
			mList.setOnRefreshListener(this);
			//	mList.setRefreshing(true);
			lv_cur_list = mList.getRefreshableView();
			 redlistback = (TextView)findViewById(R.id.redlistback);
			redlistback.setVisibility(View.VISIBLE);
			adapter = new VsCallLogTextAdapter(VsCallLogActivity.this, list);
			lv_cur_list.setAdapter(adapter);
			lv_cur_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				
			}
		});
			
			lv_cur_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (totalItemCount <= visibleItemCount&&(lv_cur_list.getLastVisiblePosition()-lv_cur_list.getFirstVisiblePosition()+1)<5) {
					mList.setMode(Mode.PULL_FROM_START);
				}else {
					mList.setMode(Mode.BOTH);
				}
				
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
//				Calendar c = Calendar.getInstance();
//				int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30 ; 
//				c.set(Calendar.DAY_OF_MONTH,startMonth);    
//		        startTime = sDateFormat.format(c.getTime());
//		        long i = c.getTimeInMillis();
		        page = 1;
		        getMyCallLog(uid,startTime,endTime,page+"");
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
//				endTime = startTime;
//				
//				Calendar c = Calendar.getInstance();
//				int startMonth = c.get(Calendar.DAY_OF_MONTH) - 30*(flag + 1) ; 
//				c.set(Calendar.DAY_OF_MONTH,startMonth);    
//		        startTime = sDateFormat.format(c.getTime());
				 page++;
			        getMyCallLog(uid, startTime, endTime,page+"");
		}

		//拉取话单列表信息
		private void getMyCallLog(String uid,String startTime,String endTime,String page){

			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put("uid", uid);
			treeMap.put("sdate", startTime);
			treeMap.put("edate", endTime);
			treeMap.put("rows", "5");
			treeMap.put("page", page);

			
			
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyCallLog, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyCallLog);
	
		}


		
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
					if (!upFlag&&list.size()!=0) {
						list.clear();
					}
					
					for (int i = 0; i < ja.length(); i++) {
						Map<String,Object> map = new TreeMap<String, Object>();
						map.put("end_time", ja.getJSONObject(i).get("end_time"));
						map.put("field_unit", ja.getJSONObject(i).get("field_unit"));
						map.put("call_type", ja.getJSONObject(i).get("call_type"));
						map.put("start_time", ja.getJSONObject(i).get("start_time"));
						map.put("app_id", ja.getJSONObject(i).get("app_id"));
						map.put("package_time", ja.getJSONObject(i).get("package_time"));
						map.put("ctime", ja.getJSONObject(i).get("ctime"));
						map.put("billing_time", ja.getJSONObject(i).get("billing_time"));
						map.put("field_rate", ja.getJSONObject(i).get("field_rate"));
						map.put("id", ja.getJSONObject(i).get("id"));
						map.put("call_time", ja.getJSONObject(i).get("call_time"));
						map.put("callee", ja.getJSONObject(i).get("callee"));
						map.put("field_fee", ja.getJSONObject(i).get("field_fee"));
						map.put("caller", ja.getJSONObject(i).get("caller"));
						map.put("wait_time", ja.getJSONObject(i).get("wait_time"));
						list.add(map);
//						map.clear();
					}
					
					adapter.notifyDataSetChanged();
					mList.onRefreshComplete(); 
					if (list.size() == 0) {
						redlistback.setVisibility(View.VISIBLE);
					}else {
						redlistback.setVisibility(View.INVISIBLE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}

		};

		
		@Override
		public void onStop(){
			if (mycalllogReceiver != null) {
				 unregisterReceiver(mycalllogReceiver);
				 mycalllogReceiver = null;
			}
	   
	    super.onStop();
		}
		
		
}