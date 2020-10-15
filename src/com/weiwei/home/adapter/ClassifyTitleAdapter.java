package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.home.test.RecycleItemClickListener;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.entity.ReadyItemClassifyTitleEntity;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/6.
 * @description:
 */

public class ClassifyTitleAdapter extends RecyclerView.Adapter implements ViewPager.OnPageChangeListener{

    private RecycleItemClickListener itemClickListener;
    private Context mContext;
    private int selectPosition;
    private List<? extends ReadyItemClassifyTitleEntity> mList;

    public ClassifyTitleAdapter(Context mContext, List<? extends ReadyItemClassifyTitleEntity> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public void setItemClickListener(RecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_classify_title, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final int finalPosition = position;
        ItemViewHolder holder =  (ItemViewHolder) holder1;
        holder.tv_title.setText(mList.get(position).getTitle());
        holder.view_select.setVisibility(position == selectPosition ? View.VISIBLE : View.INVISIBLE);
        holder.tv_title.setTextColor(ContextCompat.getColor(mContext, position == selectPosition ? R.color.color_theme_4 : R.color.default_text));
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.view_select)
        View view_select;
        @BindView(R.id.tv_title)
        TextView tv_title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
