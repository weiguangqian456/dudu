package com.weiwei.home.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.adapter.MoreRefuelAdapter;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.base.BaseRecycleActivity;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.dialog.RefuelFiltrateDialog;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.bean.RefuelFiltrate;
import com.weiwei.salemall.bean.RefuelList;
import com.weiwei.salemall.utils.GpsUtils;
import com.weiwei.salemall.utils.RxLocationUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MoreRefuelActivity extends BaseRecycleActivity implements OnAddressCallback, MoreRefuelAdapter.OnClickListener {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_fuel_oil_type)
    TextView mTvFuelOilType;
    @BindView(R.id.tv_filtrate)
    TextView mTvFiltrate;
    @BindView(R.id.tv_brand)
    TextView mTvBrand;
    @BindView(R.id.ll_refuel_filtrate)
    LinearLayout mLlRefuelFiltrate;

    public static void start(Context context, boolean isLocationSuccess, Double latitude, Double longitude) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_LOCATION_SUCCESS", isLocationSuccess);
        bundle.putDouble("LATITUDE", latitude);
        bundle.putDouble("LONGITUDE", longitude);
        ArmsUtils.startActivity(context, MoreRefuelActivity.class, bundle);
    }

    private MoreRefuelAdapter mAdapter;
    private Map<String, String> mParams = new HashMap<>();
    private RefuelFiltrateDialog mFuelOilTypeDialog;
    private RefuelFiltrateDialog mFiltrateDialog;
    private RefuelFiltrateDialog mBrandDialog;

    private String mFuelOilType;
    private String mFiltrate;
    private StringBuilder brandBuilder;

    private BDLBSMapHelper mBdlbsMapHelper;
    private CommonPopupWindow mapPop = null;
    private Double mLatitude = 0.00;
    private Double mLongitude = 0.00;
    private Double mGoLatitude = 0.00;
    private Double mGoLongitude = 0.00;
    private boolean mIsLocationSuccess;

    List<RefuelFiltrate> mFuelOilTypeList = new ArrayList<>();
    List<RefuelFiltrate> mFiltrateList = new ArrayList<>();
    List<RefuelFiltrate> mBrandList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_refuel;
    }

    @Override
    protected boolean isShowError() {
        return true;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected String getPath() {
        return "benefit/web/CheZhuBangController/queryGasStationInfoList";
    }

    @Override
    protected Map<String, String> getParams() {
        mParams.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
 //       mParams.put("searchType", "1001");
        mParams.put("searchType", "");
        return mParams;
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_CHOICES;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("加油");

        mIsLocationSuccess = getIntent().getBooleanExtra("IS_LOCATION_SUCCESS", false);
        Double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        Double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        mAdapter = new MoreRefuelAdapter(this);
        mAdapter.setOnClickListener(this);

        initFiltrate();

        if (mIsLocationSuccess) {
            mLatitude = latitude;
            mLongitude = longitude;
            mParams.put("userAddressLatitude", mLatitude + "");
            mParams.put("userAddressLongitude", mLongitude + "");
        } else {
            mBdlbsMapHelper = new BDLBSMapHelper();
            mBdlbsMapHelper.getAddressDetail(this, this);
        }
        super.initView();
    }

    private void initFiltrate() {

        mFuelOilTypeList.add(new RefuelFiltrate("92#", "92#", true));
        mFuelOilTypeList.add(new RefuelFiltrate("95#", "95#", false));
        mFuelOilTypeList.add(new RefuelFiltrate("98#", "98#", false));
        mFuelOilTypeList.add(new RefuelFiltrate("0#", "0#", false));
        mFuelOilType = mFuelOilTypeList.get(0).filtrateValue;
        mTvFuelOilType.setText(mFuelOilTypeList.get(0).filtrateName);

        mFuelOilTypeDialog = new RefuelFiltrateDialog(mContext, mLlRefuelFiltrate);
        mFuelOilTypeDialog.setData(mFuelOilTypeList);
        mFuelOilTypeDialog.mAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                RefuelFiltrate item = mFuelOilTypeDialog.mAdapter.getList().get(position);
                mFuelOilType = item.filtrateValue;
                mTvFuelOilType.setText(item.filtrateName);
                refresh(true);
                mFuelOilTypeDialog.dismiss();
            }
        });

        mFiltrateList.add(new RefuelFiltrate("智能排序", "1001", true));
        mFiltrateList.add(new RefuelFiltrate("距离优先", "1001", false));
        mFiltrateList.add(new RefuelFiltrate("价格优先", "1002", false));
        mFiltrate = mFiltrateList.get(0).filtrateValue;
        mTvFiltrate.setText(mFiltrateList.get(0).filtrateName);

        mFiltrateDialog = new RefuelFiltrateDialog(mContext, mLlRefuelFiltrate);
        mFiltrateDialog.setData(mFiltrateList);
        mFiltrateDialog.mAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                RefuelFiltrate item = mFiltrateDialog.mAdapter.getList().get(position);
                mFiltrate = item.filtrateValue;
                mTvFiltrate.setText(item.filtrateName);
                refresh(true);
                mFiltrateDialog.dismiss();
            }
        });

        mBrandList.add(new RefuelFiltrate("中石油", "1", true));
        mBrandList.add(new RefuelFiltrate("中石化", "2", true));
        mBrandList.add(new RefuelFiltrate("壳牌", "3", true));
        mBrandList.add(new RefuelFiltrate("其他", "4", true));


        mBrandDialog = new RefuelFiltrateDialog(mContext, mLlRefuelFiltrate);
        mBrandDialog.setData(mBrandList);
        mBrandDialog.setIsAllChoose(true);
        mBrandDialog.setOnClickListener(new RefuelFiltrateDialog.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.rtv_cancel:
                        mBrandDialog.dismiss();
                        break;
                    case R.id.rtv_confirm:
                        mBrandList = mBrandDialog.mAdapter.getChooseList();
                        mTvBrand.setText(mBrandList.size() == mBrandDialog.mAdapter.getList().size() ? "全部品牌" : "部分品牌");
                        refresh(true);
                        mBrandDialog.dismiss();
                        break;
                }
            }
        });

        refresh(false);

    }

    @Override
    public void onAddressStart() {

    }

    @Override
    public void onAddressFail() {

    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        mIsLocationSuccess = true;
        mLatitude = bdLocation.getLatitude();
        mLongitude = bdLocation.getLongitude();
        mParams.put("userAddressLatitude", mLatitude + "");
        mParams.put("userAddressLongitude", mLongitude + "");
        initRefresh();
    }

    @Override
    public void onAddressFinish() {

    }

    @Override
    public void initRefresh() {
        super.initRefresh();
        if (!mIsLocationSuccess) {
            mBdlbsMapHelper.getAddressDetail(this, this);
        }
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

    private void refresh(boolean isRefresh) {
        brandBuilder = new StringBuilder();
        for (int i = 0; i < mBrandList.size(); i++) {
            if (mBrandList.get(i).check) {
                brandBuilder.append(mBrandList.get(i).filtrateValue);
                if (i != mBrandList.size() - 1) {
                    brandBuilder.append("#");
                }
            }
        }

        String searchContent = "@" + mFuelOilType + "@" + mFiltrate + "@" + brandBuilder;
        LogUtils.e("++++++++-------------->" + searchContent);
        String base64Str = Base64.encodeToString(searchContent.getBytes(), Base64.NO_WRAP);
        mParams.put("searchContent", base64Str);
        if (isRefresh) {
            initRefresh();
        }
    }

    private void navWithBaidu() {
        GpsUtils bdGps = RxLocationUtils.GCJ02ToBD09(mGoLongitude, mGoLatitude);
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(bdGps.getLatitude()).append(",").append(bdGps.getLongitude()).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        startActivity(intent);
    }

    private void navWithAmap() {
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + mGoLatitude +
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
                            Toast.makeText(MoreRefuelActivity.this, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(MoreRefuelActivity.this, "com.baidu.BaiduMap")) {
                                navWithBaidu();
                            } else {
                                Toast.makeText(MoreRefuelActivity.this, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(MoreRefuelActivity.this, "com.autonavi.minimap")) {
                                navWithAmap();
                            } else {
                                Toast.makeText(MoreRefuelActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(MoreRefuelActivity.this, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(MoreRefuelActivity.this, "selectMapFlag", 1);
                        }

                        mapPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        mapPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view, int position) {
        RefuelList refuelList = JSON.parseObject(mAdapter.getItem(position).toString(), RefuelList.class);
        switch (view.getId()) {
            case R.id.rll_item:
                RefuelDetailActivity.start(this, refuelList);
                break;
            case R.id.tv_navigation:
                mGoLatitude = refuelList.gasAddressLatitude;
                mGoLongitude = refuelList.gasAddressLongitude;
                getSelectedMap();
                break;
        }
    }

    @OnClick({R.id.fl_back, R.id.ll_fuel_oil_type, R.id.ll_filtrate, R.id.ll_brand, R.id.fl_refuel_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_fuel_oil_type:
                if (mFuelOilTypeDialog.isShowing()) {
                    mFuelOilTypeDialog.dismiss();
                } else {
                    mFuelOilTypeDialog.show();
                }
                break;
            case R.id.ll_filtrate:
                if (mFiltrateDialog.isShowing()) {
                    mFiltrateDialog.dismiss();
                } else {
                    mFiltrateDialog.show();
                }
                break;
            case R.id.ll_brand:
                if (mBrandDialog.isShowing()) {
                    mBrandDialog.dismiss();
                } else {
                    mBrandDialog.show();
                }
                break;
            case R.id.fl_refuel_search:
                RefuelSearchActivity.start(this, mIsLocationSuccess, mLatitude, mLongitude);
                break;
        }
    }
}
