package com.weiwei.home.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.flyco.roundview.RoundLinearLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.account.RefuelBalanceActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.fragment.FragmentIndicator;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.adapter.HomePageBannerViewHolder;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.RefuelList;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.GpsUtils;
import com.weiwei.salemall.utils.RxLocationUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class RefuelMainActivity extends BaseActivity implements OnAddressCallback {

    @BindView(R.id.fl_back)
    FrameLayout mFlBack;
    @BindView(R.id.tv_oil_station_name)
    TextView mTvOilStationName;
    @BindView(R.id.tv_oil_station_address)
    TextView mTvOilStationAddress;
    @BindView(R.id.tv_oil_price)
    TextView mTvOilPrice;
    @BindView(R.id.tv_depreciate)
    TextView mTvDepreciate;
    @BindView(R.id.tv_international_price)
    TextView mTvInternationalPrice;
    @BindView(R.id.tv_navigation)
    TextView mTvNavigation;
    @BindView(R.id.rll_refuel)
    RoundLinearLayout mRllRefuel;
    @BindView(R.id.ll_more_refuel)
    LinearLayout mLlMoreRefuel;
    @BindView(R.id.ll_refuel_order)
    LinearLayout mLlRefuelOrder;
    @BindView(R.id.ll_refuel_balance)
    LinearLayout mLlRefuelBalance;
    @BindView(R.id.tv_error_name)
    TextView mTvErrorName;
    @BindView(R.id.ll_error)
    RoundLinearLayout mLlError;
    @BindView(R.id.cb_banner)
    ConvenientBanner mCbBanner;
    @BindView(R.id.iv_banner_default)
    ImageView mIvBannerDefault;

    private Map<String, String> mParams = new HashMap<>();
    private BDLBSMapHelper mBdlbsMapHelper;
    private CommonPopupWindow mapPop = null;
    private Double mLatitude = 0.0;
    private Double mLongitude = 0.0;
    private Double mGoLatitude = 0.0;
    private Double mGoLongitude = 0.0;
    private RefuelList mRefuelList;
    private boolean mIsLocationSuccess;
    private CustomProgressDialog loadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_main;
    }


    @Override
    protected void initView() {

//        FragmentIndicator fragmentIndicator=new FragmentIndicator(getApplicationContext());
//        fragmentIndicator.findViewById(R.id.rl_tab);

        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mFlBack).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();

        mTvInternationalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvInternationalPrice.getPaint().setAntiAlias(true);//抗锯齿

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        loadingDialog.setLoadingDialogShow();

        mBdlbsMapHelper = new BDLBSMapHelper();
        mBdlbsMapHelper.getAddressDetail(this, this);

        getBanner();

    }

    private void getBanner() {

        RetrofitClient.getInstance(this).Api()
                .getBanners()
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        BannerDataEntity banner = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);

                        if (banner.oil == null || banner.oil.size() == 0) {
                            mIvBannerDefault.setVisibility(View.VISIBLE);
                            return;
                        }

                        mIvBannerDefault.setVisibility(View.GONE);

                        mCbBanner.setPageIndicator(new int[]{R.drawable.xiang_huise, R.drawable.xing_bai});
                        mCbBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                        mCbBanner.setPages(new CBViewHolderCreator() {
                            @Override
                            public Object createHolder() {
                                return new HomePageBannerViewHolder();
                            }
                        }, banner.oil);
                        mCbBanner.startTurning(3000);

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        mIvBannerDefault.setVisibility(View.VISIBLE);
                    }

                });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            Intent intent=new Intent();
