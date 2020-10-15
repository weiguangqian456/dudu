package com.weiwei.base.adapter;

import java.util.ArrayList;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.hwtx.dududh.R;
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
import com.weiwei.base.dataprovider.DfineAction;

@SuppressLint("NewApi")
public class VsMyExChangeAdapter extends BaseAdapter {
	 private ArrayList<Map<String,Object>> data;
	 private UMSocialService mController;
	 private Context ctx;
	 private ClipboardManager mClipboard = null;
		private SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {
			@Override
			public void onStart() {
				
			}

			@Override
			public void onComplete(SHARE_MEDIA share_media, int eCode,
					SocializeEntity socializeEntity) {

				if (eCode == 200) {
					if (!"SINA".equals(share_media.name())
							&& !"SMS".equals(share_media.name())) {
						Toast.makeText(ctx, "分享成功", Toast.LENGTH_LONG)
								.show();
					}
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

					// if (!"QZONE".equals(share_media.name()) &&
					// !"QQ".equals(share_media.name())/* &&
					// !"SINA".equals(share_media.name())*/) {
					Toast.makeText(ctx, eMsg, Toast.LENGTH_SHORT).show();
					// }
				}
				
			}
		};

	   public VsMyExChangeAdapter(Context ctx, ArrayList<Map<String,Object>> data){
	        this.ctx = ctx;
	        this.data = data;
	        
	    }

	    @Override
	    public int getCount() {
	        return data.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return data.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
//	    	NoticeDataList mc = (NoticeDataList)getItem(position); 
	    	
	        ViewHolder holder= null;
	        if(convertView == null){
	            View view = LayoutInflater.from(ctx).inflate(R.layout.vs_myexchange_text,null);
	            holder=new ViewHolder(view);
	            convertView = view;
	        }else{
	            holder = (ViewHolder) convertView.getTag();
	        }
	        
	        String price = data.get(position).get("price").toString();
	        String expire_date = data.get(position).get("expire_date").toString();
	        String card_no = data.get(position).get("card_no").toString();
	        final String card_pwd = data.get(position).get("card_pwd").toString();
	        String pay_phone = data.get(position).get("pay_phone").toString();
//	        initShare(card_pwd);
	        holder.cardNo.setText(card_no);
	        holder.action.setText(Html.fromHtml("<u>"+"复制"+"</u>"));
	        holder.money.setText(price);
	        holder.time.setText(expire_date);
	       
	        holder.action.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
//					mController.getConfig().removePlatform(SHARE_MEDIA.SINA);
//					mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
//					mController.openShare((Activity)ctx, mShareListener);
					copyFromEditText(card_pwd);
				}
			});
	   
	       
	        
	        return convertView;
	    }

	  
	    @SuppressLint("NewApi")
		private void copyFromEditText(String txt) {
	    	 
	        // Gets a handle to the clipboard service.
	        if (null == mClipboard) {
	            mClipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
	        }

	        ClipData clip = ClipData.newPlainText("simple text",txt);
	        mClipboard.setPrimaryClip(clip);
	        
	        if (mClipboard.hasPrimaryClip()) {
	            Toast.makeText(ctx,"卡密已复制到剪切板", Toast.LENGTH_SHORT).show();
	        }
	        
	    }
	    
	    
	    private void initShare(String card_pwd) {
			String product = DfineAction.RES.getString(R.string.product);
			mController = UMServiceFactory.getUMSocialService("com.umeng.share");
			// 添加微信平台
			UMWXHandler wxHandler = new UMWXHandler((Activity)ctx,
					DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
			wxHandler.addToSocialSDK();
			// 添加微信朋友圈
			UMWXHandler wxCircleHandler = new UMWXHandler((Activity)ctx,
					DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
//			String url = "";
//			if (shareContent().indexOf("http")>0) {
//			 url = shareContent().substring(shareContent().indexOf("http"));
//		
//		}

			// 设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			weixinContent.setShareContent(card_pwd);//product + "电话微信分享");
			weixinContent.setTitle(product);
//			weixinContent.setTargetUrl(url);//DfineAction.WAPURI);
			UMImage localImage = new UMImage((Activity)ctx, R.drawable.icon);
			weixinContent.setShareImage(localImage);
			mController.setShareMedia(weixinContent);

			// 设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(card_pwd);//product + "电话分享朋友圈");
			circleMedia.setTitle(product);
			circleMedia.setShareImage(localImage);
//			circleMedia.setTargetUrl(url);
			mController.setShareMedia(circleMedia);

			// 添加QQ分享
			String qqAppId = DfineAction.QqAppId;
			String qqAppKey = DfineAction.QqAppKey;
			UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity)ctx,
					qqAppId, qqAppKey);
			qqSsoHandler.addToSocialSDK();
			// 设置分享内容
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(card_pwd);//product + "电话分享测试qq");
			qqShareContent.setTitle(product);
//			qqShareContent.setTargetUrl(url);
			qqShareContent.setShareImage(localImage);
			mController.setShareMedia(qqShareContent);

			// 添加QQ空间分享
			QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity)ctx,
					qqAppId, qqAppKey);
			qZoneSsoHandler.addToSocialSDK();
			// 设置分享内容
			QZoneShareContent qZoneShareContent = new QZoneShareContent();
			qZoneShareContent.setShareContent(card_pwd);//product + "电话分享qqZone");
			qZoneShareContent.setTitle(product);
//			qZoneShareContent.setTargetUrl(url);
			qZoneShareContent.setShareImage(localImage);
			mController.setShareMedia(qZoneShareContent);
			//
			// //添加SMSfenx分享
			SmsHandler smsHandler = new SmsHandler();
			smsHandler.addToSocialSDK();
			// 设置短信分享内容
			SmsShareContent smsShareContent = new SmsShareContent();
			smsShareContent.setShareContent(card_pwd);//product + "电话短信分享");
			mController.setShareMedia(smsShareContent);

			// 添加新浪微博分享
//			SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
//			sinaSsoHandler.addToSocialSDK();
//			// 设置新浪SSO handler
//			mController.getConfig().setSsoHandler(sinaSsoHandler);

			// 设置分享内容
			SinaShareContent sinaShareContent = new SinaShareContent();
			sinaShareContent.setShareContent(card_pwd);//product + "电话分享Sina");
			sinaShareContent.setTitle("        T.M.");
//			sinaShareContent.setTargetUrl(url);
			sinaShareContent.setShareImage(localImage);
			mController.setShareMedia(sinaShareContent);
		}
	    
	  

	    final static class ViewHolder{
	       
	        
	        TextView cardNo,action,money,time;
	        ImageView image_notice_zan;
	        ViewHolder(View view){
	            this.cardNo = (TextView) view.findViewById(R.id.cardNo);//
	            this.time = (TextView) view.findViewById(R.id.time);//
	            this.action = (TextView) view.findViewById(R.id.action);//操作内容
	            this.money = (TextView) view.findViewById(R.id.money);
	            view.setTag(this);
	        }
	    }
	    
	    
	    
	    
	    
	  
	}