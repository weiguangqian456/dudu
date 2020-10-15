package com.weiwei.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.util.image.StringUtils;
import com.weiwei.home.entity.SeckillTab;

import java.util.List;

public class InviteDetailAdapter extends RecyclerView.Adapter<InviteDetailAdapter.MyViewHolder> {

    private List<SeckillTab.Records> recordsList;
    private Context context;
    public InviteDetailAdapter(Context context,List<SeckillTab.Records> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_detail_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SeckillTab.Records records = recordsList.get(position);
        holder.tvMoney.setText(records.amountGun);
        if(!StringUtils.isEmpty(records.payTime)) {
            holder.tvTime.setText(records.payTime.substring(0,10));
            holder.tvHour.setText(records.payTime.substring(10,records.payTime.length()));
        }
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvHour;
        private TextView tvMoney;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvHour = (TextView) itemView.findViewById(R.id.tv_hour);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money);
        }
    }
}
