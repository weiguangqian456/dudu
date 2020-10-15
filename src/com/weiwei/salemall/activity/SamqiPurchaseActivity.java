package com.weiwei.salemall.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.home.entity.HomeBannerDataEntiy;
import com.weiwei.home.fragment.NewGoodsFragment;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.HomePageBannerViewHolder;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SamqiColumnBean;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.NetUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.activity.SelectCityActivity.action;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author Created by EDZ on 2018/07/26
 * 商企采购专区
 */
public class SamqiPurchaseActivity extends TempAppCompatActivity {
    @BindView(R.id.fl_back)
    FrameLayout backIv;
    @BindView(R.id.rl_shoppingCart)
    FrameLayout shoppingCartIv;
    @BindView(R.id.btn_fresh)
    Button freshBtn;
    @BindView(R.id.btn_fresh_tv)
    Button blankFreshBtn;

    @BindView(R.id.ll_firststyle)
    LinearLayout firstStyleLl;
    @BindView(R.id.magic_indicator)
    MagicIndicator firstIndicator;
    @BindView(R.id.view_pager)
    ViewPager firstVp;
    @BindView(R.id.badgeView)
    BadgeView badgeView;

    /**
     * 多界面判断
     */
    @BindView(R.id.fl_no_data)
    RelativeLayout noDataFl;
    @BindView(R.id.fl_no_content)
    RelativeLayout noContentFl;

    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.ll_search)
    LinearLayout mLlSearch;
    @BindView(R.id.ll_top)
    LinearLayout mLlTop;
    @BindView(R.id.iv_no_net)
    ImageView mIvNoNet;
    @BindView(R.id.tv_no_net)
    TextView mTvNoNet;
    @BindView(R.id.iv_no_content)
    ImageView mIvNoContent;
    @BindView(R.id.tv_no_content)
    TextView mTvNoContent;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.cbanner_img)
    ConvenientBanner mCbannerImg;
    @BindView(R.id.iv_banner_default)
    ImageView mIvBannerDefault;
    @BindView(R.id.iv_image_top)
    ImageView mIvImageTop;
    @BindView(R.id.toolbarLayout)
    CollapsingToolbarLayout toolbarLayout;
