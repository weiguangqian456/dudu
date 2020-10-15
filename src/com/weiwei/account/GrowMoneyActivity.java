package com.weiwei.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.VsMyRedPagActivity;
import com.weiwei.base.adapter.InviteAdapter;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.retrofit.RetrofitUtils;
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
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/10/9.
 * Describe 我的成长金
 */
public class GrowMoneyActivity extends TempAppCompatActivity {
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tablayout)
    XTabLayout tablayout;
    @BindView(R.id.rv_records)
    RecyclerView recordsRv;
    @BindView(R.id.fl_no_content)
    RelativeLayout blankRl;
    @BindView(R.id.tv_redbag)
    TextView regBagTv;
    @BindView(R.id.nestedScrollView)
    public NestedScrollView nestedScrollView;
    @BindView(R.id.rl_grow)
    RelativeLayout rlGrow;
    @BindView(R.id.tv_no_content)
    TextView tvNoContent;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.rv_invite)
    RecyclerView rvInvite;

    private List<ConsumptionRecordsEntity> recordsEntityList = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    private List<SeckillTab.Records> recordsList = new ArrayList<>();
    private InviteAdapter adapter;


    /**
     * 加载更多
     */
    private int pageNum = 1;
    private int invitePageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    /**
     * 请求参数   记录类型
     */
    public String flag = "redbag";
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
        setContentView(R.layout.activity_grow_money);
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
        endTime = sDateFormat.format(new Date());
        getMyRedRecords(flag);
    }

    private void getMyRedRecords(String flag) {
        switch (flag) {
            case "redbag":    //红包成长金
               /* if (!this.isFinishing()) {
                    loadingDialog.setLoadingDialogShow();
                }*/
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
               getInviteData();
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
                String uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
                params.put("phone", phone);
 //               params.put("appId", "dudu");
 //               params.put("level","1");
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
                    LogUtils.e("recordsEntityList:",recordsEntityList.toString());
                    isLoading = false;
                    recordsAdapter.notifyDataSetChanged();
                    mIsRefreshing = false;
                    if (recordsEntityList.size() == 0) {
                        blankRl.setVisibility(View.VISIBLE);

                    } else {
                        blankRl.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(GrowMoneyActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
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
                LogUtils.e("recordsEntityList:",recordsEntityList.toString());
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
        titleTv.setText("我的成长金");

        tablayout.addTab(tablayout.newTab().setText("成长金明细"));

        tablayout.addTab(tablayout.newTab().setText("我的邀请"));

        tablayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
 //               recordsEntityList.clear();
 //               recordsAdapter.notifyDataSetChanged();
//                pageNum = 1;
                int position = tab.getPosition();
                if(position == 0) {
                    flag = "redbag";
 //                   rlGrow.setVisibility(View.GONE);
                    recordsRv.setVisibility(View.VISIBLE);
                    rvInvite.setVisibility(View.GONE);
                    llTitle.setVisibility(View.GONE);
                    tvNoContent.setText("暂无成长金记录");
 //                   getMyRedRecords(flag);
                    if(recordsEntityList.size() > 0) {
                        blankRl.setVisibility(View.GONE);
                    }else {
                        blankRl.setVisibility(View.VISIBLE);
                    }
                }else {
                    flag = "shopchange";
//                    rlGrow.setVisibility(View.VISIBLE);
                    recordsRv.setVisibility(View.GONE);
                    rvInvite.setVisibility(View.VISIBLE);
                    llTitle.setVisibility(View.VISIBLE);
                    tvNoContent.setText("暂无邀请记录");
                    if(invitePageNum == 1) {
                        getInviteData();
                    }
                    if(recordsList.size() > 0) {
                        blankRl.setVisibility(View.GONE);
                    }else {
                        blankRl.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
 //               recordsEntityList.clear();
 //               recordsAdapter.notifyDataSetChanged();
//                pageNum = 1;
                int position = tab.getPosition();
                if (position == 0) {
                    flag = "redbag";
                    llTitle.setVisibility(View.GONE);
                } else if (position == 1) {
                    llTitle.setVisibility(View.VISIBLE);
                    flag = "shopchange";
                } else {
                    llTitle.setVisibility(View.VISIBLE);
                    flag = "shareredbag";
                }
                getMyRedRecords(flag);
            }
        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //判断是否滑到的底部
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.e(TAG, "nestedScrollView已经滑到底了");
                    isLoading = true;
                    if(flag.equals("redbag")) {
                        pageNum++;
                    }else {
                        invitePageNum++;
                    }
                    getMyRedRecords(flag);
                }
            }
        });

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void initAdapter() {
        recordsAdapter = new RecordsAdapter(GrowMoneyActivity.this, recordsEntityList);
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
                    red = redBagEntity.getRed();
                    invitation = redBagEntity.getInvitation();
                    regBagTv.setText(red);
                } else {
                    Toast.makeText(GrowMoneyActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
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

    private void getInviteData() {
        if(!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        Map<String,String> params = new HashMap<>();
        String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        String uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        params.put("phone", phone);
        LogUtils.e("phone,",phone);
        LogUtils.e("uid",uid);
        params.put("appId", "dudu");
        params.put("pageNum",invitePageNum+"");
        params.put("level","1");
        RetrofitUtils.getInstance().inviteNum(params).enqueue(new Callback<SeckillTab>() {
            @Override
            public void onResponse(Call<SeckillTab> call, Response<SeckillTab> response) {
                if(!isFinishing()) {
                    loadingDialog.setLoadingDialogDismiss();
                }
                if(response.body() != null &&response.body().records != null) {
                    recordsList.addAll(response.body().records);
                    if (recordsList.size() > 0) {
                        rvInvite.setVisibility(View.VISIBLE);
                        blankRl.setVisibility(View.GONE);
                        inviteAdapter();
                    } else {
                        blankRl.setVisibility(View.VISIBLE);
                        rvInvite.setVisibility(View.GONE);
                    }
                }else {
                    rvInvite.setVisibility(View.GONE);
                    blankRl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SeckillTab> call, Throwable t) {
                LogUtils.e("出错了",t.getMessage());
                if(!isFinishing()) {
                    loadingDialog.setLoadingDialogDismiss();
                }
            }
        });
    }
    private void inviteAdapter() {
        adapter = new InviteAdapter(this,recordsList);
        rvInvite.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvInvite.addItemDecoration(new SimpleDividerDecoration(this));
        rvInvite.setAdapter(adapter);
        rvInvite.setNestedScrollingEnabled(false);

    }

    @OnClick({R.id.rl_back, R.id.rtv_go_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rtv_go_withdraw:
                Intent intent_balance = new Intent(this, VsMyRedPagActivity.class);
                String urlTo = VsUserConfig.getDataString(this, VsUserConfig.JKey_RED_PAGE);
                String[] aboutBusiness = new String[]{"我的红包", "", urlTo};
                intent_balance.putExtra("AboutBusiness", aboutBusiness);
                intent_balance.putExtra("uiFlag", "redbag");
                startActivity(intent_balance);
                break;
        }
    }
}

