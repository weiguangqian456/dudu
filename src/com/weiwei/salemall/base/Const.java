package com.weiwei.salemall.base;

/**
 * @author Created by EDZ on 2018/6/5.
 * 基本配置信息
 */

public interface Const {
    /**
     * 返回码
     */
    int REQUEST_CODE = 0;

    /**
     * 超时时间
     */
    int HTTP_TIME = 30;

    /**
     * 域名   正式
     */
   String BASE_URL = "http://route.edawtech.com/";

//    String BASE_URL = "http://192.168.0.116:4091/";
    /**
     * 测试服
     */
      // String BASE_URL = "http://ceshi.edawtech.com/";

  //   String BASE_URL = "http://119.23.26.153:4091/api/";

    /**
     * Retrofit 域名
     */
//    String URL = BASE_URL + "/shop/api/";

    /**
     * 相关模块
     */
    String SHOP_XX = "shop/api/";
    String SHOP_MODEL = "route/shop/api/";
    String BENIFIT_MODEL = "route/benefit/api/";

    /**
     * 图片域名
     */
    String BASE_IMAGE_URL = "http://qiniu.edawtech.com/";
}
