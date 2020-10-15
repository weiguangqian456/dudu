package com.weiwei.home.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.activity.DiscoverActivity;
import com.weiwei.home.activity.DuduSeckillActivity;
import com.weiwei.home.activity.RefuelDetailActivity;
import com.weiwei.home.activity.RefuelMainActivity;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.adapter.DefaultGoodsAdapter;
import com.weiwei.home.adapter.MallRecommendTitleAdapter;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.entity.CustomShareEntity;
import com.weiwei.home.test.RecycleItemClickListener;
import com.weiwei.home.test.RequestsListener;
import com.weiwei.home.test.RetrofitCallBacks;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.test.ShareListener;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.home.utils.ImageFileUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.StateBarUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.adapter.CagegoryViewPagerAdapter;
import com.weiwei.salemall.adapter.DuduSeckillBannerHolder;
import com.weiwei.salemall.adapter.EntranceAdapter;
import com.weiwei.salemall.adapter.HomePageBannerViewHolder;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.DuduSeckillBanner;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SamqiColumnBean;
import com.weiwei.salemall.emsemob.ChatActivity;
import com.weiwei.salemall.emsemob.EaseMobMsgHelper;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.WrapContentHeightViewPager;
import com.weiwei.weibo.WebPageNavigationActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.TriangularPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.weiwei.base.dataprovider.DfineAction.COMPANY_NAME;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_DEFAULT_PASSWORD;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_IMSERVER;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TAGNAME;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author : hc
 * @date : 2019/3/19.
 * @description: 首页商城
 */

public class HomeMallFragment extends BaseRecycleFragment implements View.OnClickListener {

