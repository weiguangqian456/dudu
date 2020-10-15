package com.weiwei.salemall.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.adapter.CollectionAdapter;
import com.weiwei.salemall.bean.CollectionDataBean;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;

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
 * @author Created by EDZ on 2018/07/23
 *         我的收藏
 */
public class CollectionActivity extends VsBaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.fl_no_data)
    FrameLayout noDataFl;
    @BindView(R.id.rl_collection)
    RelativeLayout collectionRl;
    @BindView(R.id.rp_top)
    RadioGroup topRp;
    @BindView(R.id.rb_general)
    RadioButton generalRb;
    @BindView(R.id.rb_new)
    RadioButton newRb;
    @BindView(R.id.rb_near)
    RadioButton nearRb;
    @BindView(R.id.rb_price)
    RadioButton priceRb;
    @BindView(R.id.rv_collection)
    RecyclerView collectionRv;

    private static final String TAG = "CollectionActivity";
    private List<CollectionEntity> dataList = new ArrayList<>();
    private CollectionAdapter collectionAdapter = null;

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initAdapter();
        initEventBus();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataList != null && dataList.size() > 0) dataList.clear();
        pageNum = 1;
        initCollectedData();
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

    private void initView() {
        collectionRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == collectionAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    initCollectedData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initAdapter() {
        collectionAdapter = new CollectionAdapter(this, dataList);
        collectionRv.setLayoutManager(new LinearLayoutManager(this));
        collectionRv.setAdapter(collectionAdapter);
    }

    private void initEvent() {
        backIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 获取收藏数据
     */
    private void initCollectedData() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("favoriteType", 1 + "");
        params.put("pageNum", pageNum + "");
        params.put("pageSize", "10");
        retrofit2.Call<ResultEntity> call = api.getFavorite(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "收藏数据msg===>" + result.getData().toString());
                    CollectionDataBean bean = JSON.parseObject(result.getData().toString(), CollectionDataBean.class);
                    List<CollectionEntity> list = JSON.parseArray(bean.getData().toString(), CollectionEntity.class);
                    dataList.addAll(list);
                    isLoading = false;
                    collectionAdapter.notifyDataSetChanged();
                    if (dataList != null && dataList.size() > 0) {
                        noDataFl.setVisibility(View.GONE);
                        collectionRl.setVisibility(View.VISIBLE);
                    }else {
                        MessageEvent event = new MessageEvent();
                        event.setMessage("0");
                        EventBus.getDefault().post(event);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 订阅方法，当接收到事件的时候，会调用该方法
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        String eventBusMsg = messageEvent.getMessage();
        Log.e(TAG, "接收到收藏数据eventBus msg===>" + eventBusMsg);
        if ("0".equals(eventBusMsg)) {
            collectionRl.setVisibility(View.GONE);
            noDataFl.setVisibility(View.VISIBLE);
        }
    }

}
