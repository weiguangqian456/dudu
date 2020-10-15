package com.weiwei.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.activity.DiscoverActivity;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.base.BaseFragment;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.home.adapter.MemberIntroduceAdapter;
import com.weiwei.home.view.CustomErrorView;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SamqiColumnBean;
import com.weiwei.salemall.bean.SeckillDataEntity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author : hc
 * @date : 2019/4/3.
 * @description: 新会员专区 介绍页
 */

public class MemberIntroduceFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.fl_back)
    FrameLayout fl_back;
    @BindView(R.id.magic_indicator)
    MagicIndicator magic_indicator;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    @BindView(R.id.view_stub)
    ViewStub mViewStub;

    private CustomErrorView cev_view;


    private List<SamqiColumnBean> mDataList = new ArrayList<>();
    public static void launch(Context context){
        context.startActivity(new Intent(context,DiscoverActivity.class));
    }

    protected int getStateBarColor() {
        return Color.parseColor("#ef0022");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_member;
    }

    @Override
    protected void initView() {
        fl_back.setOnClickListener(this);
        getMagicIndicatorData();
    }

    /**
     * 获取子栏目
     */
    private void getMagicIndicatorData() {
        ApiService api = RetrofitClient.getInstance(getContext()).Api();
        retrofit2.Call<ResultEntity> call = api.getChildColumnsData("97bbcb919c7845d18995e8236c13d641");
        call.enqueue(new RetrofitCallback<ResultEntity>() {
            @Override
            protected void onNext(ResultEntity result) {
                List<SamqiColumnBean> list = JSONObject.parseArray(result.getData().toString(), SamqiColumnBean.class);
                mDataList.clear();
                mDataList.addAll(list);
                initPager();
                initIndicator();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if(mDataList == null || mDataList.size() == 0){
                    cev_view = (CustomErrorView) mViewStub.inflate();
                }
                if(cev_view != null){
                    cev_view.initState( mDataList == null || mDataList.size() == 0 ? CustomErrorView.ERROR_VIEW_NETWORK : CustomErrorView.ERROR_NOT);
                }
            }
        });
    }

    private void initPager(){
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            SamqiColumnBean bean = mDataList.get(i);
            Bundle bundle = new Bundle();
            bundle.putString("ID", bean.getId());
            Fragment localStoreFragment = DiscoverFragment.newInstance(bundle);
            fragmentList.add(localStoreFragment);
        }
        view_pager.setOffscreenPageLimit(1);
        view_pager.setAdapter(new MyFragmentAdapter(getFragmentManager(), fragmentList));
        view_pager.setCurrentItem(0);
    }


    private void initIndicator(){
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).getColumnName());
                int size = (int) getResources().getDimension(R.dimen.w_14_dip);
                simplePagerTitleView.setTextSize(px2sp(context, size));
                simplePagerTitleView.setNormalColor(Color.parseColor("#E4E4E4"));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.color_white));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view_pager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(getResources().getColor(R.color.color_white));
                return linePagerIndicator;
            }
        });
        magic_indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magic_indicator, view_pager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                getActivity().finish();
                break;
        }
    }
}
