package com.weiwei.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/15.
 * @description: 普通的展示用商品 - 非兑换专区
 */

public class CommonGoodsAdapter extends BaseRecycleAdapter{

    private String mColumnId;
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    public CommonGoodsAdapter(Context mContext,String mColumnId) {
        super(mContext);
        this.mColumnId = mColumnId;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_goods,parent, false));
    }

    @Override
    protected void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final ProductBean bean = JSONObject.parseObject(getItem(position).toString(),ProductBean.class);
        ItemViewHolder holder = (ItemViewHolder)holder1;
        //图片
        Glide.with(mContext).load(JudgeImageUrlUtils.isAvailable(bean.getPicture()))
                .apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(holder.iv_image);
        //标题
        holder.tv_title.setText(bean.getProductName());
        //原价
        String order = bean.getJdPrice2();
        holder.tv_price_order.setText(order);
        holder.tv_price_order.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
//        holder.tv_price_order.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //当前价格
        holder.tv_price.setText(String.format("￥%s", bean.getPrice()));
        //立省金额
        String economyPrice = "立省" + DealEcnomicalMoneyUtils.get(bean.getJdPrice(), bean.getPrice(), 1);
        holder.tv_economy.setText(economyPrice);
        //跳转
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName","productPrice","columnId"};
                String[] value = new String[]{bean.getProductNo(), bean.getPicture(), bean.getProductName(),bean.getPrice(),mColumnId};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.iv_discount)
        ImageView iv_discount;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price_order)
        TextView tv_price_order;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_economy)
        TextView tv_economy;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
