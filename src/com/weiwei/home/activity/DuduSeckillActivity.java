package com.weiwei.home.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.adapter.SeckillAdapter;
import com.weiwei.home.adapter.SeckillTabAdapter;
import com.weiwei.home.base.BaseRecycleActivity;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.StateBarUtils;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.adapter.HomePageBannerViewHolder;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.SHOP_MODEL;

public class DuduSeckillActivity extends BaseRecycleActivity implements RecycleItemClick {

    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.et_search)
    TextView mEtSearch;
    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.ll_search)
    LinearLayout mLlSearch;
    @BindView(R.id.ll_top)
    LinearLayout mLlTop;

    private SeckillAdapter mAdapter;
    private SeckillTabAdapter mSeckillTabAdapter;
    private HolderView mHeaderView;
    private float mScrolledHeight;
    private int mAlphaHeight = 200;
    private Map<String, String> mParams = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dudu_seckill;
    }

    @Override
    protected boolean isShowError() {
        return false;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected String getPath() {
        return SHOP_MODEL + "holidaySeckill/list";
    }

    @Override
    protected Map<String, String> getParams() {
        mParams.put("page", "1");
        mParams.put("limit", "4");
        return mParams;
    }


    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_SECKILL;
    }

    @Override
    protected void initView() {

        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();

        View headerView = LayoutInflater.from(this).inflate(R.layout.header_dudu_seckill, null, false);
        mHeaderView = new HolderView(headerView);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mHeaderView.mLlHeader.getLayoutParams();
        layoutParams.topMargin = StateBarUtils.getStateBarHeight(this) + DensityUtils.dp2px(this, 87);
        mHeaderView.mLlHeader.setLayoutParams(layoutParams);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mScrolledHeight += dy;
                float alpha = (mScrolledHeight > mAlphaHeight ? 1 : mScrolledHeight / mAlphaHeight);
                mTvTitleBackground.setAlpha(alpha);

            }
        });

        mAdapter = new SeckillAdapter(this);
        mSeckillTabAdapter = new SeckillTabAdapter(mContext);
        mSeckillTabAdapter.setRecycleClickListener(this);
        mAdapter.addHeadView(headerView);

        mParams.put("time", System.currentTimeMillis() + "");

        super.initView();

        getTabData();

    }

    private void getBanner() {

        RetrofitClient.getInstance(this).Api()
                .getBanners()
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        BannerDataEntity banner = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);

                        if (banner.holidaySkillBanner == null || banner.holidaySkillBanner.size() == 0) {
                            mHeaderView.mIvBannerDefault.setVisibility(View.VISIBLE);
                            return;
                        }

                        mHeaderView.mIvBannerDefault.setVisibility(View.GONE);

                        mHeaderView.mCbannerImg.setPageIndicator(new int[]{R.drawable.xiang_huise, R.drawable.xing_bai});
                        mHeaderView.mCbannerImg.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                        mHeaderView.mCbannerImg.setPages(new CBViewHolderCreator() {
                            @Override
                            public Object createHolder() {
                                return new HomePageBannerViewHolder();
                            }
                        }, banner.holidaySkillBanner);
                        mHeaderView.mCbannerImg.startTurning(3000);

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        mHeaderView.mIvBannerDefault.setVisibility(View.VISIBLE);
                    }

                });

    }

    private void getTabData() {

        RetrofitClient.getInstance(this).Api()
                .getSeckillTab()
                .enqueue(new Callback<SeckillTab>() {
                    @Override
                    public void onResponse(Call<SeckillTab> call, Response<SeckillTab> response) {
                        SeckillTab result = response.body();
                        if (result == null) {
                            return;
                        }

                        if (result.records == null || result.records.size() == 0) {
                            return;
                        }
                        result.records.get(0).check = true;
//                        mParams.put("time", result.records.get(0).startTime + "");

                        GridLayoutManager layoutManager = (GridLayoutManager) mHeaderView.mTabRecyclerView.getLayoutManager();
                        layoutManager.setSpanCount(result.records.size() >= 4 ? 4 : result.records.size());

                        mSeckillTabAdapter.setList(result.records);
                        mHeaderView.mTabRecyclerView.setAdapter(mSeckillTabAdapter);
                    }

                    @Override
                    public void onFailure(Call<SeckillTab> call, Throwable t) {

                    }
                });


    }

    @Override
    public void initRefresh() {
        getBanner();
        super.initRefresh();
    }

    @Override
    public void onHttpFinish() {
        super.onHttpFinish();
        if (getSeckillData() != null) {
            mAdapter.setStatus(getSeckillData().getStatus());
        }
    }

    @Override
    public void onItemClick(int position) {
        SeckillTab.Records item = mSeckillTabAdapter.getList().get(position);
        mParams.put("time", item.startTime + "");
        super.initRefresh();
    }

    @OnClick({R.id.fl_back, R.id.ll_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_search:
                SkipPageUtils.getInstance(mContext).skipPage(SearchViewActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    class HolderView {

        @BindView(R.id.ll_header)
        LinearLayout mLlHeader;
        @BindView(R.id.tab_recyclerView)
        RecyclerView mTabRecyclerView;
        @BindView(R.id.cbanner_img)
        ConvenientBanner mCbannerImg;
        @BindView(R.id.iv_banner_default)
        ImageView mIvBannerDefault;

        HolderView(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
