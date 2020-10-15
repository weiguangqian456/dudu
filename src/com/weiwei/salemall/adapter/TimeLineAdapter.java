package com.weiwei.salemall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.LogitsEntity;
import com.weiwei.salemall.utils.DensityUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/6/22.
 */

public class TimeLineAdapter extends BaseAdapter {

    private Context mcontext = null;
    private List<LogitsEntity> mlist = null;
    private LayoutInflater minflater;

    public TimeLineAdapter(Context context, List<LogitsEntity> list) {
        this.mcontext = context;
        minflater = LayoutInflater.from(context);
        mlist = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (null != mlist) {
            return mlist.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (null != mlist) {
            return mlist.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHold viewHold;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = minflater.inflate(R.layout.lv_logistics_item, null);
            viewHold.imageView1 = (ImageView) convertView.findViewById(R.id.mgView_logistic_tracking_status);
            viewHold.textView1 = (TextView) convertView.findViewById(R.id.tv_logistic_tracking_address);
            viewHold.textView2 = (TextView) convertView.findViewById(R.id.tv_logistic_tracking_time);
            viewHold.line1 = (View) convertView.findViewById(R.id.View_logistic_tracking_line1);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        if (position == 0) {
            viewHold.line1.setVisibility(View.INVISIBLE);
            viewHold.imageView1.setImageResource(R.drawable.mall_logits_first);
            viewHold.textView1.setTextColor(R.color.vs_blue);
            viewHold.textView2.setTextColor(R.color.vs_blue);

            int size1 = (int) mcontext.getResources().getDimension(R.dimen.w_20_dip);
            viewHold.textView1.setTextSize(DensityUtils.px2sp(mcontext, size1));
            int size2 = (int) mcontext.getResources().getDimension(R.dimen.w_15_dip);
            viewHold.textView1.setTextSize(DensityUtils.px2sp(mcontext, size2));
        } else {
            viewHold.line1.setVisibility(View.VISIBLE);
            viewHold.imageView1.setImageResource(R.drawable.mall_logits_second);
            viewHold.textView1.setTextColor(R.color.black);
            viewHold.textView2.setTextColor(R.color.black);

            int size1 = (int) mcontext.getResources().getDimension(R.dimen.w_18_dip);
            viewHold.textView1.setTextSize(DensityUtils.px2sp(mcontext, size1));
            int size2 = (int) mcontext.getResources().getDimension(R.dimen.w_13_dip);
            viewHold.textView1.setTextSize(DensityUtils.px2sp(mcontext, size2));
        }
        viewHold.textView1.setText(mlist.get(position).getAcceptStation());
        viewHold.textView2.setText(mlist.get(position).getAcceptTime());
        return convertView;
    }


    private final static class ViewHold {
        ImageView imageView1;
        View line1;
        TextView textView1;
        TextView textView2;
    }
}
