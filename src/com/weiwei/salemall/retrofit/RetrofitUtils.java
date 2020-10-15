package com.weiwei.salemall.retrofit;

import com.weiwei.salemall.base.Const;
import com.weiwei.salemall.fastjson.FastJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class RetrofitUtils {

    private Retrofit retrofit;

    private RetrofitUtils() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .client(initClient())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
    private static volatile ApiService instance;

    public static ApiService getInstance() {
        if(instance == null) {
            synchronized (ApiService.class) {
                if(instance == null) {
                    instance = new RetrofitUtils().getApi();
                }
            }
        }
        return instance;
    }
    private ApiService getApi() {
        return retrofit.create(ApiService.class);
    }

    private OkHttpClient initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Const.HTTP_TIME, TimeUnit.SECONDS)
                .readTimeout(Const.HTTP_TIME, TimeUnit.SECONDS)
                .writeTimeout(Const.HTTP_TIME, TimeUnit.SECONDS)
                .addInterceptor(new LoggerInterceptor("Ritrofit", true));
//                .addInterceptor(new CommonParamInterceptor());
        return builder.build();


    }
}
