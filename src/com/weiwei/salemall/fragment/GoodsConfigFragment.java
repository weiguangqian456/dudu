package com.weiwei.salemall.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShopConfigRecordsEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.CustomLinearLayoutManager;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ
 *         规格参数Fragment
 */
public class GoodsConfigFragment extends Fragment {
    private View view;
    public GoodsDetailActivity activity;
    private RecyclerView productConfigRv;
    private WebView dealWebView;

    private String productNo;
    private String columnId;
    private static final String TAG = "GoodsConfigFragment";
    private List<ShopConfigRecordsEntity> data = new ArrayList<>();
    private String productNote;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (GoodsDetailActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_config, null);
        productConfigRv = (RecyclerView) view.findViewById(R.id.rv_goods_config);
        dealWebView = (WebView) view.findViewById(R.id.webview_buy_deal);
        initWebView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            productNo = getArguments().getString("productNo");
            columnId = getArguments().getString("columnId");
            if (StringUtils.isEmpty(productNo)) {
                productNo = "";
            }
            if (StringUtils.isEmpty(columnId)) {
                columnId = "";
            }
            Log.e(TAG, "商品规格参数界面msg===>" + productNo + "columnId===>" + columnId);
        }
        initData();
    }

    public static GoodsConfigFragment newInstance(String productNo, String columnId, String seckillProductId) {
        GoodsConfigFragment fragment = new GoodsConfigFragment();
        Bundle args = new Bundle();
        args.putString("productNo", productNo);
        args.putString("columnId", columnId);
        args.putString("seckillProductId", seckillProductId);
        fragment.setArguments(args);
        return fragment;
    }

    private void initData() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> param = new HashMap<>();
        param.put("columnId", columnId);
        retrofit2.Call<ResultEntity> getProductDetailCall = api.getProductDetail(productNo, param);
        retrofit2.Call<ResultEntity> getProductDescCall = api.getProductDesc(productNo, param);
        getProductDetailCall.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    ProductEntity productEntity = JSON.parseObject(result.getData().toString(), ProductEntity.class);
                    productNote = productEntity.getPurchaseNote();
                    Log.e(TAG, "规格参数中注意事项msg===>" + productNote);
                    if (!TextUtils.isEmpty(productNote)) {
                        dealWebView.setVisibility(View.VISIBLE);
                        dealWebView.loadDataWithBaseURL(null, productNote, "text/html", "utf-8", null);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });

        getProductDescCall.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "规格参数Fragment中商品规格msg===>" + result.getData().toString());
                    List<ShopConfigRecordsEntity> shopConfigRecordsEntityList = JSON.parseArray(result.getData().toString(), ShopConfigRecordsEntity.class);
                    data.addAll(shopConfigRecordsEntityList);
                    CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(mContext);
                    linearLayoutManager.setScrollEnabled(false);
                    productConfigRv.setLayoutManager(linearLayoutManager);
                    productConfigRv.setAdapter(new CommonAdapter<ShopConfigRecordsEntity>(getActivity(), R.layout.rv_goods_config_item, data) {
                        @Override
                        protected void convert(ViewHolder holder, ShopConfigRecordsEntity dataBean, int position) {
                            holder.setText(R.id.shop_name, data.get(position).getSpecName().trim());
                            holder.setText(R.id.shop_value, data.get(position).getSpecValue().trim());
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void initWebView() {
        dealWebView.setLayerType(View.LAYER_TYPE_NONE, null);//开启硬件加速
        WebSettings webSettings = dealWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // User settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //关键点
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        // 设置支持javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 设置显示缩放按钮
        webSettings.setBuiltInZoomControls(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setTextSize(WebSettings.TextSize.LARGER);
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240 || mDensity == 480) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }

        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
    }
}
