package com.weiwei.home.yd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.widgets.CustomToast;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.adapter.ClassifyGoodsAdapter;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/5/13.
 * @description:
 */

public class YDClassifyItemFragment extends BaseRecycleFragment {

    @BindView(R.id.nsv_content)
    NestedScrollView nsv_content;

    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.tv_title_1)
    TextView tv_title_1;

    public static BaseRecycleFragment newInstance(Bundle bundle) {
        YDClassifyItemFragment fragment = new YDClassifyItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected Boolean isLayoutManager() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment__yd_classify_item;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new YDLocalItemAdapter(mContext);
    }

    @Override
    protected String getPath() {
        String id = getArgumentsInfo("id");
        return Const.SHOP_XX + "childColumns/dataByColumnByLocation/"+ id;
    }

    @Override
    protected Map<String, String> getParams() {
        String longitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLon");
        String latitude = PreferencesUtils.getString(VsApplication.getContext(), "currentLocationLat");
        if(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(latitude)){
            longitude="113.945527";
            latitude="22.571866";
        }
        Map<String,String> map = new HashMap<>();
        map.put("currentLon",longitude);
        map.put("currentLat",latitude);
        return map;
    }

    @Override
    protected void initView() {
        super.initView();
        View emptyView = mErrorView.getEmptyView();
        ((TextView)emptyView.findViewById(R.id.tv_no_content)).setText("附近暂时没有商家入驻\n敬请期待");
        RequestOptions options = RequestOptions
                .bitmapTransform(new RoundedCorners(10))
                .placeholder(R.drawable.image_classify_banner);
        Glide.with(mContext).load(R.drawable.image_classify_banner).apply(options).into(iv_image);
        String title = (TextUtils.isEmpty(getArgumentsInfo("TITLE")) ? "精选商品" : getArgumentsInfo("TITLE"));
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimBackActivity.launch(mContext, SimBackEnum.RECRUIT_REGION,null);
            }
        });

        tv_title_1.setText(title);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected NestedScrollView getScrollView() {
        return nsv_content;
    }
}
