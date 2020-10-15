package com.weiwei.salemall.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.adapter.ShareAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.bean.ShareItemBean;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * @author Created by EDZ on 2018/08/06
 *         分享界面Activity
 */

public class ShareActivity extends TempAppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.iv_share)
    ImageView shareIv;

    private PopupWindow shareDialog;
    private ShareAdapter shareAdapter;
    private List<ShareItemBean> shareInfos = new ArrayList<>();

    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    SocializeListeners.SnsPostListener mSnsPostListener = null;

    private static final String TAG = "ShareActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title.setText("邀请有礼");
        backRl.setOnClickListener(this);
        shareIv.setOnClickListener(this);

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
    }

    private void initShareParams() {
        //添加微信
        UMWXHandler wxHandler = new UMWXHandler(this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, DfineAction.WEIXIN_APPID, DfineAction.WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //添加QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qqSsoHandler.addToSocialSDK();

        //添加空间
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, DfineAction.QqAppId, DfineAction.QqAppKey);
        qZoneSsoHandler.addToSocialSDK();
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
        Bitmap iconMap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
        UMImage localImage = new UMImage(mContext, compressBitmap(iconMap));
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

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_share:
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                if (CheckLoginStatusUtils.isLogin()) {
                    showShareDialog(v);
                } else {
                    skipPage(VsLoginActivity.class);
                }
                break;
            default:
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
        mController.postShare(this, qzone, snsPostListener());
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
}
