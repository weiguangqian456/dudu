package com.weiwei.salemall.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.LocalGoodsAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.NetUtils;
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

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.fragment.GoodsInfoFragment.action;

/**
 * @author Created by EDZ on 2018/07/26
 *         商品模块
 */
public class LocalGoodsActivity extends TempAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.et_input)
    EditText inputEt;
    @BindView(R.id.iv_cancle)
    ImageView cancleIv;
    @BindView(R.id.rl_shoppingCart)
    FrameLayout shoppingCartIv;
    @BindView(R.id.btn_fresh)
    Button freshBtn;
    @BindView(R.id.btn_fresh_tv)
    Button blankFreshBtn;

    @BindView(R.id.ll_otherstyle)
    LinearLayout otherStyleLl;

    @BindView(R.id.badgeView)
    BadgeView badgeView;

    /**
     * 多界面判断
     */
    @BindView(R.id.fl_no_data)
    RelativeLayout noDataFl;
    @BindView(R.id.rv_localgoods)
    RecyclerView localGoodsRv;
    @BindView(R.id.fl_no_content)
    RelativeLayout noContentFl;


    /**
     * 数据源
     */
    private List<ProductBean> dataList = new ArrayList<>();
    private LocalGoodsAdapter adapter = null;

    private static final String TAG = "LocalGoodsActivity";

    /**
     * 栏目Id
     */
    private String columnId = null;

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private static ShoppingItemsBeanDao shoppingItemsBeanDao;
    private int shoppingCartcount;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_goods);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        registerNetChangedRecervier();
        initView();
        initAdapter();
        initBroadCast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", getTotalNum());
        mContext.sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(myBroadCastReceiver);
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter(action);
        mContext.registerReceiver(myBroadCastReceiver, filter);
    }

    BroadcastReceiver myBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setAction(action);
            shoppingCartcount = intent.getIntExtra("cartNumber", 0);
            setBadgeView(shoppingCartcount);
        }
    };

    //监听当前网络状态的广播
    private void registerNetChangedRecervier() {
        new IntentFilterUtils() {
            @Override
            public NetStateChangedReceiver getNetStateChangedReceiver() {
                return new NetStateChangedReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        handleOnNetStateChanged();
                    }

                    @Override
                    public void handleOnNetStateChanged() {
                        if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                            setView();
                            noDataFl.setVisibility(View.GONE);
                            localGoodsRv.setVisibility(View.VISIBLE);
                        } else {   //没网
                            noDataFl.setVisibility(View.VISIBLE);
                            noContentFl.setVisibility(View.GONE);
                            localGoodsRv.setVisibility(View.GONE);
                        }
                        freshBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                                    setView();
                                } else {
                                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
            }
        }.register(mContext);
    }

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

    @Override
    protected void init() {
    }

    private void initView() {
        columnId = getIntent().getStringExtra("columnId");
        backIv.setOnClickListener(this);
        shoppingCartIv.setOnClickListener(this);
        inputEt.setFocusable(false);
        inputEt.setOnClickListener(this);
        localGoodsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    getNormalColumnData();
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

        blankFreshBtn.setOnClickListener(this);
    }

    private void initAdapter() {
        adapter = new LocalGoodsAdapter(this, dataList, columnId, "n");
        localGoodsRv.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = adapter.getItemViewType(position);
                if (type == Constant.RECYCLE_TYPE_FOOTER){
                    return 2;
                }
                return 1;
            }
        });
        localGoodsRv.setLayoutManager(manager);
    }


    private void setView() {
        otherStyleLl.setVisibility(View.VISIBLE);
        getNormalColumnData();
    }

    /**
     * 获取非商企栏目下数据（商品）
     */
    private void getNormalColumnData() {
        if (!this.isFinishing() && pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", 10 + "");
        retrofit2.Call<ResultEntity> call = api.getColumnsData(columnId, params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "非商企栏目数据msg===>" + result.getData().toString());
                    List<ProductBean> list = JSONObject.parseArray(result.getData().toString(), ProductBean.class);
                    dataList.addAll(list);
                    //加载中状态
                    if(list.size() == 10){
                        adapter.initFooterState(Constant.RECYCLE_FOOTER_LOAD);
                    }else{
                        adapter.initFooterState(Constant.RECYCLE_FOOTER_OVER);
                    }
                }
                isLoading = false;
                adapter.notifyDataSetChanged();
                /**
                 *判断界面显示
                 */
                if (dataList != null && dataList.size() > 0) {     //有数据
                    noContentFl.setVisibility(View.GONE);
                    localGoodsRv.setVisibility(View.VISIBLE);
                } else {                                            //没数据
                    noContentFl.setVisibility(View.VISIBLE);
                    localGoodsRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.et_input:
                String[] key = new String[]{"skipFlag", "columnId"};
                String[] value = new String[]{"2", columnId};
                skipPage(SearchViewActivity.class, key, value);
                break;
            case R.id.rl_shoppingCart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                Intent intent=new Intent();
                intent.putExtra("indicator",3);
                intent.setClass(this,VsMainActivity.class);
                startActivity(intent);
                EventBus.getDefault().post(bean);
                finish();
                break;
            case R.id.btn_fresh_tv:
                if (NetUtils.isNetworkAvailable(mContext)) {     //有网
                    setView();
                } else {
                    Toast.makeText(mContext, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private int getTotalNum() {
        int num = 0;
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (Iterator iterators = beanList.iterator(); iterators.hasNext(); ) {
            ShoppingItemsBean bean = (ShoppingItemsBean) iterators.next();
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }
}
