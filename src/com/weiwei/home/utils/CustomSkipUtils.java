package com.weiwei.home.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.base.BaseBarActivity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;

/**
 * @author : hc
 * @date : 2019/3/26.
 * @description: 跳转
 */

public class CustomSkipUtils {

    public static void toSkip(final Context mContext, ModelHomeEntranceBean entrance) {
        if (entrance == null || TextUtils.isEmpty(entrance.getAndroidClassName())) return;
        if (entrance.getAndroidClassName().contains("SimBackActivity")) {
            String clazz = entrance.getAndroidClassName().trim();
            toSimActivity(mContext, clazz, entrance);
        } else {
            final int type = entrance.getType();
            final String skipUrl = entrance.getSkipUrl();
            final String className = entrance.getAndroidClassName().trim();
            final String isExchange = entrance.getIsExchange();    //是否兑换专区商品
            String id = entrance.getId();
            String columnName = entrance.getColumnName();

            Intent intent = new Intent();
            if (!TextUtils.isEmpty(entrance.getColumnName())) {
                intent.putExtra("columnName", entrance.getColumnName());
            }
            switch (type) {
                case 0://商品
                    intent.setClassName(mContext, className);
                    intent.putExtra("columnId", id);
                    intent.putExtra("columnName",columnName);
                    intent.putExtra("isExchange", isExchange);
                    break;
                case 1://店铺
                    intent.setClassName(mContext, className);
                    intent.putExtra("columnId", id);
                    break;
                case 2: //外链
                    intent.setClassName(mContext, className);
                    intent.putExtra("skipUrl", skipUrl);
                    break;



                default:
                    break;
            }
            ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo == null) {
                ToastAstrictUtils.getInstance().show("抱歉，当前界面尚未开发完成");
                return;
            }
            if (className.equals("com.weiwei.account.VipMemberActivity")) {    //VIP会员界面
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    mContext.startActivity(intent);
                }
            } else {
                mContext.startActivity(intent);
            }
        }
    }

    public static void toSkip(final Context context, BannerImageEntity entrance) {
        if (entrance == null || TextUtils.isEmpty(entrance.getAndroidClassName())) return;
        if (entrance.getAndroidClassName().contains("SimBackActivity")) {
            String clazz = entrance.getAndroidClassName().trim();
            toSimActivity(context, clazz, null);
        } else {
            final String className = entrance.getAndroidClassName().trim();
            final String params = entrance.getAndroidParams();
            JSONObject jsonObject = JSONObject.parseObject(params);
            String productNo = "";
            String skipUrl = "";
            String storeNo = "";
            String seckill = "";
            String seckillProductId = "";
            if (jsonObject != null) {
                productNo = (String) jsonObject.get("productNo");
                skipUrl = (String) jsonObject.get("skipUrl");
                storeNo = (String) jsonObject.get("storeNo");
                seckill = (String) jsonObject.get("seckill");
                seckillProductId = (String) jsonObject.get("seckillProductId");
            }
            if (entrance.getSkipType() == 0) {
                Intent intent = new Intent();
                if (className.contains("VsRechargeActivity") && !CheckLoginStatusUtils.isLogin()) {
                    intent.setClassName(context, "com.weiwei.base.activity.login.VsLoginActivity");
                    context.startActivity(intent);
                } else {
                    intent.setClassName(context, className);
                    ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (resolveInfo == null) {
                        ToastAstrictUtils.getInstance().show("抱歉，当前界面尚未开发完成");
                        return;
                    }
                    if (productNo != null && !productNo.equals("")) {
                        intent.putExtra("productNo", productNo);
                        intent.putExtra("seckill", seckill);
                        intent.putExtra("seckillProductId", seckillProductId);
                    } else {
                        intent.putExtra("storeNo", storeNo);
                    }
                    context.startActivity(intent);
                }
            } else if (entrance.getSkipType() == 1) {
                Intent intent = new Intent();
                intent.setClassName(context, className);
                intent.putExtra("skipUrl", skipUrl);
                context.startActivity(intent);
            }
        }
    }

    public static void toSimActivity(Context context, String clazz, ModelHomeEntranceBean entrance) {
        if (!TextUtils.isEmpty(clazz) && clazz.contains("SimBackActivity") && clazz.contains(".")) {
            int k;
            //防止类型转换错误
            try {
                String index = clazz.substring(clazz.indexOf(".") + 1);
                k = Integer.parseInt(index);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            SimBackEnum commonGoods = SimBackEnum.COMMON_GOODS;
            for (SimBackEnum airlineTypeEnum : SimBackEnum.values()) {
                if (airlineTypeEnum.getValue() == k) {
                    commonGoods = airlineTypeEnum;
                    break;
                }
            }
            Bundle bundle = new Bundle();
            if (entrance != null && !TextUtils.isEmpty(entrance.getId())) {
                bundle.putString("ID", entrance.getId());
            }
            if (k == 6) {
                SimBackActivity.launch(context, commonGoods, BaseBarActivity.STATE_BAR_LOCAL, bundle);
            } else {
                SimBackActivity.launch(context, commonGoods, bundle);
            }
        }
    }

}
