package com.weiwei.rider.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.weiwei.base.activity.me.VsMyInfoTextActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.rider.bean.RiderAccount;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.bean.RiderInfo;
import com.weiwei.rider.utils.MyDialog;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RiderInfoActivity extends RiderBaseActivity implements OnAddressCallback{

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_lave)
    TextView tvLave;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_status)
    RelativeLayout rlStatus;

    private String uid,phone,riderId;
    private Intent intent;
    private RiderAccount account;
    private String address;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_info;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(false).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        rlStatus.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        phone = VsUserConfig.getDataString(this,VsUserConfig.JKey_PhoneNumber);
        initLocatedCity();
        getAccount();
        getInfo();

        address = PreferencesUtils.getString(this,"MY_ADDRESS");
        if(!TextUtils.isEmpty(address)) {
            tvAddress.setText(address);
        }
    }

    private void initLocatedCity() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        new BDLBSMapHelper().getAddressDetail(this, this);
    }


    /**
     * 获取骑手的账户信息
     */
    private void getAccount() {
        Map params = new HashMap();
        params.put("phone",phone);
        RetrofitClient.getInstance(this).Api()
                .getRiderAccount(uid,params)
                .enqueue(new Callback<RiderAccount>() {
                    @Override
                    public void onResponse(Call<RiderAccount> call, Response<RiderAccount> response) {
                        account = response.body();
                        setAccount(account);
                    }
                    @Override
                    public void onFailure(Call<RiderAccount> call, Throwable t) {
                        ToastUtils.show(RiderInfoActivity.this,t.getMessage());
                    }
                });
    }

    /**
     * 获取骑手的个人信息
     */
    private void getInfo() {

        RetrofitClient.getInstance(this).Api()
                .riderInfo(uid)
                .enqueue(new Callback<RiderInfo>() {
                    @Override
                    public void onResponse(Call<RiderInfo> call, Response<RiderInfo> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        if(response.isSuccessful() && response.body() != null) {
                            RiderInfo info = response.body();
                            riderId = info.getId().toString();
                            setRiderInfo(info);
                        }else {
                            ToastUtils.show(RiderInfoActivity.this,"数据获取失败");
                        }
                    }
                    @Override
                    public void onFailure(Call<RiderInfo> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(RiderInfoActivity.this,"数据获取失败");
                    }
                });
    }

    private void setRiderInfo(RiderInfo info) {
        tvName.setText(info.getName());
        tvPhone.setText(info.getPhone());
        if(info.getLave().equals("y")) {
            tvLave.setText("上班");
        }else {
            tvLave.setText("休息");
        }
    }

    private void setAccount(RiderAccount account) {
        tvTotal.setText(account.getTotalRedPacket());
    }


    @OnClick({R.id.iv_back,R.id.rl_order,R.id.rl_location,R.id.rl_setting,R.id.rl_withdraw,R.id.myself_head,
    R.id.rl_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_order:
                intent = new Intent(this,RiderOrderActivity.class);
                intent.putExtra("riderId",riderId);
                startActivity(intent);
                break;
            case R.id.rl_location:
                intent = new Intent(this,RiderLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting:
                intent = new Intent(this,RiderSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_withdraw:
                intent = new Intent(this,RiderCenterActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.myself_head:
                intent = new Intent(this, VsMyInfoTextActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_status:
                MyDialog dialog = new MyDialog();
                dialog.setOnClickekListener(new MyDialog.OnClickListener() {
                    @Override
                    public void itemOnline() {
                        updateStatus("y");
                    }

                    @Override
                    public void itemUnline() {
                        updateStatus("n");
                    }
                });
                FragmentManager fragmentManager = getFragmentManager();
                dialog.show(fragmentManager,"ONLINE");
                break;
        }
    }

    private void updateStatus(String type) {
        loadingDialog.setLoadingDialogShow();
        Map<String,String> params = new HashMap<>();
        params.put("id",riderId);
        params.put("lave",type);

        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(this).Api()
                .updateStatus(body)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        if(response.isSuccessful()) {
                            ToastUtils.show(RiderInfoActivity.this,"状态更改成功");
                            if(type.equals("y")) {
                                tvLave.setText("上班");
                            }else {
                                tvLave.setText("休息");
                            }
                        }
                        loadingDialog.setLoadingDialogDismiss();
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                    }
                });
    }

    @Override
    public void onAddressStart() {

    }

    @Override
    public void onAddressFail() {
        loadingDialog.setLoadingDialogDismiss();
    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {
        loadingDialog.setLoadingDialogDismiss();
        if(!this.isFinishing()) {
            PreferencesUtils.putString(RiderInfoActivity.this,"MY_ADDRESS",bdLocation.getStreet());
            tvAddress.setText(bdLocation.getAddrStr());
        }
    }

    @Override
    public void onAddressFinish() {

    }
}
