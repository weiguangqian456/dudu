package com.weiwei.salemall.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weiwei.salemall.adapter.SeckillProductAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SeckillDataEntity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.DateUtil;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.NetUtils;
import com.weiwei.salemall.utils.TimeChangeUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/12/05
 * 秒杀商品界面
 */
public class SecKillActivity extends TempAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_miaosha_shi)
    TextView secKillHours;
    @BindView(R.id.tv_miaosha_minter)
    TextView secKillMinutes;
    @BindView(R.id.tv_miaosha_second)
    TextView secKillSeconds;
    @BindView(R.id.rv_seckill)
    RecyclerView secKillRv;
    @BindView(R.id.tv_next)
    TextView nextTv;
    @BindView(R.id.ll_countdownView)
    LinearLayout countDownLl;
    @BindView(R.id.iv_banner_seckill)
    ImageView banner;
    /**
     * 没网界面
     */
    @BindView(R.id.fl_no_data)
    RelativeLayout noDataFl;
    /**
     * 秒杀商品空界面
     */
    @BindView(R.id.fl_no_content)
    RelativeLayout blankRl;

    @BindView(R.id.btn_fresh)
    Button freshBtn;
    @BindView(R.id.btn_fresh_tv)
    Button blankfreshBtn;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private static final String TAG = "SecKillActivity";
    private String status;

    private CustomProgressDialog loadingDialog;
    private List<SeckillProductEntity> seckillProductEntityList = new ArrayList<>();
    private SeckillProductAdapter adapter;

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;
    boolean isCountDown = false;

    private long time;  //倒计时时长
    private long time_s;

    private long time_v2;  //倒计时时长
    private long time_s_v2;

    private String endTime;
    private String startTime;
    private String now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_kill);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        ButterKnife.bind(this);
        registerNetChangedRecervier();
        initView();
        getSeckillStatus();
        initBanner();
        initData();
    }

    private void initBanner() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getBanners();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "秒杀顶部banner===>" + result.getMsg());
                    BannerDataEntity bannerDataBean = JSONObject.parseObject(result.getData().toString(), BannerDataEntity.class);
                    BannerImageEntity seckillEntity = bannerDataBean.getSeckill();
                    setBanner(seckillEntity);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void countTime() {
        if (!isCountDown) {
            handler.postDelayed(runnable, 1000);
            isCountDown = true;
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time_s--;
            time_s_v2--;
            String formatLongToTimeStr = "";
            switch (status) {
                case "underway":
                    formatLongToTimeStr = TimeChangeUtils.getInstance(SecKillActivity.this).formatLongToTimeStr(time_s);
                    break;
                case "wait":
                    formatLongToTimeStr = TimeChangeUtils.getInstance(SecKillActivity.this).formatLongToTimeStr(time_s_v2);
                    break;
                default:
                    break;
            }
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    if (Integer.parseInt(split[0]) >= 10) {
                        if (secKillHours != null) secKillHours.setText(split[0]);
                    } else {
                        if (secKillHours != null) secKillHours.setText("0" + split[0]);
                    }
                }
                if (i == 1) {
                    if (Integer.parseInt(split[1]) >= 10) {
                        if (secKillMinutes != null) secKillMinutes.setText(split[1]);
                    } else {
                        if (secKillMinutes != null) secKillMinutes.setText("0" + split[1]);
                    }
                }
                if (i == 2) {
                    if (Integer.parseInt(split[2]) >= 10) {
                        if (secKillSeconds != null) secKillSeconds.setText(split[2]);
                    } else {
                        if (secKillSeconds != null) secKillSeconds.setText("0" + split[2]);
                    }
                }
            }


            switch (status) {
                case "underway":
                    if (time_s > 0) {
                        handler.postDelayed(this, 1000);
                    } else {  //时间到
                        handler.removeCallbacks(runnable);
                        /**=======倒计时结束重新获取倒计时状态=======**/
                        isCountDown = false;
                        getSeckillStatusAgain();
                    }
                    break;
                case "wait":
                    if (time_s_v2 > 0) {
                        handler.postDelayed(this, 1000);
                    } else {  //时间到
                        handler.removeCallbacks(runnable);
                        /**=======倒计时结束重新获取倒计时状态=======**/
                        isCountDown = false;
                        getSeckillStatusAgain();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void getSeckillStatus() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "1");
        params.put("pageNum", "1");
        retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                    status = seckillDataEntity.getStatus();
                    initAdapter(status);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void getSeckillStatusAgain() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "1");
        params.put("pageNum", "1");
        retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                    status = seckillDataEntity.getStatus();
                    initAdapter(status);
                    startTime = DateUtil.stampToDate(seckillDataEntity.getStartTime());
                    endTime = DateUtil.stampToDate(seckillDataEntity.getEndTime());
                    now = DateUtil.stampToDate(seckillDataEntity.getNow());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date toDate = df.parse(endTime);
                        Date fromDate = df.parse(now);
                        Date toDate_v2 = df.parse(startTime);
                        if (toDate.getTime() > fromDate.getTime()) {
                            time = toDate.getTime() - fromDate.getTime();
                            time_s = time / 1000;   //ms  ->  s
                        }

                        if (toDate_v2.getTime() > fromDate.getTime()) {
                            time_v2 = toDate_v2.getTime() - fromDate.getTime();
                            time_s_v2 = time_v2 / 1000;   //ms  ->  s
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    switch (status) {
                        case "end":
                            nextTv.setText("秒杀结束");
                            break;
                        case "underway":
                            nextTv.setText("秒杀进行中");
                            countTime();
                            break;
                        case "wait":
                            nextTv.setText("秒杀未开始");
                            countTime();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
                        if (NetUtils.isNetworkAvailable(mContext)) {   //有网
                            noDataFl.setVisibility(View.GONE);
                        } else {  //没网
                            noDataFl.setVisibility(View.VISIBLE);
                            blankRl.setVisibility(View.GONE);
                            secKillRv.setVisibility(View.GONE);
                        }
                    }
                };
            }
        }.register(mContext);
    }

    private void initView() {
        title.setText("秒杀专区");
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        /***点击事件***/
        backRl.setOnClickListener(this);
        blankfreshBtn.setOnClickListener(this);
        freshBtn.setOnClickListener(this);

        /***下拉加载更多***/
        secKillRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    initData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });


        /***解决SwipeRefreshLayout和RecyclerView存在滑动冲突的问题***/
        secKillRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                if (seckillProductEntityList != null) seckillProductEntityList.clear();
                if (adapter != null) {
                    pageNum = 1;
                    isLoading = false;
                    initData();
                }
                refreshlayout.finishRefresh(1000, true);//传入false表示刷新失败
            }
        });
    }

    private void setBanner(BannerImageEntity seckillEntity) {
        String url = seckillEntity.getImageUrl();
        String imageUrl = JudgeImageUrlUtils.isAvailable(url);
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.mall_member_welfare_default);
        Glide.with(this).load(imageUrl).apply(options).into(banner);
        int skipType = seckillEntity.getSkipType();
        final String params = seckillEntity.getAndroidParams();
        final String className = seckillEntity.getAndroidClassName();
        JSONObject jsonObject = JSONObject.parseObject(params);
        if (jsonObject != null) {
            final String productNo = (String) jsonObject.get("productNo");
            final String skipUrl = (String) jsonObject.get("skipUrl");
            final String storeNo = (String) jsonObject.get("storeNo");
            if (params != null && !StringUtils.isEmpty(className)) {
                switch (skipType) {
                    case 0: //app内部链接
                        banner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                if (className.contains("VsRechargeActivity") && !CheckLoginStatusUtils.isLogin()) {
                                    intent.setClassName(SecKillActivity.this, "com.weiwei.base.activity.login.VsLoginActivity");
                                    startActivity(intent);
                                } else {
                                    intent.setClassName(SecKillActivity.this, className);
                                    if (!StringUtils.isEmpty(productNo)) {
                                        intent.putExtra("productNo", productNo);
                                    } else {
                                        intent.putExtra("storeNo", storeNo);
                                    }
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case 1:   //外链
                        banner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClassName(SecKillActivity.this, className);
                                intent.putExtra("skipUrl", skipUrl);
                                startActivity(intent);
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void initAdapter(String status) {
        adapter = new SeckillProductAdapter(this, seckillProductEntityList, status);
        secKillRv.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        secKillRv.addItemDecoration(new SimpleDividerDecoration(this));
        secKillRv.setAdapter(adapter);
        secKillRv.setNestedScrollingEnabled(false);
    }

    /**
     * 加载本界面数据
     */
    private void initData() {
        if (!this.isFinishing() && pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "10");
        params.put("pageNum", pageNum + "");
        retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "首页秒杀商品List msg===>" + result.getData().toString());
                    SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);

                    /***显示列表***/
                    List<SeckillProductEntity> seckillProductList = JSONObject.parseArray(seckillDataEntity.getPrdoucts().toString(), SeckillProductEntity.class);
                    if (seckillProductList != null) {
                        seckillProductEntityList.addAll(seckillProductList);
                        noDataFl.setVisibility(View.GONE);
                        secKillRv.setVisibility(View.VISIBLE);
                    }
                    isLoading = false;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    /***关于秒杀***/
                    status = seckillDataEntity.getStatus();
                    startTime = DateUtil.stampToDate(seckillDataEntity.getStartTime());
                    endTime = DateUtil.stampToDate(seckillDataEntity.getEndTime());
                    now = DateUtil.stampToDate(seckillDataEntity.getNow());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date toDate = df.parse(endTime);
                        Date fromDate = df.parse(now);
                        Date toDate_v2 = df.parse(startTime);
                        if (toDate.getTime() > fromDate.getTime()) {
                            time = toDate.getTime() - fromDate.getTime();
                            time_s = time / 1000;   //ms  ->  s
                        }

                        if (toDate_v2.getTime() > fromDate.getTime()) {
                            time_v2 = toDate_v2.getTime() - fromDate.getTime();
                            time_s_v2 = time_v2 / 1000;   //ms  ->  s
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    switch (status) {
                        case "end":
                            nextTv.setText("秒杀结束");
                            break;
                        case "underway":
                            nextTv.setText("秒杀进行中");
                            countTime();
                            break;
                        case "wait":
                            nextTv.setText("秒杀未开始");
                            countTime();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    @Override
    protected void init() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_fresh:
            case R.id.btn_fresh_tv:
                if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                    noDataFl.setVisibility(View.GONE);
                } else { //没网
                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
    }
}
