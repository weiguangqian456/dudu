package com.weiwei.salemall.retrofit;

import com.weiwei.base.item.ChargePackageItem;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.entity.SepGoodsEntity;
import com.weiwei.merchant.bean.MerchantOrder;
import com.weiwei.rider.bean.OrderImage;
import com.weiwei.rider.bean.RiderAccount;
import com.weiwei.rider.bean.RiderInfo;
import com.weiwei.rider.bean.Order;
import com.weiwei.rider.bean.RiderIncome;
import com.weiwei.rider.bean.RiderOrder;
import com.weiwei.salemall.bean.ResultEntity;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.weiwei.salemall.base.Const.BENIFIT_MODEL;
import static com.weiwei.salemall.base.Const.SHOP_MODEL;
import static com.weiwei.salemall.base.Const.SHOP_XX;

public interface ApiService {


    /**
     * 通用RecycleView加载
     *
     * @param path   地址
     * @param params 参数
     * @return 返回对象
     */
    @GET("{path}")
    Call<ResultEntity> getList(@Path(value = "path", encoded = true) String path, @QueryMap Map<String, String> params);


    @GET(SHOP_MODEL + "childColumns/dataByColumn/{path}")
    Call<ResultEntity> getExchangeList2(@Path(value = "path") String path, @QueryMap Map<String, String> params);

    @GET(SHOP_MODEL + "product/page")
    Call<SepGoodsEntity> getExtractList(@QueryMap Map<String, String> params);

    /**
     * 新会员专区详情页
     */
    @GET(SHOP_MODEL + "seckill/list")
    Call<ResultEntity> getMemberList(@QueryMap Map<String, String> params);

    /**
     * 获取地址
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "delivery/page")
    Call<ResultEntity> getDelivery(@QueryMap Map<String, String> params);

    /**
     * 新增地址
     *
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "delivery/add")
    @FormUrlEncoded
    Call<ResultEntity> addDelivery(@FieldMap Map<String, String> params);

    /**
     * 设置默认地址
     *
     * @param deliveryId
     * @return
     */
    @POST(SHOP_MODEL + "delivery/default/{deliveryId}")
    Call<ResultEntity> setDefaultDelivery(@Path("deliveryId") String deliveryId);

    /**
     * 删除地址
     *
     * @param deliveryId
     * @return
     */
    @POST(SHOP_MODEL + "delivery/delete/{deliveryId}")
    Call<ResultEntity> deleteDelivery(@Path("deliveryId") String deliveryId);

    /**
     * 获取地址详情
     *
     * @param deliveryId
     * @return
     */
    @GET(SHOP_MODEL + "delivery/detail/{deliveryId}")
    Call<ResultEntity> getDeliveryDetail(@Path("deliveryId") String deliveryId);

    /**
     * 编辑地址
     *
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "delivery/edit")
    @FormUrlEncoded
    Call<ResultEntity> editDelivery(@FieldMap Map<String, String> params);

    /**
     * 获取Banner
     *
     * @return
     */
    @GET(SHOP_MODEL + "banner/get")
    Call<ResultEntity> getBanners();



    /**
     * 获取首页模块Banner
     *
     * @return
     */
    @GET(SHOP_MODEL + "banner/get/dudu")
    Call<ResultEntity> getBanner();


    /**
     * 获取首页商品数据
     *
     * @return
     */
    @GET(SHOP_MODEL + "product/index")
    Call<ResultEntity> getProducts();

    /**
     * 获取必买街商品数据
     *
     * @return
     */
    @GET(SHOP_MODEL + "product/page")
    Call<ResultEntity> getShoppingList(@QueryMap Map<String, String> params);

    /**
     * 分页获取推荐商品
     *
     * @return
     */
    @GET(SHOP_MODEL + "product/recommend")
    Call<ResultEntity> getRecommendProducts(@QueryMap Map<String, String> params);

    /**
     * 分页获取商品
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "product/page")
    Call<ResultEntity> getProductPage(@QueryMap Map<String, String> params);

    /**
     * 获取商品详情
     *
     * @param productNo
     * @return
     */
    @GET(SHOP_MODEL + "product/detail/{productNo}")
    Call<ResultEntity> getProductDetail(@Path("productNo") String productNo, @QueryMap Map<String, String> params);

