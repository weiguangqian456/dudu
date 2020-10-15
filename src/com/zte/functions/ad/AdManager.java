package com.zte.functions.ad;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;

import com.zte.common.AsyncImageUtil;
import com.zte.functions.HttpManager;
import com.zte.functions.IHttpResult;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;

/**
 * Created by Jiangxuewu on 2015/3/23.
 */
public class AdManager {

    private static final String TAG = AdManager.class.getSimpleName();
    /**
     * 闪屏页的广告
     */
    public static final int TYPE_START_PAGE = 1000;
    /**
     * 拨号页面
     */
    public static final int TYPE_DIAL = 1001;
    /**
     * IM页面
     */
    public static final int TYPE_IM = 1002;
    /**
     * 直拨页面
     */
    public static final int TYPE_CALLING = 1003;
    /**
     * 广告信息存储key
     */
    public static final String KEY_SP_FILE = "jkey_ad_json";
    /**
     * 下次更新广告时间
     */
    public static final String UPDATE_KEY_SP_FILE = "jkey_ad_update_key";
    /**
     * 刷新广告
     */
    public static final String ACTION_UPDATE_AD = "broad_action_update_ad";

    /**
     * true 表示显示广告， false表示不显示广告
     */
    private boolean isNeedAd = VsApplication.getContext().getResources().getString(R.string.is_need_ad).equals("open");

    private static AdManager mInstance;

    public static AdManager getInstance() {
        synchronized (TAG) {
            if (null == mInstance) {
                mInstance = new AdManager();
            }
        }
        return mInstance;
    }

    private AdManager() {
        AsyncImageUtil.getInstance();
    }

    /**
     * 判断 type类型的广告是否存在本地
     *
     * @param context
     * @param type
     * @return
     */
    public boolean hasAd(Context context, int type) {
        if (!isNeedAd) {
            return false;
        }
        String json = VsUserConfig.getDataString(context, AdManager.KEY_SP_FILE);
        AdInfo info = null;
        try {
            info = new AdInfo(new JSONObject(json));
        } catch (JSONException ignored) {
        }

        if (null == info) {
            return false;
        }

        List<AdConfig> pageAds = info.getAdConfig();
        if (null == pageAds) {
            return false;
        }

        switch (type) {
            case TYPE_START_PAGE:
                return isExistRightAd(pageAds, AdConfig.AD_START_PAGE);
            case TYPE_DIAL:
                return isExistRightAd(pageAds, AdConfig.AD_CALL_PAGE);
            case TYPE_IM:
                return isExistRightAd(pageAds, AdConfig.AD_IM_PAGE);
            case TYPE_CALLING:
                return isExistRightAd(pageAds, AdConfig.AD_CALL_LIVE_PAGE);
            default:
                return false;
        }
    }

