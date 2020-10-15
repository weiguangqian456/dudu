package com.weiwei.salemall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/7/26.
 *         主界面ViewPager 适配器
 */

public class EntranceAdapter extends RecyclerView.Adapter<EntranceAdapter.EntranceViewHolder> {

    private List<ModelHomeEntranceBean> mDatas;

    /**
     * 页数下标,从0开始(通俗讲第几页)
     */
    private int mIndex;

    /**
     * 每页显示最大条目个数
     */
    private int mPageSize;

    private Context mContext;


    public EntranceAdapter(Context context, List<ModelHomeEntranceBean> datas, int index, int pageSize) {
        mContext = context;
        mDatas = datas;
        mPageSize = pageSize;
        mIndex = index;
    }

    @Override
    public EntranceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_classification_item, parent, false);
        EntranceAdapter.EntranceViewHolder viewHolder = new EntranceAdapter.EntranceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EntranceViewHolder holder, final int position) {
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + mIndex * mPageSize，
         */
        final int pos = position + mIndex * mPageSize;

        ModelHomeEntranceBean bean = mDatas.get(pos);
        final String name = bean.getColumnName();
        if (!StringUtils.isEmpty(name)) {
            holder.name.setText(name);
        }

        String imageUrl = JudgeImageUrlUtils.isAvailable(bean.getIconUrl());
        if (!StringUtils.isEmpty(imageUrl)) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.sec_entrance_default);
            Glide.with(mContext).load(imageUrl).apply(options).into(holder.image);
        }

        final int type = bean.getType();
        final String skipUrl = bean.getSkipUrl();
        final String className = bean.getAndroidClassName().trim();
        final String isExchange = bean.getIsExchange();    //是否兑换专区商品

        if (!StringUtils.isEmpty(className)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelHomeEntranceBean entrance = mDatas.get(pos);
                    CustomSkipUtils.toSkip(mContext,entrance);

//                    SimBackActivity.launch(mContext,SimBackEnum.LOCAL_INFO,null);

//                    ModelHomeEntranceBean entrance = mDatas.get(pos);
//                    String id = entrance.getId();
//                    Intent intent = new Intent();
//                    switch (type) {
//                        case 0://商品
//                            intent.setClassName(mContext, className);
//                            intent.putExtra("columnId", id);
//                            intent.putExtra("isExchange", isExchange);
//                            break;
//                        case 1://店铺
//                            intent.setClassName(mContext, className);
//                            intent.putExtra("columnId", id);
//                            break;
//                        case 2: //外链
//                            intent.setClassName(mContext, className);
//                            intent.putExtra("skipUrl", skipUrl);
//                            break;
//                        default:
//                            break;
//                    }
//                    Log.e("栏目跳转类名===>", className);
//                    if (className.equals("com.weiwei.account.VipMemberActivity")) {    //VIP会员界面
//                        if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
//                            mContext.startActivity(intent);
//                        }
//                    } else {
//                        mContext.startActivity(intent);
//                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() > (mIndex + 1) * mPageSize ? mPageSize : (mDatas.size() - mIndex * mPageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPageSize;
    }

    class EntranceViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private LinearLayout contentLl;

        public EntranceViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.entrance_image);
            name = (TextView) itemView.findViewById(R.id.entrance_name);
            contentLl = (LinearLayout) itemView.findViewById(R.id.ll_content);
        }
    }
}