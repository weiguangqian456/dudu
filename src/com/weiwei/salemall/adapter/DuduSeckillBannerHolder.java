package com.weiwei.salemall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.holder.Holder;
import com.hwtx.dududh.R;
import com.weiwei.home.activity.DuduSeckillActivity;
import com.weiwei.home.adapter.DuduSeckillBannerAdapter;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.bean.DuduSeckillBanner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DuduSeckillBannerHolder implements Holder<List<DuduSeckillBanner.Prdoucts>> {

    private Context mContext;
    private ViewHolder mHeaderView;

    private DuduSeckillBannerAdapter mAdapter;

    @Override
    public View createView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.banner_dudu_seckill, null, false);
        mHeaderView = new ViewHolder(rootView);
        mContext = context;

        mAdapter = new DuduSeckillBannerAdapter(context);
        mHeaderView.mRecyclerView.setAdapter(mAdapter);

        mAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                ArmsUtils.startActivity(mContext, DuduSeckillActivity.class);
            }
        });

        return rootView;
    }

    @Override
    public void UpdateUI(Context context, int position, List<DuduSeckillBanner.Prdoucts> data) {

        mAdapter.getList().clear();
        mAdapter.setList(data);
        mAdapter.notifyDataSetChanged();

    }

    class ViewHolder {
        @BindView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
