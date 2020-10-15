package com.weiwei.salemall.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.roundview.RoundTextView;
import com.gxz.PagerSlidingTabStrip;
import com.hwtx.dududh.R;
import com.service.helper.BDLBSMapHelper;
import com.service.listener.OnAddressCallback;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.activity.RefuelVideoActivity;
import com.weiwei.home.adapter.RecycleItemClick;
import com.weiwei.home.dialog.ChooseAddressDialog;
import com.weiwei.home.dialog.ChooseAreaDialog;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.rider.adapter.ViewSwitcherHelper;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.activity.OrderEnsureActivity;
import com.weiwei.salemall.adapter.BannerAdapter;
import com.weiwei.salemall.adapter.DeliveryMethodAdapter;
import com.weiwei.salemall.adapter.DeliveryWayAdapter;
import com.weiwei.salemall.adapter.GoodsCommentAdapter;
import com.weiwei.salemall.adapter.GoodsPropertyAdapter;
import com.weiwei.salemall.adapter.NetworkImageHolderView;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.DeliveryModeBean;
import com.weiwei.salemall.bean.DeliveryWay;
import com.weiwei.salemall.bean.InventoryBean;
import com.weiwei.salemall.bean.JDInventory;
import com.weiwei.salemall.bean.ProductCommentEntity;
import com.weiwei.salemall.bean.ProductEntity;
import com.weiwei.salemall.bean.ProductPropertyEntity;
import com.weiwei.salemall.bean.PropertyBean;
import com.weiwei.salemall.bean.ResultDataEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.inter.OnFragmentClickListener;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DateUtil;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.StringTextUtils;
import com.weiwei.salemall.utils.TimeChangeUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.CommonPopupWindow;
import com.weiwei.salemall.widget.CustomLinearLayoutManager;
import com.weiwei.salemall.widget.SlideDetailsLayout;
import com.weiwei.salemall.widget.StarBar;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.query.Query;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;
import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.base.Const.BASE_IMAGE_URL;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/08/09
 * item页ViewPager里的商品Fragment
 * <!-- 商品详情页 - 商品信息 -- >
 */
public class GoodsInfoFragment extends Fragment implements View.OnClickListener, SlideDetailsLayout.OnSlideDetailsListener, PopupWindow.OnDismissListener, OnFragmentClickListener, OnAddressCallback {

    private static final String TAG = "GoodsInfoFragment";
    private PagerSlidingTabStrip psts_tabs;
    private SlideDetailsLayout sv_switch;
    private ScrollView sv_goods_info;
    private FloatingActionButton fab_up_slide;
    public ConvenientBanner vp_item_goods_img, vp_recommend;
    private LinearLayout ll_goods_detail, ll_goods_config;
    private TextView tv_goods_detail, tv_goods_config;
    private View v_tab_cursor;
    public FrameLayout fl_content;
    public LinearLayout ll_comment, ll_recommend, ll_pull_up;
    public TextView productName, mallPrice, jdPrice, tv_current_goods, tv_comment_count, tv_good_comment;
    private TextView tv_title;
    private TextView tv_sale_number;
    private RelativeLayout ll_current_goods;

    /**
     * 当前商品详情数据页的索引分别是图文详情、规格参数
     */
    private int nowIndex;
    private float fromX;
    public GoodsConfigFragment goodsConfigFragment;
    public GoodsInfoWebFragment goodsInfoWebFragment;
    private Fragment nowFragment;
    private List<TextView> tabTextList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public GoodsDetailActivity activity;
    private LayoutInflater inflater;

    private PopupWindow popupWindow;
    private String productNo;
    private String seckillProductId;
    private String columnId;
    private StarBar star_describe;
    private LinearLayout ll_normal_shop;

    /**
     * 秒杀商品标志（区别界面）  +  秒杀相关界面
     */
    private String mSeckill;
    private LinearLayout rl_seckill_price;
    private LinearLayout mLlDuduSeckill;
    private String seckillPrice = "";

    private TextView tv_seckill_price; //秒杀价
    private TextView tv_seckill_compare_price; //秒杀对比价
    private TextView tv_seckill_sold; //已抢
    private TextView nextTv;
    private TextView tv_miaosha_shi;
    private TextView tv_miaosha_minter;
    private TextView tv_miaosha_second;

    private TextView mTvDuduSeckillPrice; //秒杀价
    private TextView mTvDuduSeckillComparePrice; //秒杀对比价
    private TextView mTvDuduSeckillSold; //已抢
    private TextView mTvDuduSeckillMiaoShaHour;
    private TextView mTvDuduSeckillMiaoShaMinter;
    private TextView mTvDuduSeckillMiaoShaSecond;
    private TextView mTvDuduSeckillStatus;

    private LinearLayout rl_seckill_shop;
    private TextView tv_sales_volume;  //销量
    private StarBar seckill_star_describe; //评分
    private String status;

    private RelativeLayout ll_delivery_method;
    private TextView tv_delivery_method;

    private ViewPager viewPager;
    private LinearLayout llDel;


    private String startTime;
    private String endTime;
    private String nowTime;
    private long time_ms;
    private long time_ms_v2;

    private List<PropertyBean> propertyBeanList = new ArrayList<>();
    private List<InventoryBean> inventoryBeanList = new ArrayList<>();
    private GoodsPropertyAdapter goodsPropertyAdapter;
    Map<String, String> map = new HashMap<>();
    LinkedHashMap<String, String> map_property = new LinkedHashMap<>();
    LinkedHashMap<String, String> map_data = new LinkedHashMap<>();

    private RecyclerView rv_info_comment;

    private TextView tv_add;

    private String shop_name;
    private String shop_image_preview;
    private String mall_price_true = "";
    private String jd_price_true = "";
    private String jd_price = "";
    private String mHolidaySeckillProductId;

    /**
     * 兑换商品标志（区别界面）
     */
    private String isExchange;
    private String conversionPrice = "";   //兑换价
    private String coupon = "";            //加油余额
    private String stock = "";             //库存
    private String selectStock = "";
    private int shop_number = 1;
    private String desc = "";
    private String property;
    private int logisticsMinNum = 1;    //物流配送最低购买数量
    private int addNum = 1;             //物流单次累加数量
    private int mCourierMinNum = 1;             //快递配送最低购买数量
    private int mCourierMinAddNum = 1;             //快递单次累加数量

    private List<DeliveryModeBean> deliveryModeBeanList;
    private String deliveryMode;
    private DeliveryMethodAdapter deliveryMethodAdapter;

    private NetworkImageHolderView networkImageHolderView;
    /**
     * 购物车结算到确定订单界面flag
     */
    private int orderEnsureActivityFlag = 0;

    private List<String> imgUrls = new ArrayList<>();;
    private String spu = "";
    private String comprehensive;
    private int contrastSource;
//    private String[] compareCompany = new String[]{"亚马逊", "京东价", "淘宝价", "天猫价", "苏宁价", "当当价", "国美价", "其他价"};

    String propertyJson = "";

    private static final String BUY = "buy";
    private static final String ADDSHOPPINGCART = "addshoppongcart";
    public static final String action = "com.duduhx.shoppingcartNum";
    private static final String SECKILL = "seckill";
    private static final String DUDU_SECKILL = "duduSeckill";

    private ShoppingItemsBeanDao shoppingItemsBeanDao;
    IntentFilter intentFilter;

    /**
     * 界面显示评论
     */
    private GoodsCommentAdapter commentAdapter;
    private List<ProductCommentEntity> goodsCommentEntityList = new ArrayList<>();

    /**
     * 选中属性后的参数
     */
    private String selectConversionPrice = "";
    private String selectCoupon = "";
    private String selectisExchange;
    private String selectImageUrl = "";
    private String selectSeckillPrice = "";
    private String selectDuduPrice = "";
    private String selectJdPrice = "";

    private CommonPopupWindow deliveryMethodPop;
    private String deliveryMethodMsg = "";
    private String deliveryType = "";

    private int type;

    /**
     * 最大最小限购数量
     */
    private int minNumber = 0;
    private int maxNumber = 0;


    private String mThisImage;
    /**
     * 新会员专区逻辑
     */
    private boolean isMemberRegion = true;

    private LinearLayout mLlChooseAddress;
    private TextView mTvAddress;
    private TextView mTvInventory;
    private RecyclerView mDeliveryRecyclerView;

