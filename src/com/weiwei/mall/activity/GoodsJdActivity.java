package com.weiwei.mall.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.fragment.NewGoodsFragment;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.view.CustomErrorView;
import com.weiwei.home.yd.YDLocalClassEntity;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.adapter.HomePageBannerViewHolder;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.BadgeView;
import com.weiwei.salemall.widget.CustomProgressDialog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author : hc
 * @date : 2019/3/26.
 * @description:
 */

public class GoodsJdActivity extends BaseActivity {

    @BindView(R.id.badgeView)
    BadgeView badgeView;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.magic_indicator)
    MagicIndicator magic_indicator;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    @BindView(R.id.cev_view)
    CustomErrorView cev_view;

    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.cbanner_img)
    ConvenientBanner mCbannerImg;
    @BindView(R.id.iv_banner_default)
    ImageView mIvBannerDefault;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private CustomProgressDialog loadingDialog;
    private List<ModelHomeEntranceBean> mDataList;

    private int mAlphaHeight = 300;
    private ModelHomeEntranceBean modelHomeEntranceBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_show;
    }

    @Override
    protected int getStateBarColor() {
        return Color.parseColor("#447aff");
    }

    @Override
    protected boolean isInitStateBar() {
        return false;
    }

    @Override
    protected void initView() {
        modelHomeEntranceBean = new ModelHomeEntranceBean();
        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();

       // iv_title.setText(modelHomeEntranceBean.getColumnName());
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float absVerticalOffset = Math.abs(verticalOffset);

                float alpha = (absVerticalOffset > mAlphaHeight ? 1 : absVerticalOffset / mAlphaHeight);
                mTvTitleBackground.setAlpha(alpha);

            }
        });

        //异常时点击事件
        cev_view.setOnErrorClickListener(new CustomErrorView.OnErrorClickListener() {
            @Override
            public void onRefresh() {
                getDataList();
            }
        });
        cev_view.setShowView();
        getDataList();
        getBanner();
    }

    private void getBanner() {

        RetrofitClient.getInstance(this).Api()
                .getBanners()
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        BannerDataEntity banner = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);

                        if (banner.business == null || banner.business.size() == 0) {
                            mIvBannerDefault.setVisibility(View.VISIBLE);
                            return;
                        }

                        mIvBannerDefault.setVisibility(View.GONE);

                        mCbannerImg.setPageIndicator(new int[]{R.drawable.xiang_huise, R.drawable.xing_bai});
                        mCbannerImg.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                        mCbannerImg.setPages(new CBViewHolderCreator() {
                            @Override
                            public Object createHolder() {
                                return new HomePageBannerViewHolder();
                            }
                        }, banner.specialSupply);
                        mCbannerImg.startTurning(3000);

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        mIvBannerDefault.setVisibility(View.VISIBLE);
                    }

                });

    }

    private void startLoading() {
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        loadingDialog.setLoadingDialogShow();
    }

    private void stopLoading() {
        loadingDialog.setLoadingDialogDismiss();
    }

    private void getDataList() {
        startLoading();
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        Call<ResultEntity> call = api.getColumn("android");
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                stopLoading();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (result != null &&
                        REQUEST_CODE == result.getCode()
                        && result.getData() != null) {
                    DataBean dataBean = JSONObject.parseObject(result.getData().toString(), DataBean.class);
                    mDataList = JSONObject.parseArray(dataBean.getJd().toString(), ModelHomeEntranceBean.class);
                    if (mDataList == null || mDataList.size() == 0) {
                        cev_view.initState(CustomErrorView.ERROR_VIEW_EMPTY);
                    } else {
                        cev_view.initState(CustomErrorView.ERROR_NOT);
                        initBadgeView(getTotalNum());
                        initPager();
                        initIndicator();
                    }
                } else {
                    cev_view.initState(CustomErrorView.ERROR_VIEW_EMPTY);
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                stopLoading();
                cev_view.initState(CustomErrorView.ERROR_VIEW_EMPTY);
            }
        });
    }

    private void initBadgeView(int count) {
        badgeView.invalidate();
        badgeView.setVisibility(0 == count ? View.GONE : View.VISIBLE);
        badgeView.setText(10 < count ? "10+" : count + "");
    }

    private void initIndicator() {
        magic_indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).getColumnName());
                int size = (int) getResources().getDimension(R.dimen.w_14_dip);
                simplePagerTitleView.setTextSize(px2sp(context, size));
                simplePagerTitleView.setNormalColor(Color.GRAY);

                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.public_color_F11801));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view_pager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);

                linePagerIndicator.setColors(getResources().getColor(R.color.public_color_F11801));
                return linePagerIndicator;
            }
        });
        magic_indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magic_indicator, view_pager);
    }

    private void initPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            ModelHomeEntranceBean bean = mDataList.get(i);
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", bean.getId());
            bundle.putString("isExchange", bean.getIsExchange());
            Fragment localStoreFragment = NewGoodsFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        view_pager.setOffscreenPageLimit(1);
        view_pager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragmentList));
        view_pager.setCurrentItem(0);
    }

    @OnClick({R.id.fl_back, R.id.ll_search, R.id.rl_shoppingCart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_search:
                String[] key = new String[]{"skipFlag", "columnId", "searchType"};
                String[] value = new String[]{"2", "", "JD"};
                skipPage(SearchViewActivity.class, key, value);
                break;
            case R.id.rl_shoppingCart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                EventBus.getDefault().post(bean);
                finish();
                break;
            default:
                break;
        }
    }

    public void skipPage(Class<? extends Activity> cls, String[] key, String[] values) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != values) {
            for (int i = 0; i < key.length; i++) {
                intent.putExtra(key[i], values[i]);
            }
        }
        startActivity(intent);
    }

    private int getTotalNum() {
        int num = 0;
        ShoppingItemsBeanDao shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext)
                .getDaoSession().getShoppingItemsBeanDao();
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (ShoppingItemsBean bean : beanList) {
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
