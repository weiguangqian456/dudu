package com.weiwei.salemall.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxz.PagerSlidingTabStrip;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
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
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.entity.CustomShareEntity;
import com.weiwei.home.fragment.CustomShareDialog;
import com.weiwei.home.test.ShareListener;
import com.weiwei.home.utils.ImageFileUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.ItemTitlePagerAdapter;
import com.weiwei.salemall.adapter.ShareAdapter;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ShareItemBean;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.emsemob.ChatActivity;
import com.weiwei.salemall.emsemob.EaseMobMsgHelper;
import com.weiwei.salemall.fragment.GoodsCommentFragment;
import com.weiwei.salemall.fragment.GoodsDetailFragment;
import com.weiwei.salemall.fragment.GoodsInfoFragment;
import com.weiwei.salemall.inter.OnFragmentClickListener;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.BadgeView;
import com.weiwei.salemall.widget.CustomProgressDialog;
import com.weiwei.salemall.widget.NoScrollViewPager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.base.dataprovider.DfineAction.COMPANY_NAME;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_DEFAULT_PASSWORD;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_IMSERVER;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TAGNAME;
import static com.weiwei.base.dataprovider.DfineAction.SHARE_CONTENT;
import static com.weiwei.base.dataprovider.DfineAction.VS_SHAARE_URL;
import static com.weiwei.base.dataprovider.DfineAction.VS_SHAARE_URL_END;
import static com.weiwei.salemall.fragment.GoodsInfoFragment.action;

/**
 * @author Created by EDZ on 2018/5/23.
 * 商品详情界面
 */

public class GoodsDetailActivity extends FragmentActivity implements View.OnClickListener, PopupWindow.OnDismissListener {
    @BindView(R.id.psts_tabs)
    public PagerSlidingTabStrip psts_tabs;
    @BindView(R.id.vp_content)
    public NoScrollViewPager vp_content;
    @BindView(R.id.tv_title)
    public TextView tv_title;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.rl_share)
    RelativeLayout shareRl;
    /**
     * 正常商品
     */
    @BindView(R.id.ll_normal_shop)
    LinearLayout nomarlShopLl;
    @BindView(R.id.rl_shoppingcart)
    FrameLayout rl_shoppingcart;
    @BindView(R.id.bg_shoppingcart)
    BadgeView shoppingcartBg;
    @BindView(R.id.fl_customerservice)
    FrameLayout customerServiceRl;
    @BindView(R.id.bg_customservice)
    BadgeView customserviceBg;
    @BindView(R.id.btn_buy)
    Button buyBtn;
    @BindView(R.id.btn_add_shoppingCart)
    Button addShoppingCartBtn;

    /**
     * 秒杀商品相关
     */
    @BindView(R.id.ll_seckill_shop)
    LinearLayout seckillShopLl;
    @BindView(R.id.seckill_fl_customerservice)
    FrameLayout customerserviceFl;
    @BindView(R.id.seckill_bg_customservice)
    BadgeView seckillCustomserviceBg;
    @BindView(R.id.seckill_btn_buy)
    public Button seckillBuyBtn;
    @BindView(R.id.tv_remaining_stock)
    public TextView remainingStockTv;

    @BindView(R.id.ll_dudu_seckill_shop)
    LinearLayout mLlDuduSeckillShop;
    @BindView(R.id.bv_dudu_seckill_customservice)
    BadgeView mBvDuduSeckillCustomservice;
    @BindView(R.id.fl_dudu_seckill_customerservice)
    FrameLayout mFlDuduSeckillCustomerservice;
    @BindView(R.id.tv_dudu_remaining_stock)
    public TextView mTvDuduRemainingStock;
    @BindView(R.id.btn_dudu_seckill_buy)
    public Button mBtnDuduSeckillBuy;
    @BindView(R.id.ll_title_root)
    LinearLayout mLlTitleRoot;
    @BindView(R.id.mFrameLayout)
    public FrameLayout mFrameLayout;

    private List<Fragment> fragmentList = new ArrayList<>();
    private GoodsInfoFragment goodsInfoFragment;
    private GoodsDetailFragment goodsDetailFragment;
    private GoodsCommentFragment goodsCommentFragment;

    /**
     * 传进来的产品编号
     */
    private String productNo = "";
    public String productImage = "";
    public String productName = "";
    private String columnId;
    private String productPrice = ""; //商品价格

    /**
     * 是否是抢购专区商品
     */
    private String secKill;
    private String seckillProductId;

    private ItemTitlePagerAdapter itemTitlePagerAdapter;
    private String[] strings = new String[]{"商品", "详情", "评论"};

    private OnFragmentClickListener onFragmentClickListener;

    private static ShoppingItemsBeanDao shoppingItemsBeanDao;
    private int shoppingCartcount;

    private static final String TAG = "GoodsDetailActivity";

    //类型
    private String type;

    //分享窗口
    private String shareUrl;
    private PopupWindow shareDialog;
    private ShareAdapter shareAdapter;
    private List<ShareItemBean> shareInfos = new ArrayList<>();

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    SocializeListeners.SnsPostListener mSnsPostListener = null;

    public CustomProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(R.layout.activity_goods_detail);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(mLlTitleRoot).init();
        initView();
        initBroadCast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //智齿未读客服消息
