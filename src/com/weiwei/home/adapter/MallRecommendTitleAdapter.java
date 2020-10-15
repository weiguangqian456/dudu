package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.home.test.RecycleItemClickListener;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.entity.ReadyItemRecommendEntity;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/8.
 * @description: 首页 - 推荐的选择Adapter
 */

public class MallRecommendTitleAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<? extends ReadyItemRecommendEntity> mList;

    private RecycleItemClickListener itemClickListener;
    private int selectPosition;

    public MallRecommendTitleAdapter(Context mContext, List<? extends ReadyItemRecommendEntity> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_recommend_classify,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final int finalPosition = position;
        ItemViewHolder holder = (ItemViewHolder)holder1;
        boolean isSelect = selectPosition == position;
        holder.tv_name.setText(mList.get(position).getName());
        holder.tv_explain.setText(mList.get(position).getExplain());
        holder.tv_name.setTextColor(ContextCompat.getColor(mContext,isSelect ? R.color.color_theme_4 : R.color.color_text_4));
        holder.tv_explain.setTextColor(ContextCompat.getColor(mContext, isSelect ? R.color.color_white : R.color.color_text_d));
        holder.mIvTextBg.setVisibility(isSelect ? View.VISIBLE : View.GONE);
//        holder.tv_name.setBackgroundResource(isSelect ?  R.drawable.bg_radius_theme_new : R.drawable.bg_radius_white);
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                selectPosition = finalPosition;
                notifyDataSetChanged();
                if(itemClickListener != null){
                    itemClickListener.onItemClick(finalPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_explain)
        TextView tv_explain;
        @BindView(R.id.mIvTextBg)
        ImageView mIvTextBg;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
