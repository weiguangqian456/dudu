package com.weiwei.home.fragment;

import android.os.Bundle;

import com.weiwei.base.application.VsApplication;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.NewCollectionV1Adapter;
import com.weiwei.home.yd.YDRefreshInterface;
import com.weiwei.salemall.utils.PreferencesUtils;
import java.util.Map;

import static com.weiwei.salemall.base.Const.SHOP_MODEL;

/**
 * @author hc
 * @date by hc on 2019/3/1.
 * @description: 统一管理本地商家 - 统一管理本地商家
 */

public class NewLocalStoreFragment extends BaseRecycleFragment{

    private String selectForResult = "200";
    private String columnId = null;
    private String classificationId = null;
    private String latitude = null;
    private String longitude = null;
    private String cityCode = null;

    public static NewLocalStoreFragment newInstance(Bundle args) {
        NewLocalStoreFragment fragment = new NewLocalStoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void initData(){
        if(getArguments() !=null){
            columnId = getArguments().getString("columnId");
            classificationId = getArguments().getString("classificationFlag");
        }
        selectForResult = PreferencesUtils.getString(VsApplication.getContext(), "selectCityCode");
        cityCode = PreferencesUtils.getString(VsApplication.getContext(), "currentCity");
        longitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLon");
        latitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLat");
    }

    @Override
    public void onResume() {
        super.onResume();
        initRefresh();
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new NewCollectionV1Adapter(getContext());
    }

    @Override
    protected Map<String, String> getParams() {
        selectForResult = PreferencesUtils.getString(VsApplication.getContext(), "selectCityCode");
        cityCode = PreferencesUtils.getString(VsApplication.getContext(), "currentCity");
        longitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLon");
        latitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLat");
        Map<String, String> params = super.getParams();
        if (selectForResult != null && selectForResult.equals(cityCode)) {
            params.put("currentLon", longitude);
            params.put("currentLat", latitude);
            params.put("cityId", cityCode);
        }else{
            params.put("currentLon", "");
            params.put("currentLat", "");
            params.put("cityId", selectForResult);
        }
        return params;
    }

    @Override
    protected String getPath() {
        return SHOP_MODEL + "childColumns/dataByColumn/" + getArgumentsInfo("classificationFlag");
    }

}
