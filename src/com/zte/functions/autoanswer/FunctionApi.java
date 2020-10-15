package com.zte.functions.autoanswer;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;

import com.zte.common.SystemUtil;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;

/**
 * 自动接听功能Api
 * <p/>
 * 1,上报自动接听失败接口 {@link #reportNotAnswered(String)}
 *
 * @author Jiangxuewu
 */
public class FunctionApi {

    protected static final String TAG = FunctionApi.class.getSimpleName();
    protected static final String MOTHOD = "/config/report_uncalled";

    /**
     * 品牌
     */
    protected static final String BRAND_ID = "brand_id";
    /**
     * UID
     */
    protected static final String UID = "uid";
    /**
     * 手机硬件地址
     */
    protected static final String DEVICE_ID = "device_id";
    /**
     * 国际移动设备标识
     */
    protected static final String IMEI = "imei";
    /**
     * 国际移动用户识别码
     */
    protected static final String IMSI = "imsi";
    /**
     * 手机型号
     */
    protected static final String PHONE_TYPE = "phone_type";
    /**
     * 手机系统
     */
    protected static final String PV = "pv";
    /**
     * 手机系统版本号
     */
    protected static final String SYS_VERSION = "sys_version";
    /**
     * 是否开启root权限, 0未开启，1开启
     */
    protected static final String IS_ROOT = "is_root";
    /**
     * 客户端版本号
     */
    protected static final String CLIENT_VERSION = "client_version";
    /**
     * 扩展字段
     */
    protected static final String EXT = "ext";

    protected Context mContext;

    public FunctionApi(Context context) {
        mContext = context;
    }

    /**
     * 上报不能自动接听的手机型号
     * <p/>
     * 直接调用即可，无需新建线程，无需处理上传结果。
     * <p/>
     * 上传内容 {@link #BRAND_ID}, {@link #UID}, {@link #DEVICE_ID }, {@link #IMEI},
     * {@link #IMSI}, {@link #PHONE_TYPE}, {@link #PV}, {@link #SYS_VERSION},
     * {@link #IS_ROOT}, {@link #CLIENT_VERSION}, {@link #EXT}
     *
     * @param ext 拓展字段
     * @version:2015年1月16日
     * @author:Jiangxuewu
     */
    public void reportNotAnswered(final String ext) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                String authType = DfineAction.authType_UID;
//
//                TreeMap<String, String> treeMap = new TreeMap<String, String>();
//                String macAddr = SystemUtil.getLocalMacAddress(mContext);
//                String imei = SystemUtil.getIMEI(mContext);
//                String imsi = SystemUtil.getIMSI(mContext);
//                String model = SystemUtil.getModel();
//                String osVersion = SystemUtil.getOSVersion();
//                String apkVersion = SystemUtil.getApkVersion(mContext);
//
//                treeMap.put(BRAND_ID, DfineAction.brandid);
//                treeMap.put(DEVICE_ID, null == macAddr ? "" : macAddr);
//                treeMap.put(IMEI, null == imei ? "" : imei);
//                treeMap.put(FunctionApi.IMSI, null == imsi ? "" : imsi);
//                treeMap.put(PHONE_TYPE, null == model ? "" : model);
//                treeMap.put(SYS_VERSION, null == osVersion ? "" : osVersion);
//                treeMap.put(IS_ROOT, SystemUtil.isRoot() ? "1" : "0");
//                treeMap.put(CLIENT_VERSION, null == apkVersion ? "" : apkVersion);
//                treeMap.put(EXT, TextUtils.isEmpty(ext) ? "" : ext);

//                String params = CoreBusiness.getInstance().returnParamStr(mContext, treeMap, authType);
//
//                String realUrl = VsHttpTools.getInstance(mContext).getUri_prefix() + "/" + DfineAction.uri_verson + "/" + DfineAction.brandid + MOTHOD;
//                final String uri = realUrl + "?" + params;

//                JSONObject result = VsHttpTools.getInstance(mContext).doGetMethod(uri, treeMap, realUrl, authType);
//                if (null != result) {
//                    CustomLog.i(TAG, "reportNotAnswered() finish.");
//                }
//            }
//        };
//        // 使用线程池进行管理
//        GlobalVariables.fixedThreadPool.execute(runnable);
    }

    private String enmurParse(TreeMap<String, String> map) {
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

}
