package com.weiwei.salemall.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.MyOrderAdapter;
import com.weiwei.salemall.bean.OrderEntity;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/08/09
 */
public class OrderFragment extends Fragment {
    private View orderView;
    @BindView(R.id.ll_no_order_data)
    LinearLayout noOrderDataLl;
    //    @BindView(R.id.swipelayout)
//    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.allorderrv)
    RecyclerView orderRv;
    @BindView(R.id.btn_back_mall)
    TextView backMallBtn;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 上拉加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private String[] titles = new String[]{"全部", "待付款", "待发货", "待收货", "待评论"};
    private String orderStatus;
    private static final String TAG = "OrderFragment";

    private List<OrderEntity> dataList = new ArrayList<>();
    private MyOrderAdapter orderAdapter = null;

    private boolean mIsRefreshing = false;

    /**
     * 主界面
     */
    private VsMainActivity vsMain;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.vsMain = (VsMainActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (orderView != null) {
            ViewGroup parent = (ViewGroup) orderView.getParent();
            if (parent != null) {
                parent.removeView(orderView);
            }
            return orderView;
        }
        orderView = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this, orderView);
        initView();
        initAdapter();
        return orderView;
    }

    private int mDateSize = 0;
    private int mPageSize = 10;

    @Override
    public void onResume() {
        super.onResume();
        if (dataList != null) {
            mDateSize = dataList.size() / mPageSize + (dataList.size() % mPageSize >= 0 ? 1 : 0);
//            dataList.clear();
        }
        if (orderAdapter != null) {
            pageNum = 1;
            isLoading = false;
            showData(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public OrderFragment() {
    }

    public static Fragment newInstance(Bundle bundle) {
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initView() {
        backMallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    MessageEvent bean = new MessageEvent();
                bean.setMessage("salemall_fragment");
                EventBus.getDefault().post(bean);
                getActivity().finish();*/
                vsMain.showFragment(0);
                vsMain.setView(0, false);
                vsMain.setFragmentIndicator(0);
            }
        });

        orderRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        orderRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (orderAdapter.getItemCount() < 10) {
                    Log.e(TAG, "adapter总数量msg===>" + orderAdapter.getItemCount());
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == orderAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "到底了");
                    pageNum++;
                    isLoading = true;
                    showData(false);
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
        orderRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                if (dataList != null) dataList.clear();
                if (orderAdapter != null) {
                    pageNum = 1;
                    isLoading = false;
                    showData(false);
                }
                refreshlayout.finishRefresh(1000, true);//传入false表示刷新失败
            }
        });
    }

    private void initAdapter() {
        orderAdapter = new MyOrderAdapter(getActivity(), dataList);
        orderRv.setAdapter(orderAdapter);
        orderRv.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ((DefaultItemAnimator) orderRv.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 获取订单数据
     */
    private void showData(final boolean isClearData) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String classificationId = bundle.getString("classificationFlag");
            if (classificationId.equals(titles[0])) { //全部
                orderStatus = "";
            } else if (classificationId.equals(titles[1])) { //待付款
                orderStatus = "0";
            } else if (classificationId.equals(titles[2])) { //待发货
                orderStatus = "1";
            } else if (classificationId.equals(titles[3])) { //待收货
                orderStatus = "2";
            } else if (classificationId.equals(titles[4])) { //待评论
                orderStatus = "3";
            }
        }

        mIsRefreshing = true;
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> params = new HashMap<>();
        Log.e(TAG, "当前请求页数msg===>" + pageNum);
        params.put("pageNum", pageNum + "");
        if(mDateSize > 1){
            int i = mDateSize  * mPageSize;
            params.put("pageSize", i + "");
            pageNum = mDateSize;
            mDateSize = 0;
        }else{
            params.put("pageSize", 10 + "");
        }
        params.put("orderStatus", orderStatus);
        retrofit2.Call<ResultEntity> call = api.getOrderPage(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "订单status===>" + orderStatus + "\n订单msg===>" + result.getData().toString());
                    ResultDataEntity myOrderDataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);

                    List<OrderEntity> list = JSON.parseArray(myOrderDataEntity.getRecords().toString(), OrderEntity.class);
                    if(isClearData){
                        dataList.clear();
                    }
                    dataList.addAll(list);
                    isLoading = false;
                    mIsRefreshing = false;
                    orderAdapter.notifyDataSetChanged();
                    if (dataList != null && !dataList.isEmpty()) {
                        noOrderDataLl.setVisibility(View.GONE);
                        orderRv.setVisibility(View.VISIBLE);
                    } else {
                        noOrderDataLl.setVisibility(View.VISIBLE);
                        orderRv.setVisibility(View.GONE);

                    }
                    LogUtils.e("dataList:",dataList.toString());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }
}
