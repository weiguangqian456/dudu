package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.PercentProgressBar;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author Created by EDZ on 2018/12/06.
 *         秒杀界面adapter
 */

public class SeckillProductAdapter extends RecyclerView.Adapter<SeckillProductAdapter.MyViewHolder> {

    private Context context;
    private List<SeckillProductEntity> recordsBeanList;
    private String status;

    public SeckillProductAdapter(Context context, List<SeckillProductEntity> recordsBeanList, String status) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.status = status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_seckill_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SeckillProductEntity entity = recordsBeanList.get(position);
        String name = entity.getProductName();
        if (!StringUtils.isEmpty(name)) {
            holder.productName.setText(name);
        }
        String productPrice = entity.getSeckillPrice();
        if (!StringUtils.isEmpty(productPrice)) {
            holder.price.setText("¥" + productPrice);
        }
        String productComPrice = entity.getPrice();
        if (!StringUtils.isEmpty(productComPrice)) {
            holder.comparePrice.setText("¥" + productComPrice);
        }
//        holder.comparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        final String image = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        //已售
        String stock = entity.getStock();//21
        String totalStock = entity.getTotalStock();//55

        if (!StringUtils.isEmpty(stock) && !StringUtils.isEmpty(totalStock)) {
            int haveSold = Integer.parseInt(totalStock) - Integer.parseInt(stock); //34
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) haveSold / (float) (Integer.parseInt(totalStock)) * 100);
            double a = Double.parseDouble(result);
            holder.progressBar.setProgress((int) a);
        }
        if (!StringUtils.isEmpty(image)) {
            Glide.with(context).load(image).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(holder.imageView);
        }

        switch (stock) {
            case "0"://已售罄
                holder.buy.setTextColor(context.getResources().getColor(R.color.noclick));
                holder.buy.setText("已售罄");
                holder.soldOutIv.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v3));
                }
                break;
            default:
                holder.buy.setTextColor(context.getResources().getColor(R.color.vs_white));
                holder.buy.setText("立即抢购");
                holder.soldOutIv.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v2));
                }
                break;
        }

        if (!StringUtils.isEmpty(status)) {
            switch (status) {
                case "end":
                    holder.buy.setTextColor(context.getResources().getColor(R.color.noclick));
                    holder.buy.setText("已结束");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v3));
                    }
                    break;
                case "underway":
                    switch (stock) {
                        case "0"://已售罄
                            holder.buy.setTextColor(context.getResources().getColor(R.color.noclick));
                            holder.buy.setText("已售罄");
                            holder.soldOutIv.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v3));
                            }
                            break;
                        default:
                            holder.buy.setTextColor(context.getResources().getColor(R.color.vs_white));
                            holder.buy.setText("立即抢购");
                            holder.soldOutIv.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v2));
                            }
                            break;
                    }
                    break;
                case "wait":
                    holder.buy.setTextColor(context.getResources().getColor(R.color.noclick));
                    holder.buy.setText("未开始");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.buy.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_v3));
                    }
                    break;
                default:
                    break;
            }
        }

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice","seckillProductId", "seckill"};
                String[] value = new String[]{entity.getProductNo(), image, entity.getProductName(),entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill"};
                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }


    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView soldOutIv;
        private TextView productName;
        private TextView price;
        private TextView comparePrice;
        private PercentProgressBar progressBar;
        private TextView buy;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            soldOutIv = (ImageView) itemView.findViewById(R.id.iv_soldout);
            productName = (TextView) itemView.findViewById(R.id.tv_name);
            price = (TextView) itemView.findViewById(R.id.tv_price);
            comparePrice = (TextView) itemView.findViewById(R.id.tv_com_price);
            progressBar = (PercentProgressBar) itemView.findViewById(R.id.pb_seckill);
            buy = (TextView) itemView.findViewById(R.id.btn_buy);
        }
    }
}
