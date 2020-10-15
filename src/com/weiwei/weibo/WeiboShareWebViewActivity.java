package com.weiwei.weibo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.FileUtils;
import com.weiwei.base.common.VsJsonTool;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * @author: xkl
 */
@SuppressWarnings("deprecation")
@SuppressLint({"NewApi", "SetJavaScriptEnabled"})
public class WeiboShareWebViewActivity extends VsBaseActivity {
    private static final String TAG = WeiboShareWebViewActivity.class.getSimpleName();
    private static final String WEB_CACHE_FILE = "cache/webview";
    private Activity webViewActivity = this;
    TextView mCurrentTabView;
    WebView mWebView = null;
    // private ProgressDialog mProgressDialog;

    // private static final int MSG_LOADING_SPECIAL_PAGE = 0; // 正在加载页面
    // private static final int MSG_LOADING_SPECIAL_REFSH = 1; // 刷新
    // private static final int MSG_LOADING_SPECIAL_GO = 2; // 前进
    // private static final int MSG_LOADING_SPECIAL_BACK = 3; // 后退

    int times = 0;

    String mActivityState = "valid";
    private ImageView next, back;
    private ImageView refresh;
    private String business[];
    private LinearLayout web_nextback_layout;
    /**
     * 加载动画
     */
    private AnimationDrawable animationDrawable;
    /**
     * 加载的图片
     */
    private ImageView load_img;
    /**
     * 加载的文案
     */
    private TextView load_text;
    /**
     * 加载layout
     */
    private LinearLayout load_layout, load_error_ayout;
    private long TIME_OUT = 15000;
    private Timer mTimer;
    /**
     * 重新加载的url
     */
    private String curUrl;
    /**
     * 错误的url
     */
    private String errorUrl = "";
    /**
     * 是否加载界面出错
     */
    private boolean isLoadError = false;
    private SendNoteObserver noteObserver;
    /**
     * 发送d短信回调
     */
    private String smsCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_webview);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        //关闭左滑功能
        setDisEnableLeftSliding();
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);

        int defaultFontSize = getIntent().getIntExtra("AboutTextSize", 16);
        business = getIntent().getStringArrayExtra("AboutBusiness");

        // 设置标题
        mCurrentTabView = (TextView) findViewById(R.id.sys_title_txt);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setVisibility(View.GONE);
        next = (ImageView) findViewById(R.id.iamgeView1);
        back = (ImageView) findViewById(R.id.iamgeView2);
        refresh = (ImageView) findViewById(R.id.refsh);
        web_nextback_layout = (LinearLayout) findViewById(R.id.web_nextback_layout);
        load_img = (ImageView) findViewById(R.id.load_img);
        load_img.setImageResource(R.drawable.kc_loading);
        animationDrawable = (AnimationDrawable) load_img.getDrawable();
        // animationDrawable.start();
        load_text = (TextView) findViewById(R.id.load_text);
        load_layout = (LinearLayout) findViewById(R.id.load_layout);
        load_error_ayout = (LinearLayout) findViewById(R.id.load_error_ayout);
        load_text.setText("正在加载");
        load_error_ayout.setOnClickListener(loadErrorListener);
        boolean loadsImagesAutomatically = true;
        boolean javaScriptEnabled = true;
        boolean javaScriptCanOpenWindowsAutomatically = false;
        boolean rememberPasswords = true;
        boolean saveFormData = true;
        boolean loadsPageInOverviewMode = true;
        boolean useWideViewPort = true;
        boolean lightTouch = false;
        boolean navDump = false;
        int minimumFontSize = 8;
        int minimumLogicalFontSize = 8;
        int defaultFixedFontSize = 13;

        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setDomStorageEnabled(true);
        // mWebView.getSettings().setUseWideViewPort(true);
        // mWebView.getSettings().setLoadWithOverviewMode(true);
        // mWebView.getSettings().setSupportMultipleWindows(true);
