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
 */

public class SearchGoodsAdapter extends RecyclerView.Adapter<SearchGoodsAdapter.MyViewHolder> {

    private View view;
    private Context context;
    private List<ProductBean> recordsBeanList;
    private static final String TAG = "LocalGoodsAdapter";
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};
    private String columnId;
    private String isExchange;

    public SearchGoodsAdapter(Context context, List<ProductBean> recordsBeanList, String columnId, String isExchange) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.columnId = columnId;
        this.isExchange = isExchange;
    }

    @Override
    public SearchGoodsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "y":   //兑换专区
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv4, parent, false);
                    break;
                default:   //非兑换专区
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv2, parent, false);
                    break;
            }
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv2, parent, false);
        }
        SearchGoodsAdapter.MyViewHolder viewHolder = new SearchGoodsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchGoodsAdapter.MyViewHolder holder, final int position) {
        ProductBean bean = recordsBeanList.get(position);
        final String image = JudgeImageUrlUtils.isAvailable(bean.getPicture());
        final String name = bean.getProductName();
        int contrastSource = bean.getContrastSource();
        String comparePrice = bean.getJdPrice();
        final String price = bean.getPrice();
        String ecoPrice = DealEcnomicalMoneyUtils.get(comparePrice, price, 1);
        final String productNo = bean.getProductNo();
        String conversionPrice = bean.getConversionPrice();   //兑换价
        String coupon = bean.getCoupon();  //优惠券
        int spu = bean.getSpu();  //库存

        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "n":  //非兑换专区
                    Glide.with(context).load(image).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(holder.imageView);
                    if (!StringUtils.isEmpty(name)) {
                        holder.name.setText(name);
                    }
//                    holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    if (!StringUtils.isEmpty(comparePrice)) {
                        holder.comparePrice.setText("¥" + comparePrice);
                    } else {
                        holder.comparePrice.setText("");
                    }

                    if (!StringUtils.isEmpty(price)) {
                        holder.price.setText("¥" + price);
                    } else {
                        holder.comparePrice.setText("");
                    }

                    if (!StringUtils.isEmpty(ecoPrice)) {
                        holder.ecoPrice.setText("立省 ¥" + ecoPrice);
                    } else {
                        holder.ecoPrice.setText("");
                    }

                    if (!StringUtils.isEmpty(productNo)) {
                        holder.contenRl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] key = new String[]{"productNo", "productImage", "productName","productPrice", "columnId"};
                                String[] value = new String[]{productNo, image, name, price,columnId};
                                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                            }
                        });
                    }
                    break;
                case "y":    //兑换专区
                    Glide.with(context).load(image).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(holder.changeShopImage);
                    if (!StringUtils.isEmpty(name)) {
                        holder.changeShopNameTv.setText(name);
                    }

                    if (!StringUtils.isEmpty(comparePrice)) {
                        holder.changeShopPriceTv.setText("¥" + comparePrice);
                    } else {
                        holder.changeShopPriceTv.setText("");
                    }

                    //兑换价
                    if (!StringUtils.isEmpty(conversionPrice)) {
                        holder.changeShopNeedMoneyTv.setText("只需¥" + conversionPrice);
                    } else {
                        holder.changeShopNeedMoneyTv.setText("");
                    }

                    //加油余额
                    if (!StringUtils.isEmpty(coupon)) {
                        holder.CanUseCouponRl.setVisibility(View.VISIBLE);
                        holder.changeShopCouponTv.setText(coupon);
                    } else {
                        holder.CanUseCouponRl.setVisibility(View.GONE);
                    }

                    if (!StringUtils.isEmpty(coupon)) {
                        holder.CanUseCouponRl.setVisibility(View.VISIBLE);
                        holder.changeShopCouponTv.setText(coupon);
                    } else {
                        holder.CanUseCouponRl.setVisibility(View.GONE);
                    }

                    if (!StringUtils.isEmpty(productNo)) {
                        holder.changeContenRl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "columnId"};
                                String[] value = new String[]{productNo, image, name,price, columnId};
                                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        } else {
            Glide.with(context).load(image).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(holder.imageView);
            if (!StringUtils.isEmpty(name)) {
                holder.name.setText(name);
            }
//            holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if (!StringUtils.isEmpty(comparePrice)) {
                holder.comparePrice.setText("¥" + comparePrice);
            } else {
                holder.comparePrice.setText("");
            }

            if (!StringUtils.isEmpty(price)) {
                holder.price.setText("¥" + price);
            } else {
                holder.comparePrice.setText("");
            }

            if (!StringUtils.isEmpty(ecoPrice)) {
                holder.ecoPrice.setText("立省 ¥" + ecoPrice);
            } else {
                holder.ecoPrice.setText("");
            }

            if (!StringUtils.isEmpty(productNo)) {
                holder.contenRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "columnId"};
                        String[] value = new String[]{productNo, image, name, price,columnId};
                        SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
//                    ((Activity) context).finish();
                    }
                });
            }
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

        /**
         * 兑换专区相关
         */
        private RelativeLayout changeContenRl;
        private ImageView changeShopImage;
        private TextView changeShopNameTv;
        private TextView changeShopPriceTv;
        private TextView changeShopNeedMoneyTv;
        private TextView changeShopCouponTv;
        private RelativeLayout CanUseCouponRl;  //可用加油余额
        private TextView changeShopSpuTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            contenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            comparePrice = (TextView) itemView.findViewById(R.id.tv_compareprice);
            price = (TextView) itemView.findViewById(R.id.tv_price);
            ecoPrice = (TextView) itemView.findViewById(R.id.tv_ecoprice);

            changeContenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            changeShopImage = (ImageView) itemView.findViewById(R.id.iv_image);
            changeShopNameTv = (TextView) itemView.findViewById(R.id.tv_name);
            changeShopPriceTv = (TextView) itemView.findViewById(R.id.tv_price);
            changeShopNeedMoneyTv = (TextView) itemView.findViewById(R.id.tv_conversionrice);
            changeShopCouponTv = (TextView) itemView.findViewById(R.id.tv_ecoprice);
            CanUseCouponRl = (RelativeLayout) itemView.findViewById(R.id.rl_ecoprice);
        }
    }
}