    @BindView(R.id.rl_title)
    RelativeLayout rl_title;
    @BindView(R.id.view_bar)
    View view_bar;
    @BindView(R.id.view_bar_bg)
    View view_bar_bg;
    @BindView(R.id.view_title_line)
    View view_title_line;
    @BindView(R.id.iv_msg)
    ImageView iv_msg;
    @BindView(R.id.iv_share)
    ImageView iv_share;
    @BindView(R.id.tv_msg)
    TextView tv_msg;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.fl_msg)
    FrameLayout fl_msg;
    @BindView(R.id.fl_share)
    FrameLayout fl_share;
    @BindView(R.id.view_top_btn_bg_1)
    View view_top_btn_bg_1;
    @BindView(R.id.view_top_btn_bg_2)
    View view_top_btn_bg_2;
    @BindView(R.id.mIvBg)
    ImageView mIvVg;

    Unbinder unbinder;

    private String mHttpId = "7f3282a4fcc748fd9d378b92e3c00d16";

    private float mRecycleY = 0;
    private float mRecycleDimension = 0;

    private View itemView;
    private HeadViewHolder headViewHolder;

    private List<ProductBean> mShoppingList;
    private BannerDataEntity bannerDataEntity;
    private DataBean dataEntity;
    private DuduSeckillBanner mDuduSeckillBanner;
    private RequestsListener mRequestsListener;

    private Boolean isFirst = true;

    private List<SamqiColumnBean> mTitleList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycle_home;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new DefaultGoodsAdapter(mContext);
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected boolean isShowError() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        return params;
    }


    // return Const.SHOP_MODEL + "childColumns/dataByColumn/" + mHttpId;
    @Override
    protected String getPath() {
       // return Const.SHOP_MODEL + "childColumns/dataByColumn/" + mHttpId;
        return  Const.SHOP_MODEL+"childColumns/dataByColumn/" + mHttpId;     //返回
    }

    private int mTopMargin;
    private int mIvHeight;
    private int mBannerTopMargin;
    private int mTop;
    private int mNowMargin, mToMargin;
    private RelativeLayout.LayoutParams mSearchParams = null;
    private RelativeLayout.LayoutParams mIvBgParams = null;

    @Override
    protected void initView() {
        super.initView();
        //防止覆盖状态栏
        ViewGroup.LayoutParams layoutParams = view_bar.getLayoutParams();
        int stateBarHeight = StateBarUtils.getStateBarHeight(getActivity());
        layoutParams.height = stateBarHeight;
        view_bar.setLayoutParams(layoutParams);
        view_bar_bg.getLayoutParams().height = DensityUtils.dp2px(mContext, 50) + stateBarHeight;
        view_bar_bg.setAlpha(0f);
        mTop = DensityUtils.dp2px(mContext, 9);
        mBannerTopMargin = stateBarHeight + DensityUtils.dp2px(mContext, 90);
        mIvBgParams = ((RelativeLayout.LayoutParams) mIvVg.getLayoutParams());
        mSearchParams = ((RelativeLayout.LayoutParams) ll_search.getLayoutParams());
        mTopMargin = mSearchParams.topMargin;
        mToMargin = DensityUtils.dp2px(mContext, 25);
        mNowMargin = DensityUtils.dp2px(mContext, 14);
        mIvHeight = DensityUtils.dp2px(mContext, 214);
        //用于计算标题栏透明度的高度
        mRecycleDimension = DensityUtils.dp2px(mContext, 240);
        mRequestsListener = new RequestsListener(new RequestsListener.HttpRequestFinish() {
            @Override
            public void onHttpRequestFinish() {
                if (isFirst) {
                    isFirst = false;
                    rl_title.setVisibility(View.VISIBLE);
                }
                initHomeHead();
            }
        });
        //标题栏渐变
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRecycleY += dy;
                if (mRecycleY < 0) {
                    mRecycleY = 0;
                }
                int dRy = (int) (mRecycleY / 2);
                int top = (mTopMargin - dRy) > mTop ? ((mTopMargin - dRy)) : mTop;
                int margin = (dRy > mToMargin ? mToMargin : dRy);
                mSearchParams.topMargin = top;
                mSearchParams.leftMargin = mNowMargin + margin;
                mSearchParams.rightMargin = mNowMargin + margin;
                ll_search.requestLayout();
                float alpha = (mRecycleY > 280 ? 1 : mRecycleY / 280);
                view_bar_bg.setAlpha(alpha);
                mIvBgParams.height = (int) ((mIvHeight - (mRecycleY / 0.7f)) > 0 ? mIvHeight - (mRecycleY / 0.7f) : 0);
                mIvVg.requestLayout();
            }
        });
        fl_msg.setOnClickListener(this);
        fl_share.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        httpHomePush();
        initHomeHead();
        initData();
    }

    private void initData() {
        getDuduSeckillBanner();
        httpBanner();
        httpShopping();
        httpInitTitle();
        httpInitData();
        updateRefuelOrder();
    }

    @Override
    protected void onInitRefresh() {
        super.onInitRefresh();
        initData();
    }

    private void updateRefuelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        RetrofitClient.getInstance(mContext).Api()
                .updateRefuelOrder(params)
                .enqueue(new retrofit2.Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });

    }

    private void getDuduSeckillBanner() {

        Map<String, String> params = new HashMap<>();
//        params.put("page", "1");
        params.put("time", System.currentTimeMillis() + "");

        RetrofitClient.getInstance(mContext).Api()
                .getDuduSeckillBanner(params)
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        mDuduSeckillBanner = JSON.parseObject(result.getData().toString(), DuduSeckillBanner.class);
                    }
                    @Override
                    protected void onFinish() {
                        super.onFinish();
                    }
                });

    }

    /**
     * 推送
     */
    private void httpHomePush() {
        String aloneImei = VsUtil.getAloneImei(getActivity());
        //拿不到直接返回  否则会弹参数错误
        if (TextUtils.isEmpty(aloneImei)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("mobileIdentity", aloneImei);
        params.put("platform", "android");
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getHomePagePush(params)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        BannerImageEntity entity = JSON.parseObject(result.getData().toString(), BannerImageEntity.class);
                        if (getFragmentManager() != null) {
                            HomePushDialog.getInstance().initPushDialog(entity).show(getFragmentManager(), "TAG-A");
                        }
                    }
                });
    }

    private void httpBanner() {
        RetrofitClient
                .getInstance(getActivity())
                .Api()
                .getBanners()
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        bannerDataEntity = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);
                    }
                });
    }

    /**
     * 必买街 不做加载更多
     * 加载20条
     */
    private void httpShopping() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "4");
        params.put("pageNum", "0");
        params.put("pageSize", "20");
        RetrofitClient
                .getInstance(getActivity())
                .Api()
                .getShoppingList(params)
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        if (result.getCode() == 0 && result.getData() != null) {
                            ResultDataEntity entity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                            mShoppingList = JSONObject.parseArray(entity.getRecords().toString(), ProductBean.class);
                        }
                    }
                });
    }

    //精品好货
    private void httpInitTitle() {
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getChildColumnsData("a84ac91400124a6c878052fe255785b1")
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                            mTitleList.clear();
                            List<SamqiColumnBean> list = JSONObject.parseArray(result.getData().toString(), SamqiColumnBean.class);
                            if (list != null && list.size() != 0) {
                                mHttpId = list.get(0).getId();
                                mTitleList.addAll(list);
                            }
                        }
                    }
                });
    }

    private void httpInitData() {
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getColumn("android")
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        dataEntity = JSONObject.parseObject(result.getData().toString(), DataBean.class);

                    }
                });
    }

    private void initHomeHead() {
        if (headViewHolder == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_home_head, null, false);
            headViewHolder = new HeadViewHolder(itemView);
        } else {
            headViewHolder.onChange();
        }
        mAdapter.addHeadView(itemView);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_share:
                if(VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint),mContext)) {
                    toShare();
                }
                break;
            case R.id.fl_msg:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    if (ChatClient.getInstance().isLoggedInBefore()) {
                        Intent intent = new IntentBuilder(mContext).setServiceIMNumber(EASEMOB_IMSERVER).setTitleName(COMPANY_NAME).setVisitorInfo
                                (EaseMobMsgHelper.createVisitorInfo("")).setTargetClass(ChatActivity.class).build();
                        startActivity(intent);
                    } else {
                        loginEaseMob();
                    }
                }
                break;
            case R.id.ll_search:
                SkipPageUtils.getInstance(mContext).skipPage(SearchViewActivity.class);
                break;
        }
    }

    private void loginEaseMob() {
        String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        if (!StringUtils.isEmpty(uid)) {
            ChatClient.getInstance().login(uid, EASEMOB_DEFAULT_PASSWORD, new Callback() {
                @Override
                public void onSuccess() {
                    Intent intent = new IntentBuilder(mContext)
                            .setTargetClass(ChatActivity.class)
                            .setServiceIMNumber(EASEMOB_IMSERVER)
                            .setVisitorInfo(EaseMobMsgHelper.createVisitorInfo(""))
                            .setTitleName(COMPANY_NAME)
                            .build();
                    startActivity(intent);
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login fail");
                }

                @Override
                public void onProgress(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login progress");
                }
            });
        } else {
            ToastAstrictUtils.getInstance().show("登录后方可联系客服");
        }
    }

    @Override
    protected Boolean isReplace() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Adapter 头部
     */
    class HeadViewHolder implements View.OnClickListener {
        @BindView(R.id.cbanner_img)
        ConvenientBanner cbanner_img;
        @BindView(R.id.main_home_entrance_vp)
        WrapContentHeightViewPager main_home_entrance_vp;
        @BindView(R.id.main_home_entrance_indicator)
        MagicIndicator main_home_entrance_indicator;
        @BindView(R.id.rv_recommend_classify)
        RecyclerView rv_recommend_classify;
        @BindView(R.id.fl_shopping_empty)
        FrameLayout fl_shopping_empty;
        @BindView(R.id.rv_shopping)
        RecyclerView rv_shopping;
        @BindView(R.id.iv_banner_default)
        ImageView iv_banner_default;
        //        @BindView(R.id.iv_banner_one)
//        ImageView iv_banner_one;
//        @BindView(R.id.iv_banner_two)
//        ImageView iv_banner_two;
        @BindView(R.id.iv_banner_three)
        ImageView iv_banner_three;
//        @BindView(R.id.iv_banner_four)
//        ImageView iv_banner_four;
        @BindView(R.id.tv_recommend_empty)
        TextView tv_recommend_empty;
        @BindView(R.id.ll_main_shopping)
        View ll_main_shopping;
        @BindView(R.id.mIvUpMember)
        ImageView mIvUpMember;
        @BindView(R.id.mCvBanner)
        View mCvBanner;

        @BindView(R.id.rtv_dudu_seckill_hour)
        RoundTextView mRtvDuduSeckillHour;
        @BindView(R.id.rtv_dudu_seckill_minute)
        RoundTextView mRtvDuduSeckillMinute;
        @BindView(R.id.rtv_dudu_seckill_second)
        RoundTextView mRtvDuduSeckillSecond;
        @BindView(R.id.cb_dudu_seckill_banner)
        ConvenientBanner mCbDuduSeckillBanner;
        @BindView(R.id.ll_dudu_seckill_banner)
        LinearLayout mLlDuduSeckillBanner;

        private int PageCount = 8;
        private long mTime = 0;

        HeadViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            ((LinearLayout.LayoutParams) mCvBanner.getLayoutParams()).topMargin = mBannerTopMargin;
            onChange();
        }

        void onChange() {
            initRecommendTitle();
            initCarouse();
            initMainItem();
            initShopping();
            initImageBanner();
            duduSeckillBanner();
         //   Glide.with(mContext).asGif().load(R.drawable.ic_home_new_member).into(mIvUpMember);
        }

        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mTime <= 0) {
                    setSeckillCountdown(mTime);
                    onInitRefresh();
                    return;
                }
                mTime -= 1;
                setSeckillCountdown(mTime);
                mLlDuduSeckillBanner.postDelayed(this, 1000);
            }
        };

        private void setSeckillCountdown(long time) {
//
//            if (time <= 0) {
//                return;
//            }

            long hour = time / 60 / 60;
            long minute = time / 60 % 60;
            long second = time % 60;

            mRtvDuduSeckillHour.setText(hour < 10 ? "0" + hour : "" + hour);
            mRtvDuduSeckillMinute.setText(minute < 10 ? "0" + minute : "" + minute);
            mRtvDuduSeckillSecond.setText(second < 10 ? "0" + second : "" + second);

        }

        private void duduSeckillBanner() {

            if (mDuduSeckillBanner == null) {
                return;
            }
            if (mDuduSeckillBanner.prdoucts == null || mDuduSeckillBanner.prdoucts.size() == 0) {
                return;
            }

            if ("wait".equals(mDuduSeckillBanner.status)) {
                mTime = (mDuduSeckillBanner.startTime - mDuduSeckillBanner.now) / 1000;
            } else {
                mTime = (mDuduSeckillBanner.endTime - mDuduSeckillBanner.now) / 1000;
            }

            if (mTime > 0) {
                mLlDuduSeckillBanner.removeCallbacks(mRunnable);
                setSeckillCountdown(mTime);
                mLlDuduSeckillBanner.postDelayed(mRunnable, 1000);
            }

            mCbDuduSeckillBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
            mCbDuduSeckillBanner.setPages(new CBViewHolderCreator() {
                @Override
                public Object createHolder() {
                    return new DuduSeckillBannerHolder();
                }
            }, mDuduSeckillBanner.prdoucts);
            mCbDuduSeckillBanner.startTurning(2500);

        }

        /**
         * 中央图片Banner
         */
        private void initImageBanner() {
            if (bannerDataEntity != null && bannerDataEntity.getOptimal() != null) {
                BannerDataEntity.OptimalBean optimal = bannerDataEntity.getOptimal();
                if (optimal.getOne() != null) {
                    String url = JudgeImageUrlUtils.isAvailable(optimal.getOne().getImageUrl());
//                    Glide.with(mContext).load(url).into(iv_banner_one);
                }
//                if (optimal.getTwo() != null) {
//                    String url = JudgeImageUrlUtils.isAvailable(optimal.getTwo().getImageUrl());
//                    Glide.with(mContext).load(url).into(iv_banner_two);
//                }
                if (optimal.getThree() != null) {
                    String url = JudgeImageUrlUtils.isAvailable(optimal.getThree().getImageUrl());
                    Glide.with(mContext).load(url).into(iv_banner_three);
                }
//                if (optimal.getFour() != null) {
//                    String url = JudgeImageUrlUtils.isAvailable(optimal.getFour().getImageUrl());
//                    Glide.with(mContext).load(url).into(iv_banner_four);
//                }
            }
        }

