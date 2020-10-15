package com.weiwei.base.activity.me;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.weiwei.base.adapter.VsMyExChangeAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

public class VsChangeListActivity extends VsBaseActivity implements OnClickListener, OnRefreshListener2<ListView>{

	private PullToRefreshListView mList;
    private String uid;
	private ListView lv_cur_list;
	private VsMyExChangeAdapter adapter;
	private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private Boolean upFlag = false;
	private int page = 1;
	private TextView card_listback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.vs_myexchange);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		mTitleTextView.setText(getResources().getString(R.string.vs_myexchange_detail));
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
	
		uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
		init();
	
		IntentFilter myCardNoFilter = new IntentFilter();
		myCardNoFilter.addAction(GlobalVariables.actionexchange);
		mContext.registerReceiver(myCardNoReceiver, myCardNoFilter);
		
		getCardList("10",page);

		
		VsApplication.getInstance().addActivity(this);
	}
	
	
	
	private BroadcastReceiver myCardNoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Bundle bundle = intent.getExtras();
			JSONObject js;
			JSONArray ja;
			try {
				if (bundle.get("data").toString().contains("频繁")) {
					Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
					mList.onRefreshComplete(); 
					return;
				}
				ja = new JSONArray(bundle.get("data").toString());
				
				if (!upFlag&&list.size()!=0) {
					list.clear();
				}

				for (int i = 0; i < ja.length(); i++) {
					Map<String,Object> map = new TreeMap<String, Object>();
					map.put("price", ja.getJSONObject(i).get("price"));
					map.put("expire_date", ja.getJSONObject(i).get("expire_date"));
					map.put("card_no", ja.getJSONObject(i).get("card_no"));
					map.put("card_pwd", ja.getJSONObject(i).get("card_pwd"));
					map.put("pay_phone", ja.getJSONObject(i).get("pay_phone"));
					list.add(map);
//					map.clear();
				}
				
				adapter.notifyDataSetChanged();
				mList.onRefreshComplete(); 
			if (list.size() == 0) {
				card_listback.setVisibility(View.VISIBLE);
				mList.setVisibility(View.GONE);
			}else {
				card_listback.setVisibility(View.GONE);
				mList.setVisibility(View.VISIBLE);
			}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	};

	
	
	private void getCardList(String row,int page){
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		 treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		 treeMap.put("rows", row);
		 treeMap.put("page", page+"");
		 CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_EXCHANGE_CARD, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionexchange);
		 
		
	}
	
	
	private void init() {
		 mList = (PullToRefreshListView)findViewById(R.id.mList);
		 card_listback = (TextView)findViewById(R.id.card_listback);
			mList.setMode(Mode.BOTH);
			mList.setOnRefreshListener(this);
			//	mList.setRefreshing(true);
			lv_cur_list = mList.getRefreshableView();
			
			adapter = new VsMyExChangeAdapter(VsChangeListActivity.this, list);
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
					if (totalItemCount <= visibleItemCount) {
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
			page = 1;
			getCardList("10", page);
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
		page++;
		getCardList("10", page);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
