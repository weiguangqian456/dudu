package com.zbar.lib;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.fragment.VsDialFragment;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014年5月9日 下午12:25:31
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 扫描界面
 */
public class CaptureActivity extends VsBaseActivity implements Callback, OnClickListener {

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private RelativeLayout surface_rel, bottom_mask;
    private boolean isNeedCapture = false;
    //	private String image_reg = "false";
    private String flagString = "-1";
    private EditText surface_edit;
    private ImageView top_mask, capture_scan_line, left_mask, right_mask;
    private TextView surface_txt;
    private RoundTextView cature_btn, cature_btn1, cature_btn2, invit_btn, commit_btn;
    private String newResult = "";

    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initPermission();
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Code_Account, "");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (("2").equals(bundle.getString("code"))) {
                flagString = "2";
            } else if (("1").equals(bundle.getString("code"))) {
                flagString = "1";
            }
        }
        //注册返回绑定成功广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("getQrCode");
        mContext.registerReceiver(receiver, filter);

        mTitleTextView.setText(R.string.vs_code_find);
        mTitleTextView.setTextColor(getResources().getColor(R.color.vs_white));
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        surface_rel = (RelativeLayout) findViewById(R.id.surface_rel);
        bottom_mask = (RelativeLayout) findViewById(R.id.bottom_mask);
        left_mask = (ImageView) findViewById(R.id.left_mask);
        right_mask = (ImageView) findViewById(R.id.right_mask);
        surface_edit = (EditText) findViewById(R.id.surface_edit);
        top_mask = (ImageView) findViewById(R.id.top_mask);
        invit_btn = (RoundTextView) findViewById(R.id.invit_btn);
        commit_btn = (RoundTextView) findViewById(R.id.commit_btn);
        cature_btn = (RoundTextView) findViewById(R.id.cature_btn);
        cature_btn1 = (RoundTextView) findViewById(R.id.cature_btn1);
        cature_btn2 = (RoundTextView) findViewById(R.id.cature_btn2);
        cature_btn1.setText("开启闪光灯");
        cature_btn2.setText("开启闪光灯");
        surface_txt = (TextView) findViewById(R.id.surface_txt);
