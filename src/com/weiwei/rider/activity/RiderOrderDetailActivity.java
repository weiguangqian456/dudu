package com.weiwei.rider.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.google.gson.Gson;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.fragment.ImageGetDialog;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.adapter.ImageAdapter;
import com.weiwei.rider.adapter.OrderDetailAdapter;
import com.weiwei.rider.bean.GoodsImage;
import com.weiwei.rider.bean.OrderImage;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.bean.Order;
import com.weiwei.rider.bean.OrderDetail;
import com.weiwei.rider.utils.AlertDialog;
import com.weiwei.rider.utils.ImageUtils;
import com.weiwei.rider.utils.LocationUtils;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DateUtil;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class RiderOrderDetailActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.rl_money)
    RelativeLayout rlMoney;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_time)
    RelativeLayout rlTime;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.tv_orderNo)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_nav)
    TextView tvNav;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.rl_picture)
    RoundRelativeLayout rlPicture;
    @BindView(R.id.tv_intro)
    TextView tvIntro;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_call_phone)
    TextView tvCallPhone;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;


    private String id,riderId,uid;
    private Integer riderOrderStatus;
    private Order orderDetail;
    private OrderDetailAdapter adapter;
    private ImageAdapter imageAdapter;
    private List<String> imgList = new ArrayList<>();
    private boolean isShow = false;
    private Double mGoLatitude = 0.0;
    private Double mGoLongitude = 0.0;
    private String mPhotoPath;
    private AlertDialog alertDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_order_detail;
    }

    @Override
    protected void initView() {
        tvTitle.setText("订单详情");
       id = getIntent().getStringExtra("id");
       riderId = getIntent().getStringExtra("riderId");
       riderOrderStatus = Integer.parseInt(getIntent().getStringExtra("riderOrderStatus")) + 1;

    }

    @Override
    protected void initData() {
        getOrderDetails();
        alertDialog = new AlertDialog(this).builder();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i == 100) {
                    robOrder(riderOrderStatus);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LogUtils.e("onStartTrackingTouch:",seekBar.toString());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogUtils.e("onStopTrackingTouch:",seekBar.toString());
            }
        });
    }

    private void robOrder(int status) {
        Map<String,Object> params = new HashMap<>();
        loadingDialog.setLoadingDialogShow();
        uid = VsUserConfig.getDataString(this,VsUserConfig.JKey_KcId);
        params.put("id",id);
        params.put("uid",uid);
        params.put("orderMerchantNo",orderDetail.getOrderMerchantNo());
        params.put("riderId",riderId);
        params.put("riderOrderStatus",status + "");
        params.put("image",imgList.toArray());
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(params));

        RetrofitClient.getInstance(this).Api()
                .robOrder(body)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        if(response.isSuccessful() && response.body().getCode() == 0) {
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setMessage("isfresh");
                            EventBus.getDefault().post(messageEvent);
                            ToastUtils.show(RiderOrderDetailActivity.this,response.body().getMsg());
                            finish();
                        }else {
                            ToastUtils.show(RiderOrderDetailActivity.this,response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(RiderOrderDetailActivity.this,t.getMessage());
                    }
                });
    }

    private void getOrderDetails() {
        loadingDialog.setLoadingDialogShow();
        RetrofitClient.getInstance(this).Api()
                .getOrderDetails(id)
                .enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        loadingDialog.setLoadingDialogDismiss();
                        if(response.isSuccessful() && response.body() != null) {
                            orderDetail = response.body();
                            setData();
                        }else {
                            ToastUtils.show(RiderOrderDetailActivity.this,"获取数据失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        loadingDialog.setLoadingDialogDismiss();
                        ToastUtils.show(RiderOrderDetailActivity.this,"获取数据失败");
                    }
                });
    }

    private void setData() {
        LogUtils.e("orderDetail:",orderDetail.toString());
        String orderStatus = orderDetail.getRiderOrderStatus();
        if(orderStatus.equals("0")) {
            tvStatus.setText("抢单中");
            ivImage.setImageResource(R.drawable.order_status_one);
            tvNav.setVisibility(View.GONE);
        }else if(orderStatus.equals("1")) {
            tvStatus.setText("嘟嘟骑手已抢单");
            ivImage.setImageResource(R.drawable.order_status_two);
            tvNav.setVisibility(View.VISIBLE);
            rlPicture.setVisibility(View.VISIBLE);
            tvIntro.setText("右滑确认拣货完成");
            tvCancel.setVisibility(View.VISIBLE);
            tvCallPhone.setVisibility(View.GONE);
            rlPhone.setVisibility(View.VISIBLE);
            isShow = true;
        }else if(orderStatus.equals("2")){
            tvStatus.setText("嘟嘟骑手");
            tvProgress.setVisibility(View.VISIBLE);
            ivImage.setImageResource(R.drawable.order_status_three);
            tvNav.setVisibility(View.VISIBLE);
            rlPicture.setVisibility(View.VISIBLE);
            tvIntro.setText("右滑确认配送完成");
        }else if(orderStatus.equals("3")) {
            tvStatus.setText("配送已完成");
            tvIncome.setText("本单收入");
            tvProgress.setVisibility(View.VISIBLE);
            tvProgress.setText(DateUtil.stampToDate(orderDetail.getCreateTime()));
            ivImage.setImageResource(R.drawable.order_status_two);
            tvNav.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.rider_order_gray_button));
            btnConfirm.setTextColor(getResources().getColor(R.color.White));
            btnConfirm.setText("配送已完成");
        }else if(orderStatus.equals("-1")) {
            tvStatus.setText("该订单已取消");
            rlMoney.setVisibility(View.GONE);
            ivImage.setImageResource(R.drawable.order_status_five);
            rlTime.setVisibility(View.GONE);
            tvNav.setVisibility(View.GONE);
        }
        tvMoney.setText("￥" + orderDetail.getTotalMoney());
        tvTime.setText(orderDetail.getDelivery());
        tvCity.setText(orderDetail.getAddress());
        tvDetail.setText(orderDetail.getStoreAddress());
        tvOrderNo.setText(orderDetail.getOrderMerchantNo());
        tvOrderTime.setText(DateUtil.stampToDate(orderDetail.getCreateTime()));
        tvNum.setText("共" + orderDetail.getOrderDetails().size() + "件商品");

        if(orderDetail.getOrderDetails().size() > 0) {
            initAdapter(orderDetail.getOrderDetails());
        }
        if(orderDetail.getOrderMerchantImages() != null && orderDetail.getOrderMerchantImages().size() > 0) {
            List<String> imgUrlList = new ArrayList<>();
            for (GoodsImage goodsImage : orderDetail.getOrderMerchantImages()) {
                imgUrlList.add(goodsImage.getImageUrl());
            }
            imageAdapter = new ImageAdapter(this,imgUrlList,isShow);
        }else {
            imageAdapter = new ImageAdapter(this,imgList,isShow);
        }
        rvImages.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rvImages.setAdapter(imageAdapter);

        imageAdapter.setOnClickListener(() -> {
            ImageGetDialog dialog = new ImageGetDialog();
            dialog.setOnGetPath(strPath -> mPhotoPath = strPath);
            FragmentManager fragmentManager = getFragmentManager();
            dialog.show(fragmentManager,"IMAGE");
        });
    }

    private void initAdapter(List<OrderDetail> orderDetails) {
        adapter = new OrderDetailAdapter(orderDetails);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @OnClick({R.id.rl_back,R.id.tv_call_phone,R.id.tv_nav,R.id.tv_call,R.id.tv_cancel,R.id.tv_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_phone:
            case R.id.tv_call_phone:
                callPhone(orderDetail.getStorePhone());
                break;
            case R.id.tv_call:
                callPhone(orderDetail.getPhone());
                break;
            case R.id.tv_nav:
                String[] split = new String[1];
                if(orderDetail.getRiderOrderStatus().equals("1")) {
                    if(orderDetail.getLongitudeLatitude() != null)
                    split = orderDetail.getStorLongitudeLatitude().split(",");
                }else {
                    if(orderDetail.getStorLongitudeLatitude() != null)
                    split = orderDetail.getLongitudeLatitude().split(",");
                }
                if(!TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1])) {
                    LocationUtils.getInstance().showMapPop(this,split[0],split[1]);
                }else {
                    ToastUtils.show(this,"经纬度不存在");
                }
                break;
            case R.id.tv_cancel:
                cancelOrder();
                break;
        }
    }

    private void cancelOrder() {
        alertDialog.setGone().setMsg("您是否取消该订单")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", view -> {
                    robOrder(0);
                }).show();
    }

    private void callPhone(String storePhone) {
        alertDialog.setTitle("您确定拨打：").setMsg(storePhone)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", view -> {
                    call(storePhone);
                }).show();
    }

    private void call(String storePhone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + storePhone);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case ImageGetDialog.REQUEST_TAKE_PHOTO:
                    LogUtils.e("mPhotoPath:",mPhotoPath);
                    if(resultCode == Activity.RESULT_OK && mPhotoPath != null){
                        lubanZip(mPhotoPath);
                    }
                    break;
                case ImageGetDialog.REQUEST_TAKE_ALBUM:
                    if(resultCode == Activity.RESULT_OK &&
                            data.getData() != null){
                        String path = ImageUtils.getInstance().getFilePathMethod(this, data.getData());
 //                       Uri imageUri = data.getData();
                        LogUtils.e("path:",path);
                        lubanZip(path);
                    }
                    break;
                default:
                    break;
            }
    }

    private void lubanZip(String mPhotoPath) {
        Luban.with(this)
                .load(mPhotoPath)
                .ignoreBy(100)
                .setTargetDir(getPath())
                .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        postImage(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        File file = new File(mPhotoPath);
                        postImage(file);
                    }
                })
                .launch();

    }

    private void postImage(File file) {
        RequestBody fileRQ = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileRQ);

        RetrofitClient.getInstance(this).Api()
                .postImage(part)
                .enqueue(new Callback<OrderImage>() {
                    @Override
                    public void onResponse(Call<OrderImage> call, Response<OrderImage> response) {
                        if(response.isSuccessful() && response.code() == 200) {
                            ToastUtils.show(RiderOrderDetailActivity.this,"图片上传成功");
                            imgList.add(response.body().getImagePath());
                            imageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderImage> call, Throwable t) {
                        ToastUtils.show(RiderOrderDetailActivity.this,t.getMessage());
                    }
                });
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

}
