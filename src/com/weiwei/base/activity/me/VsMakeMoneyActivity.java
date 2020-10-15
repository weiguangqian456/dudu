package com.weiwei.base.activity.me;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsInviteItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
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
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description: 赚话费界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: Sundy
 * @version: 1.0.0.0
 * @Date: 2014-9-16
 */
public class VsMakeMoneyActivity extends VsBaseActivity implements OnClickListener{
	/**
	 * 赚取的总分钟数
	 */
	private TextView vs_invit_time;
	/**
	 * 分钟
	 */
	private TextView vs_invit_text;
	/**
	 * 赚取的总金额
	 */
	private TextView vs_invite_money;
	/**
	 * 任务
	 */
	private ListView vs_inite_list;

	/**
	 * 成功
	 */
	private final char MSG_ID_SUCC = 500;
	/**
	 * 失败
	 */
	private final char MSG_ID_FAIL = 501;
	/**
	 * 保存赚话费任务内容
	 */
	private ArrayList<VsInviteItem> vsInviteList = null;
	/**
	 * 赚话费任务队列
	 */
	private VsMakeMoneyAdapter adapter = null;
	
    private TextView mTextView;
    private TextView SigninNoticeView;
    /**
     * 分享
     */
    private UMSocialService mController;
    private LinearLayout linear_weixin, linear_weixin_circle, sina_weibo, linear_qq, linear_sms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_makemoney_activity_onlyinvite);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		mTitleTextView.setText(getResources().getString(R.string.vs_invite_title));
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);		
		init();
		initShare();
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}
	
	private void init() {
		linear_weixin = (LinearLayout) findViewById(R.id.linear_weixin);
		linear_weixin_circle = (LinearLayout) findViewById(R.id.linear_weixin_circle);
		sina_weibo = (LinearLayout) findViewById(R.id.linear_sina);
		linear_qq = (LinearLayout) findViewById(R.id.linear_qq);
		linear_sms = (LinearLayout) findViewById(R.id.linear_sms);
		linear_qq.setOnClickListener(this);
		linear_sms.setOnClickListener(this);
		sina_weibo.setOnClickListener(this);
		linear_weixin.setOnClickListener(this);
		linear_weixin_circle.setOnClickListener(this);
		SigninNoticeView = (TextView) findViewById(R.id.SigninNoticeView);
        String headline = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_HEADLINE);
        if (headline != null && headline.length() > 0) {
            SigninNoticeView.setText(Html.fromHtml(headline));
        } else {
//            String noticetext =getResources().getString(R.string.make_info);
//            SigninNoticeView.setText(Html.fromHtml(noticetext));
        }
        String detail = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_DETAIL);
        mTextView = (TextView) findViewById(R.id.FirstSignContentView);

        if (detail != null && detail.length() > 0) {
            mTextView.setText(detail);
        } else {
            mTextView.setText(getResources().getString(R.string.makemoney_point_first));
        }
    }
	
	private View.OnClickListener SignInButtonListenter = new View.OnClickListener() {
        public void onClick(View arg0) {
            // 推荐好友
            String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
            
            if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
                String InviteFriendInfo = DfineAction.InviteFriendInfo;
                if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) != null
                        && !"".equals(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId))) {
                    StringBuilder builder = new StringBuilder(InviteFriendInfo);
                    builder.append("/wap/lx.c?a=%").append(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
					builder.append("&s=sm");
                    mRecommendInfo = new String(builder);
                } else {
                    mRecommendInfo = DfineAction.InviteFriendInfo;
                }
            } else {
                mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
            }

            SendNoteObserver.sendSendNoteNumber = 1;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", "", null));
            intent.putExtra("sms_body", mRecommendInfo);
            startActivity(intent);
        }
    };	
	
	 private void initShare() {
			String find = "[a-zA-Z]+[^\\s]*\\.[a-zA-Z]+[^\\s]*\\.[a-zA-Z]+[^\\s]*[a-zA-Z]";
			String url="";
			Pattern p = Pattern.compile(find);
			Matcher m = p.matcher(shareContent());
			while(m.find()){
				url=m.group();
			}
		    String product=DfineAction.RES.getString(R.string.product);
	        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	        // 添加微信平台
	        UMWXHandler wxHandler = new UMWXHandler(mContext, DfineAction.WEIXIN_SHARE_APPID, DfineAction.WEIXIN_SHARE_APPSECRET);
	        wxHandler.addToSocialSDK();
	        // 添加微信朋友圈
	        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, DfineAction.WEIXIN_SHARE_APPID, DfineAction.WEIXIN_SHARE_APPSECRET);
	        wxCircleHandler.setToCircle(true);
	        wxCircleHandler.addToSocialSDK();

			String mRecommendInfo = VsUserConfig.getDataString(mContext,
					VsUserConfig.JKey_FRIEND_INVITE);
			String url_shar = mRecommendInfo.substring(mRecommendInfo.indexOf("http"),
					mRecommendInfo.length());
			CustomLog.i("123", url_shar);
			
	        //设置微信好友分享内容
	        WeiXinShareContent weixinContent = new WeiXinShareContent();
	        weixinContent.setShareContent(shareContent());
	        weixinContent.setTitle(product);
	        weixinContent.setTargetUrl(url);
	        UMImage localImage = new UMImage(mContext, createQRImage(url_shar, 114, 114));
	        weixinContent.setShareImage(localImage);
	        mController.setShareMedia(weixinContent);

	        //设置微信朋友圈分享内容
	        CircleShareContent circleMedia = new CircleShareContent();
	        circleMedia.setShareContent(shareContent());
	        circleMedia.setTitle(product);
	        circleMedia.setShareImage(localImage);
	        circleMedia.setTargetUrl(url);
	        mController.setShareMedia(circleMedia);


	        //添加QQ分享
	        String qqAppId = DfineAction.QqAppId;
	        String qqAppKey = DfineAction.QqAppKey;
	        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, qqAppId, qqAppKey);
	        qqSsoHandler.addToSocialSDK();
	        //设置分享内容
	        QQShareContent qqShareContent = new QQShareContent();
	        qqShareContent.setShareContent(shareContent());
	        qqShareContent.setTitle(product);
	        qqShareContent.setTargetUrl(url);
	        qqShareContent.setShareImage(localImage);
	        mController.setShareMedia(qqShareContent);

	        //添加QQ空间分享
	        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, qqAppId, qqAppKey);
	        qZoneSsoHandler.addToSocialSDK();
	        //设置分享内容
	        QZoneShareContent qZoneShareContent = new QZoneShareContent();
	        qZoneShareContent.setShareContent(shareContent());
	        qZoneShareContent.setTitle(product);
	        qZoneShareContent.setTargetUrl(url);
	        qZoneShareContent.setShareImage(localImage);
	        mController.setShareMedia(qZoneShareContent);
	//
