package com.zte.functions.ad;

import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;

import com.zte.common.SystemUtil;
import com.zte.functions.BaseInfo;
import com.zte.functions.IHttpResult;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;

/**
 * Created by Jiangxuewu on 2015/3/19.
 * <p>
 * 获取企业广告
 * </p>
 */
public class GetAdInfo extends BaseInfo {

    private static final String TAG = GetAdInfo.class.getSimpleName();

    private static final String BID = "bid";//品牌ID 必传
    private static final String UID = "userid";//客户ID 必传
    private static final String CID = "cid";//广告商ID 可不传，系统会自动检测，不为空时优先此广告商广告
    private static final String ADNO = "adno";//广告编号  用于校对广告内容是否有更新
    private static final String PID = "pid";//页面ID   页面ID不传时，拉取全部
    private static final String SCREENW = "screenw";//屏幕宽度
    private static final String SCREENH = "screenh";//屏幕高度
    private static final String SP = "sp";//平台 如:android,iphone,iphone-app
    private static final String APPVERSION = "appversion";//版本号


    private final String sp = "android";

    private String bid;
    private String uid;
    private String cid;
    private String adNo;
    private String pid;
    private String screenW;
    private String screenH;
    private String appVersion;

    private IHttpResult callBack;

    public GetAdInfo(Context context, String cid, String adNo, String pid, IHttpResult callBack) {
        bid = DfineAction.brandid;
        uid = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
        screenW = String.valueOf(GlobalVariables.width);
        screenH = String.valueOf(GlobalVariables.height);
        appVersion = SystemUtil.getApkVersion(context);
        this.cid = cid;
        this.adNo = adNo;
        this.pid = pid;
        this.callBack = callBack;
    }


    @Override
    public String getMethod() {
        return "/adsystem/agent_ad";
    }

    @Override
    public TreeMap<String, String> getParamas(Context context) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        if (!TextUtils.isEmpty(bid)) {
            map.put(BID, bid);
        }
        if (!TextUtils.isEmpty(uid)) {
            map.put(UID, uid);
        }
        if (!TextUtils.isEmpty(cid)) {
            map.put(CID, cid);
        }
        if (!TextUtils.isEmpty(adNo)) {
            map.put(ADNO, adNo);
        }
        if (!TextUtils.isEmpty(pid)) {
            map.put(PID, pid);
        }
        if (!TextUtils.isEmpty(screenW)) {
            map.put(SCREENW, screenW);
        }
        if (!TextUtils.isEmpty(screenH)) {
            map.put(SCREENH, screenH);
        }
        if (!TextUtils.isEmpty(appVersion)) {
            map.put(APPVERSION, appVersion);
        }
        if (!TextUtils.isEmpty(sp)) {
            map.put(SP, sp);
        }
        return map;
    }

    @Override
    public void handleResult(Context context, String result) {
        CustomLog.i(TAG, "handleResult = " + result);
        if (null != callBack){
            callBack.handleResult(context, result);
        }
    }

    @Override
    public String getSignParams(Context context) {
        return null;
    }

    @Override
    public String getAuthType() {
        return DfineAction.authType_AUTO;
    }
}
