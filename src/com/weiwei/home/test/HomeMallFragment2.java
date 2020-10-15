package com.weiwei.home.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.fragment.HomePushDialog;
import com.weiwei.home.view.PagerCarouselView;
import com.weiwei.home.adapter.ChoicesAdapter;
import com.weiwei.home.adapter.HomeMemberAdapter;
import com.weiwei.salemall.adapter.CagegoryViewPagerAdapter;
import com.weiwei.salemall.adapter.EntranceAdapter;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.HomePageDataBeanNew;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SeckillDataEntity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.widget.WrapContentHeightViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : hc
 * @date : 2019/3/19.
 * @description: 首页商城
 */

public class HomeMallFragment2 extends BaseRecycleFragment {

    private List<SeckillProductEntity> seckillProductList;
    private BannerDataEntity bannerDataEntity;
    private DataBean         dataEntity;
    private RequestsListener mRequestsListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycle_home;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ChoicesAdapter(mContext);
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_CHOICES;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String,String> params = new HashMap<>();
        params.put("type","1");
        return params;
    }

    @Override
    protected boolean isAddExtra() {
        return true;
    }

    @Override
    protected String getPath() {
        return "shop/api/product/page";
    }

    @Override
    protected void initView() {
        fullScreen(getActivity());
        super.initView();
        mRequestsListener = new RequestsListener(new RequestsListener.HttpRequestFinish() {
            @Override
            public void onHttpRequestFinish() {
                initHomeHead();
            }
        });
        httpHomePush();
        httpBanner();
        httpSeckill();
        httpInitData();
        httpInitList();
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
//                attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                window.setAttributes(attributes);
            }
        }
    }


    @Override
    protected void onInitRefresh() {
        super.onInitRefresh();
        httpHomePush();
        httpBanner();
        httpSeckill();
        httpInitData();
        httpInitList();
    }

    /**
     * 推送
     */
    private void httpHomePush(){
        Map<String,String> params = new HashMap<>();
        params.put("mobileIdentity", VsUtil.getAloneImei(getActivity()));
        params.put("platform", "android");
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getHomePagePush(params)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        BannerImageEntity entity = JSON.parseObject(result.getData().toString(), BannerImageEntity.class);
                        if(getFragmentManager() != null){
                            HomePushDialog.getInstance().initPushDialog(entity).show(getFragmentManager(),"TAG-A");
                        }
                    }
                });
    }

    private void httpBanner(){
        RetrofitClient
                .getInstance(getActivity())
                .Api()
                .getBanners()
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        bannerDataEntity = JSON.parseObject(result.getData().toString(), BannerDataEntity.class);
                    }
                });
    }

    private void httpInitList(){
        RetrofitClient
                .getInstance(getActivity())
                .Api()
                .getProducts()
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        HomePageDataBeanNew entity = JSON.parseObject(result.getData().toString(), HomePageDataBeanNew.class);
                        if(entity != null){
                            JSONArray array = JSONObject.parseArray(entity.getChoices());
                            addExtraList(array);
                        }
                    }
                });
    }

    private void httpInitData(){
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getColumn("android")
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        dataEntity = JSONObject.parseObject(result.getData().toString(), DataBean.class);
                    }
                });
    }

    private void httpSeckill(){      //秒杀
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "6");
        params.put("pageNum", "1");
        RetrofitClient
                .getInstance(mContext)
                .Api()
                .getSecKillList(params)
                .enqueue(new RetrofitCallBacks<ResultEntity>(mRequestsListener) {
                    @Override
                    protected void onNext(ResultEntity result) {
                        SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                        seckillProductList = JSONObject.parseArray(seckillDataEntity.getPrdoucts().toString(), SeckillProductEntity.class);
                    }
                });
    }

    private void initHomeHead(){
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_home_head, null, false);
        new HeadViewHolder(itemView);
        mAdapter.addHeadView(itemView);
    }

    /**
     * Adapter 头部
     */
    class HeadViewHolder implements View.OnClickListener {
        @BindView(R.id.pcv_carouse)
        PagerCarouselView pcv_carouse;
        @BindView(R.id.main_home_entrance_vp)
        WrapContentHeightViewPager main_home_entrance_vp;
        @BindView(R.id.main_home_entrance_indicator)
        MagicIndicator main_home_entrance_indicator;
        @BindView(R.id.iv_welfare)
        ImageView iv_welfare;
        @BindView(R.id.iv_navigation_member)
        ImageView iv_navigation_member;
        @BindView(R.id.iv_navigation_jd)
        ImageView iv_navigation_jd;
        @BindView(R.id.iv_navigation_choiceness)
        ImageView iv_navigation_choiceness;
        @BindView(R.id.main_home_jd_entrance_vp)
        WrapContentHeightViewPager main_home_jd_entrance_vp;
        @BindView(R.id.main_home_jd_entrance_indicator)
        MagicIndicator main_home_jd_entrance_indicator;
        @BindView(R.id.tv_more)
        TextView tv_more;
        @BindView(R.id.rv_member)
        RecyclerView rv_member;
        @BindView(R.id.iv_image)
        ImageView iv_image;

        //导览 - 每页个数
        private int PageCount = 10;

        HeadViewHolder(View itemView){
            ButterKnife.bind(this,itemView);
            iv_navigation_jd.setOnClickListener(this);
            iv_navigation_choiceness.setOnClickListener(this);
            iv_navigation_member.setOnClickListener(this);
            initCarouse();
            initMainItem();
            initRecommend();
            initJd();
            initMember();
        }

        /**
         * 初始化首页轮播图
         */
        private void initCarouse(){
            if(bannerDataEntity == null)return;
            List<BannerImageEntity> top = bannerDataEntity.getTop();
            pcv_carouse.setImageList(top).initViews();
        }

        /**
         * 初始化商品栏
         */
        private void initMainItem(){
            if(dataEntity != null){
                List<ModelHomeEntranceBean> mainList = JSONObject.parseArray(dataEntity.getOther().toString(), ModelHomeEntranceBean.class);
                initColumn(mainList,main_home_entrance_vp,main_home_entrance_indicator,"MAIN");
            }
        }

        /**
         * 初始化推荐Banner
         */
        private void initRecommend(){
            if(bannerDataEntity != null && bannerDataEntity.getOptimal() != null){
                BannerDataEntity.OptimalBean optimal = bannerDataEntity.getOptimal();
                if(optimal.getJd() != null){
                    String url = JudgeImageUrlUtils.isAvailable(optimal.getJd().getImageUrl());
                    Glide.with(mContext).load(url).into(iv_navigation_jd);
                }
                if(optimal.getOpt() != null){
                    String url = JudgeImageUrlUtils.isAvailable(optimal.getOpt().getImageUrl());
                    Glide.with(mContext).load(url).into(iv_navigation_choiceness);
                }
                if(optimal.getSec() != null){
                    String url = JudgeImageUrlUtils.isAvailable(optimal.getSec().getImageUrl());
                    Glide.with(mContext).load(url).into(iv_navigation_member);
                }
            }
        }

        /**
         * 初始化京东专区
         */
        private void initJd(){
            if(dataEntity != null){
                List<ModelHomeEntranceBean> mainList = JSONObject.parseArray(dataEntity.getJd().toString(), ModelHomeEntranceBean.class);
                initColumn(mainList,main_home_jd_entrance_vp,main_home_jd_entrance_indicator,"JD");
            }
        }

        /**
         * 初始化新会员专区
         */
        private void initMember(){
            rv_member.setVisibility(View.VISIBLE);
            rv_member.setLayoutManager(new GridLayoutManager(mContext,2));
            if(seckillProductList != null){
                rv_member.setAdapter(new HomeMemberAdapter(seckillProductList,mContext));
            }
        }

        /**
         * 初始化导览Item
         */
        private void initColumn(List<ModelHomeEntranceBean> list, final WrapContentHeightViewPager viewPager, MagicIndicator magicIndicator, String flag){
            viewPager.setVisibility(View.VISIBLE);
            if(list != null && list.size() > 0){
                int pageCount = (int) Math.ceil( list.size() * 1.0 / PageCount );
                magicIndicator.setVisibility(pageCount > 1 ? View.VISIBLE : View.INVISIBLE);
                List<View> viewList = new ArrayList<>();
                for(int i = 0 ; i < pageCount ; i++) {
                    RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(mContext).inflate(R.layout.item_hone_entrance_vp, viewPager, false);
                    //允许两行
                    GridLayoutManager manager = new GridLayoutManager(mContext, PageCount / 2);
                    recyclerView.setLayoutManager(manager);
                    EntranceAdapter entranceAdapter = new EntranceAdapter(mContext, list, i, PageCount);
                    recyclerView.setAdapter(entranceAdapter);
                    viewList.add(recyclerView);
                }
                CagegoryViewPagerAdapter adapter = new CagegoryViewPagerAdapter(viewList);
                viewPager.setAdapter(adapter);
                CircleNavigator circleNavigator = new CircleNavigator(mContext);
                circleNavigator.setCircleCount(viewList.size());
                circleNavigator.setCircleColor(mContext.getResources().getColor(R.color.vs_blue));
                circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
                    @Override
                    public void onClick(int index) {
                        viewPager.setCurrentItem(index);
                    }
                });
                magicIndicator.setNavigator(circleNavigator);
                ViewPagerHelper.bind(magicIndicator, viewPager);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
