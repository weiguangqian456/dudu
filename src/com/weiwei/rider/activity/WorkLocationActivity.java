package com.weiwei.rider.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.application.VsApplication;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.bean.RiderCity;
import com.weiwei.salemall.bean.CitiesBean;
import com.weiwei.salemall.bean.CitiesDataBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.citypicker.adapter.CityListAdapter;
import com.weiwei.salemall.citypicker.adapter.InnerListener;
import com.weiwei.salemall.citypicker.adapter.OnPickListener;
import com.weiwei.salemall.citypicker.adapter.decoration.DividerItemDecoration;
import com.weiwei.salemall.citypicker.adapter.decoration.SectionItemDecoration;
import com.weiwei.salemall.citypicker.model.City;
import com.weiwei.salemall.citypicker.model.HotCity;
import com.weiwei.salemall.citypicker.model.LocateState;
import com.weiwei.salemall.citypicker.model.LocatedCity;
import com.weiwei.salemall.citypicker.util.PinyinTool;
import com.weiwei.salemall.citypicker.view.SideIndexBar;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class WorkLocationActivity extends RiderBaseActivity implements TextWatcher,InnerListener, SideIndexBar.OnIndexTouchedChangedListener, OnAddressCallback {
    @BindView(R.id.et_input)
    EditText inputEt;
    @BindView(R.id.cp_city_recyclerview)
    RecyclerView cityRecycleView;
    @BindView(R.id.cp_overlay)
    TextView ovlerLayTv;
    @BindView(R.id.cp_side_index_bar)
    SideIndexBar indexBar;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.cp_clear_all)
    ImageView mClearAllBtn;
    @BindView(R.id.cp_empty_view)
    View mEmptyView;

    private LinearLayoutManager mLayoutManager = null;
    private CityListAdapter mAdapter = null;
    private List<City> mAllCities = new ArrayList<>();
    private List<HotCity> mHotCities = new ArrayList<>();
    private List<City> mResults = new ArrayList<>();

    private LocatedCity mLocatedCity = null;
    private int locateState;
    private OnPickListener mOnPickListener = null;

    public static final String action = "com.duduhx.selectCityCode";
    private String lat;
    private String lon;

    private PinyinTool pinyinTool;

    private static int RESULTCODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_work_location;
    }

    @Override
    protected void initView() {

        indexBar.setOverlayTextView(ovlerLayTv).setOnIndexChangedListener(this);
        inputEt.addTextChangedListener(this);

    }

    @Override
    protected void initData() {
        initLocatedCity();
        pinyinTool = new PinyinTool();
    }

    private void initLocatedCity() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        new BDLBSMapHelper().getAddressDetail(this, this);
    }


    private void updateUi() {
//        LogUtils.e("mAllCities:",mAllCities.toString());
        mAllCities.addAll(mResults);
        //显示列表
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cityRecycleView.setLayoutManager(mLayoutManager);
        cityRecycleView.setHasFixedSize(true);
        cityRecycleView.addItemDecoration(new SectionItemDecoration(this, mAllCities), 0);
        cityRecycleView.addItemDecoration(new DividerItemDecoration(this), 1);
        mAdapter = new CityListAdapter(this, mAllCities, mHotCities, locateState);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        cityRecycleView.setAdapter(mAdapter);
        cityRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }
        });
    }


    @Override
    public void onAddressStart() {

    }

    @Override
    public void onAddressFail() {
        loadingDialog.setLoadingDialogDismiss();
//        Log.e("delectCity", "定位失败");
        mLocatedCity = new LocatedCity("定位失败", "未知", "0");
        locateState = LocateState.SUCCESS;
//        mAllCities = initAllCities();
        mAllCities.add(0, mLocatedCity);
//        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
//        mResults = mAllCities;
//        updateUi();
            initAllCities();
    }

    private void initAllCities() {
        RetrofitClient.getInstance(this).Api()
                .getCitys()
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        if(result.getData() != null) {
                            Map map = JSON.parseObject(result.getData().toString(),Map.class);
//                            LogUtils.e("map",map.toString());
                            List<RiderCity> cityList = JSON.parseArray(map.get("tCitiesRiders").toString(),RiderCity.class);
 //                           LogUtils.e("cityList",cityList.toString());
                            String pinyin = null;
                            for (RiderCity riderCity : cityList) {
                                try {
                                    pinyin = pinyinTool.toPinYin(riderCity.getCityName(),"",PinyinTool.Type.LOWERCASE);
                                    mResults.add(new City(riderCity.getCityName(),riderCity.getId(),pinyin,riderCity.getCityCode()));
                                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                                }
                            }
                            Collections.sort(mResults, new CityComparator());
                            updateUi();
                        }
                    }
                });
    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        loadingDialog.setLoadingDialogDismiss();
        OnReceive(bdLocation);
    }
    private void OnReceive(BDLocation bdLocation) {
        lat = bdLocation.getLatitude() + "";
        lon = bdLocation.getLongitude() + "";
        String localCity = bdLocation.getCity();
        String citiesData = PreferencesUtils.getString(this, "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        if (localCity != null && !localCity.isEmpty()) {
            for (int i = 0; i < allCitiesList.size(); i++) {
                if (allCitiesList.get(i).getCityName().contains(localCity)) {
                    mLocatedCity = new LocatedCity(localCity, allCitiesList.get(i).getProvinceCode(), String.valueOf(allCitiesList.get(i).getId()));
                }
            }
            locateState = LocateState.SUCCESS;
        } else {
            mLocatedCity = new LocatedCity("正在定位…", "未知", "0");
            locateState = LocateState.FAILURE;
        }
 //       mAllCities = initAllCities();
        mAllCities.add(0, mLocatedCity);
        initAllCities();
//        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
//        mResults = mAllCities;
//        LogUtils.e("mResults: " ,mResults.toString());
//        updateUi();
    }


    @Override
    public void onAddressFinish() {

    }

    @Override
    public void dismiss(int position, City data) {
        String name = data.getName();
        String code = data.getCode();
        PreferencesUtils.putString(WorkLocationActivity.this, "selectCityCode", code);
        PreferencesUtils.putString(WorkLocationActivity.this, "selectCityName", name);
        PreferencesUtils.putString(WorkLocationActivity.this, "currentLocationLon", lon);
        PreferencesUtils.putString(WorkLocationActivity.this, "currentLocationLat", lat);
        sendSelectCityBroadCast(code);
 //       finish();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
//        LogUtils.e("name:",name);
 //       LogUtils.e("code:",code);
        Intent intent = new Intent();
        intent.putExtra("cityName",name);
        intent.putExtra("cityCode",code);
        setResult(RESULTCODE,intent);
        finish();
    }

    private void sendSelectCityBroadCast(String cityCode) {
        Intent intent = new Intent(action);
        intent.putExtra("selectCityCode", cityCode);
        VsApplication.getContext().sendBroadcast(intent);
    }


    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    @Override
    public void onIndexChanged(String index, int position) {
        if(mAdapter != null)
        mAdapter.scrollToSection(index);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String keyword = editable.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (cityRecycleView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
 //           mClearAllBtn.setVisibility(View.VISIBLE);
            mResults = searchCity(keyword);
            ((SectionItemDecoration) (cityRecycleView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
        cityRecycleView.scrollToPosition(0);
    }

    private List<City> searchCity(String keyword) {
        List<City> result = new ArrayList<>();
//        String citiesData = PreferencesUtils.getString(this, "citiesData");
 //       CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
 //       List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        PinyinTool tool = new PinyinTool();
        String pinyin = null;
        for (int i = 0; i < mResults.size(); i++) {
            if (mResults.get(i).getName().contains(keyword)) {
                String name = mResults.get(i).getName();
                String province = mResults.get(i).getProvince();
                try {
                    pinyin = tool.toPinYin(mResults.get(i).getName(), "", PinyinTool.Type.LOWERCASE);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                String code = String.valueOf(mResults.get(i).getCode());
                City city = new City(name, province, pinyin, code);
                result.add(city);
            }
        }
        Collections.sort(result, new CityComparator());
        return result;
    }

    @OnClick(R.id.rl_back)
    public void onViewClicked(View view) {
        finish();
    }

    /**
     * sort by a-z
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }

}
