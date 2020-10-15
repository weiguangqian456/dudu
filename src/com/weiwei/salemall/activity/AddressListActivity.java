package com.weiwei.salemall.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.AddressAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/5/23.
 *         收货地址界面
 */

public class AddressListActivity extends TempAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.ll_btn_add_address)
    LinearLayout addAddressBtn;
    @BindView(R.id.rl_no_address_data)
    RelativeLayout noDataRl;
    @BindView(R.id.rv_address)
    RecyclerView addressRv;

    private List<AddressEntity> recordsBeanList = new ArrayList<>();
    private AddressAdapter addressAdapter;
    private static final String TAG = "AddressListActivity";
    private String select = "";

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initEventBus();
        initAdapter();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recordsBeanList != null && recordsBeanList.size() > 0) recordsBeanList.clear();
        pageNum = 1;
        getDeliveryPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //注册EventBus
    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void initAdapter() {
        addressAdapter = new AddressAdapter(AddressListActivity.this, recordsBeanList, select);
        addressRv.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
        addressRv.setAdapter(addressAdapter);
        ((DefaultItemAnimator) addressRv.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapter.setOnItemChenedListener(new AddressAdapter.OnItemCheckedListener() {
            @Override
            public void onItemChecked(View view, int position) {   //设置默认地址
                setDefaultDeDelivery(recordsBeanList.get(position).getId());
            }

            @Override
            public void onItemNoChecked(View view, int position) {   //取消默认地址
                AddressEntity entity = recordsBeanList.get(position);
                saveAddressInfo(entity);
            }
        });
    }

    /**
     * 取消默认地址
     *
     * @param entity
     */
    private void saveAddressInfo(AddressEntity entity) {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> param = new HashMap<>();
        param.put("name", entity.getName());
        param.put("phone", entity.getPhone());
        param.put("province", entity.getProvince());
        param.put("city", entity.getCity());
        param.put("area", entity.getArea());
        param.put("town", entity.getTown());
        param.put("address", entity.getAddress());
        param.put("isDefault", "0");
        param.put("id", entity.getId());
        retrofit2.Call<ResultEntity> call = api.editDelivery(param);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Toast.makeText(AddressListActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                if (REQUEST_CODE == result.getCode()) {
                    if (recordsBeanList != null && recordsBeanList.size() > 0) recordsBeanList.clear();
                    pageNum = 1;
                    getDeliveryPage();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 设置默认地址
     *
     * @param deliveryId
     */
    private void setDefaultDeDelivery(String deliveryId) {
        ApiService api = RetrofitClient.getInstance(AddressListActivity.this).Api();
        retrofit2.Call<ResultEntity> call = api.setDefaultDelivery(deliveryId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Toast.makeText(AddressListActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                if (REQUEST_CODE == result.getCode()) {
                    if (recordsBeanList != null && recordsBeanList.size() > 0) recordsBeanList.clear();
                    pageNum = 1;
                    getDeliveryPage();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                select = intent.getExtras().getString("select", "");
            }
        }

        if (select != null && select.equals("1")) {
            title.setText(R.string.choose_address);
        } else {
            title.setText(R.string.my_addresses);
        }

        backRl.setOnClickListener(this);
        addAddressBtn.setOnClickListener(this);

        addressRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == addressAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    getDeliveryPage();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    /**
     * 获取地址信息
     */
    private void getDeliveryPage() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> param = new HashMap<>();
        param.put("pageNum", pageNum + "");
        param.put("pageSize", "10");
        retrofit2.Call<ResultEntity> call = api.getDelivery(param);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "地址msg===>" + result.getData().toString());
                    ResultDataEntity addressDataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                    List<AddressEntity> list = JSON.parseArray(addressDataEntity.getRecords().toString(), AddressEntity.class);
                    recordsBeanList.addAll(list);
                    isLoading = false;
                    addressAdapter.notifyDataSetChanged();
                    if (recordsBeanList != null && recordsBeanList.size() > 0) {
                        noDataRl.setVisibility(View.GONE);
                    }
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
            case R.id.ll_btn_add_address:
                skipPage(AddressAddActivity.class);
                break;
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 地址数据为null
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        Log.e(TAG, "接收到地址数据eventBus msg===>" + messageEvent.getMessage());
        String eventBusMsg = messageEvent.getMessage();
        if ("0".equals(eventBusMsg)) {
            addressRv.setVisibility(View.GONE);
            noDataRl.setVisibility(View.VISIBLE);
        }
    }
}
