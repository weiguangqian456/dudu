package com.weiwei.merchant.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.base.fragment.LazyBaseFragment;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.merchant.activity.MerchantOrderActivity;
import com.weiwei.merchant.adapter.MerchantOrderAdapter;
import com.weiwei.merchant.bean.MerchantOrder;
import com.weiwei.merchant.bean.Order;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantOrderFragment extends LazyBaseFragment {

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    private String orderStatus = "";
    private Map<String,Object> params;
    private MerchantOrderActivity activity;
    private MerchantOrderAdapter adapter;
    private List<Order> orderList;

    public static MerchantOrderFragment getInstance() {
        return new MerchantOrderFragment();
    }

    @Override
    protected void initView(View view) {
        orderStatus = getArguments().getString("type");
        activity = (MerchantOrderActivity) getActivity();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_merchant_order;
    }

    @Override
    protected void loadData() {

        refreshLayout.setOnRefreshListener(() -> {
            mHandler.postDelayed(() -> {
                refreshLayout.setRefreshing(false);
            },1500);
        });

        loadingDialog.setLoadingDialogShow();

        params = new HashMap<>();
        params.put("orderStatus",orderStatus);
        params.put("pageNum",1);
        params.put("pageSize",10);
        params.put("usernames",activity.nameList.toArray());

        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(getContext()).Api()
                .merchantOrder(body)
                .enqueue(new Callback<MerchantOrder>() {
                    @Override
                    public void onResponse(Call<MerchantOrder> call, Response<MerchantOrder> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        if(response.isSuccessful() && response.body()!= null && response.body().getRecords().size() > 0) {
                            orderList = response.body().getRecords();
                            initAdapter();
                        }else {
                            recyclerView.setVisibility(View.GONE);
                            ivEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<MerchantOrder> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                    }
                });

    }

    private void initAdapter() {
        adapter = new MerchantOrderAdapter(orderList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

    }

}
