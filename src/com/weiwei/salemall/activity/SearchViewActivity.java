package com.weiwei.salemall.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.salemall.adapter.CollectionV1Adapter;
import com.weiwei.salemall.adapter.SearchGoodsAdapter;
import com.weiwei.salemall.bean.CollectionEntity;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SearchProductDataBean;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.FlowLayout;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/7/18.
 *         商品搜索
 */

public class SearchViewActivity extends VsBaseActivity implements View.OnClickListener, TextWatcher {
    @BindView(R.id.rl_back)
    RelativeLayout backIv;
    @BindView(R.id.tv_search)
    TextView searchThingTv;
    @BindView(R.id.ll_select)
    LinearLayout selectLl;
    @BindView(R.id.view_line)
    View lineView;
    @BindView(R.id.et_input)
    EditText inputEt;
    @BindView(R.id.rl_search)
    RelativeLayout searchBtn;
    @BindView(R.id.iv_cancle)
    ImageView cancleIv;
    @BindView(R.id.fl_no_data)
    FrameLayout noDataFl;

    /**
     * 热门
     */
    @BindView(R.id.fl_hot)
    FlowLayout hotFl;
    private LayoutInflater hotFlInflater;
    private List<String> hotWordsList = null;

    /**
     * 历史
     */
    @BindView(R.id.rl_history)
    RelativeLayout historyRl;
    @BindView(R.id.fl_history)
    FlowLayout historyFl;
    @BindView(R.id.btn_clear)
    TextView clearBtn;
    private LayoutInflater hisFlInflater;
    private List<String> mHistoryKeywords;

    /**
     * 界面判断
     */
    @BindView(R.id.ll_search)
    LinearLayout searchLl;
    @BindView(R.id.ll_localgoods)
    LinearLayout localGoodsLl;
    @BindView(R.id.ll_localstore)
    LinearLayout localStoreLl;

    /**
     * 商品
     */
    @BindView(R.id.tl_goods)
    TabLayout goodsTl;
    @BindView(R.id.rv_localgoods)
    RecyclerView goodsRv;
    private List<ProductBean> localGoodsList = new ArrayList<>();
    private int goodsOrderRule = 0;
//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    /**
     * 店铺
     */
    @BindView(R.id.rv_localstore)
    RecyclerView storeRv;
    private List<CollectionEntity> localStoreList = new ArrayList<>();
    private int storesOrderRule = 0;


    private CommonPopupWindow tipPop;
    /**
     * 店铺、商品名选择器
     */
    private PopupWindow selectPop;
    private PopupWindow selectSortTypePop;
    private ListView selectLv;
    private String[] selectStr = new String[]{"商品", "店铺"};
    private String[] selectSortStr = new String[]{"从高到低", "从低到高"};
    private int sortType = 0;
    private ArrayAdapter<String> adapter;

    private static final String TAG = "SearchViewActivity";
    /**
     * 搜索关键字
     */
    private String keyWord = null;

    /**
     * 标志位
     */
    private String skipFlag;

    /**
     * 默认搜索类型
     */
    private int searchType = 0;

    private static final int GET_HOTWORD_SUCCESS = 101;
    private static final int GET_SEARCHPRODUCT_SUCCESS = 103;
    private static final int GET_SEARCHSTORE_SUCCESS = 105;

    private int[] tabIcons = {R.drawable.sec_price_default, R.drawable.sec_price_hight, R.drawable.sec_price_low};

    /**
     * 栏目Id
     */
    private String columnId = "";
    private String mSearchType = "";

    private boolean isClicked = false;

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    private SearchGoodsAdapter searchGoodsAdapter = null;
    private CollectionV1Adapter collectionAdapter = null;

