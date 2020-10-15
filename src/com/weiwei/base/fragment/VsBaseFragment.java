package com.weiwei.base.fragment;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.activity.me.KcMyQcodeActivity;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.base.activity.me.VsMyInfoTextActivity;
import com.weiwei.base.activity.setting.VsSetingActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsAdConfigItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.base.widgets.CustomToast;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.zbar.lib.CaptureActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:Android客户端
 * @Description: fragment基类
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsBaseFragment extends Fragment {
	private final String TAG = "VsBaseFragment";

	protected Activity mContext = null;
	/**
	 * 内核服务
	 */
	protected VsCoreService vsCoreService;
	/**
	 * 共用的handle
	 */
	protected Handler mBaseHandler;
	/**
	 * 广播接收器
	 */
	protected KcBroadcastReceiver vsBroadcastReceiver;
	/**
	 * 共用的mToast
	 */
	protected CustomToast mToast;
	/**
	 * 标题栏和Tips提示
	 */
	protected TextView mTitleTextView;
	protected ImageView mRightLine;

	protected ImageView mLeftLine;
	protected LinearLayout mBtnNavLeft, mBtnNavRight;
	protected TextView mBtnNavLeftTv, mBtnNavRightTv;
	protected RelativeLayout small_title;
	private static final char MSG_CloseTips = 1001;
	/**
	 * 弹出框
	 */
	protected ProgressDialog mProgressDialog;
	/**
	 * 广告
	 */
	private ScheduledExecutorService scheduledExecutorService;
	private ViewPager viewPager;
	private int currentItem = 0; // 当前图片的索引号
	private ArrayList<ImageView> imageViews = new ArrayList<ImageView>(); // 滑动的图片合集
	private ArrayList<VsAdConfigItem> adlist = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	protected DisplayImageOptions options;
	private LinearLayout vs_viewGroupimg;
	private ImageView[] tipImgs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mBaseHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				handleBaseMessage(msg);
				return false;
			}
		});
		mToast = new CustomToast(mContext);

		/**
		 * 按home键退出软件。在进入时候我们可以做的操作
		 */
		if (savedInstanceState != null && savedInstanceState.getBoolean("HomeExit")) {
			if (VsPhoneCallHistory.CONTACTLIST.size() == 0 || VsPhoneCallHistory.callLogs.size() == 0) {
				VsPhoneCallHistory.loadCallLog();
				VsPhoneCallHistory.loadContacts();
			}
		}
	}

	/**
	 * 广告
	 */

	/**
	 * 初始化广告位
	 * 
	 * @param ad_conf_str
	 *            广告位信息
	 * @param ui_id
	 *            广告位界面ID
	 * @param isclose
	 *            广告位关闭图标是否显示
	 */
	protected void initSlide(View view, final String ad_conf_str, final String ui_id, final boolean isclose) {
		options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.vs_fail_icon).cacheInMemory(true)
				.cacheOnDisc(true) // 开启硬盘缓存
				.build();
		// TODO 初始化广告视图
		viewPager = (ViewPager) view.findViewById(R.id.image_slide_page);
		vs_viewGroupimg = (LinearLayout) view.findViewById(R.id.vs_viewGroupimg);
		Time time = new Time("GMT+8");
		time.setToNow();
		if (VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CLOSE_TIME + "_" + ui_id)
				.equals(time.year + "-" + time.month + "-" + time.monthDay)) {
			view.findViewById(R.id.title).setVisibility(View.VISIBLE);
		} else {
			if (isclose) {
				final ImageView close_iv = (ImageView) view.findViewById(R.id.ad_close_iv);
				close_iv.setVisibility(View.GONE);
				close_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Time time = new Time("GMT+8");
						time.setToNow();
						VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CLOSE_TIME + "_" + ui_id,
								time.year + "-" + time.month + "-" + time.monthDay);
					}
				});
			}
		}

		// 取广告数据
		try {
			JSONArray ad_conf_json = new JSONArray(ad_conf_str);
			int i = 0;
			int len = ad_conf_json.length();
			JSONObject jObj = null;
			adlist = new ArrayList<VsAdConfigItem>(len);
			while (i < len && (jObj = (JSONObject) ad_conf_json.get(i)) != null) {
				VsAdConfigItem item = new VsAdConfigItem();
				item.setAdpid(jObj.getString("remark"));
				item.setAdid(jObj.getInt("ad_id"));
				item.setAdid(jObj.getInt("a.id"));
				item.setAdpid(jObj.getString("ad_place_id"));
				item.setName(jObj.getString("title"));
				item.setImg(jObj.getString("image_url"));
				item.setAdtype(jObj.getString("redirect_type"));
				item.setUrl(jObj.getString("target_url"));
				item.setRedirect_target(jObj.getString("redirect_target")); // 如果是页面跳转就是编号，网页就是url地址
				// item.setSortid(jObj.getString("redirect_type"));
				i++;
				adlist.add(item);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			return;
		}
		// imageViews = new ArrayList<ImageView>(adlist.size());
		imageViews.clear();
		for (int i = 0; i < adlist.size(); i++) {
			ImageView imageView = new ImageView(mContext);

			final String imgurl = adlist.get(i).getImg();
			if (imgurl != null && !imgurl.equals("")) {
				imageLoader.displayImage(imgurl, imageView, options, animateFirstListener);
			}
			imageView.setOnClickListener(new ImageOnClickListener());
			imageView.setScaleType(ScaleType.FIT_XY);
			imageViews.add(imageView);
		}
		viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		tipImgs = new ImageView[adlist.size()];
		vs_viewGroupimg.removeAllViews();
		for (int i = 0; i < tipImgs.length; i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tipImgs[i] = imageView;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lp.leftMargin = 10;
			lp.rightMargin = 10;
			vs_viewGroupimg.addView(imageView, lp);
		}

	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tipImgs.length; i++) {
			if (i == selectItems) {
				tipImgs[i].setBackgroundResource(R.drawable.drawable_guide_call_selected);
			} else {
				tipImgs[i].setBackgroundResource(R.drawable.drawable_guide_call_normal);
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			setImageBackground(currentItem);
		}

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	// 切换当前显示的图片
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当Activity不可见的时候停止切换
		if(scheduledExecutorService != null){
			scheduledExecutorService.shutdown();
		}
		super.onStop();
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}

	}

	/* 图片监听 */

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	/**
	 * 滑动页面点击事件监听器
	 * 
	 * @author dell
	 */
	private class ImageOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (adlist != null) {
				VsAdConfigItem item = adlist.get(currentItem);
				if (item.getAdtype().equals("wap")) {
					Intent intent = new Intent();

					String[] aboutBusiness = new String[] { item.getName(), "", item.getRedirect_target() };
					intent.putExtra("AboutBusiness", aboutBusiness);
					intent.setClass(mContext, WeiboShareWebViewActivity.class);
					startActivity(intent);
				} else if (item.getAdtype().equals("in")) {
					String url = item.getUrl();
					CustomLog.i("inline", "url==" + url);

					try {
						JSONObject json = null;

						url = URLDecoder.decode(url);
						url = url.replace(DfineAction.scheme_head, "");// DfineAction.image_head
						CustomLog.i("inline", "url  url==" + url);
						if (url.startsWith("inline")) {// 本应用界面跳转。
							json = new JSONObject(url.replace("inline?param=", ""));
							CustomLog.i("inline", "inline==" + json.getString("page"));
							VsUtil.startActivity(json.getString("page"), mContext, item);
						}
						CustomLog.i("", "");
					} catch (Exception e) {
						CustomLog.i("", "");
					}

				} else if (item.getAdtype().equals("web")) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(item.getRedirect_target());
					intent.setData(content_url);
					startActivity(intent);
				} else if (item.getAdtype().equals("page")) { // 内部页面
					String redirect_target = item.getRedirect_target();
					VsMainActivity activity = (VsMainActivity) getActivity();
					if (redirect_target.equals("10001")) { // TODO 拨号页面
						// 切换到拨号界面
						activity.setFragmentIndicator(FragmentIndicator.HOME_PAGE_PHONE);
					} else if (redirect_target.equals("20001")) {// 联系人页
						// 切换到联系人页
						activity.setFragmentIndicator(FragmentIndicator.HOME_PAGE_CONTACTS);
					} else if (redirect_target.equals("30001")) {// 30001 发现页
						// 切换到发现页
						activity.setFragmentIndicator(FragmentIndicator.HOME_PAGE_MAIN);
					} else if (redirect_target.equals("40001")) {// 我
						// 切换到我的界面
						activity.setFragmentIndicator(FragmentIndicator.HOME_PAGE_MINE);
					} else if (redirect_target.equals("30002")) {// 30002 赚钱计划

					} else if (redirect_target.equals("30003")) { // 分享页面

					} else if (redirect_target.equals("30004")) {// TODO 扫一扫
						if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
							Intent intent_saoma = new Intent(mContext, CaptureActivity.class);
							intent_saoma.putExtra("code", "2");
							startActivity(intent_saoma);
						}
					} else if (redirect_target.equals("40002")) {// TODO 个人信息
						startActivity(new Intent(mContext, VsMyInfoTextActivity.class));
					} else if (redirect_target.equals("40003")) {// 我的余额

					} else if (redirect_target.equals("40004")) {// 我的钱包

					} else if (redirect_target.equals("40006")) {// TODO 我的二维码
						if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
							Intent intent_qrcode = new Intent(mContext, KcMyQcodeActivity.class);
							startActivity(intent_qrcode);
						}
					} else if (redirect_target.equals("40005")) {// 我的红包

					} else if (redirect_target.equals("40007")) {// TODO 扫码绑定
						if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
							Intent intent_saoma = new Intent(mContext, CaptureActivity.class);
							intent_saoma.putExtra("code", "1");
							startActivity(intent_saoma);
						}
					} else if (redirect_target.equals("40008")) {// TODO 设置
						Intent intent = new Intent(mContext, VsSetingActivity.class);
						startActivity(intent);
					} else if (redirect_target.equals("40009")) {// 拨打方式

					} else if (redirect_target.equals("40010")) {// TODO 关于我们
						startActivity(mContext, KcQcodeActivity.class);
					}
				}
			}
		}
	}

	/**
	 * 申明一个广播
	 */
	protected void initBroadcastReceiver(String action) {
		unregisterKcBroadcast();
		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		mContext.registerReceiver(vsBroadcastReceiver, filter);

	}

	public void initTipsLayout(String tipsMsg) {
		if (small_title == null) {
			return;
		}
		if (TextUtils.isEmpty(tipsMsg)) {
			return;
		}
		small_title.setVisibility(View.GONE);
		mBaseHandler.sendEmptyMessageDelayed(MSG_CloseTips, 3000);
	}

	protected void initTitleNavBar(View view) {
		mTitleTextView = (TextView) view.findViewById(R.id.sys_title_txt);
		mBtnNavLeft = (LinearLayout) view.findViewById(R.id.btn_nav_left);
		mBtnNavLeftTv = (TextView) view.findViewById(R.id.btn_nav_left_tv);
		mBtnNavRightTv = (TextView) view.findViewById(R.id.btn_nav_right_tv);
		mBtnNavRight = (LinearLayout) view.findViewById(R.id.btn_nav_right);
		mLeftLine = (ImageView) view.findViewById(R.id.title_line_left);
		mRightLine = (ImageView) view.findViewById(R.id.title_line_right);
		small_title = (RelativeLayout) view.findViewById(R.id.small_title);
	}

	public class VsBaseHandler extends Handler {
		public void handleMessage(Message msg) {
			handleBaseMessage(msg);
		}
	}

	public VsApplication getKcApplication() {
		return (VsApplication) mContext.getApplicationContext();
	}

	/**
	 * 处理Handler消息
	 * 
	 * @param msg
	 */
	protected void handleBaseMessage(Message msg) {
		switch (msg.what) {
		case MSG_CloseTips:
			try {
				small_title.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	protected void setTitleGone() {
		small_title.setVisibility(View.GONE);
	}

	/**
	 * 隐藏右边导航
	 */
	protected void hideRightNavaBtn() {
		mBtnNavRightTv.setVisibility(View.INVISIBLE);
		mBtnNavRightTv.setEnabled(false);
	}

	/**
	 * 隐藏左边导航
	 */
	protected void hideLeftNavaBtn() {
		mBtnNavLeftTv.setVisibility(View.INVISIBLE);
		mBtnNavLeftTv.setEnabled(false);
	}

	/**
	 * 显示左边导航
	 */
	protected void showLeftNavaBtn(int id) {
		mBtnNavLeftTv.setVisibility(View.VISIBLE);
		Drawable drawable = getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mBtnNavLeftTv.setCompoundDrawables(drawable, null, null, null);
		mLeftLine.setVisibility(View.VISIBLE);
		mBtnNavLeft.setOnClickListener(leftNavBtnListener);
	}

	/**
	 * 显示左边文字
	 */
	protected void showLeftTxtBtn(String txt) {
		mBtnNavLeftTv.setVisibility(View.VISIBLE);
		mBtnNavLeftTv.setText(txt);
		mLeftLine.setVisibility(View.VISIBLE);
		mBtnNavLeft.setOnClickListener(leftNavBtnListener);
	}

	/**
	 * 显示右边导航
	 */
	protected void showRightNavaBtn(int id) {
		mBtnNavRightTv.setVisibility(View.VISIBLE);
		Drawable drawable = getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mBtnNavRightTv.setCompoundDrawables(drawable, null, null, null);
		mRightLine.setVisibility(View.VISIBLE);
		mBtnNavRight.setOnClickListener(rightNavBtnListener);
	}

	/**
	 * 显示右边文字
	 */
	protected void showRightTxtBtn(String txt) {
		mBtnNavRightTv.setVisibility(View.VISIBLE);
		mBtnNavRightTv.setText(txt);
		mRightLine.setVisibility(View.VISIBLE);
		mBtnNavRight.setOnClickListener(rightNavBtnListener);
		// mBtnNavRightTv.setTextColor(getResources().getColor(R.color.vs_gree));
	}

	protected void HandleLeftNavBtn() {
		// TODO 处理左导航按钮事件
		if (getActivity() != null)
			getActivity().finish();// 默认为后退
	}

	protected void HandleRightNavBtn() {
		// TODO 处理右导航按钮事件
	}

	private View.OnClickListener leftNavBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			HandleLeftNavBtn();
		}
	};

	private View.OnClickListener rightNavBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			HandleRightNavBtn();
		}
	};

	/**
	 * 加载等待界面
	 * 
	 * @param message
	 */
	protected void loadProgressDialog(String message) {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	protected void loadProgressDialog(String message, boolean Cancelable) {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(Cancelable);// 设置是否可以取消
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	/**
	 * 销毁等待界面
	 * 
	 * @param
	 */
	protected void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();

		mProgressDialog = null;
	}

	public class VsServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName className, IBinder service) {
			CustomLog.i(TAG, "onServiceConnected,serviceBinder");
			vsCoreService = ((VsCoreService.MyBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			CustomLog.i(TAG, "onServiceDisconnected");
			vsCoreService = null;
		}
	};

	public class KcBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleKcBroadcast(context, intent);
		}
	}

	/**
	 * 处理收到的公告信息
	 * 
	 * @param context
	 * @param intent
	 */
	protected void handleKcBroadcast(Context context, Intent intent) {
		unregisterKcBroadcast();
		String msg = intent.getStringExtra(VsCoreService.VS_KeyMsg);
		CustomLog.i(TAG, msg);
	}

	/**
	 * 注销广播接收器
	 */
	protected void unregisterKcBroadcast() {
		if (vsBroadcastReceiver != null) {
			mContext.unregisterReceiver(vsBroadcastReceiver);
			vsBroadcastReceiver = null;
		}
	}

	/**
	 * 设置文本大小
	 * 
	 * @param et
	 */
	protected void setEditTextTextSize(final EditText et) {
		if (et.getText().length() == 0)
			et.setTextSize(16);
		else
			et.setTextSize(20);

		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0)
					et.setTextSize(16);
				else
					et.setTextSize(20);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterKcBroadcast();
	}

	// /**
	// * 设置Activity全屏显示
	// *
	// * @param activity
	// * Activity引用
	// * @param isFull
	// * true为全屏，false为非全屏
	// */
	// public void setFullScreen(Activity activity, boolean isFull) {
	// Window window = activity.getWindow();
	// WindowManager.LayoutParams params = window.getAttributes();
	// if (isFull) {
	// params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
	// window.setAttributes(params);
	// window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	// } else {
	// params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	// window.setAttributes(params);
	// window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	// }
	// }

	/**
	 * 隐藏Activity的系统默认标题栏
	 * 
	 * @param activity
	 *            Activity对象
	 */
	public void hideTitleBar(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	// /**
	// * 强制设置Actiity的显示方向为垂直方向。
	// *
	// * @param activity
	// * Activity对象
	// */
	// public void setScreenVertical(Activity activity) {
	// activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	// }

	// /**
	// * 强制设置Actiity的显示方向为横向。
	// *
	// * @param activity
	// * Activity对象
	// */
	// public void setScreenHorizontal(Activity activity) {
	// activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	// }

	/**
	 * 隐藏软件输入法
	 * 
	 * @param activity
	 */
	public void hideSoftInput(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 使UI适配输入法
	 * 
	 * @param activity
	 */
	public void adjustSoftInput(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 * 跳转到某个Activity
	 * 
	 * @param activity
	 *            本Activity
	 * @param targetActivity
	 *            目标Activity的Class
	 */
	public void startActivity(Activity activity, Class<? extends Activity> targetActivity) {
		startActivity(activity, new Intent(activity, targetActivity));
	}

	/**
	 * 根据给定的Intent进行Activity跳转
	 * 
	 * @param activity
	 *            Activity对象
	 * @param intent
	 *            要传递的Intent对象
	 */
	public void startActivity(Activity activity, Intent intent) {
		activity.startActivity(intent);
	}

	/**
	 * 带参数进行Activity跳转
	 * 
	 * @param activity
	 *            Activity对象
	 * @param targetActivity
	 *            目标Activity的Class
	 * @param params
	 *            跳转所带的参数
	 */
	public void startActivity(Activity activity, Class<? extends Activity> targetActivity, Map<String, Object> params) {
		if (null != params) {
			Intent intent = new Intent(activity, targetActivity);
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				setValueToIntent(intent, entry.getKey(), entry.getValue());
			}
			startActivity(activity, intent);
		}
	}

	/**
	 * 带参数进行Activity跳转
	 * 
	 * @param activity
	 * @param target
	 * @param params
	 */
	public void startActivity(Activity activity, Class<? extends Activity> target, NameValuePair... params) {
		if (null != params) {
			Intent intent = new Intent(activity, target);
			for (NameValuePair param : params) {
				setValueToIntent(intent, param.getName(), param.getValue());
			}
			startActivity(activity, intent);
		}
	}

	// /**
	// * 显示Toast消息，并保证运行在UI线程中,如果我们用的是封装好的Toast可不用这个方法
	// *
	// * @param activity
	// * @param message
	// */
	// public void toastShow(final Activity activity, final String message) {
	// activity.runOnUiThread(new Runnable() {
	// public void run() {
	// Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	// }
	// });
	// }

	/**
	 * 将值设置到Intent里
	 * 
	 * @param intent
	 *            Inent对象
	 * @param key
	 *            Key
	 * @param val
	 *            Value
	 */
	public void setValueToIntent(Intent intent, String key, Object val) {
		if (val instanceof Boolean)
			intent.putExtra(key, (Boolean) val);
		else if (val instanceof Boolean[])
			intent.putExtra(key, (Boolean[]) val);
		else if (val instanceof String)
			intent.putExtra(key, (String) val);
		else if (val instanceof String[])
			intent.putExtra(key, (String[]) val);
		else if (val instanceof Integer)
			intent.putExtra(key, (Integer) val);
		else if (val instanceof Integer[])
			intent.putExtra(key, (Integer[]) val);
		else if (val instanceof Long)
			intent.putExtra(key, (Long) val);
		else if (val instanceof Long[])
			intent.putExtra(key, (Long[]) val);
		else if (val instanceof Double)
			intent.putExtra(key, (Double) val);
		else if (val instanceof Double[])
			intent.putExtra(key, (Double[]) val);
		else if (val instanceof Float)
			intent.putExtra(key, (Float) val);
		else if (val instanceof Float[])
			intent.putExtra(key, (Float[]) val);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

	public void onRestoreInstanceState(Bundle state) {

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getActivity() != null)
			getActivity().finish();
		return false;
	}

	/**
	 * 没有取消按钮。可以自己按钮名字的弹出框
	 * 
	 * @param titleResId
	 * @param message
	 * @param dialogId
	 * @param okBtmListener
	 * @param
	 * @param mContext
	 * @param okbutton
	 */
	public static void showMessageDialog(int titleResId, String message, int dialogId,
			DialogInterface.OnClickListener okBtmListener, Context mContext, String okbutton) {
		try {
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setTitle(mContext.getResources().getString(titleResId));
			builder.setMessage(message);
			builder.setPositiveButton(okbutton, okBtmListener);
			CustomDialog dialog = builder.create();
			dialog.setDialogId(dialogId);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showMessageDialog(String title, String message, int dialogId,
			DialogInterface.OnClickListener okBtmListener, Context mContext, String okbutton) {
		try {
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton(okbutton, okBtmListener);
			CustomDialog dialog = builder.create();
			dialog.setDialogId(dialogId);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showMessageDialog_token(String title, String message, boolean nocanbtn) {
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, true);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_RegAwardSwitch, true);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_VipValidtime, "");
				// 清空Token
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETVSUSERINFO, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN_RESULT, "");
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_CALLSERVER_FLAG, false);
			}
		});
		CustomDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * 显示消息提示框
	 * 
	 * @param titleResId
	 * @param message
	 */
	protected void showMessageDialog(int titleResId, String message) {
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle(titleResId);
		builder.setMessage(message);
		builder.setPositiveButton(this.getResources().getString(R.string.ok), null);
		builder.setNegativeButton(this.getResources().getString(R.string.cancel), null);
		CustomDialog dialog = builder.create();
		dialog.show();
	}

	protected void showYesNoDialog(String title, String messageInfo, String leftString, String rightString,
			DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener,
			DialogInterface.OnCancelListener cancelListener) {
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle(title);
		// builder.setMessage(Html.fromHtml(messageInfo) + "");
		builder.setMessage(messageInfo + "");
		builder.setPositiveButton(rightString, okBtmListener);
		builder.setNegativeButton(leftString, cancelBtnListener);
		builder.setOnCancelListener(cancelListener);
		CustomDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
