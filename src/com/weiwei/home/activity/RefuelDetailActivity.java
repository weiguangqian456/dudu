package com.weiwei.home.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.account.RefuelBalanceActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.adapter.GunNoAdapter;
import com.weiwei.home.adapter.OilNoAdapter;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.bean.RefuelDetail;
import com.weiwei.salemall.bean.RefuelList;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.GpsUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.RxLocationUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.weibo.WebPageNavigationActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefuelDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.tv_oil_station_name)
    TextView mTvOilStationName;
    @BindView(R.id.tv_oil_station_address)
    TextView mTvOilStationAddress;
    @BindView(R.id.tv_oil_price)
    TextView mTvOilPrice;
    @BindView(R.id.tv_international_price)
    RoundTextView mTvInternationalPrice;
    @BindView(R.id.tv_depreciate)
    RoundTextView mTvDepreciate;
    @BindView(R.id.tv_navigation)
    TextView mTvNavigation;
    @BindView(R.id.fl_back)
    FrameLayout mFlBack;
    @BindView(R.id.tablayout)
    XTabLayout mTablayout;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.iv_oil_station_image)
    ImageView mIvOilStationImage;
    @BindView(R.id.oil_no_recyclerView)
    RecyclerView mOilNoRecyclerView;
    @BindView(R.id.gun_no_recyclerView)
    RecyclerView mGunNoRecyclerView;
    @BindView(R.id.tv_prompt)
    TextView mTvPrompt;

    public static void start(Context context, RefuelList refuelList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("REFUEL_LIST", refuelList);
        ArmsUtils.startActivity(context, RefuelDetailActivity.class, bundle);
    }

    public static RefuelCouponMoneyActivity getInstance = new RefuelCouponMoneyActivity();

    private int mAlphaHeight = 300;
    private RefuelList mRefuelList;
    private OilNoAdapter mOilNoAdapter;
    private GunNoAdapter mGunNoAdapter;
    private String mOilStationId;
    private String mSource;
    private String mOilStationName;
    private String mOilName;
    private String mGunNo;
    private String mOilNo;
    private CommonPopupWindow mapPop = null;
    private Double mGoLatitude;
    private Double mGoLongitude;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_detail;
    }


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();

        mTablayout.addTab(mTablayout.newTab().setText("我要加油"));
        mTvPrompt.setText(Html.fromHtml(getResources().getString(R.string.refuel_detail_prompt)));

        mOilNoAdapter = new OilNoAdapter(this);
        mOilNoRecyclerView.setAdapter(mOilNoAdapter);
        mOilNoAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                RefuelDetail.OilPriceList oilPriceList = mOilNoAdapter.getList().get(position);
                RefuelDetail.OilPriceList.GunNos gunNos = oilPriceList.gunNos.get(0);

                mGunNoAdapter.cancelCheck();
                gunNos.check = true;

                mTvOilPrice.setText(oilPriceList.priceYfq + "");
                mTvInternationalPrice.setText("比国标价降" + ArmsUtils.formatting(oilPriceList.priceOfficial > oilPriceList.priceYfq ? (oilPriceList.priceOfficial - oilPriceList.priceYfq) : 0.00) + "元");
                mTvDepreciate.setText("比本站降" + ArmsUtils.formatting(oilPriceList.priceGun > oilPriceList.priceYfq ? (oilPriceList.priceGun - oilPriceList.priceYfq) : 0.00) + "元");

                mOilName = oilPriceList.oilName;
                mGunNo = gunNos.gunNo;
                mOilNo = oilPriceList.oilNo;
                mGunNoAdapter.setList(oilPriceList.gunNos);
                mGunNoAdapter.notifyDataSetChanged();
            }
        });

        mGunNoAdapter = new GunNoAdapter(this);
        mGunNoRecyclerView.setAdapter(mGunNoAdapter);
        mGunNoAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                RefuelDetail.OilPriceList.GunNos gunNos = mGunNoAdapter.getList().get(position);
                mGunNo = gunNos.gunNo;
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float absVerticalOffset = Math.abs(scrollY);

                float alpha = (absVerticalOffset > 150 ? 0.5f : absVerticalOffset / mAlphaHeight);
                mTvTitleBackground.setAlpha(alpha);
            }
        });

        mRefuelList = (RefuelList) getIntent().getSerializableExtra("REFUEL_LIST");
        if (mRefuelList != null) {
            mOilStationId = mRefuelList.gasId;
            mOilStationName = mRefuelList.gasName;
            mGoLatitude = mRefuelList.gasAddressLatitude;
            mGoLongitude = mRefuelList.gasAddressLongitude;

            Glide.with(mContext).load(JudgeImageUrlUtils.isAvailable(mRefuelList.gasLogoBig)).into(mIvOilStationImage);
            mTvOilStationName.setText(mRefuelList.gasName);
            mTvOilStationAddress.setText(mRefuelList.gasAddress);
            mTvNavigation.setText(mRefuelList.distance + "km");
        }

        getRefuelDetail();

    }



    private void isRefuelbalance() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        RetrofitClient.getInstance(this).Api()
                .isRefuelbalance(params)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        ResultEntity result = response.body();
                        if (!TextUtils.isEmpty(result.getMsg())) {
                            ArmsUtils.makeText(RefuelDetailActivity.this, result.getMsg());
                        }
                        if (result.getCode() == 0) {
                            if (null != mSource && mSource.equals("1001"))
                                RefuelCouponMoneyActivity.start(RefuelDetailActivity.this, mRefuelList.gasId, mOilNo, mGunNo);
                            else
                                WebPageNavigationActivity.start(RefuelDetailActivity.this, mOilStationId, mOilStationName, mOilName, mGunNo);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });

    }

    private void updateRefuelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        RetrofitClient.getInstance(this).Api()
                .updateRefuelOrder(params)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });

    }

    private void getRefuelDetail() {

        if (mRefuelList == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("gasId", mRefuelList.gasId);
        params.put("phone", "13822438649");

        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(this).Api()
                .getRefuelDetail(body)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        RefuelDetail data = JSON.parseObject(result.getData().toString(), RefuelDetail.class);

                        mOilStationId = data.gasId;
                        mOilStationName = data.gasName;
                        mSource = data.source;
                        mOilNo = data.oilPriceList.get(0).oilNo;

                        RefuelDetail.OilPriceList oilPriceZero = data.oilPriceList.get(0);
                        RefuelDetail.OilPriceList.GunNos gunNosZero = oilPriceZero.gunNos.get(0);

                        mTvOilPrice.setText(oilPriceZero.priceYfq + "");
                        mTvInternationalPrice.setText("比国标价降" + ArmsUtils.formatting(oilPriceZero.priceOfficial > oilPriceZero.priceYfq ? (oilPriceZero.priceOfficial - oilPriceZero.priceYfq) : 0.00) + "元");
                        mTvDepreciate.setText("比本站降" + ArmsUtils.formatting(oilPriceZero.priceGun > oilPriceZero.priceYfq ? (oilPriceZero.priceGun - oilPriceZero.priceYfq) : 0.00) + "元");

                        oilPriceZero.check = true;
                        mOilName = oilPriceZero.oilName;
                        mOilNoAdapter.setList(data.oilPriceList);
                        mOilNoAdapter.notifyDataSetChanged();

                        gunNosZero.check = true;
                        mGunNo = gunNosZero.gunNo;
                        mGunNoAdapter.setList(oilPriceZero.gunNos);
                        mGunNoAdapter.notifyDataSetChanged();
                    }
                });

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
                            Toast.makeText(RefuelDetailActivity.this, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelDetailActivity.this, "com.baidu.BaiduMap")) {
                                navWithBaidu();
                            } else {
                                Toast.makeText(RefuelDetailActivity.this, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelDetailActivity.this, "com.autonavi.minimap")) {
                                navWithAmap();
                            } else {
                                Toast.makeText(RefuelDetailActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelDetailActivity.this, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelDetailActivity.this, "selectMapFlag", 1);
                        }

                        mapPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        mapPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRefuelOrder();
    }

    @OnClick({R.id.tv_navigation, R.id.fl_back, R.id.rtv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_navigation:
                getSelectedMap();
                break;
            case R.id.fl_back:
                finish();
                break;
            case R.id.rtv_confirm:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2), mContext)) {
                    isRefuelbalance();
                }
                break;
        }
    }
}
