package com.weiwei.salemall.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Created by EDZ on 2018/7/9.
 *         计算省钱
 */

public class DealEcnomicalMoneyUtils {
    public static String get(String jdPrice, String mallPrice, int number) {
        double economicalmoney = 0;
        double malPrice = 0;
        double jPrice = 0;
        if (!StringUtils.isEmpty(mallPrice)) malPrice = Double.parseDouble(mallPrice);
        if (!StringUtils.isEmpty(jdPrice)) jPrice = Double.parseDouble(jdPrice);
        double saveMoney = jPrice - malPrice;
        economicalmoney = saveMoney * number;
        return String.format("%.2f", economicalmoney);
    }

    public static String getDiscount(String jdPrice, String mallPrice, String postage, int number) {
        double discountMoney = 0;
        double malPrice = Double.parseDouble(mallPrice);
        double jPrice = Double.parseDouble(jdPrice);
        double postag = Double.parseDouble(postage);
        double saveMoney = jPrice - malPrice - postag;
        discountMoney = saveMoney * number;
        return String.format("%.2f", discountMoney);
    }

    public static String getInt(String jdPrice, String mallPrice, int number) {
        double economicalmoney = 0;
        int malPrice = Integer.parseInt(mallPrice);
        int jPrice = Integer.parseInt(jdPrice);
        int saveMoney = jPrice - malPrice;
        economicalmoney = saveMoney * number;
        return String.valueOf(economicalmoney) == null ? "0.00" : String.valueOf(economicalmoney);
    }
}
