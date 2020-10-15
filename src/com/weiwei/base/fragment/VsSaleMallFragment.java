package com.weiwei.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.salemall.adapter.ShopRecommendAdapter;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.HomePageDataBean;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.inter.OnLoadingMore;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.NetUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/08/17
 *         特惠商城
 */

public class VsSaleMallFragment extends Fragment implements OnLoadingMore, View.OnClickListener {
    private View saleMallView;

    /**
     * 界面区别
     */
    @BindView(R.id.rv_for_you)
    RecyclerView recommendRv;
    @BindView(R.id.fl_no_data)
    RelativeLayout noDataFl;
    @BindView(R.id.fl_no_content)
    RelativeLayout blankRl;

    @BindView(R.id.btn_fresh)
    Button freshBtn;
    @BindView(R.id.btn_fresh_tv)
    Button blankfreshBtn;
    @BindView(R.id.fl_root)
    FrameLayout rootView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private static final String TAG = "VsSaleMallFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ShopRecommendAdapter recommendAdapter;
    private BannerDataEntity bannerDataEntity;

    private HomePageDataBean data;
    private List<ProductBean> choiceList = new ArrayList<>();

    boolean isLoadingData = false;
    private final int TYPE_WELFARE = 101;   //商企
    private final int TYPE_RECOMMEND = 103; //推荐
    private final int TYPE_CHOICES = 102;
    private GridLayoutManager manager;

    int pageNum = 2;
    private CustomProgressDialog loadingDialog;

    /**
     * 广告推送弹框
     */
    private CommonPopupWindow adPushPop;

    String productNo = "";
    String storeNo = "";
    String skipUrl = "";
    String seckillValue = "";

    private Handler popupHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adPushPop.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    adPushPop.update();
                    break;
            }
        }

    };

    public VsSaleMallFragment() {
    }

    public static VsSaleMallFragment newInstance(String param1, String param2) {
        VsSaleMallFragment fragment = new VsSaleMallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        saleMallView = inflater.inflate(R.layout.fragment_vs_sale_mall, container, false);
        ButterKnife.bind(this, saleMallView);
        registerNetChangedRecervier();
        initView();
        initHomePagePush();
        initBannerView();
        initHomePageData();
        return saleMallView;
    }

    /**
     * 初始化首页推送
     */
    private void initHomePagePush() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> params = new HashMap<>();
        params.put("mobileIdentity", VsUtil.getAloneImei(getActivity()));
        params.put("platform", "android");
        retrofit2.Call<ResultEntity> call = api.getHomePagePush(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "推送msg===>" + result.getData().toString());
                    BannerImageEntity bean = JSON.parseObject(result.getData().toString(), BannerImageEntity.class);
                    if (bean != null) {
                        showPushAdPop(bean);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void showPushAdPop(final BannerImageEntity bean) {
        adPushPop = new CommonPopupWindow.Builder(getActivity()).setView(R.layout.pop_ad_push).setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT).setAnimationStyle(R.style.adpop_anim).setBackGroundLevel(0.36f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
            @Override
            public void getChildView(View view, int layoutResId) {
                ImageView closeIv = (ImageView) view.findViewById(R.id.iv_close);
                closeIv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ad_push_close));
                ImageView adIv = (ImageView) view.findViewById(R.id.iv_ad);
                final String imageRes = JudgeImageUrlUtils.isAvailable(bean.getImageUrl());
                final String content = bean.getAndroidParams();
                final String androidClassName = bean.getAndroidClassName();
                JSONObject jsonObject = null;
                if (!StringUtils.isEmpty(content)) {
                    jsonObject = JSONObject.parseObject(content);
                }
                if (jsonObject != null) {
                    productNo = (String) jsonObject.get("productNo");
                    skipUrl = (String) jsonObject.get("skipUrl");
                    storeNo = (String) jsonObject.get("storeNo");
                    seckillValue = (String) jsonObject.get("seckillProductId");
                }

                Glide.with(getActivity()).load(imageRes).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(adIv);
                closeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adPushPop.dismiss();
                    }
                });
                adIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomSkipUtils.toSkip(mContext,bean);
