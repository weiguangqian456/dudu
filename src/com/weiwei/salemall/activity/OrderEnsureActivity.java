package com.weiwei.salemall.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.activity.me.VsRechargeActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.sign.AESUtils;
import com.weiwei.home.entity.PayBackEntity;
import com.weiwei.home.entity.PayBackEntity2;
import com.weiwei.home.fragment.CustomUpdataDialog;
import com.weiwei.home.fragment.PayDialog;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.OrderEnsureAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.CartBuyProductsBean;
import com.weiwei.salemall.bean.DealCartPriceBean;
import com.weiwei.salemall.bean.DealPriceDataEntity;
import com.weiwei.salemall.bean.OrderEnsureEntity;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.WxPayEntity;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.inter.PayResultListener;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.PayListenerUtils;
import com.weiwei.salemall.utils.PayUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SDAdaptiveTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.weiwei.base.dataprovider.DfineAction.UPDATE_TO_BUY_MSG;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.PayUtils.showMessage;

/**
 * @author Created by EDZ on 2018/6/1.
 * 确认订单界面
 */

public class OrderEnsureActivity extends TempAppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener, PayResultListener {
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.ll_address_nodata)
    RelativeLayout ll_address_nodata;
    @BindView(R.id.ll_address_data)
    LinearLayout ll_address_data;
    @BindView(R.id.tv_btn_pay)
    TextView tv_btn_pay;
    @BindView(R.id.tv_jd_price)
    TextView tv_jd_price;
    @BindView(R.id.tv_goods_title)
    TextView tv_goods_title;
    @BindView(R.id.iv_shop)
    ImageView iv_shop;
    @BindView(R.id.tv_mall_price)
    TextView tv_mall_price;
    @BindView(R.id.tv_goods_number)
    TextView tv_goods_number;
    @BindView(R.id.tv_shop_number)
    TextView tv_shop_number;
    @BindView(R.id.tv_econom_money)
    TextView tv_econom_money;
    @BindView(R.id.tv_money_total)
    TextView tv_money_total;
    @BindView(R.id.tv_money_ensure)
    TextView tv_money_ensure;
    @BindView(R.id.tv_send)
    TextView tv_send;
    @BindView(R.id.tv_productModeDesc)
    TextView tv_productModeDesc;
    @BindView(R.id.rv_order)
    RecyclerView orderRecycleView;
    @BindView(R.id.ll_buy_order_ensure)
    LinearLayout orderLinearLayout;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_address_detail)
    TextView tv_address_detail;
    @BindView(R.id.rl_content)
    RelativeLayout contentRl;
    @BindView(R.id.tv_total_money)
    TextView totalMoney;
    @BindView(R.id.tv_discount)
    TextView discountMoney;
    @BindView(R.id.tv_freight_money)
    TextView freightMoney;
    @BindView(R.id.rl_remark)
    RelativeLayout remarkRl;
    @BindView(R.id.tv_remark)
    TextView remarkTv;
    @BindView(R.id.rl_refuel_balance)
    RelativeLayout mRlRefuelBalance;
    /**
     * 加油余额
     */
    @BindView(R.id.tv_shoppingvoucher)
    TextView shoppingVoucherTv;
    @BindView(R.id.rl_logistics_explain)
    RelativeLayout logisticsExplainRl;
    @BindView(R.id.tv_logistics_money)
    TextView logisticsMoneytv;
    @BindView(R.id.tv_logistics_explain)
    SDAdaptiveTextView logisticsExplainTv;

    private CommonPopupWindow commonPopupWindow;
    private CheckBox[] paytypeCb = new CheckBox[3];

    private String shopName;
    private String shopImage;
    private String mall_price_true;
    private String jd_price_true;
    private int shop_number;
    private String totalAccount;
    private String conversionPrice;
    private String isExchange;

    /**
     * =================是否秒杀商品===================
     **/
    private String isSeckill;
    private String seckillProductId;

    /**
     * 下单参数
     */
    private String desc;
    private String property;
    private String addressId;
    private String payChannel = "redpay";
    private String productNo;
    private String columnId;

    /**
     * 支付回调
     */
    private static final String TAG = "OrderEnsureActivity";
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    //=================价格处理相关=============
    private String isFreePostage;   //是否包邮
    private String postage;         //邮费
    private String originalAmount;  //原价
    private String discountAmount;  //实际金额
    private String discount;        //折扣金额
    private String productMoney;    //商品总额
    private String remark;          //兑换备注
    private String coupon;          //加油余额
    private String deliveryType;    //配送方式
    private String logisticsPostage;  //预估的物流费
    private String logisticsExplain;  //物流说明字段
    private List<AddressEntity> recordsBeanList;
    private String name;
    private String phone;
    private String address;
    private int orderEnsureActivityFlag = 0;
    private String holidaySeckillProductId;

    //================支付回调相关==============
    private static final int UI_FLAG = 0;
    private static final int UI_SHOW_PAY_POP = 3;
    private String alipaymsg;
    private String alipayfailmsg;
    private String redpaymsg;
    private String wxpaymsg;
    private String wxpayfailmsg;

    private OrderEnsureAdapter orderEnsureAdapter;
    private List<OrderEnsureEntity> strList;
    private List<DealCartPriceBean> dealCartPriceBeanList;
    private List<CartBuyProductsBean> cartBuyProductsBeanList = new ArrayList<>();
    private ShoppingItemsBeanDao shoppingItemsBeanDao;

    /**
     * 支付宝默认返回提示
     */
    private String msgString;

    /**
     * 购物车数据
     */
    private String cartOriginalAmount;
    private String cartDiscountAmount;

    /**
     * 处理价格返回失败
     */
    private String dealPriceFailMsg;

    /**
     * 进度加载
     */
    private CustomProgressDialog loadingDialog;

    /**
     * 密码输入框
     */
    private CommonPopupWindow pwdInputPop;
    private PayDialog mPayDialog;
    /**
     * 标志是否选择地址
     */
    private boolean isSelectedAddress;

    /**
     * 配送方式
     */
    private String deliveryMethod = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ensure);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        PayListenerUtils.getInstance(this).addListener(this);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadingDialog != null && loadingDialog.isShowing() && !isSelectedAddress) {
            loadingDialog.dismiss();
 //           skipPage(VsMainActivity.class);
  /*          Intent intent = new Intent(this,VsMainActivity.class);
            intent.putExtra("indicator",1);
            startActivity(intent);
            finish();*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refresh();
        PayListenerUtils.getInstance(this).removeListener(this);
    }

    private void initView() {
        if (!CheckLoginStatusUtils.isLogin()) {
            Toast.makeText(this, getResources().getString(R.string.login_prompt10), Toast.LENGTH_SHORT).show();
            SkipPageUtils.getInstance(this).skipPage(VsLoginActivity.class);
            finish();
            return;
        }

        titleTv.setText("确定订单");
        tv_jd_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        backRl.setOnClickListener(this);

        //flag    区分直接直接购买还是购物车购买
        orderEnsureActivityFlag = getIntent().getIntExtra("orderEnsureActivityFlag", 0);
        setCurrentView(orderEnsureActivityFlag);

        getDefaultAddress();

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    /**
     * a
     * 区分直接购买还是购物车购买
     *
     * @param orderEnsureActivityFlag
     */
    private void setCurrentView(int orderEnsureActivityFlag) {
        switch (orderEnsureActivityFlag) {
            case 0:  //直接购买
                shopImage = JudgeImageUrlUtils.isAvailable(getIntent().getStringExtra("shop_image_preview"));
                shopName = getIntent().getStringExtra("shop_name");
                mall_price_true = getIntent().getStringExtra("mall_price_true");
                jd_price_true = getIntent().getStringExtra("jd_price_true");
                String goodsNumber = getIntent().getStringExtra("shop_number");
                shop_number = Integer.parseInt(goodsNumber);
                totalAccount = getIntent().getStringExtra("mall_price_true");
                property = getIntent().getStringExtra("property");
                desc = getIntent().getStringExtra("productDesc");
                productNo = getIntent().getStringExtra("productNo");
                columnId = getIntent().getStringExtra("columnId");
                conversionPrice = getIntent().getStringExtra("conversionPrice");
                coupon = getIntent().getStringExtra("coupon");
                isExchange = getIntent().getStringExtra("isExchange");
                isSeckill = getIntent().getStringExtra("isSeckill");
                seckillProductId = getIntent().getStringExtra("seckillProductId");
                deliveryType = getIntent().getStringExtra("deliveryType");
                deliveryMethod = getIntent().getStringExtra("deliveryMethodMsg");
                holidaySeckillProductId = getIntent().getStringExtra("holidaySeckillProductId");

                String image = getIntent().getStringExtra("Image");
                if (!StringUtils.isEmpty(deliveryMethod)) {
                    tv_econom_money.setText(deliveryMethod);
                }
                tv_goods_title.setText(shopName);
                if (desc.length() > 0) {
                    tv_productModeDesc.setText((desc.substring(0, desc.length() - 1)));
                }
                Glide.with(this).load(TextUtils.isEmpty(image) ? shopImage : image).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(iv_shop);

                if (!StringUtils.isEmpty(isExchange)) {
                    if (isExchange.equals("y")) {
                        tv_mall_price.setText(TextDisposeUtils.dispseMoneyText(conversionPrice));//"¥" + conversionPrice + " + " +
                    } else {
                        tv_mall_price.setText("¥" + mall_price_true);
                    }
                } else {
                    tv_mall_price.setText("¥" + mall_price_true);
                }

                tv_jd_price.setText("¥" + jd_price_true);
                tv_shop_number.setText("共" + shop_number + "件商品");
                tv_goods_number.setText("x" + shop_number);
                break;
            case 1:  //购物车购买
                orderLinearLayout.setVisibility(View.GONE);
                orderRecycleView.setVisibility(View.VISIBLE);
                Bundle bundle = getIntent().getExtras();
                strList = (List<OrderEnsureEntity>) bundle.getSerializable("arrayList");
                orderEnsureAdapter = new OrderEnsureAdapter(this, strList);
                orderRecycleView.setLayoutManager(new LinearLayoutManager(this));
                orderRecycleView.setAdapter(orderEnsureAdapter);
                tv_shop_number.setText("共" + strList.size() + "件商品");
                dealCartPriceBeanList = (List<DealCartPriceBean>) bundle.getSerializable("dealCartPriceBeanList");
                break;
            default:
                break;
        }
    }

    /**
     * 保存参数
     *
     * @param outState
     * @param outPersistentState
     */
    @SuppressLint("NewApi")
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.e(TAG, "onSaveInstanceState");
        outPersistentState.putString("shop_image_preview", shopImage);
        outPersistentState.putString("shop_name", shopName);
        outPersistentState.putString("mall_price_true", mall_price_true);
        outPersistentState.putString("jd_price_true", jd_price_true);
        outPersistentState.putInt("shop_number", shop_number);
        outPersistentState.putString("mall_price_true", totalAccount);
        outPersistentState.putString("property", property);
        outPersistentState.putString("productDesc", desc);
        outPersistentState.putString("productNo", productNo);
    }

    /**
     * 获取默认地址，无默认地址显示地址空白
     */
    private void getDefaultAddress() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> param = new HashMap<>();
        param.put("pageNum", "1");
        param.put("pageSize", "10");
        retrofit2.Call<ResultEntity> call = api.getDelivery(param);
        call.enqueue(new retrofit2.Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "地址msg===>" + result.getData().toString());
                    recordsBeanList = new ArrayList<>();
                    ResultDataEntity addressDataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                    List<AddressEntity> list = JSON.parseArray(addressDataEntity.getRecords().toString(), AddressEntity.class);
                    if (list != null) {
                        recordsBeanList.addAll(list);
                        for (int i = 0; i < recordsBeanList.size(); i++) {
                            AddressEntity entity = recordsBeanList.get(i);
                            if (entity.getIsDefault() == 1) {     //获取默认地址
                                name = entity.getName();      //名字
                                phone = entity.getPhone();    //电话

                                String town = entity.getTown();     //兼容新老版本
                                if (StringUtils.isEmpty(town)) {
                                    town = "";
                                }
                                address = entity.getProvince() + entity.getCity() + entity.getArea() + town + entity.getAddress();   //详细地址
                                addressId = entity.getId();   //地址Id
                            }
                        }
                    }

                    if (!StringUtils.isEmpty(name)) {       //地址不为空（处理价格）
                        ll_address_data.setVisibility(View.VISIBLE);
                        ll_address_nodata.setVisibility(View.GONE);
                        tv_name.setText(name);
                        tv_phone.setText(phone);
                        tv_address_detail.setText(address);

                        switch (orderEnsureActivityFlag) {
                            case 0:    //直接购买处理价格
                                dealPrice(addressId);
                                break;
                            case 1:    //购物车购买处理价格
                                dealCartPrice(addressId, dealCartPriceBeanList);
                                break;
                            default:
                                break;
                        }
                    } else {
                        ll_address_data.setVisibility(View.GONE);
                        ll_address_nodata.setVisibility(View.VISIBLE);
                        Toast.makeText(OrderEnsureActivity.this, "请添加收货地址", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 直接购买处理价格
     *
     * @param addressId
     */
    private void dealPrice(String addressId) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        Map<String, Object> params = new HashMap<>();
        List<DealCartPriceBean> beanList = new ArrayList<>();
        DealCartPriceBean bean = new DealCartPriceBean();
        bean.setProductNo(productNo);
        bean.setProductNum(shop_number);
        bean.setProperty(property);
        bean.setColumnId(columnId);
        bean.setDeliveryType(deliveryType);
        beanList.add(bean);
        params.put("appId", "dudu");
        params.put("uid", VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId));
        params.put("deliveryId", addressId);
        params.put("products", beanList);
        if (!StringUtils.isEmpty(isSeckill)) {
            switch (isSeckill) {
                case "dudu":
                    params.put("orderType", "1");
                    params.put("seckillProductId", "");
                    break;
                case "y":
                    params.put("orderType", "2");
                    params.put("seckillProductId", seckillProductId);
                    break;
                case "jingDong":
                    params.put("orderType", "3");
                    params.put("seckillProductId", "");
                    break;
                case "duduSeckill":
                    params.put("orderType", "4");
                    params.put("holidaySeckillProductId", holidaySeckillProductId);
                    break;
                default:
                    params.put("orderType", "1");
                    params.put("seckillProductId", "");
                    break;
            }
        }

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Log.e(TAG, "处理价格json===>" + gson.toJson(params));
        String str = gson.toJson(params);
        RequestBody requestBody = RequestBody.create(JSON_TYPE, gson.toJson(params));

        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.dealCartPrice(requestBody);
        call.enqueue(new retrofit2.Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "处理价格msg===>" + result.getData().toString());
                    DealPriceDataEntity dealPriceDataEntity = JSON.parseObject(result.getData().toString(), DealPriceDataEntity.class);
                    originalAmount = dealPriceDataEntity.getOriginalAmount();
                    discountAmount = dealPriceDataEntity.getDiscountAmount();
                    postage = dealPriceDataEntity.getPostage();
                    remark = dealPriceDataEntity.getRemark();
                    isFreePostage = dealPriceDataEntity.getIsFreePostage();
                    discount = dealPriceDataEntity.getDiscount();
                    productMoney = dealPriceDataEntity.getProductMoney();
                    coupon = dealPriceDataEntity.getCoupon();
                    logisticsPostage = dealPriceDataEntity.getLogisticsPostage();
                    logisticsExplain = dealPriceDataEntity.getLogisticsExplain();
                    setViewData(isFreePostage, postage, originalAmount, discountAmount, discount, productMoney, remark, coupon, logisticsPostage, logisticsExplain);
                } else {
                    dealPriceFailMsg = result.getMsg();
                    showUpdateDialog(dealPriceFailMsg);
                    tv_btn_pay.setClickable(false);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 购物车处理价格
     *
     * @param addressId
     * @param dealCartPriceBeanList
     */
    private void dealCartPrice(String addressId, List<DealCartPriceBean> dealCartPriceBeanList) {
        loadingDialog.setLoadingDialogShow();
        Map<String, Object> params = new HashMap<>();
        params.put("products", dealCartPriceBeanList);
        params.put("appId", "dudu");
        params.put("deliveryId", addressId);
        params.put("uid", VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId));

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String dealpriceStr = gson.toJson(params);
        Log.e(TAG, "购物车处理价格json===>" + dealpriceStr);
        RequestBody requestBody = RequestBody.create(JSON_TYPE, dealpriceStr);

        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.dealCartPrice(requestBody);
        call.enqueue(new retrofit2.Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "购物车处理价格msg===>" + result.getData().toString());
                    DealPriceDataEntity dealPriceDataEntity = JSON.parseObject(result.getData().toString(), DealPriceDataEntity.class);
                    cartOriginalAmount = dealPriceDataEntity.getOriginalAmount();
                    cartDiscountAmount = dealPriceDataEntity.getDiscountAmount();
                    postage = dealPriceDataEntity.getPostage();
                    isFreePostage = dealPriceDataEntity.getIsFreePostage();
                    discount = dealPriceDataEntity.getDiscount();
                    productMoney = dealPriceDataEntity.getProductMoney();
                    remark = dealPriceDataEntity.getRemark();
                    coupon = dealPriceDataEntity.getCoupon();
                    logisticsPostage = dealPriceDataEntity.getLogisticsPostage();
                    logisticsExplain = dealPriceDataEntity.getLogisticsExplain();
                    setViewData(isFreePostage, postage, cartOriginalAmount, cartDiscountAmount, discount, productMoney, remark, coupon, logisticsPostage, logisticsExplain);
                } else {
//                    Toast.makeText(OrderEnsureActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                    showUpdateDialog(result.getMsg());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 升级窗口
     *
     * @param msg
     */
    private void showUpdateDialog(String msg) {
        if (msg.equals("升级成为会员才有权限购买")) {
            CustomUpdataDialog
                    .getInstance()
                    .show(getSupportFragmentManager(), "");
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msg);
            int level = PreferencesUtils.getInt(VsApplication.getContext(), VsUserConfig.JKey_MyInfo_Level, 5);
            if (level == 0 && msg.equals(UPDATE_TO_BUY_MSG)) {
                builder.setPositiveButton("去升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skipPage(VsRechargeActivity.class);
                    }
                });
            } else {
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            builder.show();
        }
    }

    /**
     * 处理邮费、价格相关显示
     *
     * @param postage
     * @param originalAmount
     * @param discountAmount
     */
    private void setViewData(String isFreePostage, String postage, String originalAmount, String discountAmount, String discount, String productMoney, String remark, String
            coupon, String logisticsPostage, final String logisticsExplain) {
        tv_send.setText("运费：¥" + postage);
        totalMoney.setText("¥" + productMoney);
        discountMoney.setText("- ¥" + discount);
        freightMoney.setText("¥" + postage);
        tv_money_total.setText("付款金额：¥" + discountAmount);
        tv_money_ensure.setText(TextDisposeUtils.getTotalAmount("￥", discountAmount, coupon));
        Log.e(TAG, "当前支付金额" + discountAmount);

        if (!StringUtils.isEmpty(remark)) {
            remarkRl.setVisibility(View.VISIBLE);
            remarkTv.setText("温馨提示：" + remark);
        }
        if (Integer.parseInt(coupon)>0){
            mRlRefuelBalance.setVisibility(View.VISIBLE);
        }else {
            mRlRefuelBalance.setVisibility(View.GONE);
        }
        shoppingVoucherTv.setText(coupon);
        logisticsMoneytv.setText("(预估)¥" + logisticsPostage);
        if (!StringUtils.isEmpty(logisticsExplain)) logisticsExplainRl.setVisibility(View.VISIBLE);
        logisticsExplainTv.post(new Runnable() {
            @Override
            public void run() {
                logisticsExplainTv.setAdaptiveText(logisticsExplain);
            }
        });
    }

    /**
     * 相关View  点击事件
     */
    @OnClick({R.id.ll_address_nodata, R.id.ll_address_data})
    void OpenAddress() {
        Intent intent = new Intent(this, AddressListActivity.class);
        intent.putExtra("select", "1");
        isSelectedAddress = true;
        startActivityForResult(intent, 1);
    }

    @OnClick({R.id.tv_btn_pay})
    void Pay(View view) {
        checkAddress();
    }

    private void checkAddress() {
        if (StringUtils.isEmpty(name)) {
            Toast.makeText(OrderEnsureActivity.this, "请选择收货地址", Toast.LENGTH_SHORT).show();
        } else {
            switch (orderEnsureActivityFlag) {
                case 0:    //直接购买
                    if (!StringUtils.isEmpty(discountAmount)) showPayPopWindow(discountAmount);
                    break;
                case 1:   //购物车购买
                    if (!StringUtils.isEmpty(cartDiscountAmount))
                        showPayPopWindow(cartDiscountAmount);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示支付弹窗
     *
     * @param discountAmount
     */
    private void showPayPopWindow(final String discountAmount) {
        payChannel = "redpay";
        mPayDialog = PayDialog
                .getInstance()
                .setCoupon(coupon)
                .setDiscountAmount(discountAmount)
                .setConfirmClick(OrderEnsureActivity.this)
                .setOnCheckChange(new PayDialog.CheckChange() {
                    @Override
                    public void onCheckChange(String channel) {
                        payChannel = channel;
                    }
                });

        mPayDialog.show(getSupportFragmentManager(), null);

//        commonPopupWindow = new CommonPopupWindow.Builder(this).setView(R.layout.pop_pay_way).setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
//                .WRAP_CONTENT).setAnimationStyle(R.style.PopupWindowAnimation).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
//            @Override
//            public void getChildView(View view, int layoutResId) {
//
//                TextView commitPayTv = (TextView) view.findViewById(R.id.dialog_confirm_pay);
//                ImageView closeIv = (ImageView) view.findViewById(R.id.iv_mall_order_ensure_close);
//                TextView moneyPayTv = (TextView) view.findViewById(R.id.tv_money_ensure_dialog);
//                TextView tv_price_symbol = view.findViewById(R.id.tv_price_symbol);
//
//                paytypeCb[0] = (CheckBox) view.findViewById(R.id.cb_redbag);
//                paytypeCb[1] = (CheckBox) view.findViewById(R.id.cb_wx);
//                paytypeCb[2] = (CheckBox) view.findViewById(R.id.cb_alipay);
//                if(Float.parseFloat(discountAmount) == 0){
//                    view.findViewById(R.id.ll_wx).setVisibility(View.GONE);
//                    view.findViewById(R.id.ll_alipay).setVisibility(View.GONE);
//                }
//                paytypeCb[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            paytypeCb[1].setChecked(false);
//                            paytypeCb[2].setChecked(false);
//                            payChannel = "redpay";
//                        }
//                    }
//                });
//                paytypeCb[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            paytypeCb[0].setChecked(false);
//                            paytypeCb[2].setChecked(false);
//                            payChannel = "wxpay";
//                        }
//                    }
//                });
//                paytypeCb[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            paytypeCb[0].setChecked(false);
//                            paytypeCb[1].setChecked(false);
//                            payChannel = "alipay";
//                        }
//                    }
//                });
//
//                //需要的钱和成长金
//                tv_price_symbol.setVisibility(View.GONE);
//                StringBuilder price = TextDisposeUtils.getTotalAmount("￥",discountAmount,coupon);
//                moneyPayTv.setText(price);
//
//                commitPayTv.setOnClickListener(OrderEnsureActivity.this);
//                closeIv.setOnClickListener(OrderEnsureActivity.this);
//            }
//        }).setOutsideTouchable(true).create();
//        commonPopupWindow.showAtLocation(contentRl, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 地址回传
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "requestCode=" + requestCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ll_address_nodata.setVisibility(View.GONE);
                ll_address_data.setVisibility(View.VISIBLE);
                name = data.getExtras().getString("name");
                tv_name.setText(data.getExtras().getString("name"));
                tv_phone.setText(data.getExtras().getString("phone"));
                tv_address_detail.setText(data.getExtras().getString("detaiaddress"));
                addressId = data.getExtras().getString("addressId");

                switch (orderEnsureActivityFlag) {
                    case 0:    //直接购买处理价格
                        dealPrice(addressId);
                        break;
                    case 1:    //购物车购买处理价格
                        dealCartPrice(addressId, dealCartPriceBeanList);
                        break;
                    default:
                        break;
                }
            } else {
//                Toast.makeText(this, "未选择地址信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDismiss() {
    }

    /**
     * activity 界面异常退出数据恢复
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");

        mall_price_true = savedInstanceState.getString("mall_price_true");
        jd_price_true = savedInstanceState.getString("jd_price_true");
        shop_number = savedInstanceState.getInt("shop_number");
        totalAccount = savedInstanceState.getString("mall_price_true");
        property = savedInstanceState.getString("property");
        desc = savedInstanceState.getString("productDesc");
        productNo = savedInstanceState.getString("productNo");

        tv_mall_price.setText("¥" + mall_price_true);
        tv_jd_price.setText("¥" + jd_price_true);
        String ecnomicalMon = DealEcnomicalMoneyUtils.get(jd_price_true, mall_price_true, shop_number);
        tv_econom_money.setText("省¥" + ecnomicalMon);
        tv_money_total.setText("付款金额：¥" + mall_price_true);
        if (!TextUtils.isEmpty(desc)) {
            tv_productModeDesc.setText(desc.substring(0, desc.length() - 1));
        }
        tv_goods_number.setText("x" + shop_number);
        tv_shop_number.setText("共" + shop_number + "件商品");
        tv_money_ensure.setText("¥ " + mall_price_true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_mall_order_ensure_close:
//                commonPopupWindow.dismiss();
//                break;
//            case R.id.dialog_confirm_pay:
//                payment(orderEnsureActivityFlag, payChannel);
//                commonPopupWindow.dismiss();
//                break;
            case R.id.tv_confirm:
                httpGetPayInfo();
                if (mPayDialog != null) mPayDialog.dismiss();
                break;
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    private PayBackEntity2 mPayBackEntity;
    private void httpGetPayInfo(){
        RetrofitClient.getInstance(this).Api()
                .queryPay2()
                .enqueue(new RetrofitCallback<ResultEntity>() {

                    @Override
                    protected void onNext(ResultEntity result) {
                        Object data = result.getData();
                        if(data != null){
                            String d = (String)data ;
                            String str = AESUtils.decrypt("1234567890123456",d);
                            mPayBackEntity = new Gson().fromJson(str, PayBackEntity2.class);
                            DfineAction.WEIXIN_MCH_ID = mPayBackEntity.getWxPaySecret();
                            DfineAction.WEIXIN_APPID = mPayBackEntity.getWxPayAppid();
                            DfineAction.WEIXIN_API_KEY = mPayBackEntity.getWxPaykey();
                            payment(orderEnsureActivityFlag, payChannel);
                        }
                    }
                });
    }

    private void refresh(){
        DfineAction.WEIXIN_MCH_ID = DfineAction.GOODS_WEIXIN_MCH_ID;
        DfineAction.WEIXIN_APPID = DfineAction.GOODS_WEIXIN_APPID;
        DfineAction.WEIXIN_API_KEY = DfineAction.GOODS_WEIXIN_API_KEY;
        DfineAction.WEIXIN_APPSECRET = DfineAction.GOODS_WEIXIN_APPSECRET;
    }

    /**
     * 支付请求
     *
     * @param orderEnsureActivityFlag
     * @param payChannel
     */
    private void payment(final int orderEnsureActivityFlag, final String payChannel) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        RequestBody requestBody = null;     // json 请求 body
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();   //第三方 map转json 工具
        Map<String, Object> params = new HashMap<>();
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = null;
        params.put("appId", "dudu");
        params.put("uid", VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId));
        params.put("deliveryId", addressId);
        params.put("payChannel", payChannel);
        params.put("source", "android");
        params.put("isNew", "y");
        switch (orderEnsureActivityFlag) {
            case 0:   //直接下单
                params.put("products", payToCartPayParam(productNo, shop_number + "", desc.substring(0, desc.length() - 1), property, columnId));
                if (!StringUtils.isEmpty(isSeckill)) {
                    switch (isSeckill) {
                        case "dudu":
                            params.put("orderType", "1");
                            params.put("seckillProductId", "");
                            break;
                        case "y":
                            params.put("orderType", "2");
                            params.put("seckillProductId", seckillProductId);
                            break;
                        case "jingDong":
                            params.put("orderType", "3");
                            params.put("seckillProductId", "");
                            break;
                        case "duduSeckill":
                            params.put("orderType", "4");
                            params.put("holidaySeckillProductId", holidaySeckillProductId);
                            break;
                        default:
                            params.put("orderType", "1");
                            params.put("seckillProductId", "");
                            break;
                    }
                }
                String s = gson.toJson(params);
                requestBody = RequestBody.create(JSON_TYPE, gson.toJson(params));
                Log.e(TAG, "直接购买param===>" + gson.toJson(params));
                break;
            case 1:    //购物车下单
                if (strList != null) {
                    params.put("products", getProductParem());
                }
                requestBody = RequestBody.create(JSON_TYPE, gson.toJson(params));
                Log.e(TAG, "购物车下单param===>" + gson.toJson(params));
                break;
            default:
                break;
        }
        call = api.cartBuyProduct(requestBody);
        call.enqueue(new retrofit2.Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                switch (payChannel) {
                    case "redpay":    //红包
                        showLoginPwdInputPop();
                        loadingDialog.setLoadingDialogDismiss();
                        Log.e(TAG, "红包下单msg===>" + result.getMsg());
                        if (result.getCode() == 0) {
                            deleteDbData();
                            switch (orderEnsureActivityFlag) {
                                case 0: //直接购买
                                    skipPage(PayDoneActivity.class, "discountAmount", discountAmount);
                                    break;
                                case 1: //购物车购买
                                    skipPage(PayDoneActivity.class, "discountAmount", cartDiscountAmount);
                                    break;
                                default:
                                    break;
                            }
                            finish();
                        } else {
                            redpaymsg = result.getMsg();
                            Toast.makeText(OrderEnsureActivity.this, redpaymsg, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "wxpay":     //微信
                        if (REQUEST_CODE != result.getCode()) {
                            wxpayfailmsg = result.getMsg();
                            loadingDialog.setLoadingDialogDismiss();
                            Toast.makeText(OrderEnsureActivity.this, wxpayfailmsg, Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            WxPayEntity entity = JSON.parseObject(result.getData().toString(), WxPayEntity.class);
                            String prepay_id = entity.getPrepayid();
                            if (!StringUtils.isEmpty(prepay_id)) {
                                isSelectedAddress = false;
                                PayUtils.getInstance(OrderEnsureActivity.this).toWXPay(OrderEnsureActivity.this, prepay_id);
                                deleteDbData();               //删除购物车数据
                            } else {
                                String msg_res = "服务器生成订单失败.";
                                loadingDialog.setLoadingDialogDismiss();
                                Toast.makeText(OrderEnsureActivity.this, msg_res, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case "alipay":    //支付宝
                        loadingDialog.setLoadingDialogDismiss();
                        if (REQUEST_CODE != result.getCode()) {
                            alipayfailmsg = result.getMsg();
                            Toast.makeText(OrderEnsureActivity.this, alipayfailmsg, Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            String orderInfo = result.getData().toString();
                            try {
                                String body = "购买商品";
                                PayUtils.getInstance(OrderEnsureActivity.this).toAliPay(orderInfo, 0);
                                deleteDbData();               //删除购物车数据
                            } catch (Exception e) {
                                msgString = getResources().getString(R.string.request_failinfo);
                                Toast.makeText(OrderEnsureActivity.this, msgString, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 再次输入登录密码后发起红包支付请求
     */
    private void showLoginPwdInputPop() {
    }

    /**
     * 直接下单参数转成购物车下单参数
     *
     * @param productNo
     * @param shopNum
     * @param desc
     * @param property
     * @return
     */
    private List payToCartPayParam(String productNo, String shopNum, String desc, String property, String columnId) {
        cartBuyProductsBeanList.clear();
        CartBuyProductsBean cartBuyProductsBean = new CartBuyProductsBean();
        cartBuyProductsBean.setProductNo(productNo);
        cartBuyProductsBean.setProductDesc(desc);
        cartBuyProductsBean.setProductNum(shopNum);
        cartBuyProductsBean.setProperty(property);
        cartBuyProductsBean.setColumnId(columnId);
        cartBuyProductsBean.setDeliveryType(deliveryType);
        cartBuyProductsBeanList.add(cartBuyProductsBean);
        return cartBuyProductsBeanList;
    }

    /**
     * 配置购物车下单参数
     */
    private List getProductParem() {
        cartBuyProductsBeanList.clear();
        for (int i = 0; i < strList.size(); i++) {
            CartBuyProductsBean cartBuyProductsBean = new CartBuyProductsBean();
            OrderEnsureEntity entity = strList.get(i);
            String desc = entity.getProductDesc();
            String columnId = entity.getColumnId();
            String deliveryType = entity.getDeliveryType();
            cartBuyProductsBean.setProductNo(entity.getProductNo());
            cartBuyProductsBean.setProductDesc(desc.substring(0, desc.length() - 1));
            cartBuyProductsBean.setProperty(entity.getProductPropery());
            cartBuyProductsBean.setProductNum(entity.getProductNum() + "");
            cartBuyProductsBean.setColumnId(columnId);
            cartBuyProductsBean.setDeliveryType(deliveryType);
            cartBuyProductsBeanList.add(cartBuyProductsBean);
        }
        return cartBuyProductsBeanList;
    }

    /**
     * 根据Id删除相应数据(成功失败)
     */
    private void deleteDbData() {
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(OrderEnsureActivity.this).getDaoSession().getShoppingItemsBeanDao();
        if (strList != null && strList.size() > 0) {
            for (int i = 0; i < strList.size(); i++) {
                Long carIdL = strList.get(i).getId();
                shoppingItemsBeanDao.deleteByKey(carIdL);
            }
        }
    }

    @Override
    public void onPaySuccess() {
        showMessage("支付成功");
    }

    @Override
    public void onPayError() {
        showMessage("支付成功");
    }

    @Override
    public void onPayCancel() {
        showMessage("取消支付");
    }
}

