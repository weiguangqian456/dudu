package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.OrderEnsureEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/5/23.
 *         购物车进来确认订单  RecycleViewAdapter
 */

public class OrderEnsureAdapter extends RecyclerView.Adapter<OrderEnsureAdapter.MyViewHolder> {
    private Context context;
    private List<OrderEnsureEntity> strList;

    public OrderEnsureAdapter(Context context, List<OrderEnsureEntity> strList) {
        this.context = context;
        this.strList = strList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_order_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OrderEnsureEntity entity = strList.get(position);
        String image = JudgeImageUrlUtils.isAvailable(entity.getProductImage());
        Glide.with(context).load(image).into(holder.iv_shop_image);

        String shopname = entity.getProductName();
        if (!StringUtils.isEmpty(shopname)) {
            holder.tv_goods_title.setText(shopname);
        }

        final String shopdesc = entity.getProductDesc();
        if (!StringUtils.isEmpty(shopdesc)) {
            holder.property.setText(shopdesc.substring(0, shopdesc.length() - 1));
        }

        String isExchange = entity.getIsExchange();
        String shopMallPrice = entity.getProductMallPrice();
        String conversionPrice = entity.getConversionPrice();
        String coupon = entity.getCoupon();
        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "y":
                    holder.tv_mall_price.setText(coupon + "积分");//"¥" + conversionPrice + " + " +
                    break;
                case "n":
                    if (!StringUtils.isEmpty(shopMallPrice)) {
                        holder.tv_mall_price.setText("¥" + shopMallPrice);
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (!StringUtils.isEmpty(shopMallPrice)) {
                holder.tv_mall_price.setText("¥" + shopMallPrice);
            }
        }

        String shopJdPrice = entity.getProductJdPrice();
//        holder.tv_jd_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (!StringUtils.isEmpty(shopJdPrice)) {
            holder.tv_jd_price.setText("¥" + shopJdPrice);
        }

        int shopNumber = entity.getProductNum();
        if (shopNumber != 0) {
            holder.tv_goods_number.setText("x" + shopNumber + "");
        }

        String shopEconomicalMoney = entity.getProductEcnomicalMoney();
        if (shopEconomicalMoney != null) {
            holder.tv_save_money.setText("省:¥" + shopEconomicalMoney);
        }

        String deliveryMsg = entity.getDeliveryMsg();
        if (!StringUtils.isEmpty(deliveryMsg)) {
            holder.tv_deliveryType.setText(deliveryMsg);
        }
    }

    @Override
    public int getItemCount() {
        return strList == null ? 0 : strList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_shop_image;
        private TextView tv_goods_title;
        private TextView property;
        private TextView tv_goods_number;
        private TextView tv_mall_price;
        private TextView tv_jd_price;
        private TextView tv_save_money;
        private TextView tv_deliveryType;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_shop_image = (ImageView) itemView.findViewById(R.id.iv_shop_image);
            tv_goods_title = (TextView) itemView.findViewById(R.id.tv_goods_title);
            tv_goods_number = (TextView) itemView.findViewById(R.id.tv_goods_number);
            tv_mall_price = (TextView) itemView.findViewById(R.id.tv_mall_price);
            tv_jd_price = (TextView) itemView.findViewById(R.id.tv_jd_price);
            tv_save_money = (TextView) itemView.findViewById(R.id.tv_save_money);
            property = (TextView) itemView.findViewById(R.id.tv_property);
            tv_deliveryType = (TextView) itemView.findViewById(R.id.tv_deliveryType);
        }
    }
}
