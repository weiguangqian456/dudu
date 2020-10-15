package com.weiwei.salemall.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;

import com.hwtx.dududh.wxapi.WXPayEntryActivity;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.salemall.adapter.MoreOrderAdaper;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.ExpressInfoBean;
import com.weiwei.salemall.bean.OrderDetailBean;
import com.weiwei.salemall.bean.OrderEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.WxPayEntity;
import com.weiwei.salemall.inter.PayResultListener;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.PayListenerUtils;
import com.weiwei.salemall.utils.PayUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.utils.PayUtils.showMessage;

/**
 * @author Created by EDZ on 2018/5/23.
 *         订单详情
 */

public class OrderDetailActivity extends TempAppCompatActivity implements View.OnClickListener, PayResultListener {
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.rl_bottom_menu)
    RelativeLayout botteomMenuRl;
    @BindView(R.id.iv_flag_top)
    ImageView iv_flag_top;
    @BindView(R.id.ll_tip)
    LinearLayout ll_tip;
    @BindView(R.id.tv_tip_first)
    TextView tv_tip_first;
    @BindView(R.id.tv_tip_second)
    TextView tv_tip_second;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.iv_shop_image)
    ImageView iv_shop_image;
    @BindView(R.id.tv_goods_title)
    TextView tv_goods_title;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_goods_number)
    TextView tv_goods_number;
    @BindView(R.id.tv_shop_num)
    TextView tv_shop_num;
    @BindView(R.id.tv_mall_price)
    TextView tv_mall_price;
    @BindView(R.id.tv_jd_price)
    TextView tv_jd_price;
    @BindView(R.id.tv_save_money)
    TextView tv_save_money;
    @BindView(R.id.tv_order_number)
    TextView tv_order_number;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.btn_ensure_receive)
    Button btn_pay;
    @BindView(R.id.tv_mail_status)
    TextView tv_mail_status;
    @BindView(R.id.btn_cancle_order)
    Button btn_cancle_order;
    @BindView(R.id.tv_desc)
    TextView productDescTv;

    @BindView(R.id.ll_logistics)
    LinearLayout ll_logistics;
    @BindView(R.id.tv_logistics)
    TextView tv_logistics;
    @BindView(R.id.tv_last_logits)
    TextView tv_last_logits;
    @BindView(R.id.tv_logits_tip)
    TextView tv_logits_tip;
    @BindView(R.id.sv_order)
    ScrollView orderSv;
    @BindView(R.id.ll_order)
    LinearLayout orderLl;
    @BindView(R.id.rv_orderDetai)
    RecyclerView orderDatailRv;
    @BindView(R.id.tv_total_money)
    TextView totalMoneyTv;
    @BindView(R.id.iv_number_flag)
    ImageView iv_number_flag;

    private MoreOrderAdaper moreOrderAdaper;

    private List<AddressEntity> recordsBeanList;
    private CommonPopupWindow commonPopupWindow;
    private CheckBox[] paytypeCb = new CheckBox[3];

    private int paytype = 1;
    private String payChannel = "redpay";
    public static final int REDBAG = 1;
    public static final int WX = 2;
    public static final int ALIPAY = 3;
    private String orderStatus;
    private String orderId;
    private String orderType;

    private static final String TAG = "OrderDetailActivity";
    private String logistics_info;

    //===============支付宝
    private String AlipayOrderid;
    private String notify_url;
    private String mPrice;
    private String msgString;
    public String orderSubject = "下单";
    private String msg;
    private String orderInfo;

    //ui 数据
    private String image;
    private String productName;
    private int productNum;
    private String productPrice;
    private String jdPrice;
    private String economicalMoney;
    private String totalAmount;         //小计
    private String productAmount;       //商品金额
    private String isFreePostage;
    private String postage;
    private String productModeDesc;
    private String orderNo;
    private String productNo;
    private String orderTime;
    private String address;
    private String consignee;
    private String phone;
    private String logisticInfo = "";
    private String expressNo = "";
    private String expressName = "";
    private String logits_detail;
    private int totolNum;


    private  OrderDetailBean firstBean;
    /**
     * 物流信息
     */
    private String logitsInfoGet = "";
    private String logitsInfoSend = "";
    private String logitsInfoExpress = "";
    private String deliveryType = "";
    private String deliveryMsg = "";

    private String localBy;

    /**
     * 多选支付
     */
    private int orderNum;
    private List<OrderDetailBean> orderDetailBeanList = new ArrayList<>();

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        PayListenerUtils.getInstance(this).addListener(this);
        initView();
        initData();

    }

    @Override
    protected void init() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayListenerUtils.getInstance(this).removeListener(this);
    }

    private void initView() {
        titleTv.setText("订单详情");
        tv_jd_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ll_logistics.setOnClickListener(this);
        backRl.setOnClickListener(this);
        orderLl.setOnClickListener(this);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        orderStatus = intent.getStringExtra("orderStatus");
        orderNo = intent.getStringExtra("orderNo");
        productNo = intent.getStringExtra("productNo");
        localBy = intent.getStringExtra("localBy");

        initOrderData(orderId);
        updataUi(orderStatus);
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     */
    private void initOrderData(final String orderId) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        orderSv.setVisibility(View.GONE);
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getOrderDetail(orderId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                orderSv.setVisibility(View.VISIBLE);
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    OrderEntity shopDataEntity = JSON.parseObject(result.getData().toString(), OrderEntity.class);
                    //购物车多选
                    if (shopDataEntity.getOrderDetails() != null && shopDataEntity.getOrderDetails().size() > 0) orderNum = shopDataEntity.getOrderDetails().size();
                    orderDetailBeanList = shopDataEntity.getOrderDetails();
                    ExpressInfoBean bean = shopDataEntity.getExpressInfo();
                    totolNum = shopDataEntity.getTotalNum();
                    totalAmount = shopDataEntity.getTotalAmount();
                    productAmount = shopDataEntity.getProductAmount();
                    isFreePostage = shopDataEntity.getIsFreePostage();
                    postage = shopDataEntity.getPostage();
                    orderTime = shopDataEntity.getOrderTime();
                    orderNo = shopDataEntity.getOrderNo();
                    orderStatus = shopDataEntity.getOrderStatus();
                    orderType = shopDataEntity.getOrderType();
                    if (orderNum > 1) {
                    } else {
                        firstBean = orderDetailBeanList.get(0);
                        image = JudgeImageUrlUtils.isAvailable(firstBean.getPicture());
                        productName = firstBean.getProductName();
                        productNum = firstBean.getProductNum();
                        productModeDesc = firstBean.getProductModeDesc();
                        productPrice = TextDisposeUtils.getEndPrice(firstBean.isShowCoupon(),firstBean.getProductPrice(),firstBean.getCoupon()).toString();//firstBean.getProductPrice();
                        jdPrice = firstBean.getJdPrice();
                        economicalMoney = DealEcnomicalMoneyUtils.get(jdPrice, firstBean.getProductPrice(), productNum);
                        deliveryType = firstBean.getDeliveryType();
                    }
                    if (orderNum > 1) {
                        orderLl.setVisibility(View.GONE);
                        orderDatailRv.setVisibility(View.VISIBLE);
                        //orderType为1  表示秒杀商品只能单个购买
                        moreOrderAdaper = new MoreOrderAdaper(OrderDetailActivity.this, orderDetailBeanList, orderId, orderStatus, false, "1",localBy);
                        orderDatailRv.setLayoutManager(new WrapContentLinearLayoutManager(OrderDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                        orderDatailRv.setNestedScrollingEnabled(false);
                        orderDatailRv.setAdapter(moreOrderAdaper);
                    } else {
                        Glide.with(OrderDetailActivity.this).load(image).apply(new RequestOptions().error(R.drawable.mall_logits_default)).into(iv_shop_image);
                        tv_goods_title.setText(productName);
                        tv_goods_number.setText("x" + productNum);
                        tv_mall_price.setText(productPrice);
                        tv_jd_price.setText("¥" + jdPrice);
                        productDescTv.setText(productModeDesc);
                        if (!StringUtils.isEmpty(deliveryType)) {tv_save_money.setText(deliveryType);}
                        if (!StringUtils.isEmpty(orderType)) {
                            iv_number_flag.setVisibility(View.GONE);
//                            iv_number_flag.setVisibility(orderType.equals("1") ? View.GONE : View.VISIBLE);
                        }
                    }

                    if (!StringUtils.isEmpty(postage)) {tv_mail_status.setText("¥" + postage);}

                    tv_shop_num.setText("共" + totolNum + "件商品");
                    totalMoneyTv.setText("合计：¥" + totalAmount);
                    tv_money.setText("¥" + productAmount);
                    tv_order_number.setText(orderNo);
                    tv_time.setText(orderTime);

                    //物流相关
                    if (bean != null) {
                        address = bean.getAddress();      //地址
                        consignee = bean.getConsignee();  //收货人
                        phone = bean.getPhone();          //联系电话
                        logisticInfo = bean.getLogisticInfo();   //物流信息
                        expressNo = bean.getExpressNo();         //物流编号
                        expressName = bean.getExpressName();     //物流名称

                        if (logisticInfo != null) {
                            if (!StringUtils.isEmpty(expressName) && !StringUtils.isEmpty(expressNo)) {
                                logitsInfoExpress = expressName + "  " + expressNo;
                            } else {
                                logitsInfoExpress = "暂无相关物流和运单号信息";
                            }
                            if ("暂无物流信息".equals(logisticInfo)) {
                                logitsInfoGet = "暂无物流信息";
                                logitsInfoSend = "商家正在处理订单，请耐心等候！";
                            } else {
                                try {
                                    JSONArray jsonArray = new JSONArray(logisticInfo);
                                    logitsInfoSend = "商品已发出，请耐心等候！";
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        logitsInfoGet = jsonObject.getString("AcceptStation") + "\n" + jsonObject.getString("AcceptTime");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            logitsInfoExpress = expressName;
                            logitsInfoSend = "当前收货方式为自提，暂无相关物流信息";
                            logitsInfoGet = "当前收货方式为自提，暂无相关物流信息";
                        }
                    }

                    tv_address.setText(address);
                    tv_name.setText(consignee);
                    tv_phone.setText(phone);
                    tv_logistics.setText(logitsInfoExpress);
                    tv_logits_tip.setText(logitsInfoSend);
                    tv_last_logits.setText(logitsInfoGet);

                    //根据订单状态显示界面
                    updataUi(orderStatus);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
                orderSv.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 根据订单状态更新Ui
     *
     * @param orderStatus
     */
    private void updataUi(String orderStatus) {
        switch (orderStatus) {
            case "-1": //已取消
                iv_flag_top.setImageResource(R.drawable.ic_stay_comment);
                tv_tip_first.setText(R.string.order_tip_1);
                tv_tip_second.setText(R.string.order_tip_2);
                botteomMenuRl.setVisibility(View.GONE);
                ll_logistics.setVisibility(View.GONE);
                break;
            case "0": //待付款
                iv_flag_top.setImageResource(R.drawable.ic_stay_payment);
                tv_tip_first.setText(R.string.order_tip_3);
                tv_tip_second.setText(R.string.order_tip_4);
                botteomMenuRl.setVisibility(View.VISIBLE);
                ll_logistics.setVisibility(View.GONE);
                btn_cancle_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(OrderDetailActivity.this).
                                setMessage("确定要取消订单吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelOrder(orderId);
                            }
                        }).setNegativeButton("再看看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                });
                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paytype = REDBAG;
                        commonPopupWindow = new CommonPopupWindow.Builder(OrderDetailActivity
                                .this).setView(R.layout.pop_pay_way).setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                .setAnimationStyle(R.style.PopupWindowAnimation).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                            @Override
                            public void getChildView(View view, int layoutResId) {
                                TextView tv_money_ensure_dialog = (TextView) view.findViewById(R.id.tv_money_ensure_dialog);
                                TextView dialog_confirm_pay = (TextView) view.findViewById(R.id.dialog_confirm_pay);
                                ImageView iv_close = (ImageView) view.findViewById(R.id.iv_mall_order_ensure_close);
                                tv_money_ensure_dialog.setText(totalAmount);
                                paytypeCb[0] = (CheckBox) view.findViewById(R.id.cb_redbag);
                                paytypeCb[1] = (CheckBox) view.findViewById(R.id.cb_wx);
                                paytypeCb[2] = (CheckBox) view.findViewById(R.id.cb_alipay);
                                paytypeCb[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            paytypeCb[1].setChecked(false);
                                            paytypeCb[2].setChecked(false);
                                            paytype = REDBAG;
                                        }
                                    }
                                });
                                paytypeCb[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            paytypeCb[0].setChecked(false);
                                            paytypeCb[2].setChecked(false);
                                            paytype = WX;
                                        }
                                    }
                                });
                                paytypeCb[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            paytypeCb[0].setChecked(false);
                                            paytypeCb[1].setChecked(false);
                                            paytype = ALIPAY;
                                        }
                                    }
                                });
                                dialog_confirm_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        payment(paytype, orderNo);
                                        commonPopupWindow.dismiss();
                                    }
                                });
                                iv_close.setOnClickListener(OrderDetailActivity.this);
                            }
                        }).setOutsideTouchable(true).create();
                        commonPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    }
                });
                break;
            case "1": //待发货
                iv_flag_top.setImageResource(R.drawable.ic_stay_shipments);
                tv_tip_first.setText(R.string.order_tip_5);
                tv_tip_second.setText(R.string.order_tip_6);
                botteomMenuRl.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(localBy) && localBy.equals("1")) {
                    btn_cancle_order.setVisibility(View.VISIBLE);
                    btn_pay.setText("催单");
                }else {
                    btn_cancle_order.setVisibility(View.GONE);
                    btn_pay.setText(R.string.tip_send);
                }

                ll_logistics.setVisibility(View.GONE);
                btn_pay.setOnClickListener(v -> ToastUtils.show(OrderDetailActivity.this, "已提醒卖家及时发货"));
                btn_cancle_order.setOnClickListener(v -> new AlertDialog.Builder(OrderDetailActivity.this).
                        setMessage("确定要取消订单吗？").setPositiveButton("确定", (dialog, which) -> cancelOrder(orderId)).setNegativeButton("再看看", (dialog, which) -> {
                        }).show());
                break;
            case "2": //待收货
                iv_flag_top.setImageResource(R.drawable.ic_stay_receive_cargo);
                tv_tip_first.setText(R.string.order_tip_7);
                tv_tip_second.setText(R.string.order_tip_8);
                botteomMenuRl.setVisibility(View.VISIBLE);
                btn_cancle_order.setVisibility(View.GONE);
                btn_pay.setText(R.string.ensure_receive);
                btn_pay.setOnClickListener(v -> new AlertDialog.Builder(OrderDetailActivity.this).
                        setMessage("商品无误，确认签收？").setPositiveButton("确定", (dialog, which) -> finishOrder(orderId)).setNegativeButton("取消", (dialog, which) -> {
                        }).show());
                ll_logistics.setVisibility(View.VISIBLE);
                break;
            case "3": //待评论
                iv_flag_top.setImageResource(R.drawable.ic_stay_comment);
                tv_tip_first.setText(R.string.order_tip_9);
                tv_tip_second.setText(R.string.order_tip_10);
                botteomMenuRl.setVisibility(View.VISIBLE);
                btn_cancle_order.setVisibility(View.GONE);
                btn_pay.setText(R.string.add_comment);
                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] key = new String[]{"orderNo", "productNo"};
                        String[] value = new String[]{orderNo, productNo};
                        SkipPageUtils.getInstance(OrderDetailActivity.this).skipPage(CommentActivity.class, key, value);
                    }
                });
                ll_logistics.setVisibility(View.VISIBLE);
                break;
            case "4": //已完成
                iv_flag_top.setImageResource(R.drawable.ic_stay_comment);
                tv_tip_first.setText(R.string.order_tip_9);
                tv_tip_second.setText(R.string.order_tip_10);
                botteomMenuRl.setVisibility(View.GONE);
                break;
            case "5": //已退款
                iv_flag_top.setImageResource(R.drawable.ic_stay_comment);
                tv_tip_first.setText(R.string.order_tip_1);
                tv_tip_second.setText(R.string.order_tip_2);
                botteomMenuRl.setVisibility(View.GONE);
                ll_logistics.setVisibility(View.GONE);
                break;
            case "6": //出库中
                iv_flag_top.setImageResource(R.drawable.ic_stay_shipments);
                tv_tip_first.setText(R.string.order_tip_11);
                tv_tip_second.setText(R.string.order_tip_12);
                botteomMenuRl.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 确认收货
     *
     * @param orderId
     */
    private void finishOrder(String orderId) {
        ApiService api = RetrofitClient.getInstance(OrderDetailActivity.this).Api();
        Map<String, String> param = new HashMap<>();
        param.put("version", KcQcodeActivity.getVersionCode());
        retrofit2.Call<ResultEntity> call = api.finishOrder(orderId, param);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Log.e(TAG, "确认收货msg===>" + result.getMsg());
                if (REQUEST_CODE == result.getCode()) {
                    ToastUtils.show(OrderDetailActivity.this, "该订单已确认收货");
                    btn_pay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    private void cancelOrder(String orderId) {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.cancelOrder(orderId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Log.e(TAG, "取消订单msg===>" + result.getMsg());
                if (REQUEST_CODE == result.getCode()) {
                    btn_cancle_order.setVisibility(View.GONE);
                    btn_pay.setText("该订单已失效");
                    tv_tip_first.setText(R.string.order_tip_1);
                    tv_tip_second.setText(R.string.order_tip_2);
                    btn_pay.setClickable(false);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mall_order_ensure_close:
                commonPopupWindow.dismiss();
                break;
            case R.id.ll_logistics:
                skipPage(OrderLogitsActivity.class, "orderId", orderId);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_order:
                if(firstBean != null){
                    String type = firstBean.getIsExchange().equals("y") ? getString(R.string.exchange_region) : "";
                    String[] key = new String[]{"productNo","type","columnId"};
                    String[] value = new String[]{firstBean.getProductNo(),type,firstBean.getColumnId()};
                    SkipPageUtils.getInstance(this).skipPage(GoodsDetailActivity.class, key, value);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 支付接口
     *
     * @param paytype
     * @param orderNo
     */
    private void payment(int paytype, String orderNo) {
        final Message message = new Message();
        final Bundle bundle = new Bundle();
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = null;
        Map<String, String> param = new HashMap<>();
        switch (paytype) {
            case 1:  //红包支付
                payChannel = "redpay";
                break;
            case 2:  //微信支付
                payChannel = "wxpay";
                break;
            case 3:  //支付宝支付
                payChannel = "alipay";
                break;
            default:
                break;
        }
        param.put("payChannel", payChannel);
        param.put("isNew", "y");
        call = api.continueBuyProduct(orderNo, param);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                switch (payChannel) {
                    case "redpay":    //红包
                        Log.e(TAG, "红包下单msg===>" + result.getMsg());
                        msg = result.getMsg();
                        if (REQUEST_CODE == result.getCode()) {
                            skipPage(PayDoneActivity.class, "discountAmount", totalAmount);
                            finish();
                        } else {
                            ToastUtils.show(OrderDetailActivity.this, msg);
                        }
                        break;
                    case "wxpay":     //微信
                        Log.e("微信下单msg", "微信下单msg===>" + result.getMsg());
                        if (REQUEST_CODE == result.getCode()) {
                            WxPayEntity entity = JSON.parseObject(result.getData().toString(), WxPayEntity.class);
                            String prepay_id = entity.getPrepayid();

                            if (!StringUtils.isEmpty(prepay_id)) {
                                PayUtils.getInstance(OrderDetailActivity.this).toWXPay(OrderDetailActivity.this, prepay_id);
                            } else {
                                String msg_res = "服务器生成订单失败.";
                                ToastUtils.show(OrderDetailActivity.this, msg_res);
                                skipPage(WXPayEntryActivity.class, "message", TextUtils.isEmpty(msg_res) ? "" : msg_res);
                            }
                        }
                        break;
                    case "alipay":    //支付宝
                        if (REQUEST_CODE == result.getCode()) {
                            orderInfo = result.getData().toString();
                            PayUtils.getInstance(OrderDetailActivity.this).toAliPay(orderInfo, 0);
                        } else {
                            ToastUtils.show(OrderDetailActivity.this, result.getMsg());
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
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
