package com.weiwei.base.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class VsNetWorkTools {
    public static String TAG = "NetWorkTools";
    public static final int NO_NETWORK = 0;
    public static final int WIFI_NETWORK = 1;
    public static final int G3_NETWORK = 2;
    public static final int EDGE_NETWORK = 3;// edge means gprs
    public static final int G4_NETWORK = 4;

    /**
     * 获取当前网络状态
     *
     * @param context
     * @return 0(无网络)，1(wifi),2(3g),3(gprs)
     */
    public static final int getSelfNetworkType(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        int netSubtype = -1;
        if (activeNetInfo != null) netSubtype = activeNetInfo.getSubtype();

        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            if ("WIFI".equalsIgnoreCase(activeNetInfo.getTypeName()))// wifi
            {
                CustomLog.v("NetworkMonitor", "wifi connected");
                return WIFI_NETWORK;
            } else if ("MOBILE".equalsIgnoreCase(activeNetInfo.getTypeName()))// 3g
            // or
            // gprs
            {
                CustomLog.v("NetworkMonitor", "mobile int = " + netSubtype);
                if (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0 || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA || netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA || netSubtype == TelephonyManager
                        .NETWORK_TYPE_HSPA)/*
                                                                             * && ! mTelephony . isNetworkRoaming ( )
																			 */ {
                    CustomLog.v("NetworkMonitor", "3g connected");
                    return G3_NETWORK;
                } else if (netSubtype == TelephonyManager.NETWORK_TYPE_LTE) {
                    return G4_NETWORK;
                } else {
                    CustomLog.v("NetworkMonitor", "gprs connected");
                    return EDGE_NETWORK;
                }
            }

        }

        if (netInfo != null) {
            CustomLog.v("NetworkMonitor", netInfo.getTypeName());
        }

        return NO_NETWORK;// 网络未连接时当无网络
    }

    /**
     * 判断当前是否联网
     *
     * @param context
     * @return
     */
    // Check the network
    public static boolean isNetworkAvailable(Context context) {
        // Context context = mActivity.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                int len = info.length;
                for (int i = 0; i < len; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否是GPRS连接网络
     *
     * @param context
     * @return
     */
    public static final boolean isSelfNetworkTypeGPRS(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        int netSubtype = -1;
        if (activeNetInfo != null) netSubtype = activeNetInfo.getSubtype();

        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            if ("WIFI".equalsIgnoreCase(activeNetInfo.getTypeName()))// wifi
            {
                CustomLog.v("NetworkMonitor", "wifi connected");
                return false;
            } else if ("MOBILE".equalsIgnoreCase(activeNetInfo.getTypeName()))// 3g
            // or
            // gprs
            {
                CustomLog.v("NetworkMonitor", "mobile int = " + netSubtype);
                if (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0 || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA || netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA || netSubtype == TelephonyManager
                        .NETWORK_TYPE_HSPA)/*
                                                                             * && ! mTelephony . isNetworkRoaming ( )
																			 */ {
                    CustomLog.v("NetworkMonitor", "3g connected");
                    return true;
                } else {
                    CustomLog.v("NetworkMonitor", "gprs connected");
                    return true;
                }
            }

        }

        if (netInfo != null) {
            CustomLog.v("NetworkMonitor", netInfo.getTypeName());
        }

        return false;// 网络未连接时当无网络
    }

    /**
     * @param context
     * @return 0(无网络)，1(wifi),2(3g),3(gprs)
     */
    public static final String getNetMode(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        int netSubtype = -1;
        if (activeNetInfo != null) netSubtype = activeNetInfo.getSubtype();

        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            if ("WIFI".equalsIgnoreCase(activeNetInfo.getTypeName()))// wifi
            {
                CustomLog.v("NetworkMonitor", "wifi connected");
                return "wifi";
            } else if ("MOBILE".equalsIgnoreCase(activeNetInfo.getTypeName()))// 3g
            // or
            // gprs
            {
                CustomLog.v("NetworkMonitor", "mobile int = " + netSubtype);
                if (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0 || netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA || netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA || netSubtype == TelephonyManager
                        .NETWORK_TYPE_HSPA)/*
                                                                             * && ! mTelephony . isNetworkRoaming ( )
																			 */ {
                    CustomLog.v("NetworkMonitor", "3g connected");
                    return "3g";
                } else {
                    CustomLog.v("NetworkMonitor", "gprs connected");
                    return "2g";
                }
            }

        }

        if (netInfo != null) {
            CustomLog.v("NetworkMonitor", netInfo.getTypeName());
        }

        return "";// 网络未连接时当无网络
    }
}
