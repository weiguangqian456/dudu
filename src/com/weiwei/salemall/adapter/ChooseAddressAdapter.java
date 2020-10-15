package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.bean.AddressEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseAddressAdapter extends RecyclerView.Adapter<ChooseAddressAdapter.ItemViewHolder> {

    private Context mContext;
    private List<AddressEntity> mList = new ArrayList<>();
    private RecycleItemClick mRecycleItemClick;

    public List<AddressEntity> getList() {
        return mList;
    }

    public void setList(List<AddressEntity> list) {
        this.mList = list;
    }


    public ChooseAddressAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_address, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final AddressEntity item = mList.get(position);

        holder.mTvAddressName.setText(TextDisposeUtils.dispseMoneyText(item.getAllAddress()));
        holder.mTvAddressName.setTextColor(mContext.getResources().getColor(item.check ? R.color.public_color_333333 : R.color.public_color_666666));
        holder.mIvLeftChoose.setImageResource(item.check ? R.drawable.ic_choose_address_left_selected : R.drawable.ic_choose_address_left_normal);
        holder.mIvRightChoose.setVisibility(item.check ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCheck();
                item.check = true;

                if (mRecycleItemClick != null) {
                    mRecycleItemClick.onItemClick(position);
                }
                notifyDataSetChanged();
            }
        });

    }

    public void setRecycleClickListener(RecycleItemClick mRecycleItemClick) {
        this.mRecycleItemClick = mRecycleItemClick;
    }

    public void cancelCheck() {
        for (AddressEntity address : mList) {
            address.check = false;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_address_name)
        TextView mTvAddressName;
        @BindView(R.id.iv_left_choose)
        ImageView mIvLeftChoose;
        @BindView(R.id.iv_right_choose)
        ImageView mIvRightChoose;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
