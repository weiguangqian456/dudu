package com.weiwei.rider.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.salemall.base.Const;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<String> imgList;
    private boolean isShow;

    public ImageAdapter(Context mContext, List<String> imgList,boolean isShow) {
        this.mContext = mContext;
        this.imgList = imgList;
        this.isShow = isShow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == imgList.size()) {
            holder.ivImage.setImageResource(R.drawable.add_image);
        }else {
            Glide.with(mContext).load(Const.BASE_IMAGE_URL + imgList.get(position))
                    .into(holder.ivImage);
        }
        holder.ivImage.setOnClickListener(view -> {
            if(position == imgList.size()) {
                if(mOnClickListener != null) {
                    mOnClickListener.onItemClicked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(isShow) {
            return imgList.size() + 1;
        }else {
            return imgList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
        }
    }

    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onItemClicked();
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
}


