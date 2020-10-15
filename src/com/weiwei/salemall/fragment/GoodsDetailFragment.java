package com.weiwei.salemall.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by EDZ
 * item页ViewPager里的详情Fragment
 */
public class GoodsDetailFragment extends Fragment implements View.OnClickListener {
    private LinearLayout ll_goods_detail, ll_goods_config;
    private TextView tv_goods_detail, tv_goods_config;
    private FrameLayout fl_content;
    private View v_tab_cursor;

    private int nowIndex;
    private float fromX;
    private List<TextView> tabTextList;
    private GoodsDetailActivity activity;
    private GoodsConfigFragment goodsConfigFragment;
    private GoodsDetailWebFragment goodsDetailWebFragment;
    private Fragment nowFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private View rootView;

    private static final String TAG = "GoodsDetailFragment";
    private String productNo;
    private String columnId;
    private String seckillProductId; //秒杀商品
    private String mSeckill; //嘟嘟秒杀商品

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (GoodsDetailActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goods_detail, null);
        initView();
        initListener();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            productNo = bundle.getString("productNo");
            if (StringUtils.isEmpty(productNo)) {
                productNo = "";
            }
            columnId = bundle.getString("columnId");
            if (StringUtils.isEmpty(columnId)) {
                columnId = "";
            }
            seckillProductId = bundle.getString("seckillProductId");
            if (StringUtils.isEmpty(seckillProductId)) {
                seckillProductId = "";
            }

            mSeckill = bundle.getString("seckill");

            Log.e(TAG, "商品详情界面msg===>" + productNo + "columnId===>" + columnId);
            setData();
        }
    }

    private void initListener() {
        ll_goods_detail.setOnClickListener(this);
        ll_goods_config.setOnClickListener(this);
    }

    private void initView() {
        ll_goods_detail = (LinearLayout) rootView.findViewById(R.id.ll_goods_detail);
        ll_goods_config = (LinearLayout) rootView.findViewById(R.id.ll_goods_config);
        tv_goods_detail = (TextView) rootView.findViewById(R.id.tv_goods_detail);
        tv_goods_config = (TextView) rootView.findViewById(R.id.tv_goods_config);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
        v_tab_cursor = rootView.findViewById(R.id.v_tab_cursor);

        tabTextList = new ArrayList<>();
        tabTextList.add(tv_goods_detail);
        tabTextList.add(tv_goods_config);
    }

    /**
     * 商品信息Fragment页获取完数据执行
     */
    public void setData() {
        goodsConfigFragment = GoodsConfigFragment.newInstance(productNo, columnId, seckillProductId);
        goodsDetailWebFragment = GoodsDetailWebFragment.newInstance(productNo,mSeckill, columnId, seckillProductId);
        nowFragment = goodsDetailWebFragment;
        fragmentManager = getChildFragmentManager();
        //默认显示商品详情tab
        fragmentManager.beginTransaction().replace(R.id.fl_content, nowFragment).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_goods_detail:
                //商品详情tab
                switchFragment(nowFragment, goodsDetailWebFragment);
                nowIndex = 0;
                nowFragment = goodsDetailWebFragment;
                scrollCursor();
                break;
            case R.id.ll_goods_config:
                //规格参数tab
                switchFragment(nowFragment, goodsConfigFragment);
                nowIndex = 1;
                nowFragment = goodsConfigFragment;
                scrollCursor();
                break;
            default:
                break;
        }
    }

    /**
     * 滑动游标
     */
    private void scrollCursor() {
        TranslateAnimation anim = new TranslateAnimation(fromX, nowIndex * v_tab_cursor.getWidth(), 0, 0);
        //设置动画结束时停在动画结束的位置
        anim.setFillAfter(true);
        anim.setDuration(50);
        //保存动画结束时游标的位置,作为下次滑动的起点
        fromX = nowIndex * v_tab_cursor.getWidth();
        v_tab_cursor.startAnimation(anim);

        //设置Tab切换颜色
        for (int i = 0; i < tabTextList.size(); i++) {
            tabTextList.get(i).setTextColor(i == nowIndex ? getResources().getColor(R.color.public_color_EC6941) : getResources().getColor(R.color.vs_black));
        }
    }

    /**
     * 切换Fragment
     * <p>(hide、show、add)
     *
     * @param fromFragment
     * @param toFragment
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
        if (nowFragment != toFragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            // 先判断是否被add过
            if (!toFragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到activity中
                fragmentTransaction.hide(fromFragment).add(R.id.fl_content, toFragment).commitAllowingStateLoss();
            } else {
                // 隐藏当前的fragment，显示下一个
                fragmentTransaction.hide(fromFragment).show(toFragment).commitAllowingStateLoss();
            }
        }
    }
}