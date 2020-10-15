package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.entity.ShareItemEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/21.
 * @description: Adapter
 */

public class CustomShareAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ShareItemEntity> mList;
    private RecycleItemClick mRecycleItemClick;

    public CustomShareAdapter(Context mContext, List<ShareItemEntity> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_share, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final int finalPosition = position;
        ItemViewHolder holder = (ItemViewHolder)holder1;
        Glide.with(mContext).load(mList.get(position).getImgId()).into(holder.iv_icon);
        holder.tv_content.setText(mList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecycleItemClick != null){
                    mRecycleItemClick.onItemClick(finalPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setRecycleClickListener(RecycleItemClick mRecycleItemClick){
        this.mRecycleItemClick = mRecycleItemClick;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_icon)
        ImageView iv_icon;
        @BindView(R.id.tv_content)
        TextView tv_content;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
