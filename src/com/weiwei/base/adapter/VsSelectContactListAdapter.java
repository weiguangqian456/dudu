package com.weiwei.base.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.item.VsContactItem;
import com.hwtx.dududh.R;

public class VsSelectContactListAdapter extends BaseAdapter {
	private Context mContext = null;
	private Handler mHandler = null;
	private ArrayList<VsContactItem> data = null;

	public static final char MSG_ID_DELSELCONTACT = 121;
	public static final char MSG_ID_ADDSELCONTACT = 122;

	public VsSelectContactListAdapter(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	public void setData(ArrayList<VsContactItem> data) {
		if (mHandler == null) {
			this.data = data;
		} else {
			this.data = new ArrayList<VsContactItem>();
			this.data.addAll(data);
			VsContactItem additem = new VsContactItem();
			additem.mIndex = -11;
			additem.mContactName = "添加";
			this.data.add(additem);
		}
	}

	@Override
	public int getCount() {
		int count = data.size();
		return count % 4 == 0 ? (count / 4) : (count / 4 + 1);
	}

	@Override
	public Object getItem(int position) {
		int size = data.size();
		if (size <= 4) {
			return data;
		} else {
			ArrayList<VsContactItem> retList = new ArrayList<VsContactItem>(4);
			int i = 4 * position;
			int len = 4 * (position + 1);
			while (i < len && i < size) {
				retList.add(data.get(i++));
			}
			return retList;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InviteViewHolder holder = null;
		if (convertView == null) {
			holder = new InviteViewHolder();
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.vs_msg_invite_item, null);
			holder.invite_contact_rl = new RelativeLayout[4];
			holder.invite_contact_rl[0] = (RelativeLayout) convertView.findViewById(R.id.invite_contact_rl01);
			holder.invite_contact_rl[1] = (RelativeLayout) convertView.findViewById(R.id.invite_contact_rl02);
			holder.invite_contact_rl[2] = (RelativeLayout) convertView.findViewById(R.id.invite_contact_rl03);
			holder.invite_contact_rl[3] = (RelativeLayout) convertView.findViewById(R.id.invite_contact_rl04);
			holder.invite_contact_tv = new TextView[4];
			holder.invite_contact_tv[0] = (TextView) convertView.findViewById(R.id.invite_contact_tv01);
			holder.invite_contact_tv[1] = (TextView) convertView.findViewById(R.id.invite_contact_tv02);
			holder.invite_contact_tv[2] = (TextView) convertView.findViewById(R.id.invite_contact_tv03);
			holder.invite_contact_tv[3] = (TextView) convertView.findViewById(R.id.invite_contact_tv04);
			holder.invite_contact_iv = new ImageView[4];
			holder.invite_contact_iv[0] = (ImageView) convertView.findViewById(R.id.invite_contact_iv01);
			holder.invite_contact_iv[1] = (ImageView) convertView.findViewById(R.id.invite_contact_iv02);
			holder.invite_contact_iv[2] = (ImageView) convertView.findViewById(R.id.invite_contact_iv03);
			holder.invite_contact_iv[3] = (ImageView) convertView.findViewById(R.id.invite_contact_iv04);
			convertView.setTag(holder);
		} else {
			holder = (InviteViewHolder) convertView.getTag();
		}

		@SuppressWarnings("unchecked")
		final ArrayList<VsContactItem> items = (ArrayList<VsContactItem>) getItem(position);
		for (int i = 0; i < items.size(); i++) {
			holder.invite_contact_rl[i].setVisibility(View.VISIBLE);
			holder.invite_contact_tv[i].setText(items.get(i).mContactName);
			CustomLog.i("GDK", items.get(i).mContactName);
			if (mHandler == null) {
				holder.invite_contact_iv[i].setVisibility(View.GONE);
			} else {
				final int tmp_i = i;
				if (items.get(i).mIndex == -11) {
					holder.invite_contact_iv[i].setVisibility(View.GONE);
					Drawable drawable = mContext.getResources().getDrawable(R.drawable.invite_add);
					// / 这一步必须要做,否则不会显示.
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					holder.invite_contact_tv[i].setCompoundDrawables(drawable, null, null, null);
					holder.invite_contact_tv[i].setTextColor(mContext.getResources().getColor(R.color.invite_addcolor));
					holder.invite_contact_rl[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (VsUtil.isFastDoubleClick()) {
								return;
							}
							mHandler.sendEmptyMessage(MSG_ID_ADDSELCONTACT);
						}
					});
				} else {
					holder.invite_contact_rl[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (VsUtil.isFastDoubleClick()) {
								return;
							}
							Message msg = mHandler.obtainMessage();
							Bundle data = new Bundle();
							data.putParcelable("delContact", items.get(tmp_i));
							msg.setData(data);
							msg.what = MSG_ID_DELSELCONTACT;
							mHandler.sendMessage(msg);
						}
					});
					holder.invite_contact_iv[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (VsUtil.isFastDoubleClick()) {
								return;
							}
							Message msg = mHandler.obtainMessage();
							Bundle data = new Bundle();
							data.putParcelable("delContact", items.get(tmp_i));
							msg.setData(data);
							msg.what = MSG_ID_DELSELCONTACT;
							mHandler.sendMessage(msg);
						}
					});
				}
			}
		}
		return convertView;
	}

	private class InviteViewHolder {
		private RelativeLayout[] invite_contact_rl;
		private TextView[] invite_contact_tv;
		private ImageView[] invite_contact_iv;
	}
}
