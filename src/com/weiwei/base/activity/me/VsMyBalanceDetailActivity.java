package com.weiwei.base.activity.me;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.account.RedBagEntity;
import com.weiwei.account.RefuelBalanceActivity;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsCallMoneyAdapter;
import com.weiwei.base.adapter.VsRechargeAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.ChargePackageItem;
import com.weiwei.base.util.CircleImageView;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.utils.ImageFileUtils;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.PreferencesUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

public class VsMyBalanceDetailActivity extends VsBaseActivity {

    private TextView callmoney, calllog, taocan;
    private ImageView cursor;
    private String uid = "";
    private int offset, imageWidth;
    private boolean flag = true;
    private TextView baonian;
    private ViewPager viewPager;
    private ArrayList<View> viewList;
    private int currIndex = 0;
    private int position_one;
    private TextView myBalancDetail;
    private Button myBalancChargeBtn;
    private String flag_type = "";
    private CircleImageView myself_head;

    private ListView mChargePackageListview = null;
    private VsRechargeAdapter adapter = null;
    private ArrayList<ChargePackageItem> allData = null;
    private final char MSG_UPDATEGOODSLIST = 100;
    private String ad_conf_str = "";
    private ImageView ad_img_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_mybalance_dtail);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        flag_type = super.getIntent().getStringExtra("flag");
        initTitleNavBar();
