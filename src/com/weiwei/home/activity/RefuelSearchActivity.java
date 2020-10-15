package com.weiwei.home.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidkun.xtablayout.XTabLayout;
import com.baidu.location.BDLocation;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.adapter.MoreRefuelAdapter;
import com.weiwei.home.base.BaseRecycleActivity;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.view.CustomErrorView;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefuelSearchActivity extends BaseRecycleActivity implements OnAddressCallback, MoreRefuelAdapter.OnClickListener {

    @BindView(R.id.tablayout)
    XTabLayout mTabLayout;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.iv_search_close)
    ImageView mIvSearchClose;
    @BindView(R.id.tv_serach)
    TextView mTvSerach;

    public static void start(Context context, boolean isLocationSuccess, Double latitude, Double longitude) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_LOCATION_SUCCESS", isLocationSuccess);
        bundle.putDouble("LATITUDE", latitude);
        bundle.putDouble("LONGITUDE", longitude);
        ArmsUtils.startActivity(context, RefuelSearchActivity.class, bundle);
    }

    private MoreRefuelAdapter mAdapter;
    private Map<String, String> mParams = new HashMap<>();

    private String mSearchType = "1001";
    private String mSearchText = "";

    private BDLBSMapHelper mBdlbsMapHelper;
    private CommonPopupWindow mapPop = null;
    private Double mLatitude;
    private Double mLongitude;
    private Double mGoLatitude = 0.00;
    private Double mGoLongitude = 0.00;
    private boolean mIsLocationSuccess;

    List<RefuelFiltrate> mBrandList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_search;
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
        mParams.put("searchType", "1002");
        return mParams;
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_CHOICES;
    }

    @Override
    protected boolean isImmediatelyLoad() {
        return false;
    }

    @Override
    protected void initView() {

        mSwipeRefreshLayout.setEnabled(false);

        mIsLocationSuccess = getIntent().getBooleanExtra("IS_LOCATION_SUCCESS", false);
        Double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        Double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        mAdapter = new MoreRefuelAdapter(this);
        mAdapter.setOnClickListener(this);

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchText = mEtSearch.getText().toString().trim();
                mIvSearchClose.setVisibility(mSearchText.length() > 0 ? View.VISIBLE : View.GONE);
                mTvSerach.setVisibility(mSearchText.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTabLayout.addTab(mTabLayout.newTab().setText("目的地"));
        mTabLayout.addTab(mTabLayout.newTab().setText("加油站"));
        mTabLayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mSearchType = "1001";
                        mEtSearch.setHint("请输入要搜索的目的地");
                        break;
                    case 1:
                        mSearchType = "1002";
                        mEtSearch.setHint("请输入要搜索的加油站");
                        break;
                }
                refresh();
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
            }
        });

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

    private void refresh() {
        String searchContent = mSearchType + "@" + mSearchText;
        LogUtils.e("++++++++-------------->" + searchContent);
        String base64Str = Base64.encodeToString(searchContent.getBytes(), Base64.NO_WRAP);
        mParams.put("searchContent", base64Str);
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

    @Override
    public void onHttpFinish() {
        super.onHttpFinish();
        mSwipeRefreshLayout.setEnabled(true);
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
                            Toast.makeText(RefuelSearchActivity.this, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelSearchActivity.this, "com.baidu.BaiduMap")) {
                                navWithBaidu();
                            } else {
                                Toast.makeText(RefuelSearchActivity.this, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(RefuelSearchActivity.this, "com.autonavi.minimap")) {
                                navWithAmap();
                            } else {
                                Toast.makeText(RefuelSearchActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelSearchActivity.this, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(RefuelSearchActivity.this, "selectMapFlag", 1);
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

    @OnClick({R.id.fl_back, R.id.iv_search_close, R.id.tv_serach})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.iv_search_close:
                mEtSearch.setText("");
                break;
            case R.id.tv_serach:
                mSwipeRefreshLayout.setRefreshing(true);
                mErrorView.initState(CustomErrorView.ERROR_NOT);
                refresh();
                initRefresh();
                break;
        }
    }

}
