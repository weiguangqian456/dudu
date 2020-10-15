package com.weiwei.salemall.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.base.application.VsApplication;
import com.weiwei.salemall.adapter.CollectionV1Adapter;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.activity.SelectCityActivity.action;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/7/24
 *         统一管理本地商家
 */
public class LocalStoreFragment extends Fragment {
    @BindView(R.id.rv_localstore)
    RecyclerView localStoreRv;
    @BindView(R.id.fl_no_data)
    FrameLayout noDataFl;
    private View localStoreView;
    private static final String TAG = "LocalStoreFragment";

    private List<CollectionEntity> beanList = new ArrayList<>();
    private CollectionV1Adapter collectionV1Adapter = null;
    private LinearLayoutManager manager = null;

    /**
     * 栏目Id
     */
    private String columnId = null;
    private String latitude = null;
    private String longitude = null;
    private String cityCode = null;
    private String selectForResult = "200";

    /**
     * 栏目分类Id
     */
    private String classificationId = null;

    /**
     * 上拉加载
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private CustomProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (localStoreView != null) {
            ViewGroup parent = (ViewGroup) localStoreView.getParent();
            if (parent != null) {
                parent.removeView(localStoreView);
            }
            return localStoreView;
        }
        localStoreView = inflater.inflate(R.layout.fragment_local_store, container, false);
        ButterKnife.bind(this, localStoreView);
        initView();
        initBroadCast();
        initAdapter();
        showData();
        return localStoreView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(myBroadCastReceiver);
    }

    public LocalStoreFragment() {
    }

    public static LocalStoreFragment newInstance(Bundle args) {
        LocalStoreFragment fragment = new LocalStoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter(action);
        mContext.registerReceiver(myBroadCastReceiver, filter);
    }

    BroadcastReceiver myBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setAction(action);
            if (beanList != null) {
                beanList.clear();
                pageNum = 1;
            }
            showData();
        }
    };

    private void initView() {
        localStoreRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == collectionV1Adapter.getItemCount() && !isLoading) {
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
        collectionV1Adapter = new CollectionV1Adapter(getActivity(), beanList);
        localStoreRv.setAdapter(collectionV1Adapter);
        manager = new LinearLayoutManager(getActivity());
        localStoreRv.setLayoutManager(manager);
    }

    /**
     * 获取子栏目下的数据
     */
    private void showData() {
        if (!getActivity().isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        selectForResult = PreferencesUtils.getString(VsApplication.getContext(), "selectCityCode");
        cityCode = PreferencesUtils.getString(VsApplication.getContext(), "currentCity");
        longitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLon");
        latitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLat");

        Bundle bundle = getArguments();
        if (bundle != null) {
            columnId = bundle.getString("columnId");
            classificationId = bundle.getString("classificationFlag");
            Log.e(TAG, "栏目id===>" + columnId + "分类id===>" + classificationId + "当前城市id===>" + selectForResult);
        }

        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = null;
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", 10 + "");
        if (selectForResult != null && selectForResult.equals(cityCode)) {    //默认当前定位的位置
//            switch (classificationId) {
//                case "0":     //全部分类
//                    if (latitude == null) {
//                        latitude = "";
//                        longitude = "";
//                    }
//                    params.put("currentLon", longitude);
//                    params.put("currentLat", latitude);
//                    params.put("cityId", cityCode);
//                    call = api.getColumnData(columnId, params);
//                    break;
//                default:     //其他栏目
            params.put("currentLon", longitude);
            params.put("currentLat", latitude);
            params.put("cityId", cityCode);
//                    break;
//            }
        } else {
//            switch (classificationId) {                                      //选择城市（非当前位置）
//                case "0":   //全部分类
//                    params.put("currentLon", "");
//                    params.put("currentLat", "");
//                    params.put("cityId", selectForResult);
//                    call = api.getColumnData(columnId, params);
//                    break;
//                default:   //其他栏目
            params.put("currentLon", "");
            params.put("currentLat", "");
            params.put("cityId", selectForResult);
//                    break;
//            }
        }
        call = api.getColumnsData(classificationId, params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "选择城市的店铺msg===>" + result.getData().toString());
                    List<CollectionEntity> list = JSONObject.parseArray(result.getData().toString(), CollectionEntity.class);
                    beanList.addAll(list);
                }
                isLoading = false;
                collectionV1Adapter.notifyDataSetChanged();
                if (beanList.size() > 0) {
                    noDataFl.setVisibility(View.GONE);
                    localStoreRv.setVisibility(View.VISIBLE);
                } else {
                    noDataFl.setVisibility(View.VISIBLE);
                    localStoreRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }
}