//                        Intent intent = new Intent();
//                        //暂不加未登录跳转充值界面判断
//                        if (!StringUtils.isEmpty(androidClassName) && androidClassName.contains("com")) {
//                            intent.setClassName(getActivity(), androidClassName);
//                            if (androidClassName.equals(VIPACTIVITY_CLASSNAME) || androidClassName.equals(RECHARGE_CLASSNAME)) {    //VIP会员界面
//                                if (!CheckLoginStatusUtils.isLogin()) {
//                                    ToastUtils.show(mContext, getResources().getString(R.string.vip_login_hint));
//                                    SkipPageUtils.getInstance(getActivity()).skipPage(VsLoginActivity.class);
//                                    adPushPop.dismiss();
//                                    return;
//                                }
//                            }
//                            if (!StringUtils.isEmpty(seckillValue) && !StringUtils.isEmpty(productNo)) {   //秒杀商品的判断
//                                intent.putExtra("seckill", "seckill");
//                                intent.putExtra("seckillProductId", seckillValue);
//                                intent.putExtra("productNo", productNo);
//                                startActivity(intent);
//                                adPushPop.dismiss();
//                                return;
//                            }
//                        } else {
//                            return;
//                        }
//
//                        if (!StringUtils.isEmpty(productNo)) {
//                            intent.putExtra("productNo", productNo);
//                            intent.putExtra("productImage", imageRes);
//                        }
//                        if (!StringUtils.isEmpty(storeNo)) {
//                            intent.putExtra("storeNo", storeNo);
//                        }
//                        if (!StringUtils.isEmpty(skipUrl)) {
//                            intent.putExtra("skipUrl", skipUrl);
//                        }
//                        startActivity(intent);
                        adPushPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        popupHandler.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 监听当前网络状态的广播
     */
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
                        //有网
                        if (NetUtils.isNetworkAvailable(mContext)) {
                            recommendAdapter.setLoading(false);
                            initHomePagePush();
                            initBannerView();
                            initHomePageData();
                            noDataFl.setVisibility(View.GONE);
                        } else {  //没网
                            noDataFl.setVisibility(View.VISIBLE);
                            blankRl.setVisibility(View.GONE);
                            recommendRv.setVisibility(View.GONE);
                        }

                        freshBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //有网
                                if (NetUtils.isNetworkAvailable(mContext)) {
                                    recommendAdapter.setLoading(false);
                                    noDataFl.setVisibility(View.GONE);
                                    initHomePagePush();
                                    initBannerView();
                                    initHomePageData();
                                } else { //没网
                                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
            }
        }.register(mContext);
    }

    private void initView() {
        loadingDialog = new CustomProgressDialog(getActivity(), "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        manager = new GridLayoutManager(getActivity(), 6);
        manager.setSmoothScrollbarEnabled(false);
        recommendRv.setHasFixedSize(true);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = recommendAdapter.getItemViewType(position);
                if (type == TYPE_CHOICES) {
                    return 3;
                }
                if (type == TYPE_RECOMMEND) {
                    return 2;
                }
                if (type == 106) {
                    return 3;
                }
                return manager.getSpanCount();
            }
        });
        recommendRv.setLayoutManager(manager);
        data = new HomePageDataBean();
        recommendAdapter = new ShopRecommendAdapter(getActivity(), recommendRv, data);
        recommendRv.setAdapter(recommendAdapter);
        recommendAdapter.setLoadingMore(this);

        blankfreshBtn.setOnClickListener(this);

        /***解决SwipeRefreshLayout和RecyclerView存在滑动冲突的问题***/
        recommendRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initBannerView();
                pageNum = 2;
                isLoadingData = false;
                initHomePageData();
                recommendAdapter.freshSeckillData();
//                recommendAdapter.notifyDataSetChanged();
                //传入false表示刷新失败
                refreshlayout.finishRefresh(1000, true);
            }
        });
    }

    /**
     * 加载Banner数据
     */
    private void initBannerView() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        retrofit2.Call<ResultEntity> call = api.getBanners();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Log.e(TAG, "banners图片msg===>" + result.getMsg());
                if (REQUEST_CODE == result.getCode()) {
                    bannerDataEntity = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);
                    recommendAdapter.setBanerEntity(bannerDataEntity);
                    recommendAdapter.notifyDataSetChanged();
                    //有数据
                    if (result.getData() != null) {
                        blankRl.setVisibility(View.GONE);
                        recommendRv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 获取首页数据
     */
    private void initHomePageData() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        retrofit2.Call<ResultEntity> call = api.getProducts();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "主页msg===>" + result.getData().toString());
                    HomePageDataBean bean = JSON.parseObject(result.getData().toString(), HomePageDataBean.class);
                    recommendAdapter.addData(bean);

                    recommendAdapter.addChoices(choiceList);
                    recommendAdapter.notifyDataSetChanged();
                    isLoadingData = false;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 下拉加载更多
     */
    private void loadMoreData() {
        if (isLoadingData) {
            return;
        }
        isLoadingData = true;
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", 10 + "");
        params.put("type", 1 + "");
        retrofit2.Call<ResultEntity> call = api.getProductPage(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "获取更多精选商品msg==>" + result.getData().toString());
                    choiceList.clear();
                    ResultDataEntity entity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                    List<ProductBean> beanList = JSONObject.parseArray(entity.getRecords().toString(), ProductBean.class);
                    choiceList.addAll(beanList);
                    pageNum++;
                    isLoadingData = false;
                    recommendAdapter.setLoading(false);

                    recommendRv.setVisibility(View.VISIBLE);
                    recommendAdapter.addChoices(choiceList);
                    recommendAdapter.notifyDataSetChanged();
                    isLoadingData = false;
                } else {
                    isLoadingData = true;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLoadMore() {
        if (!isLoadingData) loadMoreData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fresh_tv:
                //有网
                if (NetUtils.isNetworkAvailable(mContext)) {
                    recommendAdapter.setLoading(false);
                    noDataFl.setVisibility(View.GONE);
                    initBannerView();
                    initHomePageData();
                } else { //没网
                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
