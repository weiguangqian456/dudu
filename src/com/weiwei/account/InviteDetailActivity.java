package com.weiwei.account;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hwtx.dududh.R;
import com.weiwei.base.adapter.InviteDetailAdapter;
import com.weiwei.base.adapter.VsInviteDetailsAdapter;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.retrofit.RetrofitUtils;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteDetailActivity extends TempAppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int pageSize = 1;
    private Map<String, String> params;
    private String phone;
    private CustomProgressDialog loadingDialog;
    private List<SeckillTab.Records> recordsList = new ArrayList<>();
    private InviteDetailAdapter adapter;
    int lastVisibleItem;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        LogUtils.e("phone",phone);
        initView();
    }


    private void initView() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText("加油明细");
        params = new HashMap<>();
        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        iniData();
    }

    private void iniData() {

        params.put("page",pageSize+"");
        params.put("phone",phone);
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        RetrofitUtils.getInstance().refuelMoney(params).enqueue(new Callback<SeckillTab>() {
            @Override
            public void onResponse(Call<SeckillTab> call, Response<SeckillTab> response) {
               loadingDialog.setLoadingDialogDismiss();
               if(response.body() != null && response.body().records != null) {
                   recordsList.addAll(response.body().records);
                   LogUtils.e("recordsList:",recordsList.toString());
                   if(recordsList.size() > 0) {
                       rlEmpty.setVisibility(View.GONE);
                       recyclerView.setVisibility(View.VISIBLE);
                       initAdapter();
                   }else {
                       rlEmpty.setVisibility(View.VISIBLE);
                       recyclerView.setVisibility(View.GONE);
                   }
               }
            }

            @Override
            public void onFailure(Call<SeckillTab> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void initAdapter() {
        adapter = new InviteDetailAdapter(getApplicationContext(),recordsList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                    Log.e("recyclerView", "当前界面已经滑到底了");
                    pageSize++;
                    isLoading = true;
                    iniData();
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

    @Override
    protected void init() {

    }

}
