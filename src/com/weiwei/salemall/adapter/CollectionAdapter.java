package com.weiwei.salemall.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * @author Created by EDZ on 2018/7/25.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.MyViewHolder> {
    private Context context;
    private List<CollectionEntity> recordsBeanList;
    private static final String TAG = "CollectionAdapter";
    private String cancleCollectionMsg = null;

    public CollectionAdapter(Context context, List<CollectionEntity> recordsBeanList) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_collection_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CollectionEntity bean = recordsBeanList.get(position);
        String name = bean.getStoreName();
        if (!StringUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        String distance = bean.getDistance();
        if (!StringUtils.isEmpty(distance)) {
            holder.distance.setText(distance);
        }

        String address = bean.getStoreAddress();
        if (!StringUtils.isEmpty(address)) {
            holder.address.setText(address);
        }

        String perPrice = bean.getAverageConsumption();
        if (!StringUtils.isEmpty(perPrice)) {
            holder.perPrice.setText("人均消费：¥" + perPrice);
        }

        String discount = bean.getPropaganda();
        if (!StringUtils.isEmpty(discount)) {
            holder.discount.setText(discount);
        }

        final String storeNo = bean.getStoreNo();
        holder.contenRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipPageUtils.getInstance(context).skipPage(StoreDetailActivity.class, "storeNo", storeNo);
            }
        });

        String image = JudgeImageUrlUtils.isAvailable(bean.getIconUrl());
        if (!StringUtils.isEmpty(image)) {
            Glide.with(context).load(image).into(holder.image);
        }

        holder.contenRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context).setMessage("确定要删除该店铺的收藏吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recordsBeanList.remove(position);
                        deleteCollection(bean.getStoreNo());
                        notifyDataSetChanged();
                        if (recordsBeanList == null || recordsBeanList.size() == 0) {     //收藏数据为空
                            MessageEvent event = new MessageEvent();
                            event.setMessage("0");
                            EventBus.getDefault().post(event);
                        }
                    }
                }).setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return false;
            }
        });
    }

    /**
     * 删除对应的收藏
     *
     * @param storeNo
     */
    private void deleteCollection(String storeNo) {
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        Map<String, String> params = new HashMap<>();
        params.put("favoriteType", String.valueOf(1));
        params.put("targetNo", storeNo);
        retrofit2.Call<ResultEntity> call = api.cancelDetailFavorite(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                cancleCollectionMsg = result.getMsg();
                Log.e(TAG, "取消收藏msg===>" + cancleCollectionMsg);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout contenRl;
        private TextView name;
        private TextView distance;
        private TextView perPrice;
        private TextView address;
        private TextView discount;
        private ImageView image;


        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            contenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            distance = (TextView) itemView.findViewById(R.id.tv_distance);
            perPrice = (TextView) itemView.findViewById(R.id.tv_perprice);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            discount = (TextView) itemView.findViewById(R.id.tv_discount);
        }
    }
}
