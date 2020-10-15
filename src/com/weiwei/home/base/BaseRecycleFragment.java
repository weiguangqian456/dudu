package com.weiwei.home.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.home.view.CustomErrorView;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SeckillDataEntity;
import com.weiwei.salemall.receiver.NetStateChangedReceiver;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.IntentFilterUtils;
import com.weiwei.salemall.utils.NetUtils;
import com.weiwei.salemall.utils.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author hc
 * @date 2019/2/27.
 * @description: 通用适配型 RecycleView加载布局
 */
public abstract class BaseRecycleFragment extends BaseLazyFragment {

    public static final String RECYCLE_TYPE_DEFAULT = "RECYCLE_TYPE_DEFAULT";
    public static final String RECYCLE_TYPE_SECKILL = "RECYCLE_TYPE_SECKILL";
    public static final String RECYCLE_TYPE_CHOICES = "RECYCLE_TYPE_CHOICES";

    @BindView(R.id.srl_load)
    public SwipeRefreshLayout  mSwipeRefreshLayout;
    @BindView(R.id.rv_load)
    public RecyclerView mRecyclerView;
    @BindView(R.id.cev_view)
    public CustomErrorView mErrorView;

    private int mHttpPage = 1;

    protected int mStartPage = -1;
    //
    private boolean isNowLoad = false;
    //
    private boolean isNotMore = false;
    //
    private boolean isFirstLoad = true;
    //
    private boolean isNetWork = false;
    //
    private List<Object> mListExtra = new ArrayList<>();

    protected BaseRecycleAdapter mAdapter;

    protected abstract BaseRecycleAdapter getAdapter();

    protected abstract String getPath();

    protected Map<String,String> getParams(){
        return new HashMap<>();
    }

    protected void addExtraList(List<Object> array){
        mListExtra.clear();
        mListExtra.addAll(array);
    }

    protected boolean isShowError(){
        return true;
    }

    protected boolean isAddExtra(){
        return false;
    }

    protected int getPageSize(){
        return 10;
    }

    protected Boolean isLayoutManager(){
        return true;
    }

    protected Boolean isReplace(){
        return false;
    }

    protected NestedScrollView getScrollView(){
        return null;
    }

    protected int getGridCount(){
        return 2;
    }

