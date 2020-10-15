package com.weiwei.base.activity.me;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.utils.ScreenAdaptiveUtils;

import java.util.Hashtable;


public class KcMyQcodeActivity extends VsBaseActivity {

    private TextView mVersionInfoTextView;// 版本号信息
    private TextView mCheckUpgrade;// 检测更新
    private TextView mKfMobileTextView;// 客服热线
    private LinearLayout mKfMobileLinearLayout;
    private TextView mMobileWapTextView;// 手机网站
    private TextView mKfTime;// 客服时间
    private TextView kfqq;// 客服qq
    private static final char MSG_ID_SendUpgradeMsg = 0;
    private final String TAG = "KcAboutActivity";
    private ImageView mCreateView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptiveUtils.initCustomDensity(this, getApplication());
        setContentView(R.layout.new_kc_mydownload);
        initStatusBar(Color.parseColor("#EC491F"));
//        initTitleNavBar();
//        showLeftNavaBtn(R.drawable.icon_back);
//        mTitleTextView.setText("我的二维码");
        // String mRecommendInfo = VsUserConfig.getDataString(mContext,
        // VsUserConfig.JKey_FRIEND_INVITE);
        ((TextView)findViewById(R.id.tv_title)).setText("我的二维码");
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
        String url = mRecommendInfo.substring(mRecommendInfo.indexOf("http"), mRecommendInfo.length());
        CustomLog.i("123", url);
        mCreateView = (ImageView) findViewById(R.id.iv_result);
        mCreateView.setImageBitmap(createQRImage(url, 800, 800));
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    private void initStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
            // 如果亮色，设置状态栏文字为黑色
            if (ColorUtils.calculateLuminance(color) >= 0.5) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
