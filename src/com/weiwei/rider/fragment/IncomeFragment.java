package com.weiwei.rider.fragment;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.fragment.LazyBaseFragment;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.activity.RiderCenterActivity;
import com.weiwei.rider.adapter.RiderIncomeAdapter;
import com.weiwei.rider.bean.Income;
import com.weiwei.rider.bean.RiderIncome;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeFragment extends LazyBaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    private RiderIncomeAdapter adapter;
    private int page = 1;
    private int totalPage = 1;
    private List<Income> incomeList = new ArrayList<>();
    private Map<String,String> params;
    private String uid;
    private RiderCenterActivity activity;
    private Call<RiderIncome> riderIncome;

    @Override
    protected void initView(View view) {
        activity = (RiderCenterActivity) getActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_income;
    }

    @Override
    protected void loadData() {
        loadingDialog.setLoadingDialogShow();
        uid = VsUserConfig.getDataString(getContext(), VsUserConfig.JKey_KcId, "");
        params = new HashMap<>();
        params.put("appId","dudu");
        params.put("uid",uid);
        params.put("phone","");
        params.put("startTime","");
        params.put("endTime","");
        params.put("serialNumber","");
        params.put("productName","");
        params.put("page",page + "");
        if(activity.type == 2) {
            riderIncome = RetrofitClient.getInstance(getContext()).Api().getRiderIncome(params);
        }else {
            riderIncome = RetrofitClient.getInstance(getContext()).Api().getMerchantIncome(params);
        }
       riderIncome.enqueue(new Callback<RiderIncome>() {
                    @Override
                    public void onResponse(Call<RiderIncome> call, Response<RiderIncome> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        if(response.isSuccessful() && response.body().getTotal() > 0) {
                            totalPage = response.body().getTotal();
                            incomeList.addAll(response.body().getRecords());
                            initAdapter();
                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RiderIncome> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(getContext(),"获取数据失败");
                    }
                });
    }

    private void initAdapter() {
        adapter = new RiderIncomeAdapter(incomeList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(() -> {
            if(totalPage > page) {
                page++;
                mHandler.postDelayed(() -> loadMore(),1500);
                adapter.loadMoreComplete();
            }else {
                adapter.loadMoreEnd();
            }
        },recyclerView);

        adapter.disableLoadMoreIfNotFullPage();

    }

    private void loadMore() {
        params.put("page",page + "");

        riderIncome.enqueue(new Callback<RiderIncome>() {
                    @Override
                    public void onResponse(Call<RiderIncome> call, Response<RiderIncome> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            incomeList.addAll(response.body().getRecords());
                            adapter.notifyDataSetChanged();
                            adapter.loadMoreComplete();
                        }else {
                            adapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void onFailure(Call<RiderIncome> call, Throwable t) {
                        adapter.loadMoreFail();
                    }
                });
    }
}
