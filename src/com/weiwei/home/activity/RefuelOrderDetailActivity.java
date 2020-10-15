package com.weiwei.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.nostra13.universalimageloader.utils.L;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.dialog.LoadingDialog;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.bean.RefuelOrder;
import com.weiwei.salemall.bean.ResultEntity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class RefuelOrderDetailActivity extends BaseActivity {

    @BindView(R.id.fl_back)
    FrameLayout mFlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_order_id)
    TextView mTvOrderId;
    @BindView(R.id.tv_oil_station_name)
    TextView mTvOilStationName;
    @BindView(R.id.tv_oil_station_address)
    TextView mTvOilStationAddress;
    @BindView(R.id.tv_oil_no)
    TextView mTvOilNo;
    @BindView(R.id.tv_gun_no)
    TextView mTvGunNo;
    @BindView(R.id.tv_refuel_sum)
    TextView mTvRefuelSum;
    @BindView(R.id.tv_refuel_litre)
    TextView mTvRefuelLitre;
    @BindView(R.id.tv_discounts)
    TextView mTvDiscounts;
    @BindView(R.id.tv_pay_way)
    TextView mTvPayWay;
    @BindView(R.id.tv_pay_sum)
    TextView mTvPaySum;
    @BindView(R.id.tv_pay_time)
    TextView mTvPayTime;
    @BindView(R.id.tv_pay_money)
    TextView mTvPayMoney;

    @BindView(R.id.textpay)
    TextView textpay;
    @BindView(R.id.textmes)
    TextView textmes;
    @BindView(R.id.textesc)
    TextView textesc;

    @BindView(R.id.paytextec)
    TextView paytextec;
    private LoadingDialog mLoadingDialog;

    public static void start(Context context, RefuelOrder refuelOrder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("REFUEL_ORDER", refuelOrder);

        ArmsUtils.startActivity(context, RefuelOrderDetailActivity.class, bundle);

    }

    private RefuelOrder mRefuelOrder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_order_detail;
    }


    @Override
    protected void initView() {
        mTvTitle.setText("订单详情");
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setCancelable(true);
        mRefuelOrder = (RefuelOrder) getIntent().getSerializableExtra("REFUEL_ORDER");

        if (mRefuelOrder != null) {
            mTvOrderId.setText(mRefuelOrder.orderId);
            mTvOilStationName.setText(mRefuelOrder.gasName);
            mTvOilStationAddress.setText(mRefuelOrder.province + mRefuelOrder.city + mRefuelOrder.county);
            mTvOilNo.setText(mRefuelOrder.oilNo);
            mTvGunNo.setText(mRefuelOrder.gunNo + "号枪");
            mTvRefuelSum.setText("￥" + mRefuelOrder.amountGun);
            mTvRefuelLitre.setText(mRefuelOrder.litre + "升");
            mTvDiscounts.setText("￥" + mRefuelOrder.amountDiscounts);
            mTvPayWay.setText(mRefuelOrder.payType);
            mTvPaySum.setText("￥" + mRefuelOrder.amountPay);
            mTvPayTime.setText(mRefuelOrder.payTime);
            mTvPayMoney.setText("￥" + mRefuelOrder.amountPay);
            textpay.setText("订单" + mRefuelOrder.orderStatusName);


            switch (mRefuelOrder.orderStatusName) {
                case "已支付":
                    textesc.setVisibility(View.GONE);
                    break;
                case "已退款":
                    textesc.setVisibility(View.GONE);
                    textmes.setText("订单" + mRefuelOrder.orderStatusName + "，可重新下单");
                    break;
                default:
                    paytextec.setText("订单未支付");
                    textmes.setText("订单" + mRefuelOrder.orderStatusName + "，可重新下单");
            }

        }


    }

    @OnClick({R.id.fl_back, R.id.textesc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.textesc:
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog.show();
                }

                delindent();
                break;
        }
    }

    public void delindent() {
        String url = "https://route.edawtech.com/benefit/web/CheZhuBangController/delete/";
        //https://route.edawtech.com/benefit/web/CheZhuBangController/delete/NP469352336586429675e3
        OkHttpUtils.post().url(url + mRefuelOrder.orderId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {

            }

            @Override
            public void onResponse(String s, int i) {
                ResultEntity resultEntity = new Gson().fromJson(s, ResultEntity.class);
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
                if (resultEntity.getCode() == 0) {
                    textesc.setVisibility(View.GONE);
                    ToastAstrictUtils.getInstance().show(resultEntity.getMsg());
                } else {
                    ToastAstrictUtils.getInstance().show(resultEntity.getMsg());
                }
                Log.e("delindent()", s);
            }
        });
    }
}
