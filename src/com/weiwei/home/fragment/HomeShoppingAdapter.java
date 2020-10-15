package com.weiwei.home.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.utils.SkipPageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/5/15.
 * @description: 必买街
 */

public class HomeShoppingAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<ProductBean> mList;

    public HomeShoppingAdapter(Context mContext, List<ProductBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_home_shopping, parent,false);
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final ProductBean bean = mList.get(position);
        ItemViewHolder holder = (ItemViewHolder)holder1;
        Glide.with(mContext).load(TextDisposeUtils.isAvailable(bean.getPicture())).into(holder.iv_image);
        holder.tv_name.setText(bean.getProductName());
        holder.tv_price.setText(TextDisposeUtils.dispseMoneyText(bean.getPrice()));
        holder.itemView.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                String[] value = new String[]{bean.getProductNo(), TextDisposeUtils.isAvailable(bean.getPicture()), bean.getProductName(), bean.getPrice()};
                SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
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
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_price)
        TextView tv_price;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
