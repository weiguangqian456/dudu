package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.RefuelDetail;
import com.weiwei.salemall.bean.RefuelFiltrate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OilNoAdapter extends RecyclerView.Adapter<OilNoAdapter.ItemViewHolder> {

    private Context mContext;
    private List<RefuelDetail.OilPriceList> mList = new ArrayList<>();
    private RecycleItemClick mRecycleItemClick;

    public List<RefuelDetail.OilPriceList> getList() {
        return mList;
    }

    public void setList(List<RefuelDetail.OilPriceList> list) {
        this.mList = list;
    }


    public OilNoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oil_no, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final RefuelDetail.OilPriceList item = mList.get(position);

        holder.mRtvFiltrateName.setText(item.oilName);

        RoundViewDelegate delegate = holder.mRtvFiltrateName.getDelegate();

        if (item.check) {
            holder.mRtvFiltrateName.setTextColor(mContext.getResources().getColor(R.color.public_color_white));
            delegate.setBackgroundColor(mContext.getResources().getColor(R.color.public_color_EC6941));
            delegate.setStrokeColor(mContext.getResources().getColor(R.color.transparency));
            delegate.setStrokeWidth(1);
        } else {
            holder.mRtvFiltrateName.setTextColor(mContext.getResources().getColor(R.color.public_color_666666));
            delegate.setBackgroundColor(mContext.getResources().getColor(R.color.public_color_white));
            delegate.setStrokeColor(mContext.getResources().getColor(R.color.public_color_666666));
            delegate.setStrokeWidth(1);
        }

        holder.mRtvFiltrateName.setOnClickListener(new View.OnClickListener() {
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
        for (RefuelDetail.OilPriceList address : mList) {
            address.check = false;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rtv_filtrate_name)
        RoundTextView mRtvFiltrateName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
