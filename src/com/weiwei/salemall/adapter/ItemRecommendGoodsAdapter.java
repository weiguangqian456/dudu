package com.weiwei.salemall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.RecommendGoodsEntity;

import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * item页底部推荐商品适配器
 */
public class ItemRecommendGoodsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<RecommendGoodsEntity> data;

    public ItemRecommendGoodsAdapter(Context context, List<RecommendGoodsEntity> data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<RecommendGoodsEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(List<RecommendGoodsEntity> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<RecommendGoodsEntity> getData() {
        return this.data;
    }

    public void clearData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recommend_goods_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecommendGoodsEntity recommendGoods = data.get(position);
        holder.tv_goods_name.setText(recommendGoods.getTitle());
        holder.tv_goods_price.setText("¥" + recommendGoods.getCurrentPrice());
        Glide.with(context).load(recommendGoods.getImag()).into(holder.sdv_goods);
        holder.tv_goods_old_price.setText("¥" + recommendGoods.getPrice());
        return convertView;
    }

    class ViewHolder {
        private ImageView sdv_goods;
        private TextView tv_goods_name, tv_goods_price, tv_goods_old_price;

        public ViewHolder(View convertView) {
            sdv_goods = (ImageView) convertView.findViewById(R.id.sdv_goods);
            tv_goods_name = (TextView) convertView.findViewById(R.id.tv_goods_name);
            tv_goods_price = (TextView) convertView.findViewById(R.id.tv_goods_price);
            tv_goods_old_price = (TextView) convertView.findViewById(R.id.tv_goods_old_price);
//            tv_goods_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
