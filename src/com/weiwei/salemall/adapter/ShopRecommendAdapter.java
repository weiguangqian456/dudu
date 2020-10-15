package com.weiwei.salemall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.activity.SimBackActivity;
import com.weiwei.home.activity.SimBackEnum;
import com.weiwei.home.utils.CustomSkipUtils;
import com.weiwei.home.utils.TextDisposeUtils;
import com.weiwei.mall.activity.GoodsJdActivity;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.base.BaseViewHolder;
import com.weiwei.salemall.bean.BannerDataEntity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.DataBean;
import com.weiwei.salemall.bean.HomePageDataBean;
import com.weiwei.salemall.bean.ModelHomeEntranceBean;
import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.bean.SeckillDataEntity;
import com.weiwei.salemall.bean.SeckillProductEntity;
import com.weiwei.salemall.inter.OnLoadingMore;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.DateUtil;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.TimeChangeUtils;
import com.weiwei.salemall.widget.WrapContentHeightViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/5/23.
 *         首页适配器
 */
public class ShopRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private HomePageDataBean data;
    private RecyclerView recyclerView;

    /**
     * 加载更多
     */
    private final int TYPE_LOAD_MORE = 100;
    /**
     * 商企轮播
     */
    private final int BANNER = 99;
    /**
     * 商企
     * 二次注释 - 未使用的不知道什么东西
     */
    private final int TYPE_WELFARE = 101;
    /**
     * 精选
     */
    private final int TYPE_CHOICES = 102;
    /**
     * 推荐
     * 二次注释 - 秒杀专区（每个item使用一格）
     */
    private final int TYPE_RECOMMEND = 103;
    /**
     * 精选广告
     * 二次注释 - 秒杀以上的界面
     */
    private final int TYPE_WELFAREBANNER = 104;
    /**
     * 推荐广告
     */
    private final int TYPE_RECOMMENDBANNER = 105;

    /**
     * 新会员专区
     */
    private final int TYPE_RECRUIT = 106;

    /**
     * 首页菜单单页显示数量
     */
    public static final int HOME_ENTRANCE_PAGE_SIZE = 10;

    private static final int MSG_FRESHUI_ENTRANCE = 106;

    private int welfare = 0;
    private int recommend = 0;
    private int choices = 0;
    private boolean isLoading = false;
    private int visibleThreshold = 3;
    private boolean canLoadMore = true;
    private OnLoadingMore loadingMore;
    /**
     * item 为奇数时候处理
     */
    private List<Object> dataList = new ArrayList<Object>();

    private BannerDataEntity mallBannerEntity;

    private List<ModelHomeEntranceBean> mainBeanList;
    private List<ModelHomeEntranceBean> jdBeanList;
    /**
     * 首页栏目+jd栏目已经请求
     */
    private boolean isLoad = false;

    /**
     * 首次开始倒计时
     */
    private boolean isFirstLoadCountDownView = false;


    private static final String TAG = "ShopRecommendAdapter";

//    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};

    public void setBanerEntity(BannerDataEntity mallBannerEntity) {
        this.mallBannerEntity = mallBannerEntity;
        notifyDataSetChanged();
    }

    private ConvenientBanner convenientBanner;

    /**
     * 倒计时相关
     */
    private TextView nextTv;
    private LinearLayout countDownLl;   //新的倒计时控件
    private TextView hourTv;            //时
    private TextView minuteTv;          //分
    private TextView secondsTv;         //秒
    private String status;
    private String startTime;
    private String endTime;
    private String now;

    private List<SeckillProductEntity> seckillProductEntityList;

    private long time;  //倒计时时长
    private long time_s;
    private boolean isCountDown1 = false;

    private long time_v2;  //倒计时时长
    private long time_s_v2;
    private boolean isCountDown2 = false;

    //添加推荐
    public void addRecommends(List<ProductBean> list) {
        if (!list.isEmpty()) {
            recommend = list.size();
            dataList.addAll(list);
        }
    }

    //添加福利
    public void addWelfare(List<ProductBean> list) {
        if (!list.isEmpty()) {
            welfare = list.size();
            dataList.addAll(list);
        }
    }

    //添加精选
    public void addChoices(List<ProductBean> list) {
        if (!list.isEmpty()) {
            choices += list.size();
            dataList.addAll(list);
        }
    }

    //往DataBean中加数据
    public void addData(HomePageDataBean bean) {
        dataList.clear();
        addRecommends(bean.getRecommends());
//        addWelfare(bean.getWelfares());
        addChoices(bean.getChoices());
    }

    public ShopRecommendAdapter(final Context context, RecyclerView recyclerView, HomePageDataBean data) {
        this.data = data;
        this.context = context;
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                Log.i("lastPosition --> ", lastPosition + "");
                Log.i("itemCount  --> ", itemCount + " ");

                if (canLoadMore && !isLoading && (lastPosition >= (itemCount - visibleThreshold))) {
                    if (loadingMore != null) {
                        isLoading = true;
                        Log.i("到底了 --> ", itemCount + " ");
                        loadingMore.onLoadMore();
                    }
                }
            }
        });

