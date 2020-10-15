package com.weiwei.salemall.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.LocalGoodsAdapter;
import com.weiwei.salemall.bean.NormalColumnDataBean;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/7/24
 *         兑换专区Fragment
 */
public class ExchangeFragment extends Fragment {
    @BindView(R.id.rv_localgoods)
    RecyclerView localGoodsRv;
    @BindView(R.id.fl_no_content)
    RelativeLayout noDataRl;

    private View goodsView = null;
    private List<ProductBean> dataList = null;
    private LocalGoodsAdapter localGoodsAdapter = null;
    private GridLayoutManager manager = null;
    /**
     * 栏目Id
     */
    private String columnId = null;
    /**
     * 栏目分类Id
     */
    private String classificationId = null;

    private static final String TAG = "ExchangeFragment";
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private CustomProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (goodsView != null) {
            ViewGroup parent = (ViewGroup) goodsView.getParent();
            if (parent != null) {
                parent.removeView(goodsView);
            }
            return goodsView;
        }
        goodsView = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, goodsView);
        initView();
        initAdapter();
        showData();
        return goodsView;
    }

    public ExchangeFragment() {
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            columnId = bundle.getString("columnId");
            classificationId = bundle.getString("classificationFlag");
            Log.e(TAG, "栏目id===>" + columnId + "分类id===>" + classificationId);
        }

        localGoodsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == localGoodsAdapter.getItemCount() && !isLoading) {
                    pageNum++;
                    isLoading = true;
                    showData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        loadingDialog = new CustomProgressDialog(getActivity(), "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void initAdapter() {
        dataList = new ArrayList<>();
        localGoodsAdapter = new LocalGoodsAdapter(getActivity(), dataList, columnId, "y");
        localGoodsRv.setAdapter(localGoodsAdapter);
        manager = new GridLayoutManager(getActivity(), 2);
        localGoodsRv.setLayoutManager(manager);
    }

    public static Fragment newInstance(Bundle bundle) {
        ExchangeFragment fragment = new ExchangeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void showData() {
//        if (!getActivity().isFinishing()) {
//            loadingDialog.setLoadingDialogShow();
//        }
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        retrofit2.Call<ResultEntity> call = null;
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", 10 + "");
//        switch (classificationId) {
//            case "0":
//                //全部栏目下
//                call = api.getColumnData(columnId, params);
//                break;
//            default:
        //其他栏目
        call = api.getColumnsData(classificationId, params);
//                break;
//        }

        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {   //成功
                    Log.e(TAG, "兑换专区商品数据msg===>" + result.getData().toString());
                    List<ProductBean> list = JSONObject.parseArray(result.getData().toString(), ProductBean.class);
                    if (list != null && list.size() > 0) {    //有数据
                        dataList.addAll(list);
                        noDataRl.setVisibility(View.GONE);
                    }
                }
                isLoading = false;
                localGoodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }
}
