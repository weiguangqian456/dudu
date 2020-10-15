package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.salemall.utils.DensityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/21.
 * @description: Adapter
 */

public class SeckillTabAdapter extends RecyclerView.Adapter<SeckillTabAdapter.ItemViewHolder> {

    private Context mContext;
    private List<SeckillTab.Records> mList = new ArrayList<>();

    public List<SeckillTab.Records> getList() {
        return mList;
    }

    public void setList(List<SeckillTab.Records> list) {
        this.mList = list;
    }

    private RecycleItemClick mRecycleItemClick;

    public SeckillTabAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seckill_tab, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final int finalPosition = position;
        final SeckillTab.Records item = mList.get(position);

        SimpleDateFormat formatDate = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        ;
        holder.mTvSeckillDate.setText(formatDate.format(item.startTime));
        holder.mTvSeckillTime.setText(formatTime.format(item.startTime));

        int color = item.check ? R.color.color_red : R.color.public_color_333333;
        holder.mTvSeckillDate.setTextColor(mContext.getResources().getColor(color));
        holder.mTvSeckillTime.setTextColor(mContext.getResources().getColor(color));

        if (position == 0) {
            holder.mViewLine.setVisibility(View.GONE);
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (mList.size() == 1) {
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 10) + 99;
        } else {
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 0);
        }
        holder.itemView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCheck();
                item.check = true;
                if (mRecycleItemClick != null) {
                    mRecycleItemClick.onItemClick(finalPosition);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void cancelCheck() {
        for (SeckillTab.Records records : mList) {
            records.check = false;
        }
    }

    public void setRecycleClickListener(RecycleItemClick mRecycleItemClick) {
        this.mRecycleItemClick = mRecycleItemClick;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_seckill_date)
        TextView mTvSeckillDate;
        @BindView(R.id.tv_seckill_time)
        TextView mTvSeckillTime;
        @BindView(R.id.view_line)
        View mViewLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
