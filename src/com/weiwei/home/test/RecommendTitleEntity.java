package com.weiwei.home.test;

import com.weiwei.home.entity.ReadyItemRecommendEntity;

/**
 * @author : hc
 * @date : 2019/5/8.
 * @description:
 */

public class RecommendTitleEntity implements ReadyItemRecommendEntity{
    @Override
    public String getName() {
        return "精选";
    }

    @Override
    public String getExplain() {
        return "你猜是什么";
    }
}