    private boolean isExistRightAd(List<AdConfig> pageAds, String adStartPage) {
        for (AdConfig page : pageAds) {
            if (adStartPage.equals(page.getPageId())) {
                List<AdData> datas = page.getAdData();
                if (null == datas || datas.size() == 0) {
                    return false;
                }
                for (AdData ad : datas) {
                    if (ad.isRight()) {//有一条有效广告就算有广告
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }


    /**
     * @param context
     * @param type
     * @param random
     * @return
     */
    public AdData getAd(Context context, int type, int random) {
        if (!isNeedAd) {
            return null;
        }
        String json = VsUserConfig.getDataString(context, AdManager.KEY_SP_FILE);
        AdInfo info = null;
        try {
            info = new AdInfo(new JSONObject(json));
        } catch (JSONException e) {
        }

        if (null == info) {
            return null;
        }

        List<AdConfig> pageAds = info.getAdConfig();
        if (null == pageAds) {
            return null;
        }
        switch (type) {
            case TYPE_START_PAGE:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_START_PAGE.equals(page.getPageId())) {
                        List<AdData> ads = page.getAdData();
                        if (random < 0)
                            random = 0;
                        if (random >= ads.size())
                            random = ads.size() - 1;
                        if (null == ads) {
                            return null;
                        }

                        if (ads.get(random).isRight()) {
                            return ads.get(random);
                        } else {
                            ads.remove(random);

                            for (int i = 0; i < ads.size(); ) {
                                AdData a = ads.get(new Random().nextInt(ads.size()));
                                if (a.isRight()) {
                                    return a;
                                } else {
                                    ads.remove(a);
                                }
                            }
                            return null;
                        }

                    }
                }
                break;
            case TYPE_DIAL:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_CALL_PAGE.equals(page.getPageId())) {
                        List<AdData> ads = page.getAdData();
                        if (random < 0)
                            random = 0;
                        if (random >= ads.size())
                            random = ads.size() - 1;
                        if (null == ads) {
                            return null;
                        }

                        if (ads.get(random).isRight()) {
                            return ads.get(random);
                        } else {
                            ads.remove(random);

                            for (int i = 0; i < ads.size(); ) {
                                AdData a = ads.get(new Random().nextInt(ads.size()));
                                if (a.isRight()) {
                                    return a;
                                } else {
                                    ads.remove(a);
                                }
                            }
                            return null;
                        }
                    }
                }
                break;
            case TYPE_IM:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_IM_PAGE.equals(page.getPageId())) {
                        List<AdData> ads = page.getAdData();
                        if (random < 0)
                            random = 0;
                        if (random >= ads.size())
                            random = ads.size() - 1;
                        if (null == ads) {
                            return null;
                        }

                        if (ads.get(random).isRight()) {
                            return ads.get(random);
                        } else {
                            ads.remove(random);

                            for (int i = 0; i < ads.size(); ) {
                                AdData a = ads.get(new Random().nextInt(ads.size()));
                                if (a.isRight()) {
                                    return a;
                                } else {
                                    ads.remove(a);
                                }
                            }
                            return null;
                        }
                    }
                }
                break;
            case TYPE_CALLING:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_CALL_LIVE_PAGE.equals(page.getPageId())) {
                        List<AdData> ads = page.getAdData();
                        if (random < 0)
                            random = 0;
                        if (random >= ads.size())
                            random = ads.size() - 1;
                        if (null == ads) {
                            return null;
                        }

                        if (ads.get(random).isRight()) {
                            return ads.get(random);
                        } else {
                            ads.remove(random);

                            for (int i = 0; i < ads.size(); ) {
                                AdData a = ads.get(new Random().nextInt(ads.size()));
                                if (a.isRight()) {
                                    return a;
                                } else {
                                    ads.remove(a);
                                }
                            }
                            return null;
                        }
                    }
                }
                break;
            default:
                return null;
        }
        return null;
    }

    /**
     * 获取type类型广告的所有广告
     *
     * @param context
     * @param type
     * @return
     */
    public List<AdData> getAd(Context context, int type) {
        if (!isNeedAd) {
            return null;
        }
        String json = VsUserConfig.getDataString(context, AdManager.KEY_SP_FILE);
        AdInfo info = null;
        try {
            info = new AdInfo(new JSONObject(json));
        } catch (JSONException e) {
        }

        if (null == info) {
            return null;
        }

        List<AdConfig> pageAds = info.getAdConfig();
        if (null == pageAds) {
            return null;
        }
        List<AdData> result = null;
        switch (type) {
            case TYPE_START_PAGE:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_START_PAGE.equals(page.getPageId())) {
                        result = page.getAdData();
                        for (int i = 0; i < result.size(); ) {
                            if (!result.get(i).isRight()) {
                                result.remove(i);
                            } else {
                                i++;
                            }
                        }
                        return result;
                    }
                }
                break;
            case TYPE_DIAL:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_CALL_PAGE.equals(page.getPageId())) {
                        result = page.getAdData();
                        for (int i = 0; i < result.size(); ) {
                            if (!result.get(i).isRight()) {
                                result.remove(i);
                            } else {
                                i++;
                            }
                        }
                        return result;
                    }
                }
                break;
            case TYPE_IM:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_IM_PAGE.equals(page.getPageId())) {
                        result = page.getAdData();
                        for (int i = 0; i < result.size(); ) {
                            if (!result.get(i).isRight()) {
                                result.remove(i);
                            } else {
                                i++;
                            }
                        }
                        return result;
                    }
                }
                break;
            case TYPE_CALLING:
                for (AdConfig page : pageAds) {
                    if (AdConfig.AD_CALL_LIVE_PAGE.equals(page.getPageId())) {
                        result = page.getAdData();
                        for (int i = 0; i < result.size(); ) {
                            if (!result.get(i).isRight()) {
                                result.remove(i);
                            } else {
                                i++;
                            }
                        }
                        return result;
                    }
                }
                break;
            default:
                return null;
        }
        return null;
    }

    public void initAd(Context context) {
        if (!isNeedAd) {
            return;
        }
        CustomLog.i(TAG, "initAd(), start...");
        long time = VsUserConfig.getDataLong(context, UPDATE_KEY_SP_FILE);
        long curTime = System.currentTimeMillis();
        if (curTime < time) {
            startTimerUpdate(context, (time - curTime));
            return;
        }

        String cid = null;
        String adNo = null;
        String pid = null;

        final String json = VsUserConfig.getDataString(context, KEY_SP_FILE);
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject js = new JSONObject(json);
                AdInfo info = new AdInfo(js);
                adNo = info.getAdNo();
            } catch (JSONException ignored) {
            }
        }

        IHttpResult callBack = new IHttpResult() {
            @Override
            public void handleResult(Context context, String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("result")) {
                        int res = jsonObject.getInt("result");
                        switch (res) {
                            case 0://获取广告成功
                                AdInfo info = new AdInfo(jsonObject);
                                boolean iRes = info.save(context, jsonObject.toString());
                                CustomLog.i(TAG, "adsystem/agent_ad....info.getAdCacheTime() = " + info.getAdCacheTime());
                                VsUserConfig.setData(context, AdManager.UPDATE_KEY_SP_FILE, System.currentTimeMillis() + info.getAdCacheTime());
                                updateAd(context);
                                startTimerUpdate(context, info.getAdCacheTime());
                                break;
                            case 100://广告内容未改变
                                break;
                            case 101://广告未配置
                            	VsUserConfig.setData(context, AdManager.KEY_SP_FILE, "");
                                break;
                            case -1://负数:错误参数
                            default:
                        }
                    }
                } catch (JSONException e) {
                }
            }
        };
        GetAdInfo adInfo = new GetAdInfo(context, cid, adNo, pid, callBack);
//        HttpManager.getInstance().getAdInfo(context, adInfo);
    }

    private void startTimerUpdate(final Context context, long delay) {
        CustomLog.i(TAG, "startTimerUpdate(), adsystem/agent_ad....delay = " + delay);
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                initAd(context);
            }
        };

        timer.schedule(task, delay);
    }

    /**
     * 刷新广告
     *
     * @param context
     */
    private void updateAd(Context context) {
        CustomLog.i(TAG, "updateAd(),....");
        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(ACTION_UPDATE_AD);
        context.sendBroadcast(intent);
    }

    /**
     * 借用第三方类下载图片
     *
     * @param imageUrl
     */
    public void downloadImage(final String imageUrl) {
        AsyncImageUtil.getInstance().downloadImage(imageUrl);
    }

    /**
     * 给ImageView设置背景图片
     *
     * @param mAdImage
     * @param imageUrl
     */
    public void initImageView(ImageView mAdImage, String imageUrl) {
        AsyncImageUtil.getInstance().initImageView(mAdImage, imageUrl);
    }

    public void initImageView_(ImageView mAdImage, String imageUrl) {
        AsyncImageUtil.getInstance().initImageView_(mAdImage, imageUrl);
    }
}