//        @OnClick(R.id.mIvUpMember)
//        public void onClickToUp() {
//            SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_REGION, null);
//        }

        //嘟嘟秒杀
        @OnClick(R.id.ll_dudu_seckill_banner)
        public void onClickBannerOne() {
            ArmsUtils.startActivity(mContext, DuduSeckillActivity.class);
//            if(bannerDataEntity != null && bannerDataEntity.getOptimal() != null && bannerDataEntity.getOptimal().getOne()!=null){
//                CustomSkipUtils.toSkip(mContext,bannerDataEntity.getOptimal().getOne());
//            }else{
//                SimBackActivity.launch(mContext, SimBackEnum.RECOMMEND_LIST,null);
//            }
        }

//        //今日爆款
//        @OnClick(R.id.iv_banner_two)
//        public void onClickBannerTwo() {
//            if (bannerDataEntity != null && bannerDataEntity.getOptimal() != null && bannerDataEntity.getOptimal().getTwo() != null) {
//                CustomSkipUtils.toSkip(mContext, bannerDataEntity.getOptimal().getTwo());
//            } else {
//                SimBackActivity.launch(mContext, SimBackEnum.CHOICES_GOODS, null);
//            }
//        }


        //新会员专区
        @OnClick(R.id.iv_banner_three)
        public void onClickBannerThree() {
            if (bannerDataEntity != null && bannerDataEntity.getOptimal() != null && bannerDataEntity.getOptimal().getFour() != null) {
                CustomSkipUtils.toSkip(mContext, bannerDataEntity.getOptimal().getFour());
            } else {
               SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_REGION, null);


            }
        }