    private ChooseAddressDialog mChooseAddressDialog;
    private List<AddressEntity> mChooseAddressList;
    private DeliveryWayAdapter mDeliveryWayAdapter;
    private ChooseAreaDialog mChooseAreaDialog;
    private BDLBSMapHelper mBdlbsMapHelper;
    private Map<String, String> mParams = new HashMap<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (GoodsDetailActivity) context;
        if (context instanceof GoodsDetailActivity) {
            activity.setOnFragmentClickListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_goods_info, null);
        initView(rootView);
        initListener();
        initAdapter();
        initData();
        return rootView;
    }

    @Override
    public void onDestroy() {
       GSYVideoManager.releaseAllVideos();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            productNo = bundle.getString("productNo");
            columnId = bundle.getString("columnId");
            columnId = bundle.getString("columnId");
            mSeckill = bundle.getString(SECKILL);
            seckillProductId = bundle.getString("seckillProductId");
            Log.e(TAG, "商品Fragment界面===>" + productNo + "columnId===>" + columnId + "是否属于秒杀商品msg===>" + mSeckill);
            if (StringUtils.isEmpty(columnId)) {
                columnId = "";
            }
            if (StringUtils.isEmpty(seckillProductId)) {
                seckillProductId = "";
            }
            if (SECKILL.equals(mSeckill)) {
                jdPrice.setVisibility(View.GONE);
                rl_seckill_price.setVisibility(View.VISIBLE);
                mLlDuduSeckill.setVisibility(View.GONE);
                ll_normal_shop.setVisibility(View.GONE);
                rl_seckill_shop.setVisibility(View.VISIBLE);
            } else if (DUDU_SECKILL.equals(mSeckill)) {
                jdPrice.setVisibility(View.GONE);
                rl_seckill_price.setVisibility(View.GONE);
                mLlDuduSeckill.setVisibility(View.VISIBLE);
                ll_normal_shop.setVisibility(View.GONE);
                rl_seckill_shop.setVisibility(View.VISIBLE);
            } else {
                mSeckill = "";
                rl_seckill_price.setVisibility(View.GONE);
                mLlDuduSeckill.setVisibility(View.GONE);
                ll_normal_shop.setVisibility(View.VISIBLE);
                rl_seckill_shop.setVisibility(View.GONE);
            }
        }
        initProductInfo();
        setDetailData();
    }

    @Override
    public void onResume() {
        super.onResume();
        vp_item_goods_img.startTurning(8000);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
       if(!isVisibleToUser) {
          GSYVideoManager.onPause();
       }
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        vp_item_goods_img.stopTurning();
    }

    private void initAdapter() {
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(mContext);
        linearLayoutManager.setScrollEnabled(false);
        rv_info_comment.setLayoutManager(linearLayoutManager);
        commentAdapter = new GoodsCommentAdapter(getActivity(), goodsCommentEntityList);
        rv_info_comment.setAdapter(commentAdapter);
    }

    private void initListener() {
        fab_up_slide.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_current_goods.setOnClickListener(this);
        ll_pull_up.setOnClickListener(this);
        ll_goods_detail.setOnClickListener(this);
        ll_goods_config.setOnClickListener(this);
        sv_switch.setOnSlideDetailsListener(this);
        ll_delivery_method.setOnClickListener(this);

        mLlChooseAddress.setOnClickListener(this);
    }

    private void initView(View rootView) {
        psts_tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.psts_tabs);
        fab_up_slide = (FloatingActionButton) rootView.findViewById(R.id.fab_up_slide);
        sv_switch = (SlideDetailsLayout) rootView.findViewById(R.id.sv_switch);
        sv_goods_info = (ScrollView) rootView.findViewById(R.id.sv_goods_info);
        v_tab_cursor = rootView.findViewById(R.id.v_tab_cursor);
        vp_item_goods_img = (ConvenientBanner) rootView.findViewById(R.id.vp_item_goods_img);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
        ll_comment = (LinearLayout) rootView.findViewById(R.id.ll_comment);
        ll_current_goods = (RelativeLayout) rootView.findViewById(R.id.ll_current_goods);
        ll_pull_up = (LinearLayout) rootView.findViewById(R.id.ll_pull_up);
        ll_goods_detail = (LinearLayout) rootView.findViewById(R.id.ll_goods_detail);
        ll_goods_config = (LinearLayout) rootView.findViewById(R.id.ll_goods_config);
        tv_goods_detail = (TextView) rootView.findViewById(R.id.tv_goods_detail);
        tv_goods_config = (TextView) rootView.findViewById(R.id.tv_goods_config);
        productName = (TextView) rootView.findViewById(R.id.tv_goods_title);
        mallPrice = (TextView) rootView.findViewById(R.id.tv_mall_price);
        jdPrice = (TextView) rootView.findViewById(R.id.tv_jd_price);
        tv_current_goods = (TextView) rootView.findViewById(R.id.tv_current_goods);
        tv_comment_count = (TextView) rootView.findViewById(R.id.tv_comment_count);
        tv_good_comment = (TextView) rootView.findViewById(R.id.tv_good_comment);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_sale_number = (TextView) rootView.findViewById(R.id.tv_sale_number);
        rv_info_comment = (RecyclerView) rootView.findViewById(R.id.rv_info_comment);
        star_describe = (StarBar) rootView.findViewById(R.id.star_describe);
        star_describe.setDisableClick(true);