//		capture_scan_line = (ImageView)findViewById(R.id.capture_scan_line);
        //如果是发现页面或注册页面跳转过来的，隐藏扫码功能
        if (flagString.equals("-1") || flagString.equals("1")) {
            cature_btn.setVisibility(View.GONE);
            cature_btn1.setVisibility(View.GONE);
            cature_btn2.setVisibility(View.VISIBLE);
        } else {
            cature_btn2.setVisibility(View.GONE);
        }
        cature_btn1.setOnClickListener(this);
        cature_btn2.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        final ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        final TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
                0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
        final Animation anim = AnimationUtils.loadAnimation(CaptureActivity.this, R.anim.footer_disappear);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                top_mask.setVisibility(View.VISIBLE);
                surface_rel.setVisibility(View.GONE);
                mCropLayout.setBackgroundResource(R.drawable.capture);
                mCropLayout.setVisibility(View.VISIBLE);
                bottom_mask.setBackgroundResource(R.drawable.scan_mask);
                bottom_mask.setVisibility(View.VISIBLE);
                right_mask.setBackgroundResource(R.drawable.scan_mask);
                right_mask.setVisibility(View.VISIBLE);
                left_mask.setBackgroundResource(R.drawable.scan_mask);
                left_mask.setVisibility(View.VISIBLE);
                surface_txt.setVisibility(View.VISIBLE);
                cature_btn.setVisibility(View.VISIBLE);
                cature_btn1.setVisibility(View.VISIBLE);
                mQrLineView.setVisibility(View.VISIBLE);
                mAnimation.setDuration(1500);
                mAnimation.setRepeatCount(-1);
                mAnimation.setRepeatMode(Animation.REVERSE);
                mAnimation.setInterpolator(new LinearInterpolator());
                mQrLineView.setAnimation(mAnimation);

            }
        });

        invit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                restartCamera();
                surface_rel.startAnimation(anim);
            }
        });

        cature_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                surface_rel.setVisibility(View.VISIBLE);
                surface_txt.setVisibility(View.INVISIBLE);
                cature_btn.setVisibility(View.INVISIBLE);
                cature_btn1.setVisibility(View.INVISIBLE);

                top_mask.setVisibility(View.INVISIBLE);
                bottom_mask.setVisibility(View.INVISIBLE);
                right_mask.setVisibility(View.INVISIBLE);
                left_mask.setVisibility(View.INVISIBLE);
                mCropLayout.setVisibility(View.INVISIBLE);

                mQrLineView.clearAnimation();
                mQrLineView.setVisibility(View.GONE);
                surface_rel.startAnimation(AnimationUtils.loadAnimation(CaptureActivity.this, R.anim.footer_appear));
                closeCamera();

            }
        });


    }

    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 开闪光灯
            CameraManager.get().openLight();
            cature_btn1.setText("关闭闪光灯");
            cature_btn2.setText("关闭闪光灯");
        } else {
            flag = true;
            // 关闪光灯
            CameraManager.get().offLight();
            cature_btn1.setText("开启闪光灯");
            cature_btn2.setText("开启闪光灯");
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        // Toast.makeText(getApplicationContext(), result,
        // Toast.LENGTH_SHORT).show();

        try {

            if (flagString.equals("1")) {
                int start = result.indexOf("&");
                int end = result.lastIndexOf("&");
                String resulget = result.substring(start + 5, end);

//				String resultd = result.substring(result.indexOf("a="), result.indexOf("&s"));
//				String resulget = resultd.replaceAll("a=", "");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_Code_Account, resulget);

                finish();
            } else if (flagString.equals("2")) {
                int start = result.indexOf("&");
                int end = result.lastIndexOf("&");
                newResult = result.substring(start + 5, end);
                TreeMap<String, String> treeMap = new TreeMap<String, String>();
                String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
                treeMap.put("uid", uid);
                treeMap.put("invited_by", newResult);
                CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GET_QRCODE,
                        DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetQrcode);

            } else {
                Uri uri = Uri.parse(result);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            // handler.sendEmptyMessage(R.id.restart_preview);

        } catch (Exception e) {
            // TODO: handle exception
            mToast.show("请扫描正确的二维码");
            // handler.sendEmptyMessage(R.id.restart_preview);

        } finally {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 3000);

        }

    }

    /**
     * 相机权限
     */
    private void initPermission() {
        XXPermissions.with(this).permission(Permission.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> list, boolean b) {

                    }

                    @Override
                    public void noPermission(List<String> list, boolean b) {
                        ToastUtils.show(getBaseContext(), "请开启相机权限用于扫描。");
                    }
                });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(true);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    void restartCamera() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
//		playBeep = true;
//		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//			playBeep = false;
//		}
//		initBeepSound();
//		vibrate = true; 

        // 恢复活动监控器  
        inactivityTimer.onResume();
    }

    void closeCamera() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();

        // 关闭摄像头  
        CameraManager.get().closeDriver();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("getQrCode");
            if (result.equals("0")) {
                mToast.show("绑定成功", Toast.LENGTH_SHORT);

                VsUserConfig.setData(mContext, VsUserConfig.Jey_Invited_By, "");
            } else {
                mToast.show(result, Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cature_btn1:// 一键删除电话号码
                light();
                break;
            case R.id.cature_btn2:// 一键删除电话号码
                light();
                break;
            case R.id.commit_btn:
                newResult = surface_edit.getText().toString().trim() + "";
                if (newResult.length() == 0) {
                    mToast.show("请输入邀请人", Toast.LENGTH_SHORT);
                } else {
                    String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
                    String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
                    if (newResult.equals(uid) || newResult.equals(phone)) {
                        mToast.show("不能绑定自己为邀请人", Toast.LENGTH_SHORT);
                    } else {
                        TreeMap<String, String> treeMap = new TreeMap<String, String>();
                        treeMap.put("uid", uid);
                        treeMap.put("invited_by", newResult);
                        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GET_QRCODE,
                                DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGetQrcode);
                    }

                }
                break;

        }
    }
}