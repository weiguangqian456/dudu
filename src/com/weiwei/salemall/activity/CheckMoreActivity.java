package com.weiwei.salemall.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.WelFareAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShopRecordsEntity;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.BadgeView;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.fragment.GoodsInfoFragment.action;

/**
 * @author Created by EDZ on 2018/6/13.
 *         查看更多商品
 */
public class CheckMoreActivity extends TempAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.fl_shoppingCart)
    FrameLayout shoppingCartFl;
    @BindView(R.id.badgeView)
    BadgeView badgeView;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;

    /**
     * 多界面判断
     */
    @BindView(R.id.fl_no_content)
    RelativeLayout noContentFl;
    @BindView(R.id.rv_more_goods)
    RecyclerView moreGoodsRv;

    private String type;

    private static final String TAG = "CheckMoreActivity";
    private GridLayoutManager manager;
    private int visibleThreshold = 3;

    private boolean canLoadMore = true;
    private WelFareAdapter welFareAdapter;
    private static final int UI_FLAG = 0;
    private static ShoppingItemsBeanDao shoppingItemsBeanDao;

    private int shoppingCartcount;
    private CustomProgressDialog loadingDialog;

    private List<ShopRecordsEntity> recordsBeanList = new ArrayList<>();

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_more);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        initRecycleView();
        initShoppingCartReceiver();
        initData();
    }

    @Override
    protected void init() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", getTotalNum());
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCastReceiver);
    }

    private void initShoppingCartReceiver() {
        IntentFilter filter = new IntentFilter(action);
        registerReceiver(myBroadCastReceiver, filter);
    }

    BroadcastReceiver myBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setAction(action);
            shoppingCartcount = intent.getIntExtra("cartNumber", 0);
            setBadgeView(shoppingCartcount);
        }
    };

    private void setBadgeView(int count) {
        badgeView.invalidate();
        if (0 != count) {
            badgeView.setVisibility(View.VISIBLE);
            badgeView.setText(count + "");
            if (count > 10) {
                badgeView.setText("10+");
            }
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    private void initView() {
        shoppingCartFl.setOnClickListener(this);
        backRl.setOnClickListener(this);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void initData() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        switch (type) {
            case "0":
                title.setText("商企特惠");
                break;
            case "1":
                title.setText("精选专区");
                break;
            case "2":
                title.setText("推荐专区");
                break;
            default:
                break;
        }
        getData();
    }


    public void getData() {
        if (!this.isFinishing() && pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", 10 + "");
        params.put("type", type);
        retrofit2.Call<ResultEntity> call = api.getRecommendProducts(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "更多推荐msg===>" + result.getData().toString());
                    ResultDataEntity dataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                    List<ShopRecordsEntity> shopRecordsEntityList = JSON.parseArray(dataEntity.getRecords().toString(), ShopRecordsEntity.class);
                    if (dataEntity != null) {
                        recordsBeanList.addAll(shopRecordsEntityList);
                        noContentFl.setVisibility(View.GONE);
                        moreGoodsRv.setVisibility(View.VISIBLE);
                    }
                    isLoading = false;
                    welFareAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void initRecycleView() {
        manager = new GridLayoutManager(this, 2);
        moreGoodsRv.setLayoutManager(manager);

        moreGoodsRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = convertDpToPixel(0);
                outRect.bottom = 0;
                int position = parent.getChildLayoutPosition(view);
                outRect.top = convertDpToPixel(1);

                //每行2个
                int i = position % 2;
                switch (i) {
                    //第一个
                    case 0:
                        outRect.left = convertDpToPixel(1);
                        outRect.right = convertDpToPixel(1);
                        break;
                    //第二个
                    case 1:
                        outRect.left = convertDpToPixel(1);
                        outRect.right = convertDpToPixel(1);
                        break;
                    default:
                        break;
                }
            }
        });

        welFareAdapter = new WelFareAdapter(this, recordsBeanList);
        moreGoodsRv.setAdapter(welFareAdapter);

        moreGoodsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == welFareAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    getData();
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

    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.fl_shoppingCart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                EventBus.getDefault().post(bean);
                finish();
                break;
            default:
                break;
        }
    }

    private int getTotalNum() {
        int num = 0;
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(this).getDaoSession().getShoppingItemsBeanDao();
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (Iterator iterators = beanList.iterator(); iterators.hasNext(); ) {
            ShoppingItemsBean bean = (ShoppingItemsBean) iterators.next();
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }
}
