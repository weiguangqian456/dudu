package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.home.adapter.ClassifyTitleAdapter;
import com.weiwei.home.base.BaseFragment;
import com.weiwei.home.test.RecycleItemClickListener;
import com.weiwei.home.view.CustomErrorView;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author : hc
 * @date : 2019/4/28.
 * @description:
 */

public class ClassifyFragment extends BaseFragment{

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

    private List<ModelHomeEntranceBean> mJdDateList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify;
    }

    @Override
    protected void initView() {
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
        httpJdColumn();
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
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < mJdDateList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", mJdDateList.get(i).getId());
            bundle.putString("isExchange", mJdDateList.get(i).getIsExchange());
            bundle.putString("TITLE", mJdDateList.get(i).getTitle());
            Fragment localStoreFragment = ClassifyItemFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        vp_classify.setOffscreenPageLimit(1);
        vp_classify.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragmentList));
        vp_classify.setCurrentItem(0);
        vp_classify.addOnPageChangeListener(classifyTitleAdapter);
    }

    private void httpJdColumn(){
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = api.getColumn("android");
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() != null) {
                    ResultEntity result = response.body();
                    if (result != null &&REQUEST_CODE == result.getCode()&& result.getData() != null) {
                        DataBean dataBean = JSONObject.parseObject(result.getData().toString(), DataBean.class);
                        mJdDateList = JSONObject.parseArray(dataBean.getJd().toString(), ModelHomeEntranceBean.class);
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
}
