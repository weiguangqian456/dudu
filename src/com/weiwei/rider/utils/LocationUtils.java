package com.weiwei.rider.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.StoreDetailActivity;
import com.weiwei.salemall.utils.GpsUtils;
import com.weiwei.salemall.utils.RxLocationUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;

public class LocationUtils {

    public static LocationUtils instance;
    public CommonPopupWindow mapPop;
    public static LocationUtils getInstance() {
        if(instance == null) {
            instance = new LocationUtils();
        }
        return instance;
    }
    public void showMapPop(Context mContext,String mGoLongitude,String mGoLatitude) {
        mapPop = new CommonPopupWindow.Builder(mContext).setView(R.layout.pop_map).setWidthAndHeight((int) mContext.getResources().getDimension(R.dimen.w_277_dip), ViewGroup.LayoutParams
                .WRAP_CONTENT).setBackGroundLevel(0.5f).setViewOnclickListener((view, layoutResId) -> {
                    final CheckBox amapCb = (CheckBox) view.findViewById(R.id.cb_amap);
                    final CheckBox baidumapCb = (CheckBox) view.findViewById(R.id.cb_baidumap);
                    final CheckBox rememberCb = (CheckBox) view.findViewById(R.id.cb_remember);
                    TextView ensureBtn = (TextView) view.findViewById(R.id.btn_ensure);

                    amapCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            baidumapCb.setChecked(false);
                        }
                    });
                    baidumapCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            amapCb.setChecked(false);
                        }
                    });

                    ensureBtn.setOnClickListener(v -> {
                        Intent intent = null;
                        if (!baidumapCb.isChecked() && !amapCb.isChecked()) {
                            Toast.makeText(mContext, "请选择一种地图", Toast.LENGTH_SHORT).show();
                        } else if (baidumapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(mContext, "com.baidu.BaiduMap")) {
                                navWithBaidu(mContext,mGoLongitude,mGoLatitude);
                            } else {
                                Toast.makeText(mContext, "您尚未安装百度地图", Toast.LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);
                            }
                        } else if (amapCb.isChecked()) {
                            if (StoreDetailActivity.mapisAvailable(mContext, "com.autonavi.minimap")) {
                                navWithAmap(mContext,mGoLongitude,mGoLatitude);
                            } else {
                                Toast.makeText(mContext, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);
                            }
                        }

                        if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(mContext, "selectMapFlag", 0);
                        } else if (rememberCb.isChecked() && amapCb.isChecked()) {
                            SpUtils.putIntValue(mContext, "selectMapFlag", 1);
                        }

                        mapPop.dismiss();
                    });
                }).setOutsideTouchable(true).create();
        mapPop.showAtLocation(((Activity)mContext).getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }
    private void navWithBaidu(Context mContext,String mGoLongitude,String mGoLatitude) {
        GpsUtils bdGps = RxLocationUtils.GCJ02ToBD09(Double.parseDouble(mGoLongitude), Double.parseDouble(mGoLatitude));
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(bdGps.getLatitude()).append(",").append(bdGps.getLongitude()).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        mContext.startActivity(intent);
    }
    private void navWithAmap(Context mContext,String mGoLongitude,String mGoLatitude) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + mGoLatitude +
                "&dlon=" + mGoLongitude + "&dname=目的地&dev=0&t=2"));
        mContext.startActivity(intent);
    }

}
