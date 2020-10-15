package com.weiwei.base.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.weiwei.base.adapter.VsRechargeAdapter;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.ChargePackageItem;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;


/**
 * 
 * @Title:Android客户端
 * @Description: 话费界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsRechargeFragment extends VsBaseFragment {

	private View mParent;
	private FragmentActivity mActivity;
	/**
	 * 保存充值套餐的对象
	 */
	private ArrayList<ChargePackageItem> allData = null;
	/**
	 * 充值 套餐listview
	 */
	private ListView mChargePackageListview = null;
	/**
	 * 充值套餐adapter
	 */
	private VsRechargeAdapter adapter = null;
	/**
	 * 充值任务列表
	 */
	private LinearLayout vs_recharge_task;
	/**
	 * 充值任务内容
	 */
	private TextView vs_recharge_task_tv;
	/**
	 * 线
	 */
	private View line;
	/**
	 * 充值任务信息
	 */
	private ChargePackageItem taskData = null;

	// private TitleView mTitle;
	// public ScrollView recharge_scrollview;

	private final char MSG_UPDATEGOODSLIST = 100;

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
	 */
	public static VsRechargeFragment newInstance(int index) {
		VsRechargeFragment f = new VsRechargeFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recharge, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = getActivity();
		initTaskInfoData();
		initView();
		initRegInfoData();
		initAdapter();
		IntentFilter netChangeFilter = new IntentFilter();
		netChangeFilter.addAction(GlobalVariables.actionGoodsConfig);
		mContext.registerReceiver(actionGoodsChange, netChangeFilter);
		// 拉取充值信息列表 启动拉取 oncreate只会启动一次 保证每次启动拉取到的都是最新信息
		if (!DfineAction.IsStartGoodsConfig) {
			VsBizUtil.getInstance().goodsConfig(mActivity);
		}
	}

	private BroadcastReceiver actionGoodsChange = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mBaseHandler.sendEmptyMessage(MSG_UPDATEGOODSLIST);
		}
	};
	/**
	 * 顶部布局
	 */
	private LinearLayout mHeaderLayout;

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleNavBar(mParent);
		mTitleTextView.setText(R.string.vs_recharge_title);
		showRightTxtBtn(getResources().getString(R.string.vs_balance_combo_Tariff));
		mChargePackageListview = (ListView) mParent.findViewById(R.id.charge_package_listview);
        if (getActivity() == null){
            return;
        }
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderLayout = (LinearLayout) inflater.inflate(R.layout.vs_recharge_scrolllayout, null);
		mChargePackageListview.addHeaderView(mHeaderLayout);
		vs_recharge_task = (LinearLayout) mParent.findViewById(R.id.vs_recharge_task);
		vs_recharge_task_tv = (TextView) mParent.findViewById(R.id.vs_recharge_task_tv);
		line = mParent.findViewById(R.id.recharge_line_bottom);
	}

	// /**
	// * 进入充值方式选择
	// */
	// private void packageClickListener() {
	// Intent intent = new Intent();
	// intent.putExtra("brandid", taskData.getBid());
	// intent.putExtra("goodsid", taskData.getGoods_id());
	// intent.putExtra("goodsvalue", taskData.getPrice());
	// intent.putExtra("goodsname", taskData.getName());
	// intent.putExtra("goodsdes", taskData.getDes());
	// intent.putExtra("recommend_flag", taskData.getRecommend_flag());
	// intent.putExtra("convert_price", taskData.getConvert_price());
	// intent.putExtra("present", "没有数据");
	// intent.putExtra("pure_name", taskData.getPure_name());
	// intent.setClass(mContext, KcRechargePayTypes.class);
	// mContext.startActivity(intent);
	// KcAction.insertAction(Resource.activity_2000, Resource.activity_action_004,
	// String.valueOf(System.currentTimeMillis() / 1000), "0", mContext);
	// }

	@Override
	protected void HandleRightNavBtn() {
		// TODO Auto-generated method stub
		super.HandleRightNavBtn();
		VsUtil.startActivity("3015", mContext, null);
	}

	/**
	 * 
	 * 初始化任务
	 * 
	 * @return
	 */
	private void initTaskInfoData() {
		String regInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_NewTaskInfo);
		try {
			if (regInfo != null && !"".equals(regInfo)) {
				JSONArray jsonArray = new JSONArray(regInfo);
				JSONObject jobj = (JSONObject) jsonArray.get(0);
				if (jobj != null && jobj.length() > 0) {
					String recommend_flag = jobj.getString("recommend_flag");
					taskData = new ChargePackageItem(jobj.getInt("sort_id"), jobj.getString("bid"),
							jobj.getString("goods_id"), recommend_flag, jobj.getString("name"), jobj.getString("des"),
							jobj.getString("price"), jobj.getString("buy_limit"), jobj.getString("goods_type"),
							jobj.getString("total_flag"),"",jobj.getString("name"),
							"[]");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化套餐信息
	 */
	private void initRegInfoData() {
		allData = new ArrayList<ChargePackageItem>();
		String regInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_NewGoodsInfo);
		CustomLog.i("FavourableInfo ===", "regInfo===" + regInfo);
		try {
			JSONArray jsonArray = new JSONArray(regInfo);
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject jobj = (JSONObject) jsonArray.get(i);
				if (jobj != null) {
					String recommend_flag = jobj.getString("recommend_flag");
					ChargePackageItem recData = new ChargePackageItem(jobj.getInt("sort_id"), jobj.getString("bid"),
							jobj.getString("goods_id"), recommend_flag, jobj.getString("name"), jobj.getString("des"),
							jobj.getString("price"), jobj.getString("buy_limit"), jobj.getString("goods_type"),
							jobj.getString("total_flag"),"",jobj.getString("name"),
							"[]");
					allData.add(recData);
				}
			}
		} catch (JSONException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (allData == null || allData.size() == 0) {
			/*int len = DfineAction.defaultPackage.length;
			for (int i = 0; i < len; i++) {
				allData.add(new ChargePackageItem(0, DfineAction.defaultPackage[i][0],
						DfineAction.defaultPackage[i][1], DfineAction.defaultPackage[i][2],
						DfineAction.defaultPackage[i][3], DfineAction.defaultPackage[i][4],
						DfineAction.defaultPackage[i][5], DfineAction.defaultPackage[i][6],
						DfineAction.defaultPackage[i][7], DfineAction.defaultPackage[i][8],
						DfineAction.defaultPackage[i][9], DfineAction.defaultPackage[i][10],
						DfineAction.defaultPackage[i][11]));
			}*/
		} else {
			// 根据sort_id排序
			Collections.sort(allData, new Comparator<ChargePackageItem>() {
				@Override
				public int compare(ChargePackageItem date1, ChargePackageItem date2) {
					return date2.getSort_id() - date1.getSort_id();
				}
			});
		}
	}

	/**
	 * 初始化adapter
	 */
	public void initAdapter() {
		// recharge_scrollview = (ScrollView) mParent.findViewById(R.id.recharge_scrollview);
		// recharge_scrollview.smoothScrollTo(0, 0);
		if (taskData != null) {// 判断是否有充值任务
			line.setVisibility(View.VISIBLE);
			vs_recharge_task.setVisibility(View.VISIBLE);
			vs_recharge_task_tv.setText(taskData.getGoods_name());
		} else {
			vs_recharge_task.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
		adapter = new VsRechargeAdapter(mContext);
		adapter.setData(allData);
		mChargePackageListview.setAdapter(adapter);
		mChargePackageListview.setDivider(null);
		// KcUtil.setListViewHeightBasedOnChildren(mChargePackageListview);
	}

	protected void handleBaseMessage(Message msg) {
		switch (msg.what) {
		case MSG_UPDATEGOODSLIST:
			initTaskInfoData();
			initRegInfoData();
			initAdapter();
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (actionGoodsChange != null) {
			mContext.unregisterReceiver(actionGoodsChange);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
		}
	}

}
