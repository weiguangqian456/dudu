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
import com.weiwei.home.entity.ItemClassEntity;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/6.
 * @description: 分类 - 2级Item
 */

public class ClassifyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ItemClassEntity> mList;

    public ClassifyAdapter(Context mContext, List<ItemClassEntity> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_classify, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder)holder1;
        holder.tv_name.setText(mList.get(position).getTitle());
        Glide.with(mContext).load(mList.get(position).getImageUrl()).into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_name)
        TextView tv_name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
