package com.weiwei.rider.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseFragment;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.activity.RiderOrderDetailActivity;
import com.weiwei.rider.adapter.RiderOrderAdapter;
import com.weiwei.rider.bean.Order;
import com.weiwei.rider.bean.RiderOrder;
import com.weiwei.rider.utils.AlertDialog;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderOrderFragment extends BaseFragment {

    @BindView(R.id.swift)
    SwipeRefreshLayout swift;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    private int orderStatus;
    private String riderId;
    private Map<String,String> params;
    private List<Order> orderList;
    private RiderOrderAdapter adapter;
    private AlertDialog alertDialog;

    private Handler mHandler;
    private Intent intent;

    public static RiderOrderFragment getInstance() {
        return new RiderOrderFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rider_order;
    }

    @Override
    protected Boolean isBindEvent() {
        return true;
    }

    @Override
    protected void initView() {
        mHandler = new Handler();
        alertDialog = new AlertDialog(getContext()).builder();
        orderStatus = getArguments().getInt("orderStatus");
        riderId = getArguments().getString("riderId");
        params = new HashMap<>();
        loadingDialog.setLoadingDialogShow();
        params.put("riderOrderStatus",orderStatus+"");
        getData();

        swift.setOnRefreshListener(() -> {
            mHandler.postDelayed(() -> {
                getData();
            },1500);

        });
    }

    private void getData() {
        orderList = new ArrayList<>();
        RetrofitClient.getInstance(getContext())
                .Api().getRiderOrder(params)
                .enqueue(new Callback<RiderOrder>() {
                    @Override
                    public void onResponse(Call<RiderOrder> call, Response<RiderOrder> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        swift.setRefreshing(false);
                        if(response.isSuccessful() && response.body().getRecords().size() > 0) {
                            orderList.addAll(response.body().getRecords());
                            initAdapter();
                        }else {
                            ivEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RiderOrder> call, Throwable t) {
                        swift.setRefreshing(false);
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(getContext(),"获取数据失败");
                        ivEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initAdapter() {
        recyclerView.setVisibility(View.VISIBLE);
        ivEmpty.setVisibility(View.GONE);
        adapter = new RiderOrderAdapter(orderList,riderId);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnSeekBarChangeListener((order) -> {
            if(orderStatus == 1) {
                togoDetail(order);
            }else {
                robOrder(order,orderStatus + 1);
            }
        });

        adapter.setOnCancelClickListener((order) -> {
            alertDialog.setGone().setMsg("您是否取消该订单")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", view -> {
                        robOrder(order,0);
                    }).show();
        });

        adapter.setOnCallPhoneClickListener(phone -> {
            callPhone(phone);
        });
    }

    private void callPhone(String storePhone) {
        alertDialog.setTitle("您确定拨打：").setMsg(storePhone)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", view -> {
                    call(storePhone);
                }).show();
    }

    private void call(String storePhone) {
        intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + storePhone);
        intent.setData(data);
        startActivity(intent);
    }

    private void togoDetail(Order order) {
        intent = new Intent(mContext, RiderOrderDetailActivity.class);
        intent.putExtra("id",order.getId());
        intent.putExtra("riderId",riderId);
        intent.putExtra("riderOrderStatus",order.getRiderOrderStatus());
        startActivity(intent);
        adapter.seekBarProgress();
    }

    private void robOrder(Order order,int status) {
        loadingDialog.setLoadingDialogShow();
        Map<String,String> params = new HashMap<>();
        String uid = VsUserConfig.getDataString(getContext(),VsUserConfig.JKey_KcId);
        params.put("id",order.getId());
        params.put("uid",uid);
        params.put("orderMerchantNo",order.getOrderMerchantNo());
        params.put("riderId",riderId);
        params.put("riderOrderStatus",status + "");
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(getContext()).Api()
                .robOrder(body)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        if(response.isSuccessful() && response.body().getCode() == 0) {
                            ToastUtils.show(getContext(),response.body().getMsg());
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setMessage("isfresh");
                            EventBus.getDefault().post(messageEvent);
                        }else {
                            loadingDialog.setLoadingDialogDismiss();
                            ToastUtils.show(getContext(),response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(getContext(),t.getMessage());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        LogUtils.e("RiderOrderFragment event msg is :",event.getMessage());
        if(event.getMessage().equals("isfresh")) {
            getData();
        }
    }
}
