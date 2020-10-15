package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/7/27.
 *         兑换专区
 */

public class RedemptionAreaAdapter extends RecyclerView.Adapter<RedemptionAreaAdapter.MyViewHolder> {

    private Context context;
    private List<ProductBean> recordsBeanList;
    private static final String TAG = "LocalGoodsAdapter";
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    public RedemptionAreaAdapter(Context context, List<ProductBean> recordsBeanList) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
    }

    @Override
    public RedemptionAreaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv2, parent, false);
        RedemptionAreaAdapter.MyViewHolder viewHolder = new RedemptionAreaAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RedemptionAreaAdapter.MyViewHolder holder, final int position) {
        ProductBean bean = recordsBeanList.get(position);
        final String image = JudgeImageUrlUtils.isAvailable(bean.getPicture());
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.mall_logits_default);
        Glide.with(context).load(image).apply(options).into(holder.imageView);

        final String name = bean.getProductName();
        if (!StringUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        int contrastSource = bean.getContrastSource();
        String comparePrice = bean.getJdPrice();
//        holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (!StringUtils.isEmpty(comparePrice)) {
            holder.comparePrice.setText(bean.getJdPrice2());
        } else {
            holder.comparePrice.setText("");
        }

        final String price = bean.getPrice();
        if (!StringUtils.isEmpty(price)) {
            holder.price.setText("¥" + price);
        } else {
            holder.comparePrice.setText("");
        }

        String ecoPrice = DealEcnomicalMoneyUtils.get(comparePrice, price, 1);
        if (ecoPrice != null && !ecoPrice.isEmpty()) {
            holder.ecoPrice.setText("立省 ¥" + ecoPrice);
        } else {
            holder.ecoPrice.setText("");
        }


        final String productNo = bean.getProductNo();
        if (!StringUtils.isEmpty(productNo)) {
            holder.contenRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                    String[] value = new String[]{productNo, image, name,price};
                    SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout contenRl;
        private TextView name;
        private TextView comparePrice;
        private TextView price;
        private TextView ecoPrice;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            contenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            comparePrice = (TextView) itemView.findViewById(R.id.tv_compareprice);
            price = (TextView) itemView.findViewById(R.id.tv_price);
            ecoPrice = (TextView) itemView.findViewById(R.id.tv_ecoprice);
        }
    }
}
