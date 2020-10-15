package com.weiwei.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.VsMyRedPagActivity;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SignInfoBean;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/10/9.
 *         Describe 我的成长金
 */
public class MyScoresActivity extends TempAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tablayout)
    XTabLayout tablayout;
    @BindView(R.id.rv_records)
    RecyclerView recordsRv;
    @BindView(R.id.tv_withdraw)
    TextView drawIntegralTv;
    @BindView(R.id.tv_transferred)
    TextView conversionIntegralTv;
    @BindView(R.id.fl_no_content)
    RelativeLayout blankRl;
    @BindView(R.id.tv_redbag)
    TextView regBagTv;

    /**
     * 红包提现
     */
    @BindView(R.id.rl_redbag)
    RelativeLayout redbagRl;
    /**
     * 分享红包提现
     */
    @BindView(R.id.rl__share_redbag)
    RelativeLayout shareRedbagRl;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    private List<ConsumptionRecordsEntity> recordsEntityList = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    /**
     * 请求参数   记录类型
     */
    private String flag = "redbag";
    private String integralType;
    private static final String TAG = "MyScoresActivity";

    private boolean mIsRefreshing = false;

    private CustomProgressDialog loadingDialog;

    private String coupon;      //兑换券
    private String red;         //红包
    private String invitation;  //邀请红包余额

    private String endTime;
    private String startTime = "2015-01-01";
    private SimpleDateFormat sDateFormat;

    private boolean isRegisterFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_scores);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_DE5A3C);
        ButterKnife.bind(this);
        initView();
        initAdapter();
        initBroadCastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterFlag && mycalllogReceiver != null) {
            unregisterReceiver(mycalllogReceiver);
            isRegisterFlag = false;
        }
    }

    /**
     * 获取成长金记录
     */
    private void initBroadCastReceiver() {
        IntentFilter mycalllogFilter = new IntentFilter();
        mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_RED_LOG);
        isRegisterFlag = true;
        registerReceiver(mycalllogReceiver, mycalllogFilter);

        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        endTime = sDateFormat.format(new java.util.Date());
        getMyRedRecords(flag);
    }

    private void getMyRedRecords(String flag) {
        switch (flag) {
            case "redbag":    //红包成长金
                if (!this.isFinishing() && pageNum == 1) {
                    loadingDialog.setLoadingDialogShow();
                }
                TreeMap<String, String> treeMap = new TreeMap<String, String>();
                treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
                treeMap.put("sdate", startTime);
                treeMap.put("edate", endTime);
                treeMap.put("rows", "10");
                treeMap.put("page", pageNum + "");
                CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyRedLog, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyRedLog);
                break;
            case "shopchange":   //商品兑换成长金
            case "shareredbag":   //分享红包明细记录
                getChangeRecords(flag);
                break;
            default:
                break;
        }
    }

    private void getChangeRecords(String flag) {
        if (!this.isFinishing() && pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        mIsRefreshing = true;
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", "10");
        retrofit2.Call<ResultEntity> call = null;
        switch (flag) {
            case "shopchange":
                //新增的兑换成长金
                String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
                params.put("phone", phone);
//                params.put("appId", "dudu");
                call = api.getExchangeList(params);
                break;
            case "shareredbag":
                call = api.getShareRedBagRecord(params);
                break;
            default:
                break;
        }
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    List<ConsumptionRecordsEntity> list = JSON.parseArray(result.getData().toString(), ConsumptionRecordsEntity.class);
                    recordsEntityList.addAll(list);
                    isLoading = false;
                    recordsAdapter.notifyDataSetChanged();
                    mIsRefreshing = false;
                    if (recordsEntityList.size() == 0) {
                        blankRl.setVisibility(View.VISIBLE);
                    } else {
                        blankRl.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(MyScoresActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private BroadcastReceiver mycalllogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadingDialog.setLoadingDialogDismiss();  //收到广播的时候取消进度条
            Bundle bundle = intent.getExtras();
            JSONObject js;
            try {
                if (bundle.get("list").toString().contains("频繁")) {
                    Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
                    return;
                }
                js = new JSONObject(bundle.get("list").toString());
                JSONArray ja = js.getJSONArray("items");
                for (int i = 0; i < ja.length(); i++) {
                    ConsumptionRecordsEntity entity = new ConsumptionRecordsEntity();
                    entity.setAmount(ja.getJSONObject(i).get("amount").toString());
                    entity.setRemark(ja.getJSONObject(i).get("remark").toString());
                    entity.setTime(ja.getJSONObject(i).get("ctime").toString());
                    entity.setStatus(ja.getJSONObject(i).get("status").toString());
                    recordsEntityList.add(entity);
                }
                recordsAdapter.notifyDataSetChanged();
                mIsRefreshing = false;
                if (recordsEntityList.size() == 0) {
                    blankRl.setVisibility(View.VISIBLE);
                } else {
                    blankRl.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void initView() {
        titleTv.setText("成长金");
        backRl.setOnClickListener(this);
        redbagRl.setOnClickListener(this);
        shareRedbagRl.setOnClickListener(this);

        tablayout.addTab(tablayout.newTab().setText("红包成长金明细"));
        tablayout.addTab(tablayout.newTab().setText("积分明细"));
        tablayout.addTab(tablayout.newTab().setText("分享红包明细"));

        tablayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                recordsEntityList.clear();
                recordsAdapter.notifyDataSetChanged();
                pageNum = 1;
                int position = tab.getPosition();
                if (position == 0) {
                    flag = "redbag";
                } else if (position == 1) {
                    flag = "shopchange";
                } else {
                    flag = "shareredbag";
                }
                getMyRedRecords(flag);
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
                recordsEntityList.clear();
                recordsAdapter.notifyDataSetChanged();
                pageNum = 1;
                int position = tab.getPosition();
                if (position == 0) {
                    flag = "redbag";
                } else if (position == 1) {
                    flag = "shopchange";
                } else {
                    flag = "shareredbag";
                }
                getMyRedRecords(flag);
            }
        });

        recordsRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //判断是否滑到的底部
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.e(TAG, "nestedScrollView已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    getMyRedRecords(flag);
                }
            }
        });


        recordsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recordsAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    getMyRedRecords(flag);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void initAdapter() {
        recordsAdapter = new RecordsAdapter(MyScoresActivity.this, recordsEntityList);
        recordsRv.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recordsRv.addItemDecoration(new SimpleDividerDecoration(this));
        recordsRv.setAdapter(recordsAdapter);
        recordsRv.setNestedScrollingEnabled(false);
    }

    private void initData() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String,String> params = new HashMap<>();
//        params.put("uid", VsUserConfig.getDataString(mContext,VsUserConfig.JKey_KcId));
//        params.put("appId", "dudu");
        retrofit2.Call<ResultEntity> call = api.getWallet2(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "我的成长金msg===>" + result.getData().toString());
                    RedBagEntity redBagEntity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
//                    coupon = redBagEntity.getSignIntegral();
                    red = redBagEntity.getRed();
                    invitation = redBagEntity.getInvitation();
                    drawIntegralTv.setText(invitation);
                    conversionIntegralTv.setText(redBagEntity.getExchangePoint());
                    regBagTv.setText(red);
                } else {
                    Toast.makeText(MyScoresActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
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
            case R.id.rl_redbag:   //红包提现
                Intent intent_balance = new Intent(this, VsMyRedPagActivity.class);
                String urlTo = VsUserConfig.getDataString(this, VsUserConfig.JKey_RED_PAGE);
                String[] aboutBusiness = new String[]{"我的红包", "", urlTo};
                intent_balance.putExtra("AboutBusiness", aboutBusiness);
                intent_balance.putExtra("uiFlag", "redbag");
                startActivity(intent_balance);
                break;
            case R.id.rl__share_redbag:   //分享红包提现
                SkipPageUtils.getInstance(this).skipPage(VsMyRedPagActivity.class, "uiFlag", "shareRedbag");
                break;
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }
}

