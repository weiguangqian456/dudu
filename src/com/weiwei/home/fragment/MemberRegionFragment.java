package com.weiwei.home.fragment;

import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.MemberIntroduceBAdapter;
import static com.weiwei.salemall.base.Const.SHOP_MODEL;

/**
 * @author : hc
 * @date : 2019/3/11.
 * @description: 新会员专区
 */

public class MemberRegionFragment extends BaseRecycleFragment {

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new MemberIntroduceBAdapter(getActivity());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recruit_rigion;
    }

    @Override
    protected String getPath() {
        return SHOP_MODEL + "seckill/list";
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleFragment.RECYCLE_TYPE_SECKILL;
    }

    @Override
    protected boolean isMonitorNetwork() {
        return true;
    }
}
