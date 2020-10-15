package com.weiwei.base.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import android.content.Context;
import android.content.Intent;

import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;

/**
 * 多点接入线程
 *
 * @author dell create at 2013-5-28下午04:25:10
 */
public class VsTestAccessPoint {

    public static char MSG_NONETWORK = 100;// 没有打开网络
    public static char MSG_NOLINKNETWORK = 200;// 连接网络失败
    public static char MSG_CONVERTNETWORK = 300;// 转换网络失败
    public static String isSuccess = null;

    private ArrayList<String> ipsList = new ArrayList<String>();
    private static String url_ports[] = null;
    private ArrayList<String> addrUrlsList = new ArrayList<String>();
    Hashtable<String, String> appParams = new Hashtable<String, String>();

    private Random r = new Random();

    public void TestAccessPoint(Context mContext) {

        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TestAccessPointState, 0);
        // 判断网络
        if (!VsNetWorkTools.isNetworkAvailable(mContext)) {
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_TestAccessPointState, MSG_NONETWORK);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            Intent intent = new Intent(mContext, CustomDialogActivity.class);
            intent.putExtra("business", "NetworkError");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            isSuccess = "no";
            return;
        }

        // 升级接口判断
//        if (VsUtil.getUpdate(mContext)) {
//            // 请求静态配置信息
//            appParams.clear();
//            appParams.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG));
//            CoreBusiness.getInstance().defaultConfigAppMethod(mContext, "config/app", appParams, DfineAction.authType_AUTO);
//            isSuccess = "no";
//            return;
//        } else 
        	if (VsUserConfig.getDataString(mContext, VsUserConfig.ADDR_IP_LIST).trim().length() < 5) {

            url_ports = DfineAction.RES.getStringArray(R.array.url_prots);  //获取默认ip列表
            setListData(ipsList, url_ports);
            if (getResqust(ipsList, mContext)) {
                return;
            } else {
                url_ports = null;
            }

            // 直连

            if (VsUserConfig.getDataString(mContext, VsUserConfig.ADDR_IP_LIST).trim().length() < 5) {
                setListData(addrUrlsList, DfineAction.RES.getStringArray(R.array.addr_urls));
            }
            InputStream is = null;


            while (addrUrlsList.size() > 0) {

                int i = r.nextInt(addrUrlsList.size());
                try {


                    if (addrUrlsList.get(i).endsWith(".html")) {
                        JSONObject root = null;                                                             //isNetworkAvailable
                        root = VsHttpTools.getInstance(mContext).doGetMethod(addrUrlsList.get(i), null, null, null);
                        if (root != null&&root.getJSONObject("ipaddr").getString("result")!="-99") {
                            url_ports = root.getString("ipaddr").split(",");
                            break;
                        } else {
                            /**
                             * 直连地址如果不通删掉不通的
                             */
                            addrUrlsList.remove(i);
                        }

                    } else if (addrUrlsList.get(i).endsWith(".txt")) {
                        URL url = new URL(addrUrlsList.get(i));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        /**
                         * 如果连接上资源
                         */
                        conn.connect();
                        if (conn.getResponseCode() < 400) {
                            // 获得文件的输入流
                            is = conn.getInputStream();
                            String result = VsUtil.readData(is).trim();
                            url_ports = result.split(",");
                            if (url_ports[0].matches("\\d+\\.\\d+\\.\\d+\\.\\d+\\:\\d+")) {
                                break;
                            } else {
                                url_ports = null;
                                addrUrlsList.remove(i);
                            }

                        } else {
                            /**
                             * 直连地址如果不通删掉不通的
                             */
                            addrUrlsList.remove(i);
                        }
                    }
                    /**
                     * 其他格式请求Ip
                     */
                    else {
                        addrUrlsList.remove(i);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (addrUrlsList.size() > 0) {
                        addrUrlsList.remove(i);
                    }

                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }


        if (url_ports == null) {
            isSuccess = "no";
            return;
        } else {
            setListData(ipsList, url_ports);
            VsUserConfig.setData(mContext, VsUserConfig.ADDR_IP_LIST, getIpListStr(ipsList));
        }

        if (getResqust(ipsList, mContext)) {
            return;
        }
        isSuccess = "no";

        VsUserConfig.setData(mContext, VsUserConfig.JKEY_TestAccessPointState, MSG_CONVERTNETWORK);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_UriAndPort, DfineAction.RES.getString(R.string.uri_prefix));

    }


    /**
     * String数组存储在ArraList中
     *
     * @return
     */
    public void setListData(ArrayList<String> list, String[] str) {
        if (list == null) {
            list = new ArrayList<String>();
        }
        for (int i = 0; i < str.length; i++) {
            if (!str[i].contains("http://")) {
                str[i] = "http://" + url_ports[i];
            }
            list.add(str[i]);
        }

    }

    /**
     * 多点接入随机链接检验
     *
     * @param list IP列表
     * @return 链接成功为 true 不成功为 false
     */
    public boolean getResqust(ArrayList<String> list, Context mContext) {
        /**
         * 随机数组
         */
        while (list.size() > 0) {
            int i = r.nextInt(list.size());   //setUri_prefix
            VsHttpTools.getInstance(mContext).setUri_prefix(list.get(i)); // 取一个随机ip，然后放到缓存里面，并且用这个缓存的ip来做请求


            CustomLog.i("ports", "port" + i + ":" + list.get(i));


//            if (VsUtil.getUpdate(mContext)) {
//                // 拉静态配置
//                appParams.clear();
//                appParams.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG));
//                CoreBusiness.getInstance().defaultConfigAppMethod(mContext, "config/app", appParams, DfineAction.authType_AUTO);
//
//                VsUserConfig.setData(mContext, VsUserConfig.ADDR_IP_LIST, getIpListStr(list));
//                isSuccess = "no";
//                return true;
//            } else {
//                ipsList.remove(i);
//            }
        }
        VsUserConfig.setData(mContext, VsUserConfig.ADDR_IP_LIST, "");
        return false;
    }


    /**
     * 数组转换String字符串缓存
     *
     * @param ips
     * @return
     */

    public String getIpListStr(ArrayList<String> ips) {
        StringBuffer iplist = new StringBuffer();
        for (int i = 0; i < ips.size(); i++) {
            if (i < ips.size() - 1) {
                iplist.append(ips.get(i));
                iplist.append(",");
            } else {
                iplist.append(ips.get(i));
            }
            CustomLog.i("ports", "port" + i + ":" + ips.get(i));
        }
        return iplist.toString();
    }


}