//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.top = convertDpToPixel(0);
//                outRect.bottom = 0;
//                int position = parent.getChildLayoutPosition(view);
//                outRect.top = convertDpToPixel(0);
//                if (position == 0) outRect.top = convertDpToPixel(0);
//                if (position > 0 && position <= recommend) {
//                    //每行3个
//                    int i = (position - 1) % 3;
//                    if (position > 3) {
//                        outRect.top = convertDpToPixel(9);
//                    }
////                    switch (i) {
////                        //第一个
////                        case 0:
////                            outRect.left = convertDpToPixel(14);
////                            outRect.right = convertDpToPixel(2);
////                            break;
////                        //第二个
////                        case 1:
////                            outRect.left = convertDpToPixel(2);
////                            outRect.right = convertDpToPixel(2);
////                            break;
////                        //第二个
////                        case 2:
////                            outRect.left = convertDpToPixel(2);
////                            outRect.right = convertDpToPixel(14);
////                            break;
////                        default:
////                            break;
////                    }
//                }
//
//                if (position > recommend + 2) {
//                    outRect.top = convertDpToPixel(0);
//                    //每行2个
//                    int i = (position - (recommend + 1)) % 2;
//                    switch (i) {
//                        //第一个
//                        case 0:
//                            outRect.left = convertDpToPixel(0);
//                            outRect.right = convertDpToPixel(0);
//                            break;
//                        //第二个
//                        case 1:
//                            outRect.left = convertDpToPixel(0);
//                            outRect.right = convertDpToPixel(0);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        });
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == TYPE_LOAD_MORE) {
            /**
             * 加载更多
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
            RelativeLayout checkMoreRl = (RelativeLayout) view.findViewById(R.id.rl_checkmore);
            progressBar.setInterpolator(new AccelerateInterpolator(2));
            progressBar.setIndeterminate(true);
            checkMoreRl.setVisibility(View.GONE);
            return new BaseViewHolder<>(view);
        } else if (viewType == TYPE_WELFAREBANNER) {
            /**
             * 福利版块头布局 + 导览图
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dudu_welfare_headview, parent, false);
            viewHolder = new WelfareHeadViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_WELFARE) {
            /**
             * 福利版块
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_member_welfare_item, parent, false);
            viewHolder = new WelfareViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_CHOICES) {
            /**
             * 精选板块
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dudu_special_item, parent, false);
            viewHolder = new ChoicesViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_RECOMMENDBANNER) {
            /**
             * 推荐版块头布局
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dudu_recommend_headview, parent, false);
            viewHolder = new RecommendHeadViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_RECOMMEND) {
            /**
             * 推荐版块
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_member_welfare_item, parent, false);
            viewHolder = new RecommendViewHolder(view);
            return viewHolder;
        } else if(viewType == TYPE_RECRUIT){
            //新会员专区
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false);
            return new ItemViewHolder(view);
        } else {
            return viewHolder;
        }
    }
    private boolean isInitBanner;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //强制关闭复用
        if (convenientBanner != null && !isInitBanner) {
            isInitBanner = true;
            convenientBanner.startTurning(2000);
        }

        if (getItemViewType(position) == TYPE_LOAD_MORE) {
            View itemView = holder.itemView;
            if (canLoadMore && isLoading) {   //加载更多
                if (itemView.getVisibility() != View.VISIBLE) {
                    itemView.setVisibility(View.VISIBLE);
                }
                itemView.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
                ((TextView)itemView.findViewById(R.id.tv_msg)).setText("正在加载~");
            } else if (itemView.getVisibility() == View.VISIBLE) {
                itemView.findViewById(R.id.pb_loading).setVisibility(View.GONE);
                ((TextView)itemView.findViewById(R.id.tv_msg)).setText("已经到底了~");
//                itemView.setVisibility(View.GONE);
            }
        } else if (getItemViewType(position) == TYPE_WELFAREBANNER) {    //限时抢购头布局
            ((WelfareHeadViewHolder) holder).notifyBanner();
            ((WelfareHeadViewHolder) holder).notifyOptimalBanner();
            if (!isLoad) {
                ((WelfareHeadViewHolder) holder).getSeckillListAndStartCountDown();
                ((WelfareHeadViewHolder) holder).getColumdData();
            }
            Glide.with(context).load(R.drawable.image_member).into(((WelfareHeadViewHolder)holder).iv_member_advertising);
            if (mallBannerEntity != null && mallBannerEntity.getWelfare() != null) {
                final BannerImageEntity welfareImageEntity = mallBannerEntity.getWelfare();
                String url = welfareImageEntity.getImageUrl();
                String imageUrl = JudgeImageUrlUtils.isAvailable(url);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.mall_member_welfare_default);
                Glide.with(context).load(imageUrl).apply(options).into(((WelfareHeadViewHolder) holder).iv_welfare);
                ((WelfareHeadViewHolder) holder).iv_welfare.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CustomSkipUtils.toSkip(context,welfareImageEntity);
                    }
                });
//                int skipType = welfareImageEntity.getSkipType();
//                final String params = welfareImageEntity.getAndroidParams();
//                final String className = welfareImageEntity.getAndroidClassName().trim();
//                JSONObject jsonObject = JSONObject.parseObject(params);
//                if (jsonObject != null) {
//                    final String productNo = (String) jsonObject.get("productNo");
//                    final String skipUrl = (String) jsonObject.get("skipUrl");
//                    final String storeNo = (String) jsonObject.get("storeNo");
//                    if (params != null && className != null && !className.equals("")) {
//                        switch (skipType) {
//                            case 0:
//                                ((WelfareHeadViewHolder) holder).iv_welfare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        if (className.contains("VsRechargeActivity") && !CheckLoginStatusUtils.isLogin()) {
//                                            intent.setClassName(context, "com.weiwei.base.activity.login.VsLoginActivity");
//                                            context.startActivity(intent);
//                                        } else {
//                                            intent.setClassName(context, className);
//                                            if (productNo != null && !productNo.equals("")) {
//                                                intent.putExtra("productNo", productNo);
//                                            } else {
//                                                intent.putExtra("storeNo", storeNo);
//                                            }
//                                            context.startActivity(intent);
//                                        }
//                                    }
//                                });
//                                break;
//                            case 1:   //外链
//                                ((WelfareHeadViewHolder) holder).iv_welfare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        intent.setClassName(context, className);
//                                        intent.putExtra("skipUrl", skipUrl);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                }
            }
        } else if (getItemViewType(position) == TYPE_WELFARE) {
//            final MallHomePageEntity.DataBean.ChoicesBean choicesBean = ((MallHomePageEntity.DataBean.RecommendsBean) dataList.get(position - 3));
//            String url = welfaresBean.getPicture();
//            String imageUrl = JudgeImageUrlUtils.isAvailable(url);
//            Glide.with(context).load(imageUrl).into(((WelfareViewHolder) holder).draweeView);
//            ((WelfareViewHolder) holder).productName.setText(welfaresBean.getProductName());
//            ((WelfareViewHolder) holder).jdPriceTv.setText("¥" + welfaresBean.getJdPrice());
//            ((WelfareViewHolder) holder).jdPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            ((WelfareViewHolder) holder).mallPriceTv.setText("¥" + welfaresBean.getPrice());
//
//            //将position保存在itemView的Tag中，以便点击时进行获取
//            holder.itemView.setTag(position);
//            ((WelfareViewHolder) holder).containerLl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, "productNo", welfaresBean.getProductNo());
//                }
//            });
//            judgeAndSetBgColor(position, (WelfareViewHolder) holder);,
        } else if (getItemViewType(position) == TYPE_CHOICES) {    //精选专区
            final ProductBean choicesBean = (ProductBean) dataList.get(position - 2);
            final String imageUrl = JudgeImageUrlUtils.isAvailable(choicesBean.getPicture());
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.mall_logits_default);
            Glide.with(context).load(imageUrl).apply(options).into(((ChoicesViewHolder) holder).imageView);
            ((ChoicesViewHolder) holder).productEconomicalPrice.setText("立省¥" + DealEcnomicalMoneyUtils.get(choicesBean.getJdPrice(), choicesBean.getPrice(), 1));
            int contrastSource = choicesBean.getContrastSource();
            ((ChoicesViewHolder) holder).productJdPrice.setText(choicesBean.getJdPrice2());
//            ((ChoicesViewHolder) holder).productJdPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ((ChoicesViewHolder) holder).productMallPrice.setText("¥" + choicesBean.getPrice());
            ((ChoicesViewHolder) holder).productName.setText(choicesBean.getProductName());
            ((ChoicesViewHolder) holder).contentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] key = new String[]{"productNo", "productImage", "productName","productPrice"};
                    String[] value = new String[]{choicesBean.getProductNo(), imageUrl, choicesBean.getProductName(),choicesBean.getPrice()};
                    SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                }
            });
        } else if (getItemViewType(position) == TYPE_RECOMMENDBANNER) {    //精选专区头布局
            if (mallBannerEntity != null && mallBannerEntity.getChoice() != null) {
                final BannerImageEntity choiceImageEntity = mallBannerEntity.getChoice();
                String url = choiceImageEntity.getImageUrl();
                String imageUrl = JudgeImageUrlUtils.isAvailable(url);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.mall_member_welfare_default);
                Glide.with(context).load(imageUrl).apply(options).into(((RecommendHeadViewHolder) holder).choiceAd);
                ((RecommendHeadViewHolder) holder).checkMoreLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        SkipPageUtils.getInstance(context).skipPage(CheckMoreActivity.class, "type", "2");
//                        SkipPageUtils.getInstance(context).skipPage(SecKillActivity.class);    //本行代码仅供测试
                        SimBackActivity.launch(context,SimBackEnum.RECRUIT_REGION,null);
                    }
                });

                ((RecommendHeadViewHolder) holder).choiceAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomSkipUtils.toSkip(context,choiceImageEntity);
                    }
                });
//                int skipType = choiceImageEntity.getSkipType();
//                final String params = choiceImageEntity.getAndroidParams();
//                final String className = choiceImageEntity.getAndroidClassName().trim();
//                JSONObject jsonObject = JSONObject.parseObject(params);
//                if (jsonObject != null) {
//                    final String productNo = (String) jsonObject.get("productNo");
//                    final String skipUrl = (String) jsonObject.get("skipUrl");
//                    final String storeNo = (String) jsonObject.get("storeNo");
//                    if (params != null && !StringUtils.isEmpty(className)) {
//                        switch (skipType) {
//                            case 0:
//                                ((RecommendHeadViewHolder) holder).choiceAd.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        if (className.contains("VsRechargeActivity") && !CheckLoginStatusUtils.isLogin()) {
//                                            intent.setClassName(context, "com.weiwei.base.activity.login.VsLoginActivity");
//                                            context.startActivity(intent);
//                                        } else {
//                                            intent.setClassName(context, className);
//                                            if (!StringUtils.isEmpty(productNo)) {
//                                                intent.putExtra("productNo", productNo);
//                                            } else {
//                                                intent.putExtra("storeNo", storeNo);
//                                            }
//                                            context.startActivity(intent);
//                                        }
//                                    }
//                                });
//                                break;
//                            case 1:
//                                ((RecommendHeadViewHolder) holder).choiceAd.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        intent.setClassName(context, className);
//                                        intent.putExtra("skipUrl", skipUrl);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                                break;
//                            case 2:
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                }
            }
        } else if (getItemViewType(position) == TYPE_RECOMMEND) {  //限时抢购专区
            if (position > 6) {
                judgeAndSetBgColor(position - 1, ((RecommendViewHolder) holder).draweeView);
            }

            /**====================拟解决设置秒杀size为0时显示占位的问题=======**/
            ((RecommendViewHolder) holder).containerLl.setVisibility(View.GONE);

            if (seckillProductEntityList != null) {
                if (position > seckillProductEntityList.size()) {
                    return;
                }

                ((RecommendViewHolder) holder).containerLl.setVisibility(View.VISIBLE);
                final SeckillProductEntity entity = seckillProductEntityList.get(position - 1);
                final String imageUrl = JudgeImageUrlUtils.isAvailable(entity.getPicture());
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.mall_logits_default);
                Glide.with(context).load(imageUrl).apply(options).into(((RecommendViewHolder) holder).draweeView);
                String stock = entity.getStock();
                ((RecommendViewHolder) holder).soldOutIv.setVisibility(stock.equals("0") ? View.VISIBLE : View.GONE);
                ((RecommendViewHolder) holder).remainNumber.setText(stock.equals("0") ? "已售罄" : "仅剩" + stock + "件");
                ((RecommendViewHolder) holder).productName.setText(entity.getProductName().trim());
                ((RecommendViewHolder) holder).jdPriceTv.setText("¥" + entity.getPrice());
//                ((RecommendViewHolder) holder).jdPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ((RecommendViewHolder) holder).mallPriceTv.setText("¥" + entity.getSeckillPrice());
                ((RecommendViewHolder) holder).seckillIv.setImageDrawable(context.getResources().getDrawable(R.drawable.seckill));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((RecommendViewHolder) holder).seckillRl.setBackground(context.getResources().getDrawable(R.drawable.bg_seckill));
                }
                ((RecommendViewHolder) holder).containerLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] key = new String[]{"productNo", "productImage", "productName", "productPrice","seckillProductId", "seckill"};
                        String[] value = new String[]{entity.getProductNo(), imageUrl, entity.getProductName(),entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill"};
                        SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                    }
                });
            }
        }else if(getItemViewType(position) == TYPE_RECRUIT && holder instanceof ItemViewHolder){
            ((ItemViewHolder) holder).ll_layout.setVisibility(View.GONE);
            if (seckillProductEntityList != null) {
                //视图数量绑定的推荐-临时解决-比推荐少隐藏  比推荐多-待解决
                if (position > seckillProductEntityList.size()) {
                    ((ItemViewHolder) holder).ll_layout.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).view_new_line.setVisibility(View.VISIBLE);
                    return;
                }else{
                    ((ItemViewHolder) holder).ll_layout.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).view_new_line.setVisibility(View.GONE);
                }
                //新会员专区
                final SeckillProductEntity entity = seckillProductEntityList.get(position - 1);
                String stock = entity.getStock();
                final String imageUrl = JudgeImageUrlUtils.isAvailable(entity.getPicture());
                Glide.with(context).load(imageUrl).into(((ItemViewHolder) holder).iv_image);
                ((ItemViewHolder) holder).tv_title.setText(entity.getProductName().trim());
                ((ItemViewHolder) holder).tv_price.setText("￥" + entity.getSeckillPrice().trim());
                String originally_price = "¥" + entity.getPrice();
                ((ItemViewHolder) holder).tv_originally_price.setText(originally_price);
                ((ItemViewHolder) holder).tv_originally_price.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
