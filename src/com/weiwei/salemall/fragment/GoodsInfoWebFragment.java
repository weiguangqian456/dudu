package com.weiwei.salemall.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.ItemWebView;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ
 * 图文详情webview的Fragment
 */

public class GoodsInfoWebFragment extends Fragment {
    public ItemWebView detailWebView;
    private String productNo;
    private String columnId;
    private String seckillProductId; //秒杀商品
    private int descType;
    private String description;
    private String mSeckill;
    private static final String TAG = "GoodsInfoWebFragment";
    private GoodsDetailActivity activity;

    public static GoodsInfoWebFragment newInstance(String productNo, String seckill, String columnId, String seckillProductId) {
        GoodsInfoWebFragment fragment = new GoodsInfoWebFragment();
        Bundle args = new Bundle();
        args.putString("param", productNo);
        args.putString("seckill", seckill);
        args.putString("columnId", columnId);
        args.putString("seckillProductId", seckillProductId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (GoodsDetailActivity) getActivity();
        if (getArguments() != null) {
            productNo = getArguments().getString("param");
            if (StringUtils.isEmpty(productNo)) {
                productNo = "";
            }

            columnId = getArguments().getString("columnId");
            if (StringUtils.isEmpty(columnId)) {
                columnId = "";
            }

            seckillProductId = getArguments().getString("seckillProductId");
            mSeckill = getArguments().getString("seckill");
            if (StringUtils.isEmpty(seckillProductId)) {
                seckillProductId = "";
            }
            initData(productNo.equals("") ? seckillProductId : productNo);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWebView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_info_web, null);
        detailWebView = (ItemWebView) rootView.findViewById(R.id.wv_detail);
        return rootView;
    }

    private void initWebView() {
        detailWebView.setLayerType(View.LAYER_TYPE_NONE, null);//开启硬件加速
        WebSettings webSettings = detailWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 网页内容的宽度自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

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
       /*
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放*/

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        detailWebView.setWebChromeClient(new MyWebChromeClient());

    }

    /***
     * 获取商品详情的url片段
     * @param params
     */
    private void initData(String params) {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> param = new HashMap<>();
        param.put("columnId", columnId);
        retrofit2.Call<ResultEntity> getProductDetailCall = null;
        if (!StringUtils.isEmpty(productNo)) {
            getProductDetailCall = api.getProductDetail(params, param);
        }
        if (!StringUtils.isEmpty(seckillProductId)) {
//            getProductDetailCall = api.getSecKillProductDetail(seckillProductId);
            getProductDetailCall = api.getDuduSeckillDetail(seckillProductId);
        }
        if ("duduSeckill".equals(mSeckill)) {
            getProductDetailCall = api.getDuduSeckillDetail(seckillProductId);
        } else if (!StringUtils.isEmpty(seckillProductId)) {
            getProductDetailCall = api.getSecKillProductDetail(seckillProductId);
        }

        getProductDetailCall.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    ProductEntity productEntity = JSON.parseObject(result.getData().toString(), ProductEntity.class);
                    description = productEntity.getDescription();
                    descType = productEntity.getDescType();
                    if(description != null) {
                        switch (descType) {
                            case 0:
                                detailWebView.loadDataWithBaseURL(null, getNewContent(description), "text/html", "utf-8", null);
                                break;
                            case 1:
                                detailWebView.loadUrl(description);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }
    /**
     * 将html文本内容中包含img标签的图片，宽度变为屏幕宽度，高度根据宽度比例自适应
     **/
    public String getNewContent(String htmltext){
        String html = htmltext.replace("iframe","video");
        try {
            Document doc= Jsoup.parse(html);
            Elements elements=doc.getElementsByTag("video");
            for (Element element : elements) {
                String url = element.attr("src");
                element.attr("width","100%").attr("height","700px")
                        .attr("poster",url + "?vframe/jpg/offset/1")
                        .attr("controls","true")
                        .attr(" webkit-playsinline","false")
                        .attr("playsinline","false")
                        .attr("type","video/mp4");
            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }
    private class MyWebChromeClient extends WebChromeClient {
        WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            LogUtils.e("ToVmp","onShowCustomView");
            fullScreen();

            detailWebView.setVisibility(View.GONE);
            activity.mFrameLayout.setVisibility(View.VISIBLE);
            activity.mFrameLayout.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);

        }

        @Override
        public void onHideCustomView() {
            LogUtils.e("ToVmp","onHideCustomView");
            fullScreen();

            detailWebView.setVisibility(View.VISIBLE);
            activity.mFrameLayout.setVisibility(View.GONE);
            activity.mFrameLayout.removeAllViews();
            super.onHideCustomView();

        }
    }

    private String htmlData(String html) {

        String head = "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,user-scalable=0\">\n" +
                "  </head>";
        return "<html>" + head + html + "</html>";

    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LogUtils.e("ToVmp","横屏");
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LogUtils.e("ToVmp","竖屏");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser) {
            detailWebView.destroy();
        }
    }

    @Override
    public void onDestroy() {
        if(detailWebView != null)
        detailWebView.destroy();
        super.onDestroy();
    }
}