    private boolean mIsRefreshing = false;
    private CustomProgressDialog loadingDialog;
    private String isExchange;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
        showSoftInputFromWindow(this, inputEt);
        initPopWindow(searchThingTv);
        initHotSearchiew();
        initHistoryView();
        initEvent();
        initAdapter();
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        switch (msg.what) {
            case GET_SEARCHPRODUCT_SUCCESS:
                searchGoodsAdapter.notifyDataSetChanged();
                mIsRefreshing = false;
                updateGoodsUi();
                break;
            case GET_SEARCHSTORE_SUCCESS:
                collectionAdapter.notifyDataSetChanged();
                mIsRefreshing = false;
                updateStoreUi();
                break;
            case GET_HOTWORD_SUCCESS:
                setHotSearchViewData();
                break;
            default:
                break;
        }
    }

    private void updateGoodsUi() {
        if (localGoodsList != null && localGoodsList.size() > 0) {
            searchLl.setVisibility(View.GONE);
            noDataFl.setVisibility(View.GONE);
            localStoreLl.setVisibility(View.GONE);
            localGoodsLl.setVisibility(View.VISIBLE);
        } else {
            searchLl.setVisibility(View.GONE);
            noDataFl.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        searchGoodsAdapter = new SearchGoodsAdapter(this, localGoodsList,columnId,isExchange);
        goodsRv.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        goodsRv.setAdapter(searchGoodsAdapter);

        collectionAdapter = new CollectionV1Adapter(this, localStoreList);
        storeRv.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        storeRv.setAdapter(collectionAdapter);
    }

    private void initView() {
        skipFlag = getIntent().getStringExtra("skipFlag");
        columnId = getIntent().getStringExtra("columnId");
        mSearchType = getIntent().getStringExtra("searchType");
        isExchange = getIntent().getStringExtra("isExchange");
        if (skipFlag != null && skipFlag.equals("1")) {   //搜商家
            searchType = 1;
            lineView.setVisibility(View.GONE);
            selectLl.setVisibility(View.GONE);
            inputEt.setHint("请搜索商家名称");
        } else if (skipFlag != null && skipFlag.equals("2")) {   //搜商品
            searchType = 0;
            lineView.setVisibility(View.GONE);
            selectLl.setVisibility(View.GONE);
            inputEt.setHint("请搜索商品名称");
        } else {
            searchType = 0;
            lineView.setVisibility(View.VISIBLE);
            selectLl.setVisibility(View.VISIBLE);
            inputEt.setHint("请输入关键字");
        }

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void showSoftInputFromWindow(SearchViewActivity searchViewActivity, EditText inputEt) {
        inputEt.setFocusable(true);
        inputEt.setFocusableInTouchMode(true);
        inputEt.requestFocus();
        searchViewActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void initPopWindow(final TextView searchThingTv) {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectStr);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_search_select, null, false);
        selectLv = (ListView) view.findViewById(R.id.lv_select);
        selectLv.setAdapter(adapter);
        selectLv.setVerticalScrollBarEnabled(false);
        selectLv.setFastScrollEnabled(false);
        selectLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchThingTv.setText(selectStr[position]);
                searchType = position;
                selectPop.dismiss();
            }
        });

        int width = (int) getResources().getDimension(R.dimen.w_80_dip);
        int height = (int) getResources().getDimension(R.dimen.w_85_dip);
        selectPop = new PopupWindow(view, width, height, true);
        selectPop.setTouchable(true);
        selectPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        selectPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    private void initEvent() {
        backIv.setOnClickListener(this);
        selectLl.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        cancleIv.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        inputEt.addTextChangedListener(this);
        inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() &&
                        KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行操作
                    save(keyWord);
                    hideKeyBoard();
                }
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        initTabLayout();

        goodsRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        goodsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchGoodsAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    showProductData(goodsOrderRule);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        storeRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == collectionAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    showStoreData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        storeRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        goodsTl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                localGoodsList.clear();
                searchGoodsAdapter.notifyDataSetChanged();
                goodsOrderRule = tab.getPosition();
                pageNum = 1;
                isClicked = true;
                showProductData(goodsOrderRule);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                localGoodsList.clear();
                searchGoodsAdapter.notifyDataSetChanged();
                pageNum = 1;
                if (isClicked) {
                    goodsOrderRule = 4;
                    isClicked = false;
                } else {
                    goodsOrderRule = 3;
                    isClicked = true;
                }
                showProductData(goodsOrderRule);
            }
        });
    }

    private void initTabLayout() {
        goodsTl.addTab(goodsTl.newTab().setText("综合"));
        goodsTl.addTab(goodsTl.newTab().setText("销量"));
        goodsTl.addTab(goodsTl.newTab().setText("新品"));
        goodsTl.addTab(goodsTl.newTab().setText("价格"));
        goodsTl.getTabAt(3).setCustomView(getTabView(0));
    }

    private View getTabView(int i) {
        View view = LayoutInflater.from(this).inflate(R.layout.price_up_down, null);
        TextView txt_title = (TextView) view.findViewById(R.id.price_id);
        txt_title.setText("价格");
        ImageView img_title = (ImageView) view.findViewById(R.id.iv_time);
        img_title.setImageResource(tabIcons[i]);
        return view;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 初始化流式布局，加载数据
     */
    private void initHotSearchiew() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getHotwords();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "热门搜索msg===>" + result.getData().toString());
                    List<String> stringList = JSON.parseArray(result.getData().toString(), String.class);
                    hotWordsList = new ArrayList<>();
                    hotWordsList.addAll(stringList);
                }
                Message message = new Message();
                message.what = GET_HOTWORD_SUCCESS;
                mBaseHandler.sendMessage(message);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void setHotSearchViewData() {
        hotFlInflater = LayoutInflater.from(this);
        for (int i = 0; i < hotWordsList.size(); i++) {
            TextView tv = (TextView) hotFlInflater.inflate(R.layout.search_label_tv, hotFl, false);
            tv.setText(hotWordsList.get(i));
            final String str = tv.getText().toString();
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard();
                    inputEt.setText(str);
                    save(str);
                }
            });
            hotFl.addView(tv);
        }
    }

    private void initHistoryView() {
        mHistoryKeywords = new ArrayList<>();
        mHistoryKeywords = SpUtils.getStrListValue(SearchViewActivity.this, "searchHistory");
        if (mHistoryKeywords != null && mHistoryKeywords.size() > 0) {
            historyRl.setVisibility(View.VISIBLE);
            hisFlInflater = LayoutInflater.from(this);
            for (int i = 0; i < mHistoryKeywords.size(); i++) {
                TextView tv = (TextView) hisFlInflater.inflate(R.layout.search_label_tv, historyFl, false);
                tv.setText(mHistoryKeywords.get(i));
                final String str = tv.getText().toString();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyBoard();
                        inputEt.setText(str);
                        getSearchData(str);
                    }
                });
                historyFl.addView(tv);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search:
                hideKeyBoard();
                if (!TextUtils.isEmpty(keyWord)) {
                    save(keyWord);
                } else {
                    Toast.makeText(SearchViewActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear:
                hideKeyBoard();
                showTipPop();
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_select:
                showSelectPop();
                break;
            case R.id.iv_cancle:
                keyWord = "";
                inputEt.setText(keyWord);
                getDefaultUi();
                break;
            default:
                break;
        }
    }

    /**
     * 恢复默认UI
     */
    private void getDefaultUi() {
        localGoodsList.clear();
        pageNum = 1;
        searchLl.setVisibility(View.VISIBLE);
        if (localGoodsLl != null && localGoodsLl.isShown()) {
            localGoodsLl.setVisibility(View.GONE);
        }
        if (localStoreLl != null && localStoreLl.isShown()) {
            localStoreLl.setVisibility(View.GONE);
        }
        if (noDataFl != null && noDataFl.isShown()) {
            noDataFl.setVisibility(View.GONE);
        }
    }

    /**
     * 获取搜索结果
     *
     * @param keyword
     */
    private void getSearchData(String keyword) {
        localGoodsList.clear();
        localStoreList.clear();
        pageNum = 1;
        //栏目Id
        if (columnId == null) columnId = "";
        switch (searchType) {
            case 0:    //商品
                showProductData(goodsOrderRule);
                break;
            case 1:  //商家
                showStoreData();
                break;
            default:
                break;
        }
    }

    private void showProductData(int orderRule) {
        if(!this.isFinishing()&&pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        mIsRefreshing = true;
        Map<String, String> params = new HashMap<>();
        params.put("columnId", columnId);
        params.put("orderRule", orderRule + "");
        params.put("source", "android");
        params.put("identification", VsUtil.getAloneImei(SearchViewActivity.this));

        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call;
        if("JD".equals(mSearchType)){
            params.put("productName", keyWord);
            params.put("page", pageNum + "");
            params.put("limit", 10 + "");
            params.put("queryType","jd");
            call = api.searchProductJD(params);
        }else{
            params.put("keyword", keyWord);
            params.put("pageNum", pageNum + "");
            params.put("pageSize", 10 + "");
            call = api.searchProduct(params);
        }
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (result != null && REQUEST_CODE == result.getCode() && result.getData() != null) {
                    if("JD".equals(mSearchType)){
                        List<ProductBean> goodsBeanList = JSON.parseArray(result.getData().toString(), ProductBean.class);
                        localGoodsList.addAll(goodsBeanList);
                    }else{
                        SearchProductDataBean bean = JSON.parseObject(result.getData().toString(), SearchProductDataBean.class);
                        List<ProductBean> goodsBeanList = JSON.parseArray(bean.getData().toString(), ProductBean.class);
                        localGoodsList.addAll(goodsBeanList);
                    }
                }
                isLoading = false;
                Message message = new Message();
                message.what = GET_SEARCHPRODUCT_SUCCESS;
                mBaseHandler.sendMessage(message);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 搜索店铺
     */
    private void showStoreData() {
        if(!this.isFinishing()&&pageNum == 1) {
            loadingDialog.setLoadingDialogShow();
        }
        mIsRefreshing = true;
        String cityId = PreferencesUtils.getString(this, "selectCityCode");
        String lon = PreferencesUtils.getString(this, "currentLocationLon");
        String lat = PreferencesUtils.getString(this, "currentLocationLat");

        //栏目Id
        if (columnId == "") {     //没有栏目Id
            columnId = "";
            lon = "";
            lat = "";
            cityId = "";
        }

        if (lon == null || lat == null) {
            lon = "";
            lat = "";
            cityId = "";
        }

        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", 10 + "");
        params.put("currentLon", lon);
        params.put("currentLat", lat);
        params.put("columnId", columnId);
        params.put("keyword", keyWord);
        params.put("cityId", cityId);
        params.put("orderRule", storesOrderRule + "");
        params.put("source", "android");
        params.put("identification", VsUtil.getAloneImei(SearchViewActivity.this));
        retrofit2.Call<ResultEntity> call = api.searchStore(params);

        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    SearchProductDataBean bean = JSON.parseObject(result.getData().toString(), SearchProductDataBean.class);
                    List<CollectionEntity> storeBeanlist = JSON.parseArray(bean.getData().toString(), CollectionEntity.class);
                    localStoreList.addAll(storeBeanlist);
                }
                isLoading = false;
                Message message = new Message();
                message.what = GET_SEARCHSTORE_SUCCESS;
                mBaseHandler.sendMessage(message);
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void updateStoreUi() {
        if (localStoreList != null && localStoreList.size() > 0) {
            searchLl.setVisibility(View.GONE);
            noDataFl.setVisibility(View.GONE);
            localGoodsLl.setVisibility(View.GONE);
            localStoreLl.setVisibility(View.VISIBLE);
        } else {
            searchLl.setVisibility(View.GONE);
            noDataFl.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 保存到搜索历史sp 并 搜索
     *
     * @param keyWord
     */
    private void save(String keyWord) {
        mHistoryKeywords.add(keyWord);
        //去除重复数据
        Set set = new HashSet();
        List newList = new ArrayList();
        for (String cd : mHistoryKeywords) {
            if (set.add(cd)) {
                newList.add(cd);
            }
        }
        SpUtils.putStrListValue(SearchViewActivity.this, "searchHistory", newList);
        getSearchData(keyWord);
    }

    /**
     * 删除搜索历史提示
     */
    private void showTipPop() {
        tipPop = new CommonPopupWindow.Builder(this).setView(R.layout.custom_dialog_layout).setWidthAndHeight((int) getResources().getDimension(R.dimen.w_300_dip), (int)
                getResources().getDimension(R.dimen.w_150_dip)).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
            @Override
            public void getChildView(View view, int layoutResId) {
                TextView title = (TextView) view.findViewById(R.id.tv_title);
                TextView message = (TextView) view.findViewById(R.id.tv_message);
                TextView cancleBtn = (TextView) view.findViewById(R.id.tv_cancel);
                TextView sureBtn = (TextView) view.findViewById(R.id.tv_sure);
                title.setVisibility(View.VISIBLE);
                title.setText("删除历史");
                message.setText("确定删除全部历史记录吗？");
                cancleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipPop.dismiss();
                    }
                });
                sureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearSearchHistoryData();
                        tipPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        tipPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 清空搜索历史
     */
    private void clearSearchHistoryData() {
        SpUtils.removeStrList(SearchViewActivity.this, "searchHistory");
        historyRl.setVisibility(View.GONE);
        historyFl.setVisibility(View.GONE);
        Toast.makeText(SearchViewActivity.this, "搜索历史清除成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示选择搜索的弹框
     */
    private void showSelectPop() {
        if (selectPop != null && selectPop.isShowing()) {
            selectPop.dismiss();
            return;
        }
        selectPop.showAsDropDown(searchThingTv, 0, 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        inputEt.removeTextChangedListener(this);
        inputEt.addTextChangedListener(this);
        keyWord = String.valueOf(s);
        Log.e(TAG, "搜索关键字" + keyWord);
        if (keyWord.length() > 0) {
            cancleIv.setVisibility(View.VISIBLE);
        } else {
            cancleIv.setVisibility(View.GONE);
        }
    }
}
