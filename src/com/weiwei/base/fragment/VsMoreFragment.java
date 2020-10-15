package com.weiwei.base.fragment;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.me.VsAboutActivity;
import com.weiwei.base.activity.me.VsBalanceActivity;
import com.weiwei.base.activity.me.VsMakeMoneyActivity;
import com.weiwei.base.activity.me.VsPersonalizedActivity;
import com.weiwei.base.activity.me.VsRechargeActivity;
import com.weiwei.base.activity.me.VsSignInFirstActivity;
import com.weiwei.base.activity.setting.VsSetingActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.MoreItem;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.hwtx.dududh.R;

public class VsMoreFragment extends VsBaseFragment implements android.view.View.OnClickListener {
	private TextView more_ad_text;
	private ImageView local_ad;
	private ImageView more_imageview_01;
	private ImageView more_imageview_02;
	private ImageView more_imageview_03;
	private ImageView more_imageview_04;
	private ImageView more_imageview_05;
	private ImageView more_imageview_06;
	private ImageView more_imageview_07;
	private ImageView more_imageview_08;

	private LinearLayout more_layout_01;
	private LinearLayout more_layout_02;
	private LinearLayout more_layout_03;
	private LinearLayout more_layout_04;
	private LinearLayout more_layout_05;
	private LinearLayout more_layout_06;
	private LinearLayout more_layout_07;
	private LinearLayout more_layout_08;

	private LinearLayout recommend_row_2;
	private View vs_more_line9;

	private TextView more_textview_01;
	private TextView more_textview_02;
	private TextView more_textview_03;
	private TextView more_textview_04;
	private TextView more_textview_05;
	private TextView more_textview_06;
	private TextView more_textview_07;
	private TextView more_textview_08;
	private String[] type_temp;
	private TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();
	private StringBuffer sb = new StringBuffer();
	private RelativeLayout vs_more_share;
	private View mParent;
	private GridView moreGridView;
	private MoreAdapter moreAdapter;
	private ArrayList<MoreItem> moreList = new ArrayList<MoreItem>();
	private static final String TAG = "VsMoreFragment";
	private String ad_conf_str = "";

