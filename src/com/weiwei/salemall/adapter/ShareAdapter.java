package com.weiwei.salemall.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.ShareItemBean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/9/28.
 *         Describe
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyViewHolder> implements View.OnClickListener{
    List<ShareItemBean> shareInfos;
    private ShareAdapter.OnItemClickListener mOnItemClickListener;

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    //点击事件的接口
    public  interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ShareAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ShareAdapter(List<ShareItemBean> shareInfos) {
        this.shareInfos = shareInfos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        v.setOnClickListener(this);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ShareItemBean shareInfo = shareInfos.get(position);
        holder.imageView.setImageResource(shareInfo.getImgId());
        holder.textView.setText(shareInfo.getName());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return shareInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView imageView;
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.img_share);
            textView = (TextView) itemView.findViewById(R.id.tvShare);
        }
    }
}
