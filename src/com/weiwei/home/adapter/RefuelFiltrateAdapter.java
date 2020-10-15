package com.weiwei.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.hwtx.dududh.R;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.RefuelFiltrate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefuelFiltrateAdapter extends RecyclerView.Adapter<RefuelFiltrateAdapter.ItemViewHolder> {

    private Context mContext;
    private List<RefuelFiltrate> mList = new ArrayList<>();
    private RecycleItemClick mRecycleItemClick;
    private boolean mIsAllChoose;

    public void setIsAllChoose(boolean isAllChoose) {
        this.mIsAllChoose = isAllChoose;
    }


    public List<RefuelFiltrate> getList() {
        return mList;
    }

    public void setList(List<RefuelFiltrate> list) {
        this.mList = list;
    }


    public RefuelFiltrateAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refuel_filtrate, parent, false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final RefuelFiltrate item = mList.get(position);

        holder.mRtvFiltrateName.setText(item.filtrateName);

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
                if (!mIsAllChoose) {
                    cancelCheck();
                    item.check = true;
                } else {
                    item.check = !item.check;
                }

                if (mRecycleItemClick != null && !mIsAllChoose) {
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
        for (RefuelFiltrate address : mList) {
            address.check = false;
        }
    }

    public List<RefuelFiltrate> getChooseList() {
        List<RefuelFiltrate> temp = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).check) {
                temp.add(mList.get(i));
            }
        }
        return temp;
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
