package com.weiwei.salemall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.ProductCommentEntity;
import com.weiwei.salemall.widget.StarBar;

import java.util.List;

/**
 * @author Created by EDZ on 2018/6/1.
 *         商品评论RecycleView  Adapter
 */

public class GoodsCommentAdapter extends RecyclerView.Adapter<GoodsCommentAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ProductCommentEntity> dataList;
    private GoodsCommentAdapter.OnItemClickListener mOnItemClickListener;


    //点击事件的接口
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public GoodsCommentAdapter(Context context, List<ProductCommentEntity> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public GoodsCommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_goods_comment_item, parent, false);
        GoodsCommentAdapter.MyViewHolder viewHolder = new GoodsCommentAdapter.MyViewHolder(view);
        //将创建的view注册点击事件
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GoodsCommentAdapter.MyViewHolder holder, int position) {
        //星星评价数量
        holder.startbar_comment.setStarMark((float) Double.parseDouble(dataList.get(position).getComprehensive()));
        holder.startbar_comment.setDisableClick(true);
        holder.tv_nickName.setText(dataList.get(position).getNickName());
        holder.tv_time.setText(dataList.get(position).getCreateTime());
        holder.tv_comment_content.setText(dataList.get(position).getContent());
        holder.tv_good_paramter.setText(dataList.get(position).getProductDesc());

        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    public void setOnItemClickListener(GoodsCommentAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_head_image;
        private TextView tv_nickName;
        private StarBar startbar_comment;
        private TextView tv_time;
        private TextView tv_comment_content;
        private TextView tv_good_paramter;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_head_image = (ImageView) itemView.findViewById(R.id.iv_head_image);
            tv_nickName = (TextView) itemView.findViewById(R.id.tv_nickName);
            startbar_comment = (StarBar) itemView.findViewById(R.id.star_describe);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_good_paramter = (TextView) itemView.findViewById(R.id.tv_good_paramter);
        }
    }
}