    /**
     * 获取商品规格
     *
     * @param productNo
     * @return
     */
    @GET(SHOP_MODEL + "product/spec/{productNo}")
    Call<ResultEntity> getProductDesc(@Path("productNo") String productNo, @QueryMap Map<String, String> params);

    /**
     * 获取商品属性
     *
     * @param productNo
     * @return
     */
    @GET(SHOP_MODEL + "product/property/{productNo}")
    Call<ResultEntity> getProductProperty(@Path("productNo") String productNo, @QueryMap Map<String, String> params);

    /**
     * 获取订单
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "order/page")
    Call<ResultEntity> getOrderPage(@QueryMap Map<String, String> params);

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @GET(SHOP_MODEL + "order/detail/{orderId}")
    Call<ResultEntity> getOrderDetail(@Path("orderId") String orderId);

    /**
     * 取消订单
     *
     * @param orderId
     * @return
     */
    @POST(SHOP_MODEL + "order/cancel/{orderId}")
    Call<ResultEntity> cancelOrder(@Path("orderId") String orderId);

    /**
     * 确认收货(未测试)
     *
     * @param orderId
     * @return
     */
    @POST(SHOP_MODEL + "order/finish/{orderId}")
    Call<ResultEntity> finishOrder(@Path("orderId") String orderId, @QueryMap Map<String, String> params);

    /**
     * 获取评论
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "comment/page")
    Call<ResultEntity> getCommentPage(@QueryMap Map<String, String> params);

    /***
     * 新增评论
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "comment/add")
    @FormUrlEncoded
    Call<ResultEntity> addComment(@FieldMap Map<String, String> params);

    /**
     * 下单
     *
     * @param route
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(SHOP_MODEL + "pay/buy")
    Call<ResultEntity> buyProduct(@Body RequestBody route);

    /**
     * 继续下单
     *
     * @param orderNo
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "pay/continueNew/{orderNo}")
    Call<ResultEntity> continueBuyProduct(@Path("orderNo") String orderNo, @QueryMap Map<String, String> params);

    /**
     * 处理价格
     *
     * @param route
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(SHOP_MODEL + "pay/dealPrice")
    Call<ResultEntity> dealPrice(@Body RequestBody route);

    /**
     * 购物车下单处理价格
     *
     * @param route
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(SHOP_MODEL + "pay/dealCartPriceNew")
    Call<ResultEntity> dealCartPrice(@Body RequestBody route);

    /**
     * 购物车下单
     *
     * @param route
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(SHOP_MODEL + "pay/cartBuyNew")
    Call<ResultEntity> cartBuyProduct(@Body RequestBody route);

    /**
     * 广告
     *
     * @return
     */
    @GET(SHOP_MODEL + "initPage/get")
    Call<ResultEntity> getAdpage();

    /**
     * 获取首页栏目
     *
     * @return
     */
    @GET(SHOP_MODEL + "column/getV2")
    Call<ResultEntity> getColumn(@Query("phoneType") String aa);

