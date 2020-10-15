package com.weiwei.weibo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by czb365 on 2018/8/16.
 */

public class WebPageNavigationActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.x5Webview)
    X5WebView mX5Webview;

    public static void start(Context context, String oilStationId, String oilStationName, String oilName, String gunNo) {
        Bundle bundle = new Bundle();
        bundle.putString("OIL_STATION_ID", oilStationId);
        bundle.putString("OIL_STATION_NAME", oilStationName);
        bundle.putString("OIL_NAME", oilName);
        bundle.putString("GUN_NO", gunNo);
        ArmsUtils.startActivity(context, WebPageNavigationActivity.class, bundle);
    }

    private String mOilStationId;
    private String mOilStationName;
    private String mOilNnme;
    private String mGunNo;
    private String mPhone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("加油优惠");
        setWebViewListener();
    }

    private void setWebViewListener() {

        mOilStationId = getIntent().getStringExtra("OIL_STATION_ID");
        mOilStationName = getIntent().getStringExtra("OIL_STATION_NAME");
        mOilNnme = getIntent().getStringExtra("OIL_NAME");
        mGunNo = getIntent().getStringExtra("GUN_NO");
        mPhone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);

        //加载的url按规定的使用即可
        mX5Webview.loadUrl("https://open.czb365.com/redirection/todo/?platformType=92655483&platformCode=" + mPhone + "&gasId=" + mOilStationId + "&gunNo=" + mGunNo + "");
        final WebPageNavigationJsObject webPageNavigationJsObject = new WebPageNavigationJsObject(this);
        mX5Webview.addJavascriptInterface(webPageNavigationJsObject, "czb");//第二个参数czb不可更改，
        mX5Webview.setWebChromeClient(new WebChromeClient() {
            @Override//进度条
            public void onProgressChanged(WebView webView, int progress) {
                super.onProgressChanged(webView, progress);
                if (progress == 100) {
                    mProgress.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mProgress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mProgress.setProgress(progress);//设置进度值
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                Log.e("标题", s);
            }
        });
//        androidamap://route?sourceApplication


        mX5Webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Log.e("url=", url);
                if (url.startsWith("weixin://") || url.contains("alipays://platformapi")) {//如果微信或者支付宝，跳转到相应的app界面,
                    mX5Webview.goBack();
                    recordUserInfo();
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(WebPageNavigationActivity.this, "未安装相应的客户端", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }


                if (url.startsWith("http://m.amap.com")) {// http://m.amap.com
                    webView.loadUrl(url);
                    return true;
                }

                if (url.startsWith("androidamap://route")) {//安装高德则打开导航
                    return true;
                }

                if (url.startsWith("http://ditu.amap.com") || url.startsWith("https://ditu.amap.com")) {
                    return true;
                }

                /**
                 *
                 * 设置 Header 头方法
                 * window.czb.extraHeaders(String key, String value)
                 */
                if (webPageNavigationJsObject != null && webPageNavigationJsObject.getKey() != null) {
                    Map extraHeaders = new HashMap();
                    extraHeaders.put(webPageNavigationJsObject.getKey(), webPageNavigationJsObject.getValue());
                    webView.loadUrl(url, extraHeaders);
                } else {
                    webView.loadUrl(url);
                }
                return true;

            }
        });
    }

    private void recordUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("duduPhone", mPhone);
        params.put("gasName", mOilStationName);
        params.put("oilNo", mOilNnme);
        params.put("gunNo", mGunNo);

        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(this).Api()
                .recordUserInfo(body)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });

    }

    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //返回上一级
    public void goBack() {
        if (mX5Webview.canGoBack()) {
            mX5Webview.goBack();
            if (mX5Webview.getUrl().startsWith("http://m.amap.com") || mX5Webview.getUrl().startsWith("http://ditu.amap.com/") ||
                    mX5Webview.getUrl().startsWith("https://m.amap.com") || mX5Webview.getUrl().startsWith("https://ditu.amap.com/")) {
                mX5Webview.goBack();
            }
        } else {
            finish();
        }
    }

    @OnClick({R.id.fl_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
