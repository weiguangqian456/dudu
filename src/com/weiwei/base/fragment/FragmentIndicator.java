package com.weiwei.base.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.activity.RefuelMainActivity;
import com.weiwei.salemall.activity.MyOrderActivity;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.utils.SkipPageUtils;


/**
 * @author Sundy 兴
 * @version 创建时间：2014-12-16上午11:44:02
 * @instruction 主界面导航栏
 */
public class FragmentIndicator extends LinearLayout implements OnClickListener {

    public static final int HOME_PAGE_MAIN = 0;    //商城界面(首页)
    public static final int HOME_PAGE_CLASSIFY = 1;    //分类界面
    public static final int HOME_PAGE_PHONE = 2;         //打电话界面
    public static final int HOME_PAGE_SHOPPING = 3;      //购物车界面
    public static final int HOME_PAGE_MINE = 4;          //我的界面
    public static final int HOME_PAGE_CONTACTS = 5;       //联系人界面
    public static final int HOME_PAGE_REFUEL = 6;       //嘟嘟加油界面

    public OnIndicateListener mOnIndicateListener;
    // 拨号图标动画
    private Animation animDown = null;
    private Animation animUp = null;

    /**
     * 容器
     */
    private Context mContext;

    /**
     * 拨号
     */
    private LinearLayout vs_keyborad_layout;
    // 拨号图标
    private ImageView vs_keyborad_imagview;
    // 拨号文字
    private TextView vs_keyborad_tv;
    static TextView vs_mms_unread;

    /**
     * 联系人
     */
    private LinearLayout vs_contact_layout;
    // 联系人图标
    private ImageView vs_contact_imagview;
    // 联系人文字
    private TextView vs_contact_tv;

    /**
     * 话费
     */
    private LinearLayout vs_money_layout;
    // 话费图标
    private ImageView vs_money_imagview;
    // 话费文字
    private TextView vs_money_tv;

    /**
     * 首页
     */
    private LinearLayout vs_homepage_layout;
    private ImageView vs_homepage_iamgeview;
    private TextView vs_homepage_tv;

//	/**
//	 * 信息
//	 */
//	private LinearLayout vs_mms_layout;
//	// 信息图标
//	private ImageView vs_mms_imagview;
//	// 信息文字
//	private TextView vs_mms_tv;
    /**
     * 发现
     */
    private LinearLayout vs_found_layout;
    private ImageView vs_found_imagview;
    private TextView vs_found_tv;

    /**
     * 我
     */
    private LinearLayout vs_me_layout;
    /**
     * 嘟嘟加油
     */
    private RelativeLayout vs_refuel_layout;

    // 我的图标
    private ImageView vs_me_imagview;
    // 我的图标文字
    private TextView vs_me_tv;

    private LinearLayout   ll_classify;
    private ImageView iv_classify;
    private TextView tv_classify;

    private static ImageView vs_mms_imagview_red;
    private RelativeLayout rl_tab;
    private RelativeLayout rl_tab1;

    /*
     * //服务 private LinearLayout vs_service_layout; // 服务图标 private ImageView
     * vs_service_imagview; // 服务文字 private TextView vs_service_tv;
     */
    public FragmentIndicator(Context context) {
        super(context);
        this.mContext = context;
        initView(context);
    }

    public FragmentIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
        onShoppingRefresh();
    }

    /**
     * 初始化视图
     */
    private void initView(Context context) {
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.framement_tab_layout, this, true);

        vs_homepage_layout = (LinearLayout) findViewById(R.id.vs_homepage_layout);
        vs_homepage_iamgeview = (ImageView) findViewById(R.id.vs_homepage_iv);
        vs_homepage_tv = (TextView) findViewById(R.id.vs_homepage_tv);

        vs_keyborad_layout = (LinearLayout) findViewById(R.id.vs_keyborad_layout);
        vs_keyborad_imagview = (ImageView) findViewById(R.id.vs_keyborad_imagview);
        vs_keyborad_tv = (TextView) findViewById(R.id.vs_keyborad_tv);

        rl_tab1 = (RelativeLayout) findViewById(R.id.rl_tab);


//        vs_contact_layout = (LinearLayout) findViewById(R.id.vs_contact_layout);
//        vs_contact_imagview = (ImageView) findViewById(R.id.vs_contact_imagview);
//        vs_contact_tv = (TextView) findViewById(R.id.vs_contact_tv);
        vs_mms_unread = (TextView) findViewById(R.id.vs_mms_unread);
        // vs_money_layout = (LinearLayout) findViewById(R.id.vs_money_layout);
        // vs_money_imagview = (ImageView) findViewById(R.id.vs_money_imagview);
        // vs_money_tv = (TextView) findViewById(R.id.vs_money_tv);
        vs_found_layout = (LinearLayout) findViewById(R.id.vs_found_layout);
        vs_found_imagview = (ImageView) findViewById(R.id.vs_found_imagview);
        vs_found_tv = (TextView) findViewById(R.id.vs_found_tv);