//        setCustomerServiceBadgeView(VsApplication.getInstance().getNoReadMsg());
        //环信
        setCustomerServiceBadgeView(getEaseMobNoReadMsgCount());

        //购物车数量信息
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", getTotalNum());
        mContext.sendBroadcast(intent);
    }

    private int getEaseMobNoReadMsgCount() {
        int noReadMsgCount = 0;
        if (ChatClient.getInstance().isLoggedInBefore()) {
            Conversation conversation = ChatClient.getInstance().chatManager().getConversation(EASEMOB_IMSERVER);
            noReadMsgCount = conversation.unreadMessagesCount();
        }
        Log.e(EASEMOB_TAGNAME, TAG + "未读消息msg===》" + noReadMsgCount);
        return noReadMsgCount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(myBroadCastReceiver);
        if (mController != null) {
            mController.getConfig().cleanListeners();
        }
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
            setShoppingCartBadgeView(shoppingCartcount);
        }
    };

    private void setShoppingCartBadgeView(int count) {
        if (0 != count) {
            shoppingcartBg.setVisibility(View.VISIBLE);
            shoppingcartBg.setText(count + "");
            if (count > 10) {
                shoppingcartBg.setText("10+");
            }
        } else {
            shoppingcartBg.setVisibility(View.GONE);
        }
    }

    private void setCustomerServiceBadgeView(int count) {
        if (0 != count) {
            customserviceBg.setVisibility(View.VISIBLE);
            seckillCustomserviceBg.setVisibility(View.VISIBLE);
            mBvDuduSeckillCustomservice.setVisibility(View.VISIBLE);
            customserviceBg.setText(count + "");
            seckillCustomserviceBg.setText(count + "");
            mBvDuduSeckillCustomservice.setText(count + "");
            if (count > 10) {
                customserviceBg.setText("10+");
                seckillCustomserviceBg.setText("10+");
                mBvDuduSeckillCustomservice.setText("10+");
            }
        } else {
            customserviceBg.setVisibility(View.GONE);
            seckillCustomserviceBg.setVisibility(View.GONE);
            mBvDuduSeckillCustomservice.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    private void initView() {
        Intent intent = getIntent();
        productNo = intent.getStringExtra("productNo");
        productImage = intent.getStringExtra("productImage");
        productName = intent.getStringExtra("productName");
        productPrice = intent.getStringExtra("productPrice");
        columnId = intent.getStringExtra("columnId");
        secKill = intent.getStringExtra("seckill");
        seckillProductId = intent.getStringExtra("seckillProductId");
        type = intent.getStringExtra("type");

        if (secKill != null) {
            switch (secKill) {
                case "seckill":
                    seckillShopLl.setVisibility(View.VISIBLE);
                    mLlDuduSeckillShop.setVisibility(View.GONE);
                    nomarlShopLl.setVisibility(View.GONE);
                    break;
                case "duduSeckill":
                    seckillShopLl.setVisibility(View.GONE);
                    mLlDuduSeckillShop.setVisibility(View.VISIBLE);
                    nomarlShopLl.setVisibility(View.GONE);
                    break;
                default:
                    seckillShopLl.setVisibility(View.GONE);
                    mLlDuduSeckillShop.setVisibility(View.GONE);
                    nomarlShopLl.setVisibility(View.VISIBLE);
                    break;
            }
        }
        fragmentList.add(goodsInfoFragment = new GoodsInfoFragment());
        fragmentList.add(goodsDetailFragment = new GoodsDetailFragment());
        fragmentList.add(goodsCommentFragment = new GoodsCommentFragment());

        itemTitlePagerAdapter = new ItemTitlePagerAdapter(getSupportFragmentManager(), fragmentList, strings, productNo, columnId, secKill, seckillProductId, type);

        vp_content.setAdapter(itemTitlePagerAdapter);
        vp_content.setOffscreenPageLimit(1);
        psts_tabs.setViewPager(vp_content);

        shareRl.setOnClickListener(this);
        backRl.setOnClickListener(this);

        customerServiceRl.setOnClickListener(this);
        addShoppingCartBtn.setOnClickListener(this);
        rl_shoppingcart.setOnClickListener(this);

        customerserviceFl.setOnClickListener(this);
        mFlDuduSeckillCustomerservice.setOnClickListener(this);
        seckillBuyBtn.setOnClickListener(this);
        mBtnDuduSeckillBuy.setOnClickListener(this);
        buyBtn.setOnClickListener(this);

        //分享相关
        initShareParams();
        initShareDialog();


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
        loadingDialog.show();
    }

    private void initShareParams() {
        //添加微信
        UMWXHandler wxHandler = new UMWXHandler(GoodsDetailActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(GoodsDetailActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //添加QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(GoodsDetailActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qqSsoHandler.addToSocialSDK();

        //添加空间
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(GoodsDetailActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void initShareContent() {
        shareUrl = VS_SHAARE_URL + "&productNo=" + productNo + "&uid=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) + VS_SHAARE_URL_END;

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(SHARE_CONTENT);
        weixinContent.setTitle(productName);
        weixinContent.setShareImage(new UMImage(GoodsDetailActivity.this, productImage));
        weixinContent.setTargetUrl(shareUrl);
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(SHARE_CONTENT);
        circleMedia.setTitle(productName);
        circleMedia.setShareImage(new UMImage(GoodsDetailActivity.this, productImage));
        circleMedia.setTargetUrl(shareUrl);
        mController.setShareMedia(circleMedia);

        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(SHARE_CONTENT);
        qqShareContent.setTitle(productName);
        qqShareContent.setShareImage(new UMImage(GoodsDetailActivity.this, productImage));
        qqShareContent.setTargetUrl(shareUrl);
        mController.setShareMedia(qqShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(SHARE_CONTENT);
        qzone.setTargetUrl(shareUrl);
        qzone.setTitle(productName);
        qzone.setShareImage(new UMImage(GoodsDetailActivity.this, productImage));
        mController.setShareMedia(qzone);


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
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_buy:     //立即购买
            case R.id.seckill_btn_buy:
            case R.id.btn_dudu_seckill_buy:
                if (onFragmentClickListener != null) {
                    onFragmentClickListener.OnFragmentClick("buy");
                }
                break;
            case R.id.btn_add_shoppingCart:    //加入购物车
                if (getString(R.string.exchange_region).equals(type)) {
                    ToastUtils.show(mContext, "兑换专区商品无法加入购物车");
                    break;
                }
                if (onFragmentClickListener != null) {
                    onFragmentClickListener.OnFragmentClick("addshoppongcart");
                }
                break;
            case R.id.rl_shoppingcart:
                MessageEvent bean = new MessageEvent();
                bean.setMessage("shoppingcart_fragment");
                EventBus.getDefault().post(bean);
                Intent intent=new Intent();
                intent.putExtra("indicator",3);

                intent.setClass(this,VsMainActivity.class);
                startActivity(intent);
                finish();
              //  SkipPageUtils.getInstance(this).skipPage(VsMainActivity.class);
               // finish();
                break;
            case R.id.rl_share:
                if(VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    initShareContent();//分享商品
                    showShareDialog(v);
                }
                break;
            case R.id.fl_customerservice: //客服
            case R.id.seckill_fl_customerservice:
            case R.id.fl_dudu_seckill_customerservice:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
//                    initImformation();   //智齿
                    gotoChat();            //环信
                }
                break;
            default:
                break;
        }
    }

    private void gotoChat() {
        if (ChatClient.getInstance().isLoggedInBefore()) {
            //已经登录，可以直接进入会话界面
            Message message = Message.createTxtSendMessage(productName, EASEMOB_IMSERVER);
            message.addContent(EaseMobMsgHelper.createOrderInfo(this, productNo, productPrice, productName, productImage, shareUrl));
            ChatClient.getInstance().chatManager().sendMessage(message);

            Intent intent = new IntentBuilder(this).setTargetClass(ChatActivity.class).setVisitorInfo(EaseMobMsgHelper.createVisitorInfo(productNo)).setServiceIMNumber
                    (EASEMOB_IMSERVER).setTitleName(COMPANY_NAME).setShowUserNick(true).build();
            startActivity(intent);
        } else {
            loginEaseMob();
        }
    }

    private void loginEaseMob() {
        String uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId);
        if (!StringUtils.isEmpty(uid)) {
            ChatClient.getInstance().login(uid, EASEMOB_DEFAULT_PASSWORD, new Callback() {
                @Override
                public void onSuccess() {
                    Log.e(EASEMOB_TAGNAME, "demo login succedd");
                    //已经登录，可以直接进入会话界面
                    Message message = Message.createTxtSendMessage(productName, EASEMOB_IMSERVER);
                    message.addContent(EaseMobMsgHelper.createOrderInfo(GoodsDetailActivity.this, productNo, productPrice, productName, productImage, shareUrl));
                    ChatClient.getInstance().chatManager().sendMessage(message);

                    Intent intent = new IntentBuilder(GoodsDetailActivity.this).setTargetClass(ChatActivity.class).setVisitorInfo(EaseMobMsgHelper.createVisitorInfo(productNo))
                            .setServiceIMNumber(EASEMOB_IMSERVER).setTitleName(COMPANY_NAME).setShowUserNick(true).build();
                    startActivity(intent);
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login fail");
                }

                @Override
                public void onProgress(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login progress");
                }
            });
        } else {
            ToastUtils.show(GoodsDetailActivity.this, "登录后方可联系客服");
        }
    }

    /**
     * 配置客服信息
     */
    private void initImformation() {
//        Information info = new Information();
////        info.setAppkey(ZHICHI_APP_KEY);
//        info.setAppkey(ZHICHI_APP_KEY);
//        info.setTel(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//        info.setUid(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//        //客服模式控制 -1不控制 按照服务器后台设置的模式运行  1仅机器人 2仅人工 3机器人优先 4人工优先
//        info.setInitModeType(2);
//        //拿到用户信息
//        Map<String, String> customInfo = new HashMap<String, String>();
//        customInfo.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//        customInfo.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//        customInfo.put("productNo", productNo);
//        info.setCustomInfo(customInfo);
//        //发送商品信息
//        ConsultingContent consultingContent = new ConsultingContent();
//        consultingContent.setSobotGoodsTitle(productName);
//        consultingContent.setSobotGoodsImgUrl(productImage);
//        consultingContent.setSobotGoodsFromUrl(shareUrl);
//        consultingContent.setSobotGoodsDescribe(productName);
//        consultingContent.setSobotGoodsLable("");
//        info.setConsultingContent(consultingContent);
//        //返回是否显示客服评价
//        info.setShowSatisfaction(true);
//        //设置用户头像
//        info.setFace(SOBOT_FACE);
//        VsApplication.getInstance().setNoReadMsg(0);
//        SobotApi.startSobotChat(this, info);
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

               /* if (CheckLoginStatusUtils.isLogin()) {
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
                } else {
                    SkipPageUtils.getInstance(GoodsDetailActivity.this).skipPage(VsLoginActivity.class);
                }*/
            }
        });
        shareRv.setLayoutManager(new GridLayoutManager(mContext, 3));
        shareRv.setAdapter(shareAdapter);
        int height = (int) getResources().getDimension(R.dimen.w_277_dip);
        shareDialog = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, height, true);
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
        mController.postShare(GoodsDetailActivity.this, qzone, snsPostListener());
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

    public void setOnFragmentClickListener(OnFragmentClickListener onFragmentClickListener) {
        this.onFragmentClickListener = onFragmentClickListener;
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

   /*   @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                LogUtils.e("ToVmp","ORIENTATION_LANDSCAPE");
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                LogUtils.e("ToVmp","ORIENTATION_PORTRAIT");
                break;
        }
    }*/
}
