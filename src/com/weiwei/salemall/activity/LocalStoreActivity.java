package com.weiwei.salemall.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.application.VsApplication;
import com.weiwei.salemall.adapter.BannerViewHolder;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.CitiesBean;
import com.weiwei.salemall.bean.CitiesDataBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SamqiColumnBean;
import com.weiwei.salemall.citypicker.util.GpsUtil;
import com.weiwei.home.fragment.NewLocalStoreFragment;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author Created by EDZ on 2018/7/23
 *         本地商家
 */

public class LocalStoreActivity extends TempAppCompatActivity implements View.OnClickListener, OnAddressCallback {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.et_input)
    EditText inputEt;
    @BindView(R.id.ll_location)
    LinearLayout locationLl;
    @BindView(R.id.tv_location)
    TextView locationTv;
    @BindView(R.id.cbanner_img)
    ConvenientBanner cBanner;
    @BindView(R.id.magic_indicator)
    MagicIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private List<BannerImageEntity> imgUrls = new ArrayList<>();
    private List<SamqiColumnBean> mIndicatorDataList = null;
    private boolean enable = false;
    private List<Fragment> fragmentList = null;
    private GpsUtil gpsUtil = null;
    /**
     * 栏目Id
     */
    private String columnId;

    /**
     * 地址信息
     */
    private String localCity = null;
    private String latitude = null;
    private String longitude = null;
    private String cityCode = null;
    private String columnName = null;
    private static final String TAG = "LocalStoreActivity";

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitStateUtils.setImmersionStateMode(this);
        setContentView(R.layout.activity_local_store);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        checkLocationPermission();
    }

    private void initView() {
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        cBanner.startTurning(2000);
        String now = PreferencesUtils.getString(VsApplication.getContext(), "selectCityName");
        if (now != null && !now.equals("")) {
            locationTv.setText(now);
        } else {
            locationTv.setText("深圳市");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cBanner.stopTurning();
    }

    @Override
    protected void init() {

    }

    /**
     * 检测定位权限
     */
    private void checkLocationPermission() {
        XXPermissions.with(this).permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION, Permission.READ_PHONE_STATE, Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                if (isAll) {
                    initLocationData();
                } else {
                    PreferencesUtils.putString(LocalStoreActivity.this, "selectCityCode", "200");
                    PreferencesUtils.putString(LocalStoreActivity.this, "currentCity", "200");
                    locationTv.setText("深圳市");
                    initEvent();
                    initBannerImageData();
                    getMagicIndicatorData();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    PreferencesUtils.putString(LocalStoreActivity.this, "selectCityCode", "200");
                    PreferencesUtils.putString(LocalStoreActivity.this, "currentCity", "200");
                    locationTv.setText("深圳市");
                    initEvent();
                    initBannerImageData();
                    getMagicIndicatorData();
                } else {
                    PreferencesUtils.putString(LocalStoreActivity.this, "selectCityCode", "200");
                    PreferencesUtils.putString(LocalStoreActivity.this, "currentCity", "200");
                    locationTv.setText("深圳市");
                    initEvent();
                    initBannerImageData();
                    getMagicIndicatorData();
                }
            }
        });
    }

    /**
     * 开始定位
     */
    private void initLocationData() {
        new BDLBSMapHelper().getAddressDetail(this, this);
    }

    public void OnReceive(BDLocation bdLocation) {
        localCity = bdLocation.getCity();
        longitude = String.valueOf(bdLocation.getLongitude());
        latitude = String.valueOf(bdLocation.getLatitude());

        if (localCity == "") {
            finish();
        }

        String citiesData = PreferencesUtils.getString(getApplicationContext(), "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        for (int i = 0; i < allCitiesList.size(); i++) {
            if (!StringUtils.isEmpty(localCity) && localCity.equals(allCitiesList.get(i).getCityName())) {
                cityCode = String.valueOf(allCitiesList.get(i).getId());
            }
        }

        PreferencesUtils.putString(this, "selectCityCode", cityCode);
        PreferencesUtils.putString(this, "selectCityName", localCity);
        PreferencesUtils.putString(this, "currentCity", cityCode);
        PreferencesUtils.putString(this, "currentLocationLon", longitude);
        PreferencesUtils.putString(this, "currentLocationLat", latitude);

        locationTv.setText(localCity);
        initEvent();
        initBannerImageData();
        getMagicIndicatorData();
    }

    @Override
    public void onAddressStart() {
        Log.e(TAG, "onAddressStart ");
    }

    @Override
    public void onAddressFail() {
        loadingDialog.setLoadingDialogDismiss();
        Log.e(TAG, "onAddressFail ");
    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        loadingDialog.setLoadingDialogDismiss();
        Log.e(TAG, "onAddressSuccess ");
        OnReceive(bdLocation);
    }

    @Override
    public void onAddressFinish() {
        Log.e(TAG, "onAddressFinish ");
    }

    private void initEvent() {
        columnId = getIntent().getStringExtra("columnId");
        columnName = getIntent().getStringExtra("columnName");
        backIv.setOnClickListener(this);
        inputEt.setFocusable(false);
        inputEt.setOnClickListener(this);
        locationLl.setOnClickListener(this);
    }

    /**
     * 初始化Store Banner数据
     */
    private void initBannerImageData() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getBanners();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "店铺banners msg===>" + result.getMsg());
                    BannerDataEntity bannerDataBean = JSONObject.parseObject(result.getData().toString(), BannerDataEntity.class);
                    imgUrls.addAll(bannerDataBean.getStore());
                    cBanner.setPageIndicator(new int[]{R.drawable.index_white_point, R.drawable.index_blue_point});
                    cBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                    cBanner.setPages(new CBViewHolderCreator() {
                        @Override
                        public Object createHolder() {
                            return new BannerViewHolder();
                        }
                    }, imgUrls);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 初始化商家栏目数据
     */
    private void getMagicIndicatorData() {
        mIndicatorDataList = new ArrayList<>();
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getChildColumnsData(columnId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "店铺栏目msg===>" + result.getMsg());
                    List<SamqiColumnBean> list = JSONObject.parseArray(result.getData().toString(), SamqiColumnBean.class);
//                    SamqiColumnBean allBean = new SamqiColumnBean();
//                    allBean.setId("0");
//                    allBean.setAppId("dudu");
//                    allBean.setColumnName("全部");
//                    mIndicatorDataList.add(allBean);
                    mIndicatorDataList.addAll(list);
                }
                setMagicIndicator();
                initViewPager();
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void setMagicIndicator() {
        indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mIndicatorDataList == null ? 0 : mIndicatorDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mIndicatorDataList.get(index).getColumnName());
                int size = (int) getResources().getDimension(R.dimen.w_14_dip);
                simplePagerTitleView.setTextSize(px2sp(context, size));
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.public_color_F11801));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(getResources().getColor(R.color.public_color_F11801));
                return linePagerIndicator;
            }
        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < mIndicatorDataList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", mIndicatorDataList.get(i).getId());
            bundle.putString("columnId", columnId);
            Fragment localStoreFragment = NewLocalStoreFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.et_input:
                String[] key = new String[]{"skipFlag", "columnId"};
                String[] value = new String[]{"1", columnId};
                skipPage(SearchViewActivity.class, key, value);
                break;
            case R.id.ll_location:
                skipPage(SelectCityActivity.class);
                break;
            default:
                break;
        }
    }
}
