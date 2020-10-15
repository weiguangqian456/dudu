package com.weiwei.account;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.VsMyRedPagActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.fragment.GrowMoneyFragment;
import com.weiwei.base.fragment.InviteFragment;
import com.weiwei.base.util.ViewPageFragment;
import com.weiwei.base.widgets.MyViewPager;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

public class MyGrowMoneyActivity extends TempAppCompatActivity {

    private static final String TAG = MyGrowMoneyActivity.class.getSimpleName();
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_redbag)
    TextView regBagTv;
    @BindView(R.id.tablayout)
    XTabLayout tablayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private List<Fragment> fragmentList = null;
    private ViewPageFragment viewPageFragment;
    private String[] titles = {"成长金明细","我的邀请"};
    private CustomProgressDialog loadingDialog;
    private String red;         //红包
    private String invitation;  //邀请红包余额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grow_money);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_DE5A3C);
        ButterKnife.bind(this);
        initView();
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        initData();

    }

    private void initView() {
        titleTv.setText("成长金");

        fragmentList = new ArrayList<>();
        fragmentList.add(new GrowMoneyFragment());
        fragmentList.add(new VsInviteFragment());

        viewPageFragment = new ViewPageFragment(getSupportFragmentManager(),fragmentList,titles);
        viewPager.setAdapter(viewPageFragment);
        tablayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }
    private void initData() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
//        params.put("uid", VsUserConfig.getDataString(mContext,VsUserConfig.JKey_KcId));
//        params.put("appId", "dudu");
        Call<ResultEntity> call = api.getWallet2(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "我的成长金msg===>" + result.getData().toString());
                    RedBagEntity redBagEntity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
//                    coupon = redBagEntity.getSignIntegral();
                    red = redBagEntity.getRed();
                    invitation = redBagEntity.getInvitation();
                    regBagTv.setText(red);
                } else {
                    Toast.makeText(MyGrowMoneyActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }
    @Override
    protected void init() {

    }

    @OnClick({R.id.rl_back, R.id.rtv_go_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rtv_go_withdraw:
                Intent intent_balance = new Intent(this, VsMyRedPagActivity.class);
                String urlTo = VsUserConfig.getDataString(this, VsUserConfig.JKey_RED_PAGE);
                String[] aboutBusiness = new String[]{"我的红包", "", urlTo};
                intent_balance.putExtra("AboutBusiness", aboutBusiness);
                intent_balance.putExtra("uiFlag", "redbag");
                startActivity(intent_balance);
                break;
        }
    }
}
