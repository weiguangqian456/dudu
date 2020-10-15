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

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * @author : hc
 * @date : 2019/4/10.
 * @description: 首页 新会员专区
 */

public class HomeMemberAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SeckillProductEntity> mList;

    public HomeMemberAdapter(List<SeckillProductEntity> mList,Context mContext){
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        ItemViewHolder holder = (ItemViewHolder) holder1;
        //新会员专区
        final SeckillProductEntity entity = mList.get(position);
        final String imageUrl = JudgeImageUrlUtils.isAvailable(entity.getPicture());
        Glide.with(mContext).load(imageUrl).into(holder.iv_image);
        holder.tv_title.setText(entity.getProductName().trim());
        String price = "￥" + entity.getSeckillPrice().trim();
        holder.tv_price.setText(price);
        String originally_price = "¥" + entity.getPrice();
        holder.tv_originally_price.setText(originally_price);
        holder.tv_originally_price.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
//        holder.tv_originally_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        String spare = "立省" + (TextDisposeUtils.toStringInt(entity.getPrice()) -
                TextDisposeUtils.toStringFloat(entity.getSeckillPrice())) + "元";
        holder.tv_spare.setText(spare);
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "seckillProductId", "seckill","type"};
                String[] value = new String[]{entity.getProductNo(), imageUrl, entity.getProductName(), entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill",context.getString(R.string.member_region)};
                SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_originally_price)
        TextView tv_originally_price;
        @BindView(R.id.tv_spare)
        TextView tv_spare;
        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
