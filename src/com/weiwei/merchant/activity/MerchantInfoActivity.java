package com.weiwei.merchant.activity;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.VsMyInfoTextActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.merchant.bean.MerchantInfo;
import com.weiwei.merchant.bean.User;
import com.weiwei.rider.activity.RiderCenterActivity;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.bean.RiderAccount;
import com.weiwei.rider.bean.RiderInfo;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantInfoActivity extends RiderBaseActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_lave)
    TextView tvLave;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_total)
    TextView tvTotal;

    private String uid,phone,riderId;
    private Intent intent;
    private RiderAccount account;
    private ArrayList<String> nameList = new ArrayList<String>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant_info;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(false).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
    }

    @Override
    protected void initData() {
        uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        phone = VsUserConfig.getDataString(this,VsUserConfig.JKey_PhoneNumber);

        getAccount();
        getInfo();

    }

    private void getAccount() {
        RetrofitClient.getInstance(this).Api()
                .merchantAccount(uid,phone)
                .enqueue(new Callback<RiderAccount>() {
                    @Override
                    public void onResponse(Call<RiderAccount> call, Response<RiderAccount> response) {
                       if(response.isSuccessful() && response.body() != null) {
                           LogUtils.e("RiderAccount:",response.body().toString());
                           account = response.body();
                           setAccount(account.getTotalRedPacket());

                       }
                    }

                    @Override
                    public void onFailure(Call<RiderAccount> call, Throwable t) {

                    }
                });
    }

    private void setAccount(String totalRedPacket) {
        tvTotal.setText(totalRedPacket);
    }

    private void getInfo() {
        RetrofitClient.getInstance(this).Api()
                .merchantInfo(phone)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        if(response.isSuccessful() && response.body().getData() != null) {
                            MerchantInfo info = JSON.parseObject(response.body().getData().toString(),MerchantInfo.class);
                            LogUtils.e("info:",info.toString());
                            setInfo(info);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {

                    }
                });
    }

    private void setInfo(MerchantInfo info) {
        tvName.setText(info.getAgentName());
        tvPhone.setText(info.getPhone());

        if(info.getUser() != null && info.getUser().size() > 0) {
            for (User user : info.getUser()) {
                nameList.add(user.getUsername());
            }
        }
    }

    @OnClick({R.id.iv_back,R.id.myself_head,R.id.rl_withdraw,R.id.rl_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.myself_head:
                intent = new Intent(this,VsMyInfoTextActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_withdraw:
                intent = new Intent(this,RiderCenterActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            case R.id.rl_order:
                intent = new Intent(this,MerchantOrderActivity.class);
                intent.putStringArrayListExtra("username", nameList);
                startActivity(intent);
                break;
        }
    }
}