//    @BindView(R.id.iv_title)
//    ImageView mIvTitle;

    /**
     * 栏目
     */
    private List<SamqiColumnBean> mDataList = null;

    private List<Fragment> fragmentList = null;

    private static final String TAG = "LocalGoodsActivity";
    private String mHttpId = "";

    private DataBean dataEntity = null;

    private List<BannerImageEntity> bannerData;


    /**
     * 栏目Id
     */
    private String mColumnId = null;
    private String mColumnName = null;

    private static ShoppingItemsBeanDao shoppingItemsBeanDao;
    private int shoppingCartcount;

    private CustomProgressDialog loadingDialog;

    private int mAlphaHeight = 300;
 /*   //商企
    private String TYPE_BUSINESS = "7fd8c09313834b6bb66c1da3b4f5547d";
    //嘟嘟优选
    private String TYPE_OPTIMIZATION = "bf5437594c2d487ea7c8e9639b281bd0";

    private String TYPE_PREFERENTIAL = "a84ac91400124a6c878052fe255785b1";//  //品牌特惠
    private String TYPE_SAMESHOP = "f7af1ef2a2fa43e0b0e4146d566783aa";   //同城商家
    private String TYPE_DUOCAIDUIHUAN = "b23480a0f7bc477b896727d1cdfb6263";  //多彩兑换
    private String TYPE_CHONHEUYONGPIN = "02ff736583974ad699fa632091783afb";    //宠物用品
    private String TYPE_JIAYOGDIANQI = "82eaf67a2e774eaea22c9bced525ce69";    //家用电器
    private String TYPE_SHUMABANGONG = "5c9514422a5f4a82967b1adf805f0f92";    //数码办公

    private String TYPE_ZHONGWAIMINGJIUL = "6d799edb9a1c4d62b7070f259fd1a7bf";  //中外名酒
    private String TYPE_JIAJUBAIHUO = "e90f7a097ad14e5abf1cf70445a1f736";   //家具百货
    private String TYPE_JINGXUANXIANGBAO = "934353c257e94e02bfc09ff54fb7af68";  //精选箱包
    private String TYPE_CHUFANGYONGPIN = "64f17d9a3a42499caee25ba9b3e8ceaf";    //厨房用品
    private String TYPE_SHIPINSHNEGXIAN = "34344ca3d9be47f088b6c895927961be";    //食品生鲜
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitStateUtils.setImmersionStateMode(this);


        setContentView(R.layout.activity_samqi_purchase);
        ButterKnife.bind(this);


        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        registerNetChangedRecervier();

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float absVerticalOffset = Math.abs(verticalOffset);

                float alpha = (absVerticalOffset > mAlphaHeight ? 1 : absVerticalOffset / mAlphaHeight);
                mTvTitleBackground.setAlpha(alpha);

            }
        });


        initBroadCast();
        initView();
        getBanner();

    }

    private void getBanner() {   //获取banner
        RetrofitClient.getInstance(this).Api()
                .getBanner()
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        Map map = JSON.parseObject(result.getData().toString(),Map.class);
                        List<BannerImageEntity> bannerData = JSON.parseArray(map.get(mColumnId).toString(), BannerImageEntity.class);
//                        BannerDataEntity banner = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);
//                        LogUtils.e("banner:",banner.toString());
                      /*  if (TYPE_BUSINESS.equals(mColumnId)) {
                            if (banner.business == null || banner.business.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_ZHONGWAIMINGJIUL.equals(mColumnId)) {
                            if (banner.jiu == null || banner.jiu.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_OPTIMIZATION.equals(mColumnId)) {
                            if (banner.optimization == null || banner.optimization.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_PREFERENTIAL.equals(mColumnId)) {
                            if (banner.preferential == null || banner.preferential.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_JINGXUANXIANGBAO.equals(mColumnId)) {
                            if (banner.xiangbao == null || banner.xiangbao.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_JIAJUBAIHUO.equals(mColumnId)) {
                            if (banner.jiaju == null || banner.jiaju.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_JIAYOGDIANQI.equals(mColumnId)) {
                            if (banner.electric == null || banner.electric.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_SHUMABANGONG.equals(mColumnId)) {
                            if (banner.digital == null || banner.digital.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_CHUFANGYONGPIN.equals(mColumnId)) {
                            if (banner.chufang == null || banner.chufang.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (TYPE_SHIPINSHNEGXIAN.equals(mColumnId)) {
                            if (banner.shiping == null || banner.shiping.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }

                        } else if (TYPE_CHONHEUYONGPIN.equals(mColumnId)) {
                            if (banner.pet == null || banner.pet.size() == 0) {
                                mIvBannerDefault.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
*/

 //                       mIvBannerDefault.setVisibility(View.GONE);
