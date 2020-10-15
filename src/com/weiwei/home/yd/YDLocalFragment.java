package com.weiwei.home.yd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.application.VsApplication;
import com.weiwei.home.adapter.ClassifyTitleAdapter;
import com.weiwei.home.base.BaseFragment;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.fragment.ClassifyItemFragment;
import com.weiwei.home.test.RecycleItemClickListener;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.view.CustomErrorView;
import com.weiwei.salemall.activity.LocalStoreActivity;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.bean.CitiesBean;
import com.weiwei.salemall.bean.CitiesDataBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author : hc
 * @date : 2019/4/28.
 * @description:
 */

public class YDLocalFragment extends BaseFragment implements OnAddressCallback {

    @BindView(R.id.rv_title)
    RecyclerView rv_title;
    @BindView(R.id.vp_classify)
    ViewPager vp_classify;
    @BindView(R.id.srl_title)
    SwipeRefreshLayout srl_title;
    @BindView(R.id.fl_hint)
    View fl_hint;
    @BindView(R.id.cev_view)
    CustomErrorView cev_view;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;

    private List<Fragment> fragmentList;
    private List<YDLocalClassEntity> mJdDateList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify2;
    }

    @Override
    protected void initView() {
        ll_search.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                SkipPageUtils.getInstance(mContext).skipPage(SearchViewActivity.class);
            }
        });
        checkLocationPermission();
        srl_title.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                httpJdColumn();
            }
        });
        cev_view.setOnErrorClickListener(new CustomErrorView.OnErrorClickListener(){

            @Override
            public void onRefresh() {
                httpJdColumn();
            }

        });
    }

    private void initViewPager(){
        rv_title.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rv_title.setLayoutManager(new LinearLayoutManager(mContext));
        ClassifyTitleAdapter classifyTitleAdapter = new ClassifyTitleAdapter(mContext, mJdDateList);
        classifyTitleAdapter.setItemClickListener(new RecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                vp_classify.setCurrentItem(position);
            }
        });
        rv_title.setAdapter(classifyTitleAdapter);
        fragmentList = new ArrayList<>();
        for (int i = 0; i < mJdDateList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("id", mJdDateList.get(i).getId());
            bundle.putString("TYPE", "YD");
            bundle.putString("TITLE", mJdDateList.get(i).getTitle());
            Fragment localStoreFragment = YDClassifyItemFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        vp_classify.setOffscreenPageLimit(1);
        vp_classify.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragmentList));
        vp_classify.setCurrentItem(0);
        vp_classify.addOnPageChangeListener(classifyTitleAdapter);
    }

    private void httpJdColumn(){
        if(checkLocationPermission){
            return;
        }
        String id = getArgumentsInfo("ID");
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        Map<String,String> map = new HashMap<>();
        retrofit2.Call<ResultEntity> call = api.getLocalClass(id,map);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() != null) {
                    ResultEntity result = response.body();
                    if (REQUEST_CODE == result.getCode()&& result.getData() != null) {
                        mJdDateList = JSONObject.parseArray(result.getData().toString(), YDLocalClassEntity.class);
                        LogUtils.e("mJdDateList:",mJdDateList.toString());
                        initViewPager();
                        fl_hint.setVisibility(View.GONE);
                    }
                    srl_title.setRefreshing(false);
                }
                cev_view.initState(mJdDateList == null || mJdDateList.size() == 0 ? CustomErrorView.ERROR_VIEW_NETWORK : CustomErrorView.ERROR_NOT);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                srl_title.setRefreshing(false);
                cev_view.initState(mJdDateList == null || mJdDateList.size() == 0 ? CustomErrorView.ERROR_VIEW_NETWORK : CustomErrorView.ERROR_NOT);
            }
        });
    }


    /**
     * 开始定位
     */
    private void initLocationData() {
        new BDLBSMapHelper().getAddressDetail(mContext, this);
    }

    private boolean checkLocationPermission = true;
    /**
     * 检测定位权限
     */
    private void checkLocationPermission() {
        XXPermissions.with(getActivity()).permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION
                , Permission.READ_PHONE_STATE, Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        checkLocationPermission = false;
                        if (isAll) {
                            initLocationData();
                        } else {
                            PreferencesUtils.putString(mContext, "selectCityCode", "200");
                            PreferencesUtils.putString(mContext, "currentCity", "200");
                        }
                        httpJdColumn();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        checkLocationPermission = false;
                        if (quick) {
                            PreferencesUtils.putString(mContext, "selectCityCode", "200");
                            PreferencesUtils.putString(mContext, "currentCity", "200");
                        } else {
                            PreferencesUtils.putString(mContext, "selectCityCode", "200");
                            PreferencesUtils.putString(mContext, "currentCity", "200");
                        }
                        httpJdColumn();
                    }
                });
    }


    @Override
    public void onAddressStart() {
        LogUtils.e("onAddressStart");
    }

    @Override
    public void onAddressFail() {
        LogUtils.e("onAddressFail");
    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        LogUtils.e("onAddressSuccess");
        String localCity = bdLocation.getCity();
        String longitude = String.valueOf(bdLocation.getLongitude());
        String latitude = String.valueOf(bdLocation.getLatitude());
        String cityCode = "";
        String citiesData = PreferencesUtils.getString(mContext, "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        for (int i = 0; i < allCitiesList.size(); i++) {
            if (!StringUtils.isEmpty(localCity) && localCity.equals(allCitiesList.get(i).getCityName())) {
                cityCode = String.valueOf(allCitiesList.get(i).getId());
            }
        }
        String o_longitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLon");
        String o_latitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLat");
        boolean isNotRefresh = false;
        if(!TextUtils.isEmpty(o_longitude) && !TextUtils.isEmpty(o_latitude)){
            isNotRefresh = o_longitude.equals(longitude) && o_latitude.equals(latitude);
        }
        PreferencesUtils.putString(mContext, "selectCityCode", cityCode);
        PreferencesUtils.putString(mContext, "selectCityName", localCity);
        PreferencesUtils.putString(mContext, "currentCity", cityCode);
        PreferencesUtils.putString(mContext, "currentLocationLon", longitude);
        PreferencesUtils.putString(mContext, "currentLocationLat", latitude);
        if(fragmentList != null && !isNotRefresh){
            Log.e("TAG-A","将已加载的刷新");
            for(Fragment f : fragmentList){
                if(f instanceof BaseRecycleFragment && ((BaseRecycleFragment) f).isLoadLazy()){
                    ((BaseRecycleFragment)f).initRefresh();
                }
            }
        }
    }

    @Override
    public void onAddressFinish() {
        LogUtils.e("onAddressFinish");
    }
}
