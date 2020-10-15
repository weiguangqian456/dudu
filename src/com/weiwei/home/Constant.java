package com.weiwei.home;

/**
 * @author Created by EDZ on 2019/2/27.
 * Describe
 */

public class Constant {

    public static final String APP_ID = "dudu";

    public static String[] compareFactory = new String[]{"亚马逊价", "京东价", "淘宝价", "天猫价", "苏宁价", "当当价", "国美价", "嘟嘟价", "其他价", "市场价"};

    /**
     * 公司域名
     */
    public static String HTTP_HOST = "http://route.edawtech.com/route/";

    /**
     * 图片域名
     */
    public static String IMAGE_HOST = "http://qiniu.edawtech.com/";

    /**
     * Recycle 的 TYPE类型
     */
    public static final int RECYCLE_TYPE_ITEM = 0;
    public static final int RECYCLE_TYPE_FOOTER = 1;
    public static final int RECYCLE_TYPE_HEAD = 2;

    /**
     * Recycle 的 Footer状态
     */
    public static final int RECYCLE_FOOTER_LOAD = 0;
    public static final int RECYCLE_FOOTER_OVER = 1;
    public static final int RECYCLE_FOOTER_ERROR = 2;
    public static final int RECYCLE_FOOTER_GONE = 3;
}
