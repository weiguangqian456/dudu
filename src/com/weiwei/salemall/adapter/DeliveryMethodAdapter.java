package com.weiwei.salemall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.DeliveryModeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Created by EDZ on 2018/5/23.
 *         配送方式
 */

public class DeliveryMethodAdapter extends RecyclerView.Adapter<DeliveryMethodAdapter.MyViewHolder> {
    private Context context;
    private List<DeliveryModeBean> recordsBeanList;
    private static final String TAG = "DeliveryMethodAdapter";
    private Map<Integer, CheckBox> cbMap = new HashMap<>();
    private int checkedPosition = -1;
    private String deliveryType;

    public DeliveryMethodAdapter(Context context, List<DeliveryModeBean> recordsBeanList, String deliveryType) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.deliveryType = deliveryType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_deliverymethod_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DeliveryModeBean entity = recordsBeanList.get(position);
        String name = entity.getName();
        holder.tv_name.setText(name);

        cbMap.put(position, holder.cb_address_default);

        holder.cb_address_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    clearStatus(position);
                    checkedPosition = position;
                    getCheckedPosition();
                }
            }
        });
        if (deliveryType.equals(entity.getCode())) {
            holder.cb_address_default.setChecked(true);
        }
    }

    private void clearStatus(int position) {
        //遍历map
        Iterator entries = cbMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer) entry.getKey();
            CheckBox value = (CheckBox) entry.getValue();
            if (position != key) {
                value.setChecked(false);
            } else {
                value.setChecked(true);
            }
        }
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        public CheckBox cb_address_default;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_delivery_method);
            cb_address_default = (CheckBox) itemView.findViewById(R.id.cb_delivery_method);
        }
    }
}
