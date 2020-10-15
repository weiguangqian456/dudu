package com.weiwei.base.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.activity.GoodsDetailActivity;

import java.security.MessageDigest;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

public class VideoFragment extends LazyBaseFragment{

    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private String videoUrl;

    public static VideoFragment getInstance(String url) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("video_url",url);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }
    private StandardGSYVideoPlayer videoPlayer;
    private String BASEURL = "http://qiniu.edawtech.com/";
    private GoodsDetailActivity activity;

    @Override
    protected void initView(View view) {
        activity = (GoodsDetailActivity) getActivity();
        videoUrl = getArguments().getString("video_url");
        videoPlayer = view.findViewById(R.id.video);

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


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void loadData() {
        videoPlayer.setUp(BASEURL + videoUrl,true,"");
        videoPlayer.getBackButton().setVisibility(View.GONE);
        videoPlayer.setIsTouchWiget(true);
        ImageView imageView = new ImageView(getContext());
        videoPlayer.setThumbImageView(imageView);
//        videoPlayer.setBackgroundResource(R.drawable.cicler_back);
        orientationUtils = new OrientationUtils(getActivity(),videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        loadVideoScreenshot(getContext(),BASEURL+videoUrl,imageView,10000);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setUrl(BASEURL + videoUrl)
                .setCacheWithPlay(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                            isFull = false;
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(videoPlayer);

        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                videoPlayer.startWindowFullscreen(activity, true, true);
                isFull = true;
            }
        });
    }

    @Override
    public void onPause() {
        if(videoPlayer != null) {
            videoPlayer.getCurrentPlayer().onVideoPause();
        }
        super.onPause();
        isPause = true;
    }

    private boolean isFull = false;
    @Override
    public void onResume() {
        videoPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    LogUtils.e("isFull",isFull + "");
                    if(isFull) {
                        orientationUtils.backToProtVideo();
                        isFull = false;
                        if (GSYVideoManager.backFromWindowFull(getContext())) {
                            return true;
                        }
                    }else {
                        videoPlayer.getCurrentPlayer().onVideoResume(false);
                        isPause = false;
                    }
                }
                return false;//当fragmenet没有消费点击事件，返回false，activity中继续执行对应的逻辑
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if(orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        videoPlayer.setVideoAllCallBack(null);
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(getActivity(), newConfig, orientationUtils, true, true);
        }
    }*/
}