//                        List<BannerImageEntity> bannerData;


                     /*   if (TYPE_BUSINESS.equals(mColumnId)) {
                            bannerData = banner.business;
                        } else if (TYPE_OPTIMIZATION.equals(mColumnId)) {
                            bannerData = banner.optimization;
                        } else if (TYPE_PREFERENTIAL.equals(mColumnId)) {
                            bannerData = banner.preferential;
                        } else if (TYPE_SHUMABANGONG.equals(mColumnId)) {
                            bannerData = banner.digital;
                        } else if (TYPE_JIAYOGDIANQI.equals(mColumnId)) {
                            bannerData = banner.electric;
                        } else if (TYPE_CHONHEUYONGPIN.equals(mColumnId)) {
                            bannerData = banner.pet;
                        } else if (TYPE_ZHONGWAIMINGJIUL.equals(mColumnId)) {
                            bannerData = banner.jiu;
                        } else if (TYPE_JIAJUBAIHUO.equals(mColumnId)) {
                            bannerData = banner.jiaju;
                        } else if (TYPE_JINGXUANXIANGBAO.equals(mColumnId)) {
                            bannerData = banner.xiangbao;
                        } else if (TYPE_CHUFANGYONGPIN.equals(mColumnId)) {
                            bannerData = banner.chufang;
                        } else if (TYPE_SHIPINSHNEGXIAN.equals(mColumnId)) {
                            bannerData = banner.shiping;
                        } else {
                            bannerData = new ArrayList<>();
                        }*/

                        mCbannerImg.setPageIndicator(new int[]{R.drawable.xiang_huise, R.drawable.xing_bai});
                        mCbannerImg.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                        mCbannerImg.setPages(new CBViewHolderCreator() {
                            @Override
                            public Object createHolder() {
                                return new HomePageBannerViewHolder();
                            }
                        }, bannerData);
                        mCbannerImg.startTurning(3000);

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        mIvBannerDefault.setVisibility(View.VISIBLE);
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", getTotalNum());
        mContext.sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(myBroadCastReceiver);
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter(action);
        mContext.registerReceiver(myBroadCastReceiver, filter);
    }

    BroadcastReceiver myBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setAction(action);
            shoppingCartcount = intent.getIntExtra("cartNumber", 0);
            setBadgeView(shoppingCartcount);
        }
    };

    //监听当前网络状态的广播
    private void registerNetChangedRecervier() {
        new IntentFilterUtils() {
            @Override
            public NetStateChangedReceiver getNetStateChangedReceiver() {
                return new NetStateChangedReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        handleOnNetStateChanged();
                    }

                    @Override
                    public void handleOnNetStateChanged() {
                        if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                            setView();
                            noDataFl.setVisibility(View.GONE);
                        } else {   //没网
                            noDataFl.setVisibility(View.VISIBLE);
                            noContentFl.setVisibility(View.GONE);
                        }
                        freshBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                                    setView();
                                } else {
                                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
            }
        }.register(mContext);
    }

    private void setBadgeView(int count) {
        badgeView.invalidate();
        if (0 != count) {
            badgeView.setVisibility(View.VISIBLE);
            badgeView.setText(count + "");
            if (count > 10) {
                badgeView.setText("10+");
            }
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void init() {
    }

    private void initView() {
        mColumnId = getIntent().getStringExtra("columnId");
        mColumnName = getIntent().getStringExtra("columnName");


        tv_title.setText(mColumnName);
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

    }

    private void setView() {
        firstStyleLl.setVisibility(View.VISIBLE);
        getMagicIndicatorData();
    }

    /**
     * 获取商企子栏目
     */
    private void getMagicIndicatorData() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Call<ResultEntity> call = api.getChildColumnsData(mColumnId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                noContentFl.setVisibility(View.GONE);
                loadingDialog.setLoadingDialogDismiss();
                noContentFl.setVisibility(View.VISIBLE);
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "商企采购栏目下面的子栏目msg===>" + result.getData().toString());
                    List<SamqiColumnBean> list = JSONObject.parseArray(result.getData().toString(), SamqiColumnBean.class);
                    mDataList = new ArrayList<>();
                    mDataList.addAll(list);
                    noContentFl.setVisibility(View.GONE);
                } else {
                    noContentFl.setVisibility(View.VISIBLE);
                }

                initMagicIndicator();
                initViewPager();
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void initMagicIndicator() {
        firstIndicator.setBackgroundColor(Color.WHITE);
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
                        firstVp.setCurrentItem(index);
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
        firstIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(firstIndicator, firstVp);
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", mDataList.get(i).getId());
            bundle.putString("mColumnId", mColumnId);
            Fragment localStoreFragment = NewGoodsFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        firstVp.setOffscreenPageLimit(1);
        firstVp.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragmentList));
        firstVp.setCurrentItem(0);
    }

    @OnClick({R.id.fl_back, R.id.et_search, R.id.rl_shoppingCart, R.id.btn_fresh_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.et_search:
                String[] key = new String[]{"skipFlag", "mColumnId"};
                String[] value = new String[]{"2", mColumnId};
                skipPage(SearchViewActivity.class, key, value);
                break;
            case R.id.rl_shoppingCart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                EventBus.getDefault().post(bean);
                Intent intent = new Intent();
                intent.putExtra("indicator", 3);

                intent.setClass(this, VsMainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_fresh_tv:
                if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                    setView();
                } else {
                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private int getTotalNum() {
        int num = 0;
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (Iterator iterators = beanList.iterator(); iterators.hasNext(); ) {
            ShoppingItemsBean bean = (ShoppingItemsBean) iterators.next();
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }


}