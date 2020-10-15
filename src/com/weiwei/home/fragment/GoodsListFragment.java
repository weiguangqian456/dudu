package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.ChoicesAdapter;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.HomePageDataBeanNew;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author : hc
 * @date : 2019/5/16.
 * @description: 推荐
 */

public class GoodsListFragment extends BaseRecycleFragment {

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ChoicesAdapter(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartPage = 0;
        initHomePageData();
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String,String> params = new HashMap<>();
        params.put("type","2");
        return params;
    }

    @Override
    protected String getPath() {
        return Const.SHOP_MODEL + "product/page";
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_CHOICES;
    }


    @Override
    protected boolean isAddExtra() {
        return true;
    }


    private void initHomePageData() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        retrofit2.Call<ResultEntity> call = api.getProducts();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (result !=null && REQUEST_CODE == result.getCode() && result.getData() != null) {
                    HomePageDataBeanNew entity = JSON.parseObject(result.getData().toString(), HomePageDataBeanNew.class);
                    if(entity != null){
                        JSONArray array = JSONObject.parseArray(entity.getRecommends());
                        addExtraList(array);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }
}