//		vs_mms_layout = (LinearLayout) findViewById(R.id.vs_mms_layout);
//		vs_mms_imagview = (ImageView) findViewById(R.id.vs_mms_imagview);
//		vs_mms_tv = (TextView) findViewById(R.id.vs_mms_tv);

        vs_me_layout = (LinearLayout) findViewById(R.id.vs_me_layout);
        vs_me_imagview = (ImageView) findViewById(R.id.vs_me_imagview);
        vs_me_tv = (TextView) findViewById(R.id.vs_me_tv);
       // vs_mms_imagview_red = (ImageView) findViewById(R.id.vs_contact_layout_red);

        ll_classify=(LinearLayout)findViewById(R.id.ll_classify);
        iv_classify = (ImageView)findViewById(R.id.iv_classify);
        tv_classify = (TextView)findViewById(R.id.tv_classify);

        vs_refuel_layout = findViewById(R.id.vs_refuel_layout);

		/*
         * vs_service_layout = (LinearLayout)
		 * findViewById(R.id.vs_service_layout); vs_service_imagview =
		 * (ImageView) findViewById(R.id.vs_service_imagview); vs_service_tv =
		 * (TextView) findViewById(R.id.vs_service_tv);
		 */

        // 设置监听事件
        vs_me_layout.setOnClickListener(this);
        vs_homepage_layout.setOnClickListener(this);
        vs_found_layout.setOnClickListener(this);
        ll_classify.setOnClickListener(this);
        vs_keyborad_layout.setOnClickListener(this);

        vs_keyborad_imagview.setOnClickListener(this);
        vs_refuel_layout.setOnClickListener(this);
//        vs_contact_layout.setOnClickListener(this);
        // vs_money_layout.setOnClickListener(this);
