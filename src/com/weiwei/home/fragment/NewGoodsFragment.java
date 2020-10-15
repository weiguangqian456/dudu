package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.NewGoodsAdapter;
import com.weiwei.salemall.base.Const;

/**
 * @author hc
 * @date by hc on 2019/2/27.
 * @description: 新的商品采购 Fragment
 */

public class NewGoodsFragment extends BaseRecycleFragment {

    public static Fragment newInstance(Bundle bundle) {
        NewGoodsFragment fragment = new NewGoodsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new NewGoodsAdapter(mContext,getArgumentsInfo("columnId"),"n");
    }

    @Override
    protected String getPath() {
        String classificationFlag = getArgumentsInfo("classificationFlag");
        return Const.SHOP_MODEL + "childColumns/dataByColumn/"+ classificationFlag;
    }

}
