package com.weiwei.rider.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.image.StringUtils;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
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

import static com.weiwei.base.application.VsApplication.mContext;

public class JoinRiderActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.et_address_detail)
    EditText etAddressDetail;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_mobile)
    EditText etMobile;

    private String cityName,cityCode,addressDetail,name,phone,uid,userPhone;
    private Map<String,String> params;
    private Intent intent;
    private static int REQUESTCODE = 1;
    private static int RESULTCODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join_rider;
    }

    @Override
    protected void initView() {
        tvTitle.setText("快速报名通道");

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.rl_back,R.id.btn_rider,R.id.iv_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_rider:
                signUp();
                break;
            case R.id.iv_location:
                intent = new Intent(JoinRiderActivity.this,WorkLocationActivity.class);
                startActivityForResult(intent,REQUESTCODE);
                break;
        }
    }

    private void signUp() {
        name = etName.getText().toString();
        phone = etMobile.getText().toString();
        addressDetail = etAddressDetail.getText().toString();
        uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        userPhone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        if(!TextUtils.isEmpty(cityName)) {
            if(!TextUtils.isEmpty(addressDetail)) {
                if(!TextUtils.isEmpty(name)) {
                    if(!TextUtils.isEmpty(phone) && StringUtils.validateMobilePhone(phone)) {
                        loadingDialog.setLoadingDialogShow();
                        params = new HashMap<>();
                        params.put("name",name);
                        params.put("phone",phone);
                        params.put("uid",uid);
                        params.put("workCity",cityName);
                        params.put("workingPlace",addressDetail);
                        params.put("userPhone",userPhone);
                        params.put("appId","dudu");
//                        LogUtils.e("请求参数：",params.toString());
                        Gson gson = new Gson();
                        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

                        RetrofitClient.getInstance(this)
                                .Api().signUp(body)
                                .enqueue(new Callback<ResultEntity>() {
                                    @Override
                                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                                        loadingDialog.setLoadingDialogDismiss();
                                        LogUtils.e("response:",response.toString());
                                        if(response.isSuccessful() && response.code() == 200) {
                                            ToastUtils.show(JoinRiderActivity.this,"报名成功");
                                            finish();
                                        }else {
                                            ToastUtils.show(JoinRiderActivity.this,"报名失败:" + response.message());
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                                        loadingDialog.setLoadingDialogDismiss();
                                        ToastUtils.show(JoinRiderActivity.this,"报名失败:" + t.getMessage());
                                    }
                                });
                    }else {
                        ToastUtils.show(this,"手机号码错误");
                    }
                }else {
                    ToastUtils.show(this,"请填姓名");
                }
            }else {
                ToastUtils.show(this,"请填写意向工作城市");
            }
        }else {
            ToastUtils.show(this,"请选择意向工作地点");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCODE && resultCode == RESULTCODE && data != null) {
            cityName = data.getStringExtra("cityName");
            cityCode = data.getStringExtra("cityCode");
            tvAddress.setText(cityName);
        }
    }
}