    protected RecyclerView.LayoutManager getLayoutManager(){
        RecyclerView.LayoutManager manager;
        if(isLayoutManager()){
            manager = new LinearLayoutManager(mContext);
        }else{
            manager = new GridLayoutManager(mContext,getGridCount());
            ((GridLayoutManager)manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = mAdapter.getItemViewType(position);
                    if (type == Constant.RECYCLE_TYPE_FOOTER || type == Constant.RECYCLE_TYPE_HEAD){
                        return getGridCount();
                    }
                    return 1;
                }
            });
        }
        return manager;
    }

    /**
     * 重新请求时调用
     */
    protected void onInitRefresh(){

    }

    protected boolean isRightAwayLoad(){
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycle;
    }

    private boolean isLoadLazy = false;

    public boolean isLoadLazy() {
        return isLoadLazy;
    }

    @Override
    protected void loadLazyData() {
        isLoadLazy = true;
        if(mStartPage >= 0){
            mHttpPage = mStartPage;
        }
        if(isMonitorNetwork()){initNetwork();}
        mAdapter = getAdapter();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRefresh();
            }
        });
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(mAdapter);
        if(getScrollView()!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getScrollView().setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int
                        oldScrollY) {
                             View view = getScrollView().getChildAt(0);
                             if (getScrollView().getHeight() + getScrollView().getScrollY() == view.getHeight()) {
                                 loadMore();
                             }
                }
            });
        }else{
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.canScrollVertically(-1)) {
                        loadMore();
                    }
                }
            });
        }
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#447aff"));
        mSwipeRefreshLayout.setRefreshing(true);
        if(isImmediatelyLoad()){
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isRightAwayLoad()){
                        initRefresh();
                    }
                }
            },500);
        }
        mErrorView.setOnErrorClickListener(new CustomErrorView.OnErrorClickListener() {
            @Override
            public void onRefresh() {
                initRefresh();
            }
        });
    }

    protected boolean isImmediatelyLoad(){
        return true;
    }

    protected void refreshReplace(){
        mHttpPage = 1;
        isNotMore = false;
        loadData();
    }


    /**
     * 初始化数据
     */
    private void loadData(){
        isNowLoad = true;
        mAdapter.initFooterState(Constant.RECYCLE_FOOTER_LOAD);
        Map<String, String> params = getParams();
        params.put("pageNum", mHttpPage + "");
        params.put("pageSize",getPageSize() + "");
        String path = getPath();
        Log.e("TAG-HTTP",path);
        RetrofitClient.getInstance(mContext)
                .Api()
                .getList(path,params)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity>  response) {
                        ResultEntity resultEntity = response.body();
                        if(resultEntity!=null && Const.REQUEST_CODE == resultEntity.getCode()){
                            JSONArray array = getJsonArray(resultEntity);
                            if(isReplace() && mHttpPage == 1){
                                mAdapter.clearList();
                            }
                            mAdapter.addList(array);
                            if (array.size() != getPageSize()){
                                isNotMore = true;
                                mAdapter.initFooterState(Constant.RECYCLE_FOOTER_OVER);
                            }
                        }
                        onHttpFinish();
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        mAdapter.initFooterState(Constant.RECYCLE_FOOTER_ERROR);
                        onHttpFinish();
                    }
                });
    }

    private JSONArray getJsonArray(ResultEntity resultEntity){
        switch (getRequestType()){
            case RECYCLE_TYPE_SECKILL:
                SeckillDataEntity seckillDataEntity = JSONObject.parseObject(resultEntity.getData().toString(), SeckillDataEntity.class);
                return JSONObject.parseArray(seckillDataEntity.getPrdoucts().toString());
            case RECYCLE_TYPE_CHOICES:
                ResultDataEntity entity = JSON.parseObject(resultEntity.getData().toString(), ResultDataEntity.class);
                return JSONObject.parseArray(entity.getRecords().toString());
            default:
                return JSONObject.parseArray(resultEntity.getData().toString());
        }
    }

    private void onHttpFinish(){
        isNowLoad = false;
        mSwipeRefreshLayout.setRefreshing(false);
        if(isShowError() && mAdapter.getList().size() == 0){
            mErrorView.initState(CustomErrorView.ERROR_VIEW_EMPTY);
        }else {
            mErrorView.initState(CustomErrorView.ERROR_NOT);
        }
    }

    private void loadMore(){
        if(!isNotMore && !isNowLoad){
            mHttpPage++;
            loadData();
        }
    }

    public void initRefresh(){
        if(!isNowLoad){
            if(isAddExtra()){
                mHttpPage = 2;
                isNotMore = false;
                loadData();
                mAdapter.initList(mListExtra);
                onHttpFinish();
            }else{
                mHttpPage = 1;
                isNotMore = false;
                mAdapter.initList();
                loadData();
            }
            if(!isFirstLoad){
                onInitRefresh();
            }else{
                isFirstLoad = false;
            }
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            ToastUtils.show(mContext,"正在加载中~");
        }
    }

    /**
     * 有些特殊的返回
     */
    protected String getRequestType(){
        return RECYCLE_TYPE_DEFAULT;
    }

    /**
     * 是否监听网络
     */
    protected boolean isMonitorNetwork(){
        return false;
    }

    private void initNetwork(){
        new IntentFilterUtils() {
            @Override
            public NetStateChangedReceiver getNetStateChangedReceiver() {
                return new NetStateChangedReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if(isNetWork){
                            handleOnNetStateChanged();
                        }
                        isNetWork = true;
                    }

                    @Override
                    public void handleOnNetStateChanged() {
                        //有网
                        if (NetUtils.isNetworkAvailable(mContext)) {
                            initRefresh();
                            mErrorView.initState(CustomErrorView.ERROR_NOT);
                        } else {
                            mErrorView.initState(CustomErrorView.ERROR_VIEW_NETWORK);
                        }
                    }
                };
            }
        }.register(mContext);
    }
}
