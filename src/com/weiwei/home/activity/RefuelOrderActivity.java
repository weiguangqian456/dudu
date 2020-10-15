package com.weiwei.home.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.Constant;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.adapter.RefuelOrderAdapter;
import com.weiwei.home.base.BaseRecycleActivity;
import com.weiwei.home.base.BaseRecycleAdapter;
import com.weiwei.home.dialog.PromptDialog;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.RefuelOrder;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DensityUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefuelOrderActivity extends BaseRecycleActivity {

    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;

    private RefuelOrderAdapter mAdapter;
    private HolderView mHeaderView;
    private float mScrolledHeight;
    private int mAlphaHeight = 200;
    private PromptDialog mPromptDialog;
    Map<String, String> params = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_order;
    }

    @Override
    protected boolean isShowError() {
        return true;
    }

    @Override
    protected BaseRecycleAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected String getPath() {
        return "benefit/web/CheZhuBangController/queryOrderInfo";
    }

    @Override
    protected String getRequestType() {
        return RECYCLE_TYPE_REFUEL_ORDER;
    }

    @Override
    protected Map<String, String> getParams() {
       params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

        params.put("orderStatusName", "");
        return params;
    }

    @Override
    protected void initView() {

        mTvTitle.setText("加油订单");
        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();

        View headerView = LayoutInflater.from(this).inflate(R.layout.header_refuel_order, null, false);
        mHeaderView = new HolderView(headerView);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mErrorView.getLayoutParams();
        layoutParams.topMargin = DensityUtils.dp2px(this, 287);
        mErrorView.setLayoutParams(layoutParams);
        mErrorView.setShowView();

        mPromptDialog = new PromptDialog(this);
        mPromptDialog.widthScale(0.8f);
        mPromptDialog.setTitle("开具发票")
                .setContent("请直接拨打客服电话：400-0365-388  来开具发票。");

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mScrolledHeight += dy;
                float alpha = (mScrolledHeight > mAlphaHeight ? 1 : mScrolledHeight / mAlphaHeight);
                mTvTitleBackground.setAlpha(alpha);

            }
        });

        mAdapter = new RefuelOrderAdapter(this);
        mAdapter.addHeadView(headerView);

        mAdapter.setmRecycleItemClick(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                RefuelOrder refuelOrder = JSON.parseObject(mAdapter.getItem(position).toString(), RefuelOrder.class);
              //  RefuelOrderDetailActivity.start(RefuelOrderActivity.this, refuelOrder);
                Bundle bundle = new Bundle();
                bundle.putSerializable("REFUEL_ORDER", refuelOrder);
                Intent intent = new Intent(RefuelOrderActivity.this, RefuelOrderDetailActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);

            }
        });

        mHeaderView.mtallText.setOnClickListener(onClickListener);
        mHeaderView.mTtextpaid.setOnClickListener(onClickListener);
        mHeaderView.mTreimburset.setOnClickListener(onClickListener);

        super.initView();

    }



    @Override
    public void initRefresh() {
        super.initRefresh();
    }

    @Override
    public void onHttpFinish() {
        super.onHttpFinish();
        if (getRefuelOrderList() != null) {
            DecimalFormat df = new DecimalFormat("#.00");//保留两位小数

            mHeaderView.mTvAccumulateConsumption.setText(getRefuelOrderList().amountPaySum);
            mHeaderView.mTvTvAccumulateRefuel.setText(getRefuelOrderList().litreSum);
        }
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IDStatView();
            switch (v.getId()) {

                case R.id.allText:
                    mHttpPage=1;
                    mAdapter.initList();
                    mHeaderView.mtallText.setBackgroundResource(R.drawable.textback_bg_le);
                    mHeaderView.mtallText.setTextColor(Color.parseColor("#ffffff"));
                    orderStatusName ="";
                    initRefresh();


                    break;
                case R.id.textpaid:
                    mHttpPage=1;
                    mAdapter.initList();
                    mHeaderView.mTtextpaid.setBackgroundColor(Color.parseColor("#EC6941"));
                    mHeaderView.mTtextpaid.setTextColor(Color.parseColor("#ffffff"));
//                    loadData( "已支付");
                    orderStatusName ="已支付";
                    initRefresh();
                    break;

                case R.id.reimbursetext:
                    mHttpPage=1;
                    mAdapter.initList();
                    mHeaderView.mTreimburset.setBackgroundResource(R.drawable.textback_bg_re);
                    mHeaderView.mTreimburset.setTextColor(Color.parseColor("#ffffff"));
                 //   loadData("已退款");
                    orderStatusName ="已退款";
                    initRefresh();

                    break;
            }
        }
    };

    @OnClick({R.id.fl_back, R.id.tv_invoice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_invoice:
                mPromptDialog.show();
                break;
        }
    }

    public void IDStatView() {
        mHeaderView.mtallText.setBackgroundResource(R.drawable.textback_bg_le1);
        mHeaderView.mTtextpaid.setBackgroundColor(Color.parseColor("#EEEEEE"));
        mHeaderView.mTreimburset.setBackgroundResource(R.drawable.textback_bg_re2);

        mHeaderView.mtallText.setTextColor(Color.parseColor("#000000"));
        mHeaderView.mTtextpaid.setTextColor(Color.parseColor("#000000"));
        mHeaderView.mTreimburset.setTextColor(Color.parseColor("#000000"));

    }

    class HolderView {

        @BindView(R.id.tv_accumulate_consumption)
        TextView mTvAccumulateConsumption;
        @BindView(R.id.tv_tv_accumulate_refuel)
        TextView mTvTvAccumulateRefuel;
        @BindView(R.id.allText)
        TextView mtallText;
        @BindView(R.id.textpaid)
        TextView mTtextpaid;
        @BindView(R.id.reimbursetext)
        TextView mTreimburset;

        HolderView(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2){
            mAdapter.initList();
            initRefresh();
        }
    }
}