//        showRightTxtBtn("话单查询");
        if (flag_type.equals("2")) {
            mTitleTextView.setText(getResources().getString(R.string.vs_mypackage_detail));
        } else {
            mTitleTextView.setText(getResources().getString(R.string.vs_myaccount_detail));
        }

        showLeftNavaBtn(R.drawable.icon_back);
        setDisEnableLeftSliding();
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
        init();
        showAd();
        InitViewPager(savedInstanceState);
        VsBizUtil.getInstance().goodsConfig(this);

        VsApplication.getInstance().addActivity(this);
    }


    @Override
    protected void HandleRightNavBtn() {

        startActivity(new Intent(this, VsCheckPhoneActivity.class));
        super.HandleRightNavBtn();

    }

    private void init() {

        cursor = (ImageView) findViewById(R.id.cursor);
        callmoney = (TextView) findViewById(R.id.callmoney);
        calllog = (TextView) findViewById(R.id.calllog);
        taocan = (TextView) findViewById(R.id.taocan);
        baonian = (TextView) findViewById(R.id.baonian);
        ad_img_back = (ImageView) findViewById(R.id.ad_img_back);
        initRegInfoData();
        IntentFilter netChangeFilter = new IntentFilter();
        netChangeFilter.addAction(GlobalVariables.actionGoodsConfigsec);
        mContext.registerReceiver(actionGoodsChangesec, netChangeFilter);
        mChargePackageListview = (ListView) findViewById(R.id.charge_package_listview);
        adapter = new VsRechargeAdapter(mContext);
        adapter.setData(allData);
        mChargePackageListview.setAdapter(adapter);
        mChargePackageListview.setDivider(null);

//        baonian.setText(VsUserConfig.getDataString(mContext, "baonian", "暂无"));
        baonian.setText(PreferencesUtils.getString(VsApplication.getContext(), VsUserConfig.JKey_MyInfo_LevelName));
        myBalancDetail = (TextView) findViewById(R.id.myBalancDetail);
//        String balance = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Balance_Left, "0.00");
//        myBalancDetail.setText(balance);
        myself_head = (CircleImageView) findViewById(R.id.myself_head);
//        String path = "";
//		if (uid != null && uid.length() > 0) {
//			path = VsUserConfig.getDataString(mContext,
//					VsUserConfig.JKey_MyInfo_photo, "");
//		}
//
//		if (fileIsExists(path) && path.contains(uid)) {
//			myself_head.setImageBitmap(BitmapFactory.decodeFile(path));
//		}
        Glide.with(mContext)
                .load(new File(ImageFileUtils.mSaveHeadPortraitPath))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.myself_head)
                        .skipMemoryCache(true)//不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE))//不使用本地缓存
                .into(myself_head);
        myBalancChargeBtn = (Button) findViewById(R.id.myBalancChargeBtn);
        myBalancChargeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, VsRechargeActivity.class));

            }
        });

        int screenWidth = this.getScreenWidth();
        imageWidth = BitmapFactory.decodeResource(getResources(), R.drawable.scour).getWidth();//screenWidth/3;
        offset = (screenWidth / 3 - imageWidth) / 2;
        Matrix matrix = new Matrix();
        if ("2".equals(flag_type)) {
            matrix.postTranslate(2 * screenWidth / 3 + offset, 0);
            currIndex = 2;
        } else {
            matrix.postTranslate(offset, 0);
        }

        cursor.setImageMatrix(matrix);
        callmoney.setOnClickListener(new MyOnClickListener(0));
        calllog.setOnClickListener(new MyOnClickListener(1));
        taocan.setOnClickListener(new MyOnClickListener(2));
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
//        params.put("uid", VsUserConfig.getDataString(mContext,VsUserConfig.JKey_KcId));
//        params.put("appId", "dudu");

        RetrofitClient.getInstance(this).Api()
                .getWallet2(params)
                .enqueue(new RetrofitCallback<ResultEntity>() {
                    @Override
                    protected void onNext(ResultEntity result) {
                        RedBagEntity redBagEntity = JSON.parseObject(result.getData().toString(), RedBagEntity.class);
                        myBalancDetail.setText(redBagEntity.getExchangePoint());
                    }
                });
    }

    private BroadcastReceiver actionGoodsChangesec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBaseHandler.sendEmptyMessage(MSG_UPDATEGOODSLIST);
        }
    };

    private void showAd() {
        ad_conf_str = VsUserConfig.getDataString(mContext,
                VsUserConfig.JKEY_AD_CONFIG_4002);
        if ("".equals(ad_conf_str)) {
            findViewById(R.id.slid_title).setVisibility(View.GONE);
            ad_img_back.setVisibility(View.GONE);
        } else {
            ad_img_back.setVisibility(View.GONE);
            findViewById(R.id.slid_title).setVisibility(View.VISIBLE);
//				findViewById(R.id.title).setVisibility(View.GONE);
            initSlide(this, ad_conf_str, "4002", false);
        }
    }

    protected void handleBaseMessage(Message msg) {
        CustomLog.i("lte", "regInfo===" + msg.what);
        switch (msg.what) {
            case MSG_UPDATEGOODSLIST:
                CustomLog.i("lte", "regInfo===");
                initRegInfoData();
                adapter = new VsRechargeAdapter(mContext);
                adapter.setData(allData);
                mChargePackageListview.setAdapter(adapter);
                mChargePackageListview.setDivider(null);
                break;
        }
    }

    /**
     * 初始化套餐信息
     */
    private void initRegInfoData() {
        allData = new ArrayList<ChargePackageItem>();
        String regInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_NewGoodsInfo);
        CustomLog.i("FavourableInfo ===", "regInfo===" + regInfo);
        try {
            JSONArray jsonArray = new JSONArray(regInfo);
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jobj = (JSONObject) jsonArray.get(i);
                if (jobj != null) {
                    String recommend_flag = jobj.getString("recommend_flag");
                    ChargePackageItem recData = new ChargePackageItem(jobj.getInt("sort_id"), jobj.getString("bid"),
                            jobj.getString("goods_id"), recommend_flag, jobj.getString("goods_name"), jobj.getString("des"),
                            jobj.getString("price"), jobj.getString("buy_limit"), jobj.getString("goods_type"),
                            jobj.getString("total_flag"), "", jobj.getString("goods_name"),
                            "[]");
                    allData.add(recData);
                }
            }
        } catch (JSONException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allData == null || allData.size() == 0) {

        } else {
            // 根据sort_id排序
            Collections.sort(allData, new Comparator<ChargePackageItem>() {
                @Override
                public int compare(ChargePackageItem date1, ChargePackageItem date2) {
                    return date2.getSort_id() - date1.getSort_id();
                }
            });
        }
    }


    private void InitViewPager(Bundle savedInstanceState) {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        LocalActivityManager manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        // 添加页面数据
        viewList = new ArrayList<View>();
        Intent intent = new Intent(this, VsBalanceMoney.class);
        viewList.add(manager.startActivity("A", intent).getDecorView());
        intent = new Intent(this, VsCallLogActivity.class);
        viewList.add(manager.startActivity("B", intent).getDecorView());
        intent = new Intent(this, VsMyMealActivity.class);
        viewList.add(manager.startActivity("C", intent).getDecorView());

        viewPager.setAdapter(new VsCallMoneyAdapter(viewList));
        if ("2".equals(flag_type)) {
            viewPager.setCurrentItem(2);

        } else {
            viewPager.setCurrentItem(0);
        }

        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    ;


    //获取手机屏幕的宽度，为滑条移动定位
    private int getScreenWidth() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        return screenWidth;
    }


    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            int one = offset * 2 + imageWidth;    // 页卡1移动到 页卡2的偏移量
            int two = one * 2;                    // 页卡1移动到 页卡3的偏移量.
            switch (arg0) {
                case 0://在第一位置，，要么移到1.要么到2
                    if (currIndex == 1) {
                        //设置图片平移（）
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-one, -two, 0, 0);
                        } else {
                            animation = new TranslateAnimation(one, 0, 0, 0);
                        }


                    } else if (currIndex == 2) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(0, -two, 0, 0);
                        } else {
                            animation = new TranslateAnimation(two, 0, 0, 0);
                        }

                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-two, -one, 0, 0);
                        } else {
                            animation = new TranslateAnimation(offset, one, 0, 0);
                        }


                    } else if (currIndex == 2) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(0, -one, 0, 0);
                        } else {
                            animation = new TranslateAnimation(two, one, 0, 0);
                        }


                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-two, 0, 0, 0);
                        } else {
                            animation = new TranslateAnimation(offset, two, 0, 0);
                        }

                    } else if (currIndex == 1) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-one, 0, 0, 0);
                        } else {
                            animation = new TranslateAnimation(one, two, 0, 0);
                        }


                    }
                    break;

            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGoodsChangesec != null) {
            mContext.unregisterReceiver(actionGoodsChangesec);
        }
    }


    // 判断文件是否存在
    public boolean fileIsExists(String strFile) {
        if (strFile.length() != 0) {

            try {
                File f = new File(strFile);
                if (!f.exists()) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }

            return true;

        } else {
            return false;
        }
    }

}
