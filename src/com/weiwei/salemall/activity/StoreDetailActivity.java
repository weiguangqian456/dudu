package com.weiwei.salemall.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.GpsUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.RxLocationUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/7/25
 *         商家详情
 */
public class StoreDetailActivity extends VsBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.iv_collect)
    CheckBox collectCb;
    @BindView(R.id.tv_name)
    TextView nameTv;
    @BindView(R.id.iv_image)
    ImageView imageIv;
    @BindView(R.id.tv_perprice)
    TextView perPriceTv;
    @BindView(R.id.tv_address)
    TextView addressTv;
    @BindView(R.id.tv_contact)
    TextView contactTv;
    @BindView(R.id.tv_discount)
    TextView discountTv;
    @BindView(R.id.iv_nevigation)
    ImageView nevigationIv;
    @BindView(R.id.iv_phone)
    ImageView phoneIv;
    @BindView(R.id.wv_service_content)
    WebView serviceWv;
    @BindView(R.id.rl_address)
    RelativeLayout addressRl;
    @BindView(R.id.rl_contact)
    RelativeLayout contactRl;

    /**
     * 店铺名
     */
    private String storeName = null;
    /**
     * 人均消费
     */
    private String perPrice = null;
    /**
     * 地址
     */
    private String storeAddress = null;
    /**
     * 店铺联系电话
     */
    private String storePhone = null;
    /**
     * 店铺距当前位置距离
     */
    private String distance = null;
    /**
     * 店铺促销活动
     */
    private String storeDiscount = null;

    private CommonPopupWindow mapPop = null;
    private static final String TAG = "StoreDetailActivity";

    private String latitude = null;
    private String longitude = null;
    private String targetNo = null;
    private String addCollectionMsg = null;
    private String cancleCollectionMsg = null;

    private String serviceContent = null;
    private CollectionEntity bean = null;
    private static final int MSG_FRESHUI_STOREDETAIL = 105;
    private static final int MSG_FRESHUI_ADDCOLLECTION = 101;
    private static final int MSG_FRESHUI_CANCELCOLLECTION = 102;
    private String storeNo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        switch (msg.what) {
            case MSG_FRESHUI_STOREDETAIL:
                updateUi();
                break;
            case MSG_FRESHUI_ADDCOLLECTION:
                showToast("收藏成功");
                break;
            case MSG_FRESHUI_CANCELCOLLECTION:
                showToast("取消成功");
                break;
            default:
                break;
        }
    }

    private void initView() {
        storeNo = getIntent().getStringExtra("storeNo");
        backIv.setOnClickListener(this);
        collectCb.setOnCheckedChangeListener(this);
        addressRl.setOnClickListener(this);
        contactRl.setOnClickListener(this);
    }

    private void initData() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getStoreDetail(storeNo);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "店铺详情msg===>" + result.getData().toString());
                    bean = JSON.parseObject(result.getData().toString(), CollectionEntity.class);
                }
                Message message = new Message();
                message.what = MSG_FRESHUI_STOREDETAIL;
                mBaseHandler.sendMessage(message);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void updateUi() {
        if (bean != null) {
            serviceContent = bean.getDescription();
            initWebView();
            storeName = bean.getStoreName();
            if (!StringUtils.isEmpty(storeName)) {
                nameTv.setText(storeName);
            }
            perPrice = bean.getAverageConsumption();
            if (!StringUtils.isEmpty(perPrice)) {
                perPriceTv.setText("人均消费：¥" + perPrice);
            }

            storeAddress = bean.getStoreAddress();
            distance = bean.getDistance();
            if (!StringUtils.isEmpty(storeAddress)) {
                addressTv.setText(storeAddress);
            }

            storePhone = bean.getStorePhone();
            if (!StringUtils.isEmpty(storePhone)) {
                contactTv.setText(storePhone);
            }

            storeDiscount = bean.getPropaganda();
            if (!StringUtils.isEmpty(storeDiscount)) {
                discountTv.setText(storeDiscount);
            }

            latitude = bean.getLatitude();
            longitude = bean.getLongitude();
            targetNo = bean.getStoreNo();

            String isFavorite = bean.getIsFavorite();
            updateCollectionStatus(isFavorite);

            String image = JudgeImageUrlUtils.isAvailable(bean.getIconUrl());
            if (!StringUtils.isEmpty(image)) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.mall_logits_default);
                Glide.with(this).load(image).apply(options).into(imageIv);
            }
        }
    }

    /**
     * 匹配店铺当前的收藏状态
     *
     * @param isFavorite
     */
    private void updateCollectionStatus(String isFavorite) {
        if ("n".equals(isFavorite)) {
            collectCb.setChecked(false);
        } else if ("y".equals(isFavorite)) {
            collectCb.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_address:
                getSelectedMap();
                break;
            case R.id.rl_contact:
                if (!StringUtils.isEmpty(storePhone)) {
                    new AlertDialog.Builder(this).
                            setMessage("您确定拨打：" + storePhone).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + storePhone));
                            startActivity(intentPhone);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
                break;
            default:
                break;
        }
    }

    private void getSelectedMap() {
        int selectedMapFlag = SpUtils.getIntValue(StoreDetailActivity.this, "selectMapFlag", 2);
        switch (selectedMapFlag) {
            case 0:   //高德
                navWithAmap();
                break;
            case 1:  //百度
                navWithBaidu();
                break;
            default:
                showMapPop();
                break;
        }
    }

    private void navWithBaidu() {
        GpsUtils bdGps = RxLocationUtils.GCJ02ToBD09(Double.parseDouble(longitude), Double.parseDouble(latitude));
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(bdGps.getLatitude()).append(",").append(bdGps.getLongitude()).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        startActivity(intent);
    }

    private void navWithAmap() {
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latitude +
                "&dlon=" + longitude + "&dname=目的地&dev=0&t=2"));
        startActivity(intent);
    }

    private void showMapPop() {
        mapPop = new CommonPopupWindow.Builder(this).setView(R.layout.pop_map).setWidthAndHeight((int) getResources().getDimension(R.dimen.w_277_dip), ViewGroup.LayoutParams
                .WRAP_CONTENT).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
            @Override
            public void getChildView(View view, int layoutResId) {
                final CheckBox amapCb = (CheckBox) view.findViewById(R.id.cb_amap);
                final CheckBox baidumapCb = (CheckBox) view.findViewById(R.id.cb_baidumap);
                final CheckBox rememberCb = (CheckBox) view.findViewById(R.id.cb_remember);
                TextView ensureBtn = (TextView) view.findViewById(R.id.btn_ensure);

                amapCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            baidumapCb.setChecked(false);
                        }
                    }
                });
                baidumapCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            amapCb.setChecked(false);
                        }
                    }
                });

                ensureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if (!baidumapCb.isChecked() && !amapCb.isChecked()) {
                            Toast.makeText(StoreDetailActivity.this, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (mapisAvailable(StoreDetailActivity.this, "com.baidu.BaiduMap")) {
                                navWithBaidu();
                            } else {
                                Toast.makeText(StoreDetailActivity.this, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (mapisAvailable(StoreDetailActivity.this, "com.autonavi.minimap")) {
                                navWithAmap();
                            } else {
                                Toast.makeText(StoreDetailActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(StoreDetailActivity.this, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(StoreDetailActivity.this, "selectMapFlag", 1);
                        }

                        mapPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        mapPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public static boolean mapisAvailable(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isPressed()) {
            if (isChecked) {
                addCollection();
            } else {
                cancelCollection();
            }
        }
    }

    private void showToast(String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 添加收藏
     */
    private void addCollection() {
        if (!checkIsLogin()) {
            Toast.makeText(StoreDetailActivity.this, "您当前处于未登录状态，暂不能收藏", Toast.LENGTH_SHORT).show();
            skipPageAndFinish(VsLoginActivity.class);
        } else {
            ApiService api = RetrofitClient.getInstance(this).Api();
            Map<String, String> params = new HashMap<>();
            params.put("favoriteType", String.valueOf(1));
            params.put("targetNo", targetNo);
            retrofit2.Call<ResultEntity> call = api.addFavorite(params);
            call.enqueue(new Callback<ResultEntity>() {
                @Override
                public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                    if (response.body() == null) {
                        return;
                    }
                    ResultEntity result = response.body();
                    if (REQUEST_CODE == result.getCode()) {
                        Log.e(TAG, "添加收藏msg===>" + result.getMsg());
                        Message message = new Message();
                        message.what = MSG_FRESHUI_ADDCOLLECTION;
                        mBaseHandler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                }
            });
        }
    }

    private boolean checkIsLogin() {
        boolean isLogin = false;
        String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        if (uid != null && !uid.isEmpty()) {
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 取消收藏
     */
    private void cancelCollection() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("favoriteType", String.valueOf(1));
        params.put("targetNo", targetNo);
        retrofit2.Call<ResultEntity> call = api.cancelDetailFavorite(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "取消收藏msg===>" + result.getMsg());
                    Message message = new Message();
                    message.what = MSG_FRESHUI_CANCELCOLLECTION;
                    mBaseHandler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        serviceWv.setLayerType(View.LAYER_TYPE_NONE, null);//开启硬件加速
        WebSettings webSettings = serviceWv.getSettings();
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
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
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

        //2参数为地址
        Log.e(TAG, "需要加载的地址===>" + serviceContent);
        serviceWv.loadDataWithBaseURL(null, serviceContent, "text/html", "utf-8", null);
    }
}
