package com.weiwei.base.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weiwei.base.item.VsContactItem;
import com.hwtx.dududh.R;

/**
 * 邀请好友适配器
 * 
 * @author 李志
 * 
 */
public class VsInvideFriendAdapter extends BaseAdapter {
	/**
	 * 邀请好友数据
	 */
	private ArrayList<VsContactItem> list = null;
	private LayoutInflater inflater;

	public VsInvideFriendAdapter(Context context, ArrayList<VsContactItem> list) {
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
			holder=new ViewHolder();
			convertView = inflater.inflate(R.layout.invide_item_layout, null);
			holder.contactName = (TextView) convertView.findViewById(R.id.contactName);
			holder.contactName.setText(list.get(position).mContactName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView contactName;
	}
}
