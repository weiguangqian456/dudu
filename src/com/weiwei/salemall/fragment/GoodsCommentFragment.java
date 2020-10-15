package com.weiwei.salemall.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.adapter.GoodsCommentAdapter;
import com.weiwei.salemall.bean.ProductCommentEntity;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ
 *         item页ViewPager里的评价Fragment
 */
public class GoodsCommentFragment extends Fragment {
    @BindView(R.id.rv_comment)
    RecyclerView comentRv;
    @BindView(R.id.ll_no_comment)
    LinearLayout noCommentDataLl;

    private List<ProductCommentEntity> productCommentEntityList = new ArrayList<>();
    private GoodsCommentAdapter goodsCommentAdapter;

    public GoodsDetailActivity activity;
    private String productNo;
    private String columnId;
    private static final String TAG = "GoodsCommentFragment";

    /**
     * 加载更多
     */
    private int pageNum = 1;
    int lastVisibleItem;
    boolean isLoading = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (GoodsDetailActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goods_comment, null);
        ButterKnife.bind(this, rootView);
        initView();
        initAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            productNo = bundle.getString("productNo");
            columnId = bundle.getString("columnId");
            Log.e(TAG, "商品评论界面msg===>" + productNo + "columnId===>" + columnId);
            if (StringUtils.isEmpty(columnId)) {
                columnId = "";
            }
            initCommentPage();
        }
    }

    private void initView() {
        comentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == goodsCommentAdapter.getItemCount() && !isLoading) {
                    Log.e(TAG, "当前界面已经滑到底了");
                    pageNum++;
                    isLoading = true;
                    initCommentPage();
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
        goodsCommentAdapter = new GoodsCommentAdapter(getActivity(), productCommentEntityList);
        comentRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        comentRv.setAdapter(goodsCommentAdapter);
    }

    private void initCommentPage() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", "10");
        params.put("productNo", productNo);
        params.put("type", "0");    //好评
        params.put("columnId", columnId);
        retrofit2.Call<ResultEntity> getProductCommentCall = api.getCommentPage(params);
        getProductCommentCall.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "商品好评msg===>" + result.getData().toString());
                    ResultDataEntity shopCommentDataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                    List<ProductCommentEntity> list = JSON.parseArray(shopCommentDataEntity.getRecords().toString(), ProductCommentEntity.class);
                    productCommentEntityList.addAll(list);
                    isLoading = false;
                    goodsCommentAdapter.notifyDataSetChanged();
                    if (productCommentEntityList != null && productCommentEntityList.size() > 0) {
                        comentRv.setVisibility(View.VISIBLE);
                        noCommentDataLl.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }
}