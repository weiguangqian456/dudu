package com.weiwei.home.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.BaseShareContent;
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
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.home.adapter.CustomShareAdapter;
import com.weiwei.home.base.BaseDefaultDialog;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.entity.CustomShareEntity;
import com.weiwei.home.entity.ShareItemEntity;
import com.weiwei.salemall.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/3/21.
 * @description: 分享
 * 示例：
    CustomShareDialog
    .getInstance()
    .initEntity(new CustomShareEntity(content,url,"http://img4.duitang.com/uploads/item/201402/14/20140214120558_2f4NN.jpeg",product))
    .setShareActivity(new ShareListener())
    .show(getFragmentManager(),"");
 */

public class CustomShareDialog extends BaseDefaultDialog{

    @BindView(R.id.rv_share)
    RecyclerView rv_share;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    private CustomShareEntity entity;
    private List<ShareItemEntity> mList = new ArrayList<>();
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    public static final String SHARE_TYPE_QQ = "QQ";
    public static final String SHARE_TYPE_QZONE = "QZone";
    public static final String SHARE_TYPE_WEIXIN = "WeiXin";
    public static final String SHARE_TYPE_CIRCLE = "Circle";
    public static final String SHARE_TYPE_SINA = "Sina";
    public static final String SHARE_TYPE_SMS = "Sms";

    String[] shareArray = new String[]{SHARE_TYPE_QQ,SHARE_TYPE_QZONE,SHARE_TYPE_WEIXIN,SHARE_TYPE_CIRCLE,SHARE_TYPE_SINA,SHARE_TYPE_SMS};

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_dialog;
    }

    public static CustomShareDialog getInstance() {
        return new CustomShareDialog();
    }

    public CustomShareDialog initEntity(CustomShareEntity entity){
        this.entity = entity;
        return this;
    }

    @Override
    protected void initView() {
        initShare(getActivity());
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rv_share.setLayoutManager(new GridLayoutManager(mContext,4));
        CustomShareAdapter adapter = new CustomShareAdapter(mContext, mList);
        rv_share.setAdapter(adapter);
        adapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                if(mListener == null){
                    ToastUtils.show(mContext,"分享异常 - 05L");
                    return;
                }
                switch (mList.get(position).getKey()){
                    case SHARE_TYPE_WEIXIN:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.WEIXIN,mListener);
                        break;
                    case SHARE_TYPE_CIRCLE:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.WEIXIN_CIRCLE, mListener);
                        break;
                    case SHARE_TYPE_QQ:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.QQ, mListener);
                        break;
                    case SHARE_TYPE_QZONE:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.QZONE,mListener);
                        break;
                    case SHARE_TYPE_SINA:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.SINA,mListener);
                        break;
                    case SHARE_TYPE_SMS:
                        mController.postShare(VsApplication.getContext(), SHARE_MEDIA.SMS,mListener);
                        break;
                    default:
                        ToastUtils.show(mContext,"未添加对应的分享事件");
                        break;
                }
                dismiss();
            }
        });
    }

    private void initShare(Activity activity){
        if(activity == null){
            ToastUtils.show(mContext,"分享异常 - 05A");
            return;
        }
        for (String aShareArray : shareArray) {
            switch (aShareArray) {
                case SHARE_TYPE_WEIXIN:
                    UMWXHandler wxHandler = new UMWXHandler(activity, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
                    wxHandler.addToSocialSDK();
                    mList.add(new ShareItemEntity(SHARE_TYPE_WEIXIN,R.drawable.ic_wechat, "微信好友"));
                    setShareContent(new WeiXinShareContent(),activity);
                    break;
                case SHARE_TYPE_CIRCLE:
                    UMWXHandler wxCircleHandler = new UMWXHandler(activity, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
                    wxCircleHandler.setToCircle(true);
                    wxCircleHandler.addToSocialSDK();
                    mList.add(new ShareItemEntity(SHARE_TYPE_CIRCLE,R.drawable.ic_moments, "朋友圈"));
                    setShareContent(new CircleShareContent(),activity);
                    break;
                case SHARE_TYPE_QQ:
                    UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, DfineAction.QqAppId, DfineAction.QqAppKey);
                    qqSsoHandler.addToSocialSDK();
                    mList.add(new ShareItemEntity(SHARE_TYPE_QQ,R.drawable.ic_qq, "QQ好友"));
                    setShareContent(new QQShareContent(),activity);
                    break;
                case SHARE_TYPE_QZONE:
                    QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, DfineAction.QqAppId, DfineAction.QqAppKey);
                    qZoneSsoHandler.addToSocialSDK();
                    mList.add(new ShareItemEntity(SHARE_TYPE_QZONE,R.drawable.ic_zone, "QQ空间"));
                    setShareContent(new QZoneShareContent(),activity);
                    break;
                case SHARE_TYPE_SINA:
                    mList.add(new ShareItemEntity(SHARE_TYPE_SINA,R.drawable.ic_sina, "新浪"));
                    setShareContent(new SinaShareContent(),activity);
                    break;
                case SHARE_TYPE_SMS:
                    SmsHandler smsHandler = new SmsHandler();
                    smsHandler.addToSocialSDK();
                    mList.add(new ShareItemEntity(SHARE_TYPE_SMS,R.drawable.vs_share_sms, "短信"));
                    setShareContent(new SmsShareContent());
                    break;
                default:
                    break;
            }
        }
    }

    public CustomShareDialog removeShare(String type){
        int k = -1;
        for(int i = 0; i < shareArray.length;i++){
            if(shareArray[i].equals(type)){
                k = i;
            }
        }
        if(k != -1){
            shareArray[k] = "NULL";
        }
        return this;
    }

    private void setShareContent(BaseShareContent content,Activity activity){
        content.setShareContent(entity.getShareContent());
        content.setTargetUrl(entity.getShareUrl());
        content.setTitle(entity.getShareTitle());
        if(entity.getShareBitmap() !=null){
            content.setShareImage(new UMImage(activity, entity.getShareBitmap()));
        }else{
            content.setShareImage(new UMImage(activity, entity.getShareImage()));
        }
        mController.setShareMedia(content);
    }

    private void setShareContent(SmsShareContent content){
        content.setShareContent(entity.getShareContent());
        mController.setShareMedia(content);
    }

    private SocializeListeners.SnsPostListener mListener;

    public CustomShareDialog setShareActivity(SocializeListeners.SnsPostListener mListener){
        this.mListener = mListener;
        return this;
    }
}
