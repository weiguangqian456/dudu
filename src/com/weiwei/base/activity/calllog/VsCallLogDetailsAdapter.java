package com.weiwei.base.activity.calllog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.item.VsCallLogItem;

import java.util.ArrayList;

/**
 * @Title:Android客户端
 * @Description: 通话记录ListView适配器
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsCallLogDetailsAdapter extends BaseAdapter {
    /**
     * 联系人对象
     */
    private ArrayList<VsCallLogItem> data;
    /**
     *
     */
    private LayoutInflater mInflater;

    // /**
    // * 上下文
    // */
    // private Context mContext;

    /**
     * 构造方法
     *
     * @param context
     */
    public VsCallLogDetailsAdapter(Context context, ArrayList<VsCallLogItem> data) {
        mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.vs_calllog_detail_item, null);
            holder.calllog_detail_time = (TextView) convertView.findViewById(R.id.calllog_detail_time);
            holder.calllog_detail_callstate_inorout = (TextView) convertView.findViewById(R.id.calllog_detail_callstate_inorout);
            holder.calllog_detail_calltime = (TextView) convertView.findViewById(R.id.calllog_detail_calltime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 为控件填充内容
        if (-999 != data.get(position).calltimestamp) {// 判断是否有通话记录
            holder.calllog_detail_time.setTextColor(VsApplication.getContext().getResources().getColor(R.color.calllog_detail_textcolor));
            holder.calllog_detail_callstate_inorout.setTextColor(VsApplication.getContext().getResources().getColor(R.color.calllog_detail_textcolor));
            holder.calllog_detail_calltime.setTextColor(VsApplication.getContext().getResources().getColor(R.color.calllog_detail_textcolor));

            holder.calllog_detail_time.setText(VsUtil.getCallLogTime(data.get(position).calltimestamp));
            if ("1".equals(data.get(position).ctype)) {// 呼入
                holder.calllog_detail_callstate_inorout.setText("（呼入）");
            } else if ("2".equals(data.get(position).ctype)) {// 呼出
                holder.calllog_detail_callstate_inorout.setText("（呼出）");
            } else if ("3".equals(data.get(position).ctype)) {
                holder.calllog_detail_callstate_inorout.setText("（未接）");
                holder.calllog_detail_time.setTextColor(VsApplication.getContext().getResources().getColor(R.color.vs_cs));
                holder.calllog_detail_callstate_inorout.setTextColor(VsApplication.getContext().getResources().getColor(R.color.vs_cs));
                holder.calllog_detail_calltime.setTextColor(VsApplication.getContext().getResources().getColor(R.color.vs_cs));
            } else {
                holder.calllog_detail_callstate_inorout.setText("");
            }
            CustomLog.i("vsdebug", data.get(position).calltimelength + "---" + data.get(position).callmoney);

            if (data.get(position).directCall != 1) {
                if (data.get(position).calltimelength != null && !"".equals(data.get(position).calltimelength) && !"222".equals(data.get(position).calltimelength) && data.get(position).callmoney != null && !"".equals(data.get(position).callmoney) && Double.valueOf(data.get(position).callmoney) > 0 && !"3".equals(data.get
						(position).ctype) && !"00分00秒".equals(data.get(position).calltimelength)) {
                    holder.calllog_detail_calltime.setText(data.get(position).calltimelength);
                } else {
                    if (data.get(position).directCall == 2 && !"3".equals(data.get(position).ctype)) {
                        holder.calllog_detail_calltime.setText("接通");
                    } else {
                        holder.calllog_detail_calltime.setText("未接通");
                    }
                }
            } else {
                if (data.get(position).calltimelength != null && !"".equals(data.get(position).calltimelength) && !"222".equals(data.get(position).calltimelength) && !"3".equals
						(data.get(position).ctype) && !"00分00秒".equals(data.get(position).calltimelength) && !"3".equals(data.get(position).ctype)) {
                    holder.calllog_detail_calltime.setText(data.get(position).calltimelength);
                } else {
                    holder.calllog_detail_calltime.setText("未接通");
                }
            }

            if ("3".equals(data.get(position).ctype)) {// 是未接来电
                holder.calllog_detail_calltime.setText("未接通");
            }
        }
        return convertView;
    }

    /**
     * ListView组件类
     *
     * @author 李志
     */
    private class ViewHolder {
        /**
         * 记录时间
         */
        private TextView calllog_detail_time;
        /**
         * 记录类型（呼入、呼出）
         */
        private TextView calllog_detail_callstate_inorout;
        /**
         * 通话时长（时间、未接通）
         */
        private TextView calllog_detail_calltime;
    }
}
