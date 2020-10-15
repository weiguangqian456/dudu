package com.weiwei.salemall.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.TimeLineAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ExpressInfoBean;
import com.weiwei.salemall.bean.LogitsEntity;
import com.weiwei.salemall.bean.OrderDetailBean;
import com.weiwei.salemall.bean.OrderEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.NoScrollListview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/6/11.
 *         订单物流详情
 */

public class OrderLogitsActivity extends TempAppCompatActivity {
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.tv_logistics_factory)
    TextView logisticsFactoryTv;
    @BindView(R.id.tv_logistics_no)
    TextView logisticsNoTv;
    @BindView(R.id.iv_shop)
    ImageView shopImage;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.lv_logits)
    NoScrollListview logitsLv;
    @BindView(R.id.tv_no_logitsinfo)
    TextView noLogitsInfoTv;
    @BindView(R.id.fl_zt)
    FrameLayout ztFl;
    @BindView(R.id.tv_zt_tip)
    TextView ztTipTv;

    private String orderStatus;
    private String orderId;
    private static final String TAG = "OrderLogitsActivity";
    private ArrayList<LinkedHashMap<String, String>> listItem_time = new ArrayList<>();
    private List<LogitsEntity> listItem = new ArrayList<>();
    private LinkedHashMap<String, String> map_key;
    private LinkedHashMap<String, String> map_value;

    private String image;
    private String expressNo = "";
    private String expressName = "";
    private String logistics_info = "";
    private TimeLineAdapter timeLineAdapter;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_logits);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initAdapter();
        initData();
    }

    @Override
    protected void init() {

    }

    private void initAdapter() {
        timeLineAdapter = new TimeLineAdapter(this, listItem);
        logitsLv.setAdapter(timeLineAdapter);
    }

    private void initView() {
        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv.setText("物流详情");
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        orderStatus = intent.getStringExtra("orderStatus");
        getOrderLogitsInfo(orderId, orderStatus);
    }

    /**
     * 物流详情
     *
     * @param orderId
     * @param orderStatus
     */
    private void getOrderLogitsInfo(String orderId, String orderStatus) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getOrderDetail(orderId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "订单详情===>" + result.getData().toString());
                    OrderEntity shopDataEntity = JSON.parseObject(result.getData().toString(), OrderEntity.class);
                    ExpressInfoBean bean = shopDataEntity.getExpressInfo();
                    List<OrderDetailBean> orderDetailBeanList = new ArrayList<>();
                    orderDetailBeanList = shopDataEntity.getOrderDetails();

                    image = JudgeImageUrlUtils.isAvailable(orderDetailBeanList.get(0).getPicture());
                    if (bean != null) {
                        expressNo = bean.getExpressNo();
                        expressName = bean.getExpressName();
                        logistics_info = bean.getLogisticInfo();
                        if (logistics_info != null) {
                            ztFl.setVisibility(View.GONE);
                            scrollView.setVisibility(VISIBLE);
                            logisticsFactoryTv.setText(expressName + "");
                            logisticsNoTv.setText(expressNo + "");
                            Glide.with(OrderLogitsActivity.this).load(image).into(shopImage);
                            try {
                                JSONArray jsonArray = new JSONArray(logistics_info);
                                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String a = jsonObject.getString("AcceptStation");
                                    String b = jsonObject.getString("AcceptTime");
                                    map_key = new LinkedHashMap<>();
                                    map_value = new LinkedHashMap<>();
                                    map_key.put("AcceptStation", a);
                                    map_value.put("AcceptTime", b);
                                    listItem_time.add(map_value);

                                    LogitsEntity logitsEntity = new LogitsEntity();
                                    logitsEntity.setAcceptStation(a);
                                    logitsEntity.setAcceptTime(b);
                                    listItem.add(logitsEntity);

                                    noLogitsInfoTv.setVisibility(View.GONE);
                                    logitsLv.setVisibility(View.VISIBLE);
                                    timeLineAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                noLogitsInfoTv.setVisibility(VISIBLE);
                                logitsLv.setVisibility(View.GONE);
                            }
                        } else {
                            ztFl.setVisibility(View.VISIBLE);
                            ztTipTv.setText("签收方式：自提");
                            scrollView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }
}
