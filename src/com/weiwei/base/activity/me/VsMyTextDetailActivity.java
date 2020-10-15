package com.weiwei.base.activity.me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.area.ArrayWheelAdapter;
import com.weiwei.base.area.OnWheelChangedListener;
import com.weiwei.base.area.WheelView;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.DateTimePickDialogUtil;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.TreeMap;

public class VsMyTextDetailActivity extends VsBaseActivity implements OnClickListener, OnWheelChangedListener {

    private EditText rl_my_text_oldName, rl_my_text_phoneName, rl_my_text_bir, rl_my_text_mailbox;
    private LinearLayout vs_detail_phonenum, vs_detail_name, vs_detail_sex, vs_detail_bir, vs_detail_area, area_wheel,
            vs_about_update, vs_detail_mailbox;
    private ImageView sexm, sexfem;
    private RelativeLayout rl_my_sexf, rl_my_sexm;
    private TextView rl_my_text_area;
    private String flag = "";
    private String name = "";

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_myinfo_text_detail);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        init();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DfineAction.ACTION_PUSHINFOSUCCESS);
        mContext.registerReceiver(getInfoReceiver, filter);

        Intent intent = super.getIntent();
        flag = intent.getStringExtra("flag");
        name = intent.getStringExtra("oldName");
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        if (flag.equals("name")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_name));
            vs_detail_name.setVisibility(View.VISIBLE);
            rl_my_text_oldName.setText(name);
            showRightTxtBtn("保存");
        } else if (flag.equals("phone")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_phone));
            vs_detail_phonenum.setVisibility(View.VISIBLE);
            rl_my_text_phoneName.setText(name);
            showRightTxtBtn("保存");
        } else if (flag.equals("sex")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_sex));
            vs_detail_sex.setVisibility(View.VISIBLE);
            if (name.equals("女")) {
                sexfem.setVisibility(View.VISIBLE);
                sexm.setVisibility(View.INVISIBLE);
            } else {
                sexfem.setVisibility(View.INVISIBLE);
                sexm.setVisibility(View.VISIBLE);
            }
        } else if (flag.equals("bir")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_bir));
            vs_detail_bir.setVisibility(View.VISIBLE);
            rl_my_text_bir.setText(name);
            showRightTxtBtn("保存");
        } else if (flag.equals("mailbox")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_mailbox));
            vs_detail_mailbox.setVisibility(View.VISIBLE);
            rl_my_text_mailbox.setText(name);
            showRightTxtBtn("保存");
        } else if (flag.equals("area")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_myinfotext_area));
            vs_detail_area.setVisibility(View.VISIBLE);
            area_wheel.setVisibility(View.VISIBLE);
            rl_my_text_area.setText(name);
            showRightTxtBtn("保存");
            // 城市地区数据
            setUpViews();
            setUpListener();
            setUpData();
        }

        showLeftNavaBtn(R.drawable.icon_back);
        VsApplication.getInstance().addActivity(this);
    }

    private void init() {
        vs_detail_phonenum = (LinearLayout) findViewById(R.id.vs_detail_phonenum);
        vs_detail_name = (LinearLayout) findViewById(R.id.vs_detail_name);
        vs_detail_sex = (LinearLayout) findViewById(R.id.vs_detail_sex);
        vs_detail_bir = (LinearLayout) findViewById(R.id.vs_detail_bir);
        vs_detail_area = (LinearLayout) findViewById(R.id.vs_detail_area);
        vs_detail_mailbox = (LinearLayout) findViewById(R.id.vs_detail_mailbox);
        area_wheel = (LinearLayout) findViewById(R.id.area_wheel);
        rl_my_text_area = (TextView) findViewById(R.id.rl_my_text_area);
        rl_my_sexf = (RelativeLayout) findViewById(R.id.rl_my_sexf);
        rl_my_sexm = (RelativeLayout) findViewById(R.id.rl_my_sexm);
        sexfem = (ImageView) findViewById(R.id.sexfem);
        sexm = (ImageView) findViewById(R.id.sexm);

        rl_my_text_oldName = (EditText) findViewById(R.id.rl_my_text_oldName);
        rl_my_text_phoneName = (EditText) findViewById(R.id.rl_my_text_phoneName);
        rl_my_text_mailbox = (EditText) findViewById(R.id.rl_my_text_mailbox);
        rl_my_text_bir = (EditText) findViewById(R.id.rl_my_text_bir);

        rl_my_sexm.setOnClickListener(this);
        rl_my_sexf.setOnClickListener(this);
        rl_my_text_bir.setOnClickListener(this);
    }

    // 导航栏右边按钮
    @Override
    protected void HandleRightNavBtn() {
        // 点击保存
        Intent intent = new Intent();
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            mToast.show("网络连接失败，请检查网络");
            return;
        }
        if (flag.equals("name")) {
            String name = rl_my_text_oldName.getText().toString();
            if (name.length() != 0) {
                intent.putExtra("name", name);
                // 本地保存
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Nickname, name);
                getMyInfo(uid, "nick", name);
                VsMyTextDetailActivity.this.setResult(1000, intent);
                VsMyTextDetailActivity.this.finish();
            } else {
                Toast.makeText(mContext, "昵称不能为空", Toast.LENGTH_SHORT).show();
            }

        } else if (flag.equals("bir")) {
            String bir = rl_my_text_bir.getText().toString();
            if (bir.length() != 0) {
                intent.putExtra("bir", bir);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Birth, bir);
                getMyInfo(uid, "birthday", bir);
                VsMyTextDetailActivity.this.setResult(3000, intent);
                VsMyTextDetailActivity.this.finish();
            } else {
                Toast.makeText(mContext, "生日不能为空", Toast.LENGTH_SHORT).show();
            }
        } else if (flag.equals("mailbox")) {
            String mailbox = rl_my_text_mailbox.getText().toString();
            if (mailbox.length() != 0) {
                if (!mailbox
                        .matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
                    Toast.makeText(mContext, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("mailbox", mailbox);
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_MailBox, mailbox);
                    getMyInfo(uid, "email", mailbox);
                    VsMyTextDetailActivity.this.setResult(5000, intent);
                    VsMyTextDetailActivity.this.finish();

                }
            } else {
                Toast.makeText(mContext, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            }
        } else if (flag.equals("area")) {
            String area = rl_my_text_area.getText().toString();
            if (area.length() != 0) {
                intent.putExtra("area", area);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Province, area);
                // getMyInfo(uid,"area",area);
                VsMyTextDetailActivity.this.setResult(4000, intent);
                VsMyTextDetailActivity.this.finish();
            } else {
                Toast.makeText(mContext, "地区不能为空", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                showSelectedResult();
                break;
            case R.id.rl_my_sexm:
                sexfem.setVisibility(View.INVISIBLE);
                sexm.setVisibility(View.VISIBLE);

                Intent intent = new Intent();
                intent.putExtra("sex", "男");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Gender, "男");
                getMyInfo(uid, "gender", "M");
                VsMyTextDetailActivity.this.setResult(2000, intent);
                VsMyTextDetailActivity.this.finish();
                break;

            case R.id.rl_my_sexf:
                sexfem.setVisibility(View.VISIBLE);
                sexm.setVisibility(View.INVISIBLE);
                Intent intentm = new Intent();
                intentm.putExtra("sex", "女");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Gender, "女");
                getMyInfo(uid, "gender", "F");
                VsMyTextDetailActivity.this.setResult(2000, intentm);
                VsMyTextDetailActivity.this.finish();
                break;
            case R.id.rl_my_text_bir:

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(VsMyTextDetailActivity.this,
                        name.replaceFirst("-", "年").replaceFirst("-", "月") + "日");
                dateTimePicKDialog.dateTimePicKDialog(rl_my_text_bir).setView(v, 0, 0, 0, 0);
                ;

                break;
        }
    }

    private void setUpViews() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    private void setUpListener() {

        mViewProvince.addChangingListener(this);

        mViewCity.addChangingListener(this);

        mViewDistrict.addChangingListener(this);

        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(VsMyTextDetailActivity.this, mProvinceDatas));

        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
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

    private void showSelectedResult() {
        rl_my_text_area.setText(mCurrentProviceName + " " + mCurrentCityName);
        // Toast.makeText(VsMyTextDetailActivity.this,
        // mCurrentProviceName+","+mCurrentCityName+","
        // +mCurrentDistrictName+","+mCurrentZipCode, Toast.LENGTH_SHORT).show();
        // Toast.makeText(VsMyTextDetailActivity.this,
        // mCurrentProviceName+","+mCurrentCityName,Toast.LENGTH_SHORT).show();
    }

    // 上传参数
    private void getMyInfo(String uid, String flag, String para) {
        // {
        // "birthday": "1980-12-01", , "icon": "/data/sesfsdfsd/a.jpg", "gender": "M",
        // "nick": "test中文",
        // , "email": "Jeyha_yan@126.c om"}
        // if (getInfoReceiver!=null) {
        // unregisterReceiver(getInfoReceiver);
        // }
        //

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", uid);
        // treeMap.put(flag, para);
        treeMap.put("icon", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_photo));
        String sex = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Gender);
        if (sex.equals("男")) {
            treeMap.put("gender", "M");
        } else if (sex.equals("女")) {
            treeMap.put("gender", "F");
        } else {
            treeMap.put("gender", "O");
        }

        treeMap.put("nick", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname));
        treeMap.put("email", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_MailBox));
        treeMap.put("birthday", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Birth));

        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.PushMyInfo, DfineAction.authType_AUTO, treeMap,
                GlobalVariables.actionPushmyInfo);

    }

    private BroadcastReceiver getInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DfineAction.ACTION_PUSHINFOSUCCESS.equals(intent.getAction())) {
                if (intent.getStringExtra("result").equals("0")) {
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, intent.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onStop() {
        if (getInfoReceiver != null) {
            unregisterReceiver(getInfoReceiver);
            getInfoReceiver = null;
        }

        super.onStop();
    }

}