//        jdPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        fab_up_slide.hide();
        ll_normal_shop = (LinearLayout) rootView.findViewById(R.id.ll_normal_shop);

        /**=================秒杀相关===================**/
        rl_seckill_price = (LinearLayout) rootView.findViewById(R.id.rl_seckill_price);
        tv_seckill_price = (TextView) rootView.findViewById(R.id.tv_seckill_price);
        tv_seckill_compare_price = (TextView) rootView.findViewById(R.id.tv_seckill_compare_price);
        tv_seckill_compare_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_seckill_compare_price.getPaint().setAntiAlias(true);//抗锯齿

        tv_seckill_sold = (RoundTextView) rootView.findViewById(R.id.tv_seckill_sold);
        nextTv = (TextView) rootView.findViewById(R.id.tv_end);
        tv_miaosha_shi = (TextView) rootView.findViewById(R.id.tv_miaosha_shi);
        tv_miaosha_minter = (TextView) rootView.findViewById(R.id.tv_miaosha_minter);
        tv_miaosha_second = (TextView) rootView.findViewById(R.id.tv_miaosha_second);
        rl_seckill_shop = (LinearLayout) rootView.findViewById(R.id.rl_seckill_shop);
        tv_sales_volume = (TextView) rootView.findViewById(R.id.tv_sales_volume);
        seckill_star_describe = (StarBar) rootView.findViewById(R.id.seckill_star_describe);

        /**=================嘟嘟秒杀相关===================**/
        mLlDuduSeckill = (LinearLayout) rootView.findViewById(R.id.ll_dudu_seckill);
        mTvDuduSeckillPrice = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_price);
        mTvDuduSeckillComparePrice = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_compare_price);
        mTvDuduSeckillSold = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_sold);
        mTvDuduSeckillMiaoShaHour = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_miaosha_hour);
        mTvDuduSeckillMiaoShaMinter = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_miaosha_minter);
        mTvDuduSeckillMiaoShaSecond = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_miaosha_second);
        mTvDuduSeckillStatus = (TextView) rootView.findViewById(R.id.tv_dudu_seckill_status);

        vp_item_goods_img.setPageIndicator(new int[]{R.drawable.index_white_point, R.drawable.index_blue_point});
        vp_item_goods_img.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        ll_delivery_method = (RelativeLayout) rootView.findViewById(R.id.ll_delivery_method);
        tv_delivery_method = (TextView) rootView.findViewById(R.id.tv_delivery_method);

        mLlChooseAddress = rootView.findViewById(R.id.ll_choose_address);
        mTvAddress = rootView.findViewById(R.id.tv_address);
        mTvInventory = rootView.findViewById(R.id.tv_inventory);
        mDeliveryRecyclerView = rootView.findViewById(R.id.delivery_recyclerView);

        viewPager = rootView.findViewById(R.id.viewPager);
        llDel = rootView.findViewById(R.id.ll_del);

        //新会员专区
        Bundle arguments = getArguments();
        if (arguments != null) {
            String mDetailsType = arguments.getString("type");
            isMemberRegion = !TextUtils.isEmpty(mDetailsType) && getString(R.string.member_region).equals(mDetailsType);
        }
        if (isMemberRegion) {
            rootView.findViewById(R.id.tv_end).setVisibility(View.GONE);
            rootView.findViewById(R.id.iv_seckill_light).setVisibility(View.GONE);
            rootView.findViewById(R.id.ll_countdownView).setVisibility(View.GONE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                GSYVideoManager.onPause();
                if(viewPager.getCurrentItem() == position) {
                    llDel.getChildAt(position).setEnabled(true);
                    if(position > 0) {
                        llDel.getChildAt(position - 1).setEnabled(false);
                    }
                    if(position < imgUrls.size() - 1) {
                        llDel.getChildAt(position + 1).setEnabled(false);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initProductInfo() {
        getProductDetail();
        if (VsUtil.checkHasAccount(mContext)) {
            getChooseAddress();
        } else {
            mBdlbsMapHelper.getAddressDetail(mContext, GoodsInfoFragment.this);
        }
    }


    private void getJDInventory() {

        RetrofitClient.getInstance(mContext).Api()
                .getJDInventory(mParams)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        ResultEntity result = response.body();
                        if (!TextUtils.isEmpty(result.getMsg())) {
                            ArmsUtils.makeText(mContext, "" + result.getMsg());
                        }
                        if (result.getCode() != 0) {
                            return;
                        }
                        if (result.getData() == null) {
                            return;
                        }
                        JDInventory jdInventory = JSON.parseObject(result.getData().toString(), JDInventory.class);

                        mTvInventory.setVisibility(View.VISIBLE);
                        mTvInventory.setText(jdInventory.isHasStock);

                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        mTvInventory.setVisibility(View.GONE);
                    }
                });

    }

    private void getProductDetail() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> param = new HashMap<>();
        param.put("columnId", columnId);
        retrofit2.Call<ResultEntity> call = null;
        if (!StringUtils.isEmpty(mSeckill)) {
            switch (mSeckill) {
                //获取秒杀商品详情
                case SECKILL:
                    call = api.getSecKillProductDetail(seckillProductId);
                    break;
                //获取嘟嘟秒杀商品详情
                case DUDU_SECKILL:
                    call = api.getDuduSeckillDetail(seckillProductId);
                    break;
                default:
                    break;
            }
        } else {
            if (!TextUtils.isEmpty(productNo)) {
                productNo = productNo.trim();
            }
            call = api.getProductDetail(productNo, param);
        }
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                activity.loadingDialog.setLoadingDialogDismiss();
                if (response.body() == null) {

                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
//                    Log.e(TAG, "商品详情msg===>" + result.getData().toString());
                    ProductEntity productEntity = JSON.parseObject(result.getData().toString(), ProductEntity.class);
                    activity.productName = productEntity.getProductName();
                    activity.productImage =  BASE_IMAGE_URL + productEntity.getPicture();
 //                   LogUtils.e("productEntity:",productEntity.toString());
                    if(productEntity != null && productEntity.getVideos() != null && productEntity.getVideos().size() > 0) {
                       imgUrls.addAll(productEntity.getVideos());
                       vp_item_goods_img.setVisibility(View.GONE);
                       viewPager.setVisibility(View.VISIBLE);
                       llDel.setVisibility(View.VISIBLE);
                    }
                    if (productEntity != null && productEntity.getImages() != null && productEntity.getImages().size() > 0) {
                        imgUrls.addAll(productEntity.getImages());
                    }
                    if(productEntity != null && productEntity.getVideos() != null && productEntity.getVideos().size() > 0) {
                        setBannerview(imgUrls);

                    }else {
                        vp_item_goods_img.setPages(new CBViewHolderCreator() {
                            @Override
                            public Object createHolder() {
                                return new NetworkImageHolderView();
                            }
                        }, imgUrls);
                    }
                    shop_image_preview = productEntity.getPicture();
                    mall_price_true = productEntity.getPrice();

                    mHolidaySeckillProductId = productEntity.holidaySeckillProductId;

                    getProductProperty();
                    getProductComment();

                    jd_price_true = productEntity.getJdPrice();
                    jd_price = productEntity.getJdPrice2();
                    shop_name = productEntity.getProductName();
                    spu = productEntity.getSpu();
                    comprehensive = productEntity.getComprehensive();
                    contrastSource = productEntity.getContrastSource();
                    conversionPrice = productEntity.getConversionPrice();
                    coupon = productEntity.getCoupon();
                    isExchange = productEntity.getIsExchange();
                    deliveryMode = productEntity.getDeliveryMode();
                    logisticsMinNum = productEntity.getLogisticsMinNum();
                    addNum = productEntity.getAddNum();
                    mCourierMinNum = productEntity.courierMinNum;
                    mCourierMinAddNum = productEntity.courierMinAddNum;
                    List<DeliveryModeBean> list = JSON.parseArray(deliveryMode, DeliveryModeBean.class);
                    List<DeliveryWay> deliveryList = JSON.parseArray(deliveryMode, DeliveryWay.class);
                    deliveryModeBeanList = new ArrayList<>();
                    if (list != null && list.size() > 0) {
                        deliveryList.get(0).check = true;
                        mDeliveryWayAdapter.setList(deliveryList);
                        mDeliveryWayAdapter.notifyDataSetChanged();
                        deliveryModeBeanList.addAll(list);
                    }
                    if (deliveryModeBeanList != null && deliveryModeBeanList.size() > 0) {
                        deliveryType = deliveryModeBeanList.get(0).getCode();
                        deliveryMethodMsg = deliveryModeBeanList.get(0).getName();
                        tv_delivery_method.setText(deliveryMethodMsg);
                    }

                    productName.setText(StringTextUtils.stringFilter(shop_name));

                    tv_sale_number.setText("已售" + spu);
                    star_describe.setStarMark(Float.parseFloat(comprehensive));

                    /**===============兑换专区相关==================**/
                    if (!StringUtils.isEmpty(isExchange)) {
                        //CCTV
                        if (isExchange.equals("y")) {
                            //TODO MINE
//                            String str = Constant.compareFactory[contrastSource] + "：￥" + jdPrice;
                            jdPrice.setText(jd_price_true);
//                            jdPrice.setVisibility(View.GONE);
                        } else {
                            jdPrice.setText(jd_price_true);
                        }
                    } else {
                        jdPrice.setText(jd_price_true);
                    }

                    if (!StringUtils.isEmpty(conversionPrice)) {
                        mallPrice.setText("¥ " + conversionPrice + "+" + coupon + "积分兑换");
                    } else {
                        mallPrice.setText("¥ " + mall_price_true);
                    }

                    type = productEntity.getType();
                    if (type == 3) {
                        ll_delivery_method.setEnabled(false);
                    } else {
                        ll_delivery_method.setEnabled(true);
                    }

                    /**===========================秒杀相关===============**/
                    if (SECKILL.equals(mSeckill) || DUDU_SECKILL.equals(mSeckill)) {
                        tv_sales_volume.setText("销量：" + spu + "件");
                    }

                    minNumber = productEntity.getMinNum();
                    if (minNumber != 0) {
                        shop_number = minNumber;
                    }
                    maxNumber = productEntity.getMaxNum();
                    seckill_star_describe.setStarMark(Float.parseFloat(comprehensive));
                    seckillPrice = productEntity.getSeckillPrice();
                    status = productEntity.getStatus();
                    stock = productEntity.getStock();
                    activity.remainingStockTv.setText(stock + "件");
                    activity.mTvDuduRemainingStock.setText(stock);

                    activity.mTvDuduRemainingStock.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    activity.mTvDuduRemainingStock.getPaint().setAntiAlias(true);//抗锯齿

                    tv_seckill_price.setText("¥" + seckillPrice);
                    mTvDuduSeckillPrice.setText("¥" + seckillPrice);
                    String comSeckillPrice = productEntity.getPrice();
                    tv_seckill_compare_price.setText(TextDisposeUtils.dispseMoneyText(comSeckillPrice));

                    mTvDuduSeckillComparePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    mTvDuduSeckillComparePrice.getPaint().setAntiAlias(true);//抗锯齿
                    mTvDuduSeckillComparePrice.setText(TextDisposeUtils.dispseMoneyText(comSeckillPrice));

                    if (productEntity.getTotalStock() != null && productEntity.getStock() != null) {
                        int saleNum = Integer.parseInt(productEntity.getTotalStock()) - Integer.parseInt(productEntity.getStock());
                        tv_seckill_sold.setText("已抢" + saleNum + "件");
                        mTvDuduSeckillSold.setText("已抢" + saleNum + "件");
                    }

                    if (productEntity.getStartTime() != null && productEntity.getEndTime() != null) {
                        startTime = DateUtil.stampToDate(productEntity.getStartTime());
                        endTime = DateUtil.stampToDate(productEntity.getEndTime());
                        nowTime = DateUtil.stampToDate(productEntity.getNow());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        /***获取当前时间***/
                        try {
                            Date toDate = df.parse(endTime);
                            Date fromDate = df.parse(nowTime);
                            Date toDate_v2 = df.parse(startTime);
                            if (toDate.getTime() > fromDate.getTime()) {
                                time_ms = (toDate.getTime() - fromDate.getTime()) / 1000;
                            }

                            if (toDate_v2.getTime() > fromDate.getTime()) {
                                time_ms_v2 = (toDate_v2.getTime() - fromDate.getTime()) / 1000;
                            }

                            if (!StringUtils.isEmpty(status)) {
                                switch (status) {
                                    case "end":
                                        nextTv.setText("秒杀结束");
                                        break;
                                    case "underway":
                                        nextTv.setText("秒杀进行中");
                                        countTime();
                                        break;
                                    case "wait":
                                        nextTv.setText("秒杀未开始");
                                        countTime();
                                        break;
                                    default:
                                        break;
                                }
                                freshUi(status, stock);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                activity.loadingDialog.setLoadingDialogDismiss();
            }
        });
    }

    /**
     * 顶部轮播图
     */
    private OrientationUtils orientationUtils;
    private List<View> mViewlist = new ArrayList<>();

    protected void setBannerview(List<String> imgUrls) {
        addPoint(imgUrls.size());
        //区分图片和视频布局
        for (int i=0;i<imgUrls.size();i++){
            if (imgUrls.get(i).endsWith("jpg")||imgUrls.get(i).endsWith(".png")) {//1 为视频 以下布局中是视频布局
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_image, null);
                ImageView _imageview = view.findViewById(R.id.iv_center);
                Glide.with(getContext()).load(BASE_IMAGE_URL + imgUrls.get(i)).into(_imageview);
                mViewlist.add(view);
            } else {//以下布局中为视频布局
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_video, null);
                StandardGSYVideoPlayer videoplayer =view.findViewById(R.id.player);
                videoplayer.getBackButton().setVisibility(View.GONE);

                orientationUtils = new OrientationUtils(getActivity(),videoplayer);
                //初始化不打开外部的旋转
                orientationUtils.setEnable(false);
                ImageView imagePlayer = new ImageView(getContext());
                loadVideoScreenshot(getContext(),BASE_IMAGE_URL + imgUrls.get(i),imagePlayer,10000);
                GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
                gsyVideoOption.setThumbImageView(imagePlayer)
                        .setIsTouchWiget(false)
                        .setReleaseWhenLossAudio(false)
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(true)
                        .setNeedLockFull(false)
                        .setUrl(BASE_IMAGE_URL + imgUrls.get(i))
                        .setCacheWithPlay(true)
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);
                                //开始播放了才能旋转和全屏
                                if(orientationUtils != null) {
                                    orientationUtils.setEnable(false);
                                }
                            }
                            @Override
                            public void onQuitFullscreen(String url, Object... objects) {
                                super.onQuitFullscreen(url, objects);
                                if (orientationUtils != null) {
                                    orientationUtils.backToProtVideo();
                                }
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                            }
                        }).setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(false);
                        }
                    }
                }).build(videoplayer);

                videoplayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //直接横屏
                        orientationUtils.resolveByClick();
                        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                        videoplayer.startWindowFullscreen(getContext(), true, true);
                    }
                });
                mViewlist.add(view);
            }
        }
        viewPager.setAdapter(new BannerAdapter(mViewlist));

    }
    //todo 添加小圆点的方法
    private void addPoint(int size) {
        View view;
        for (int i = 0; i < size; i++) {
            //创建底部指示器(小圆点)
            view = new View(getContext());
            view.setBackgroundResource(R.drawable.background_point);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            //设置间隔
            if (i != 0) {
                layoutParams.leftMargin = 20;
            }
            //添加到LinearLayout
            llDel.addView(view, layoutParams);
        }
        llDel.getChildAt(0).setEnabled(true);
    }

    /**
     * 加载视频缩略图
     * @param context
     * @param uri
     * @param imageView
     * @param frameTimeMicros
     */
    private void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }


    /**
     * 秒杀状态改变后更新UI
     *
     * @param status
     */
    private void freshUi(String status, String stock) {
        //新会员专区
        if (isMemberRegion) {
            status = "underway";
        }
        switch (status) {
            case "end":
                activity.seckillBuyBtn.setText("已结束");
                activity.mBtnDuduSeckillBuy.setText("已结束");
                if (isAdded()) {
                    activity.seckillBuyBtn.setBackgroundColor(getResources().getColor(R.color.soldout));
                    activity.mBtnDuduSeckillBuy.setBackgroundColor(getResources().getColor(R.color.soldout));
                }
                activity.seckillBuyBtn.setClickable(false);
                activity.seckillBuyBtn.setEnabled(false);

                activity.mBtnDuduSeckillBuy.setClickable(false);
                activity.mBtnDuduSeckillBuy.setEnabled(false);
                break;
            case "underway":
                if (!StringUtils.isEmpty(stock) && stock.equals("0")) {
                    activity.seckillBuyBtn.setText("已售罄");
                    activity.mBtnDuduSeckillBuy.setText("已售罄");
                    if (isAdded()) {
                        activity.seckillBuyBtn.setBackgroundColor(getResources().getColor(R.color.soldout));
                        activity.mBtnDuduSeckillBuy.setBackgroundColor(getResources().getColor(R.color.soldout));
                    }
                    activity.seckillBuyBtn.setClickable(false);
                    activity.seckillBuyBtn.setEnabled(false);

                    activity.mBtnDuduSeckillBuy.setClickable(false);
                    activity.mBtnDuduSeckillBuy.setEnabled(false);
                } else {
                    activity.seckillBuyBtn.setText("立即购买");
                    activity.mBtnDuduSeckillBuy.setText("立即购买");
                    mTvDuduSeckillStatus.setText("距活动结束");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isAdded()) {
                        activity.seckillBuyBtn.setBackgroundColor(getResources().getColor(R.color.public_color_FD4900));
                        activity.mBtnDuduSeckillBuy.setBackgroundColor(getResources().getColor(R.color.public_color_FD4900));
                    }
                    activity.seckillBuyBtn.setClickable(true);
                    activity.seckillBuyBtn.setEnabled(true);

                    activity.mBtnDuduSeckillBuy.setClickable(true);
                    activity.mBtnDuduSeckillBuy.setEnabled(true);
                }
                break;
            case "wait":
                activity.seckillBuyBtn.setText("未开始");
//                activity.mBtnDuduSeckillBuy.setText("未开始");
                mTvDuduSeckillStatus.setText("距活动开始");
                if (isAdded()) {
                    activity.seckillBuyBtn.setBackgroundColor(getResources().getColor(R.color.soldout));
//                    activity.mBtnDuduSeckillBuy.setBackgroundColor(getResources().getColor(R.color.soldout));
                }
                activity.seckillBuyBtn.setClickable(false);
                activity.seckillBuyBtn.setEnabled(false);

//                activity.mBtnDuduSeckillBuy.setClickable(false);
//                activity.mBtnDuduSeckillBuy.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void countTime() {
        handler.postDelayed(runnable, 1000);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time_ms--;
            time_ms_v2--;
            String formatLongToTimeStr = "";
            switch (status) {
                case "underway":
                    formatLongToTimeStr = TimeChangeUtils.getInstance(getActivity()).formatLongToTimeStr(time_ms);
                    break;
                case "wait":
                    formatLongToTimeStr = TimeChangeUtils.getInstance(getActivity()).formatLongToTimeStr(time_ms_v2);
                    break;
                default:
                    break;
            }
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    if (Integer.parseInt(split[0]) >= 10) {
                        tv_miaosha_shi.setText(split[0]);
                        mTvDuduSeckillMiaoShaHour.setText(split[0]);
                    } else {
                        tv_miaosha_shi.setText("0" + split[0]);
                        mTvDuduSeckillMiaoShaHour.setText("0" + split[0]);
                    }
                }
                if (i == 1) {
                    if (Integer.parseInt(split[1]) >= 10) {
                        tv_miaosha_minter.setText(split[1]);
                        mTvDuduSeckillMiaoShaMinter.setText(split[1]);
                    } else {
                        tv_miaosha_minter.setText("0" + split[1]);
                        mTvDuduSeckillMiaoShaMinter.setText("0" + split[1]);
                    }
                }
                if (i == 2) {
                    if (Integer.parseInt(split[2]) >= 10) {
                        tv_miaosha_second.setText(split[2]);
                        mTvDuduSeckillMiaoShaSecond.setText(split[2]);
                    } else {
                        tv_miaosha_second.setText("0" + split[2]);
                        mTvDuduSeckillMiaoShaSecond.setText("0" + split[2]);
                    }
                }
            }

            switch (status) {
                case "underway": /***秒杀进行中倒计时***/
                    if (time_ms > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        handler.removeCallbacks(runnable);
                        getProductDetail();
                    }
                    break;
                case "wait": /***秒杀未开始倒计时***/
                    if (time_ms_v2 > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        handler.removeCallbacks(runnable);
                        //秒杀开始（重新获取商品当前状态）
                        getProductDetail();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void getChooseAddress() {

        Map<String, String> param = new HashMap<>();
        param.put("pageNum", "1");
        param.put("pageSize", "10");

        RetrofitClient.getInstance(getActivity()).Api()
                .getDelivery(param)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        ResultDataEntity addressDataEntity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                        mChooseAddressList = JSON.parseArray(addressDataEntity.getRecords().toString(), AddressEntity.class);

                        if (mChooseAddressList == null || mChooseAddressList.size() == 0) {
                            mBdlbsMapHelper.getAddressDetail(mContext, GoodsInfoFragment.this);
                            return;
                        }

                        AddressEntity zeroItem = mChooseAddressList.get(0);

                        zeroItem.check = true;
                        mTvAddress.setText(zeroItem.getAllAddress());
                        mChooseAddressDialog.setData(mChooseAddressList);

                        mParams.put("province", zeroItem.getProvince());
                        mParams.put("city", zeroItem.getCity());
                        mParams.put("county", zeroItem.getArea());
                        mParams.put("town", zeroItem.getTown());
                        mParams.put("productNo", productNo);
                        getJDInventory();

                    }
                });

    }

    /**
     * 获取商品属性
     */
    private void getProductProperty() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> param = new HashMap<>();
        param.put("columnId", columnId);
        //添加秒杀版本更新
        switch (mSeckill) {
            case SECKILL:
                param.put("seckillProductId", seckillProductId);
                break;
            case DUDU_SECKILL:
                param.put("holidaySeckillProductId", mHolidaySeckillProductId);
                break;
        }
        retrofit2.Call<ResultEntity> call = api.getProductProperty(productNo, param);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "商品属性msg===>" + result.getData().toString());
                    ProductPropertyEntity entity = JSON.parseObject(result.getData().toString(), ProductPropertyEntity.class);
                    propertyBeanList.addAll(entity.getProperty());
                    inventoryBeanList.addAll(entity.getInventory());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 获取商品评论
     */
    private void getProductComment() {
        ApiService api = RetrofitClient.getInstance(getActivity()).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", 1 + "");
        params.put("pageSize", 10 + "");
        params.put("productNo", productNo);
        params.put("type", 0 + "");
        params.put("columnId", columnId);
        switch (mSeckill) {
            case SECKILL:
                params.put("seckillProductId", seckillProductId);
                break;
            case DUDU_SECKILL:
                params.put("holidaySeckillProductId", mHolidaySeckillProductId);
                break;
        }
        retrofit2.Call<ResultEntity> call = api.getCommentPage(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Log.e(TAG, "商品好评msg===>" + result.getData().toString());
                ResultDataEntity entity = JSON.parseObject(result.getData().toString(), ResultDataEntity.class);
                List<ProductCommentEntity> commentEntityList = JSON.parseArray(entity.getRecords().toString(), ProductCommentEntity.class);
                if (commentEntityList != null) {
                    goodsCommentEntityList.addAll(commentEntityList);
                }

                if (goodsCommentEntityList.size() > 0) {
                    rv_info_comment.setVisibility(View.VISIBLE);
                    tv_comment_count.setText("(" + goodsCommentEntityList.size() + ")");
                } else {
                    rv_info_comment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void initData() {

        fragmentList = new ArrayList<>();
        tabTextList = new ArrayList<>();
        tabTextList.add(tv_goods_detail);
        tabTextList.add(tv_goods_config);

        mChooseAddressDialog = new ChooseAddressDialog(getActivity());
        mChooseAddressDialog.mAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                AddressEntity item = mChooseAddressList.get(position);

                mParams.put("province", item.getProvince());
                mParams.put("city", item.getCity());
                mParams.put("county", item.getArea());
                mParams.put("town", item.getTown());
                mParams.put("productNo", productNo);

                mTvAddress.setText(item.getAllAddress());
                getJDInventory();

                mChooseAddressDialog.dismiss();
            }
        });
        mChooseAddressDialog.setOnClickListener(new ChooseAddressDialog.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_choose_address_close:
                        mChooseAddressDialog.dismiss();
                        break;
                    case R.id.rtv_choose_else_address:
                        mChooseAreaDialog.show();
                        break;
                }
            }
        });

        mChooseAreaDialog = new ChooseAreaDialog(getActivity());
        mChooseAreaDialog.setOnClickListener(new ChooseAreaDialog.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_cancel:
                        mChooseAreaDialog.dismiss();
                        break;
                    case R.id.tv_confirm:

                        mParams.put("province", mChooseAreaDialog.mCurrentProvinceName);
                        mParams.put("city", mChooseAreaDialog.mCurrentCityName);
                        mParams.put("county", mChooseAreaDialog.mCurrentDistrictName);
                        mParams.put("town", mChooseAreaDialog.mCurrentTownName);
                        mParams.put("productNo", productNo);

                        mTvAddress.setText(mChooseAreaDialog.getAllArea());
                        getJDInventory();

                        mChooseAreaDialog.dismiss();

                        if (mChooseAddressList == null || mChooseAddressList.size() == 0) {
                            return;
                        }


                        mChooseAddressDialog.mAdapter.cancelCheck();
                        mChooseAddressDialog.dismiss();
                        break;
                }
            }
        });

        mDeliveryWayAdapter = new DeliveryWayAdapter(getActivity());
        mDeliveryWayAdapter.setRecycleClickListener(new RecycleItemClick() {
            @Override
            public void onItemClick(int position) {
                DeliveryWay item = mDeliveryWayAdapter.getList().get(position);
                deliveryType = item.code;
                deliveryMethodMsg = item.name;
            }
        });
        mDeliveryRecyclerView.setAdapter(mDeliveryWayAdapter);

        mBdlbsMapHelper = new BDLBSMapHelper();

    }

    /**
     * 加载完商品详情执行
     */
    public void setDetailData() {
        goodsConfigFragment = GoodsConfigFragment.newInstance(productNo, columnId, seckillProductId);
        goodsInfoWebFragment = GoodsInfoWebFragment.newInstance(productNo, mSeckill, columnId, seckillProductId);
        fragmentList.add(goodsConfigFragment);
        fragmentList.add(goodsInfoWebFragment);

        nowFragment = goodsInfoWebFragment;
        fragmentManager = getChildFragmentManager();
        //默认显示商品详情tab
        fragmentManager.beginTransaction().replace(R.id.fl_content, nowFragment).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_pull_up:
                //上拉查看图文详情
                scrollCursor();
                sv_switch.smoothOpen(false);
                break;
            case R.id.fab_up_slide:
                //点击滑动到顶部
                scrollCursor();
                sv_goods_info.smoothScrollTo(0, 0);
                sv_switch.smoothClose(false);
                break;
            case R.id.ll_goods_detail:
                //商品详情tab
                nowIndex = 0;
                scrollCursor();
                activity.tv_title.setText("商品详情");
                switchFragment(nowFragment, goodsInfoWebFragment);
                nowFragment = goodsInfoWebFragment;
                Bundle bundle = new Bundle();
                bundle.putString("productNo", productNo);
                bundle.putString("columnId", columnId);
                bundle.putString("seckillProductId", seckillProductId);
                nowFragment.setArguments(bundle);
                break;
            case R.id.ll_goods_config:
                //规格参数tab
                nowIndex = 1;
                scrollCursor();
                activity.tv_title.setText("规格参数");
                activity.tv_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                switchFragment(nowFragment, goodsConfigFragment);
                nowFragment = goodsConfigFragment;
                Bundle bundle1 = new Bundle();
                bundle1.putString("productNo", productNo);
                bundle1.putString("columnId", columnId);
                bundle1.putString("seckillProductId", seckillProductId);
                nowFragment.setArguments(bundle1);
                break;
            case R.id.ll_delivery_method:
                showDeliveryMethodDialog(v);
                break;
            case R.id.ll_current_goods:
                OnFragmentClick("buy");
                break;
            case R.id.ll_choose_address:
                if (mChooseAddressList == null || mChooseAddressList.size() == 0) {
                    mChooseAreaDialog.show();
                    return;
                }
                mChooseAddressDialog.show();
                break;
            default:
                break;
        }
    }

    private void showDeliveryMethodDialog(View view) {
        deliveryMethodPop = new CommonPopupWindow.Builder(getActivity()).setView(R.layout.pop_delivery_method).setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT).setAnimationStyle(R.style.PopupWindowAnimation).setBackGroundLevel(0.5f).setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
            @Override
            public void getChildView(View view, int layoutResId) {
                TextView confirmTv = (TextView) view.findViewById(R.id.dialog_confirm);
                RecyclerView rv_delivery_method = (RecyclerView) view.findViewById(R.id.rv_delivery_method);

                CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(mContext);
                linearLayoutManager.setScrollEnabled(false);
                rv_delivery_method.setLayoutManager(linearLayoutManager);
                if (deliveryModeBeanList != null) {
                    deliveryMethodAdapter = new DeliveryMethodAdapter(getActivity(), deliveryModeBeanList, deliveryType);
                    rv_delivery_method.setAdapter(deliveryMethodAdapter);
                }

                confirmTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectPosition = deliveryMethodAdapter.getCheckedPosition();
                        if (selectPosition > -1) {
                            deliveryType = deliveryModeBeanList.get(selectPosition).getCode();
                            deliveryMethodMsg = deliveryModeBeanList.get(selectPosition).getName();
                        }
                        tv_delivery_method.setText(deliveryMethodMsg);
                        deliveryMethodPop.dismiss();
                    }
                });
            }
        }).setOutsideTouchable(true).create();
        deliveryMethodPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    private Map<String, String> mSelectMap = new HashMap<>();

    /**
     * 显示商品规格弹窗
     *
     * @param v
     */
    private void showGoodsParamsDialog(View v, String flag) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_good_paramter, null);
        final TextView btn_buy = (TextView) contentView.findViewById(R.id.btn_buy);

        if (flag.equals(BUY)) {
            btn_buy.setText("立即购买");
        } else if (flag.equals(ADDSHOPPINGCART)) {
            btn_buy.setText("加入购物车");
        }

        /***库存***/
        final TextView inventoryTv = (TextView) contentView.findViewById(R.id.tv_shop_inventory);
        /***商城价***/
        final TextView mallPriceTv = (TextView) contentView.findViewById(R.id.tv_mall_price);
        /***京东价***/
        final TextView jdPriceTv = (TextView) contentView.findViewById(R.id.tv_jd_price);
        /***属性***/
        final TextView propertyTv = (TextView) contentView.findViewById(R.id.tv_shop_property);
        /***商品图片***/
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_good_image);
        /***属性布局***/
        RecyclerView propertyRv = (RecyclerView) contentView.findViewById(R.id.rv_property);
        /***加减、数量***/
        ImageView addIv = (ImageView) contentView.findViewById(R.id.iv_add);
        final EditText numberEt = (EditText) contentView.findViewById(R.id.et_shop_num);
        ImageView subIv = (ImageView) contentView.findViewById(R.id.iv_sub);

        if ("logistics".equals(deliveryType)) {
            shop_number = addNum;
        } else if ("express".equals(deliveryType)) {
            shop_number = mCourierMinAddNum;
        } else {
            shop_number = 1;
        }

        numberEt.setText(shop_number + "");

        /**=================对秒杀商品进行数量限制===================**/
        addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //秒杀
                if (SECKILL.equals(mSeckill) || DUDU_SECKILL.equals(mSeckill)) {
                    if (shop_number < maxNumber) {
                        if ("logistics".equals(deliveryType)) {
                            shop_number += addNum;
                        } else if ("express".equals(deliveryType)) {
                            shop_number += mCourierMinAddNum;
                        } else {
                            shop_number += 1;
                        }

                        numberEt.setText(shop_number + "");
                    } else {
                        ToastUtils.show(getActivity(), "购买数量已达到最大限购数量");
                        return;
                    }
                } else {                               //非秒杀
                    if ("logistics".equals(deliveryType)) {
                        shop_number += addNum;
                    } else if ("express".equals(deliveryType)) {
                        shop_number += mCourierMinAddNum;
                    } else {
                        shop_number += 1;
                    }
                    numberEt.setText(shop_number + "");
                    Log.e(TAG, "商品数量加减msg===>" + shop_number);
                }
            }
        });

        subIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //秒杀
                if (SECKILL.equals(mSeckill) || DUDU_SECKILL.equals(mSeckill)) {
                    if (shop_number > 1) {
                        if (minNumber < shop_number) {
                            if ("logistics".equals(deliveryType)) {
                                shop_number -= addNum;
                                if (shop_number < addNum) {
                                    shop_number = addNum;
                                }
                            } else if ("express".equals(deliveryType)) {
                                shop_number -= mCourierMinAddNum;
                                if (shop_number < mCourierMinAddNum) {
                                    shop_number = mCourierMinAddNum;
                                }
                            } else {
                                shop_number -= 1;
                            }

                            numberEt.setText(shop_number + "");
                        } else {
                            ToastUtils.show(getActivity(), "购买数量已达到最小限购数量");
                            return;
                        }
                    }
                } else {  //非秒杀
                    if ("logistics".equals(deliveryType)) {
                        if (logisticsMinNum >= shop_number) {
                            ToastUtils.show(getActivity(), "购买数量已达到最小限购数量");
                            return;
                        }
                    } else if ("express".equals(deliveryType)) {
                        if (mCourierMinNum >= shop_number) {
                            ToastUtils.show(getActivity(), "购买数量已达到最小限购数量");
                            return;
                        }
                    } else {
                        if (shop_number == 1) {
                            ToastUtils.show(getActivity(), "购买数量已达到最小限购数量");
                            return;
                        }
                    }
                    if (shop_number > 1) {

                        if ("logistics".equals(deliveryType)) {
                            shop_number -= addNum;
                            if (shop_number < addNum) {
                                shop_number = addNum;
                            }
                        } else if ("express".equals(deliveryType)) {
                            shop_number -= mCourierMinAddNum;
                            if (shop_number < mCourierMinAddNum) {
                                shop_number = mCourierMinAddNum;
                            }
                        } else {
                            shop_number -= 1;
                        }
                        numberEt.setText(shop_number + "");
                        Log.e(TAG, "商品数量加减msg===>" + shop_number);
                    }
                }
            }
        });

        if (!StringUtils.isEmpty(mSeckill) && shop_number > maxNumber) {
            numberEt.setEnabled(false);
        } else {
            numberEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!StringUtils.isEmpty(s.toString())) {
                        try {
                            shop_number = Integer.parseInt(s.toString());
                            if ("logistics".equals(deliveryType)) {
                                while (shop_number % addNum != 0) {
                                    shop_number++;
                                    numberEt.setText(shop_number + "");
                                }
                            } else if ("express".equals(deliveryType)) {
                                while (shop_number % mCourierMinAddNum != 0) {
                                    shop_number++;
                                    numberEt.setText(shop_number + "");
                                }
                            }
                        } catch (NumberFormatException e) {
                            ToastUtils.show(getActivity(), "当前输入数量超出范围");
                            return;
                        }
                    }
                }
            });
        }
        jdPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        /**=================普通商品===================**/
        if (!StringUtils.isEmpty(mall_price_true)) {
            mallPriceTv.setText("¥" + mall_price_true);
        }
        if (!StringUtils.isEmpty(jd_price)) {
            jdPriceTv.setText("价格:  " + "¥" + jd_price);
        } else {
            jdPriceTv.setText("价格:  " + "¥");
        }

        /**=================秒杀商品===================**/
        if (!StringUtils.isEmpty(mSeckill)) {
            switch (mSeckill) {
                case SECKILL:
                case DUDU_SECKILL:
                    mallPriceTv.setText("¥" + seckillPrice);
                    jdPriceTv.setText("价格:  " + "¥" + mall_price_true);
                    break;
                default:
                    break;
            }
        }

        /**=================兑换商品===================**/
        if (!StringUtils.isEmpty(isExchange)) {
            switch (isExchange) {
                case "y":
                    mallPriceTv.setText("¥" + conversionPrice + "+" + coupon + "积分兑换");
                    jdPriceTv.setText("价格:  " + "¥" + jd_price);
                    break;
                default:
                    break;
            }
        }

        if (!StringUtils.isEmpty(shop_image_preview)) {
            String imageUrl = JudgeImageUrlUtils.isAvailable(shop_image_preview);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.mall_logits_default);
            Glide.with(getActivity()).load(imageUrl).apply(options).into(imageView);
        }

        propertyRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        map.clear();
        map_property.clear();
        map_data.clear();
        goodsPropertyAdapter = new GoodsPropertyAdapter(getActivity(), propertyBeanList);
        propertyRv.setAdapter(goodsPropertyAdapter);

        goodsPropertyAdapter.setOnRadioBtnCheckedListener(new GoodsPropertyAdapter.OnRadioBtnCheckedListener() {
            @Override
            public void OnRadioBtnChecked(int propertype, int tag) {
                InventoryBean inventory = null;
                mSelectMap.put(propertyBeanList.get(propertype).getPropertyNameId(),
                        propertyBeanList.get(propertype).getPropertyValues().get(tag).getPropertyValueId());

                map.put(propertyBeanList.get(propertype).getPropertyNameId(),
                        propertyBeanList.get(propertype).getPropertyValues().get(tag).getPropertyValueId());
                map_property.put(propertyBeanList.get(propertype).getPropertyNameId(),
                        propertyBeanList.get(propertype).getPropertyValues().get(tag).getPropertyValueName());
                String nameId = propertyBeanList.get(propertype).getPropertyNameId();
                String valueId = propertyBeanList.get(propertype).getPropertyValues().get(tag).getPropertyValueId();
                map_data.put(String.valueOf(propertype), nameId + "," + valueId);

                //按Key进行排序
//                Map<String, String> resultMap = sortMapByKey(map_data);
                String desc_now = "";
                String desc_second = "";

                for (Map.Entry<String, String> entry : map_property.entrySet()) {
                    desc_now += "[" + entry.getValue() + "]";
                    desc_second += entry.getValue() + ",";
                    Log.e("test data", "====" + desc_now);
                    propertyTv.setText("已选：" + desc_now);
                    desc = desc_second;
                }

                propertyJson = "{";
                for (Map.Entry<String, String> entry : map_data.entrySet()) {
                    String[] values = entry.getValue().toString().split(",");
                    propertyJson += "\"" + values[0] + "\":\"" + values[1] + "\",";
                }
                propertyJson = propertyJson.substring(0, propertyJson.length() - 1);
                propertyJson += "}";

                Log.e("propertyJson==========", propertyJson);

                //遍历inventoryList
                for (int i = 0; i < inventoryBeanList.size(); i++) {
                    boolean isTrue = true;
                    String[] split = propertyJson.split(",");
                    String[] splitInventory = inventoryBeanList.get(i).getProperty().split(",");
                    if (splitInventory.length == split.length) {
                        for (String str : splitInventory) {
                            if (!inventoryBeanList.get(i).getProperty().contains(str)) {
                                isTrue = false;
                                break;
                            }
                        }
                    } else {
                        isTrue = false;
                    }
                    if (isTrue) {
                        String mPJson = "";
                        for (int s = 0; s < inventoryBeanList.size(); s++) {
                            int y = 0;
                            StringBuilder arrayJson2 = new StringBuilder("{");
                            String[] mInventotySplit = inventoryBeanList.get(s).getProperty().split(",");
                            for (int x = 0; x < mInventotySplit.length; x++) {
                                String[] mPropertyArray = mInventotySplit[x].split(":");
                                String replace = mPropertyArray[1].replace("{", "").replace("}", "").replace("\"", "");
                                String key = mPropertyArray[0].replace("{", "").replace("}", "").replace("\"", "");
                                if (mSelectMap.containsKey(key)) {
                                    String value = mSelectMap.get(key).replace("\"", "");
                                    if (replace.contains(value)) {
                                        y++;
                                        arrayJson2.append("\"" + key + "\":\"" + value + "\"");
                                        arrayJson2.append(y == propertyBeanList.size() ? "}" : ",");
                                        if (y == mInventotySplit.length) {
                                            mPJson = arrayJson2.toString();
                                            for (InventoryBean bean : inventoryBeanList) {
                                                if (bean.getProperty().equals(mPJson)) {
                                                    inventory = bean;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        propertyJson = mPJson;

                        selectJdPrice = inventory == null ? inventoryBeanList.get(i).getJdPrice() : inventory.getJdPrice();
                        selectDuduPrice = inventory == null ? inventoryBeanList.get(i).getPrice() : inventory.getPrice();
                        selectStock = inventory == null ? inventoryBeanList.get(i).getStock() : inventory.getStock();
                        selectConversionPrice = inventory == null ? inventoryBeanList.get(i).getConversionPrice() : inventory.getConversionPrice();
                        selectCoupon = inventory == null ? inventoryBeanList.get(i).getCoupon() : inventory.getCoupon();
                        selectisExchange = inventory == null ? inventoryBeanList.get(i).getIsExchange() : inventory.getIsExchange();
                        selectImageUrl = inventory == null ? inventoryBeanList.get(i).getImageUrl() : inventory.getImageUrl();
                        selectSeckillPrice = inventory == null ? inventoryBeanList.get(i).getSeckillPrice() : inventory.getSeckillPrice();
                        //选择对应规格的图片
                        if (!StringUtils.isEmpty(selectImageUrl)) {
                            String available = JudgeImageUrlUtils.isAvailable(selectImageUrl);
                            mThisImage = available;
                            Glide.with(getActivity())
                                    .load(available)
                                    .apply(new RequestOptions().error(R.drawable.mall_logits_default))
                                    .into(imageView);
                        }

                        String shop_property = map.get(propertyBeanList.get(propertype).getPropertyNameId());
                        Log.e(TAG, "选择商品属性msg===>" + shop_property);
                        break;
                    } else {
                        selectStock = "0";
                    }
                    Log.e(TAG, "嘟嘟价格===>" + selectDuduPrice + "对比价===>" + selectJdPrice + "兑换价===>" + selectConversionPrice + "加油余额===>" + selectCoupon + "库存===>" + selectStock);
                }


                /**=================普通商品选中属性===================**/
                if (!StringUtils.isEmpty(selectDuduPrice)) {
                    mallPriceTv.setText("¥" + selectDuduPrice);
                }
                if (!StringUtils.isEmpty(selectJdPrice)) {
                    jdPriceTv.setText("价格:  " + "¥" + selectJdPrice);
                } else {
                    jdPriceTv.setText("价格:  " + "¥" + jd_price);
                }

                if (!StringUtils.isEmpty(selectStock)) {
                    inventoryTv.setText(selectStock);
                } else {
                    inventoryTv.setText("0");
                }

                /**=================兑换商品选中属性===================**/
                if (!StringUtils.isEmpty(isExchange)) {
                    switch (isExchange) {
                        case "y":
                            jdPriceTv.setText("价格:  ¥" + (TextUtils.isEmpty(selectJdPrice) ? jd_price : selectJdPrice));
//                            mallPriceTv.setText("¥" + selectConversionPrice + " + " + selectCoupon + "加油余额");
                            mallPriceTv.setText("¥" + conversionPrice + "+" + coupon + "积分兑换");
                            break;
                        default:
                            mallPriceTv.setText("¥" + selectDuduPrice);
                            jdPriceTv.setText("价格:  " + "¥" + selectJdPrice);
                            break;
                    }
                }

                /**=================秒杀商品选中属性===================**/
                if (!StringUtils.isEmpty(mSeckill)) {
                    switch (mSeckill) {
                        case SECKILL:
                        case DUDU_SECKILL:
                            mallPriceTv.setText("¥" + selectSeckillPrice);
                            jdPriceTv.setText("价格:  " + "¥" + selectDuduPrice);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        /**=================flag 区别直接购买还是加入购物车购买===================**/
        switch (flag) {
            case BUY:
                btn_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (propertyBeanList.size() != map.size()) {
                            String str = "商品属性";
                            for (int i = 0; i < propertyBeanList.size(); i++) {
                                if (map.get(propertyBeanList.get(i).getPropertyNameId()) == null) {
                                    str = propertyBeanList.get(i).getPropertyName();
                                    break;
                                }
                            }
                            ToastUtils.show(getActivity(), "请选择 - " + str);
                            return;
                        }
                        if (selectStock.equals("0")) {
                            ToastUtils.show(getActivity(), "该商品规格暂无库存");
                            return;
                        }
                        Log.e(TAG, "立即购买商品数量msg===>" + shop_number);
                        if ("logistics".equals(deliveryType)) {//物流配送
                            if (shop_number < logisticsMinNum) {
                                ToastUtils.show(getActivity(), "数量未达到最低购买数量" + logisticsMinNum);
                                return;
                            }
                        }

                        if ("express".equals(deliveryType)) {  //快递配送
                            if (shop_number < mCourierMinNum) {
//                                ToastUtils.show(getActivity(), "商品数量未达到快递配送最低购买数量" + mCourierMinNum + "，请重新选择配送方式");
                                ToastUtils.show(getActivity(), "数量未达到最低购买数量" + mCourierMinNum);
                                return;
                            }
                        }

                        if (shop_number < 1) {
                            ToastUtils.show(getActivity(), "请输入正确的购买数量");
                            return;
                        }

                        if (map.size() != propertyBeanList.size()) {
                            ToastUtils.show(getActivity(), "请选择商品属性");
                            return;
                        } else {
                            if (!StringUtils.isEmpty(mSeckill)) {
                                switch (mSeckill) {
                                    /**=================秒杀商品(和普通商品的区别在于  价格 不一样)===================**/
                                    case SECKILL:
                                        if (minNumber != 0 && shop_number < minNumber) {
                                            ToastUtils.show(getActivity(), "该秒杀商品最低限购" + minNumber + "件");
                                            return;
                                        }

                                        if (maxNumber != 0 && shop_number > maxNumber) {
                                            ToastUtils.show(getActivity(), "该秒杀商品限购" + maxNumber + "件");
                                            return;
                                        }

                                        String[] seckill_keys = new String[]{"shop_image_preview", "shop_name", "shop_number", "mall_price_true", "jd_price_true", "productDesc",
                                                "property", "productNo", "orderEnsureActivityFlag", "conversionPrice", "columnId", "coupon", "isExchange", "isSeckill",
                                                "seckillProductId", "deliveryType", "deliveryMethodMsg", "Image"};
                                        String[] seckill_values = new String[]{shop_image_preview, shop_name, shop_number + "", seckillPrice, mall_price_true, desc,
                                                propertyJson, productNo, orderEnsureActivityFlag + "", conversionPrice, columnId, coupon, isExchange, "y", seckillProductId,
                                                deliveryType, deliveryMethodMsg, mThisImage};
                                        SkipPageUtils.getInstance(getActivity()).skipPage(OrderEnsureActivity.class, seckill_keys, seckill_values);
                                        break;
                                    case DUDU_SECKILL:

                                        if ("end".equals(status)) {
                                            ArmsUtils.makeText(getActivity(), "活动已结束");
                                            popupWindow.dismiss();
                                            return;
                                        } else if ("wait".equals(status)) {
                                            ArmsUtils.makeText(getActivity(), "活动未开始");
                                            popupWindow.dismiss();
                                            return;
                                        }

                                        if (minNumber != 0 && shop_number < minNumber) {
                                            ToastUtils.show(getActivity(), "该秒杀商品最低限购" + minNumber + "件");
                                            return;
                                        }

                                        if (maxNumber != 0 && shop_number > maxNumber) {
                                            ToastUtils.show(getActivity(), "该秒杀商品限购" + maxNumber + "件");
                                            return;
                                        }

                                        String[] dudu_seckill_keys = new String[]{"shop_image_preview", "shop_name", "shop_number", "mall_price_true", "jd_price_true", "productDesc",
                                                "property", "productNo", "orderEnsureActivityFlag", "conversionPrice", "columnId", "coupon", "isExchange", "isSeckill",
                                                "seckillProductId", "deliveryType", "deliveryMethodMsg", "Image", "holidaySeckillProductId"};
                                        String[] dudu_seckill_values = new String[]{shop_image_preview, shop_name, shop_number + "", seckillPrice, mall_price_true, desc,
                                                propertyJson, productNo, orderEnsureActivityFlag + "", conversionPrice, columnId, coupon, isExchange, DUDU_SECKILL, seckillProductId,
                                                deliveryType, deliveryMethodMsg, mThisImage, mHolidaySeckillProductId};
                                        SkipPageUtils.getInstance(getActivity()).skipPage(OrderEnsureActivity.class, dudu_seckill_keys, dudu_seckill_values);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                /**=================普通商品===================**/
                                if (shop_number > Integer.parseInt(selectStock)) {
                                    ToastUtils.show(getActivity(), "商品库存不足");
                                    return;
                                }
                                String tempType = "";
                                if (type == 3) {
                                    tempType = "jingDong";
                                } else if (type == 1) {
                                    tempType = "dudu";
                                } else {
                                    tempType = "n";
                                }
                                Log.e(TAG, "商品数量normal msg===>" + shop_number);
                                String[] keys = new String[]{"shop_image_preview", "shop_name", "shop_number", "mall_price_true", "jd_price_true", "productDesc", "property",
                                        "productNo", "orderEnsureActivityFlag", "conversionPrice", "columnId", "coupon", "isExchange", "isSeckill", "deliveryType",
                                        "deliveryMethodMsg", "Image"};
                                String[] values = new String[]{shop_image_preview, shop_name, shop_number + "", selectDuduPrice, selectJdPrice, desc, propertyJson, productNo,
                                        orderEnsureActivityFlag + "", conversionPrice, columnId, coupon, isExchange, tempType, deliveryType, deliveryMethodMsg, mThisImage};
                                SkipPageUtils.getInstance(getActivity()).skipPage(OrderEnsureActivity.class, keys, values);
                            }
                        }
                        popupWindow.dismiss();
                    }
                });
                break;
            case ADDSHOPPINGCART:
                btn_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "加入购物车商品数量msg===>" + shop_number);

                        if ("logistics".equals(deliveryType)) {//物流配送
                            if (shop_number < logisticsMinNum) {
                                ToastUtils.show(getActivity(), "商品数量未达到物流配送最低购买数量" + logisticsMinNum + "，请重新选择配送方式");
                                return;
                            }
                        }

                        if ("express".equals(deliveryType)) {  //快递配送
                            if (shop_number < mCourierMinNum) {
                                ToastUtils.show(getActivity(), "商品数量未达到快递配送最低购买数量" + mCourierMinNum + "，请重新选择配送方式");
                                return;
                            }
                        }

                        if (selectStock.equals("0")) {
                            ToastUtils.show(getActivity(), "该商品规格暂无库存");
                            return;
                        }
                        if (shop_number < 1) {
                            ToastUtils.show(getActivity(), "请输入正确的购买数量");
                            return;
                        }
                        if (map.size() != propertyBeanList.size()) {
                            ToastUtils.show(getActivity(), "请选择商品属性");
                            return;
                        } else {
                            List<ShoppingItemsBean> list = new ArrayList<>();
                            list = queryDbData(desc, productNo);
                            saveShopInfo(list);
                        }
                        popupWindow.dismiss();
                    }
                });
                break;
            default:
                break;
        }

        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.setOutsideTouchable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(this);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 添加之前遍历本地数据库
     */
    private List queryDbData(String desc, String productNo) {
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        Query query = shoppingItemsBeanDao.queryBuilder().where(ShoppingItemsBeanDao.Properties.Desc.eq(desc), ShoppingItemsBeanDao.Properties.ProductNo.eq(productNo)).build();
        return query.list();
    }

    /**
     * 保存购买信息到greendao数据库
     *
     * @param list
     */
    private void saveShopInfo(List<ShoppingItemsBean> list) {
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        ShoppingItemsBean shoppingItemsBean = new ShoppingItemsBean();
        String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        shoppingItemsBean.setUserId(StringUtils.isEmpty(uid) ? "" : uid);
        shoppingItemsBean.setImage(shop_image_preview);
        shoppingItemsBean.setName(shop_name);
        shoppingItemsBean.setMallPrice(mall_price_true);
        shoppingItemsBean.setJdPrice(jd_price);
        shoppingItemsBean.setDesc(desc);
        shoppingItemsBean.setProperty(propertyJson);
        shoppingItemsBean.setProductNo(productNo);
        shoppingItemsBean.setColumnId(columnId);
        shoppingItemsBean.setConversionPrice(selectConversionPrice);
        shoppingItemsBean.setCoupon(selectCoupon);
        shoppingItemsBean.setIsExchange(isExchange);
        shoppingItemsBean.setDeliveryType(deliveryType);
        shoppingItemsBean.setDeliveryMsg(deliveryMethodMsg);
        shoppingItemsBean.setAddNum(addNum);
        shoppingItemsBean.setLogisticsMinNum(logisticsMinNum);
        shoppingItemsBean.setImgaeUrl(mThisImage);

        if (list.size() > 0) {   //有相同属性数据就更新
            ShoppingItemsBean shoppingItemsBean1 = list.get(0);
            shoppingItemsBean.setNum(shop_number);
            shoppingItemsBean.set_id(shoppingItemsBean1.get_id());
            shoppingItemsBeanDao.update(shoppingItemsBean);
        } else {                 //没有相同属性数据则插入
            shoppingItemsBean.setNum(shop_number);
            shoppingItemsBeanDao.insert(shoppingItemsBean);
        }

        sendCartNumBroadCast(getCartTotalNum());
        ToastUtils.show(getActivity(), "商品已在购物车等待您的付款~");
    }

    /**
     * 发送广播
     *
     * @param cartTotalNum
     */
    private void sendCartNumBroadCast(int cartTotalNum) {
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", cartTotalNum);
        VsApplication.getContext().sendBroadcast(intent);
    }

    private int getCartTotalNum() {
        int num = 0;
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (Iterator iterators = beanList.iterator(); iterators.hasNext(); ) {
            ShoppingItemsBean bean = (ShoppingItemsBean) iterators.next();
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }

    /**
     * 使用 Map按value进行排序
     *
     * @param map_data
     * @return
     */
    public Map<String, String> sortMapByKey(LinkedHashMap<String, String> map_data) {
        if (map_data == null || map_data.isEmpty()) {
            return null;
        }
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(map_data.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, String>> iter = entryList.iterator();
        Map.Entry<String, String> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        //0.0-1.0
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onStatucChanged(SlideDetailsLayout.Status status) {
        if (status == SlideDetailsLayout.Status.OPEN) {
            //当前为图文详情页
            fab_up_slide.show();
            activity.vp_content.setNoScroll(true);
            scrollCursor();
            activity.tv_title.setVisibility(View.VISIBLE);
            activity.psts_tabs.setVisibility(View.GONE);
        } else {
            //当前为商品详情页
            fab_up_slide.hide();
            activity.vp_content.setNoScroll(false);
            activity.tv_title.setVisibility(View.GONE);
            activity.psts_tabs.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滑动游标
     */
    private void scrollCursor() {
        TranslateAnimation anim = new TranslateAnimation(fromX, nowIndex * v_tab_cursor.getWidth(), 0, 0);
        //设置动画结束时停在动画结束的位置
        anim.setFillAfter(true);
        anim.setDuration(50);
        //保存动画结束时游标的位置,作为下次滑动的起点
        fromX = nowIndex * v_tab_cursor.getWidth();
        v_tab_cursor.startAnimation(anim);

        //设置Tab切换颜色
        for (int i = 0; i < tabTextList.size(); i++) {
            tabTextList.get(i).setTextColor(i == nowIndex ? getResources().getColor(R.color.public_color_EC6941) : getResources().getColor(R.color.vs_black));
        }
    }

    /**
     * 切换Fragment
     *
     * @param fromFragment
     * @param toFragment
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
       GSYVideoManager.onPause();
        if (nowFragment != toFragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            // 先判断是否被add过
            if (!toFragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到activity中
                fragmentTransaction.hide(fromFragment).add(R.id.fl_content, toFragment).commitAllowingStateLoss();
            } else {
                // 隐藏当前的fragment，显示下一个
                fragmentTransaction.hide(fromFragment).show(toFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1.0f);
        //秒杀
        if ((SECKILL.equals(mSeckill) || DUDU_SECKILL.equals(mSeckill)) && minNumber != 0) {
            shop_number = minNumber;
        } else {                         //非秒杀
            if ("logistics".equals(deliveryType)) {
                shop_number = addNum;
            } else if ("express".equals(deliveryType)) {
                shop_number = mCourierMinAddNum;
            } else {
                shop_number = 1;
            }
        }
    }

    @Override
    public void OnFragmentClick(String flag) {
//        String mImageUrl = JudgeImageUrlUtils.isAvailable(shop_image_preview);
//        SelectBuyDialog.getInstance()
//                .setShopInfo(mImageUrl,mall_price_true,jd_price_true)
//                .setPropertyBeanList(propertyBeanList,inventoryBeanList)
//                .show(getFragmentManager(),"");
        showGoodsParamsDialog(getActivity().getWindow().getDecorView().findViewById(R.id.ll_content), flag);
    }

    @Override
    public void onAddressStart() {

    }

    @Override
    public void onAddressFail() {

    }

    @Override
    public void onAddressSuccess(BDLocation bdLocation) {

        mParams.put("province", bdLocation.getProvince());
        mParams.put("city", bdLocation.getCity());
        mParams.put("county", bdLocation.getDistrict());
        mParams.put("town", bdLocation.getStreet());
        mParams.put("productNo", productNo);

        mTvAddress.setText(bdLocation.getAddrStr());
        getJDInventory();
    }

    @Override
    public void onAddressFinish() {

    }

    /**
     * 比较器
     */
    class MapValueComparator implements Comparator<Map.Entry<String, String>> {
        @Override
        public int compare(Map.Entry<String, String> me1, Map.Entry<String, String> me2) {
            return me1.getValue().compareTo(me2.getValue());
        }
    }
}
