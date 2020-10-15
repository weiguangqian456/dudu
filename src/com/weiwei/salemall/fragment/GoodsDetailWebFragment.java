package com.weiwei.salemall.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.ProductEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;

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
 * 商品详情下的web介绍
 */

public class GoodsDetailWebFragment extends Fragment {
    public WebView detailWv;
    private String productNo;   //普通商品
    private String seckillProductId; //秒杀商品
    private String mSeckill; //嘟嘟秒杀商品
    private String columnId;
    private String description;
    private int descType;
    private static final String TAG = "GoodsDetailWebFragment";
    private static final int DESCRIPTION_FLAG = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DESCRIPTION_FLAG:
                    switch (descType) {
                        case 0:
                            detailWv.loadDataWithBaseURL(null, getNewContent(description), "text/html", "utf-8", null);
                            break;
                        case 1:
                            detailWv.loadUrl(description);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private GoodsDetailActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail_web, null);
        activity = (GoodsDetailActivity) getActivity();
        detailWv = (WebView) rootView.findViewById(R.id.wv_detail);
        init();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            productNo = getArguments().getString("productNo");
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
            Log.e(TAG, "商品详情webView===>" + productNo + "columnId===>" + columnId);
            initData(productNo.equals("") ? seckillProductId : productNo);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        init();
    }

    public static GoodsDetailWebFragment newInstance(String productNo, String seckill, String columnId, String seckillProductId) {
        GoodsDetailWebFragment fragment = new GoodsDetailWebFragment();
        Bundle args = new Bundle();
        args.putString("productNo", productNo);
        args.putString("columnId", columnId);
        args.putString("seckillProductId", seckillProductId);
        args.putString("seckill", seckill);
        fragment.setArguments(args);
        return fragment;
    }

    private void init() {
        detailWv.setLayerType(View.LAYER_TYPE_NONE, null);//开启硬件加速
        WebSettings webSettings = detailWv.getSettings();
        // 网页内容的宽度自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
 //       webSettings.setJavaScriptEnabled(true);
        // User settings
//        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setDisplayZoomControls(false);
        // 设置支持javascript脚本
//        webSettings.setJavaScriptEnabled(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 设置显示缩放按钮
//        webSettings.setBuiltInZoomControls(true);
        // 支持缩放
//        webSettings.setSupportZoom(true);
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

        detailWv.setWebChromeClient(new MyWebChromeClient());

    }

    /**
     * 获取商品数据
     */
    private void initData(String params) {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> param = new HashMap<>();
        param.put("columnId", columnId);
        retrofit2.Call<ResultEntity> getProductDetailCall = null;
        if (!StringUtils.isEmpty(productNo)) {
            getProductDetailCall = api.getProductDetail(params, param);
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
                    Log.e(TAG, "商品详情msg===>" + result.getData().toString());
                    ProductEntity productEntity = JSON.parseObject(result.getData().toString(), ProductEntity.class);
                    description = productEntity.getDescription();
                    descType = productEntity.getDescType();
                    if(description != null) {
                        Message message = new Message();
                        message.what = DESCRIPTION_FLAG;
                        handler.sendMessage(message);
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
//                LogUtils.e("视频地址：",url);
                element.attr("width","100%").attr("height","700px")
                .attr("poster",url + "?vframe/jpg/offset/1")
                        .attr("controls","controls")
                        .attr("x5-video-player-type","h5")
                        .attr("playsinline","true")
                        .attr("x5-video-orientation","landscape")
                        .attr("x5-video-player-fullscreen","true")
                .attr("type","video/mp4");
            }
//            LogUtils.e("拼接完的地址：",doc.toString());
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }

    private String HTMLData( String html) {
        String head = " <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,user-scalable=0\">\n" +
                "  </head>";
        return "<html>" + head + "<body>" + html + "</body></html>";
    }
    private class MyWebChromeClient extends WebChromeClient{
        IX5WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            LogUtils.e("ToVmp","onShowCustomView");
            fullScreen();
            detailWv.setVisibility(View.GONE);
            activity.mFrameLayout.setVisibility(View.VISIBLE);
            activity.mFrameLayout.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);

        }

        @Override
        public void onHideCustomView() {
            LogUtils.e("ToVmp","onHideCustomView");
            fullScreen();
            detailWv.setVisibility(View.VISIBLE);
            activity.mFrameLayout.setVisibility(View.GONE);
            activity.mFrameLayout.removeAllViews();
            super.onHideCustomView();

        }
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

  /*  @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                LogUtils.e("ToVmp","ORIENTATION_LANDSCAPE");
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                LogUtils.e("ToVmp","ORIENTATION_PORTRAIT");
                break;
        }
    }*/

    @Override
    public void onDestroy() {
        if (detailWv != null) {
            detailWv.destroy();
        }
        super.onDestroy();
    }
}
