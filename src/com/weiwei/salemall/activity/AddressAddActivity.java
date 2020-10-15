package com.weiwei.salemall.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.area.ArrayWheelAdapter;
import com.weiwei.base.area.OnWheelChangedListener;
import com.weiwei.base.area.WheelView;
import com.weiwei.base.common.VsUtil;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/5/24.
 *         新增收货地址
 */

public class AddressAddActivity extends VsBaseActivity implements View.OnClickListener, OnWheelChangedListener, PopupWindow.OnDismissListener {
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.tv_btn_right)
    TextView navRightTv;
    @BindView(R.id.ex_name)
    EditText nameEt;
    @BindView(R.id.ex_phone)
    EditText phoneTv;
    @BindView(R.id.ex_address)
    EditText detailAddressTv;
    @BindView(R.id.ex_provinces)
    EditText provinceEt;
    @BindView(R.id.ck_default)
    ToggleButton defaultAddressTb;

    private Button btn_ok;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private WheelView mViewTown;
    private ImageView iv_close;
    private static final String TAG = "AddressAddActivity";
    private PopupWindow popupWindow;
    private String Id;
    private int isDefault = 0;
    private String editAddressUrl;
    private String msg_toast;
    private String name;
    private String phone;
    private String detailAddress;
    private String address;
    private int defaultAddress;
    private String currentProvince;
    private String currentCity;
    private String currentArea;
    private String currentTown;

    private String flag;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        navRightTv.setText(R.string.btn_save);
        loadingDialog = new CustomProgressDialog(this, "新增中，请稍后", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        backRl.setOnClickListener(this);
        defaultAddressTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDefault = 1;
                } else {
                    isDefault = 0;
                }
            }
        });

        navRightTv.setOnClickListener(this);
        provinceEt.setOnClickListener(this);
    }

    private void initListener() {

    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            Id = bundle.getString("id");
            if (!Id.equals("")) {
                getAddressInfo(Id);
                title.setText(R.string.edit_address);
                flag = "edit";
            }
        } else {
            title.setText(R.string.add_address);
            flag = "add";
        }
    }

    /**
     * 根据地址id 请求地址详情
     *
     * @param id
     */
    private void getAddressInfo(String id) {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getDeliveryDetail(id);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "地址详情msg===>" + result.getData().toString());
                    AddressEntity addressDataEntity = JSON.parseObject(result.getData().toString(), AddressEntity.class);
                    name = addressDataEntity.getName();
                    phone = addressDataEntity.getPhone();
                    detailAddress = addressDataEntity.getAddress();
                    currentProvince = addressDataEntity.getProvince();
                    currentCity = addressDataEntity.getCity();
                    currentArea = addressDataEntity.getArea();
                    address = currentProvince + currentCity + currentArea;
                    defaultAddress = addressDataEntity.getIsDefault();

                    nameEt.setText(name);
                    phoneTv.setText(phone);
                    detailAddressTv.setText(detailAddress);
                    provinceEt.setText(address);
                    defaultAddressTb.setChecked(defaultAddress == 1 ? true : false);
                } else {
                    mToast.show(result.getMsg());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ex_provinces:
                hideInputKeyBord(view);
                showSelectAreaPop(view);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_btn_right:
                check(Id, isDefault);
                break;
            default:
                break;
        }
    }

    private void hideInputKeyBord(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    private void showSelectAreaPop(View v) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(this);
        setOnPopupViewClick(view);
        setBackgroundAlpha(0.5f);
    }

    private void setOnPopupViewClick(View view) {
        mCurrentTownName = "";   //默认无
        setUpViews(view);
        setUpData();
    }

    private void setUpViews(View view) {
        mViewProvince = (WheelView) view.findViewById(R.id.id_province);
        mViewCity = (WheelView) view.findViewById(R.id.id_city);
        mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
        mViewTown = (WheelView) view.findViewById(R.id.id_town);
        iv_close = (ImageView) view.findViewById(R.id.iv_close);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);

        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);
        mViewTown.addChangingListener(this);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provinceEt.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName + mCurrentTownName);
                popupWindow.dismiss();
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        mViewTown.setVisibleItems(7);
        updateCities();
        updateAreas();
        updateTowns();
    }

    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);
        updateTowns();
    }

    private void updateTowns() {
        int pCurrent = mViewDistrict.getCurrentItem();
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[pCurrent];
        String[] towns = mTownDatasMap.get(mCurrentDistrictName);
        if (towns == null || towns.length == 0) {
            towns = new String[]{""};
            mCurrentTownName = "";
        } else {
            mCurrentTownName = mTownDatasMap.get(mCurrentDistrictName)[0];
        }
        mViewTown.setViewAdapter(new ArrayWheelAdapter<String>(this, towns));
        mViewTown.setCurrentItem(0);
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    private void check(String id, int isDefault) {
        // TODO Auto-generated method stub
        String name = nameEt.getText().toString().trim();
        String phone = phoneTv.getText().toString().trim();
        String address = detailAddressTv.getText().toString().trim();
        String provinces = provinceEt.getText().toString().trim();
        if (name.length() == 0) {
            Toast.makeText(AddressAddActivity.this, "请输入收货人", Toast.LENGTH_LONG).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(AddressAddActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
            return;
        }
        if (address.length() == 0) {
            Toast.makeText(AddressAddActivity.this, "请输入收详细地址", Toast.LENGTH_LONG).show();
            return;
        }
        if (provinces.length() == 0) {
            Toast.makeText(AddressAddActivity.this, "请选择省份", Toast.LENGTH_LONG).show();
            return;
        }
        saveAddress(name, phone, address, id, isDefault);
    }

    /**
     * 保存地址/修改地址
     *
     * @param name
     * @param phone
     * @param address
     * @param id
     * @param isDefault
     */
    private void saveAddress(String name, String phone, String address, String id, int isDefault) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("phone", phone);
        if (StringUtils.isEmpty(mCurrentProviceName)) {
            mCurrentProviceName = currentProvince;
        }
        param.put("province", mCurrentProviceName);

        if (StringUtils.isEmpty(mCurrentCityName)) {
            mCurrentCityName = currentCity;
        }
        param.put("city", mCurrentCityName);
        if (StringUtils.isEmpty(mCurrentDistrictName)) {
            mCurrentDistrictName = currentArea;
        }
        param.put("area", mCurrentDistrictName);
        param.put("town", mCurrentTownName);
        param.put("address", address);
        param.put("isDefault", String.valueOf(isDefault));

        if (id != null) {
            param.put("id", id);
        }

        retrofit2.Call<ResultEntity> call = null;
        if (flag.equals("add")) {
            call = api.addDelivery(param);
        } else if (flag.equals("edit")) {
            call = api.editDelivery(param);
        }
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                msg_toast = result.getMsg();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "添加地址msg===>" + msg_toast);
                    new AlertDialog.Builder(AddressAddActivity.this).
                            setMessage(msg_toast).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                } else {
                    mToast.show(result.getMsg());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {   //省
            updateCities();
        } else if (wheel == mViewCity) {  //市
            updateAreas();
        } else if (wheel == mViewDistrict) {  //县
            updateTowns();
        } else if (wheel == mViewTown) {     //区镇
            mCurrentTownName = mTownDatasMap.get(mCurrentDistrictName)[newValue];
            Log.e(TAG, mCurrentTownName);
        }
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }
}