    /**
     * 获取本地商品
     *
     * @return
     */
    @GET(SHOP_XX + "childColumns/selectChildColumn/{columnId}")
    Call<ResultEntity> getLocalClass(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    @GET(SHOP_MODEL + "column/get")
    Call<ResultEntity> getColumnGet(@QueryMap Map<String, String> params);

    /**
     * 获取首页栏目数据
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "column/dataByColumn/{columnId}")
    Call<ResultEntity> getColumnData(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    /**
     * 搜索商品
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "column/productForApp")
    Call<ResultEntity> searchProduct(@QueryMap Map<String, String> params);

    @GET(SHOP_MODEL + "purchase/product/queryProductByPage")
    Call<ResultEntity> searchProductJD(@QueryMap Map<String, String> params);

    /**
     * 搜索店铺
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "column/storeForApp")
    Call<ResultEntity> searchStore(@QueryMap Map<String, String> params);

    /**
     * 获取收藏
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "favorite/get")
    Call<ResultEntity> getFavorite(@QueryMap Map<String, String> params);

    /**
     * 添加收藏
     *
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "favorite/add")
    @FormUrlEncoded
    Call<ResultEntity> addFavorite(@FieldMap Map<String, String> params);

    /**
     * 删除收藏
     *
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "favorite/del")
    Call<ResultEntity> deleteFavorite(@QueryMap Map<String, String> params);

    /**
     * 获取店铺栏目
     *
     * @return
     */
    @GET(SHOP_MODEL + "storeColumns/get")
    Call<ResultEntity> getStoreColumns();

    /**
     * 获取店铺栏目数据
     *
     * @param columnId
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "storeColumns/dataByColumn/{columnId}")
    Call<ResultEntity> getStoreColumnData(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    /**
     * 获取城市
     *
     * @return
     */
    @GET(SHOP_MODEL + "region/get")
    Call<ResultEntity> getCities();

    /**
     * 获取商企栏目
     *
     * @return
     */
    @GET(SHOP_MODEL + "samqiColumns/get")
    Call<ResultEntity> getSamqiColumns();

    /**
     * 获取商企栏目数据
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "samqiColumns/dataByColumn/{columnId}")
    Call<ResultEntity> getSamqiColumnsData(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    /**
     * 根据首页栏目获取子栏目数据
     *
     * @return
     */
    @GET(SHOP_MODEL + "childColumns/selectChildColumn/{columnId}")
    Call<ResultEntity> getChildColumnsData(@Path("columnId") String columnId);


    /**
     * 根据栏目id获取数据
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "childColumns/dataByColumn/{columnId}")
    Call<ResultEntity> getColumnsData(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    /**
     * 获取兑换专区栏目
     *
     * @return
     */
    @GET(SHOP_MODEL + "couponColumns/get")
    Call<ResultEntity> getCouponColumns();

    /**
     * 获取兑换专区栏目数据
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "couponColumns/dataByColumn/{columnId}")
    Call<ResultEntity> getCouponColumnsData(@Path("columnId") String columnId, @QueryMap Map<String, String> params);

    /**
     * 热门搜索
     *
     * @return
     */
    @GET(SHOP_MODEL + "hotword/get")
    Call<ResultEntity> getHotwords();

    /**
     * 店铺详情
     *
     * @return
     */
    @GET(SHOP_MODEL + "store/detail/{storeNo}")
    Call<ResultEntity> getStoreDetail(@Path("storeNo") String storeNo);

    /**
     * 店铺中取消收藏
     *
     * @param params
     * @return
     */
    @POST(SHOP_MODEL + "favorite/detail/cancel")
    @FormUrlEncoded
    Call<ResultEntity> cancelDetailFavorite(@FieldMap Map<String, String> params);

    //=======================Benifit模块start==========================

    /**
     * 获取用户级别接口
     *
     * @return
     */
    @GET(BENIFIT_MODEL + "userInfo/userLevel")
    Call<ResultEntity> getUserLevel();

    /**
     * 分页获取用户加油余额明细接口
     *
     * @return
     */
    @GET(BENIFIT_MODEL + "userInfo/getCouponRecord")
    Call<ResultEntity> getCouponRecord(@QueryMap Map<String, String> params);

    /**
     * 分页获取分享红包明细接口
     *
     * @return
     */
    @GET(BENIFIT_MODEL + "invitation/findInvitationLog")
    Call<ResultEntity> getShareRedBagRecord(@QueryMap Map<String, String> params);

    /**
     * 获取用户钱包接口
     *
     * @return
     */
    @GET(BENIFIT_MODEL + "userInfo/getWallet")
    Call<ResultEntity> getWallet();

    /**
     * 微信提现
     *
     * @param body
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(BENIFIT_MODEL + "invitation/drawV2")
    Call<ResultEntity> draw(@Body RequestBody body);

    /**
     * VIP界面获取充值列表
     *
     * @param params
     * @return
     */
    @GET("config/goodsList")
    Call<List<ChargePackageItem>> getGoodList(@QueryMap Map<String, String> params);


    /**
     * 分页获取秒杀活动
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "seckill/list")
    Call<ResultEntity> getSecKillList(@QueryMap Map<String, String> params);

    /**
     * 获取秒杀商品详情
     *
     * @param seckillProductId
     * @return
     */
    @POST(SHOP_MODEL + "seckill/detail/{seckillProductId}")
    Call<ResultEntity> getSecKillProductDetail(@Path("seckillProductId") String seckillProductId);

    /**
     * 获取秒杀商品详情
     *
     * @param seckillProductId
     * @return
     */
    @POST(SHOP_MODEL + "holidaySeckill/detail/{seckillProductId}")
    Call<ResultEntity> getDuduSeckillDetail(@Path("seckillProductId") String seckillProductId);


    /**
     * 获取首页推送
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "homePagePush/get")
    Call<ResultEntity> getHomePagePush(@QueryMap Map<String, String> params);

    /**
     * 提交设备信息
     *
     * @param params
     * @return
     */
    @GET(SHOP_MODEL + "deviceInfo/get")
    Call<ResultEntity> getDeviceInfo(@QueryMap Map<String, String> params);

    /**
     * benefit/api/
     * "http://route.edawtech.com/route/benefit/api/exchangePointWallet/sign/{phone}"
     * //BENIFIT_MODEL +
     */
    @GET(BENIFIT_MODEL + "exchangePointWallet/sign/{phone}")
    Call<ResultEntity> httpSignInfo(@Path("phone") String phone);

    @GET(BENIFIT_MODEL + "exchangePointWallet/rechargeRecords")
    Call<ResultEntity> getExchangeList(@QueryMap Map<String, String> params);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(BENIFIT_MODEL + "exchangePointWallet/signIn")
    Call<ResultEntity> httpSign(@Body RequestBody body);

    @GET(BENIFIT_MODEL + "userInfo/getWallet")
    Call<ResultEntity> getWallet2(@QueryMap Map<String, String> params);

    /**
     * 验证电话
     */
    @GET("charge/api/callLimit/check")
    Call<ResultEntity> getPhoneMag(@QueryMap Map<String, String> params);

    /**
     * 嘟嘟秒杀列表
     *
     * @return
     */
    @GET(SHOP_XX + "holidaySeckill/page")
    Call<SeckillTab> getSeckillTab();

    /**
     * 嘟嘟秒杀首页banner
     *
     * @return
     */
    @GET(SHOP_XX + "holidaySeckill/home/page")
    Call<ResultEntity> getDuduSeckillBanner(@QueryMap Map<String, String> params);

    /**
     * 加油余额充值
     *
     * @return
     */
    @POST(BENIFIT_MODEL + "exchangePointWallet/increase")
    Call<ResultEntity> refuelRecharge(@Body RequestBody body);

    /**
     * 获取油站列表
     *
     * @return
     */
    @GET("benefit/web/CheZhuBangController/queryGasStationInfoList")
    Call<ResultEntity> getRefuelList(@QueryMap Map<String, String> params);

    /**
     * 获取油站详情
     *
     * @return
     */
    @POST("benefit/web/CheZhuBangController/queryPriceByPhone")
    Call<ResultEntity> getRefuelDetail(@Body RequestBody body);

    /**
     * 验证是否有加油余额
     *
     * @return
     */
    @GET("route/benefit/web/OrderController/selectOrder")
    Call<ResultEntity> isRefuelbalance(@QueryMap Map<String, String> params);

    /**
     * 更新加油订单
     *
     * @return
     */
    @FormUrlEncoded
    @POST("route/benefit/web/OrderController/updateOrder")
    Call<ResultEntity> updateRefuelOrder(@FieldMap Map<String, String> params);

    /**
     * 记录加油用户信息
     *
     * @return
     */
    @POST("route/benefit/tGasStationReconciliation")
    Call<ResultEntity> recordUserInfo(@Body RequestBody body);

    /**
     * 获取京东有货无货
     *
     * @return
     */
    @FormUrlEncoded
    @POST(SHOP_MODEL + "product/isHaveStock")
    Call<ResultEntity> getJDInventory(@FieldMap Map<String, String> params);

    /**
     * 根据加油金额获取界面金额信息
     *
     * @return
     */
    @POST("benefit/web/OrderController/queryPriceByGas")
    Call<ResultEntity> queryPriceByGas(@Body RequestBody body);

    /**
     * 支付
     **/
    @GET("benefit/api/pay/getpay/{gasId}")
    Call<ResultEntity> queryPay(@Path("gasId") String gasId );

    /**
     * 支付
     */
    @GET("benefit/api/pay")
    Call<ResultEntity> queryPay2();

    /**
     * 嘟嘟自主下单创建订单
     *
     * @return
     */
    @POST("benefit/web/OrderController/placeOrder")
    Call<ResultEntity> placeOrder(@Body RequestBody body);

    /**
     * 获取用户等级
     * @param
     * @return
     */
    @GET("benefit/api/userInfo/userLevel")
    Call<ResultEntity> getUserLevel(@QueryMap Map<String,String> params);
    /**
     * 获取我要邀请的人数
     * @param
     * @return
     */
    @GET("benefit/api/userInfo/childAgentUserList")
    Call<SeckillTab> inviteNum(@QueryMap Map<String,String> params);

    /**
     * 我邀请的收益
     * @param params
     * @return
     */
    @GET("route/benefit/api/userInfo/getWallet")
    Call<ResultEntity> inviteIncome(@QueryMap Map<String,String> params);

    @GET("benefit/api/userInfo/childAgentUserList")
    Call<SeckillTab> getInviteNum(@QueryMap Map<String,String> params);

    /**
     * 加油金额
     * @return
     */
    @GET("benefit/web/OrderController//selectGasStation")
    Call<SeckillTab> refuelMoney(@QueryMap Map<String,String> params);

    /**
     * 骑手地址选择
     * @return
     */
    @GET("shop/api/tCitiesRider/get")
    Call<ResultEntity> getCitys();

    /**
     * 骑手报名
     * @return
     */
    @POST("benefit/api/tRiderUser")
    Call<ResultEntity> signUp(@Body RequestBody body);

    /**
     * 获取骑手信息
     * @param uid
     * @return
     */
    @GET("benefit/api/tRiderUser/getUid/{uid}")
    Call<RiderInfo> riderInfo(@Path("uid") String uid);

    /**
     * 获取骑手的账户
     * @param uid
     * @param params
     * @return
     */
    @GET("benefit/api/tRiderIncome/get/{uid}")
    Call<RiderAccount> getRiderAccount(@Path("uid") String uid,@QueryMap Map<String,String> params);

    /**
     * 获取骑手收入明细
     * @param params
     * @return
     */
    @GET("benefit/api/tRiderIncomeDetails/page")
    Call<RiderIncome> getRiderIncome(@QueryMap Map<String,String> params);

    /**
     *骑手提现明细
     * @return
     */
    @GET("benefit/api/tRiderIncomeWithdrawal/page")
    Call<RiderIncome> getRiderWithdraw(@QueryMap Map<String,String> params);

    /**
     * 获取骑手订单
     * @param params
     * @return
     */
    @GET("shop/api/order/riderOrder")
    Call<RiderOrder> getRiderOrder(@QueryMap Map<String,String> params);

    /**
     * 获取骑手订单详情
     * @param id
     * @return
     */
    @GET("shop/api/order/{id}")
    Call<Order> getOrderDetails(@Path("id") String id);

    /**
     * 骑手抢单
     * @param body
     * @return
     */
    @PUT("shop/api/order/updateMerchant")
    Call<ResultEntity> robOrder(@Body RequestBody body);

    /**
     * 骑手商品图片上传
     * @param  body
     * @return
     */
    @Multipart
    @POST("shop/api/order/upload")
    Call<OrderImage> postImage(@Part MultipartBody.Part body);

    /**
     * 骑手上线下线
     * @param body
     * @return
     */
    @PUT("benefit/api/tRiderUser")
    Call<ResultEntity> updateStatus(@Body RequestBody body);

    /**
     * 获取商家信息
     * @param
     * @param phone
     * @return
     */
    @GET("shop/api/purchase/user/getTAgentUser/{phone}")
    Call<ResultEntity> merchantInfo(@Path("phone") String phone);

    /**
     * 获取商家金额
     * @param uid
     * @param phone
     * @return
     */
    @GET("benefit/api/tMerchantsIncome/getUid/{uid}")
    Call<RiderAccount> merchantAccount(@Path("uid") String uid,@Query("phone") String phone);

    /**
     * 商家收入明细
     * @param params
     * @return
     */
    @GET("benefit/api/tMerchantsIncomeDetails/page")
    Call<RiderIncome> getMerchantIncome(@QueryMap Map<String,String> params);

    /**
     * 商家提现明细
     * @param params
     * @return
     */
    @GET("benefit/api/tMerchantsIncomeWithdrawal/page")
    Call<RiderIncome> getMerchantWithdraw(@QueryMap Map<String,String> params);

    /**
     * 获取商家订单
     * @param body
     * @return
     */
    @POST("route/shop/api/order/agentPages")
    Call<MerchantOrder> merchantOrder(@Body RequestBody body);


}
