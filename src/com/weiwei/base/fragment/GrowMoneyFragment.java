package com.weiwei.base.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.account.ConsumptionRecordsEntity;
import com.weiwei.account.GrowMoneyActivity;
import com.weiwei.account.RecordsAdapter;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseLazyFragment;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.SimpleDividerDecoration;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

import static com.weiwei.base.application.VsApplication.mContext;

public class GrowMoneyFragment  extends BaseLazyFragment {

    @BindView(R.id.rv_records)
    RecyclerView recyclerView;

    private List<ConsumptionRecordsEntity> recordsEntityList = new ArrayList<>();
    private RecordsAdapter recordsAdapter;
    private SimpleDateFormat sDateFormat;
    private String endTime;
    private String startTime = "2015-01-01";

    private boolean isRegisterFlag = false;
    private int pageNum = 1;
    private CustomProgressDialog loadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grow_money;
    }

    @Override
    protected void initView() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void loadLazyData() {
        loadingDialog = new CustomProgressDialog(getContext(), "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        endTime = sDateFormat.format(new Date());


//        initBroadCastReceiver();
        getMyRedRecords();

        initAdapter();
    }
    /**
     * 获取成长金记录
     */
    private void initBroadCastReceiver() {
        IntentFilter mycalllogFilter = new IntentFilter();
        mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_RED_LOG);
        isRegisterFlag = true;
        getActivity().registerReceiver(mycalllogReceiver, mycalllogFilter);

        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        endTime = sDateFormat.format(new Date());
        getMyRedRecords();
    }
    private BroadcastReceiver mycalllogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadingDialog.setLoadingDialogDismiss();  //收到广播的时候取消进度条
            Bundle bundle = intent.getExtras();
            JSONObject js;
            try {
                if (bundle.get("list").toString().contains("频繁")) {
                    Toast.makeText(mContext, "操作过于频繁", Toast.LENGTH_SHORT).show();
                    return;
                }
                js = new JSONObject(bundle.get("list").toString());
                JSONArray ja = js.getJSONArray("items");
                for (int i = 0; i < ja.length(); i++) {
                    ConsumptionRecordsEntity entity = new ConsumptionRecordsEntity();
                    entity.setAmount(ja.getJSONObject(i).get("amount").toString());
                    entity.setRemark(ja.getJSONObject(i).get("remark").toString());
                    entity.setTime(ja.getJSONObject(i).get("ctime").toString());
                    entity.setStatus(ja.getJSONObject(i).get("status").toString());
                    recordsEntityList.add(entity);
                }
                recordsAdapter.notifyDataSetChanged();
                LogUtils.e("recordsEntityList",recordsEntityList.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    private void getMyRedRecords() {
         //红包成长金
                if (!getActivity().isFinishing() && pageNum == 1) {
                    loadingDialog.setLoadingDialogShow();
                }
                TreeMap<String, String> treeMap = new TreeMap<String, String>();
                treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
                treeMap.put("sdate", startTime);
                treeMap.put("edate", endTime);
                treeMap.put("rows", "10");
                treeMap.put("page", pageNum + "");
                CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyRedLog, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetMyRedLog);
    }
    private void initAdapter() {
        recordsAdapter = new RecordsAdapter(getContext(), recordsEntityList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(getContext()));
        recyclerView.setAdapter(recordsAdapter);
        recyclerView.setNestedScrollingEnabled(false);


    }
}
