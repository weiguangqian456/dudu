package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.base.widgets.CustomToast;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;


/**
 * @author hc
 * @date by hc on 2019/2/27.
 * @description: 新的商企采购 内容使用的原来的（LocalGoodsAdapter）
 */

public class NewGoodsAdapter extends BaseRecycleAdapter {

    private View view;
    private Context context;
    private static final String TAG = "LocalGoodsAdapter";
    //    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};
    private String columnId;
    private String isExchange;

    public NewGoodsAdapter(Context context, String columnId, String isExchange) {
        super(context);
        this.context = context;
        this.columnId = columnId;
        this.isExchange = isExchange;
    }


    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                //非兑换专区
                case "n":
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_item, parent, false);
                    break;
                //兑换专区
                case "y":
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv3, parent, false);
                    break;
                default:
                    break;
            }
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_item, parent, false);
        }
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = ((ItemViewHolder) holder1);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position % 2 == 0) {
            layoutParams.leftMargin = DensityUtils.dp2px(mContext, 10);
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 3);
        } else {
            layoutParams.leftMargin = DensityUtils.dp2px(mContext, 3);
            layoutParams.rightMargin = DensityUtils.dp2px(mContext, 10);
        }
        holder.itemView.setLayoutParams(layoutParams);

        if (position >= getList().size()) {
            ToastAstrictUtils.getInstance().show("ERROR: Index");
            return;
        }
        ProductBean bean = JSONObject.parseObject(getItem(position).toString(), ProductBean.class);
        final String image = JudgeImageUrlUtils.isAvailable(bean.getPicture());
        final String name = bean.getProductName();
        int contrastSource = bean.getContrastSource();
        String comparePrice = bean.getJdPrice();
        String comparePrice2 = bean.getJdPrice2();
        final String price = bean.getPrice();
        String ecoPrice = DealEcnomicalMoneyUtils.get(comparePrice, price, 1);
        final String productNo = bean.getProductNo();
        String conversionPrice = bean.getConversionPrice();   //兑换价
        String coupon = bean.getCoupon();  //优惠券
        int spu = bean.getSpu();  //库存

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.mall_logits_default);
        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "n":  //非兑换专区
                    Glide.with(context).load(image).apply(options).into(holder.imageView);
                    if (!StringUtils.isEmpty(name)) {
                        holder.name.setText(name);
                    }
//                    holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    if (!StringUtils.isEmpty(comparePrice2)) {
                        holder.comparePrice.setText(comparePrice2);
                    } else {
                        holder.comparePrice.setText("");
                    }

                    if (!StringUtils.isEmpty(price)) {
                        holder.price.setText("VIP" + TextDisposeUtils.dispseMoneyText(price));
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
                                String[] value = new String[]{productNo, image, name, price, columnId};
                                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
//                    ((Activity) context).finish();
                            }
                        });
                    }
                    break;
                case "y":    //兑换专区
                    Glide.with(context).load(image).apply(options).into(holder.changeShopImage);
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
                                String[] value = new String[]{productNo, image, name, price, columnId};
                                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        } else {
            Glide.with(context).load(image).apply(options).into(holder.imageView);
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
                holder.price.setText("VIP" + TextDisposeUtils.dispseMoneyText(price));
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
                        String[] value = new String[]{productNo, image, name, price, columnId};
                        SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                    }
                });
            }
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * 普通专区
         */
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
        //可用加油余额
        private RelativeLayout CanUseCouponRl;
        private TextView changeShopSpuTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            contenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            comparePrice = (TextView) itemView.findViewById(R.id.tv_compareprice);
            price = (TextView) itemView.findViewById(R.id.tv_price);
            ecoPrice = (TextView) itemView.findViewById(R.id.tv_ecoprice);

            changeContenRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            changeShopImage = (ImageView) itemView.findViewById(R.id.image);
            changeShopNameTv = (TextView) itemView.findViewById(R.id.tv_name);
            changeShopPriceTv = (TextView) itemView.findViewById(R.id.tv_price);
            changeShopNeedMoneyTv = (TextView) itemView.findViewById(R.id.tv_conversionrice);
            changeShopCouponTv = (TextView) itemView.findViewById(R.id.tv_ecoprice);
            CanUseCouponRl = (RelativeLayout) itemView.findViewById(R.id.rl_ecoprice);
        }
    }
}
