package com.weiwei.home.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.hwtx.dududh.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
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
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.entity.CustomShareEntity;
import com.weiwei.home.fragment.CustomShareDialog;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.test.ShareListener;
import com.weiwei.home.utils.ImageFileUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.rider.adapter.ViewSwitcherHelper;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.adapter.BannerAdapter;
import com.weiwei.salemall.adapter.NetworkImageHolderView;
import com.weiwei.salemall.adapter.ShareAdapter;
import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.bean.ProductEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShareItemBean;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.widget.CustomProgressDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;
import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.base.dataprovider.DfineAction.REFUEL_SHARE_CONTENT;
import static com.weiwei.base.dataprovider.DfineAction.SHARE_CONTENT;
import static com.weiwei.base.dataprovider.DfineAction.VS_SHAARE_URL;
import static com.weiwei.base.dataprovider.DfineAction.VS_SHAARE_URL_END;
import static com.weiwei.salemall.base.Const.BASE_IMAGE_URL;

public class RefuelVideoActivity extends FragmentActivity implements PopupWindow.OnDismissListener{

    private static final String TAG = "RefuelVideoActivity";
    @BindView(R.id.ll_title_root)
    LinearLayout mLlTitleRoot;
    @BindView(R.id.vp_item_goods_img)
    ConvenientBanner vp_item_goods_img;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.btn_refuel)
    Button btnRefuel;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ll_del)
    LinearLayout llDel;


    private List<String> imgUrls = new ArrayList<>();
    private List<View> mViewlist = new ArrayList<>();

    private String productNo;
    private String columnId;
    private String productName;
    private String  productImage;
    private String description;
    private int descType;

    //分享窗口
    private String shareUrl;
    private PopupWindow shareDialog;
    private ShareAdapter shareAdapter;
    private List<ShareItemBean> shareInfos = new ArrayList<>();

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    SocializeListeners.SnsPostListener mSnsPostListener = null;

    private CustomProgressDialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(R.layout.activity_refuel_video);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(mLlTitleRoot).init();

        Intent intent = getIntent();
        productNo = intent.getStringExtra("productNo");

        loadingDialog = new CustomProgressDialog(this, "正在加载中...", R.drawable.loading_frame);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        loadingDialog.setLoadingDialogShow();

        initWebViewSetting();
        initData();
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                GSYVideoManager.onPause();
                if(viewPager.getCurrentItem() == position) {
                    llDel.getChildAt(position).setEnabled(true);
                    if(position > 0) {
                        llDel.getChildAt(position - 1).setEnabled(false);
                    }
                    if(position < imgUrls.size() - 1) {
                        llDel.getChildAt(position + 1).setEnabled(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void initView() {
        vp_item_goods_img.setPageIndicator(new int[]{R.drawable.index_white_point, R.drawable.index_blue_point});
        vp_item_goods_img.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }
    private void initShareParams() {
        //添加微信
        UMWXHandler wxHandler = new UMWXHandler(RefuelVideoActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(RefuelVideoActivity.this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //添加QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(RefuelVideoActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qqSsoHandler.addToSocialSDK();

        //添加空间
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(RefuelVideoActivity.this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void initShareContent() {
        shareUrl = VS_SHAARE_URL + "&productNo=" + productNo + "&uid=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) + VS_SHAARE_URL_END;

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(REFUEL_SHARE_CONTENT);
        weixinContent.setTitle(productName);
        weixinContent.setShareImage(new UMImage(RefuelVideoActivity.this, productImage));
        weixinContent.setTargetUrl(shareUrl);
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(REFUEL_SHARE_CONTENT);
        circleMedia.setTitle(productName);
        circleMedia.setShareImage(new UMImage(RefuelVideoActivity.this, productImage));
        circleMedia.setTargetUrl(shareUrl);
        mController.setShareMedia(circleMedia);

        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(REFUEL_SHARE_CONTENT);
        qqShareContent.setTitle(productName);
        qqShareContent.setShareImage(new UMImage(RefuelVideoActivity.this, productImage));
        qqShareContent.setTargetUrl(shareUrl);
        mController.setShareMedia(qqShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(REFUEL_SHARE_CONTENT);
        qzone.setTargetUrl(shareUrl);
        qzone.setTitle(productName);
        qzone.setShareImage(new UMImage(RefuelVideoActivity.this, productImage));
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


    private void initData() {
        Map<String, String> param = new HashMap<>();
        RetrofitClient.getInstance(this).Api().getProductDetail(productNo,param)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        loadingDialog.setLoadingDialogDismiss();
                       if(result != null && result.getData() != null) {
                           ProductEntity productEntity = JSON.parseObject(result.getData().toString(), ProductEntity.class);
                           productName = productEntity.getProductName();
                           productImage = BASE_IMAGE_URL + productEntity.getPicture();
                           tvName.setText(productName);
                           if(productEntity.getVideos() != null && productEntity.getVideos().size() > 0) {
                               imgUrls.addAll(productEntity.getVideos());
                              vp_item_goods_img.setVisibility(View.GONE);
                              viewPager.setVisibility(View.VISIBLE);
                              llDel.setVisibility(View.VISIBLE);
                           }
                           if(productEntity.getImages() != null && productEntity.getImages().size() > 0) {
                               imgUrls.addAll(productEntity.getImages());
                           }
                           if(productEntity.getVideos() != null && productEntity.getVideos().size() > 0) {
                               setBannerview(imgUrls);
                           }else {
                               initView();
                               vp_item_goods_img.setPages(new CBViewHolderCreator() {
                                   @Override
                                   public Object createHolder() {
                                       return new NetworkImageHolderView();
                                   }
                               }, imgUrls);
                           }

                           description = productEntity.getDescription();
                           descType = productEntity.getDescType();
                           if(description != null) {
                               switch (descType) {
                                   case 0:
                                       webView.loadDataWithBaseURL(null, getNewContent(description), "text/html", "utf-8", null);
                                       break;
                                   case 1:
                                       webView.loadUrl(description);
                                       break;
                                   default:
                                       break;
                               }
                           }
                       }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        loadingDialog.setLoadingDialogDismiss();
                    }
                });



    }

    /**
     * 顶部轮播图
     */
    private OrientationUtils orientationUtils;

    protected void setBannerview(List<String> imgUrls) {
        addPoint(imgUrls.size());
        //区分图片和视频布局
        for (int i=0;i<imgUrls.size();i++){
            if (imgUrls.get(i).endsWith("jpg")||imgUrls.get(i).endsWith(".png")) {//1 为视频 以下布局中是视频布局
                View view = LayoutInflater.from(this).inflate(R.layout.view_guide_image, null);
                ImageView _imageview = view.findViewById(R.id.iv_center);
                Glide.with(this).load(BASE_IMAGE_URL + imgUrls.get(i)).into(_imageview);
                mViewlist.add(view);
            } else {//以下布局中为视频布局
                View view = LayoutInflater.from(this).inflate(R.layout.view_guide_video, null);
                StandardGSYVideoPlayer videoplayer =view.findViewById(R.id.player);
                videoplayer.getBackButton().setVisibility(View.GONE);

                orientationUtils = new OrientationUtils(this,videoplayer);
                //初始化不打开外部的旋转
                orientationUtils.setEnable(false);
                ImageView imagePlayer = new ImageView(this);
                loadVideoScreenshot(this,BASE_IMAGE_URL + imgUrls.get(i),imagePlayer,10000);
                GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
                gsyVideoOption.setThumbImageView(imagePlayer)
                        .setIsTouchWiget(false)
                        .setReleaseWhenLossAudio(false)
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(true)
                        .setNeedLockFull(false)
                        .setUrl(BASE_IMAGE_URL + imgUrls.get(i))
                        .setCacheWithPlay(true)
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);
                                //开始播放了才能旋转和全屏
                                if(orientationUtils != null) {
                                    orientationUtils.setEnable(false);
                                }
                            }
                            @Override
                            public void onQuitFullscreen(String url, Object... objects) {
                                super.onQuitFullscreen(url, objects);
                                if (orientationUtils != null) {
                                    orientationUtils.backToProtVideo();
                                }
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                            }
                        }).setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(false);
                        }
                    }
                }).build(videoplayer);

                videoplayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //直接横屏
                        orientationUtils.resolveByClick();
                        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                        videoplayer.startWindowFullscreen(RefuelVideoActivity.this, true, true);
                    }
                });
                mViewlist.add(view);
            }
        }
        viewPager.setAdapter(new BannerAdapter(mViewlist));
    }

    //todo 添加小圆点的方法
    private void addPoint(int size) {
        View view;
        for (int i = 0; i < size; i++) {
            //创建底部指示器(小圆点)
            view = new View(this);
            view.setBackgroundResource(R.drawable.background_point);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            //设置间隔
            if (i != 0) {
                layoutParams.leftMargin = 20;
            }
            //添加到LinearLayout
            llDel.addView(view, layoutParams);
        }
        llDel.getChildAt(0).setEnabled(true);
    }

    /**
     * 加载视频缩略图
     * @param context
     * @param uri
     * @param imageView
     * @param frameTimeMicros
     */
    private void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }



    private void initWebViewSetting() {
        webView.setLayerType(View.LAYER_TYPE_NONE, null);//开启硬件加速
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 网页内容的宽度自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //关键点
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        // 设置支持javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 设置显示缩放按钮
        webSettings.setBuiltInZoomControls(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240 || mDensity == 480) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }
        /*
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放*/

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webView.setWebChromeClient(new MyWebChromeClient());

    }

    private class MyWebChromeClient extends WebChromeClient {
        IX5WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            LogUtils.e("ToVmp","onShowCustomView");
            fullScreen();

            webView.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.VISIBLE);
            mFrameLayout.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);

        }

        @Override
        public void onHideCustomView() {
            LogUtils.e("ToVmp","onHideCustomView");
            fullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            webView.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
            mFrameLayout.removeAllViews();
            super.onHideCustomView();

        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LogUtils.e("ToVmp","横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LogUtils.e("ToVmp","竖屏");
        }
    }

   @OnClick({R.id.rl_back,R.id.rl_share,R.id.btn_refuel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_share:
                if(VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    initShareContent();//分享商品
                    showShareDialog(view);
                }

                break;
            case R.id.btn_refuel:
                if(PreferencesUtils.getBoolean(RefuelVideoActivity.this,"IS_LOCATION_SUCCESS")) {
                    MoreRefuelActivity.start(this, PreferencesUtils.getBoolean(RefuelVideoActivity.this,"IS_LOCATION_SUCCESS"),
                            Double.parseDouble(PreferencesUtils.getString(this,"userAddressLatitude")),
                            Double.parseDouble(PreferencesUtils.getString(this,"userAddressLongitude")));
                }else {
                    startActivity(new Intent(this,MoreRefuelActivity.class));
                }
                break;
        }
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
                    SkipPageUtils.getInstance(RefuelVideoActivity.this).skipPage(VsLoginActivity.class);
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
        mController.postShare(RefuelVideoActivity.this, qzone, snsPostListener());
    }

    private SocializeListeners.SnsPostListener snsPostListener() {
        return mSnsPostListener;
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1.0f);
    }

    @Override
    protected void onPause() {
        GSYVideoManager.onPause();
        super.onPause();

    }


    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 将html文本内容中包含img标签的图片，宽度变为屏幕宽度，高度根据宽度比例自适应
     **/
    public String getNewContent(String htmltext){
        String html = htmltext.replace("iframe","video");
        try {
            Document doc= Jsoup.parse(html);
            Elements elements=doc.getElementsByTag("video");
            for (Element element : elements) {
                String url = element.attr("src");
                element.attr("width","100%").attr("height","600px")
                        .attr("poster",url + "?vframe/jpg/offset/1")
                        .attr("controls","true")
                        .attr(" webkit-playsinline","false")
                        .attr("playsinline","false")
                        .attr("type","video/mp4");
            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if(orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }
}