//        //发现好货
//        @OnClick(R.id.iv_banner_four)
//        public void onClickBannerFour() {
//            if (bannerDataEntity != null && bannerDataEntity.getOptimal() != null && bannerDataEntity.getOptimal().getFour() != null) {
//                CustomSkipUtils.toSkip(mContext, bannerDataEntity.getOptimal().getFour());
//                Log.d("4666++++++++", "onClickBannerFour: "+"dddddddddd");
//
//            } else {
 //               DiscoverActivity.launch(mContext);
//
//            }
//        }

        /**
         * 必买街
         */
        private void initShopping() {
            boolean isNull = mShoppingList == null || mShoppingList.size() == 0;
            fl_shopping_empty.setVisibility(isNull ? View.VISIBLE : View.GONE);
            if (!isNull) {
                LinearLayoutManager manager = new LinearLayoutManager(mContext);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv_shopping.setLayoutManager(manager);
                rv_shopping.setAdapter(new HomeShoppingAdapter(mContext, mShoppingList));
            }
        }

        /**
         * 推荐栏目
         */
        private void initRecommendTitle() {
            tv_recommend_empty.setVisibility(mTitleList == null || mTitleList.size() == 0 ? View.VISIBLE : View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_recommend_classify.setLayoutManager(manager);
            MallRecommendTitleAdapter mallRecommendTitleAdapter = new MallRecommendTitleAdapter(mContext, mTitleList);
            mallRecommendTitleAdapter.setItemClickListener(new RecycleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (mTitleList != null && mTitleList.size() != 0) {
                        mHttpId = mTitleList.get(position).getId();
                        refreshReplace();
                    }
                }
            });
            rv_recommend_classify.setAdapter(mallRecommendTitleAdapter);
        }

        /**
         * 初始化首页轮播图 以及 圆点指示器
         */
        private void initCarouse() {
            iv_banner_default.setVisibility(bannerDataEntity == null ? View.VISIBLE : View.GONE);
            if (bannerDataEntity == null) {
                return;
            }
            List<BannerImageEntity> top = bannerDataEntity.getTop();
            cbanner_img.setPageIndicator(new int[]{R.drawable.xiang_huise, R.drawable.xing_bai});
            cbanner_img.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
            cbanner_img.setPages(new CBViewHolderCreator() {
                @Override
                public Object createHolder() {
                    return new HomePageBannerViewHolder();
                }
            }, top);
            cbanner_img.startTurning(3000);
        }

        /**
         * 初始化商品栏
         */
        private void initMainItem() {
            ll_main_shopping.setVisibility(dataEntity != null ? View.VISIBLE : View.GONE);
            if (dataEntity != null) {
                List<ModelHomeEntranceBean> mainList = JSONObject.parseArray(dataEntity.getOther().toString(), ModelHomeEntranceBean.class);
                initColumn(mainList, main_home_entrance_vp, main_home_entrance_indicator, "MAIN");
            }
        }

        /**
         * 初始化导览Item
         */
        private void initColumn(List<ModelHomeEntranceBean> list, final WrapContentHeightViewPager viewPager, MagicIndicator magicIndicator, String flag) {
            LogUtils.e("ModelHomeEntranceBeanList:",list.toString());
            viewPager.setVisibility(View.VISIBLE);
            if (list != null && list.size() > 0) {
                int pageCount = (int) Math.ceil(list.size() * 1.0 / PageCount);
                magicIndicator.setVisibility(pageCount > 1 ? View.VISIBLE : View.INVISIBLE);
                List<View> viewList = new ArrayList<>();
                for (int i = 0; i < pageCount; i++) {
                    RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(mContext).inflate(R.layout.item_hone_entrance_vp, viewPager, false);
                    //允许两行
                    GridLayoutManager manager = new GridLayoutManager(mContext, PageCount / 2);
                    recyclerView.setLayoutManager(manager);
                    EntranceAdapter entranceAdapter = new EntranceAdapter(mContext, list, i, PageCount);
                    recyclerView.setAdapter(entranceAdapter);
                    viewList.add(recyclerView);
                }
                CagegoryViewPagerAdapter adapter = new CagegoryViewPagerAdapter(viewList);
                viewPager.setAdapter(adapter);
                CircleNavigator circleNavigator = new CircleNavigator(mContext);
                circleNavigator.setCircleCount(viewList.size());

                circleNavigator.setCircleColor(mContext.getResources().getColor(R.color.color_theme_4));
                circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
                    @Override
                    public void onClick(int index) {
                        viewPager.setCurrentItem(index);
                    }
                });
                magicIndicator.setNavigator(circleNavigator);
                ViewPagerHelper.bind(magicIndicator, viewPager);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    private void toChangeTitleAlpha() {
        if (mRecycleY <= mRecycleDimension) {
            float alpha = mRecycleY / mRecycleDimension;
            view_bar.setAlpha(alpha);
            view_bar_bg.setAlpha(alpha);
        } else if (view_bar_bg.getAlpha() != 1) {
            view_bar.setAlpha(1);
            view_bar_bg.setAlpha(1);
        }
        //改变
//        if(ll_search.getTag() == null || ll_search.getTag().toString().equals("y") != mRecycleY > (mRecycleDimension / 2)){
//            toChangeText();
//        }
    }

    public void toChangeText() {
        boolean isAlpha = mRecycleY > (mRecycleDimension / 2);
        //文字颜色
        if (getActivity() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isAlpha) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        //图标变化
        ll_search.setTag(isAlpha ? "y" : "n");
        ll_search.setBackgroundResource(isAlpha ? R.drawable.home_search_bg_gray : R.drawable.home_search_bg);
        view_title_line.setVisibility(isAlpha ? View.VISIBLE : View.GONE);
        view_top_btn_bg_1.setBackgroundResource(isAlpha ? R.drawable.bg_no : R.drawable.bg_home_top);
        view_top_btn_bg_2.setBackgroundResource(isAlpha ? R.drawable.bg_no : R.drawable.bg_home_top);
        Glide.with(mContext).load(isAlpha ? R.drawable.icon_msg_black : R.drawable.icon_msg_white).into(iv_msg);
        Glide.with(mContext).load(isAlpha ? R.drawable.icon_share_black : R.drawable.icon_share_white).into(iv_share);
        tv_msg.setTextColor(Color.parseColor(isAlpha ? "#666666" : "#FFFFFF"));
        tv_share.setTextColor(Color.parseColor(isAlpha ? "#666666" : "#FFFFFF"));
    }

    private void toShare() {
        if (getFragmentManager() == null) {
            ToastAstrictUtils.getInstance().show("分享异常 - 05F");
        }
        String product = DfineAction.RES.getString(R.string.product);
        String url = "";
        String content = shareContent();
        if (content.indexOf("http") > 0) {
            url = content.substring(content.indexOf("http"));
        }
        Bitmap iconMap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        CustomShareDialog
                .getInstance()
//                .removeShare(CustomShareDialog.SHARE_TYPE_SINA)
//                .removeShare(CustomShareDialog.SHARE_TYPE_QZONE)
                .initEntity(new CustomShareEntity(content, url, product, ImageFileUtils.compressBitmap(iconMap)))
                .setShareActivity(new ShareListener())
                .show(getFragmentManager(), "");
    }

    public String shareContent() {
        // 推荐好友
        String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
        if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
            mRecommendInfo = DfineAction.InviteFriendInfo;
        }
        return mRecommendInfo;
    }
}