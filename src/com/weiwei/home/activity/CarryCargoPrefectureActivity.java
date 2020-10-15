package com.weiwei.home.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.home.adapter.ExchangeRegionAdapter;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.base.BaseRecycleActivity;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.base.BaseRecycleFragment;
import com.weiwei.home.entity.SepGoodsEntity;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.SHOP_MODEL;

public class CarryCargoPrefectureActivity extends BaseRecycleActivity implements View.OnClickListener {

    public static void start(Context context, String columnId) {
        Bundle bundle = new Bundle();
        bundle.putString("COLUMN_ID", columnId);
        ArmsUtils.startActivity(context, CarryCargoPrefectureActivity.class, bundle);
    }

    @BindView(R.id.fl_back)
    FrameLayout fl_back;
    @BindView(R.id.tv_title)
    TextView tv_title;


    private ExchangeRegionAdapter mAdapter;
    private String mColumnId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_carry_cargo_prefecture;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        tv_title.setText("提货专区");
        mColumnId = getIntent().getStringExtra("COLUMN_ID");

        fl_back.setOnClickListener(this);
        super.initView();
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return new ExchangeRegionAdapter(this, 1, mColumnId);
    }

    @Override
    protected String getPath() {
        return SHOP_MODEL + "product/page";
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("indexColumnId", "64024a67103e4a359c55028bac8f670d");
        return params;
    }

    @Override
    protected Boolean isLayoutManager() {
        return false;
    }

    @Override
    protected String getRequestType() {
        return BaseRecycleActivity.RECYCLE_TYPE_CHOICES;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
