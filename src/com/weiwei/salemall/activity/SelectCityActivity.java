package com.weiwei.salemall.activity;

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
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.salemall.bean.CitiesBean;
import com.weiwei.salemall.bean.CitiesDataBean;
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
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Created by EDZ on 2018/08/03
 *         选择城市
 */
public class SelectCityActivity extends VsBaseActivity implements TextWatcher, View.OnClickListener, InnerListener, SideIndexBar.OnIndexTouchedChangedListener, OnAddressCallback {
    @BindView(R.id.cp_search_box)
    EditText inputEt;
    @BindView(R.id.cp_cancel)
    TextView cancelTv;
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
    private List<City> mAllCities = null;
    private List<HotCity> mHotCities = null;
    private List<City> mResults = null;

    private LocatedCity mLocatedCity = null;
    private int locateState;
    private OnPickListener mOnPickListener = null;

    public static final String action = "com.duduhx.selectCityCode";
    private String lat;
    private String lon;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        inputEt.addTextChangedListener(this);
        cancelTv.setOnClickListener(this);
        backRl.setOnClickListener(this);
        indexBar.setOverlayTextView(ovlerLayTv).setOnIndexChangedListener(this);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        initLocatedCity();
        initHotCities();
    }

    private void updateUi() {
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

    private void initHotCities() {
        mHotCities = new ArrayList<>();
        String citiesData = PreferencesUtils.getString(this, "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> hotCitiesList = JSON.parseArray(citiesDataBean.getHotCity().toString(), CitiesBean.class);
        for (int i = 0; i < hotCitiesList.size(); i++) {
            mHotCities.add(new HotCity(hotCitiesList.get(i).getCityName(), hotCitiesList.get(i).getProvinceCode(), String.valueOf(hotCitiesList.get(i).getId())));
        }
    }

    private void initLocatedCity() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        new BDLBSMapHelper().getAddressDetail(this, this);
    }

    private List<City> initAllCities() {
        mResults = new ArrayList<>();
        String citiesData = PreferencesUtils.getString(this, "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        PinyinTool tool = new PinyinTool();
        for (int i = 0; i < allCitiesList.size(); i++) {
            City city = new City();
            city.setName(allCitiesList.get(i).getCityName());
            city.setProvince(allCitiesList.get(i).getProvinceCode());
            try {
                city.setPinyin(tool.toPinYin(allCitiesList.get(i).getCityName(), "", PinyinTool.Type.LOWERCASE));
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            city.setCode(String.valueOf(allCitiesList.get(i).getId()));
            mResults.add(city);
        }
        Collections.sort(mResults, new CityComparator());
        return mResults;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (cityRecycleView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
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
        String citiesData = PreferencesUtils.getString(this, "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        PinyinTool tool = new PinyinTool();
        String pinyin = null;
        for (int i = 0; i < allCitiesList.size(); i++) {
            if (allCitiesList.get(i).getCityName().contains(keyword)) {
                String name = allCitiesList.get(i).getCityName();
                String province = allCitiesList.get(i).getProvinceCode();
                try {
                    pinyin = tool.toPinYin(allCitiesList.get(i).getCityName(), "", PinyinTool.Type.LOWERCASE);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                String code = String.valueOf(allCitiesList.get(i).getId());
                City city = new City(name, province, pinyin, code);
                result.add(city);
            }
        }
        Collections.sort(result, new CityComparator());
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cp_cancel:
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss(int position, City data) {
        String name = data.getName();
        String code = data.getCode();
        PreferencesUtils.putString(SelectCityActivity.this, "selectCityCode", code);
        PreferencesUtils.putString(SelectCityActivity.this, "selectCityName", name);
        PreferencesUtils.putString(SelectCityActivity.this, "currentLocationLon", lon);
        PreferencesUtils.putString(SelectCityActivity.this, "currentLocationLat", lat);
        sendSelectCityBroadCast(code);
        finish();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
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
        mAdapter.scrollToSection(index);
    }

    @Override
    public void onAddressStart() {
    }

    @Override
    public void onAddressFail() {
        loadingDialog.setLoadingDialogDismiss();
        Log.e("delectCity", "定位失败");
        mLocatedCity = new LocatedCity("定位失败", "未知", "0");
        locateState = LocateState.SUCCESS;
        mAllCities = initAllCities();
        mAllCities.add(0, mLocatedCity);
        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
        mResults = mAllCities;
        updateUi();
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
        mAllCities = initAllCities();
        mAllCities.add(0, mLocatedCity);
        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
        mResults = mAllCities;
        updateUi();
    }

    @Override
    public void onAddressFinish() {
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