//	            //添加SMSfenx分享
	        SmsHandler smsHandler = new SmsHandler();
	        smsHandler.addToSocialSDK();
	        //设置短信分享内容
	        SmsShareContent smsShareContent = new SmsShareContent();
	        smsShareContent.setShareContent(shareContent());
	        mController.setShareMedia(smsShareContent);

	        //添加新浪微博分享
//	        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
//	        sinaSsoHandler.addToSocialSDK();
//	        //设置新浪SSO handler
//	        mController.getConfig().setSsoHandler(sinaSsoHandler);

	        // 设置分享内容
	        SinaShareContent sinaShareContent = new SinaShareContent();
	        sinaShareContent.setShareContent(shareContent());
	        sinaShareContent.setTitle("        T.M.");
	        sinaShareContent.setTargetUrl(url);
	        sinaShareContent.setShareImage(localImage);
	        mController.setShareMedia(sinaShareContent);
	    }
	 
	 public Bitmap createQRImage(String url, final int width, final int height) {
			try {
				// 判断URL合法性
				if (url == null || "".equals(url) || url.length() < 1) {
					return null;
				}
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				// 图像数据转换，使用了矩阵转换
				BitMatrix bitMatrix = new QRCodeWriter().encode(url,
						BarcodeFormat.QR_CODE, width, height, hints);
				int[] pixels = new int[width * height];
				// 下面这里按照二维码的算法，逐个生成二维码的图片，
				// 两个for循环是图片横列扫描的结果
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (bitMatrix.get(x, y)) {
							pixels[y * width + x] = 0xff000000;
						} else {
							pixels[y * width + x] = 0xffffffff;
						}
					}
				}
				// 生成二维码图片的格式，使用ARGB_8888
				Bitmap bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
				return bitmap;
			} catch (WriterException e) {
				e.printStackTrace();
			}
			return null;
		}
	 
	 public String shareContent()
	    {
	        
	     // 推荐好友
	        String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);

	        if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
	            String InviteFriendInfo =DfineAction.InviteFriendInfo;
	            if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) != null && !"".equals(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId))) {
	                StringBuilder builder = new StringBuilder(InviteFriendInfo);
	                builder.append("/wap/lx.c?a=%").append(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
					builder.append("&s=sm");
	                mRecommendInfo = new String(builder);
	            }
	            else {
	                mRecommendInfo =DfineAction.InviteFriendInfo;
	            }
	        }
	        else {
	            mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
	        }
	        return mRecommendInfo;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);
		Message message = mBaseHandler.obtainMessage();
		Bundle bundle = new Bundle();
		String jsStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
		JSONObject jsData;
		if (GlobalVariables.action_invite_task.equals(intent.getAction())) {
			try {
				jsData = new JSONObject(jsStr);
				if ("0".equals(jsData.getString("result"))) {
					message.what = MSG_ID_SUCC;
					bundle.putString("msg", jsStr);
				} else {
					bundle.putString("msg", jsData.getString("reason"));
					message.what = MSG_ID_SUCC;
				}
			} catch (Exception e) {
				bundle.putString("msg", "异常");
				message.what = MSG_ID_FAIL;
			} finally {
				message.setData(bundle);
				mBaseHandler.sendMessage(message);
			}
		}
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case MSG_ID_SUCC:
			String jsStr = msg.getData().getString("msg");
			if (jsStr != null && !"".equals(jsStr)) {
				try {
					JSONObject jsData = new JSONObject(jsStr);
					JSONObject total_award = jsData.getJSONObject("total_award");
					String total_award_min = total_award.getString("total_award_min");
					// 根据内容长度设置字体大小
					if (total_award_min.length() < 4) {
						vs_invit_time.setTextSize(50f);
					} else if (total_award_min.length() < 6) {
						vs_invit_time.setTextSize(43f);
					} else if (total_award_min.length() < 8) {
						vs_invit_time.setTextSize(39f);
					}
					vs_invit_time.setText(total_award_min);
					vs_invite_money.setText("(" + total_award.getString("total_award_money") + "元)");
					initListData(jsData.getJSONArray("task"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case MSG_ID_FAIL:
			mToast.show(msg.getData().getString("msg"));
			break;
		default:
			break;
		}
	}

	/**
	 * 初试话列表数据
	 * 
	 * @param jsonArray
	 */
	private void initListData(JSONArray jsonArray) {
		try {
			if (jsonArray != null && jsonArray.length() > 0) {
				vsInviteList = new ArrayList<VsInviteItem>(20);
				for (int i = 0; i < jsonArray.length(); i++) {
					VsInviteItem item = new VsInviteItem();
					item.setTitle(jsonArray.getJSONObject(i).getString("title"));
					item.setName(jsonArray.getJSONObject(i).getString("name"));
					item.setGoods_id(jsonArray.getJSONObject(i).getString("goods_id"));
					item.setUrl(jsonArray.getJSONObject(i).getString("url"));
					item.setBtn_name(jsonArray.getJSONObject(i).getString("btn_name"));
					item.setJump_url(jsonArray.getJSONObject(i).getString("jump_url"));
					item.setTasktype(jsonArray.getJSONObject(i).getString("tasktype"));
					item.setTips(jsonArray.getJSONObject(i).getString("tips"));
					item.setTotal_money(jsonArray.getJSONObject(i).getString("total_money"));
					item.setSecond_name(jsonArray.getJSONObject(i).getString("second_name"));
					item.setTotal_min(jsonArray.getJSONObject(i).getString("total_min"));
					vsInviteList.add(item);
				}
				adapter = new VsMakeMoneyAdapter(mContext, vsInviteList);
				vs_inite_list.setAdapter(adapter);
				VsUtil.setListViewHeightBasedOnChildren(vs_inite_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		  // TODO Auto-generated method stub
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
        case R.id.linear_weixin:
            performShare(SHARE_MEDIA.WEIXIN);
            break;
        case R.id.linear_weixin_circle:
        	performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
        	break;
        case R.id.linear_sina:
            performShare(SHARE_MEDIA.SINA);
            break;
        case R.id.linear_qq:
            performShare(SHARE_MEDIA.QQ);
            break;
        case R.id.linear_sms:
            performShare(SHARE_MEDIA.SMS);
            break;
        }		
	}
	  private void performShare(SHARE_MEDIA platform) {
	        mController.postShare(mContext, platform, new SnsPostListener() {

	            @Override
	            public void onStart() {

	            }
	            @Override
	            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {	            	                
	            }
	        });
	    }}