//		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUserAgentString(null);
        webSettings.setUseWideViewPort(useWideViewPort);
        webSettings.setLoadsImagesAutomatically(loadsImagesAutomatically);
        webSettings.setJavaScriptEnabled(javaScriptEnabled);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
        webSettings.setMinimumFontSize(minimumFontSize);
        webSettings.setMinimumLogicalFontSize(minimumLogicalFontSize);
        webSettings.setDefaultFontSize(defaultFontSize);
        webSettings.setDefaultFixedFontSize(defaultFixedFontSize);
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setLightTouchEnabled(lightTouch);
        webSettings.setSaveFormData(saveFormData);
        webSettings.setSavePassword(rememberPasswords);
        webSettings.setLoadWithOverviewMode(loadsPageInOverviewMode);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAppCacheMaxSize(Integer.MAX_VALUE);
        webSettings.setAppCachePath(FileUtils.getSaveFilePath() + WEB_CACHE_FILE);
        int netType = VsNetWorkTools.getSelfNetworkType(this);
        CustomLog.i(TAG, "netType = " + netType);
        switch (netType) {
            case VsNetWorkTools.WIFI_NETWORK:
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                break;
            case VsNetWorkTools.NO_NETWORK:
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                break;
            case VsNetWorkTools.G3_NETWORK:
            case VsNetWorkTools.EDGE_NETWORK:
            case VsNetWorkTools.G4_NETWORK:
            default:
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                break;
        }

        // mWebView.
        handleBusiness();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        
        super.onResume();
        CustomLog.i("GDK", "smsCallBack=" + smsCallBack);
        if (smsCallBack != null) {
            mWebView.loadUrl(smsCallBack + "&sendsucc=" + SendNoteObserver.isSendSuc);
            smsCallBack = null;
        }
        SendNoteObserver.isSendSuc = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        
    }

    public void onStop() {
        super.onStop();
    }

    private View.OnClickListener loadErrorListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            // TODO Auto-generated method stub
            if (curUrl.startsWith(DfineAction.scheme_head + "finish")) {
                mWebView.stopLoading();
                finish();
                return;
            } else if (URLDecoder.decode(curUrl).indexOf("historycontact") != -1) {
                try {
                    curUrl = URLDecoder.decode(curUrl);
                    JSONObject json = new JSONObject(curUrl.replace(DfineAction.scheme_head + "business?param=", ""));
                    WebViewcallBack(json.getString("callback"), json.getString("callbacktype"),
                            VsPhoneCallHistory.loadHistoryContact(mContext, VsJsonTool.GetIntegerFromJSON(json, "num"),
                                    false), mWebView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else if (curUrl.startsWith(DfineAction.scheme_head)) {
                VsUtil.skipForTarget(curUrl, mContext, 0, null);
                mWebView.stopLoading();
                return;
            }
            mWebView.loadUrl(curUrl);
            loading();
        }
    };

    /**
     * 处理业务
     */
    private void handleBusiness() {
        CustomLog.i(TAG, "Entering WeiboShareWebViewActivity.handleBusiness()...");

        StringBuilder builder = null;
        String title = business[0].equals("") ? webViewActivity.getString(R.string.app_name) : business[0];
        String name = business[1];
        String sinaTxt = business[2];
        String kcId = VsUserConfig.getDataString(webViewActivity, VsUserConfig.JKey_KcId);
        // if (name.equals(WeiboConstParam.SINA_NAME)) {
        // mCurrentTabView.setText(this.getResources().getString(R.string.weibo_sina_share));
        // builder = new StringBuilder("http://v.t.sina.com.cn/share/share.php?");
        // builder.append("url=" + WeiboShareActivity.content_url);
        // if (kcId != null && !"".equals(kcId)) {
        // builder.append("/b" + kcId);
        // }
        // builder.append("&title=");
        // builder.append(encodeURL(sinaTxt));
        // } else

        if (name.equals("recharge")) {
            mCurrentTabView.setText(title);
            builder = new StringBuilder(business[2]);
        } else if (name.equals("aplpay")) {
            setTitleGone();
            builder = new StringBuilder(business[2]);
            web_nextback_layout.setVisibility(View.GONE);
        } else {
            if (name.equals("store")) {
                showRightNavaBtn(R.drawable.webview_finish);
            }
            mCurrentTabView.setText(title);
            builder = new StringBuilder(business[2]);
            web_nextback_layout.setVisibility(View.GONE);
            CustomLog.i(TAG, "纯URL==" + builder.toString());
        }

        String wap_uri = builder.toString();
        CustomLog.v(TAG, "wap_uri:" + wap_uri);
        // showProgressDialog(MSG_LOADING_SPECIAL_PAGE);
        try {
            if (wap_uri.contains("html_content?id=")) {
                String html = VsHttpTools.getInstance(mContext).getHtml(wap_uri);
                CustomLog.i(TAG, "进入到html网页====" + html);
                mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            } else {
                mWebView.loadUrl(wap_uri);
            }
            // mWebView.postUrl(wap_uri, EncodingUtils.getBytes(postDate, "BASE64"));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                // 实现下载的代码
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                CustomLog.v(TAG, "webview_onPageFinished,URL:" + url + ",newProgress" + mWebView.getProgress());
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                } // 加载结束
                times++;
                if (times > 2) {
                    mWebView.setVisibility(View.VISIBLE);
                    load_layout.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                CustomLog.i(TAG, "shouldOverrideUrlLoading方法被执行了" + "参数：url=" + url);
                if (url.startsWith("tel:")) {
                    mWebView.stopLoading();

                    Intent intent = new Intent(webViewActivity, CustomDialogActivity.class);
                    intent.putExtra("messagetitle", "温馨提示");
                    intent.putExtra("messagebody", "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打");
                    intent.putExtra("business", "webtel");
                    intent.putExtra("telphone", url.replace("tel:", ""));
                    intent.putExtra("messagebuttontext", DfineAction.RES.getString(R.string.product) + "拨打");
                    intent.putExtra("negativeButtontext", "手机拨打");
                    startActivity(intent);

                    return true;
                }
                String urlString = URLDecoder.decode(url);
                if (urlString.startsWith(DfineAction.scheme_head + "finish")) {
                    mWebView.stopLoading();
                    finish();
                    return true;
                } else if (urlString.indexOf("historycontact") != -1) {
                    try {
                        JSONObject json = new JSONObject(urlString.replace(DfineAction.scheme_head + "business?param=",
                                ""));
                        WebViewcallBack(
                                json.getString("callback"),
                                json.getString("callbacktype"),
                                VsPhoneCallHistory.loadHistoryContact(mContext,
                                        VsJsonTool.GetIntegerFromJSON(json, "num"), false), view);
                        // url=json.getString("callback")+("&phonelist=" +
                        // KcPhoneCallHistory.loadHistoryContact(mContext,
                        // KcJsonTool.GetIntegerFromJSON(json, "num")));
                        // view.postUrl(json.getString("callback"), ("phonelist=" + phoneList).getBytes());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return true;
                } else if (urlString.indexOf("sms") != -1 && urlString.indexOf("share") != -1) {
                    noteObserver = new SendNoteObserver(mBaseHandler, mContext);
                    mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true,
                            noteObserver);
                    try {
                        JSONObject json = new JSONObject(urlString.replace(DfineAction.scheme_head + "business?param=",
                                ""));
                        smsCallBack = json.getString("callback");
                        VsUtil.smsShare(mContext, json.getString("share_text"), json.getString("share_imageurl"));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return true;
                } else if (urlString.startsWith(DfineAction.scheme_head)) {
                    VsUtil.skipForTarget(urlString, mContext, 0, null);
                    mWebView.stopLoading();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                CustomLog.i(TAG, "onPageStarted方法被执行了" + "参数：url=" + url + ",Bitmap=" + favicon + "errorUrl="
                        + errorUrl);
                if (errorUrl.equals(url)) {
                    return;
                }
                curUrl = url;
                loading();
                mTimer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        Message m = new Message();
                        m.what = MSG_PAGE_TIMEOUT;
                        mBaseHandler.sendMessage(m);

                        mTimer.cancel();
                        mTimer.purge();
                    }
                };
                mTimer.schedule(tt, TIME_OUT);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // TODO Auto-generated method stub
                CustomLog.i(TAG, "onReceivedError方法被执行了" + "参数：errorCode=" + errorCode + "参数：description="
                        + description + "参数：failingUrl=" + failingUrl);
                loadError();
                errorUrl = failingUrl;
                mWebView.clearView();
                return;
                // super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // mBaseHandler.sendEmptyMessageDelayed(MSG_LOADFINISH, 1000);
                    if (!isLoadError) {
                        mWebView.setVisibility(View.VISIBLE);
                        load_layout.setVisibility(View.GONE);
                    }

                }
                CustomLog.i(TAG, "onProgressChanged方法被执行了,newProgress=" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (business[1].equals("store")) {
                    if (title.length() > 8) {
                        mCurrentTabView.setText(title.substring(0, 8) + "...");
                    } else {
                        mCurrentTabView.setText(title);
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                if (mWebView.canGoForward()) {
                    // showProgressDialog(MSG_LOADING_SPECIAL_GO);
                    loading();
                    mWebView.goForward();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                if (mWebView.canGoBack()) {
                    // showProgressDialog(MSG_LOADING_SPECIAL_BACK);
                    loading();
                    mWebView.goBack();
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                // showProgressDialog(MSG_LOADING_SPECIAL_REFSH);
                loading();
                mWebView.reload();
            }
        });
        mWebView.addJavascriptInterface(new KcWebView(), "KcWebView");
        mBaseHandler.sendEmptyMessageDelayed(MSG_SHOW_ANIMATION, 500);

    }

    private final char MSG_SHOW_ANIMATION = 400;

    @Override
    protected void HandleRightNavBtn() {
        // TODO Auto-generated method stub
        super.HandleRightNavBtn();
        finish();
    }

    @Override
    protected void HandleLeftNavBtn() {
        // TODO Auto-generated method stub
        if (business[1].equals("store")) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 加载中
     */
    public void loading() {
        errorUrl = "";
        isLoadError = false;
        mWebView.setVisibility(View.GONE);
        load_layout.setVisibility(View.VISIBLE);
        load_error_ayout.setVisibility(View.GONE);
    }

    /**
     * 加载失败
     */
    public void loadError() {
        isLoadError = true;
        mWebView.setVisibility(View.GONE);
        load_layout.setVisibility(View.GONE);
        load_error_ayout.setVisibility(View.VISIBLE);
    }

    // 定义被绑定的java对象
    class KcWebView {
        public void finish() {
            // TO-DO
            webViewActivity.finish();
        }
    }

    private final char MSG_PAGE_TIMEOUT = 101;

    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_PAGE_TIMEOUT:
                if (mWebView != null && mWebView.getProgress() < 100) {
                    loadError();
                }
                break;
            case MSG_SHOW_ANIMATION:
                animationDrawable.start();
                break;
            default:
                break;
        }
    }

    // @Override
    // public void onConfigurationChanged(Configuration newConfig) {
    // // TODO Auto-generated method stub
    // super.onConfigurationChanged(newConfig);
    // if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    // setTitleGone();
    // web_nextback_layout.setVisibility(View.GONE);
    // } else {
    // setTitleVisible();
    // web_nextback_layout.setVisibility(View.VISIBLE);
    // }
    // }

	/*
     * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if(keyCode == KeyEvent.KEYCODE_BACK){
	 * if(mWebView.canGoBack()){ mWebView.goBack(); }else{ finish(); } return false; } return super.onKeyDown(keyCode,
	 * event); }
	 */

    // /**
    // * 加载进度框
    // */
    // private void showProgressDialog(int what) {
    // CustomLog.i(TAG, "Entering WeiboShareWebViewActivity.showProgressDialog(int what)...");
    //
    // if (mProgressDialog != null) {
    // mProgressDialog.dismiss();
    // }
    //
    // mProgressDialog = new ProgressDialog(WeiboShareWebViewActivity.this);
    // mProgressDialog.setIndeterminate(true);
    // mProgressDialog.setCancelable(true);
    //
    // switch (what) {
    // // 正在加载页面
    // case MSG_LOADING_SPECIAL_PAGE:
    // mProgressDialog.setMessage(this.getResources().getString(R.string.weibo_loading));
    // break;
    // // 刷新
    // case MSG_LOADING_SPECIAL_REFSH:
    // mProgressDialog.setMessage(this.getResources().getString(R.string.weibo_refreshing));
    // break;
    // // 前进
    // case MSG_LOADING_SPECIAL_GO:
    // mProgressDialog.setMessage(this.getResources().getString(R.string.weibo_advancing));
    // break;
    // // 后退
    // case MSG_LOADING_SPECIAL_BACK:
    // mProgressDialog.setMessage(this.getResources().getString(R.string.weibo_retreating));
    // break;
    // default:
    // break;
    // }
    // CustomLog.i(TAG, "test progress dialog:" + mProgressDialog);
    // if (!mActivityState.equals("invalid"))
    // mProgressDialog.show();
    // }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomLog.i(TAG, "ondestory");
        // setConfigCallback(null);
        try {
            if (noteObserver != null) {
                getContentResolver().unregisterContentObserver(noteObserver);
            }
            if (mWebView != null) {//
                mWebView.setVisibility(View.GONE);
                mWebView.getSettings().setBuiltInZoomControls(true);
                mWebView.setVisibility(View.GONE);//
                // long timeout = ViewConfiguration.getZoomControlsTimeout();// 扩大 缩小 小控件的消失的时间
                // new Timer().schedule(new TimerTask() {
                // @Override
                // public void run() {
                // // TODO Auto-generated method stub
                // if (mWebView != null) {
                // // mWebView.destroy();
                // }
                // }
                // }, 500);// timeout 也可以自己定义短点时间
                mWebView.setVisibility(View.GONE);
                mWebView.freeMemory();
                mWebView.clearSslPreferences();
                mWebView.clearView();
                mWebView.clearFormData();
                mWebView.clearHistory();
                mWebView.clearCache(true);
                mWebView.clearMatches();
                if (GlobalVariables.SDK_VERSON > 11) {
                    webViewActivity.deleteDatabase("webview.db");
                    webViewActivity.deleteDatabase("webviewCache.db");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mActivityState = "invalid";
    }

    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);

            if (null == configCallback) {
                return;
            }

            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {
        }
    }

    public String encodeURL(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            loading();
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            if (data == null)
                return;

            ArrayList<VsContactItem> items = data.getParcelableArrayListExtra("SELECTCONTACTLIST");
            String callback = data.getStringExtra("callback");
            String callbackType = data.getStringExtra("callbacktype");
            JSONArray jsonArray = new JSONArray();
            try {
                if (items != null && items.size() > 0) {
                    for (VsContactItem kcContactItem : items) {
                        JSONObject json = new JSONObject();
                        json.put("name", kcContactItem.mContactName);
                        json.put("number", kcContactItem.mContactPhoneNumber);
                        jsonArray.put(json);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            WebViewcallBack(callback, callbackType, jsonArray.toString(), mWebView);
        } else if (resultCode == 1) {
            WebViewcallBack(data.getStringExtra("callback"), data.getStringExtra("callbacktype"), null, mWebView);
        }
    }

    public void WebViewcallBack(String callback, String callbackType, String params, WebView view) {
        CustomLog.i("GDK", "webview 启动activity返回:" + "phonelist=" + params + ",callback=" + callback);
        if (callbackType.equals("1")) {
            if (params == null) {
                view.loadUrl(callback);
            } else {
                view.postUrl(callback, ("phonelist=" + params).getBytes());
                CustomLog.i("GDK", "" + ("phonelist=" + params));
            }
        } else {
            VsUtil.callBack(webViewActivity, callbackType, callback, "phonelist=" + params.toString());
        }
    }

}
