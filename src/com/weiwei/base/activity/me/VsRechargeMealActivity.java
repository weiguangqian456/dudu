package com.weiwei.base.activity.me;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description: 充值套餐
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 石云升
 * @version: 1.0.0.0
 * @Date: 2014-9-18
 */
public class VsRechargeMealActivity extends VsBaseActivity {
	private ListView mBytcOtherInfoListview = null;
	private RechargeMealAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_charge_meal);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		mTitleTextView.setText(this.getIntent().getStringExtra("title"));
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		init();
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
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
	 * 初始化视图
	 */
	private void init() {
		Intent intent = this.getIntent();
		ArrayList<String> mNameData = intent.getStringArrayListExtra("mNameData");
		ArrayList<String> mInfoData = intent.getStringArrayListExtra("mInfoData");
		mBytcOtherInfoListview = (ListView) findViewById(R.id.bytc_another_info);
		adapter = new RechargeMealAdapter(mContext);
		adapter.setData(mNameData, mInfoData);
		mBytcOtherInfoListview.setAdapter(adapter);
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
