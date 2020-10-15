package com.weiwei.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.weiwei.base.activity.recharge.VsRechargeBangbangCard;
import com.weiwei.base.activity.recharge.VsRechargePayTypes;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.ChargePackageItem;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.adapter.ShareAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.BalanceWallet;
import com.weiwei.salemall.bean.PackageItem;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShareItemBean;
import com.weiwei.salemall.fastjson.FastJsonConverterFactory;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/11/15.
 *         Describe Vip会员界面
 */
public class VipMemberActivity extends TempAppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener {
    /**
     * 标题栏
     */
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_right)
    TextView rightTv;
    /**
     * 用户等级
     */
    @BindView(R.id.iv_user_level)
    ImageView userLevelIv;
    /**
     * 用户等级名字
     */
    @BindView(R.id.tv_user_level)
    TextView userLevelTv;
    /**
     * 用户名
     */
    @BindView(R.id.tv_user_name)
    TextView userNameTv;
    /**
     * 到期时间
     */
    @BindView(R.id.tv_expired_time)
    TextView expiredTimeTv;
    /**
     * 红包成长金
     */
    @BindView(R.id.tv_redbag_integral)
    TextView redbagIntegralTv;
    /**
     * 加油余额
     */
    @BindView(R.id.tv_coupon)
    TextView couponTv;
    /**
     * 分享红包
     */
    @BindView(R.id.tv_share_redbag)
    TextView shareRedbagTv;
    /**
     * 分享按钮
     */
    @BindView(R.id.btn_share)
    TextView shareBtn;
    /**
     * 充值299
     */
    @BindView(R.id.ll_recharge1)
    LinearLayout rechargeLl1;
    @BindView(R.id.tv_chearge_money1)
    TextView chargeTv1;
    /**
     * 充值3600
     */
    @BindView(R.id.ll_recharge2)
    LinearLayout rechargeLl2;
    @BindView(R.id.tv_chearge_money2)
    TextView chargeTv2;
    /**
     * 充值99
     */
    @BindView(R.id.ll_recharge3)
    LinearLayout rechargeLl3;
    @BindView(R.id.tv_chearge_money3)
    TextView chargeTv3;

    @BindView(R.id.ll_total_points)
    LinearLayout redBagLl;
    @BindView(R.id.ll_withdraw_integral)
    LinearLayout shareredBagLl;
    @BindView(R.id.ll_point_balance)
    LinearLayout couponLl;

    private static final String TAG = "VipMemberActivity";
    private String userName;
    private String phone;
    private String expridTime;

    private String coupon;      //兑换券
    private String red;         //红包
    private String invitation;  //邀请红包余额

    //分享窗口
    private String shareUrl;
    private PopupWindow shareDialog;
    private ShareAdapter shareAdapter;
    private List<ShareItemBean> shareInfos = new ArrayList<>();

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    SocializeListeners.SnsPostListener mSnsPostListener = null;

    private String baseUrl;
    /**
     * 充值选项列表
     */
    private List<ChargePackageItem> chargePackageItemList;

    /**
     * 进度加载
     */
    private CustomProgressDialog loadingDialog;

    /**
     * 广播注册标志
     */
    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersionStateMode();
        setContentView(R.layout.activity_vip_member);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodList();
        getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myRedReceiver != null && isRegister) {
            unregisterReceiver(myRedReceiver);
            isRegister = false;
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    private void setImmersionStateMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.vs_white));
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 获取到期天数
     */
    private void getBalanceWallet() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.actionSeaRchbalance);
        isRegister = true;
        mContext.registerReceiver(myRedReceiver, filter);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_BALANCE, DfineAction.authType_UID, treeMap, GlobalVariables.actionSeaRchbalance);
    }

    private BroadcastReceiver myRedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GlobalVariables.actionSeaRchbalance)) {
                String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
                ResultEntity resultEntity = JSONObject.parseObject(jStr, ResultEntity.class);
                BalanceWallet balanceWallet = JSONObject.parseObject(resultEntity.getData().toString(), BalanceWallet.class);
                List<PackageItem> packageItemList = JSONObject.parseArray(balanceWallet.getPackage_list().toString(), PackageItem.class);
                if (packageItemList != null && packageItemList.size() > 0) {
                    //最后一次充值记录的到期时间
                    String expire_time = packageItemList.get(packageItemList.size() - 1).getExpire_time();
                    String isTime = packageItemList.get(packageItemList.size() - 1).getIs_time();
                    String leftCallTime = packageItemList.get(packageItemList.size() - 1).getLeft_call_time();
                    Log.e(TAG, "onReceive到期时间msg===>" + expire_time);
                    if (isTime.equals("y")) {    //显示时长
                        VsUserConfig.setData(mContext, "baonian", leftCallTime + "分钟");





                        expiredTimeTv.setText("会员权限还有" + leftCallTime + "分钟");
                    } else if (isTime.equals("n")) {  //显示天数
                        VsUserConfig.setData(mContext, "baonian", getTime(expire_time) + "天");
                        expiredTimeTv.setText("会员权限还有" + getTime(expire_time) + "天");
                    }
                }
            }
        }
    };

    /**
     * 获取充值列表
     */
    private void getGoodList() {
        if (!this.isFinishing()) {
            loadingDialog.setLoadingDialogShow();
        }
        baseUrl = VsApplication.getContext().getResources().getString(R.string.uri_prefix) + "/" + "8.0.1" + "/" + DfineAction.brand_id + "/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(FastJsonConverterFactory.create()).build();
        //获取接口
        ApiService service = retrofit.create(ApiService.class);

        Map<String, String> param = new HashMap<>();
        param.put("appId", DfineAction.brand_id);
        Call<List<ChargePackageItem>> call = service.getGoodList(param);
        call.enqueue(new Callback<List<ChargePackageItem>>() {
            @Override
            public void onResponse(Call<List<ChargePackageItem>> call, Response<List<ChargePackageItem>> response) {
                if (response.body() == null) {
                    return;
                } else {
                    chargePackageItemList = new ArrayList<>();
                    chargePackageItemList.addAll(response.body());
                    Log.e(TAG, "充值界面item msg===>" + JSON.toJSONString(chargePackageItemList));
                    if (chargeTv1 != null) chargeTv1.setText("价格：" + getItemPrice(chargePackageItemList, 1) + "元/年");
                    if (chargeTv2 != null) chargeTv2.setText("价格：" + getItemPrice(chargePackageItemList, 2) + "元/年");
                    if (chargeTv3 != null) chargeTv3.setText("价格：" + getItemPrice(chargePackageItemList, 3) + "元/年");
                }
            }

            @Override
            public void onFailure(Call<List<ChargePackageItem>> call, Throwable t) {
                Log.e(TAG, "onFailure");
            }
        });
    }

    /**
     * 处理价格显示
     * @param chargePackageItemList
     * @param i
     * @return
     */
    private double getItemPrice(List<ChargePackageItem> chargePackageItemList, int i) {
        return Double.parseDouble(chargePackageItemList.get(i - 1).getPrice()) / 10000;
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        userName = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname);
        phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname);
        expridTime = VsUserConfig.getDataString(mContext, "baonian");
        Log.e(TAG, "到期时间msg===>" + expridTime);
        if (!StringUtils.isEmpty(userName)) {
            userNameTv.setText(phone);
        } else {
            userNameTv.setText(userName);
        }
        if (!StringUtils.isEmpty(expridTime)) {
            expiredTimeTv.setText("会员权限还有" + expridTime);
        } else {
            getBalanceWallet();
        }
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call1 = api.getUserLevel();
        retrofit2.Call<ResultEntity> call2 = api.getWallet();
        call1.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    RedBagEntity entity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
                    String userLevel = entity.getLevel();
                    String levelName = entity.getLevelName();
                    if (!StringUtils.isEmpty(levelName)) {
                        if (userLevelTv != null) {
                            userLevelTv.setText(levelName);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
            }
        });

        call2.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "我的成长金msg===>" + result.getData().toString());
                    RedBagEntity redBagEntity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
                    coupon = redBagEntity.getExchangePoint();
                    red = redBagEntity.getRed();
                    invitation = redBagEntity.getInvitation();
                    if (shareRedbagTv != null) {
                        shareRedbagTv.setText(invitation);
                    }
                    if (redbagIntegralTv != null) {
                        redbagIntegralTv.setText(red);
                    }
                    if (couponTv != null) {
                        couponTv.setText(coupon);
                    }
                } else {
                    Toast.makeText(VipMemberActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
                loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    private void initView() {
        titleTv.setText("VIP会员");
        backRl.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        rechargeLl1.setOnClickListener(this);
        rechargeLl2.setOnClickListener(this);
        rechargeLl3.setOnClickListener(this);
        redBagLl.setOnClickListener(this);
        shareredBagLl.setOnClickListener(this);
        couponLl.setOnClickListener(this);

        //分享相关
        initShareParams();
        initShareDialog();
        initShareContent();

        mSnsPostListener = new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Log.e(TAG, "分享监听完成");
                }
            }
        };
        mController.registerListener(mSnsPostListener);

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    }

    @Override
    protected void init() {

    }

    private void initShareParams() {
        //添加微信
        UMWXHandler wxHandler = new UMWXHandler(VipMemberActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(VipMemberActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //添加QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(VipMemberActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qqSsoHandler.addToSocialSDK();

        //添加空间
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(VipMemberActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void initShareContent() {
        String product = DfineAction.RES.getString(R.string.product);
        String url = "";
        if (shareContent().indexOf("http") > 0) {
            url = shareContent().substring(shareContent().indexOf("http"));
        }
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(shareContent());
        weixinContent.setTitle(product);
        weixinContent.setTargetUrl(url);
        Bitmap iconMap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        UMImage localImage = new UMImage(this, compressBitmap(iconMap));
        weixinContent.setShareImage(localImage);
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareContent());// (product + "电话分享朋友圈");
        circleMedia.setTitle(product);
        circleMedia.setShareImage(localImage);
        circleMedia.setTargetUrl(url);// DfineAction.WAPURI);
        mController.setShareMedia(circleMedia);

        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(shareContent());// (product + "电话分享测试qq");
        qqShareContent.setTitle(product);
        qqShareContent.setTargetUrl(url);// DfineAction.WAPURI);
        qqShareContent.setShareImage(localImage);
        mController.setShareMedia(qqShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        qZoneShareContent.setShareContent(shareContent());// (product + "电话分享qqZone");
        qZoneShareContent.setTitle(product);
        qZoneShareContent.setTargetUrl(url);// DfineAction.WAPURI);
        qZoneShareContent.setShareImage(localImage);
        mController.setShareMedia(qZoneShareContent);
    }

    /**
     * 设置分享内容
     *
     * @return
     */
    public String shareContent() {
        // 推荐好友
        String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
        if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
            mRecommendInfo = DfineAction.InviteFriendInfo;
        }
        return mRecommendInfo;
    }

    /**
     * 压缩图片
     *
     * @param bitMap
     */
    private Bitmap compressBitmap(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 99;
        int newHeight = 99;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        return newBitMap;
    }

    private void initShareDialog() {
        ShareItemBean shareInfo = new ShareItemBean(R.drawable.ic_wechat, "微信好友");
        shareInfos.add(shareInfo);
        shareInfo = new ShareItemBean(R.drawable.ic_moments, "朋友圈");
        shareInfos.add(shareInfo);
        shareInfo = new ShareItemBean(R.drawable.ic_qq, "QQ好友");
        shareInfos.add(shareInfo);
        shareInfo = new ShareItemBean(R.drawable.ic_zone, "QQ空间");
        shareInfos.add(shareInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:     //显示充值卡弹窗
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                skipPage(VsRechargeBangbangCard.class);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_share:    //分享app
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                showShareDialog(v);
                break;
            case R.id.ll_recharge1:  //充值299
                goToRechargePayTypesActivity(1);
                break;
            case R.id.ll_recharge2: //充值3600
                goToRechargePayTypesActivity(2);
                break;
            case R.id.ll_recharge3: //充值99
                goToRechargePayTypesActivity(3);
                break;
            case R.id.ll_total_points:
            case R.id.ll_withdraw_integral:
            case R.id.ll_point_balance:
                skipPage(MyScoresActivity.class);
                break;
            default:
                break;
        }
    }

    private void goToRechargePayTypesActivity(int i) {
        Intent intent = new Intent();
        intent.putExtra("brandid", chargePackageItemList.get(i - 1).getBid());
        intent.putExtra("goodsdes", chargePackageItemList.get(i - 1).getDes());
        intent.putExtra("recommend_flag", chargePackageItemList.get(i - 1).getRecommend_flag());
        intent.putExtra("convert_price", chargePackageItemList.get(i - 1).getConvert_price());
        intent.putExtra("present", chargePackageItemList.get(i - 1).getPresent());
        intent.putExtra("goodsid", chargePackageItemList.get(i - 1).getGoods_id());
        intent.putExtra("goodsvalue", chargePackageItemList.get(i - 1).getPrice());
        intent.putExtra("goodsname", chargePackageItemList.get(i - 1).getGoods_name());
        intent.putExtra("pure_name", chargePackageItemList.get(i - 1).getGoods_name());
        Log.e(TAG, chargePackageItemList.get(i - 1).getGoods_id() + chargePackageItemList.get(i - 1).getGoods_name() + chargePackageItemList.get(i - 1).getPrice());
        intent.setClass(this, VsRechargePayTypes.class);
        startActivity(intent);
    }

    /**
     * 分享框
     *
     * @param rootView
     */
    private void showShareDialog(View rootView) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_share, null);
        RecyclerView shareRv = (RecyclerView) contentView.findViewById(R.id.rv_share);
        final TextView cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        shareAdapter = new ShareAdapter(shareInfos);
        shareAdapter.setOnItemClickListener(new ShareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                shareDialog.dismiss();
                switch (position) {
                    case 0: //微信好友
                        shareToMedia(SHARE_MEDIA.WEIXIN);
                        break;
                    case 1://朋友圈
                        shareToMedia(SHARE_MEDIA.WEIXIN_CIRCLE);
                        break;
                    case 2://QQ好友
                        shareToMedia(SHARE_MEDIA.QQ);
                        break;
                    case 3://QQ空间
                        shareToMedia(SHARE_MEDIA.QZONE);
                        break;
                    default:
                        break;
                }
            }
        });
        shareRv.setLayoutManager(new GridLayoutManager(mContext, 4));
        shareRv.setAdapter(shareAdapter);
        int dialog_hight = (int) getResources().getDimension(R.dimen.w_200_dip);
        shareDialog = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, dialog_hight, true);
        shareDialog.setAnimationStyle(R.style.PopupWindowAnimation);
        shareDialog.setOutsideTouchable(true);
        backgroundAlpha(0.3f);
        shareDialog.setOnDismissListener(this);
        shareDialog.setBackgroundDrawable(new BitmapDrawable());
        shareDialog.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
    }

    private void shareToMedia(SHARE_MEDIA qzone) {
        mController.postShare(VipMemberActivity.this, qzone, snsPostListener());
    }

    private SocializeListeners.SnsPostListener snsPostListener() {
        return mSnsPostListener;
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1.0f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 时间转天数
     *
     * @param time
     * @return
     */
    public String getTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(time);
            Date d2 = new Date(System.currentTimeMillis());// 你也可以获取当前时间
            long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            // System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            return days + "";
        } catch (Exception e) {
            return "0";
        }
    }
}
