package com.weiwei.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;

import java.util.ArrayList;
import java.util.Map;

public class VsMyRedAdapter extends BaseAdapter {
    private static final String TAG = VsCallLogTextAdapter.class.getName();
    private ArrayList<Map<String, Object>> data;
    Context ctx;

    public VsMyRedAdapter(Context ctx, ArrayList<Map<String, Object>> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//    	NoticeDataList mc = (NoticeDataList)getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.vs_myred_text, null);
            holder = new ViewHolder(view);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String action = data.get(position).get("remark").toString();
//        String action = data.get(position).get("action").toString();
        String amount = data.get(position).get("amount").toString();
        String ctime = data.get(position).get("ctime").toString();
        String status = data.get(position).get("status").toString();
        ctime = ctime.substring(0, ctime.indexOf(" "));
        holder.redlee.setText(ctime);
        holder.action.setText(action);

        switch (status) {
            case "0":
                holder.money.setText(amount);
                holder.ctime.setText("处理中");
                break;
            case "1":
                holder.money.setText(amount);
                holder.ctime.setText("成功");
                break;
            default:
                holder.ctime.setText(status);
                if (amount != null && amount.length() != 0) {
                    amount = "" + Double.parseDouble(amount) / 10000;
                }
                holder.money.setText(amount);
                break;
        }
        return convertView;
    }

    final static class ViewHolder {
        TextView redlee, action, money, ctime;
        ImageView image_notice_zan;

        ViewHolder(View view) {
            this.redlee = (TextView) view.findViewById(R.id.redlee);//时间
            this.money = (TextView) view.findViewById(R.id.money);//金额
            this.action = (TextView) view.findViewById(R.id.action);//操作内容
            this.ctime = (TextView) view.findViewById(R.id.ctime);
            view.setTag(this);
        }
    }
}