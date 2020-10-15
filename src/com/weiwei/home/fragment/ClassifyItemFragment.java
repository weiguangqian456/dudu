package com.weiwei.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.adapter.ClassifyGoodsAdapter;
import com.weiwei.salemall.base.Const;
import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/5/13.
 * @description:
 */

public class ClassifyItemFragment extends BaseRecycleFragment {

    @BindView(R.id.nsv_content)
    NestedScrollView nsv_content;

    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.tv_title_1)
    TextView tv_title_1;

    public static Fragment newInstance(Bundle bundle) {
        ClassifyItemFragment fragment = new ClassifyItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify_item;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ClassifyGoodsAdapter(mContext,getArgumentsInfo("columnId"),"n");
    }

    @Override
    protected String getPath() {
        String classificationFlag = getArgumentsInfo("classificationFlag");
        return Const.SHOP_MODEL + "childColumns/dataByColumn/"+ classificationFlag;
    }

    @Override
    protected void initView() {
        super.initView();
        RequestOptions options = RequestOptions
                .bitmapTransform(new RoundedCorners(10))
                .placeholder(R.drawable.image_classify_banner);
        Glide.with(mContext).load(R.drawable.image_classify_banner).apply(options).into(iv_image);
        String title = (TextUtils.isEmpty(getArgumentsInfo("TITLE")) ? "精选商品" : getArgumentsInfo("TITLE")) + " · ";
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