	private SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {
		@Override
		public void onStart() {
			CustomLog.i(TAG, "开始分享...");
		}

		@Override
		public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {

			if (eCode == 200) {
				if (!"SINA".equals(share_media.name()) && !"SMS".equals(share_media.name())) {
					Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_LONG).show();
				}
				CustomLog.i(TAG, "分享成功");
			} else {
				String eMsg = "";
				switch (eCode) {
				case -101:
					eMsg = "没有Oauth授权";
					break;
				case -102:
					eMsg = "未知错误";
					break;
				case -103:
					eMsg = "服务器没响应";
					break;
				case -104:
					eMsg = "初始化失败";
					break;
				case -105:
					eMsg = "参数错误";
					break;
				case 40000:
					eMsg = "取消分享";
					break;
				default:
					break;
				}

				Toast.makeText(mContext, eMsg, Toast.LENGTH_SHORT).show();
			}
			CustomLog.i(TAG, "Code[" + eCode + "] " + ", share_media is " + share_media.name());
		}
	};

	private UMSocialService mController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vs_layout_more, container, false);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initShare();
			showAd();
		}
	}

	private void showAd() {
		ad_conf_str = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CONFIG_3001);
		if ("".equals(ad_conf_str)) {
			mParent.findViewById(R.id.slid_title).setVisibility(View.GONE);
			local_ad.setVisibility(View.VISIBLE);
		} else {
			local_ad.setVisibility(View.GONE);
			mParent.findViewById(R.id.slid_title).setVisibility(View.VISIBLE);
			mParent.findViewById(R.id.title).setVisibility(View.GONE);
			initSlide(mParent, ad_conf_str, "3001", false);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mContext = getActivity();
		// setdata();
		initView();
		// initShare();
	}

	private void initShare() {
		String product = DfineAction.RES.getString(R.string.product);
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), DfineAction.WEIXIN_APPID,
				DfineAction.WEIXIN_APPSECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		String url = "";
		if (shareContent().indexOf("http") > 0) {
			url = shareContent().substring(shareContent().indexOf("http"));
		}

		// 设置微信好友分享内容
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(shareContent());// product + "电话微信分享");
		weixinContent.setTitle(product);
		weixinContent.setTargetUrl(url);// DfineAction.WAPURI);
		UMImage localImage = new UMImage(getActivity(), R.drawable.icon);
		weixinContent.setShareImage(localImage);
		mController.setShareMedia(weixinContent);

		// 设置微信朋友圈分享内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(shareContent());// product + "电话分享朋友圈");
		circleMedia.setTitle(product);
		circleMedia.setShareImage(localImage);
		circleMedia.setTargetUrl(url);
		mController.setShareMedia(circleMedia);

		// 添加QQ分享
		String qqAppId = DfineAction.QqAppId;
		String qqAppKey = DfineAction.QqAppKey;
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), qqAppId, qqAppKey);
		qqSsoHandler.addToSocialSDK();
		// 设置分享内容
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(shareContent());// product + "电话分享测试qq");
		qqShareContent.setTitle(product);
		qqShareContent.setTargetUrl(url);
		qqShareContent.setShareImage(localImage);
		mController.setShareMedia(qqShareContent);

		// 添加QQ空间分享
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), qqAppId, qqAppKey);
		qZoneSsoHandler.addToSocialSDK();
		// 设置分享内容
		QZoneShareContent qZoneShareContent = new QZoneShareContent();
		qZoneShareContent.setShareContent(shareContent());// product +
															// "电话分享qqZone");
		qZoneShareContent.setTitle(product);
		qZoneShareContent.setTargetUrl(url);
		qZoneShareContent.setShareImage(localImage);
		mController.setShareMedia(qZoneShareContent);
		//
		// //添加SMSfenx分享
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		// 设置短信分享内容
		SmsShareContent smsShareContent = new SmsShareContent();
		smsShareContent.setShareContent(shareContent());// product + "电话短信分享");
		mController.setShareMedia(smsShareContent);

		// 添加新浪微博分享
		// SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
		// sinaSsoHandler.addToSocialSDK();
		// // 设置新浪SSO handler
		// mController.getConfig().setSsoHandler(sinaSsoHandler);

		// 设置分享内容
		SinaShareContent sinaShareContent = new SinaShareContent();
		sinaShareContent.setShareContent(shareContent());// product +
															// "电话分享Sina");
		sinaShareContent.setTitle("        T.M.");
		sinaShareContent.setTargetUrl(url);
		sinaShareContent.setShareImage(localImage);
		mController.setShareMedia(sinaShareContent);
	}

	public String shareContent() {

		// 推荐好友
		String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);

		if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
			mRecommendInfo = DfineAction.InviteFriendInfo;
		}
		return mRecommendInfo;
	}

	private void initView() {
		initTitleNavBar(mParent);
		mTitleTextView.setText("发现");

		local_ad = (ImageView) mParent.findViewById(R.id.local_ad);
		more_imageview_01 = (ImageView) mParent.findViewById(R.id.more_imageview_01);
		more_imageview_02 = (ImageView) mParent.findViewById(R.id.more_imageview_02);
		more_imageview_03 = (ImageView) mParent.findViewById(R.id.more_imageview_03);
		more_imageview_04 = (ImageView) mParent.findViewById(R.id.more_imageview_04);
		more_imageview_05 = (ImageView) mParent.findViewById(R.id.more_imageview_05);
		more_imageview_06 = (ImageView) mParent.findViewById(R.id.more_imageview_06);
		more_imageview_07 = (ImageView) mParent.findViewById(R.id.more_imageview_07);
		more_imageview_08 = (ImageView) mParent.findViewById(R.id.more_imageview_08);
		vs_more_share = (RelativeLayout) mParent.findViewById(R.id.vs_more_share);

		more_layout_01 = (LinearLayout) mParent.findViewById(R.id.more_layout_01);
		more_layout_02 = (LinearLayout) mParent.findViewById(R.id.more_layout_02);
		more_layout_03 = (LinearLayout) mParent.findViewById(R.id.more_layout_03);
		more_layout_04 = (LinearLayout) mParent.findViewById(R.id.more_layout_04);
		more_layout_05 = (LinearLayout) mParent.findViewById(R.id.more_layout_05);
		more_layout_06 = (LinearLayout) mParent.findViewById(R.id.more_layout_06);
		more_layout_07 = (LinearLayout) mParent.findViewById(R.id.more_layout_07);
		more_layout_08 = (LinearLayout) mParent.findViewById(R.id.more_layout_08);
		recommend_row_2 = (LinearLayout) mParent.findViewById(R.id.recommend_row_2);
		vs_more_line9 = (View) mParent.findViewById(R.id.vs_more_line9);
		more_textview_01 = (TextView) mParent.findViewById(R.id.more_textview_01);
		more_textview_02 = (TextView) mParent.findViewById(R.id.more_textview_02);
		more_textview_03 = (TextView) mParent.findViewById(R.id.more_textview_03);
		more_textview_04 = (TextView) mParent.findViewById(R.id.more_textview_04);
		more_textview_05 = (TextView) mParent.findViewById(R.id.more_textview_05);
		more_textview_06 = (TextView) mParent.findViewById(R.id.more_textview_06);
		more_textview_07 = (TextView) mParent.findViewById(R.id.more_textview_07);
		more_textview_08 = (TextView) mParent.findViewById(R.id.more_textview_08);

		more_imageview_01.setOnClickListener(this);
		more_imageview_02.setOnClickListener(this);
		more_imageview_03.setOnClickListener(this);
		more_imageview_04.setOnClickListener(this);
		more_imageview_05.setOnClickListener(this);
		more_imageview_06.setOnClickListener(this);
		more_imageview_07.setOnClickListener(this);
		more_imageview_08.setOnClickListener(this);
		vs_more_share.setOnClickListener(this);

		// 分享内容广播
		initIntentFilter(VsUserConfig.JKey_GET_MY_SHARE_BRO, shareReceiver);

		// 页面配置信息
		String regInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_discoverTypes);
		JSONObject json;
		try {
			json = new JSONObject(regInfo);
			getResult(json.getString("convenient"), "convenient");
			getResult(json.getString("mini_game"), "mini_game");
			getResult(json.getString("share_to"), "share_to");
			getResult(json.getString("pay_center"), "pay_center");
			getResult(json.getString("scan_code"), "scan_code");
			getResult(json.getString("sina_page"), "sina_page");
			getResult(json.getString("baidu_page"), "baidu_page");
			getResult(json.getString("user_help"), "user_help");
			getResult(json.getString("earn_money"), "earn_money");
			getResult(json.getString("shop_mall"), "shop_mall");
			if (json.getString("earn_money").equals("y")) {
				vs_more_share.setVisibility(View.VISIBLE);
			} else {
				vs_more_share.setVisibility(View.GONE);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} finally {
			if (!(regInfo != null && regInfo.length() != 0)) {
				more_layout_01.setVisibility(View.VISIBLE);
				more_layout_02.setVisibility(View.VISIBLE);
				more_layout_03.setVisibility(View.VISIBLE);
				more_layout_04.setVisibility(View.VISIBLE);
				more_layout_05.setVisibility(View.VISIBLE);
				more_layout_06.setVisibility(View.VISIBLE);
				more_layout_07.setVisibility(View.VISIBLE);
				more_layout_08.setVisibility(View.VISIBLE);
			}
		}

		if (sb.length() != 0) {

			String info = sb.substring(0, sb.length() - 1);
			type_temp = info.split(",");
			if (type_temp.length <= 4) {
				recommend_row_2.setVisibility(View.INVISIBLE);
				vs_more_line9.setVisibility(View.INVISIBLE);
			}
			for (int i = 0; i < type_temp.length; i++) {
				if (type_temp[i].equals("convenient")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_server));
					getTextView(i).setText("便民服务");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("mini_game")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_game));
					getTextView(i).setText("游戏");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("share_to")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_open));
					getTextView(i).setText("分享");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("pay_center")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_recharge));
					getTextView(i).setText("充值");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("scan_code")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_scan));
					getTextView(i).setText("扫一扫");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("sina_page")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_sina_img));
					getTextView(i).setText("新浪");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("baidu_page")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.vs_baidu_img));
					getTextView(i).setText("百度");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("user_help")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_help));
					getTextView(i).setText("帮助中心");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("earn_money")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_mmoney));
					getTextView(i).setText("赚钱计划");
					getlayout(i).setVisibility(View.VISIBLE);
				} else if (type_temp[i].equals("shop_mall")) {
					getImageView(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_shop));
					getTextView(i).setText("嘟嘟商城");
					getlayout(i).setVisibility(View.VISIBLE);
				}
			}
		}

		more_ad_text = (TextView) mParent.findViewById(R.id.more_ad_text);
		final String ad_Text_str = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CONFIG_300002);
		if ("".equals(ad_Text_str) || "".equals(ad_conf_str)) {
			more_ad_text.setVisibility(View.GONE);
		} else {
			try {
				JSONArray ad_conf_json = new JSONArray(ad_Text_str);
				JSONObject jObj;
				jObj = (JSONObject) ad_conf_json.get(0);
				more_ad_text.setVisibility(View.VISIBLE);
				more_ad_text.setText(jObj.getString("des"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		moreAdapter = new MoreAdapter();
		moreGridView = (GridView) mParent.findViewById(R.id.more_GridView);
		moreGridView.setAdapter(moreAdapter);
	}

	private void getResult(String key, String string) {
		if (key != null && key.equals("y")) {
			sb.append(string + ",");
		}
	}

	// 申明广播
	private void initIntentFilter(String action, BroadcastReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);
		mContext.registerReceiver(receiver, filter);

	}

	private BroadcastReceiver shareReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(VsUserConfig.JKey_GET_MY_SHARE_BRO)) {
				String jStr = intent.getStringExtra("data");
				if (jStr != null && jStr.length() > 0) {
					initShare();
				}
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// 分享内容拉取
		String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
		if (uid != null && uid.length() > 0) {
			VsBizUtil.getInstance().getShareContent(mContext);
		}
	}

	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.more_imageview_01:
			setStartClass(type_temp[0]);
			break;
		case R.id.more_imageview_02:
			setStartClass(type_temp[1]);

			break;
		case R.id.more_imageview_03:
			setStartClass(type_temp[2]);

			break;
		case R.id.more_imageview_04:
			setStartClass(type_temp[3]);

			break;
		case R.id.more_imageview_05:
			setStartClass(type_temp[4]);

			break;
		case R.id.more_imageview_06:
			setStartClass(type_temp[5]);

			break;
		case R.id.more_imageview_07:
			setStartClass(type_temp[6]);

			break;
		case R.id.more_imageview_08:
			setStartClass(type_temp[7]);

			break;
		case R.id.vs_more_share:
			mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
			mController.getConfig().removePlatform(SHARE_MEDIA.SINA);
			mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
			mController.openShare(getActivity(), mShareListener);
			break;
		}

	}

	private void setStartClass(String type) {

		if (type != null) {

			if (type.equals("convenient")) {
				VsUtil.startActivity("http://m.46644.com/tool/?tpltype=weixin", mContext,
						mContext.getResources().getString(R.string.found_service));
			} else if (type.equals("user_help")) {
				VsUtil.startActivity("http://paas.edawtech.com/help/dudu_help.html", mContext,
						mContext.getResources().getString(R.string.found_help));
			} else if (type.equals("earn_money")) {
				VsUtil.startActivity("http://www.wind163.com/help/money_plan.html", mContext,
						mContext.getResources().getString(R.string.found_mmoney));
			} else if (type.equals("shop_mall")) {
				VsUtil.startActivity("http://shop.boriton.com/mobile/", mContext,
						mContext.getResources().getString(R.string.found_shop));
			} else if (type.equals("mini_game")) {
				VsUtil.startActivity("http://autobox.meiriq.com", mContext,
						mContext.getResources().getString(R.string.found_game));
			} else if (type.equals("share_to")) {
				mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
				mController.getConfig().removePlatform(SHARE_MEDIA.SINA);
				mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
				mController.openShare(getActivity(), mShareListener);
			} else if (type.equals("pay_center")) {
				if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt3), mContext)) {
					startActivity(mContext, VsRechargeActivity.class);
				}
			} else if (type.equals("scan_code")) {
				VsUtil.startActivity("4002", mContext, null);
			} else if (type.equals("sina_page")) {
				VsUtil.startActivity("http://www.sina.com.cn", mContext,
						mContext.getResources().getString(R.string.found_sina));
			} else if (type.equals("baidu_page")) {
				VsUtil.startActivity("http://www.baidu.com", mContext,
						mContext.getResources().getString(R.string.found_baidu));
			} else {
				VsUtil.startActivity("4001", mContext, null);// 手势密码
			}

		}
	}

	public void setdata() {

		moreList.add(new MoreItem(R.drawable.vs_user_search_img, "便民查询", "http://m.46644.com/tool/?tpltype=weixin"));
		moreList.add(new MoreItem(R.drawable.vs_mini_game_img, "游戏", "http://autobox.meiriq.com/list/e169c53e"));
		moreList.add(new MoreItem(R.drawable.vs_my_sigin, "每日签到", "3006"));
		moreList.add(new MoreItem(R.drawable.vs_hand_password_img, "手势密码", "4001"));
		moreList.add(new MoreItem(R.drawable.vs_qrcode_img, "二维码", "4002"));
		if (moreList.size() % 3 == 1) {
			moreList.add(new MoreItem(0, "", ""));
			moreList.add(new MoreItem(0, "", ""));
		} else if (moreList.size() % 3 == 2) {
			moreList.add(new MoreItem(0, "", ""));
		}
	}

	class hold {
		private ImageView moreimageview;
		private TextView more_textview;
		private LinearLayout more_layout;
	}

	class MoreAdapter extends BaseAdapter {
		LayoutInflater inflater = LayoutInflater.from(mContext);

		@Override
		public int getCount() {
			return moreList.size();
		}

		@Override
		public Object getItem(int position) {
			return moreList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final hold h;
			if (convertView == null) {
				h = new hold();
				convertView = inflater.inflate(R.layout.vs_layout_more_item, null);
				h.more_textview = (TextView) convertView.findViewById(R.id.more_textview);
				h.moreimageview = (ImageView) convertView.findViewById(R.id.more_imageview);
				h.more_layout = (LinearLayout) convertView.findViewById(R.id.more_layout);
				convertView.setTag(h);
			} else {
				h = (hold) convertView.getTag();
			}
			if (moreList.get(position).getImgId() != 0) {
				h.more_textview.setText(moreList.get(position).getMoreName());
				h.moreimageview.setImageResource(moreList.get(position).getImgId());
			}
			final int i = position;
			h.more_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					VsUtil.startActivity(moreList.get(i).getUrl(), mContext, moreList.get(i).getMoreName());
				}
			});
			return convertView;
		}

	}

	private LinearLayout getlayout(int i) {

		LinearLayout layout = null;
		switch (i) {
		case 0:
			layout = more_layout_01;
			break;
		case 1:
			layout = more_layout_02;
			break;
		case 2:
			layout = more_layout_03;
			break;
		case 3:
			layout = more_layout_04;
			break;
		case 4:
			layout = more_layout_05;
			break;
		case 5:
			layout = more_layout_06;
			break;
		case 6:
			layout = more_layout_07;
			break;
		case 7:
			layout = more_layout_08;
			break;

		}
		return layout;
	}

	private ImageView getImageView(int i) {

		ImageView layout = null;
		switch (i) {
		case 0:
			layout = more_imageview_01;
			break;
		case 1:
			layout = more_imageview_02;
			break;
		case 2:
			layout = more_imageview_03;
			break;
		case 3:
			layout = more_imageview_04;
			break;
		case 4:
			layout = more_imageview_05;
			break;
		case 5:
			layout = more_imageview_06;
			break;
		case 6:
			layout = more_imageview_07;
			break;
		case 7:
			layout = more_imageview_08;
			break;

		}
		return layout;
	}

	private TextView getTextView(int i) {

		TextView layout = null;
		switch (i) {
		case 0:
			layout = more_textview_01;
			break;
		case 1:
			layout = more_textview_02;
			break;
		case 2:
			layout = more_textview_03;
			break;
		case 3:
			layout = more_textview_04;
			break;
		case 4:
			layout = more_textview_05;
			break;
		case 5:
			layout = more_textview_06;
			break;
		case 6:
			layout = more_textview_07;
			break;
		case 7:
			layout = more_textview_08;
			break;

		}
		return layout;
	}

}