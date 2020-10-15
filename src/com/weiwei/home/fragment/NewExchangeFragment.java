package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.ExchangeRegionAdapter;
import static com.weiwei.salemall.base.Const.SHOP_MODEL;

/**
 * @author : hc
 * @date : 2019/3/4.
 * @description: 新的-兑换专区Fragment？
 */

public class NewExchangeFragment extends BaseRecycleFragment{

    public static Fragment newInstance(Bundle bundle) {
        NewExchangeFragment fragment = new NewExchangeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ExchangeRegionAdapter(mContext,getArgumentsInfo("columnId"));
    }

    @Override
    protected String getPath() {
        String classificationFlag = getArgumentsInfo("classificationFlag");
        return SHOP_MODEL + "childColumns/dataByColumn/" + classificationFlag;
    }

}
