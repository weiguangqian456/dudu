package com.weiwei.salemall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.DeliveryWay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryWayAdapter extends RecyclerView.Adapter<DeliveryWayAdapter.ItemViewHolder> {

    private Context mContext;
    private List<DeliveryWay> mList = new ArrayList<>();
    private RecycleItemClick mRecycleItemClick;

    public List<DeliveryWay> getList() {
        return mList;
    }

    public void setList(List<DeliveryWay> list) {
        this.mList = list;
    }


    public DeliveryWayAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_way, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final DeliveryWay item = mList.get(position);

        holder.mRtvDeliveryWay.setText(item.name);
        holder.mRtvDeliveryWay.setTextColor(mContext.getResources().getColor(item.check ? R.color.public_color_09C809 : R.color.public_color_CCCCCC));
        holder.mRtvDeliveryWay.getDelegate().setStrokeColor(mContext.getResources().getColor(item.check ? R.color.public_color_09C809 : R.color.public_color_CCCCCC));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCheck();
                item.check = true;

                if (mRecycleItemClick != null) {
                    mRecycleItemClick.onItemClick(holder.getAdapterPosition());
                }
                notifyDataSetChanged();
            }
        });

    }

    public void setRecycleClickListener(RecycleItemClick mRecycleItemClick) {
        this.mRecycleItemClick = mRecycleItemClick;
    }

    private void cancelCheck() {
        for (DeliveryWay address : mList) {
            address.check = false;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rtv_delivery_way)
        RoundTextView mRtvDeliveryWay;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
