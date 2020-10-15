package com.weiwei.salemall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.merchant.activity.MoveLocationActivity;
import com.weiwei.salemall.activity.CommentActivity;
import com.weiwei.salemall.activity.OrderLogitsActivity;
import com.weiwei.salemall.activity.PayDoneActivity;
import com.weiwei.salemall.bean.OrderDetailBean;
import com.weiwei.salemall.bean.OrderEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.WxPayEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PayUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/5/23.
 * 订单界面适配器
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {
    private static final String TAG = "MyOrderAdapter";
    private Context context;
    private List<OrderEntity> recordsBeanList;
    private MoreOrderAdaper moreOrderAdaper;
    private List<OrderDetailBean> orderDetailBeanList = new ArrayList<>();
    String productNo = null;
    private String productAmount;       //商品金额
    private String orderType = "";      //订单类型
    private int paytype = 1;
    public static final int REDBAG = 1;
    public static final int WX = 2;
    public static final int ALIPAY = 3;
    private String payChannel = "redpay";
    private CommonPopupWindow commonPopupWindow;
    private CheckBox[] paytypeCb = new CheckBox[3];
    private String msg;

    /**
     * 可以点击
     */
    private boolean canClick = true;

    public MyOrderAdapter(Context context, List<OrderEntity> recordsBeanList) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_my_orders_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        freshUi(holder, position);
    }

    /**
     * 对数据进行处理
     *
     * @param holder
     * @param position
     */
    private void freshUi(final MyViewHolder holder, final int position) {
        orderDetailBeanList = recordsBeanList.get(position).getOrderDetails();
        OrderEntity orderEntity = recordsBeanList.get(position);
        final String orderId = orderEntity.getOrderId();
        final String orderStatus = orderEntity.getOrderStatus();
        final String orderNo = orderEntity.getOrderNo();
        orderType = orderEntity.getOrderType();
        int ordeDetailNum = orderDetailBeanList.size();
        productAmount = orderEntity.getProductAmount();

        //购物车多选提交
        holder.moreOrderRv.setVisibility(View.VISIBLE);
        holder.orderDetaiLl.setVisibility(View.GONE);
        moreOrderAdaper = new MoreOrderAdaper(context, orderDetailBeanList, orderId, orderStatus, canClick, orderType,orderEntity.getLocalBy());
        holder.moreOrderRv.setLayoutManager(new LinearLayoutManager(context));
        holder.moreOrderRv.setNestedScrollingEnabled(false);
        holder.moreOrderRv.setAdapter(moreOrderAdaper);

        String orderTime = orderEntity.getOrderTime();
        if (!StringUtils.isEmpty(orderTime)) {
            holder.tv_time.setText(orderTime);
        }

//        holder.tv_jd_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        String totalAmount = orderEntity.getTotalAmount();
        if (!StringUtils.isEmpty(totalAmount)) {
            holder.tv_total_money.setText("合计：¥" + totalAmount);
        }

        String isFreePostage = orderEntity.getIsFreePostage();
        String postage = orderEntity.getPostage();
        if (!StringUtils.isEmpty(postage)) {
            holder.tv_mail_status.setText("¥" + postage);
        }

        if (!StringUtils.isEmpty(productAmount)) {
            holder.tv_money.setText("¥" + productAmount);
        }

        int totalNum = orderEntity.getTotalNum();
        if (totalNum != 0) {
            holder.tv_shop_number.setText("共" + totalNum + "件商品");
        }

        if (orderDetailBeanList != null && orderDetailBeanList.size() > 0) {
            productNo = orderDetailBeanList.get(0).getProductNo();
        }
        if(!TextUtils.isEmpty(orderEntity.getStoreName())) {
            holder.tvName.setVisibility(View.VISIBLE);
        }else {
            holder.tvName.setVisibility(View.GONE);
        }
        holder.tvName.setText(orderEntity.getStoreName());

        updateUi(holder, position, orderStatus, productNo, orderId, orderNo, totalAmount,orderEntity.getLocalBy());
    }

    /**
     * item中按钮的点击事件和显示
     *
     * @param holder
     * @param orderStatus
     */
    private void updateUi(MyViewHolder holder, final int position, String orderStatus, final String productNo, final String orderId, final String orderNo, final String
            totalAmount,String localBy) {
        switch (orderStatus) {
            case "-1": //已取消订单（全部）
                holder.shop_status.setText(R.string.order_cancel);
                holder.rl_bottom.setVisibility(View.GONE);
                holder.tvLocation.setVisibility(View.GONE);
                break;
            case "0": //待付款
                holder.rl_bottom.setVisibility(View.VISIBLE);
                holder.shop_status.setText(R.string.order_wait_pay);
                holder.btn_cancle_order.setVisibility(View.VISIBLE);
                holder.btn_cancle_order.setText(R.string.cancelorder);
                holder.btn_ensure_receive.setVisibility(View.VISIBLE);
                holder.btn_ensure_receive.setText(R.string.pay);
                holder.tvLocation.setVisibility(View.GONE);
                holder.btn_ensure_receive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPaymentPop(orderNo, totalAmount);
                    }
                });

                holder.btn_cancle_order.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context).
                                setMessage("确定取消订单？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recordsBeanList.remove(position);
                                notifyItemRangeChanged(position, getItemCount());
                                notifyItemRemoved(position);
                                cancelOrder(orderId);
                            }
                        }).setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                });
                break;
            case "1": //待发货
                holder.shop_status.setText(R.string.order_wait_send);
                holder.rl_bottom.setVisibility(View.VISIBLE);
                if(localBy != null && localBy.equals("1")) {
                    holder.btn_cancle_order.setVisibility(View.VISIBLE);
                    holder.btn_ensure_receive.setText("催单");
                }else {
                    holder.btn_cancle_order.setVisibility(View.GONE);
                    holder.btn_ensure_receive.setText(R.string.tip_send);
                }
                holder.btn_ensure_receive.setVisibility(View.VISIBLE);
                holder.tvLocation.setVisibility(View.GONE);
                holder.btn_ensure_receive.setOnClickListener(v -> showTip("已提醒卖家及时发货"));
                holder.btn_cancle_order.setOnClickListener(v -> new AlertDialog.Builder(context).
                        setMessage("确定取消订单？").setPositiveButton("确定", (dialog, which) -> {
                            recordsBeanList.remove(position);
                            notifyItemRangeChanged(position, getItemCount());
                            notifyItemRemoved(position);
                            cancelOrder(orderId);
                        }).setNegativeButton("再想想", (dialog, which) -> {
                        }).show());

                break;
            case "2": //待收货
                holder.shop_status.setText(R.string.order_wait_receive);
                holder.rl_bottom.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(localBy) && localBy.equals("1")) {
                    holder.tvLocation.setVisibility(View.VISIBLE);
                    holder.btn_cancle_order.setVisibility(View.GONE);
                    holder.btn_ensure_receive.setVisibility(View.GONE);
                }else {
                    holder.btn_cancle_order.setVisibility(View.VISIBLE);
                    holder.btn_cancle_order.setText(R.string.logits);
                    holder.btn_ensure_receive.setVisibility(View.VISIBLE);
                    holder.btn_ensure_receive.setText(R.string.ensure_receive);
                    holder.tvLocation.setVisibility(View.GONE);
                }
                holder.tvLocation.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, MoveLocationActivity.class);
                        mContext.startActivity(intent);

                    }
                });
                holder.btn_ensure_receive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context).
                                setMessage("商品无误，确认签收？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recordsBeanList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());
                                finishOrder(orderId);
                            }
                        }).setNegativeButton("取消", (dialog, which) -> {
                        }).show();
                    }
                });
                holder.btn_cancle_order.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SkipPageUtils.getInstance(context).skipPage(OrderLogitsActivity.class, "orderId", orderId);
                    }
                });
                break;
            case "3": //待评论
                holder.shop_status.setText(R.string.order_wait_comment);
                holder.rl_bottom.setVisibility(View.VISIBLE);
                holder.btn_cancle_order.setVisibility(View.GONE);
                holder.btn_ensure_receive.setVisibility(View.VISIBLE);
                holder.btn_ensure_receive.setText(R.string.add_comment);
                holder.tvLocation.setVisibility(View.GONE);
                holder.btn_ensure_receive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] key = new String[]{"orderNo", "productNo"};
                        String[] value = new String[]{orderNo, productNo};
                        SkipPageUtils.getInstance(context).skipPage(CommentActivity.class, key, value);
                    }
                });
                break;
            case "4": //已评价
                holder.btn_ensure_receive.setText("已评论");
                holder.shop_status.setText(R.string.order_have_comment);
                holder.rl_bottom.setVisibility(View.GONE);
                holder.tvLocation.setVisibility(View.GONE);
                break;
            case "5": //已退款
                holder.shop_status.setText(R.string.order_backmoney);
                holder.rl_bottom.setVisibility(View.GONE);
                holder.tvLocation.setVisibility(View.GONE);
                break;
            case "6": //出库中
                holder.shop_status.setText(R.string.order_outoflibrary);
                holder.rl_bottom.setVisibility(View.GONE);
                holder.tvLocation.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void clear() {
        if (orderDetailBeanList != null) {
            orderDetailBeanList.clear();
        }
        if (recordsBeanList != null) {
            recordsBeanList.clear();
        }
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout viewLl;
        private TextView tvName;
        private TextView tv_time;
        private TextView shop_status;
        private ImageView productImage;
        private TextView tv_goods_title;
        private TextView tv_goods_number, tv_shop_number;
        private TextView tv_mall_price;
        private TextView tv_jd_price;
        private TextView tv_total_money;
        private TextView tv_save_money;
        private TextView tv_money;
        private TextView tv_mail_status;
        private Button btn_ensure_receive;
        private Button btn_cancle_order;
        private LinearLayout orderDetaiLl;
        private RecyclerView moreOrderRv;
        private LinearLayout contentLl;
        private TextView productDesc;
        private RelativeLayout rl_bottom;
        private ImageView iv_number_flag;
        private RoundTextView tvLocation;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            productDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            contentLl = (LinearLayout) itemView.findViewById(R.id.ll_content);
            moreOrderRv = (RecyclerView) itemView.findViewById(R.id.rv_more);
            orderDetaiLl = (LinearLayout) itemView.findViewById(R.id.ll_order);
            viewLl = (LinearLayout) itemView.findViewById(R.id.ll_view);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            shop_status = (TextView) itemView.findViewById(R.id.shop_status);
            productImage = (ImageView) itemView.findViewById(R.id.iv_shop_image);
            tv_goods_title = (TextView) itemView.findViewById(R.id.tv_goods_title);
            tv_goods_number = (TextView) itemView.findViewById(R.id.tv_goods_number);
            tv_shop_number = (TextView) itemView.findViewById(R.id.totalNum);
            tv_mall_price = (TextView) itemView.findViewById(R.id.tv_mall_price);
            tv_jd_price = (TextView) itemView.findViewById(R.id.tv_jd_price);
            tv_save_money = (TextView) itemView.findViewById(R.id.tv_save_money);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
            tv_mail_status = (TextView) itemView.findViewById(R.id.tv_mail_status);
            btn_ensure_receive = (Button) itemView.findViewById(R.id.btn_ensure_receive);
            btn_cancle_order = (Button) itemView.findViewById(R.id.btn_cancle_order);
            tv_total_money = (TextView) itemView.findViewById(R.id.tv_total_money);
            rl_bottom = (RelativeLayout) itemView.findViewById(R.id.rl_bottom);
            iv_number_flag = (ImageView) itemView.findViewById(R.id.iv_number_flag);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }

    private void showPaymentPop(final String orderNo, final String totalAmount) {
        paytype = REDBAG;
        commonPopupWindow = new CommonPopupWindow.Builder(context).setView(R.layout.pop_pay_way).setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT).setAnimationStyle(R.style.PopupWindowAnimation).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
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
                dialog_confirm_pay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payment(paytype, orderNo, totalAmount);
                        commonPopupWindow.dismiss();
                    }
                });
                iv_close.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        commonPopupWindow.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void showTip(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 确认收货
     *
     * @param orderId
     */
    private void finishOrder(String orderId) {
        ApiService api = RetrofitClient.getInstance(mContext).Api();
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
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "确认收货msg===>" + result.getMsg());
                    notifyDataSetChanged();
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
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = api.cancelOrder(orderId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "取消订单msg===>" + result.getMsg());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 继续支付
     *
     * @param paytype
     * @param orderNo
     */
    private void payment(int paytype, String orderNo, final String totalAmount) {
        ApiService api = RetrofitClient.getInstance(mContext).Api();
        retrofit2.Call<ResultEntity> call = null;
        Map<String, String> param = new HashMap<>();
        switch (paytype) {
            case 1:
                payChannel = "redpay";
                break;
            case 2:
                payChannel = "wxpay";
                break;
            case 3:
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
                            SkipPageUtils.getInstance(context).skipPage(PayDoneActivity.class, "discountAmount", totalAmount);
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "wxpay":     //微信
                        Log.e(TAG, "微信下单msg===>" + result.getMsg());
                        if (REQUEST_CODE == result.getCode()) {
                            WxPayEntity entity = JSON.parseObject(result.getData().toString(), WxPayEntity.class);
                            String prepay_id = entity.getPrepayid();
                            if (!StringUtils.isEmpty(prepay_id)) {
                                PayUtils.getInstance((Activity) context).toWXPay(mContext, prepay_id);
                            } else {
                                String msg_res = "服务器生成订单失败.";
                                Toast.makeText(context, msg_res, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case "alipay":    //支付宝
                        if (REQUEST_CODE == result.getCode()) {
                            Log.e(TAG, "支付宝下单msg===>" + result.getMsg());
                            String orderInfo = result.getData().toString();
                            PayUtils.getInstance((Activity) context).toAliPay(orderInfo, 0);
                        } else {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
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
}
