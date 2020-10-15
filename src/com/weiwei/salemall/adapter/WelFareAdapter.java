package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ShopRecordsEntity;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/5/23.
 *         福利
 */

public class WelFareAdapter extends RecyclerView.Adapter<WelFareAdapter.MyViewHolder> {

    private Context context;
    private List<ShopRecordsEntity> dataList;
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    public WelFareAdapter(Context context, List<ShopRecordsEntity> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_for_you_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ShopRecordsEntity entity = dataList.get(position);
        final String imageUrl = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        Glide.with(context).load(imageUrl).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(holder.iv_shop);
        holder.tv_introduction.setText(entity.getProductName());
        int contrastSource = entity.getContrastSource();
        holder.tv_jd_price.setText(entity.getJdPrice2());
//        holder.tv_jd_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_mall_price.setText("¥" + entity.getPrice());
        String economicalPrice = DealEcnomicalMoneyUtils.get(entity.getJdPrice(), entity.getPrice(), 1);
        holder.ecoprice.setText("立省¥" + economicalPrice);
        holder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] key = new String[]{"productNo","productImage","productName","productPrice"};
                String[] value = new String[]{entity.getProductNo(),imageUrl,entity.getProductName(),entity.getPrice()};
                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
//                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_introduction;
        private TextView tv_mall_price;
        private TextView tv_jd_price;
        private ImageView iv_shop;
        private LinearLayout ll_content;
        private TextView ecoprice;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_shop = (ImageView) itemView.findViewById(R.id.iv_shop);
            tv_introduction = (TextView) itemView.findViewById(R.id.tv_introduction);
            tv_jd_price = (TextView) itemView.findViewById(R.id.tv_jd_price);
            tv_mall_price = (TextView) itemView.findViewById(R.id.tv_mall_price);
            ll_content = (LinearLayout) itemView.findViewById(R.id.ll_content);
            ecoprice = (TextView) itemView.findViewById(R.id.tv_ecoprice);
        }
    }
}
