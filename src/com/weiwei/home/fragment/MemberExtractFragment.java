package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.home.activity.CarryCargoPrefectureActivity;
import com.weiwei.home.adapter.ExchangeRegionAdapter;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.entity.SepGoodsEntity;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.SHOP_MODEL;

/**
 * @author : hc
 * @date : 2019/4/3.
 * @description: 新会员专区 介绍页
 */

public class MemberExtractFragment extends BaseRecycleFragment {

    public static Fragment newInstance(Bundle bundle) {
        MemberExtractFragment fragment = new MemberExtractFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private BannerImageEntity mVipBanner;
    private HeadViewHolder headViewHolder;
    private View itemView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycle;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ExchangeRegionAdapter(mContext, getArgumentsInfo("columnId"));
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected String getPath() {
        return SHOP_MODEL + "childColumns/dataByColumn/" + getArgumentsInfo("classificationFlag");
    }

    @Override
    protected void initView() {
        super.initView();
//        httpLoadDataExtract();
//        httpLoadDataExchange();
//        initHomeHead();
    }

    @Override
    public void initRefresh() {
        super.initRefresh();
        initHomeHead();
    }

    private void initHomeHead() {
        if (headViewHolder == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_extract, null, false);
            headViewHolder = new MemberExtractFragment.HeadViewHolder(itemView);
            headViewHolder.onChange();
        } else {
            headViewHolder.onChange();
        }
        mAdapter.addHeadView(itemView);
    }

    class HeadViewHolder {

        @BindView(R.id.iv_banner)
        ImageView iv_banner;
        @BindView(R.id.rv_view)
        RecyclerView rv_view;
        @BindView(R.id.iv_more)
        ImageView iv_more;
        @BindView(R.id.fl_load)
        FrameLayout fl_load;

        HeadViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            fl_load.setVisibility(View.VISIBLE);

            rv_view.setNestedScrollingEnabled(false);
            rv_view.setLayoutManager(new GridLayoutManager(mContext, 2));
            iv_more.setOnClickListener(new ValidClickListener() {
                @Override
                public void onValidClick() {
//                SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_LIST,null);
                    CarryCargoPrefectureActivity.start(mContext, getArgumentsInfo("columnId"));
                }
            });
            iv_banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVipBanner != null) {
                        CustomSkipUtils.toSkip(mContext, mVipBanner);
                    }
                }
            });
        }

        void onChange() {
            httpLoadDataExtract();
        }

        //http://route.edawtech.com/route/shop/api/product/page?appId=dudu&indexColumnId=64024a67103e4a359c55028bac8f670d&pageNum=1&pageSize=4
        private void httpLoadDataExtract() {
            Map<String, String> params = new HashMap<>();
            params.put("pageNum", 1 + "");
            params.put("pageSize", 4 + "");
            params.put("indexColumnId", "64024a67103e4a359c55028bac8f670d");
            RetrofitClient.getInstance(mContext)
                    .Api()
                    .getExtractList(params)
                    .enqueue(new Callback<SepGoodsEntity>() {
                        @Override
                        public void onResponse(Call<SepGoodsEntity> call, Response<SepGoodsEntity> response) {
                            fl_load.setVisibility(View.GONE);
                            SepGoodsEntity resultEntity = response.body();
                            if (resultEntity != null && Const.REQUEST_CODE == resultEntity.getCode()) {
                                ExchangeRegionAdapter adapter = new ExchangeRegionAdapter(mContext, 1, getArgumentsInfo("columnId"));
                                adapter.initFooterState(Constant.RECYCLE_FOOTER_GONE);
                                rv_view.setAdapter(adapter);
                                List<Object> records = resultEntity.getData().getRecords();
                                adapter.addList(records);
                            }
                        }

                        @Override
                        public void onFailure(Call<SepGoodsEntity> call, Throwable t) {
                            fl_load.setVisibility(View.GONE);
                        }
                    });
        }

    }
}
