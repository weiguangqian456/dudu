package com.weiwei.base.activity.me;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.common.VsUtil;
import com.weiwei.base.item.VsInviteItem;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:Android客户端
 * @Description:赚话费任务适配器 
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsMakeMoneyAdapter extends BaseAdapter {
	/**
	 * 保存任务数据
	 */
	private ArrayList<VsInviteItem> inviteList = null;

	private LayoutInflater inflater = null;
	/**
	 * context
	 */
	private Context context;

	public VsMakeMoneyAdapter(Context context, ArrayList<VsInviteItem> inviteList) {
		this.inviteList = inviteList;
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return inviteList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return inviteList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.vs_makemoney_task_item, null);
			holder.vs_invite_item_layout = (RelativeLayout) convertView.findViewById(R.id.vs_invite_item_layout);
			holder.vs_invite_item_tv = (TextView) convertView.findViewById(R.id.vs_invite_item_tv);
			holder.vs_invite_item_small_tv = (TextView) convertView.findViewById(R.id.vs_invite_item_small_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VsInviteItem vsInviteItem = inviteList.get(position);
		if (vsInviteItem != null) {
			holder.vs_invite_item_tv.setText(vsInviteItem.getName());
			holder.vs_invite_item_small_tv.setText(vsInviteItem.getSecond_name());
		}
		holder.vs_invite_item_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vsInviteItem.getTasktype().equals("invite")){
				}else if(vsInviteItem.getTasktype().equals("charge")){
				}
				// TODO Auto-generated method stub
				if (vsInviteItem.getUrl().contains("040201")) {
					VsUtil.skipForScheme(vsInviteItem.getUrl(), (Activity) context, vsInviteItem);
				} else {
					Intent intent = new Intent(context, VsMakeMoneyTaskActivity.class);
					intent.putExtra("vsInviteItem", vsInviteItem);
					context.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		/**
		 * 条目布局
		 */
		private RelativeLayout vs_invite_item_layout;
		/**
		 * 一级标题
		 */
		private TextView vs_invite_item_tv;
		/**
		 * 一级标题 子标题
		 */
		private TextView vs_invite_item_small_tv;
	}
}
