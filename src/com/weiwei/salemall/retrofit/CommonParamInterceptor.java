package com.weiwei.salemall.retrofit;

import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * @author Created by EDZ on 2018/9/10.
 *         Describe Retrofit 基本参数拦截器
 */

public class CommonParamInterceptor implements Interceptor {
    /**
     * 请求方法-GET
     */
    private static final String REQUEST_METHOD_GET = "GET";

    /**
     * 请求方法POST
     */
    private static final String REQUEST_METHOD_POST = "POST";

    /**
     * app对应名字
     */
    private static final String APPID = "dudu";

    /**
     * 用户id(未登录默认为空)
     */
    private static final String UID = "";

    /**
     * 软件版本号
     */
    private static final String VERSION_CODE = KcQcodeActivity.getVersionCode();

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取原先的请求对象
        Request request = chain.request();
        //Get请求
        if (REQUEST_METHOD_GET.equals(request.method())) {
            request = addGetBaseParams(request);
        //post请求
        } else if (REQUEST_METHOD_POST.equals(request.method())) {
            request = addPostBaseParams(request);
        }
        return chain.proceed(request);
    }


    /**
     * 添加GET方法基础参数
     *
     * @param request
     * @return
     */
    private Request addGetBaseParams(Request request) {
        HttpUrl httpUrl = request.url().newBuilder()
                //平台号
                .addQueryParameter("appId", APPID)
                //版本号
                .addQueryParameter("version", VERSION_CODE)
                .build();

        //根据登录状态添加userId&&token
        if (CheckLoginStatusUtils.isLogin()) {
            httpUrl = httpUrl.newBuilder().addQueryParameter("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId)).build();
        } else {
            httpUrl = httpUrl.newBuilder().addQueryParameter("uid", "\"\"").build();
        }

        request = request.newBuilder().url(httpUrl).build();
        return request;
    }

    /**
     * 添加POST方法基础参数
     *
     * @param request
     * @return
     */
    private Request addPostBaseParams(Request request) {

        /**
         * request.body() instanceof FormBody 为true的条件为：
         * 在ApiService 中将POST请求中设置
         * 1，请求参数注解为@FieldMap
         * 2，方法注解为@FormUrlEncoded
         */
        if (request.body() instanceof FormBody) {
            FormBody formBody = (FormBody) request.body();
            FormBody.Builder builder = new FormBody.Builder();

            for (int i = 0; i < formBody.size(); i++) {
                //@FieldMap 注解 Map元素中 key 与 value 皆不能为null,否则会出现NullPointerException
                if (formBody.value(i) != null) builder.add(formBody.name(i), formBody.value(i));
            }

            builder.add("appId", APPID).add("version", VERSION_CODE);

            //根据登录状态添加userId&&token
            if (CheckLoginStatusUtils.isLogin()) {
                builder.add("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
            } else {
                builder.add("uid", "\"\"");
            }

            formBody = builder.build();
            request = request.newBuilder().post(formBody).build();
        }
        return request;
    }
}
