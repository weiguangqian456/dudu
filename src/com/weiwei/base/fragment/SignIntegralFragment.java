package com.weiwei.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.hwtx.dududh.R;
import com.weiwei.account.RedBagEntity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseFragment;
import com.weiwei.home.fragment.CustomUpdataDialog;
import com.weiwei.home.fragment.SignAccomplishDialog;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SignInfoBean;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description: 任务中心（签到）
 */

public class SignIntegralFragment extends BaseFragment implements SensorEventListener {

    @BindView(R.id.iv_sign)
    ImageView iv_sign;
    @BindView(R.id.tv_rule)
    TextView tv_rule;
    @BindView(R.id.tv_integral)
    TextView tv_integral;
    @BindView(R.id.fl_load)
    FrameLayout fl_load;

    private boolean isShake = false;
    private SensorManager mSensorManager;

    /**
     * 加载中Dialog
     */
    private CustomProgressDialog loadingDialog;

    private boolean isSignLoad = false;
    private boolean isSign = false;

    String userLevel;
    private String mGetValue = "10";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView() {
        //获取等级信息
        httpUserInfo();
        initLoadingDialog();
        fl_load.postDelayed(new Runnable() {
            @Override
            public void run() {
                httpSignInfo();
            }
        }, 500);
        iv_sign.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                httpSign();
            }
        });
        if (!CheckLoginStatusUtils.isLogin()) {
            if (getActivity() != null) {
                ToastAstrictUtils.getInstance().show("请先登录哦");
                getActivity().finish();
            }
        }
    }

    private void initLoadingDialog() {
        //初始化加载中
        loadingDialog = new CustomProgressDialog(getActivity(), "正在加载中...", R.drawable.loading_frame);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
    }

    /**
     * 获取加速度传感器---摇一摇
     */
    private void initSensor() {
        if (isSign) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null) {
            return;
        }
        Sensor defaultSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        initSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    /**
     * 签到
     */
    private void httpSign() {
        if (TextUtils.isEmpty(userLevel)) {
            userLevel = PreferencesUtils.getInt(VsApplication.getContext(), VsUserConfig.JKey_MyInfo_Level) + "";
        }
        //已经签到 或者 签到中
        if (isSign || isSignLoad) {
            if (isSignLoad) {
                ToastAstrictUtils.getInstance().show("正在努力签到");
            } else {
                ToastAstrictUtils.getInstance().show("今天已经签过了哦");
            }
            return;
        }
        isSignLoad = true;
        loadingDialog.setLoadingDialogShow();
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        Map<String, String> params = new HashMap<>();
        params.put("appId", "dudu");
        params.put("level", userLevel);
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                new GsonBuilder().enableComplexMapKeySerialization().create().toJson(params));
        retrofit2.Call<ResultEntity> call = api.httpSign(requestBody);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (iv_sign == null) return;
                loadingDialog.setLoadingDialogDismiss();
                isSignLoad = false;
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (result != null && REQUEST_CODE == result.getCode()) {
                    boolean aFalse = !result.getData().toString().contains("false");
                    if (aFalse) {
                        isSign = true;
                        mGetValue = result.getData().toString();
                        signSuccess();
                    } else {
                        if (TextUtils.isEmpty(result.getMsg())) {
                            ToastUtils.show(mContext, "签到失败");
                        } else {
                            ToastUtils.show(mContext, result.getMsg());
                        }
                    }
                } else if (result != null && result.getCode() == -1) {
                    notCanSign();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
                isSignLoad = false;
            }
        });
    }

    /**
     * 请求等级
     */
    private void httpUserInfo() {
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = api.getUserLevel();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (iv_sign == null) return;
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    RedBagEntity entity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
                    userLevel = entity.getLevel();
//                    String levelName = entity.getLevelName();
//                    if (!StringUtils.isEmpty(levelName)) {
//                        if(!"1".equals(userLevel)&&
//                        !"2".equals(userLevel)&&
//                        !"3".equals(userLevel)){
//                        }
//                    }
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 请求签到信息
     */
    private void httpSignInfo() {
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = null;
        call = api.httpSignInfo(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (iv_sign == null) return;
                //加载完隐藏
                alphaAnimationView();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    SignInfoBean bean = JSONObject.parseObject(result.getData().toString(), SignInfoBean.class);
                    if (bean != null) {
                        String rule = bean.getRule();
                        String amount = "我的积分" + bean.getAmount();
                        String replace = rule.replace("\\n", "\n");
                        isSign = !TextUtils.isEmpty(bean.getIsSign()) && !"n".equals(bean.getIsSign());
                        if (isSign) {
                            Glide.with(mContext.getApplicationContext()).load(R.drawable.icon_sign_accomplish).into(iv_sign);
                        }
                        tv_rule.setText(replace);
                        tv_integral.setText(amount);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                //加载完隐藏
                alphaAnimationView();
            }
        });
    }

    private CustomUpdataDialog mHintDialog;

    //    private HintMessageDialog mHintDialog;
    private void notCanSign() {
        if (getFragmentManager() != null) {
            if (mHintDialog != null && mHintDialog.isVisible()) {
                return;
            }
            mHintDialog = CustomUpdataDialog
                    .getInstance()
                    .setDrawableId(R.drawable.ic_sign_in_member_prompt);
            mHintDialog.show(getFragmentManager(), "TAG");
        } else {
            ToastAstrictUtils.getInstance().show("升级成为会员才有权限签到哦！");
        }
    }

    private void alphaAnimationView() {
        fl_load.setVisibility(View.GONE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e("TAG-A", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fl_load.setVisibility(View.GONE);
                Log.e("TAG-A", "onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e("TAG-A", "onAnimationRepeat");
            }
        });
        fl_load.setAnimation(alphaAnimation);
    }

    private void signSuccess() {
        //完成一次后注销
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
        showDialog();
        httpSignInfo();
        Glide.with(mContext).load(R.drawable.icon_sign_accomplish).into(iv_sign);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            boolean isThisShake = (Math.abs(x) > 16 || Math.abs(y) > 16 || Math.abs(z) > 16) && !isSign;
            if (isThisShake) {
                MineHandler handler = new MineHandler(getActivity());
                handler.obtainMessage().sendToTarget();
                httpSign();//去签到
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 签到成功
     */
    private void showDialog() {
        SignAccomplishDialog dialog = new SignAccomplishDialog();
        if (getFragmentManager() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("value", mGetValue);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "SIGN");
        } else {
            ToastUtils.show(mContext, "签到成功");
        }
    }

    /**
     * Handler 震动
     */
    private static class MineHandler extends Handler {

        private WeakReference<Activity> mReference;
        private Activity mActivity;

        MineHandler(Activity activity) {
            mReference = new WeakReference<>(activity);
            mActivity = mReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Vibrator vibrator = (Vibrator) mActivity.getSystemService(Activity.VIBRATOR_SERVICE);
            if (vibrator != null) {
                //静止 - 震动 - 静止 ---
                long[] patter = {100, 300, 500, 300};
//                int [] am     = {100, 100, 100, 100};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //.createWaveform(patter, am,-1)
                    vibrator.vibrate(VibrationEffect.createOneShot(500, 100));
                } else {
                    vibrator.vibrate(patter, -1);
                }
            }
        }
    }
}
