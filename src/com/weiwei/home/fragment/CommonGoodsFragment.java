package com.weiwei.home.fragment;

import com.weiwei.salemall.base.Const;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.CommonGoodsAdapter;

/**
 * @author : hc
 * @date : 2019/3/15.
 * @description: 普通的商品展示
 */

public class CommonGoodsFragment extends BaseRecycleFragment{

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new CommonGoodsAdapter(mContext,getArgumentsInfo("columnId"));
    }

    @Override
    protected String getPath() {
        return Const.SHOP_MODEL + "childColumns/dataByColumn/" + getArgumentsInfo("columnId");
    }

    @Override
    protected boolean isMonitorNetwork() {
        return true;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }
}