//                ((ItemViewHolder) holder).tv_originally_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                String spare = "立省" + (TextDisposeUtils.toStringInt(entity.getPrice()) -
                        TextDisposeUtils.toStringFloat(entity.getSeckillPrice())) + "元";
                ((ItemViewHolder) holder).tv_spare.setText(spare);
                ((ItemViewHolder) holder).itemView.setOnClickListener(new ValidClickListener() {
                    @Override
                    public void onValidClick() {
                        String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "seckillProductId", "seckill","type"};
                        String[] value = new String[]{entity.getProductNo(), imageUrl, entity.getProductName(), entity.getSeckillPrice(), entity.getSeckillProductId(), "seckill",context.getString(R.string.member_region)};
                        SkipPageUtils.getInstance(context).skipPage(GoodsDetailActivity.class, key, value);
                    }
                });
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 判断位置，设置背景颜色
     *
     * @param current
     * @param draweeView
     */
    private void judgeAndSetBgColor(int current, ImageView draweeView) {
        int curr = current % 3; //0 1 2
        switch (curr) {
            case 0:  //左
                draweeView.setBackgroundColor(Color.parseColor("#fbf5f2"));
                break;
            case 1:
                draweeView.setBackgroundColor(Color.parseColor("#f6f6f6"));
                break;
            case 2:  //右
                draweeView.setBackgroundColor(Color.parseColor("#f5f4f6"));
                break;
            default:
                break;
        }
    }

    /**
     * 刷新秒杀数据
     */
    public void freshSeckillData() {
        getSeckillList();
    }

    private void getSeckillList() {
        ApiService api = RetrofitClient.getInstance(context).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "6");
        params.put("pageNum", "1");
        retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "下拉刷新秒杀商品List msg===>" + result.getData().toString());
                    SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                    List<SeckillProductEntity> seckillProductList = JSONObject.parseArray(seckillDataEntity.getPrdoucts().toString(), SeckillProductEntity.class);
                    seckillProductEntityList = new ArrayList<>();

                    seckillProductEntityList.addAll(seckillProductList);
                    notifyDataSetChanged();
                    status = seckillDataEntity.getStatus();

                    /***倒计时***/
                    startTime = DateUtil.stampToDate(seckillDataEntity.getStartTime());
                    endTime = DateUtil.stampToDate(seckillDataEntity.getEndTime());
                    now = DateUtil.stampToDate(seckillDataEntity.getNow());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date toDate = df.parse(endTime);
                        Date fromDate = df.parse(now);
                        Date toDate_v2 = df.parse(startTime);
                        if (toDate.getTime() > fromDate.getTime()) {
                            time = toDate.getTime() - fromDate.getTime();
                            time_s = time / 1000;   //ms  ->  s
                        }

                        if (toDate_v2.getTime() > fromDate.getTime()) {
                            time_v2 = toDate_v2.getTime() - fromDate.getTime();
                            time_s_v2 = time_v2 / 1000;   //ms  ->  s
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    switch (status) {
                        case "end":
                            nextTv.setText("秒杀结束");
                            break;
                        case "underway":
                            nextTv.setText("秒杀进行中");
                            countTime(status);
                            break;
                        case "wait":
                            nextTv.setText("秒杀未开始");
                            countTime(status);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    Handler handler1 = new Handler();
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            time_s--;
            String formatLongToTimeStr = TimeChangeUtils.getInstance(context).formatLongToTimeStr(time_s);
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    if (Integer.parseInt(split[0]) >= 10) {
                        hourTv.setText(split[0]);
                    } else {
                        hourTv.setText("0" + split[0]);
                    }
                }
                if (i == 1) {
                    if (Integer.parseInt(split[1]) >= 10) {
                        minuteTv.setText(split[1]);
                    } else {
                        minuteTv.setText("0" + split[1]);
                    }
                }
                if (i == 2) {
                    if (Integer.parseInt(split[2]) >= 10) {
                        secondsTv.setText(split[2]);
                    } else {
                        secondsTv.setText("0" + split[2]);
                    }
                }
            }
            if (time_s > 0) {
                handler1.postDelayed(this, 1000);
            } else {  //时间到
                handler1.removeCallbacks(runnable1);
                isCountDown1 = false;
                /**=======倒计时结束重新获取倒计时状态=======**/
                getSeckillStatusAgain();
            }
        }
    };

    Handler handler2 = new Handler();
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            time_s_v2--;
            String formatLongToTimeStr = TimeChangeUtils.getInstance(context).formatLongToTimeStr(time_s_v2);
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    if (Integer.parseInt(split[0]) >= 10) {
                        hourTv.setText(split[0]);
                    } else {
                        hourTv.setText("0" + split[0]);
                    }
                }
                if (i == 1) {
                    if (Integer.parseInt(split[1]) >= 10) {
                        minuteTv.setText(split[1]);
                    } else {
                        minuteTv.setText("0" + split[1]);
                    }
                }
                if (i == 2) {
                    if (Integer.parseInt(split[2]) >= 10) {
                        secondsTv.setText(split[2]);
                    } else {
                        secondsTv.setText("0" + split[2]);
                    }
                }
            }

            if (time_s_v2 > 0) {
                handler2.postDelayed(this, 1000);
            } else {  //时间到
                handler2.removeCallbacks(runnable2);
                /**=======倒计时结束重新获取倒计时状态=======**/
                isCountDown2 = false;
                getSeckillStatusAgain();
            }
        }
    };

    private void getSeckillStatusAgain() {
        ApiService api = RetrofitClient.getInstance(context).Api();
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", "1");
        params.put("pageNum", "1");
        retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                    status = seckillDataEntity.getStatus();
                    startTime = DateUtil.stampToDate(seckillDataEntity.getStartTime());
                    endTime = DateUtil.stampToDate(seckillDataEntity.getEndTime());
                    now = DateUtil.stampToDate(seckillDataEntity.getNow());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date toDate = df.parse(endTime);
                        Date fromDate = df.parse(now);
                        Date toDate_v2 = df.parse(startTime);
                        if (toDate.getTime() > fromDate.getTime()) {
                            time = toDate.getTime() - fromDate.getTime();
                            time_s = time / 1000;
                        }

                        if (toDate_v2.getTime() > fromDate.getTime()) {
                            time_v2 = toDate_v2.getTime() - fromDate.getTime();
                            time_s_v2 = time_v2 / 1000;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "getSeckillStatusAgain ===>" + status);

                    switch (status) {
                        case "end":
                            nextTv.setText("秒杀结束");
                            break;
                        case "underway":
                            nextTv.setText("秒杀进行中");
                            countTime(status);
                            break;
                        case "wait":
                            nextTv.setText("秒杀未开始");
                            countTime(status);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    class WelfareViewHolder extends RecyclerView.ViewHolder {
        private ImageView draweeView;
        private TextView jdPriceTv;
        private TextView mallPriceTv;
        private TextView productName;
        private LinearLayout containerLl;

        public WelfareViewHolder(View itemView) {
            super(itemView);
            containerLl = (LinearLayout) itemView.findViewById(R.id.ll_container);
            draweeView = (ImageView) itemView.findViewById(R.id.iv_mem_welfave);
            jdPriceTv = (TextView) itemView.findViewById(R.id.tv_jd_price);
            mallPriceTv = (TextView) itemView.findViewById(R.id.tv_mall_price);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
        }
    }

    class WelfareHeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_welfare;
        private WrapContentHeightViewPager entranceViewPager;
        private MagicIndicator entranceIndicatorView;
        private WrapContentHeightViewPager jdEntranceViewPager;
        private MagicIndicator jdEntranceIndicatorView;
        private LinearLayout jdLl;
        private TextView tv_more;
        private ImageView iv_member_advertising;

        private ImageView iv_navigation_member;
        private ImageView iv_navigation_jd;
        private ImageView iv_navigation_choiceness;

        public WelfareHeadViewHolder(View itemView) {
            super(itemView);
            iv_navigation_member = itemView.findViewById(R.id.iv_navigation_member);
            iv_navigation_jd = itemView.findViewById(R.id.iv_navigation_jd);
            iv_navigation_choiceness = itemView.findViewById(R.id.iv_navigation_choiceness);

            iv_member_advertising= itemView.findViewById(R.id.iv_member_advertising);
            tv_more = (TextView)itemView.findViewById(R.id.tv_more);
            iv_welfare = (ImageView) itemView.findViewById(R.id.iv_welfare);
            convenientBanner = (ConvenientBanner) itemView.findViewById(R.id.cbanner_img);
            entranceViewPager = (WrapContentHeightViewPager) itemView.findViewById(R.id.main_home_entrance_vp);
            entranceIndicatorView = (MagicIndicator) itemView.findViewById(R.id.main_home_entrance_indicator);
            jdEntranceViewPager = (WrapContentHeightViewPager) itemView.findViewById(R.id.main_home_jd_entrance_vp);
            jdEntranceIndicatorView = (MagicIndicator) itemView.findViewById(R.id.main_home_jd_entrance_indicator);
            jdLl = (LinearLayout) itemView.findViewById(R.id.ll_jd_discount_store);
            countDownLl = (LinearLayout) itemView.findViewById(R.id.ll_countdownView);
            nextTv = (TextView) itemView.findViewById(R.id.tv_next);
            hourTv = (TextView) itemView.findViewById(R.id.tv_miaosha_shi);
            minuteTv = (TextView) itemView.findViewById(R.id.tv_miaosha_minter);
            secondsTv = (TextView) itemView.findViewById(R.id.tv_miaosha_second);


            tv_more.setOnClickListener(this);
            iv_member_advertising.setOnClickListener(this);
            iv_navigation_jd.setOnClickListener(this);
            iv_navigation_choiceness.setOnClickListener(this);
            iv_navigation_member.setOnClickListener(this);

            notifyOptimalBanner();
        }

        public void notifyOptimalBanner(){
            if(mallBannerEntity != null && mallBannerEntity.getOptimal() != null){
                BannerDataEntity.OptimalBean optimal = mallBannerEntity.getOptimal();
            if(optimal.getJd() != null){
                String url = JudgeImageUrlUtils.isAvailable(optimal.getJd().getImageUrl());
                Glide.with(context).load(url).into(iv_navigation_jd);
            }
            if(optimal.getOpt() != null){
                String url = JudgeImageUrlUtils.isAvailable(optimal.getOpt().getImageUrl());
                Glide.with(context).load(url).into(iv_navigation_choiceness);
            }
            if(optimal.getSec() != null){
                String url = JudgeImageUrlUtils.isAvailable(optimal.getSec().getImageUrl());
                Glide.with(context).load(url).into(iv_navigation_member);
            }
        }
        }

        /**
         * Banner界面设置
         */
        public void notifyBanner() {
            if (mallBannerEntity != null && mallBannerEntity.getTop() != null) {
                List<BannerImageEntity> list = new ArrayList<>();
                list.addAll(mallBannerEntity.getTop());
                convenientBanner.setPageIndicator(new int[]{R.drawable.index_white_point, R.drawable.index_blue_point});
                convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                convenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public Object createHolder() {
                        return new HomePageBannerViewHolder();
                    }
                }, list);
            }
        }

        public void getColumdData() {
            ApiService api = RetrofitClient.getInstance(context).Api();
            retrofit2.Call<ResultEntity> call = api.getColumn("android");
            call.enqueue(new Callback<ResultEntity>() {
                @Override
                public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                    if (response.body() == null) {
                        return;
                    }
                    ResultEntity result = response.body();
                    if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                        Log.e(TAG, "首页栏目分类msg===>" + result.getData().toString());
                        DataBean dataBean = JSONObject.parseObject(result.getData().toString(), DataBean.class);
                        mainBeanList = new ArrayList<>();
                        jdBeanList = new ArrayList<>();
                        List<ModelHomeEntranceBean> dataList = JSONObject.parseArray(dataBean.getOther().toString(), ModelHomeEntranceBean.class);
                        mainBeanList.addAll(dataList);
                        List<ModelHomeEntranceBean> jdDataList = JSONObject.parseArray(dataBean.getJd().toString(), ModelHomeEntranceBean.class);
                        jdBeanList.addAll(jdDataList);
                        isLoad = true;
                        //是否显示Main的Icon集合
                        if (mainBeanList != null && mainBeanList.size() > 0) bindMainColumnViewPager("main", mainBeanList, entranceViewPager, entranceIndicatorView, jdLl);
                        //是否显示JD专区
                        if (jdBeanList != null && jdBeanList.size() > 0) bindMainColumnViewPager("jd", jdBeanList, jdEntranceViewPager, jdEntranceIndicatorView, jdLl);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                }
            });
        }

        public void getSeckillListAndStartCountDown() {
            ApiService api = RetrofitClient.getInstance(context).Api();
            Map<String, String> params = new HashMap<>();
            params.put("pageSize", "6");
            params.put("pageNum", "1");
            retrofit2.Call<ResultEntity> call = api.getSecKillList(params);
            call.enqueue(new Callback<ResultEntity>() {
                @Override
                public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                    if (response.body() == null) {
                        return;
                    }
                    ResultEntity result = response.body();
                    if (REQUEST_CODE == result.getCode()) {
                        Log.e(TAG, "首次获取秒杀商品List msg===>" + result.getData().toString());
                        SeckillDataEntity seckillDataEntity = JSONObject.parseObject(result.getData().toString(), SeckillDataEntity.class);
                        List<SeckillProductEntity> seckillProductList = JSONObject.parseArray(seckillDataEntity.getPrdoucts().toString(), SeckillProductEntity.class);
                        seckillProductEntityList = new ArrayList<>();
                        seckillProductEntityList.addAll(seckillProductList);
                        notifyDataSetChanged();
//                        int size = seckillProductEntityList.size();
//                        if (size < 6 && size > 0) {
//                            SeckillProductEntity seckillProductEntity = new SeckillProductEntity();
//                            for (int i = 0; i < 6 - size; i++) {
//                                seckillProductEntityList.add(seckillProductEntity);
//                            }
//                        }
                        /***倒计时***/
                        status = seckillDataEntity.getStatus();
                        startTime = DateUtil.stampToDate(seckillDataEntity.getStartTime());
                        endTime = DateUtil.stampToDate(seckillDataEntity.getEndTime());

                        switch (status) {
                            case "end":
                                nextTv.setText("秒杀结束");
                                break;
                            case "underway":   /***开启计时***/
                                nextTv.setText("秒杀进行中");
                                countTime(status);
                                break;
                            case "wait":
                                nextTv.setText("秒杀未开始");
                                countTime(status);
                                break;
                            default:
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_more:
                    SimBackActivity.launch(context,SimBackEnum.RECRUIT_REGION,null);
                    break;
                case R.id.iv_navigation_jd:
                    if(mallBannerEntity.getOptimal() != null && mallBannerEntity.getOptimal().getJd()!=null){
                        CustomSkipUtils.toSkip(context,mallBannerEntity.getOptimal().getJd());
                    }else{
                        context.startActivity(new Intent(context, GoodsJdActivity.class));
                    }
                    break;
                case R.id.iv_navigation_choiceness:
                    if(mallBannerEntity.getOptimal() != null && mallBannerEntity.getOptimal().getJd()!=null){
                        CustomSkipUtils.toSkip(context,mallBannerEntity.getOptimal().getOpt());
                    }else{
                        CustomSkipUtils.toSimActivity(context,"SimBackActivity.3",null);
                    }
                    break;
                case R.id.iv_navigation_member:
                    if(mallBannerEntity.getOptimal() != null && mallBannerEntity.getOptimal().getJd()!=null){
                        CustomSkipUtils.toSkip(context,mallBannerEntity.getOptimal().getSec());
                    }else{
                        SimBackActivity.launch(context,SimBackEnum.RECRUIT_REGION,null);
                    }
                    break;
                case R.id.iv_member_advertising:
                    SimBackActivity.launch(context,SimBackEnum.RECRUIT_REGION,null);
                    break;
            }
        }
    }

    private void countTime(String status) {
        switch (status) {
            case "underway":
                if (!isCountDown1) {
                    handler1.postDelayed(runnable1, 1000);
                    isCountDown1 = true;
                }
                break;
            case "wait":
                if (!isCountDown2) {
                    handler2.postDelayed(runnable2, 1000);
                    isCountDown2 = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    /**
     * 绑数据
     *
     * @param beanList
     * @param entranceViewPager
     * @param entranceIndicatorView
     */
    private void bindMainColumnViewPager(String flag, List<ModelHomeEntranceBean> beanList, final WrapContentHeightViewPager entranceViewPager, MagicIndicator
            entranceIndicatorView, LinearLayout jdLl) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int pageSize = HOME_ENTRANCE_PAGE_SIZE;
        if (beanList != null) {
            switch (flag) {
                case "jd":   //京东模块
                    jdLl.setVisibility(beanList.size() < 1 ? View.GONE : View.VISIBLE);
                    break;
                default:
                    break;
            }
            int pageCount = (int) Math.ceil(beanList.size() * 1.0 / pageSize);
            entranceIndicatorView.setVisibility(pageCount > 1 ? View.VISIBLE : View.INVISIBLE);
            final List<View> viewList = new ArrayList<>();
            for (int index = 0; index < pageCount; index++) {
                //每个页面都是inflate出一个新实例
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.item_hone_entrance_vp, entranceViewPager, false);
                GridLayoutManager manager = new GridLayoutManager(context, 5);
                recyclerView.setLayoutManager(manager);
                EntranceAdapter entranceAdapter = new EntranceAdapter(context, beanList, index, HOME_ENTRANCE_PAGE_SIZE);
                recyclerView.setAdapter(entranceAdapter);
                viewList.add(recyclerView);
            }
            CagegoryViewPagerAdapter adapter = new CagegoryViewPagerAdapter(viewList);
            entranceViewPager.setAdapter(adapter);
            //2019.3.1 class 的跳转
            //商企采购 -- com.weiwei.salemall.activity.SamqiPurchaseActivity
            //同城联盟 -- com.weiwei.salemall.activity.LocalStoreActivity
            //兑换专区 -- com.weiwei.salemall.activity.ExchangeGoodsActivity
            //厂家直销 -- com.weiwei.salemall.activity.SamqiPurchaseActivity
            //VIP专区 -- com.weiwei.account.VipMemberActivity
            CircleNavigator circleNavigator = new CircleNavigator(context);
            circleNavigator.setCircleCount(viewList.size());
            circleNavigator.setCircleColor(context.getResources().getColor(R.color.vs_blue));
            circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
                @Override
                public void onClick(int index) {
                    entranceViewPager.setCurrentItem(index);
                }
            });
            entranceIndicatorView.setNavigator(circleNavigator);
            ViewPagerHelper.bind(entranceIndicatorView, entranceViewPager);
        }
    }

    class RecommendHeadViewHolder extends RecyclerView.ViewHolder {
        private ImageView choiceAd;
        private RelativeLayout checkMoreLl;

        public RecommendHeadViewHolder(View itemView) {
            super(itemView);
            choiceAd = (ImageView) itemView.findViewById(R.id.iv_festival);
            checkMoreLl = (RelativeLayout) itemView.findViewById(R.id.ll_welfare_check_more);
        }
    }

    class RecommendViewHolder extends RecyclerView.ViewHolder {
        public ImageView draweeView;
        private ImageView soldOutIv;
        private TextView jdPriceTv;
        private TextView mallPriceTv;
        private TextView productName;
        private TextView remainNumber;
        private ImageView seckillIv;
        private LinearLayout containerLl;
        private RelativeLayout seckillRl;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            containerLl = (LinearLayout) itemView.findViewById(R.id.ll_container);
            draweeView = (ImageView) itemView.findViewById(R.id.iv_mem_welfave);
            seckillIv = (ImageView) itemView.findViewById(R.id.iv_seckill);
            soldOutIv = (ImageView) itemView.findViewById(R.id.iv_number_flag);
            jdPriceTv = (TextView) itemView.findViewById(R.id.tv_jd_price);
            mallPriceTv = (TextView) itemView.findViewById(R.id.tv_mall_price);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
            remainNumber = (TextView) itemView.findViewById(R.id.tv_shop_number);
            seckillRl = (RelativeLayout) itemView.findViewById(R.id.rl_seckill);
        }
    }

    class ChoicesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView productName;
        private TextView productMallPrice;
        private TextView productJdPrice;
        private TextView productEconomicalPrice;
        private RelativeLayout contentLl;

        public ChoicesViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_shop);
            productName = (TextView) itemView.findViewById(R.id.tv_introduction);
            productMallPrice = (TextView) itemView.findViewById(R.id.tv_mall_price);
            productJdPrice = (TextView) itemView.findViewById(R.id.tv_jd_price);
            productEconomicalPrice = (TextView) itemView.findViewById(R.id.tv_ecoprice);
            contentLl = (RelativeLayout) itemView.findViewById(R.id.ll_content);
        }
    }
    /**
     * 新会员专区
     */
    class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_originally_price)
        TextView tv_originally_price;
        @BindView(R.id.tv_spare)
        TextView tv_spare;
        @BindView(R.id.tv_buy)
        TextView tv_buy;
        @BindView(R.id.ll_layout)
        LinearLayout ll_layout;
        @BindView(R.id.view_new_line)
        View view_new_line;
        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }

        if (position == 0) {
            return TYPE_WELFAREBANNER;
        }

//        if (position > 0 && position <= welfare) {
//            return TYPE_WELFARE;
//        }

        if (position > 0 && position <= recommend) {
            //TYPE_RECOMMEND - TYPE_RECRUIT
            return TYPE_RECRUIT;
        }

        if (position == recommend + 1) {
            return TYPE_RECOMMENDBANNER;
        }
        return TYPE_CHOICES;
    }

    public void setLoadingMore(OnLoadingMore loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }
}
