package com.weiwei.home.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.image.StringUtils;
import com.weiwei.base.util.sign.AESUtils;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.entity.PayBackEntity;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.salemall.bean.RefuelCoupon;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.WxPayEntity;
import com.weiwei.salemall.inter.PayResultListener;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PayListenerUtils;
import com.weiwei.salemall.utils.RefuelPayUtil;
import com.weiwei.salemall.widget.CommonPopupWindow;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.PayUtils.showMessage;

public class RefuelCouponMoneyActivity extends BaseActivity implements PayResultListener {
    private static final String TAG = "RefuelCouponMoneyActivity";
    @BindView(R.id.tv_title_background)
    TextView mTvTitleBackground;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.tv_litre)
    TextView mTvLitre;
    @BindView(R.id.tv_drop_money)
    TextView mTvDropMoney;
    @BindView(R.id.tv_input_1)
    TextView nInput1;
    @BindView(R.id.tv_input_2)
    TextView nInput2;
    @BindView(R.id.tv_input_3)
    TextView nInput3;
    @BindView(R.id.tv_input_4)
    TextView nInput4;
    @BindView(R.id.tv_input_5)
    TextView nInput5;
    @BindView(R.id.tv_input_6)
    TextView nInput6;
    @BindView(R.id.tv_input_7)
    TextView nInput7;
    @BindView(R.id.tv_input_8)
    TextView nInput8;
    @BindView(R.id.tv_input_9)
    TextView nInput9;
    @BindView(R.id.tv_input_0)
    TextView nInput0;
    @BindView(R.id.tv_input_decimal)
    TextView nInputDecimal;
    @BindView(R.id.ll_input_cut)
    LinearLayout nInputCut;
    @BindView(R.id.ll_input_sure)
    LinearLayout nInputSure;

    @BindView(R.id.ll_input)
    LinearLayout mllInput;
    @BindView(R.id.ll_actual_money)
    LinearLayout mLlActualMoney;
    @BindView(R.id.ll_detail)
    LinearLayout mLlDetail;
    @BindView(R.id.tv_total_amount)
    TextView mTvTotalAmount;
    @BindView(R.id.tv_coupon_amount)
    TextView mTvCouponAmount;
    @BindView(R.id.tv_actual_amount)
    TextView mTvActualAmount;
    @BindView(R.id.tv_pay)
    TextView mTvPay;
    @BindView(R.id.tv_detail)
    TextView mTvDetail;

    private String mInput = "";

    private String mGunNo;
    private String mOilNo;
    private String mGasId;// "HF000003246";//WN448243779

    private String mLitre;
    private String mCouponMoney;
    private String mActualAmount;
    private CommonPopupWindow commonPopupWindow;
    private int mPayType = -1;

    public static void start(Context context, String gasId, String oilNo, String gunNo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("gasId", gasId);
        bundle.putSerializable("oilNo", oilNo);
        bundle.putSerializable("gunNo", gunNo);
        ArmsUtils.startActivity(context, RefuelCouponMoneyActivity.class, bundle);
    }
   public static RefuelCouponMoneyActivity instance;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuel_coupon_money;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refresh();
        PayListenerUtils.getInstance(this).removeListener(this);
    }

    @Override
    protected void initView() {
        instance = this;
        disableShowSoftInput();
        mTvTitle.setText("加油优惠");
        mGasId = getIntent().getStringExtra("gasId");
        mOilNo = getIntent().getStringExtra("oilNo");
        mGunNo = getIntent().getStringExtra("gunNo");
        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mLlTitle).keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        PayListenerUtils.getInstance(this).addListener(this);
        mEtMoney.addTextChangedListener(new TextWatcher() {
            boolean hint;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    // no text, hint is visible
                    hint = true;
                    mEtMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else {
                    // no hint, text is visible
                    hint = false;
                    mEtMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtMoney.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if (mllInput.getVisibility() == View.GONE) {
                        mllInput.setVisibility(View.VISIBLE);
                        mLlActualMoney.setVisibility(View.GONE);
                        mLlDetail.setVisibility(View.GONE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
                }
            }
        });

    }


    private void getRefuelCoupon() {
        if (StringUtils.isEmpty(mInput)) {
            ArmsUtils.makeText(RefuelCouponMoneyActivity.this, "请输入加油金额");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("gasId", mGasId);
//        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        params.put("oilNo", mOilNo);
        params.put("gunNo", mGunNo);
        params.put("gasMoney", mInput);
        Gson gson = new Gson();
        String s = gson.toJson(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), s);
        RetrofitClient.getInstance(this).Api()
                .queryPriceByGas(body)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        RefuelCoupon data = JSON.parseObject(result.getData().toString(), RefuelCoupon.class);
                        mLitre = data.litre;
                        mCouponMoney = data.couponMoney;
                        mActualAmount = data.actualAmount;
                        mTvLitre.setText("约" + mLitre + "L");
                        mTvDropMoney.setText("￥" + mCouponMoney);

                        //隐藏键盘
                        mllInput.setVisibility(View.GONE);
                        mLlActualMoney.setVisibility(View.VISIBLE);
                        DecimalFormat dFormat = new DecimalFormat("#.00");
                        mTvTotalAmount.setText("￥" + dFormat.format(Double.parseDouble(mInput)));
                        mTvCouponAmount.setText("￥" + mCouponMoney);
                        mTvActualAmount.setText("￥" + mActualAmount);
                    }
                });
    }

    @OnClick({R.id.tv_input_0,
            R.id.tv_input_1,
            R.id.tv_input_2,
            R.id.tv_input_3,
            R.id.tv_input_4,
            R.id.tv_input_5,
            R.id.tv_input_6,
            R.id.tv_input_7,
            R.id.tv_input_8,
            R.id.tv_input_9,
            R.id.ll_input_cut,
            R.id.ll_input_sure,
            R.id.tv_input_decimal,
            R.id.fl_back,
            R.id.tv_pay,
            R.id.tv_detail,
            R.id.et_money,
            R.id.rtv_input_500,
            R.id.rtv_input_300,
            R.id.rtv_input_200,
            R.id.rtv_input_100,
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rtv_input_100:
                mInput = "100.00";
                mEtMoney.setText(mInput);

                mEtMoney.clearFocus();
                getRefuelCoupon();
                break;
            case R.id.rtv_input_200:
                mInput = "200.00";
                mEtMoney.setText(mInput);

                mEtMoney.clearFocus();
                getRefuelCoupon();

                break;
            case R.id.rtv_input_300:
                mInput = "300.00";
                mEtMoney.setText(mInput);

                mEtMoney.clearFocus();
                getRefuelCoupon();

                break;
            case R.id.rtv_input_500:
                mInput = "500.00";
                mEtMoney.setText(mInput);

                mEtMoney.clearFocus();
                getRefuelCoupon();

                break;
            case R.id.et_money:
                if (mllInput.getVisibility() == View.GONE) {
                    mllInput.setVisibility(View.VISIBLE);
                    mLlActualMoney.setVisibility(View.GONE);
                    mLlDetail.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_detail:
                if (mLlDetail.getVisibility() == View.VISIBLE || mLlDetail.getVisibility() == View.INVISIBLE)
                    mLlDetail.setVisibility(View.GONE);
                else
                    mLlDetail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_pay:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2), mContext))
                    httpGetPayInfo();
                break;
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_input_0:
                if (StringUtils.isEmpty(mInput))
                    return;

                mInput += "0";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_1:
                mInput += "1";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_2:
                mInput += "2";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_3:
                mInput += "3";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_4:
                mInput += "4";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_5:
                mInput += "5";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_6:
                mInput += "6";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_7:
                mInput += "7";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_8:
                mInput += "8";
                mEtMoney.setText(mInput);
                break;
            case R.id.tv_input_9:
                mInput += "9";
                mEtMoney.setText(mInput);
                break;
            case R.id.ll_input_cut:
                if (StringUtils.isEmpty(mInput) || mInput.length() == 0)
                    return;

                mInput = mInput.substring(0, mInput.length() - 1);
                mEtMoney.setText(mInput);
                mTvLitre.setText("约0L");
                mTvDropMoney.setText("￥0.00");
                break;
            case R.id.ll_input_sure:
                mEtMoney.clearFocus();
                getRefuelCoupon();
                break;
            case R.id.tv_input_decimal:
                if (mInput.contains("."))
                    return;
                mInput += ".";
                mEtMoney.setText(mInput);
                break;
        }
        mEtMoney.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        mEtMoney.setFilters(new InputFilter[]{new InputFilter() {


            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int length = dest.toString().substring(index).length();
                    if (length == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});

        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        mEtMoney.setText(s);
                        mEtMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtMoney.setText(s);
                    mEtMoney.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtMoney.setText(s.subSequence(0, 1));
                        mEtMoney.setSelection(1);
                        return;
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Editable etext = mEtMoney.getText();
        Selection.setSelection(etext, etext.length());

    }

    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public void disableShowSoftInput() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEtMoney.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(mEtMoney, false);
            } catch (Exception e) {
            }
        }
    }

    private void showPaymentPop() {
        commonPopupWindow = new CommonPopupWindow.Builder(this).setView(R.layout.pop_refuel_pay_sure).setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT).setAnimationStyle(R.style.PopupWindowAnimation).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
            @Override
            public void getChildView(View view, int layoutResId) {
                LinearLayout mLlWechat = (LinearLayout) view.findViewById(R.id.ll_wechat);
                LinearLayout mLlAlipay = (LinearLayout) view.findViewById(R.id.ll_alipay);
                if(mPayBackEntity != null){
                    if(TextUtils.isEmpty(mPayBackEntity.getWxpay())){
                        mLlWechat.setVisibility(View.GONE);
                    }
                    if(TextUtils.isEmpty(mPayBackEntity.getAliPay())){
                        mLlAlipay.setVisibility(View.GONE);
                    }
                }
                mLlWechat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPayType = 0;
                        payment();
                        commonPopupWindow.dismiss();
                    }
                });

                mLlAlipay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPayType = 1;
                        payment();
                        commonPopupWindow.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        commonPopupWindow.showAtLocation((this).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void httpGetPayInfo(){
        RetrofitClient.getInstance(this).Api()
                .queryPay(mGasId)
                .enqueue(new RetrofitCallback<ResultEntity>() {

                    @Override
                    protected void onNext(ResultEntity result) {
                        Object data = result.getData();
                        if(data != null){
                            String d = (String)data ;
                            String str = AESUtils.decrypt("1234567890123456",d);
                            mPayBackEntity = new Gson().fromJson(str, PayBackEntity.class);
                            showPaymentPop();
                        }
                    }
                });
    }

    private PayBackEntity mPayBackEntity;

    /**
     * 支付
     */
    private void payment() {
        Map<String, Object> params = new HashMap<>();
        String payType = "";
        switch (mPayType) {
            case 0:
                payType = "wxpay";
                break;
            case 1:
                payType = "alipay";
                break;
            default:
                break;
        }
        params.put("payType", payType);
        params.put("gasId", mGasId);
        params.put("oilNo", mOilNo);
        params.put("gunNo", mGunNo);
        params.put("gasMoney", mInput);
        params.put("amountPay", mActualAmount);
        params.put("amountGun", mInput);
        params.put("litre", mLitre);
        params.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        params.put("appId", "dudu");
        params.put("ip", "40.125.12.21");
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));
        RetrofitClient.getInstance(this).Api()
                .placeOrder(body)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        Log.d("code++++++", "onNext: " + result.getCode());
                        switch (mPayType) {
                            case 0:     //微信
                                if (REQUEST_CODE == result.getCode()) {
                                    WxPayEntity entity = JSON.parseObject(result.getData().toString(), WxPayEntity.class);
                                    String code = entity.getCode();
                                    String sign = entity.getSign();
                                    String prepay_id = entity.getPrepayid();
                                    String notifyurl = entity.getNotifyurl();
                                    if (!StringUtils.isEmpty(prepay_id)) {
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = 0;
                                        msg.obj = prepay_id;
                                        mHandler.sendMessage(msg);
                                    } else {
                                        String msg_res = "服务器生成订单失败.";
                                        Toast.makeText(mContext, msg_res, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case 1:    //支付宝
                                if (REQUEST_CODE == result.getCode()) {
                                    JSONObject orderJSON = JSON.parseObject(result.getData().toString());
                                    String orderInfo = orderJSON.getString("aliPay");
                                    Log.d("", "onNext: " + orderInfo.toString());
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 1;
                                    msg.obj = orderInfo;
                                    mHandler.sendMessage(msg);
                                } else {
                                    Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    DfineAction.WEIXIN_MCH_ID = mPayBackEntity.getWxPaySecret();
                    DfineAction.WEIXIN_APPID = mPayBackEntity.getWxPayAppid();
                    DfineAction.WEIXIN_API_KEY = mPayBackEntity.getWxPaykey();
                    Log.e("111", "prepay_id' : " + msg.obj.toString());
                    RefuelPayUtil.getInstance(RefuelCouponMoneyActivity.this).toWXPay(RefuelCouponMoneyActivity.this, msg.obj.toString());
                    break;
                case 1:
                    Log.e("111", "toAliPay' : " + msg.obj.toString());
                    RefuelPayUtil.getInstance(RefuelCouponMoneyActivity.this).toAliPay(msg.obj.toString(), 0);
                    break;
                default:
                    break;
            }
        }
    };

    private void refresh(){
        DfineAction.WEIXIN_MCH_ID = DfineAction.GOODS_WEIXIN_MCH_ID;
        DfineAction.WEIXIN_APPID = DfineAction.GOODS_WEIXIN_APPID;
        DfineAction.WEIXIN_API_KEY = DfineAction.GOODS_WEIXIN_API_KEY;
        DfineAction.WEIXIN_APPSECRET = DfineAction.GOODS_WEIXIN_APPSECRET;
    }

    @Override
    public void onPaySuccess() {
        showMessage("支付成功");
    }

    @Override
    public void onPayError() {
        showMessage("支付失败");
    }

    @Override
    public void onPayCancel() {
        showMessage("支付取消");
    }
}
