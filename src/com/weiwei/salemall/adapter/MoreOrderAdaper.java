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
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.OrderDetailActivity;
import com.weiwei.salemall.bean.OrderDetailBean;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/7/9.
 *         购物车下单适配器
 */

public class MoreOrderAdaper extends RecyclerView.Adapter<MoreOrderAdaper.MyViewHolder> {
    private Context context;
    private List<OrderDetailBean> recordsBeanList;
    private String orderId;
    private String orderStatus;
    private boolean canClick;
    private String orderType;
    private String localBy;

    public MoreOrderAdaper(Context context, List<OrderDetailBean> recordsBeanList, String orderId, String orderStatus, boolean canClick, String orderType,String localBy) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.canClick = canClick;
        this.orderType = orderType;
        this.localBy = localBy;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final OrderDetailBean bean = recordsBeanList.get(position);
        String desc = bean.getProductModeDesc();
        String deliveryMsg = bean.getDeliveryType();
        if (!StringUtils.isEmpty(deliveryMsg)) {
            holder.ecnomicalMoney.setText(deliveryMsg);
        }

        if (!StringUtils.isEmpty(desc)) {
            holder.productDesc.setText(desc);
        }

        String name = bean.getProductName();
        if (!StringUtils.isEmpty(name)) {
            holder.productName.setText(name);
        }

        int num = bean.getProductNum();
        if (num != 0) {
            holder.productNum.setText("x" + num);
        }
        String malPrice = TextDisposeUtils.getEndPrice(bean.isShowCoupon(),bean.getProductPrice(),bean.getCoupon()).toString();//bean.getProductPrice();
        if (!StringUtils.isEmpty(malPrice)) {
            holder.mallPrice.setText(malPrice);
        }

        String jPrice = bean.getJdPrice();
        if (!StringUtils.isEmpty(jPrice)) {
            holder.jdPrice.setText("¥" + jPrice);
        }
        holder.jdPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        String image = JudgeImageUrlUtils.isAvailable(bean.getPicture());
        Glide.with(context).load(image).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(holder.productImage);

        //点击事件
        if(canClick){
            holder.contentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] key = new String[]{"orderId", "orderStatus","localBy"};
                    String[] value = new String[]{orderId, orderStatus,localBy};
                    SkipPageUtils.getInstance(context).skipPage(OrderDetailActivity.class, key, value);
                }
            });
        }
//        holder.itemView.setOnClickListener(new ValidClickListener() {
//            @Override
//            public void onValidClick() {
//                String type = bean.getIsExchange().equals("y") ? context.getString(R.string.exchange_region) : "";
//                String[] key = new String[]{"productNo", "productImage", "productName","productPrice","type"};
//                String[] value = new String[]{bean.getProductNo(),"", "","",type};
//                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
//            }
//        });
        if (!StringUtils.isEmpty(orderType)) {
            holder.iv_number_flag.setVisibility(View.GONE);
//            holder.iv_number_flag.setVisibility(orderType.equals("1") ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productDesc;
        private TextView productName;
        private TextView productNum;
        private TextView mallPrice;
        private TextView jdPrice;
        private TextView ecnomicalMoney;
        private RelativeLayout contentRl;
        private ImageView iv_number_flag;

        public MyViewHolder(View itemView) {
            super(itemView);
            productDesc = (TextView) itemView.findViewById(R.id.tv_property);
            productImage = (ImageView) itemView.findViewById(R.id.iv_shop_image);
            productName = (TextView) itemView.findViewById(R.id.tv_goods_title);
            productNum = (TextView) itemView.findViewById(R.id.tv_goods_number);
            mallPrice = (TextView) itemView.findViewById(R.id.tv_mall_price);
            jdPrice = (TextView) itemView.findViewById(R.id.tv_jd_price);
            ecnomicalMoney = (TextView) itemView.findViewById(R.id.tv_save_money);
            contentRl = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            iv_number_flag = (ImageView) itemView.findViewById(R.id.iv_number_flag);
        }
    }

    /**
     * 刷新数据
     */
    public void notifyData() {
        notifyDataSetChanged();
    }
}

