package com.weiwei.netphone.data.process;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.hwtx.dududh.BuildConfig;
import com.hwtx.dududh.R;
import com.rc.rc.Rc;
import com.weiwei.base.bakcontact.ContactSync;
import com.weiwei.base.common.BaseRunable;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.FileUtils;
import com.weiwei.base.common.VsJsonTool;
import com.weiwei.base.common.VsTestAccessPoint;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalFuntion;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 核心服务：主要是业务处理，启动一个线程直接调用GlobalFuntion和common中的类
 */
@SuppressLint("SimpleDateFormat")
public class CoreBusiness {
    private String TAG = CoreBusiness.class.getSimpleName();
    private static CoreBusiness mInstance;
    public static boolean isLoadVsContact = false;// 是否正在加载Vs好友联系人
    public static boolean isLoadVsContactOver = false;// 加载vs好友是否完毕

    /**
     * 单例模式中获取唯一的实例
     *
     * @return
     */
    public static CoreBusiness getInstance() {
        if (null == mInstance) {
            mInstance = new CoreBusiness();
        }
        return mInstance;
    }

    private CoreBusiness() {
    }

    /**
     * 启动多点接入线程 (用静态配置来判断多点接入)
     *
     * @param context
     */
    public void startTestPointThread(final Context context) {
        // 使用runable实现多线程
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                new VsTestAccessPoint().TestAccessPoint(context);
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    /**
     * 删除旧的Apk文件
     *
     * @param urlFile
     * @param context
     * @param url
     */
    public void startCallDownloadThread(final String urlFile, final Context context, final String url) {
        // 使用runnable实现多线程
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                if (TextUtils.isEmpty(url)) {// 如果url为空，表示不用升级，就删除以前下载过的apk文件
                    FileUtils.deleteFile(VsUserConfig.getDataString(context, VsUserConfig.JKey_UPDATE_APK_FILE_PATH));
                }
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    /**
     * 回调线程。专门用户拉新活动回调用
     *
     * @param context
     * @param url
     * @param params
     */
    public void startCallBackThread(final Context context, final String url, final String params) {
        // 使用runable实现多线程
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                // 记录线程id
                VsHttpTools.getInstance(context).sendPostRequest(url, context, params);
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    /**
     * 非网关接口联网请求
     *
     * @param context
     * @param url
     * @param paramStr
     * @param actionName
     */
    public void startOtherHttpThread(final Context context, final String url, final String paramStr, final String actionName) {
        // 使用runable实现多线程
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                // 记录线程id
                JSONObject root = null;
                try {
                    if (GlobalVariables.netmode == 0) {
                        return;
                    }
                    if (actionName.equals(GlobalVariables.actionReportList)) {
                        root = VsHttpTools.getInstance(context).sendPostRequest(url, context, paramStr);
                    } else {
                        root = VsHttpTools.getInstance(context).doGetMethod(url + "?" + paramStr, null, null, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (actionName.equals(GlobalVariables.actionReportControl)) {
                        reportControl(context, root);
                    } else {

                    }
                }
            }

            public void reportControl(Context mContext, JSONObject root) {
                try {
                    if (root != null && root.toString().length() > 0) {
                        String result = root.getString("result");
                        if (result != null && result.equals("0")) {
                            String size = root.getString("size");
                            if (size != null) {
                                VsUserConfig.setData(mContext, VsUserConfig.JKEY_REPORT_CONFIGSIZE, size);
                            }
                            String flag = root.getString("upflag");
                            if (flag != null) {
                                VsUserConfig.setData(mContext, VsUserConfig.JKEY_REPORT_CONFIGFLAG, flag);
                            }
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_GET_ERROR_LOG, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    /**
     * 启动一个新的线程去运行http网关访问
     *
     * @param context    :启动线程的Context
     * @param treeMap    :传入的参数
     * @param actionName :传入的action name收到数据后发送广播给activity使用
     */
    public void startThread(final Context context, final String target, final String authType, final TreeMap<String, String> treeMap, final String actionName) {
        // 使用runable实现多线程
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                // while (VsTestAccessPoint.isSuccess == null) {
                // try {
                // Thread.sleep(1000);
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                // }
                // 记录线程id
                GlobalFuntion.PrintValue("启动新的一个线程", "");
                if (GlobalVariables.INTERFACE_CONTACT_BACKUP.equals(target)) {
                    TreeMap<String, String> bizparams = new TreeMap<String, String>();
                    // 获取联系人信息放到线程中来做
                    bizparams.put("vcard_contacts", ContactSync.getInstance().getBakContactData(context));
                    httpAccess(context, actionName, target, authType, bizparams);
                } else {
                    httpAccess(context, actionName, target, authType, treeMap);
                }
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    /**
     * @param mContext
     * @param actionName 传入的action name收到数据后发送广播给activity使用
     * @param target     获取数据地址
     * @param authType
     * @param treeMap    参数
     * @return
     */
    public Object httpAccess(Context mContext, String actionName, String target, String authType, TreeMap<String, String> treeMap) {
        JSONObject root = null;
        String json = DfineAction.defaultResult;
        String RealUrl = null;
        String paramStr = null;
        // 判断是否登陆，若没有登陆则不需要发请求，除了注册，登陆，静态配置，忘记密码，验证码；
        if (!(actionName.equals(GlobalVariables.actionFirUpdate) | actionName.equals(VsUserConfig.VS_ACTION_CHECK_USER) | actionName.equals(VsCoreService.VS_ACTION_LOGIN) |
                actionName.equals(GlobalVariables.actionDefaultConfig) | actionName.equals(VsUserConfig.VS_ACTION_SET_PASSWORD) | actionName.equals(VsUserConfig
                .VS_ACTION_RESETPWD_CODE) | actionName.equals(VsUserConfig.VS_ACTION_GET_CODE))) {
            if (!VsUtil.checkHasAccount(mContext)) {
                return null;
            }
        }
        try {
            if (GlobalVariables.netmode == 0) {
                return json;
            }
            // TODO Json
            if (actionName.equals(GlobalVariables.actionFirUpdate)) {
                RealUrl = "http://api.fir.im/apps/latest" + target;
            } else {
                // RealUrl = VsHttpTools.getInstance(mContext).getUri_prefix()
                // + "/" + DfineAction.uri_verson + "/" + DfineAction.brandid
                // + target;
                RealUrl = VsHttpTools.getInstance(mContext).getUri_prefix() + "/" + "8.0.1" + "/" + DfineAction.brand_id + target;
            }
            if (target.equals(GlobalVariables.INTERFACE_CONFIG)){
                CustomLog.e("vsdeb---", "url---" + RealUrl);
            }
            CustomLog.i("vsdebug", "url---" + RealUrl);


            if (actionName.equals(GlobalVariables.actionFirUpdate)) {
                paramStr = enmurParse(treeMap);
            } else {
                returnParamStr(mContext, treeMap, authType);
            }
            CustomLog.i("vsdebug", "paramStr---" + paramStr);


            if (actionName.equals(GlobalVariables.actionBackUp) || actionName.equals(GlobalVariables.actionCount) || actionName.equals(GlobalVariables.actionGetVsFriend)) {
                root = VsHttpTools.getInstance(mContext).sendPostRequest(RealUrl, mContext, paramStr);
                CustomLog.i("vsdebug", "RealUrl" + RealUrl + ",root=" + root);
            } else {
                // byte[] jsonData = paramStr.getBytes("utf-8");
                if (actionName.equals(GlobalVariables.actionFirUpdate)) {
                    root = VsHttpTools.getInstance(mContext).doGetMethod(RealUrl + "?" + paramStr, treeMap, RealUrl, authType);
                } else {
                    root = VsHttpTools.getInstance(mContext).sendPostRequesthttp(RealUrl, mContext, treeMap);
                }
            }
            //用于断点拦截相应的接口数据
            if(RealUrl.contains("/action/call_ready")){
                LogUtils.e("就是这里了");
            }
            if (root != null && root.length() > 0) json = root.toString();
            CustomLog.d("vsdebug", "json: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (actionName.equals(GlobalVariables.actionCount)) {
            } else if (actionName.equals(GlobalVariables.actionGetNumber)) {
                getSmPhoneNumber(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionRecordInstall)) {
                recordInstall(mContext, root, authType);
            } else if (actionName.equals(GlobalVariables.actionMoBind)) {
                return moBind(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionMoReg)) {
                return moReg(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionTempLateConfig)) {
                handleTemplateConfigInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionDefaultConfig)) {
                handleDefaultConfigInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionFirUpdate)) {
                FirUpdateInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionexchange)) {
                saveCardNoInfo(root, mContext);
            } else if (actionName.equals(GlobalVariables.actionupdate)) {
                saveUpgradeInfo(root, mContext);
            } else if (actionName.equals(GlobalVariables.actionGoodsConfig)) {
                CustomLog.i("lte", "action----" + GlobalVariables.actionGoodsConfig);
                handleGoodsConfigInfo(mContext, root, actionName);
            } else if (actionName.equals(GlobalVariables.actionHeartBeat)) {
                CustomLog.i("vsdebug", "action----heart---beat");
                setReportActiveTime(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionAdsConfig)) {
                // 广告
                handleAdConfigInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetOK)) {
                getOKInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionSignConfig)) {
                setSignInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetMyBank)) {
                getBankInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.adActionCount)) {
                // 统计用户点击广告行为
            } else if (actionName.equals(GlobalVariables.actionGiftPkgTime)) {
                // 绑定获取30分钟
                getRegisterGiftInfo(root, mContext);
            } else if (actionName.equals(GlobalVariables.VS_ACTION_CHECK_CONTACTS)) {
                setBakContactInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionReportPushInfo)) {
                setBakContactInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetmyInfo)) {
                getMyInfoText(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionPushmyInfo)) {
                pushMyInfo(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetMyCallMoney)) {
                getMyCallMoney(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionShareConfig)) {
                getMyshare(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetMyCallLog)) {
                getMyCallLog(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetMyRedLog)) {
                getMyRedLog(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetQrcode)) {
                getMyQrCode(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionSeaRchtaocan)) {
                getMyTaocan(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetMyRedMoney)) {
                getMyRedMoney(mContext, root);
            } else if (actionName.equals(GlobalVariables.actionGetToken)) {// 获取token
                CustomLog.i("vsdebugtoken", "vsdebugtokenjson" + json);
                if (root != null && !"".equals(root.toString()) && root.toString().length() > 1) {
                    if ("0".equals(VsJsonTool.GetStringFromJSON(root, "result"))) {
                        // 保存token
                        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN, VsJsonTool.GetStringFromJSON(root, "token"));
                        // 拨打组件建立连接
                        // mContext.sendBroadcast(new Intent(
                        // UIDfineAction.ACTION_LOGIN).putExtra("sid_pwd",
                        // VsJsonTool.GetStringFromJSON(root, "token")));
                        CustomLog.i("vsdebugtoken", "token---" + VsJsonTool.GetStringFromJSON(root, "token"));
                    } else {
                        // Toast.makeText(mContext, "获取Token失败！", 0).show();
                        CustomLog.i("vsdebugtoken", "获取Token失败！");
                        // 获取token失败重链
                        // KcBizUtil.getInstance().getToken(mContext);
                    }
                }
            } else if (actionName.equals(GlobalVariables.actionGetVsFriend)) {
                CustomLog.i("actionGetVsFriend", "friend-----" + json);
                if (root != null && !"".equals(root.toString()) && root.toString().length() > 1) {

                    if ("0".equals(VsJsonTool.GetStringFromJSON(root, "result"))) {
                        // 修改联系人信息VsJsonTool.GetStringFromJSON(root, "numbers")
                        VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETVSUSERINFO, VsJsonTool.GetStringFromJSON(root, "numbers"));
                        changeContactsData(mContext, VsJsonTool.GetStringFromJSON(root, "numbers"));
                        VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETVSUSERINFO_FLAG, VsJsonTool.GetStringFromJSON(root, "flag"));
                    } else {
                        // VsUserConfig.setData(mContext,
                        // VsUserConfig.JKEY_GETVSUSERINFO,
                        // VsJsonTool.GetStringFromJSON(root, "numbers"));
                        changeContactsData(mContext, VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_GETVSUSERINFO));
                        CustomLog.i("actionGetVsFriend", "获取VS好友失败！");
                        mContext.sendBroadcast(new Intent(VsUserConfig.JKey_GET_VSUSER_FAIL));
                    }
                }
                // 没有网络等情况下
                else {
                    changeContactsData(mContext, VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_GETVSUSERINFO));
                    CustomLog.i("actionGetVsFriend", "获取VS好友失败！");
                }
            } else {
                GlobalFuntion.SendBroadcastMsg(mContext, actionName, json);
            }
        }
        return null;
    }

    private void saveCardNoInfo(JSONObject root, Context mContext) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            if (root.getInt("code") != 0) {
                return;
            }
            JSONArray data = root.getJSONArray("data");
            Intent intent = new Intent(GlobalVariables.actionexchange);
            intent.putExtra("data", data.toString());
            mContext.sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBankInfo(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            if (root.getInt("code") != 0) {
                return;
            }
            JSONObject data = root.getJSONObject("data");
            Intent intent = new Intent(VsUserConfig.JKey_GET_MY_BANK);
            intent.putExtra("data", data.toString());
            mContext.sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getOKInfo(Context mContext, JSONObject root) {
        Log.e(TAG, "getOKInfo");
        if (root == null || root.length() == 0) {
            return;
        }
        // {"msg":"用户账号被冻结","code":1106}
        try {
            if (root.getInt("code") == 0) {
                JSONObject data = root.getJSONObject("data");
                Intent intent = new Intent(GlobalVariables.actionGetOK);
                intent.putExtra("data", data.toString());
                mContext.sendBroadcast(intent);
            } else {
                String data = root.getString("msg");
                Intent intent = new Intent(GlobalVariables.actionGetOK);
                intent.putExtra("data", data.toString());
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 签到说明
     *
     * @param mContext
     * @param root
     */
    private void setSignInfo(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            if (root.getInt("result") != 0) {
                return;
            }
            JSONObject checkin = root.getJSONObject("checkin");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_header, checkin.getString("title"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_sign_explain, checkin.getString("tips"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广告
     *
     * @Title: handleAdConfigInfo @Description: @param @param mContext @param @param
     * root 设定文件 @return void 返回类型 @throws
     */
    private void handleAdConfigInfo(Context mContext, JSONObject root) {
        try {
            if (root == null || root.getInt("code") == 101) return;
            if (root.getInt("code") == 0) {
                try {
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_APPSERVER_AD_CONFIG_FLAG, root.getString("flag"));

                    JSONObject data = root.getJSONObject("data");
                    // VsUserConfig.setData(mContext,
                    // VsUserConfig.JKEY_AD_CONFIG_000001,
                    // VsJsonTool.GetStringFromJSON(data, "000001"));
                    // VsUserConfig.setData(mContext,
                    // VsUserConfig.JKEY_AD_CONFIG_300001,
                    // VsJsonTool.GetStringFromJSON(data, "300001"));
                    // VsUserConfig.setData(mContext,
                    // VsUserConfig.JKEY_AD_CONFIG_300002,
                    // VsJsonTool.GetStringFromJSON(data, "300002"));
                    // VsUserConfig.setData(mContext,
                    // VsUserConfig.JKEY_AD_CONFIG_400501,
                    // VsJsonTool.GetStringFromJSON(data, "400501"));

                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_0001, VsJsonTool.GetStringFromJSON(data, "0001"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_1001, VsJsonTool.GetStringFromJSON(data, "1001"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_3001, VsJsonTool.GetStringFromJSON(data, "3001"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_3002, VsJsonTool.GetStringFromJSON(data, "3002"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_4001, VsJsonTool.GetStringFromJSON(data, "4001"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_4002, VsJsonTool.GetStringFromJSON(data, "4002"));

                    VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CONFIG_4003, VsJsonTool.GetStringFromJSON(data, "4003"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

				/*
                 * SharedPreferences settings =
				 * context.getSharedPreferences(KcUserConfig.PREFS_NAME, 0);
				 * SharedPreferences.Editor editor = settings.edit();
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_300001,
				 * KcJsonTool.GetStringFromJSON(data, "300001"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_000001,
				 * KcJsonTool.GetStringFromJSON(data, "000001"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_200001,
				 * KcJsonTool.GetStringFromJSON(data, "200001"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_100001,
				 * KcJsonTool.GetStringFromJSON(data, "100001"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_300002,
				 * KcJsonTool.GetStringFromJSON(data, "300002"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_300201,
				 * KcJsonTool.GetStringFromJSON(data, "300201"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_300601,
				 * KcJsonTool.GetStringFromJSON(data, "300601"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_301101,
				 * KcJsonTool.GetStringFromJSON(data, "301101"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_301601,
				 * KcJsonTool.GetStringFromJSON(data, "301601"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_303401,
				 * KcJsonTool.GetStringFromJSON(data, "303401"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_303801,
				 * KcJsonTool.GetStringFromJSON(data, "303801"));
				 * editor.putString(KcUserConfig.JKEY_AD_CONFIG_303701,
				 * KcJsonTool.GetStringFromJSON(data, "303701")); editor.commit();
				 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param mContext
     * @param numbers
     * @instruction在联系人信息中加入VS好友
     * @author Sundy 兴
     * @version 创建时间：2014-12-16 下午05:24:15
     */
    public void changeContactsData(Context mContext, String numbers) {
        JSONObject jsonObject = null;
        try {
            CustomLog.i(TAG, "changeContactsData(), numbers is " + numbers);

            jsonObject = new JSONObject(numbers);
        } catch (JSONException e) {
            isLoadVsContact = false;
            CustomLog.e(TAG, "changeContactsData, error is " + e);
        }
        isLoadVsContact = true;
        isLoadVsContactOver = false;
        String phone;
        VsPhoneCallHistory.VSCONTACTLIST.clear();
        if (!VsPhoneCallHistory.isloadContact && VsPhoneCallHistory.CONTACTLIST.size() > 0) {// 判断是否有联系人
            for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                item.mContactUid.clear();// 确保 uid跟phone一一对应关系.
                for (int j = 0; j < item.phoneNumList.size(); j++) {
                    phone = VsUtil.filterPhoneNumber(item.phoneNumList.get(j));
                    if (numbers.contains(phone)) {
                        item.isVsUser = true;// 设置为VS好友
                        item.ISVsPhone.add(true);
                        String param = null;
                        if (null != jsonObject) {
                            try {
                                param = jsonObject.getString(phone);
                            } catch (JSONException ignored) {
                            }
                        }
                        if (TextUtils.isEmpty(param)) {
                            item.mContactCliendid.add("");
                            item.mContactUid.add("");
                            continue;
                        }
                        CustomLog.i(TAG, "changeContactsData(), param is " + param);
                        JSONObject jsonObject2 = null;
                        try {
                            jsonObject2 = new JSONObject(param);
                        } catch (JSONException ignored) {
                        }

                        if (null == jsonObject2) {
                            item.mContactCliendid.add("");
                            item.mContactUid.add("");
                            continue;
                        }

                        String uidString = null;
                        try {
                            uidString = jsonObject2.getString("user_id");
                        } catch (JSONException ignored) {
                        }
                        String clientid = null;
                        try {
                            clientid = jsonObject2.getString("client_id");
                        } catch (JSONException ignored) {
                        }
                        item.mContactCliendid.add(clientid);
                        item.mContactUid.add(uidString);
                        if (!VsPhoneCallHistory.VSCONTACTLIST.contains(item)) {
                            VsPhoneCallHistory.VSCONTACTLIST.add(item);// 加入到vs用户列表中
                        }
                    } else {
                        item.ISVsPhone.add(false);
                        item.mContactCliendid.add("");
                        item.mContactUid.add("");
                    }
                }
            }
        }
        CustomLog.i("actionGetVsFriend", "好友=" + VsPhoneCallHistory.VSCONTACTLIST.size());

        if (null != VsPhoneCallHistory.IMCONTACTLIST) {
            VsPhoneCallHistory.IMCONTACTLIST.clear();
            VsPhoneCallHistory.IMCONTACTLIST = null;
        }
        VsPhoneCallHistory.IMCONTACTLIST = Collections.synchronizedList(new ArrayList<VsContactItem>(VsPhoneCallHistory.VSCONTACTLIST));

        Intent intent = new Intent(VsUserConfig.JKey_GET_VSUSER_OK);
        intent.setPackage(mContext.getPackageName());
        mContext.sendBroadcast(intent);
        // isLoadVsContactOver = VsPhoneCallHistory.IMCONTACTLIST.size() >=
        // (null == jsonObject? 0 :jsonObject.length());
        isLoadVsContactOver = true;
        CustomLog.i(TAG, "isLoadVsContactOver = " + isLoadVsContactOver + ", VsPhoneCallHistory.IMCONTACTLIST.size = " + VsPhoneCallHistory.IMCONTACTLIST.size() + ", " +
                "VsPhoneCallHistory.VSCONTACTLIST.size = " + VsPhoneCallHistory.VSCONTACTLIST.size());

        isLoadVsContact = false;
    }

    /**
     * 临时解析返回字符串
     *
     * @param obj
     * @return
     */
    public String[] getJsonString(JSONObject obj) {
        String[] result = new String[2];
        try {
            String str = obj.getString("ipaddr").toString();
            String[] array = str.split("&");
            if (array.length > 0) {
                result[0] = array[0].split("=")[1];
                result[1] = array[3].substring(6, array[3].length());
                return result;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void getMyRedLog(Context mContext, JSONObject root) {
        try {
            // amount":20000,"id":5,"action":"split","remark":"18576477213用户充值10.00元","app_id":"wind","
            // +
            // ""uid":"1234567","ctime":"2016-03-08
            // 11:38:00","order_no":"10402016030720411019072343466"
            if (root == null || root.length() == 0) {
                return;
            }
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (retStr.equals("success")) {
                JSONObject js = root.getJSONObject("data");

                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_RED_LOG);
                intent.putExtra("list", js.toString());
                mContext.sendBroadcast(intent);

            }
            if (code.equals("1002")) {
                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_RED_LOG);
                intent.putExtra("list", retStr);
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 拉取系统公告信息
    private void getNotice(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (code.equals("0")) {
                JSONObject js = root.getJSONObject("data");
                Intent intent = new Intent(GlobalVariables.actionSysMsg);
                intent.putExtra("list", js.toString());

                mContext.sendBroadcast(intent);

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // VsUserConfig.setData(mContext, VsUserConfig.JKey_Announcement,
        // cateConfig.getJSONObject("announcement").toString());

    }

    // 提取红包结果
    private void getMyRedMoney(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            // if (code.equals("0")) {
            // JSONObject js = root.getJSONObject("data");
//            if(retStr.contains("失败")){
//                retStr = "您的账户已过期提现失败";
//            }
            Intent intent = new Intent(VsUserConfig.JKey_GET_MY_REDMONEY);
            intent.putExtra("list", retStr);
            mContext.sendBroadcast(intent);

            // }else {
            // Intent intent = new Intent(VsUserConfig.JKey_GET_MY_REDMONEY);
            // intent.putExtra("list", retStr);
            // mContext.sendBroadcast(intent);
            // }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 查询套餐
    private void getMyTaocan(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (retStr.equals("success")) {
                JSONObject js = root.getJSONObject("data");

                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_TAOCAN_LOG);
                intent.putExtra("list", js.toString());
                mContext.sendBroadcast(intent);

            }
            if (code.equals("1002")) {
                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_TAOCAN_LOG);
                intent.putExtra("list", retStr);
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 扫码绑定
    private void getMyQrCode(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            Intent intent = new Intent("getQrCode");

            if (root == null || root.length() == 0) {
                intent.putExtra("getQrCode", "-1");
            }

            if (retStr.equals("success")) {

                intent.putExtra("getQrCode", code);
                mContext.sendBroadcast(intent);

            }

            if (code.equals("1610")) {
                intent.putExtra("getQrCode", retStr);
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void getMyCallLog(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (retStr.equals("success")) {
                JSONObject js = root.getJSONObject("data");

                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_CALL_LOG);
                intent.putExtra("list", js.toString());
                mContext.sendBroadcast(intent);

            }
            if (code.equals("1002")) {
                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_CALL_LOG);
                intent.putExtra("list", retStr);
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getMyshare(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (retStr.equals("success")) {
                JSONObject js = root.getJSONObject("data").getJSONObject("sms");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_GET_MY_SHARE, js.getString("content"));
                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_SHARE_BRO);
                intent.putExtra("data", js.getString("content"));
                mContext.sendBroadcast(intent);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 拉取话费流水信息
     */
    private void getMyCallMoney(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) {
            return;
        }
        try {
            String retStr = root.getString("msg");
            String code = root.getString("code");
            if (retStr.equals("success")) {
                JSONObject js = root.getJSONObject("data");

                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_CALL_MONEY);
                intent.putExtra("list", js.toString());
                mContext.sendBroadcast(intent);

            }
            if (code.equals("1002")) {
                Intent intent = new Intent(VsUserConfig.JKey_GET_MY_CALL_MONEY);
                intent.putExtra("list", retStr);
                mContext.sendBroadcast(intent);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 上传个人信息
     */
    private void pushMyInfo(Context mContext, JSONObject root) {
        Intent intent = new Intent(DfineAction.ACTION_PUSHINFOSUCCESS);
        if (root == null || root.length() == 0) {

            return;
        }
        try {
            String retStr = root.getString("msg");
            if ("成功".equals(retStr)) {
                intent.putExtra("result", "0");
            } else {
                intent.putExtra("result", retStr);
            }
            mContext.sendBroadcast(intent);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 拉取个人信息
     */
    private void getMyInfoText(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) return;
        String retStr;
        try {
            retStr = root.getString("msg");
            if ("success".equals(retStr)) {
                // {"msg":"success","code":0,"data":{"icon":"","nick":"","mtime":"","email":"","app_id":"wind","uid":"2343566","gender":"F","ctime":"","birthday":""}}

                // String province = VsUserConfig.getDataString(mContext,
                // VsUserConfig.JKey_MyInfo_Province);
                // String city = VsUserConfig.getDataString(mContext,
                // VsUserConfig.JKey_MyInfo_City);

                JSONObject js = root.getJSONObject("data");
                // VsUserConfig.setData(mContext,
                // VsUserConfig.JKey_MyInfo_photo, js.getString("icon"));
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Nickname, js.getString("nick"));
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_MailBox, js.getString("email"));
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Birth, js.getString("birthday"));
                if (js.getString("gender").equals("F")) {
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Gender, "女");
                } else {
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Gender, "男");
                }
                Intent intent = new Intent(VsCoreService.VS_ACTION_NICK);
                intent.putExtra("nick", js.getString("nick"));
                mContext.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 联系人备份信息
     *
     * @param mContext
     * @param root
     */
    private void setBakContactInfo(Context mContext, JSONObject root) {
        if (root == null || root.length() == 0) return;

        try {
            String retStr = root.getString("result");
            if ("0".equals(retStr)) {

                VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactServerNum, Integer.parseInt(root.getString("contactnum").trim()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 ");
                try {
                    Date backup_date = sdf.parse(VsJsonTool.GetStringFromJSON(root, "lastbackup"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactBakTime, formatter.format(backup_date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Date rew_backup_date = sdf.parse(VsJsonTool.GetStringFromJSON(root, "lastrenew"));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactRenewBakTime, formatter.format(rew_backup_date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 得到注册送30分钟信息方法
    public void getRegisterGiftInfo(JSONObject root, Context mContext) {
        try {
            if (root == null || root.length() == 0 || root.getInt("result") != 0) {
                return;
            }
            JSONObject reg_award_tip = root.getJSONObject("reg_award_tip");
            boolean reg_award_switch = reg_award_tip.getBoolean("reg_award_switch");
            if (reg_award_switch) {
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RegAwardTip, reg_award_tip.getString("info"));
                // 服务端返回的剩余套餐时间

                String time = reg_award_tip.getString("surplus");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RegSurplus, time);
                mContext.sendBroadcast(new Intent(DfineAction.ACTION_REGSENDMONEY));
                if (VsUserConfig.getDataLong(mContext, VsUserConfig.JKey_start_time) == 0) {
                    CustomLog.i("beifen", "jey_start_time===" + VsUserConfig.getDataLong(mContext, VsUserConfig.JKey_start_time));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_start_time, System.currentTimeMillis() / 1000 + Long.parseLong(time));
                }
            } else {
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RegAwardSwitch, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public void FirUpdateInfo(Context context, JSONObject root) {
        try {
            // JSONObject root = new
            // JSONObject(VsJsonTool.GetStringFromJSON(roots, "update"));
            CustomLog.i("update", "返回值：" + root);
            String version = root.getString("version");
            String install_url = root.getString("install_url");
            int newbuild = Integer.parseInt(version);
            int oldbuild = Integer.parseInt(VsUserConfig.getDataString(context, VsUserConfig.JKey_FIRVERSION));
            if (newbuild > oldbuild) {
                VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeUrl, install_url);
            } else {
                VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeUrl, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存在线升级 保存Json对象到本地
     *
     * @param root
     */
    public void saveUpgradeInfo(JSONObject root, Context context) {
        if (root == null || root.length() == 0) {
            return;
        }

        String retStr;
        try {
            retStr = root.getString("msg");
            String code = root.getString("code");
            if (code.equals("0")) {
                // "new_v": "1.1.4", "update_mode": 0, "pv": "iphone", "url":
                // "http://www.baidu.com", "app_id": "wind", "tips_gap": 5, "agent_id": "wind",
                // "desc": "升级测试"}
                JSONObject js = root.getJSONObject("data");
                String new_v = js.getString("new_v");
                String update_mode = js.getString("update_mode");
                String url = js.getString("url");
                String tips_gap = js.getString("tips_num");
                String desc = js.getString("desc");
                String pv = js.getString("pv");

                VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeUrl, url);
                VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeInfo, desc);
                VsUserConfig.setData(context, VsUserConfig.JKey_new_version, new_v);
                VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeMandatory, update_mode);
                VsUserConfig.setData(context, VsUserConfig.JKEY_UPGRADETIPSNUMBER, tips_gap);

                // Intent intent = new Intent(VsUserConfig.JKey_GET_MY_RED_LOG);
                // intent.putExtra("list", js.toString());
                // context.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // JSONObject root = new
        // JSONObject(VsJsonTool.GetStringFromJSON(roots, "update"));
        // CustomLog.i("update", "返回值：" + root);
        // String updateAddr = VsJsonTool.GetStringFromJSON(root,
        // "update_addr");
        //
        // String updateInfo = VsJsonTool.GetStringFromJSON(root,
        // "update_info");
        // String mandatory = VsJsonTool.GetStringFromJSON(root,
        // "mandatory");
        // String tipsNumber = VsJsonTool.GetStringFromJSON(root,
        // "tips_number");
        // String newVersion = VsJsonTool.GetStringFromJSON(root,
        // "new_version");
        //
        // VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeUrl,
        // updateAddr);
        // VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeInfo,
        // updateInfo);
        // VsUserConfig.setData(context, VsUserConfig.JKey_new_version,
        // newVersion);
        // VsUserConfig.setData(context, VsUserConfig.JKey_UpgradeMandatory,
        // mandatory);
        // VsUserConfig.setData(context,
        // VsUserConfig.JKEY_UPGRADETIPSNUMBER, tipsNumber);
        //
        // CustomLog.i("update", "获更新地址：" + updateAddr
        // + ", mandatory is " + mandatory
        // + ", new version is " + newVersion
        // + ", update info is " + updateInfo);
        //
        // startCallDownloadThread(DfineAction.updateFilePath, context,
        // updateAddr);

    }

    /**
     * 发送用户点击行为
     */
    public void sendClickStatistics(Context mContext, JSONObject root) {
        if (root != null && root.length() > 0) {
            try {
                // JSONObject root = new
                // JSONObject("{\"rn_list_num\":2,\"result\":0,\"reason\":\"success\"}");
                String retStr = root.getString("result");
                CustomLog.i(TAG, "发送用户点击行为返回json数据为==" + root.toString());
                if (retStr.equals("0")) {
                    CustomLog.i(TAG, "发送用户点击行为成功");
                } else {
                    CustomLog.i(TAG, "发送用户点击行为失败原因===" + (retStr.equals("1") ? "参数错误" : (retStr.equals("2") ? "记录到数据库失败" : "服务器内部错误" + retStr)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 设置显示的图片类型
     *
     * @param target
     * @return 1显示new 2显示点 3显示气泡数字
     */
    public String setMoreAppTipsImageType(String target) {
        if (target.equalsIgnoreCase("3034")) {
            return "2";
        } else if (target.equalsIgnoreCase("4002")) {
            return "3";
        }
        return "1";
    }

    /**
     * 上传活跃成功则修改上报日期
     */
    private void setReportActiveTime(Context mContext, JSONObject root) {
        if (root != null && root.length() > 0) {
            try {
                String retStr = root.getString("result");
                if (retStr.equals("0")) {
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_REPORT_ACTIVE_DAY, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 保存商品套餐和充值页面广告位信息
     *
     * @param mContext
     * @param root
     * @param actionName 用于更新界面。
     */
    private void handleGoodsConfigInfo(Context mContext, JSONObject root, String actionName) {
        if (root == null || root.length() == 0) return;
        try {
            // CustomLog.i("lte", root.toString());
            // CustomLog.i("lte", root.getInt("result") + "");
            if (!root.isNull("code") && root.getInt("code") == 0) {
                DfineAction.IsStartGoodsConfig = true;
                JSONObject data = root.getJSONObject("data");
                JSONArray data_list = data.getJSONArray("goods_list");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_NewGoodsInfo, String.valueOf(data_list));// 保存充值套餐信息

                // 动态更新充值套餐
                VsUtil.sendSpecialBroadcast(mContext, actionName);
                VsUtil.sendSpecialBroadcast(mContext, "actionGoodsConfigsec");
            } else {
                VsUserConfig.setData(mContext, VsUserConfig.JKey_NewGoodsInfo, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模版配置接口数据处理
     *
     * @param mContext
     * @param root
     */
    private void handleTemplateConfigInfo(Context mContext, JSONObject root) {
        try {
            if (root == null || root.length() == 0 || root.getInt("result") != 0) return;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return;
        }
        try {
            JSONObject serviceConfig = root.getJSONObject("wap_target_url");
            JSONObject account = serviceConfig.getJSONObject("wap_account_details");// 收支明细
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ACCOUNT_DETAILS, account.getString("url"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_APPSERVER_TEMPLATE_CONFIG_FLAG, root.getString("flag"));
            JSONObject calllog = serviceConfig.getJSONObject("wap_call_log");// 话单查询
            VsUserConfig.setData(mContext, VsUserConfig.JKey_CALL_LOG, calllog.getString("url"));
            JSONObject redpage = serviceConfig.getJSONObject("redPackage");// 红包列表
            VsUserConfig.setData(mContext, VsUserConfig.JKey_RED_PAGE, redpage.getString("url"));
            JSONObject vip_center = serviceConfig.getJSONObject("wap_vip_center");// vip会员中心
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_VIP_CENTER_URL, vip_center.getString("url"));

            JSONObject recomend = root.getJSONObject("recommend_url");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_INVITE, VsJsonTool.GetStringFromJSON(recomend, "sms"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_WEIBO_SHARE_CONTENT, VsJsonTool.GetStringFromJSON(recomend, "weibo"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_WEIXINQUAN_SHARE_CONTENT, VsJsonTool.GetStringFromJSON(recomend, "weixinquan"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_WEIXIN_SHARE_CONTENT, VsJsonTool.GetStringFromJSON(recomend, "weixin"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_QQDX_SHARE_CONTENT, VsJsonTool.GetStringFromJSON(recomend, "qq"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_DIECODE_SHARE_CONTENT, VsJsonTool.GetStringFromJSON(recomend, "die-code"));

            JSONObject recomend_img = root.getJSONObject("recommend_img");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_QQDX_SHARE_IMAGE_LOCAL_URL, VsJsonTool.GetStringFromJSON(recomend_img, "qq"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_WEIXIN_SHARE_IMAGE_LOCAL_URL, VsJsonTool.GetStringFromJSON(recomend_img, "weixin"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 上报安装量处理
     *
     * @param mContext
     * @param root
     * @param authType
     */
    private void recordInstall(Context mContext, JSONObject root, String authType) {
        if (root == null || root.length() == 0) return;
        try {
            if (root.getString("result").equals("0")) {
                if (authType.equals(DfineAction.authType_UID)) {// 有UID
                    // 安装成功标识已经上传
                    CustomLog.i("GDK", "有UID上报安装量成功");
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_RECORDINSTALL_WITH_UID, false);
                } else if (authType.equals(DfineAction.authType_Key)) {
                    CustomLog.i("GDK", "无UID上报安装量成功");
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_RECORDINSTALL_NO_UID, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存短信通道号码
     *
     * @param context
     * @param root
     */
    private void getSmPhoneNumber(Context context, JSONObject root) {
        try {
            if (root != null && root.length() > 0) {
                Object gObjtemp = root.get("result");
                int gresult = Integer.valueOf(gObjtemp.toString());
                switch (gresult) {
                    case 0:
                        gObjtemp = root.get("number");
                        if (gObjtemp != null) {
                            VsUserConfig.setData(context, VsUserConfig.JKEY_SENDNOTESERVICESPHONE, gObjtemp.toString());
                        }
                        VsUserConfig.setData(context, VsUserConfig.JKEY_REG_BIND_IDENTIFY, VsJsonTool.GetStringFromJSON(root, "identify"));
                        break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * mo注册处理
     *
     * @param context
     * @param root
     * @return
     */
    private int moReg(Context context, JSONObject root) {
        int retCode = -99;
        if (root != null && root.length() > 0) {
            Object gObjtemp = VsJsonTool.GetIntegerFromJSON(root, "result");
            GlobalFuntion.PrintValue("上行短信绑定返回码－－》", retCode);
            retCode = Integer.valueOf(gObjtemp.toString());

            switch (retCode) {
                case 0:
                    gObjtemp = VsJsonTool.GetStringFromJSON(root, "uid");

                    if (gObjtemp != null && gObjtemp.toString().length() > 0) {
                        VsUserConfig.setData(context, VsUserConfig.JKey_KcId, gObjtemp.toString());
                    }
                    gObjtemp = VsJsonTool.GetStringFromJSON(root, "passwd");
                    if (gObjtemp != null && gObjtemp.toString().length() > 0) {
                        VsUserConfig.setData(context, VsUserConfig.JKey_Password, gObjtemp.toString());
                    }
                    gObjtemp = VsJsonTool.GetStringFromJSON(root, "mobile");
                    if (gObjtemp != null && gObjtemp.toString().length() > 0) {
                        VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, gObjtemp.toString());
                        // 弹出对话框提示用户
                        context.sendBroadcast(new Intent(GlobalVariables.actionMORB));
                        // 发送登录成功广播
                        Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
                        intent2.putExtra("packname", context.getPackageName());
                        context.sendBroadcast(intent2);

                        Intent intent = new Intent(context, CustomDialogActivity.class);
                        intent.putExtra("messagetitle", context.getResources().getString(R.string.lb_alter));
                        intent.putExtra("messagebody", context.getResources().getString(R.string.mo_register_succ));
                        intent.putExtra("cancelButton", true);
                        intent.putExtra("messagelink", "0000");
                        intent.putExtra("messagelinktype", "in");
                        context.startActivity(intent);
                    }
            }
        }
        return retCode;
    }

    private int moBind(final Context mContext, JSONObject root) {
        int retCode = -99;
        if (root != null && root.length() > 0) {
            Object gObjtemp = VsJsonTool.GetIntegerFromJSON(root, "result");
            retCode = Integer.valueOf(gObjtemp.toString());

            switch (retCode) {
                case 0:
                    gObjtemp = VsJsonTool.GetStringFromJSON(root, "phone");
                    if (gObjtemp != null && gObjtemp.toString().length() > 0) {

                        VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, gObjtemp.toString());
                        // 弹出对话框提示用户
                        mContext.sendBroadcast(new Intent(GlobalVariables.actionMORB));
                        // 发送登录成功广播
                        Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
                        intent2.putExtra("packname", mContext.getPackageName());
                        mContext.sendBroadcast(intent2);

                        Intent intent = new Intent(mContext, CustomDialogActivity.class);
                        intent.putExtra("messagetitle", mContext.getResources().getString(R.string.lb_alter));
                        intent.putExtra("messagebody", mContext.getResources().getString(R.string.mo_bind_succ));
                        intent.putExtra("cancelButton", true);
                        intent.putExtra("messagelink", "0000");
                        intent.putExtra("messagelinktype", "in");
                        mContext.startActivity(intent);
                    } else {
                        retCode = -16;
                        // 弹出对话框提示用户

                    }
                    GlobalFuntion.PrintValue("上行短信绑定返回码－－》", retCode);
                    break;
                default:
                    GlobalFuntion.PrintValue("上行短信绑定返回码－－》", retCode);
                    break;
            }
        }
        return retCode;
    }

    /**
     * 是否需要改变签名并从新获取 备注：此方法只在报403错误的时候调用。 首先判断服务端返回的签名信息是否跟本地一致
     * 如果一致会登录一次。如果返回0则会在次调用一次当前请求。并且设置下一次请求不需要验证登录了。防止死循环 如果登录失败。则提醒用户重新登录
     */
    // private Object[] isNeedToChangeSign(Context mContext, JSONObject root,
    // String json) {
    // Object[] object = new Object[2];
    // if (KcUserConfig.getDataString(mContext,
    // KcUserConfig.JKey_SIGN_TK).equals(
    // KcJsonTool.GetStringFromJSON(root, "tk"))
    // && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_AN) ==
    // KcJsonTool.GetIntegerFromJSON(root,
    // "an")
    // && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_KN) ==
    // KcJsonTool.GetIntegerFromJSON(root,
    // "kn")) {
    //
    // if (KcUserConfig.getDataBoolean(mContext,
    // KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN, true)) {//
    // 需要进行sign验证。如果需要就进行登录
    // JSONObject obj = KcBizUtil.getInstance().Longin(
    // KcUserConfig.getDataString(mContext, KcUserConfig.JKey_KcId),
    // KcUserConfig.getDataString(mContext, KcUserConfig.JKey_Password),
    // mContext);
    // String retStr = KcJsonTool.GetStringFromJSON(obj, "result");
    // if (retStr.equals("0")) {
    // KcUserConfig.setData(mContext, KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN,
    // false);
    // object[0] = true;
    // object[1] = json;
    // return object;
    // } else {
    // json = retStr;
    // object[0] = false;
    // object[1] = json;
    // return object;
    // }
    // } else {
    // object[0] = false;
    // object[1] = json;
    // return object;
    // }
    // }
    // KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_TK,
    // KcJsonTool.GetStringFromJSON(root, "tk"));
    // KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_AN,
    // KcJsonTool.GetIntegerFromJSON(root, "an"));
    // KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_KN,
    // KcJsonTool.GetIntegerFromJSON(root, "kn"));
    // object[0] = true;
    // object[1] = json;
    // return object;
    // }

    /**
     * 返回URL连接地址
     *
     * @param mContext
     * @param authType  是加密类型
     */
    public void returnParamStr(Context mContext, TreeMap<String, String> treeMap, String authType) {
        Long curTime = System.currentTimeMillis() / 1000;
        String sign = "";
        treeMap.put("pv", DfineAction.pv);
        treeMap.put("v", BuildConfig.VERSION_NAME);
        treeMap.put("app_id", DfineAction.brandid);
        treeMap.put("agent_id", "");
        String token = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_LoginToken);
        treeMap.put("token", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_LoginToken));
        treeMap.put("ts", curTime.toString());
        sign = getSign(treeMap, DfineAction.key1, mContext);

        treeMap.put("sign", sign);
    }

    @SuppressWarnings("rawtypes")
    private String getSign(TreeMap<String, String> map, String pwd, Context context) {
        TreeMap<String, String> treemapsign = new TreeMap<String, String>();
        treemapsign.putAll(map);
        treemapsign.put("key", "($*key*$)");
        StringBuffer src = null;
        Iterator iter = treemapsign.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (src == null) {
                src = new StringBuffer();
                src.append(key).append("=").append(URLEncoder.encode(val.toString()));
            } else {
                src.append("&").append(key).append("=").append(val.toString());
            }
        }
        String sign = null;
        try {
            // sign = VsMd5.md5(src.toString());
            sign = Rc.getSign(src.toString(), 1);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 转换成URL
     *
     * @param ht
     * @version: 2012-7-24 上午10:16:02
     */
    @SuppressWarnings("deprecation")
    public String enmurParse(Hashtable<String, String> ht) {
        StringBuffer cmd = null;
        java.util.Enumeration<String> enu = ht.keys();
        while (enu.hasMoreElements()) {
            String k = enu.nextElement();
            if (cmd == null) {
                cmd = new StringBuffer();
                cmd.append(k).append("=").append(URLEncoder.encode(ht.get(k)));
            } else {
                cmd.append("&").append(k).append("=").append(URLEncoder.encode(ht.get(k)));
            }
        }
        return cmd.toString();
    }

    /**
     * 请求静态配置信息 （这个方法用于多点接入成功后获取默认配置信息）
     *
     * @param mContext
     * @param target
     * @param authType
     */
    public void defaultConfigAppMethod(final Context mContext, final String target, final TreeMap<String, String> treeMap, final String authType) {
        JSONObject root = null;
        String json = DfineAction.defaultResult;
        try {
            if (GlobalVariables.netmode == 0) {
                return;
            }

            // TODO Json
            String RealUrl = VsHttpTools.getInstance(mContext).getUri_prefix() + "/" + DfineAction.uri_verson + "/" + DfineAction.brandid + "/" + target;
            // String paramStr = returnParamStr(mContext, treeMap, authType);
            // CustomLog.i("DGK===", "SendRequestUrl=" + RealUrl + "?" +
            // paramStr);
            // byte[] jsonData = paramStr.getBytes("utf-8");
            // root = VsHttpTools.getInstance(mContext).doGetMethod(
            // RealUrl + "?" + paramStr, treeMap, RealUrl, authType);
            if (root != null) {
                json = root.toString();
            }
            CustomLog.i("DGK", "ServiceConfigReturn json=" + json);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final JSONObject tempRoot = root;
            handleDefaultConfigInfo(mContext, tempRoot);
        }
    }

    @SuppressWarnings({"rawtypes", "deprecation"})
    public String enmurParse(TreeMap<String, String> map) {
        StringBuffer src = null;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val.toString().length() > 0) {
                if (src == null) {
                    src = new StringBuffer();
                    src.append(key).append("=").append(URLEncoder.encode(val.toString()));
                } else {
                    src.append("&").append(key).append("=").append(URLEncoder.encode(val.toString()));
                }
            }
        }
        return src.toString();
    }

    public String enmurParsehttp(TreeMap<String, String> map) {
        StringBuffer src = null;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val.toString().length() > 0) {
                if (src == null) {
                    src = new StringBuffer();
                    src.append(key).append("=").append(URLEncoder.encode(val.toString()));
                } else {
                    src.append(key).append("=").append(URLEncoder.encode(val.toString()));
                }
            }
        }
        return src.toString();
    }

    public boolean udateInfo(final Context mContext, final String target, final TreeMap<String, String> treeMap, final String authType) {
        boolean bool = false;
        JSONObject root = null;
        String json = DfineAction.defaultResult;
        try {
            if (GlobalVariables.netmode == 0) {
                return bool;
            }

            // TODO Json
            String RealUrl = VsHttpTools.getInstance(mContext).getUri_prefix() + "/" + DfineAction.uri_verson + "/" + DfineAction.brandid + "/" + target;
            // String paramStr = returnParamStr(mContext, treeMap, authType);
            // CustomLog.i("DGK===", "SendRequestUrl=" + RealUrl + "?" +
            // paramStr);
            //
            // // byte[] jsonData = paramStr.getBytes("utf-8");
            // root = VsHttpTools.getInstance(mContext).doGetMethod(
            // RealUrl + "?" + paramStr, treeMap, RealUrl, authType);
            if (root.getString("result").equals("-99")) {
                bool = false;
            } else {
                bool = true;
            }

            if (root != null) {
                json = root.toString();
            }
            CustomLog.i("DGK===", "ServiceConfigReturn json=" + root);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bool) {
                saveUpgradeInfo(root, mContext);
            }
        }
        return bool;
    }

    private void handleDefaultConfigInfo(Context mContext, JSONObject root) {
        try {
            if (root == null || root.length() == 0) {
                return;
            }
            if (root.getInt("result") != 0) {
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETDEFAULTTIME, System.currentTimeMillis() / 1000);
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            // return;
        }

        JSONObject serviceConfig = null;
        try {
            serviceConfig = root.getJSONObject("data");
            // .getJSONObject("bootini");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_PayTypes, serviceConfig.getJSONArray("pay_type").toString());// 保存充值套餐信息
            JSONArray ja = serviceConfig.getJSONArray("pay_type");
            for (int i = 0; i < ja.length(); i++) {

                String str = ((JSONObject) ja.get(i)).getString("pay_type");
                if (str.equals("card_direct")) {
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_Card_Direct, true);
                    break;
                }
            }
            VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_types, serviceConfig.getJSONObject("dial_type").toString());// 拉取拨打设置信息

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 邀请人信息
        JSONObject invitInfo;
        try {
            if (serviceConfig != null) {
                invitInfo = serviceConfig.getJSONObject("single_init");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_Single_Init, VsJsonTool.GetStringFromJSON(invitInfo, "invited_flag"));
            }

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // 拉取版本信息
        JSONObject cateConfig = null;
        try {
            cateConfig = root.getJSONObject("data");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_cateTypes, cateConfig.getJSONObject("page_me").toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 拉取发现页面配置
        JSONObject foundConfig = null;
        try {
            foundConfig = root.getJSONObject("data");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_discoverTypes, foundConfig.getJSONObject("page_discover").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "serviceConfig = " + serviceConfig);
        try {
            // flag = 拉去信息的总标识
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG, root.getString("flag"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        VsUserConfig.setData(mContext, VsUserConfig.JKey_UpompWebsite, VsJsonTool.GetStringFromJSON(serviceConfig, "url_online"));

        try {
            JSONObject plugins = serviceConfig.getJSONObject("plugins");// 支付宝下载地址
            String location = plugins.getString("location");
            String alipay = plugins.getString("alipay");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumberUrl, plugins.getString("location"));// 归属地文件下载地址
            VsUserConfig.setData(mContext, VsUserConfig.JKey_AlipayDownUrl, plugins.getString("alipay"));
            startCallDownloadThread(DfineAction.mWldhFilePath, mContext, location);
            startCallDownloadThread(DfineAction.mWldhFilePath, mContext, alipay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 拉取直拨地址
        try {
            ArrayList<String> ips = new ArrayList<String>();
            JSONObject driect_call = serviceConfig.getJSONObject("direct_call");
            JSONArray driect_callAddrJsonArr = driect_call.getJSONArray("sip_addr");
            JSONArray driect_callPortJsonArr = driect_call.getJSONArray("sip_port");
            for (int i = 0; i < driect_callAddrJsonArr.length(); i++) {
                for (int j = 0; j < driect_callPortJsonArr.length(); j++) {
                    ips.add((InetAddress.getByName(driect_callAddrJsonArr.get(i).toString()).getHostAddress() + ":" + driect_callPortJsonArr.get(j).toString()).replaceAll(" ",
                            ""));
                }
            }

            VsUserConfig.setData(mContext, VsUserConfig.JKey_Driect_Call, ips.get(0));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {

            JSONObject kefuInfo = serviceConfig.getJSONObject("abount_us");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ServicePhone, VsJsonTool.GetStringFromJSON(kefuInfo, "ServiceTel"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ServiceQQ, VsJsonTool.GetStringFromJSON(kefuInfo, "ServiceQQ"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ServiceTime, VsJsonTool.GetStringFromJSON(kefuInfo, "ServiceTime"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_CopyRight, VsJsonTool.GetStringFromJSON(kefuInfo, "CopyRight"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_WechatPublic, VsJsonTool.GetStringFromJSON(kefuInfo, "WechatPublic"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_SinaWeibo, VsJsonTool.GetStringFromJSON(kefuInfo, "SinaWeibo"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_LogoName, VsJsonTool.GetStringFromJSON(kefuInfo, "LogoName"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 云之讯服务器地址
        try {
            CustomLog.i("GDK", "upass=" + serviceConfig.getJSONObject("ucpaas_url").getString("ip") + serviceConfig.getJSONObject("ucpaas_url").getString("port"));
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_UCPASS_URL, serviceConfig.getJSONObject("ucpaas_url").getString("ip"));
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_UCPASS_PORT, serviceConfig.getJSONObject("ucpaas_url").getString("port"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (null != serviceConfig) {
                String num = serviceConfig.getString("caller_display");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_NUM_PEOPLE, num);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            String url_help = serviceConfig.getString("url_help");// 帮助中心URL
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_HELP, url_help);

            String url_about_callback = serviceConfig.getString("url_about_callback");// 什么是回拨
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_CALLBACK, url_about_callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * try { String url_phone_wap = serviceConfig.getString("url_phone_wap");// 手机官网
		 * VsUserConfig.setData(mContext, VsUserConfig.JKey_phone_wap, url_phone_wap);
		 * String url_computer_wap = serviceConfig.getString("url_computer_wap");// 电脑官网
		 * VsUserConfig.setData(mContext, VsUserConfig.JKey_computer_wap,
		 * url_computer_wap); } catch (Exception e) { e.printStackTrace(); } try {
		 * String url_mal = serviceConfig.getString("url_mal");// 商城
		 * VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_MALL, url_mal);
		 *
		 * String url_service = serviceConfig.getString("url_service");//绑服务
		 * VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_SERVICE, url_service); }
		 * catch (Exception e) { e.printStackTrace(); } try { String url_service_tiao =
		 * serviceConfig.getString("url_service_tiao");// 服务条款
		 * VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_SERVICE_TIAO,
		 * url_service_tiao);
		 *
		 *
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
        try {
            String url_help = serviceConfig.getString("url_tariff");// 资费说明URL
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_TARIFF, url_help);

            // 一些需要从服务器获取的配置信息：充值说明URL
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_URL_CHARGE, VsJsonTool.GetStringFromJSON(serviceConfig, "url_charge"));

            // 一些需要从服务器获取的配置信息：赚话费—邀请好友
            JSONObject friendObj = serviceConfig.getJSONObject("recommend_award");
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_TITLE, friendObj.getString("title"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_PROMPT, friendObj.getString("prompt"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_ONSHOW, friendObj.getString("onshow"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_HEADLINE, friendObj.getJSONObject("content").getString("headline"));
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FRIEND_DETAIL, friendObj.getJSONObject("content").getString("detail"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // saveUpgradeInfo(root, mContext);
    }

    // private void pingIp(JSONObject driect_call, Context mContext) {
    // try {
    // ArrayList<String> ips = new ArrayList<String>();
    // JSONArray driect_callAddrJsonArr = driect_call.getJSONArray("sip_addr");
    // JSONArray driect_callPortJsonArr = driect_call.getJSONArray("sip_port");
    // for (int i = 0; i < driect_callAddrJsonArr.length(); i++) {
    // for (int j = 0; j < driect_callPortJsonArr.length(); j++) {
    // ips.add((InetAddress.getByName(driect_callAddrJsonArr.get(i).toString()).getHostAddress()
    // + ":" +
    // driect_callPortJsonArr
    // .get(j).toString()).replaceAll(" ", ""));
    // }
    // }
    // HashMap<Double, String> hm = PingData.privatePingHost(ips, 3000, true);
    // ArrayList<Map.Entry<Double, String>> ids = new
    // ArrayList<Map.Entry<Double, String>>(hm.entrySet());
    // // 排序
    // Collections.sort(ids, new Comparator<Map.Entry<Double, String>>() {
    // @Override
    // public int compare(Entry<Double, String> lhs, Entry<Double, String> rhs)
    // {
    // Double d = lhs.getKey() - rhs.getKey();
    // if (d > 0.00d) {
    // return 1;
    // } else if (d == 0.00d) {
    // return 0;
    // } else {
    // return -1;
    // }
    // }
    // });
    // StringBuffer strBuffer = new StringBuffer();
    // for (int i = 0; i < ids.size(); i++) {
    // strBuffer.append(ids.get(i).getValue()).append(",");
    // }
    // KcUserConfig.setData(mContext, KcUserConfig.JKEY_DriectCallAddrAndPort,
    // strBuffer.toString());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // public static void httpAccess(Context context, String actionName, String
    // target, String authType,
    // Hashtable<String, String> reqParam) {
    // // 传入的参数
    // // String
    // //
    // paramdata=String.format("account=%s&passwd=%s&ptype=%s&netmode=%s",GlobalVariables.uAccout,GlobalVariables.passwdMd5,null,null);
    // // url请求
    // HttpRequest reqHttp = new HttpRequest(); // 静态方法必须使用静态类
    // String recvdataStr = reqHttp.URLRequest(context, actionName, target,
    // authType, reqParam);
    // // 解析返回的结果
    // if (recvdataStr != "" && recvdataStr != null) {
    // try {
    // JSONObject root = new JSONObject(recvdataStr);
    // String result = KcJsonTool.GetStringFromJSON(root, "result");
    // String reasonStr = KcJsonTool.GetStringFromJSON(root, "reason");
    // // 发送广播到所要处理ui界面...
    // GlobalFuntion.SendBroadcastMsg(context, actionName, result, recvdataStr);
    // // 解析一些公用数据，并保存下来
    // if (result.equals("0")) {
    // if (actionName.equals(GlobalVariables.actionLogin)) { // 登录
    // // 获取所需的数据
    // GlobalVariables.uid = KcJsonTool.GetStringFromJSON(root, "uid");
    // GlobalVariables.uEmail = KcJsonTool.GetStringFromJSON(root, "email");
    // GlobalVariables.uFirstLogin = KcJsonTool.GetStringFromJSON(root,
    // "first");
    // // 保存uid,email等信息
    // // KcUserConfig.setData(context, KcUserConfig.JKEY_TCP_PORT,
    // GlobalVariables.uid);
    // // 登录成功后要进行tcp连接
    // KcTcpConnection tcp = new KcTcpConnection();
    // tcp.connection();
    // }
    // } else if (actionName.equals(GlobalVariables.actionBalance)) { // 查询余额
    // // 获取所需的数据
    // GlobalVariables.uBalance = KcJsonTool.GetStringFromJSON(root,
    // "basicbalance");
    // GlobalVariables.uValidate = KcJsonTool.GetStringFromJSON(root,
    // "valid_date");
    // GlobalVariables.mVipValidate = KcJsonTool.GetStringFromJSON(root,
    // "vip_validtime");
    // }
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // GlobalFuntion.PrintValue("json解析异常", e.toString());
    // }
    // }
    // }
}
