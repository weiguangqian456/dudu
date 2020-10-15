package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Created by EDZ on 2018/7/27.
 */

public class LocalGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View view;
    private Context context;
    private List<ProductBean> recordsBeanList;
    private static final String TAG = "LocalGoodsAdapter";
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};
    private String columnId;
    private String isExchange;

    private int mFooterState;

    public LocalGoodsAdapter(Context context, List<ProductBean> recordsBeanList, String columnId, String isExchange) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.columnId = columnId;
        this.isExchange = isExchange;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == Constant.RECYCLE_TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        }else  if(!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "n":   //非兑换专区
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_item, parent, false);
                    break;
                case "y":   //兑换专区
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_itemv3, parent, false);
                    break;
                default:
                    break;
            }
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_localgoods_item, parent, false);
        }
        LocalGoodsAdapter.MyViewHolder viewHolder = new LocalGoodsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if(holder1 instanceof MyViewHolder){
            onBindItem(holder1,position);
        }else if(holder1 instanceof FooterViewHolder){
            Boolean isNowLoad = mFooterState == Constant.RECYCLE_FOOTER_LOAD;
            String str =  isNowLoad ? context.getString(R.string.more_load) : context.getString(R.string.more_period);
            ((FooterViewHolder)holder1).mTvFooter.setText(str);
            ((FooterViewHolder)holder1).progress_bar.setVisibility(isNowLoad ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 重置加载中状态
     * @param mFooterState 状态
     */
    public void initFooterState(int mFooterState){
        this.mFooterState = mFooterState;
        notifyItemChanged(getItemCount() - 1);
    }

    private void onBindItem(RecyclerView.ViewHolder holder1, int position){
        MyViewHolder holder = (MyViewHolder)holder1;
        ProductBean bean = recordsBeanList.get(position);
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
        if(!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "n":  //非兑换专区
                    Glide.with(context).load(image).apply(options).into(holder.imageView);
                    if (!StringUtils.isEmpty(name)) {
                        holder.name.setText(name);
                    }
//                    holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    if (!StringUtils.isEmpty(comparePrice)) {
                        holder.comparePrice.setText(comparePrice2);
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
                                String[] key = new String[]{"productNo", "productImage", "productName","productPrice","columnId"};
                                String[] value = new String[]{productNo, image, name,price,columnId};
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
                                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice","columnId"};
                                String[] value = new String[]{productNo, image, name, price,columnId};
                                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }else {
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
        }
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == recordsBeanList.size() ? Constant.RECYCLE_TYPE_FOOTER : Constant.RECYCLE_TYPE_ITEM;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
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
            changeShopImage = (ImageView) itemView.findViewById(R.id.image);
            changeShopNameTv = (TextView) itemView.findViewById(R.id.tv_name);
            changeShopPriceTv = (TextView) itemView.findViewById(R.id.tv_price);
            changeShopNeedMoneyTv = (TextView) itemView.findViewById(R.id.tv_conversionrice);
            changeShopCouponTv = (TextView) itemView.findViewById(R.id.tv_ecoprice);
            CanUseCouponRl = (RelativeLayout) itemView.findViewById(R.id.rl_ecoprice);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_footer)
        TextView mTvFooter;
        @BindView(R.id.progress_bar)
        ProgressBar progress_bar;
        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