//		vs_mms_layout.setOnClickListener(this);


        // vs_service_layout.setOnClickListener(this);
    }

    /**
     * 3-11
     * 显示购物车小红点
     */
    public void onShoppingRefresh(){
        ShoppingItemsBeanDao shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext)
                .getDaoSession().getShoppingItemsBeanDao();
        findViewById(R.id.vs_found_layout_red).setVisibility(shoppingItemsBeanDao != null && shoppingItemsBeanDao.loadAll().size() != 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置键盘开关图片
     */
    public void setTabImage(boolean isopen) {
//        if (isopen) {
//            vs_keyborad_imagview.setBackgroundResource(R.drawable.ic_dial_normal);
//        } else {
//            vs_keyborad_imagview.setBackgroundResource(R.drawable.ic_dial_normal_down);
//        }
    }

    /**
     * 设置键盘开关按钮你状态
     */
    public void setTabImageFouse2(boolean isopen) {
//        // 直接默认
//        vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou);
//        animDown = AnimationUtils.loadAnimation(mContext, R.anim.call_tab_logo_anim_down);
//        LinearInterpolator lin = new LinearInterpolator();
//        animDown.setInterpolator(lin);
//        animDown.setFillAfter(true);
//        animUp = AnimationUtils.loadAnimation(mContext, R.anim.call_tab_logo_anim_up);
//        LinearInterpolator linUp = new LinearInterpolator();
//        animUp.setInterpolator(linUp);
//        animUp.setFillAfter(true);
//
//        if (isopen) {
//            vs_keyborad_imagview.startAnimation(animDown);
//            vs_keyborad_tv.setText("最近通话");
//        } else {
//            vs_keyborad_imagview.startAnimation(animUp);
//            vs_keyborad_tv.setText("拨号");
//        }

    }

    /**
     * 设置键盘开关按钮状态
     */
    public void setTabImageFouse(boolean isopen) {
//        if (isopen) {
//            vs_keyborad_imagview.setBackgroundResource(R.drawable.ic_dial_focused_down);
//        } else {
//            vs_keyborad_imagview.setBackgroundResource(R.drawable.ic_dial_focused);
//        }
    }

    /**
     * 设置键盘下显示文字
     *
     * @param isopen
     */
    public void setTabText(boolean isopen) {
//        if (isopen) {
//            vs_keyborad_tv.setText("最近通话");
//        } else {
//            vs_keyborad_tv.setText("拨号");
//        }
    }

    /**
     * 设置按钮状态
     *
     * @param which
     */
    public void setIndicator(int which) {
        Log.d("which++++++++", "setIndicator: "+which);
//        iv_classify.setBackgroundResource( which == 1 ? R.drawable.myorderchoose : R.drawable.myorder2x);
//        tv_classify.setTextColor(which == 1 ? mContext.getResources().getColor(R.color.color_theme_4) : mContext.getResources().getColor(R.color.text_gray));
        switch (which) {
            case 0:  //首页
                rl_tab1.setVisibility(VISIBLE);
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_sel);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                iv_classify.setBackgroundResource(R.drawable.myorder2x);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                break;
            case 3: //购物车
                rl_tab1.setVisibility(VISIBLE);
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_carty);
                iv_classify.setBackgroundResource(R.drawable.myorder2x);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                break;
            case 2: //嘟嘟加油
                rl_tab1.setVisibility(GONE);    //设置跳入拨号界面时不显示首页的底部导航栏   显示拨号界面的导航栏
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                iv_classify.setBackgroundResource(R.drawable.myorder2x);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                break;
            case 1:  //我的订单
                rl_tab1.setVisibility(VISIBLE);
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                iv_classify.setBackgroundResource(R.drawable.myorderchoose);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                break;

//            case 3: //拨号
//                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
//                vs_keyborad_imagview.setBackgroundResource(R.drawable.ic_dial_focused_down);
//                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
//                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);
//
//                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
//                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
//                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
//                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
//                break;

            case 4:  //我的
                rl_tab1.setVisibility(VISIBLE);
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                iv_classify.setBackgroundResource(R.drawable.myorder2x);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_y);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                break;
            case 6:
                rl_tab1.setVisibility(VISIBLE);
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                iv_classify.setBackgroundResource(R.drawable.myorder2x);
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);

                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                break;
            default:
                break;
        }
    }

    public interface OnIndicateListener {
        void onIndicate(View v, int which);
    }

    public void setOnIndicateListener(OnIndicateListener listener) {
        mOnIndicateListener = listener;
    }

    @Override
    public void onClick(View v) {
        int order_flag=0;
        switch (v.getId()) {
            case R.id.vs_homepage_layout:
                mOnIndicateListener.onIndicate(v, 0);
                setIndicator(0);
                break;
            case R.id.ll_classify:  //我的订单
                mOnIndicateListener.onIndicate(v, 1);
                setIndicator(1);
             /*   iv_classify.setBackgroundResource(R.drawable.myorderchoose);
                tv_classify.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
                vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
                vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
                vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
                vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);
                vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    Log.d("order_flag", "onClick: "+order_flag);
                    SkipPageUtils.getInstance(mContext).skipPage(MyOrderActivity.class, "flag", 0);
                }*/
             break;
         case R.id.vs_keyborad_imagview:
            case R.id.vs_refuel_layout:
               mOnIndicateListener.onIndicate(v, 6);
               setIndicator(6);
              /* iv_classify.setBackgroundResource(R.drawable.myorder2x);
               tv_classify.setTextColor(mContext.getResources().getColor(R.color.text_gray));
               vs_homepage_iamgeview.setBackgroundResource(R.drawable.icon_homepage_normal);
               vs_homepage_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
               vs_found_imagview.setBackgroundResource(R.drawable.icon_home_cart_n);
               vs_found_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
               vs_keyborad_imagview.setBackgroundResource(R.drawable.dudujiayou2x);
               vs_keyborad_tv.setTextColor(mContext.getResources().getColor(R.color.color_theme_4));
               vs_me_imagview.setBackgroundResource(R.drawable.icon_home_mine_n);
               vs_me_tv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    SkipPageUtils.getInstance(mContext).skipPage(RefuelMainActivity.class, "flag", order_flag);
                }*/
                break;
            case R.id.vs_found_layout:
                mOnIndicateListener.onIndicate(v, 3);
                setIndicator(3);
                break;
            case R.id.vs_me_layout:
                mOnIndicateListener.onIndicate(v, 4);
                setIndicator(4);
                break;
            default:
                break;
        }
    }

    /**
     * 红点提示
     *
     * @param count
     */
    public static void showHideRed(int count) {
        if (vs_mms_unread != null) {
            if (count > 0) {
                vs_mms_unread.setVisibility(View.VISIBLE);
                vs_mms_unread.setText(count + "");
                vs_mms_imagview_red.setVisibility(View.GONE);
            } else {
                vs_mms_imagview_red.setVisibility(View.GONE);
                vs_mms_unread.setVisibility(View.INVISIBLE);
            }
        }
    }
}
