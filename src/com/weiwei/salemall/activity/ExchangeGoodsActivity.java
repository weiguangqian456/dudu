package com.weiwei.salemall.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.fragment.MemberExtractFragment;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SamqiColumnBean;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.home.fragment.NewExchangeFragment;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.NetUtils;
import com.weiwei.salemall.widget.BadgeView;
import com.weiwei.salemall.widget.CustomProgressDialog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;
import static com.weiwei.salemall.fragment.GoodsInfoFragment.action;
import static com.weiwei.salemall.utils.DensityUtils.px2sp;

/**
 * @author Created by EDZ on 2018/07/26
 * 兑换专区商品
 */
public class ExchangeGoodsActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.et_input)
    EditText inputEt;
    @BindView(R.id.rl_shoppingCart)
    FrameLayout shoppingCartIv;
    @BindView(R.id.btn_fresh)
    Button freshBtn;
    @BindView(R.id.btn_fresh_tv)
    Button blankFreshBtn;
    /**
     * 兑换专区
     */
    @BindView(R.id.magic_indicator)
    MagicIndicator firstIndicator;
    @BindView(R.id.view_pager)
    ViewPager firstVp;
    @BindView(R.id.badgeView)
    BadgeView badgeView;

    /**
     * 多界面判断
     */
    @BindView(R.id.fl_no_data)
    RelativeLayout noDataFl;
    @BindView(R.id.fl_no_content)
    RelativeLayout noContentFl;
    @BindView(R.id.ll_firststyle)
    LinearLayout exChangeLl;

    /**
     * 栏目
     */
    private List<SamqiColumnBean> mDataList = null;

    private List<Fragment> fragmentList = null;
    private static final String TAG = "ExchangeGoodsActivity";

    /**
     * 栏目Id
     */
    private String columnId = null;

    private static ShoppingItemsBeanDao shoppingItemsBeanDao;
    private int shoppingCartcount;

    private CustomProgressDialog loadingDialog;
    private MyFragmentAdapter myFragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_exchange_goods;
    }

    @Override
    protected int getStateBarColor() {
        return Color.parseColor("#1A338E");
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        registerNetChangedRecervier();
        initBroadCast();
        init();
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
                        //有网
                        if (NetUtils.isNetworkAvailable(mContext)) {
                            setView();
                            noDataFl.setVisibility(View.GONE);
                        } else {   //没网
                            noDataFl.setVisibility(View.VISIBLE);
                            noContentFl.setVisibility(View.GONE);
                        }
                        freshBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //有网
                                if (NetUtils.isNetworkAvailable(mContext)) {
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

    private void init() {
        firstIndicator.setVisibility(View.GONE);
        columnId = getIntent().getStringExtra("columnId");
        backIv.setOnClickListener(this);
        shoppingCartIv.setOnClickListener(this);
        inputEt.setFocusable(false);
        inputEt.setOnClickListener(this);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        blankFreshBtn.setOnClickListener(this);
    }

    private void setView() {
        getMagicIndicatorData();
    }

    /**
     * 获取子栏目
     */
    private void getMagicIndicatorData() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getChildColumnsData(columnId);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "兑换专区子栏目msg===>" + result.getData().toString());
                    List<SamqiColumnBean> list = JSONObject.parseArray(result.getData().toString(), SamqiColumnBean.class);
                    mDataList = new ArrayList<>();
//                    SamqiColumnBean allBean = new SamqiColumnBean();
//                    allBean.setId("0");
//                    allBean.setAppId("dudu");
//                    allBean.setColumnName("全部");
//                    mDataList.add(allBean);
                    mDataList.addAll(list);
                    if (mDataList != null && mDataList.size() > 0) {
                        noContentFl.setVisibility(View.GONE);
                        exChangeLl.setVisibility(View.VISIBLE);
                        initMagicIndicator();
                        initViewPager();
                    } else {
                        noContentFl.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void initMagicIndicator() {
        firstIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).getColumnName());
                int size = (int) getResources().getDimension(R.dimen.w_14_dip);
                simplePagerTitleView.setTextSize(px2sp(context, size));
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.vs_blue));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstVp.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(getResources().getColor(R.color.vs_blue));
                return linePagerIndicator;
            }
        });
        firstIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(firstIndicator, firstVp);
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < (mDataList.size() == 0 ? 0 : 1); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", mDataList.get(i).getId());
            bundle.putString("columnId", columnId);
            Fragment exChangeFragment = MemberExtractFragment.newInstance(bundle);
            fragmentList.add(exChangeFragment);
        }
        firstVp.setOffscreenPageLimit(0);
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);
        firstVp.setAdapter(myFragmentAdapter);
        firstVp.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.et_input:
                String[] key = new String[]{"skipFlag", "columnId", "isExchange"};
                String[] values = new String[]{"2", columnId, "y"};
                Intent intent = new Intent(this, SearchViewActivity.class);
                for (int i = 0; i < key.length; i++) {
                    intent.putExtra(key[i], values[i]);
                }
                startActivity(intent);
                break;
            case R.id.rl_shoppingCart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                EventBus.getDefault().post(bean);
                finish();
                break;
            case R.id.btn_fresh_tv:
                //有网
                if (NetUtils.isNetworkAvailable(mContext)) {
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