//            intent.setClass(RefuelMainActivity.this,VsMainActivity.class);
//            startActivity(intent);
            RefuelMainActivity.this.finish();
            return true;//不执行父类的点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类的其他点击事件
    }


    private void getRefuelData() {
//        mParams.put("searchType", "1001");
        mParams.put("searchType", "");
        mParams.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        mParams.put("pageNum", "1");
        mParams.put("pageSize", "1");

        String searchContent = "@92#@1001@1#2#3#4";
        String base64Str = Base64.encodeToString(searchContent.getBytes(), Base64.NO_WRAP);
        mParams.put("searchContent", "");

        RetrofitClient.getInstance(this).Api()
                .getRefuelList(mParams)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        ResultDataEntity dataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                        List<RefuelList> lists = JSONObject.parseArray(dataEntity.getRecords().toString(), RefuelList.class);

                        loadingDialog.setLoadingDialogDismiss();

                        if (lists == null || lists.size() == 0) {
                            return;
                        }
                        mLlError.setVisibility(View.GONE);
                        mRllRefuel.setVisibility(View.VISIBLE);
                        mRefuelList = lists.get(0);
                        mTvOilStationName.setText(mRefuelList.gasName);
                        mTvOilStationAddress.setText(mRefuelList.gasAddress);
                        mTvOilPrice.setText(mRefuelList.priceYfq + "");
                        mTvInternationalPrice.setText("国标价￥" + mRefuelList.priceOfficial);
                        mTvDepreciate.setText("已降￥" + ArmsUtils.formatting(mRefuelList.priceOfficial > mRefuelList.priceYfq ? (mRefuelList.priceOfficial - mRefuelList.priceYfq) : 0.00));
                        mTvNavigation.setText(mRefuelList.distance + "km");
                        mGoLatitude = mRefuelList.gasAddressLatitude;
                        mGoLongitude = mRefuelList.gasAddressLongitude;

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        loadingDialog.setLoadingDialogDismiss();
                        mTvErrorName.setText("附近暂无可用加油站");
                        mLlError.setVisibility(View.VISIBLE);
                        mRllRefuel.setVisibility(View.GONE);
                    }
                });

    }

    private void updateRefuelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        RetrofitClient.getInstance(mContext).Api()
                .updateRefuelOrder(params)
                .enqueue(new retrofit2.Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });

    }

    @Override
    public void onAddressStart() {

    }

    @Override
    public void onAddressFail() {
        loadingDialog.setLoadingDialogDismiss();
        mTvErrorName.setText("定位失败，请开启定位服务后重试");
        mRllRefuel.setVisibility(View.GONE);
        mLlError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        mIsLocationSuccess = true;
        mLatitude = bdLocation.getLatitude();
        mLongitude = bdLocation.getLongitude();
        mParams.put("userAddressLatitude", mLatitude + "");
        mParams.put("userAddressLongitude", mLongitude + "");
        getRefuelData();
    }

    @Override
    public void onAddressFinish() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRefuelOrder();
    }

    private void getSelectedMap() {
        int selectedMapFlag = SpUtils.getIntValue(this, "selectMapFlag", 2);
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
        GpsUtils bdGps = RxLocationUtils.GCJ02ToBD09(mGoLongitude, mGoLatitude);
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(bdGps.getLatitude()).append(",").append(bdGps.getLongitude()).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        startActivity(intent);
    }

    private void navWithAmap() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + mGoLatitude +
                "&dlon=" + mGoLongitude + "&dname=目的地&dev=0&t=2"));
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
                            Toast.makeText(RefuelMainActivity.this, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelMainActivity.this, "com.baidu.BaiduMap")) {
                                navWithBaidu();
                            } else {
                                Toast.makeText(RefuelMainActivity.this, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelMainActivity.this, "com.autonavi.minimap")) {
                                navWithAmap();
                            } else {
                                Toast.makeText(RefuelMainActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelMainActivity.this, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelMainActivity.this, "selectMapFlag", 1);
                        }

                        mapPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        mapPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    @OnClick({R.id.fl_back, R.id.rll_refuel, R.id.ll_more_refuel, R.id.ll_refuel_order, R.id.ll_refuel_balance,
            R.id.ll_refuel_money,R.id.tv_navigation, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
              RefuelMainActivity.this.finish();
               Intent intent=new Intent();
               intent.setClass(RefuelMainActivity.this,VsMainActivity.class);
               startActivity(intent);
                break;
            case R.id.rll_refuel:
                RefuelDetailActivity.start(this, mRefuelList);
                break;
            case R.id.ll_more_refuel:
                MoreRefuelActivity.start(this, mIsLocationSuccess, mLatitude, mLongitude);
                break;
            case R.id.ll_refuel_order:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2), mContext)) {
                    ArmsUtils.startActivity(mContext, RefuelOrderActivity.class);
                }
                break;
            case R.id.ll_refuel_balance:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2), mContext)) {
                    ArmsUtils.startActivity(mContext, RefuelBalanceActivity.class);
                }
                break;
            case R.id.ll_refuel_money:
                if(VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2),mContext)) {
                    ArmsUtils.startActivity(mContext,MakeMoneyActivity.class);
                }
                break;
            case R.id.tv_navigation:
                getSelectedMap();
                break;
            case R.id.ll_error:
                loadingDialog.setLoadingDialogShow();
                mBdlbsMapHelper.getAddressDetail(this, this);
                break;
        }
    }

}
