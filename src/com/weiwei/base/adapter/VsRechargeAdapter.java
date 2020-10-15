package com.weiwei.base.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.me.TwitterEquitiesActivity;
import com.weiwei.base.activity.recharge.VsRechargePayTypes;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.Resource;
import com.weiwei.base.db.provider.VsAction;
import com.weiwei.base.item.ChargePackageItem;
import com.hwtx.dududh.R;

public class VsRechargeAdapter extends BaseAdapter {
	private Context mContext = null;
	// private boolean isDrCz = false;
	private LayoutInflater mInflater;
	private ArrayList<ChargePackageItem> data = null;
	/**
	 * 是否点击了更多
	 */
	public static boolean ismore = false;

	public VsRechargeAdapter(Context ctx) {
		this.mContext = ctx;
		this.mInflater = LayoutInflater.from(ctx);
	}

	public void setData(ArrayList<ChargePackageItem> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return (data == null ? 0 : data.size());
	}

	@Override
	public Object getItem(int position) {
		return (data == null ? null : data.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.vs_recharge_package_item, null);
			holder.vs_recharge_item_tv=(TextView) convertView.findViewById(R.id.vs_recharge_item_tv);
			holder.vs_recharge_item_btn=(RoundTextView) convertView.findViewById(R.id.vs_recharge_item_btn);
			holder.v_line = (View) convertView.findViewById(R.id.v_line);
			holder.v_line1 = (View) convertView.findViewById(R.id.v_line1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ChargePackageItem item = data.get(position);
		holder.vs_recharge_item_tv.setText(item.getGoods_name());
		// 设置金额
		int price = Integer.parseInt(item.getPrice());
//			holder.vs_recharge_item_btn.setText("¥" + price / 10000);
			float temp = Float.parseFloat(item.getPrice()) / 10000;
			holder.vs_recharge_item_btn.setText("¥" + temp);
		//if((position+1)==getCount()){
		//	holder.v_line.setVisibility(View.GONE);			
		//}
		
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (VsUtil.isFastDoubleClick()) {
					return;
				}
				if (VsNetWorkTools.isNetworkAvailable(mContext)) {
					if (VsUtil.isLogin("请先登录！", mContext)) {
						packageClickListener(item);
					}
				} else {
					Toast.makeText(mContext, R.string.not_network_connon_msg, Toast.LENGTH_SHORT).show();
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		/**
		 * 充值按钮
		 */
	    private RoundTextView vs_recharge_item_btn;
	    /**
	     * 套餐内容
	     */
	    private TextView vs_recharge_item_tv;
		/**
		 * 第一条Item下面的空白格
		 */
		private View v_line;
		/**
		 * item下面的分割线
		 */
		private View v_line1;
	}

	private void packageClickListener(final ChargePackageItem item) {
		// 跳转到充值中心
		// Intent intent=new Intent(mContext, KcRechargePayTypes.class);
		// mContext.startActivity(intent);
		Intent intent = new Intent();
		intent.putExtra("brandid", item.getBid());
		intent.putExtra("goodsid", item.getGoods_id());
		intent.putExtra("goodsvalue", item.getPrice());
		intent.putExtra("goodsname", item.getGoods_name());
		intent.putExtra("goodsdes", item.getDes());
		intent.putExtra("recommend_flag", item.getRecommend_flag());
		intent.putExtra("convert_price", item.getConvert_price());
		intent.putExtra("present", "没有数据");
		intent.putExtra("pure_name", item.getPure_name());

		// mContext.startActivity(intent);
		// mContext.sendBroadcast(new Intent(KcRechargeSelectPackageActivity.MSG_ID_CLOSE_OTHERSACTIVITY));
		intent.setClass(mContext, TwitterEquitiesActivity.class);
		mContext.startActivity(intent);
		VsAction.insertAction(Resource.activity_2000, Resource.activity_action_004,
				String.valueOf(System.currentTimeMillis() / 1000), "0", mContext);
	}
}
