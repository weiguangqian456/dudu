package com.weiwei.home.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.DiscoverAdapter;
import com.weiwei.salemall.base.Const;

/**
 * @author : hc
 * @date : 2019/5/16.
 * @description: 发现好货
 */

public class DiscoverFragment extends BaseRecycleFragment {

    public static Fragment newInstance(Bundle bundle) {
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new DiscoverAdapter(mContext,getArgumentsInfo("ID"));
    }

    @Override
    protected String getPath() {
        return Const.SHOP_MODEL + "childColumns/dataByColumn/"+ getArgumentsInfo("ID");
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

}
