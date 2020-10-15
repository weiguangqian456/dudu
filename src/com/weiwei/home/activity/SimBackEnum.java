package com.weiwei.home.activity;

import android.support.v4.app.Fragment;
import com.weiwei.base.fragment.SignIntegralFragment;
import com.weiwei.home.fragment.ChoicesFragment;
import com.weiwei.home.fragment.CommonGoodsFragment;
import com.weiwei.home.fragment.GoodsListFragment;
import com.weiwei.home.fragment.MemberIntroduceFragment;
import com.weiwei.home.fragment.MemberIntroduceFragment;
import com.weiwei.home.fragment.MemberRegionFragment;
import com.weiwei.home.yd.YDLocalFragment;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description:
 */

public enum SimBackEnum {
    MINE_INTEGRAL(0, "每日签到",SignIntegralFragment.class),
    RECRUIT_REGION(1,"新品首发",MemberIntroduceFragment.class),
    COMMON_GOODS(2,"商品区",CommonGoodsFragment.class),
    CHOICES_GOODS(3,"今日爆款",ChoicesFragment.class),
    RECRUIT_LIST(4,"新会员专区",MemberRegionFragment.class),
    RECOMMEND_LIST(5,"嘟嘟推荐",GoodsListFragment.class),
    LOCAL_INFO(6,"本地当日达",YDLocalFragment.class);

    private int value;
    private String title;
    private Class<? extends Fragment> clazz;

    SimBackEnum(int value,String title,Class<? extends Fragment> clazz){
        this.value = value;
        this.title = title;
        this.clazz = clazz;
    }

    public int getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends Fragment> getClazz() {
        return clazz;
    }
}
