package com.weiwei.home.utils;

import android.text.TextUtils;

import com.weiwei.home.Constant;

import static com.weiwei.salemall.base.Const.BASE_IMAGE_URL;

/**
 * @author : hc
 * @date : 2019/3/11.
 * @description: 文字添加处理类
 */

public class TextDisposeUtils {

    public static String dispseMoneyJdText(int contrastSource,String price){
        return " ￥" + (TextUtils.isEmpty(price) ? "0" :  price);
    }

    public static String dispseMoneyText(String price){
        return "￥" + (TextUtils.isEmpty(price) ? "0" :  price);
    }

    public static String dispseMoneyText(String symbol,String price){
        return symbol + (TextUtils.isEmpty(price) ? "0" :  price);
    }

    public static int toStringInt(String price){
        return (TextUtils.isEmpty(price) ? 0 :  Integer.parseInt(price));
    }

    public static Float toStringFloat(String price){
        return (TextUtils.isEmpty(price) ? 0 :  Float.parseFloat(price));
    }

    /**
     * 金额和兑换成长金整合
     */
    public static StringBuilder getTotalAmount(String symbol,String discountAmount,String coupon){
        StringBuilder price = new StringBuilder("");
        discountAmount = TextUtils.isEmpty(discountAmount) ? "0" : discountAmount;
        coupon = TextUtils.isEmpty(coupon) ? "0" : coupon;
        boolean hasPrice  = Float.parseFloat(discountAmount) != 0;
        boolean hasCoupon = Float.parseFloat(coupon) != 0;
        //不需要成长金且不需要钱 或者 需要钱
        if((!hasPrice && !hasCoupon ) || hasPrice){
            price.append(symbol);
            price.append(discountAmount);
        }
        //需要成长金
        if(hasCoupon){
            if(hasPrice){
                price.append(" + ");
            }
            price.append(coupon);
            price.append("我的积分");
        }
        return price;
    }

    public static String getEndPrice(boolean isCoupon,String discountAmount,String coupon){
        if(isCoupon){
            return "我的积分: " + coupon;
        }else {
            return "￥" + discountAmount;
        }
    }
    /**
     * 图片合法？
     */
    public static String isAvailable(String url) {
        if (url != null && !url.contains("http")) {
            url = BASE_IMAGE_URL + url;
        }
        return url;
    }
}
