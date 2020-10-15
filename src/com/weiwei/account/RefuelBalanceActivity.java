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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Describe 我的成长金
 */
public class RefuelBalanceActivity extends TempAppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_back)
    RelativeLayout mRlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.title)
    RelativeLayout mTitle;
    @BindView(R.id.tv_transferred)
    TextView mTvTransferred;
    @BindView(R.id.tablayout)
    XTabLayout mTablayout;
    @BindView(R.id.rv_records)
    RecyclerView mRvRecords;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.fl_no_content)
    RelativeLayout mFlNoContent;

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
    private String flag = "shopchange";
    private static final String TAG = "MyScoresActivity";

    private boolean mIsRefreshing = false;

    private CustomProgressDialog loadingDialog;

    private String endTime;
    private String startTime = "2015-01-01";
    private SimpleDateFormat sDateFormat;

    private boolean isRegisterFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refuel_balance);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_DE5A3C);
        ButterKnife.bind(this);
        initView();
        initAdapter();
        initBroadCastReceiver();
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
        endTime = sDateFormat.format(new Date());
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
        Call<ResultEntity> call = null;
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
                        mFlNoContent.setVisibility(View.VISIBLE);
                    } else {
                        mFlNoContent.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(RefuelBalanceActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
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
                    mFlNoContent.setVisibility(View.VISIBLE);
                } else {
                    mFlNoContent.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void initView() {
        mTvTitle.setText("我的积分");
        mRlBack.setOnClickListener(this);

        mTablayout.addTab(mTablayout.newTab().setText("积分明细"));

        mTablayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                recordsEntityList.clear();
                recordsAdapter.notifyDataSetChanged();
                pageNum = 1;
                int position = tab.getPosition();
                if (position == 0) {
                    flag = "shopchange";
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
                    flag = "shopchange";
                } else if (position == 1) {
                    flag = "shopchange";
                } else {
                    flag = "shareredbag";
                }
                getMyRedRecords(flag);
            }
        });

        mRvRecords.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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


        mRvRecords.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        recordsAdapter = new RecordsAdapter(RefuelBalanceActivity.this, recordsEntityList);
        mRvRecords.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvRecords.addItemDecoration(new SimpleDividerDecoration(this));
        mRvRecords.setAdapter(recordsAdapter);
        mRvRecords.setNestedScrollingEnabled(false);
    }

    private void initData() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
//        params.put("uid", VsUserConfig.getDataString(mContext,VsUserConfig.JKey_KcId));
//        params.put("appId", "dudu");
        Call<ResultEntity> call = api.getWallet2(params);
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
                    mTvTransferred.setText(redBagEntity.getExchangePoint());
                } else {
                    Toast.makeText(RefuelBalanceActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
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
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
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

}

