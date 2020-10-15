package com.weiwei.salemall.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.NoMultClickUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.StarBar;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/5/31.
 *         评论界面
 */

public class CommentActivity extends VsBaseActivity {
    @BindView(R.id.sys_title_txt)
    TextView sys_title_txt;
    @BindView(R.id.btn_nav_right_tv)
    TextView btn_nav_right_tv;
    @BindView(R.id.et_comment_content)
    EditText et_comment_content;
    @BindView(R.id.star_describe)
    StarBar star_describe;
    @BindView(R.id.star_service)
    StarBar star_service;
    @BindView(R.id.star_delivery)
    StarBar star_delivery;

    private static final String TAG = "CommentActivity";
    private String orderNo;
    private String productNo;

    private CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        orderNo = intent.getStringExtra("orderNo");
        productNo = intent.getStringExtra("productNo");

        sys_title_txt.setText("发布评论");
        btn_nav_right_tv.setVisibility(View.VISIBLE);
        btn_nav_right_tv.setText("提交");

        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
        star_describe.setIntegerMark(true);
        star_service.setIntegerMark(true);
        star_delivery.setIntegerMark(true);

        if (!NoMultClickUtils.isFastClick()) {
            btn_nav_right_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check();
                }
            });
        }

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    private void check() {
        String commentContent = et_comment_content.getText().toString().trim();
        float describe = star_describe.getStarMark();
        float service = star_service.getStarMark();
        float delivery = star_delivery.getStarMark();
        if (commentContent.length() == 0) {
            Toast.makeText(CommentActivity.this, "评论内容不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (describe == 0) {
            Toast.makeText(CommentActivity.this, "请评价商品描述", Toast.LENGTH_LONG).show();
            return;
        }
        if (service == 0) {
            Toast.makeText(CommentActivity.this, "请评价发货速度", Toast.LENGTH_LONG).show();
            return;
        }
        if (delivery == 0) {
            Toast.makeText(CommentActivity.this, "请评价服务态度", Toast.LENGTH_LONG).show();
            return;
        }
        saveComment(commentContent, describe, service, delivery);
    }

    /**
     * 提交评论
     *
     * @param commentContent
     * @param describe
     * @param service
     * @param delivery
     */
    private void saveComment(String commentContent, float describe, float service, float delivery) {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        String nickName = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname);
        String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put("content", commentContent);
        params.put("describe", describe + "");
        params.put("service", service + "");
        params.put("delivery", delivery + "");
        params.put("orderNo", orderNo);
        params.put("productNo", productNo);
        params.put("nickName", nickName);
        if (nickName == null || nickName.equals("")) {
            params.put("nickName", phone);
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.addComment(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "添加评论 msg===>" + result.getMsg());
                    new AlertDialog.Builder(CommentActivity.this).
                            setMessage("评论成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }
}
