package com.weiwei.base.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwtx.dududh.BuildConfig;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.hwtx.dududh.R;

/**
 * 商城
 */
public class VsMallFragment extends VsBaseFragment implements OnClickListener {
    private View mParent;
    private static final String TAG = "WeiViewActivity";

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
    // private String business[];
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
    private TextView tv_title;

    public static VsMallFragment newInstance(int index) {
        VsMallFragment f = new VsMallFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mall, container, false);
        return view;
    }

    protected void HandleRightNavBtn() {
    }

    @SuppressLint("ResourceType")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParent = getView();

        // 初始化title

        // initTitleNavBar(mParent);
        //showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        //  mTitleTextView.setText(R.string.mall);
        // business = getActivity().getIntent().getStringArrayExtra("AboutBusiness");

        // 设置标题
        mCurrentTabView = (TextView) getActivity().findViewById(R.id.sys_title_txt);
        mWebView = (WebView) mParent.findViewById(R.id.webview);
        mWebView.setVisibility(View.GONE);
        next = (ImageView) mParent.findViewById(R.id.iamgeView1);
        back = (ImageView) getActivity().findViewById(R.id.tv_left_contact);
        refresh = (ImageView) getActivity().findViewById(R.id.tv_right_contact);
        web_nextback_layout = (LinearLayout) mParent.findViewById(R.id.web_nextback_layout);
        load_img = (ImageView) mParent.findViewById(R.id.load_img);
        load_img.setImageResource(R.drawable.kc_loading);
        tv_title = (TextView) mParent.findViewById(R.id.tv_title);
        tv_title.setText(R.string.tab_found);
        animationDrawable = (AnimationDrawable) load_img.getDrawable();
        // animationDrawable.start();
        load_text = (TextView) mParent.findViewById(R.id.load_text);
        load_layout = (LinearLayout) mParent.findViewById(R.id.load_layout);
        load_error_ayout = (LinearLayout) mParent.findViewById(R.id.load_error_ayout);
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
        int defaultFontSize = 16;
        int defaultFixedFontSize = 13;

        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setDomStorageEnabled(true);
        // mWebView.getSettings().setUseWideViewPort(true);
        // mWebView.getSettings().setLoadWithOverviewMode(true);
        // mWebView.getSettings().setSupportMultipleWindows(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
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
        // webSettings.setAppCacheEnabled(true);
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // mWebView.

        if (mWebView.canGoBack()) {
            back.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.GONE);
        }
        handleBusiness();

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {

        }
    }

    Timer time;

    @Override
    public void onResume() {
        super.onResume();
    }


    private View.OnClickListener loadErrorListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            /*  // TODO Auto-generated method stub
              if (curUrl.startsWith(DfineAction.scheme_head + "finish")) {
                  mWebView.stopLoading();

                  return;
              }
              else if (URLDecoder.decode(curUrl).indexOf("historycontact") != -1) {
                   try {
                       curUrl = URLDecoder.decode(curUrl);
                       JSONObject json = new JSONObject(curUrl.replace(DfineAction.scheme_head + "business?param=", ""));
                       WebViewcallBack(json.getString("callback"), json.getString("callbacktype"),
                               VsPhoneCallHistory.loadHistoryContact(mContext, VsJsonTool.GetIntegerFromJSON(json, "num"),
                                       false), mWebView);
                   } catch (Exception e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                  return;
              }
              else if (curUrl.startsWith(DfineAction.scheme_head)) {
                  VsUtil.skipForTarget(curUrl, mContext, 0, null);
                  mWebView.stopLoading();
                  return;
              }*/
            mWebView.loadUrl(curUrl);
            loading();
        }
    };

    /**
     * 处理业务
     */
    private void handleBusiness() {
        String bid = DfineAction.RES.getString(R.string.brandid);
        String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        curUrl = "http://adopenapi.weiwei.com/agent_ad_html.php?uid=" + uid + "&bid=" + bid + "&cid=" + "&appversion=" + BuildConfig.VERSION_NAME + "&screenw=480&screenh=854&sp=" + DfineAction.pv;
        try {

            mWebView.loadUrl(curUrl);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 实现下载的代码
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (mWebView.canGoBack()) {
                    back.setVisibility(View.VISIBLE);
                } else {
                    back.setVisibility(View.GONE);
                }
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

                if (url.startsWith("tel:")) {
                    mWebView.stopLoading();

                    if (getActivity() == null) {
                        return false;
                    }
                    Intent intent = new Intent(getActivity(), CustomDialogActivity.class);
                    intent.putExtra("messagetitle", "温馨提示");
                    intent.putExtra("messagebody", "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打");
                    intent.putExtra("business", "webtel");
                    intent.putExtra("telphone", url.replace("tel:", ""));
                    intent.putExtra("messagebuttontext", DfineAction.RES.getString(R.string.product) + "拨打");
                    intent.putExtra("negativeButtontext", "手机拨打");
                    startActivity(intent);
                    return true;
                } else if (url.startsWith(DfineAction.image_head)) {
                    try {
                        VsUtil.showInView(url, mContext, 0, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.endsWith(".apk")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }


                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub

                CustomLog.i(TAG, "onPageStarted方法被执行了" + "参数：url=" + url + ",Bitmap=" + favicon + "errorUrl=" + errorUrl);
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
                CustomLog.i(TAG, "onReceivedError方法被执行了" + "参数：errorCode=" + errorCode + "参数：description=" + description + "参数：failingUrl=" + failingUrl);
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
                /* if (business[1].equals("store")) {
                     if (title.length() > 8) {
                         mCurrentTabView.setText(title.substring(0, 8) + "...");
                     } else {
                         mCurrentTabView.setText(title);
                     }
                 }*/
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
        // mWebView.addJavascriptInterface(new KcWebView(), "KcWebView");
        mBaseHandler.sendEmptyMessageDelayed(MSG_SHOW_ANIMATION, 500);

    }

    private final char MSG_SHOW_ANIMATION = 400;

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

    /*  // 定义被绑定的java对象
      class KcWebView {
          public void finish() {
              // TO-DO
              webViewActivity.finish();
          }
      }
    */
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDetach() {
        super.onDestroy();
        CustomLog.i(TAG, "ondestory");
        // setConfigCallback(null);
        try {

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
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().deleteDatabase("webview.db");
                    getActivity().deleteDatabase("webviewCache.db");
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

}
